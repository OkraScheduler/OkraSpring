/*
 * Copyright (c) 2017 Okra Scheduler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package okra;

import okra.base.AbstractOkra;
import okra.base.OkraItem;
import okra.base.OkraStatus;
import okra.exception.OkraRuntimeException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class OkraSpring<T extends OkraItem> extends AbstractOkra<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkraSpring.class);

    private final MongoTemplate mongoTemplate;
    private final long defaultHeartbeatExpirationMillis;
    private final Class<T> scheduleItemClass;

    public OkraSpring(final MongoTemplate mongoTemplate,
                      final String database,
                      final String collection,
                      final long defaultHeartbeatExpiration,
                      final TimeUnit defaultHeartbeatExpirationUnit,
                      final Class<T> scheduleItemClass) {
        super(database, collection);
        this.mongoTemplate = mongoTemplate;
        this.defaultHeartbeatExpirationMillis = defaultHeartbeatExpirationUnit.toMillis(defaultHeartbeatExpiration);
        this.scheduleItemClass = scheduleItemClass;
    }

    @Override
    public void initDbIfNeeded() {
    }

    @Override
    public Optional<T> poll() {
        final LocalDateTime expiredHeartbeatDate = LocalDateTime
                .now()
                .minus(defaultHeartbeatExpirationMillis, ChronoUnit.MILLIS);
        final Criteria mainOr = generatePollCriteria(expiredHeartbeatDate);

        final Update update = Update
                .update("status", OkraStatus.PROCESSING)
                .set("heartbeat", LocalDateTime.now());

        final Query query = Query.query(mainOr);
        final FindAndModifyOptions opts = new FindAndModifyOptions().returnNew(true);

        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, opts, scheduleItemClass));
    }

    private Criteria generatePollCriteria(final LocalDateTime expiredHeartbeatDate) {
        final Criteria pendingCriteria = new Criteria().andOperator(
                Criteria.where("runDate").lt(LocalDateTime.now()),
                Criteria.where("status").is(OkraStatus.PENDING)
        );

        final Criteria heartbeatCriteria = new Criteria().andOperator(
                Criteria.where("status").is(OkraStatus.PROCESSING),
                new Criteria().orOperator(
                        Criteria.where("heartbeat").lt(expiredHeartbeatDate),
                        Criteria.where("heartbeat").is(null)));

        return new Criteria().orOperator(pendingCriteria, heartbeatCriteria);
    }

    @Override
    public Optional<T> reschedule(T item) {
        throw new OkraRuntimeException();
    }

    @Override
    public Optional<T> heartbeat(T item) {
        return heartbeatAndUpdateCustomAttrs(item, null);
    }

    @Override
    public Optional<T> heartbeatAndUpdateCustomAttrs(final T item, final Map<String, Object> attrs) {
        if (item.getId() == null
                || item.getHeartbeat() == null
                || item.getStatus() == null) {
            return Optional.empty();
        }

        final Criteria criteria = Criteria
                .where("_id").is(new ObjectId(item.getId()))
                .and("status").is(OkraStatus.PROCESSING)
                .and("heartbeat").is(item.getHeartbeat());

        final Query query = Query.query(criteria);

        final Update update = Update.update("heartbeat", LocalDateTime.now());

        if (attrs != null && !attrs.isEmpty()) {
            attrs.forEach(update::set);
        }

        final FindAndModifyOptions opts = new FindAndModifyOptions().returnNew(true);

        LOGGER.info("Querying for schedules using query: {}", query);

        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, opts, scheduleItemClass));
    }

    @Override
    public void delete(final T item) {
        if (item.getId() == null) {
            return;
        }

        mongoTemplate.remove(item);
    }

    @Override
    public void schedule(final T item) {
        validateSchedule(item);
        item.setStatus(OkraStatus.PENDING);
        mongoTemplate.save(item);
    }

    private void validateSchedule(final T item) {
        if (item.getId() != null) {
            LOGGER.error("Impossible to schedule item because it already has an ID. Item: {}", item);
            throw new OkraRuntimeException();
        }

        if (item.getRunDate() == null) {
            LOGGER.error("Impossible to schedule item because it doesn't have a schedule date. Item: {}", item);
            throw new OkraRuntimeException();
        }
    }
}
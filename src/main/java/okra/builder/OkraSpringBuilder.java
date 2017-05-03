/*
 *  Copyright (c) 2016 Fernando Nogueira
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package okra.builder;

import okra.OkraSpring;
import okra.base.AbstractOkra;
import okra.base.OkraItem;
import okra.exception.InvalidOkraConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public class OkraSpringBuilder<T extends OkraItem> extends OkraBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkraSpringBuilder.class);

    private MongoTemplate mongoTemplate;

    @Override
    public AbstractOkra<T> build() {
        validateConfiguration();
        return new OkraSpring<>(mongoTemplate, getDatabase(),
                getCollection(), getExpireDuration(),
                getExpireDurationUnit(), getScheduleItemClass());
    }

    /**
     * Set mongo template that will be used by Okra
     *
     * @param mongoTemplate the mongo template
     * @return this builder
     */
    public OkraSpringBuilder<T> withMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        return this;
    }

    @Override
    public void validateConfiguration() {
        if (mongoTemplate == null || getCollection() == null
                || getDatabase() == null || getCollection().isEmpty()
                || getDatabase().isEmpty()
                || getExpireDuration() == null
                || getExpireDurationUnit() == null) {
            LOGGER.error("Invalid MongoScheduler configuration. " +
                            "Please verify params: " +
                            "[MongoTemplate not null? {}, Database: {}, " +
                            "Collection: {}, ExpireTime: {}, ExpireTimeUnit: {}]",
                    mongoTemplate != null, getDatabase(), getCollection(),
                    getExpireDuration(), getExpireDurationUnit());

            throw new InvalidOkraConfigurationException();

        }
    }

}

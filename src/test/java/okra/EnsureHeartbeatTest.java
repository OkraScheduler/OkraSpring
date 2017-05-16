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

import okra.model.DefaultOkraItem;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EnsureHeartbeatTest extends OkraBaseContainerTest {

    @Test
    public void ensureHeartbeatTest() {
        final DefaultOkraItem persistedItem = new DefaultOkraItem();
        persistedItem.setRunDate(LocalDateTime.now().minusSeconds(1));
        getDefaultOkra().schedule(persistedItem);

        final Optional<DefaultOkraItem> itemOpt = getDefaultOkra().peek();
        assertThat(itemOpt.isPresent()).isTrue();

        final DefaultOkraItem item = itemOpt.get();
        assertThat(item.getHeartbeat()).isNotNull();
        assertThat(Math.abs(item.getHeartbeat().until(LocalDateTime.now(), ChronoUnit.MICROS)))
                .isLessThan(TimeUnit.MILLISECONDS.toNanos(100));

        final Optional<DefaultOkraItem> itemHeartbeatOpt = getDefaultOkra().heartbeat(item);
        assertThat(itemHeartbeatOpt.isPresent()).isTrue();

        final DefaultOkraItem itemHeartbeat = itemHeartbeatOpt.get();
        assertThat(Math.abs(itemHeartbeat.getHeartbeat().until(LocalDateTime.now(), ChronoUnit.MICROS)))
                .isLessThan(TimeUnit.MILLISECONDS.toNanos(100));

        assertThat(itemHeartbeat).isNotEqualTo(item);
        assertThat(itemHeartbeat.getHeartbeat()).isNotEqualTo(item.getHeartbeat());
    }
}
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

package okra;

import okra.base.Okra;
import okra.model.DefaultOkraItem;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EnsureHearbeatTest extends OkraBaseContainerTest {

    @Test
    public void ensureHeartbeatTest() {

        given_an_item_is_scheduled();

        // Retrieve the item
        Optional<DefaultOkraItem> itemOpt = getDefaultOkra().poll();

        assertThat(itemOpt.isPresent()).isTrue();

        DefaultOkraItem item = itemOpt.get();

        assertThat(item.getHeartbeat()).isNotNull();
        assertThat(
                Math.abs(item.getHeartbeat().until(LocalDateTime.now(), ChronoUnit.MICROS)))
                .isLessThan(TimeUnit.MILLISECONDS.toNanos(100));

        // then, try to heartbeat it
        Optional<DefaultOkraItem> itemHeartbeatOpt = getDefaultOkra().heartbeat(item);

        // Must be succeeded
        assertThat(itemHeartbeatOpt.isPresent()).isTrue();

        DefaultOkraItem itemHeartbeat = itemHeartbeatOpt.get();
        assertThat(
                Math.abs(itemHeartbeat.getHeartbeat().until(LocalDateTime.now(), ChronoUnit.MICROS)))
                .isLessThan(TimeUnit.MILLISECONDS.toNanos(100));

        assertThat(itemHeartbeat).isNotEqualTo(itemOpt.get());
        assertThat(itemHeartbeat.getHeartbeat()).isNotEqualTo(itemOpt.get().getHeartbeat());
    }

    private void given_an_item_is_scheduled() {
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusSeconds(1));
        getDefaultOkra().schedule(item);
    }

    public Okra<DefaultOkraItem> getDefaultOkra() {
        return okraSpringMongo32;
    }
}

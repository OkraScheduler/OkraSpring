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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EnsurePollDoesNotRetrieveTheSameItemTwiceTest extends OkraBaseContainerTest {

    @Test
    public void ensurePollDoesntRetrieveTheSameItemTwiceWhenOnlyAItemIsPersistedBeforeTest() {
        final DefaultOkraItem persistedItem = new DefaultOkraItem();
        persistedItem.setRunDate(LocalDateTime.now().minusNanos(100));
        getDefaultOkra().schedule(persistedItem);

        final Optional<DefaultOkraItem> retrievedOpt = getDefaultOkra().poll();
        assertThat(retrievedOpt.isPresent()).isTrue();

        final DefaultOkraItem item = retrievedOpt.get();
        final Optional<DefaultOkraItem> optThatShouldBeEmpty = getDefaultOkra().poll();
        assertThat(optThatShouldBeEmpty.isPresent()).isFalse();

        getDefaultOkra().delete(item);
    }

    @Test
    public void ensurePollDoesntRetrieveTheSameItemTwiceTest() {
        final DefaultOkraItem persistedItem1 = new DefaultOkraItem();
        persistedItem1.setRunDate(LocalDateTime.now().minusNanos(100));
        getDefaultOkra().schedule(persistedItem1);

        final DefaultOkraItem persistedItem2 = new DefaultOkraItem();
        persistedItem2.setRunDate(LocalDateTime.now().minusNanos(100));
        getDefaultOkra().schedule(persistedItem2);

        final Optional<DefaultOkraItem> retrievedOpt1 = getDefaultOkra().poll();
        assertThat(retrievedOpt1.isPresent()).isTrue();

        final Optional<DefaultOkraItem> retrievedOpt2 = getDefaultOkra().poll();
        assertThat(retrievedOpt2.isPresent()).isTrue();

        assertThat(retrievedOpt1).isNotEqualTo(retrievedOpt2);

        getDefaultOkra().delete(retrievedOpt1.get());
        getDefaultOkra().delete(retrievedOpt2.get());
    }
}
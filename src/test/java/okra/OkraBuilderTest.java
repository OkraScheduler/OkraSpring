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

import com.mongodb.MongoClient;
import okra.base.Okra;
import okra.base.model.OkraItem;
import okra.builder.OkraSpringBuilder;
import okra.exception.InvalidOkraConfigurationException;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class OkraBuilderTest {

    @Test(expected = InvalidOkraConfigurationException.class)
    public void shouldValidateNullMongoTemplate() {
        new OkraSpringBuilder<>()
                .withDatabase("dbName")
                .withCollection("schedulerCollection")
                .build();
    }

    @Test(expected = InvalidOkraConfigurationException.class)
    public void shouldValidateNullCollectionName() throws UnknownHostException {
        new OkraSpringBuilder<>()
                .withMongoTemplate(new MongoTemplate(new MongoClient("localhost"), "dbName"))
                .withDatabase("dbName")
                .build();
    }

    @Test
    public void shouldCreateNewOkra() throws UnknownHostException {
        final Okra<OkraItem> okra = new OkraSpringBuilder<>()
                .withMongoTemplate(new MongoTemplate(new MongoClient("localhost"), "dbName"))
                .withDatabase("dbName")
                .withCollection("testCollection")
                .withExpiration(5, TimeUnit.MINUTES)
                .build();

        assertThat(okra).isNotNull();
    }
}
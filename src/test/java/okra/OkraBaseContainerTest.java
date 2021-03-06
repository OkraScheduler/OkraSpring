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
import okra.builder.OkraSpringBuilder;
import okra.model.DefaultOkraItem;
import org.junit.Before;
import org.junit.ClassRule;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.GenericContainer;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public abstract class OkraBaseContainerTest {

    @ClassRule
    public static GenericContainer mongoContainer = new GenericContainer("mongo:3.4").withExposedPorts(27017);

    private OkraSpring<DefaultOkraItem> okraSpring;

    public static OkraSpring<DefaultOkraItem> prepareDefaultMongo34OkraSpring() throws UnknownHostException {
        final MongoClient client = new MongoClient(
                mongoContainer.getContainerIpAddress(),
                mongoContainer.getMappedPort(27017)
        );

        return (OkraSpring<DefaultOkraItem>) new OkraSpringBuilder<DefaultOkraItem>()
                .withMongoTemplate(new MongoTemplate(client, "okraBenchmark"))
                .withDatabase("okraSpringTests")
                .withCollection("schedulerCollection")
                .withExpiration(5, TimeUnit.MINUTES)
                .withItemClass(DefaultOkraItem.class)
                .build();
    }

    @Before
    public void setUp() throws UnknownHostException {
        okraSpring = prepareDefaultMongo34OkraSpring();
    }

    public OkraSpring<DefaultOkraItem> getDefaultOkra() {
        return okraSpring;
    }
}
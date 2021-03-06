# Okra

A simple and scalable Scheduler that uses MongoDB as backend.

[![codecov](https://codecov.io/gh/OkraScheduler/OkraSpring/branch/master/graph/badge.svg)](https://codecov.io/gh/OkraScheduler/OkraSpring)

### Requirements

* Java 8
* MongoDB

#### Note
For now Okra only has one module which requires Spring Data MongoDB to work, but you can send a Pull Request any time creating a new maven module (like okra-spring) that requires only the raw MongoDB Driver.

### Binaries

[![](https://jitpack.io/v/OkraScheduler/OkraSpring.svg)](https://jitpack.io/#OkraScheduler/OkraSpring)

#### Gradle
build.gradle
```groovy
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```

```groovy
    dependencies {
        compile 'com.github.OkraScheduler:OkraSpring:x.y.z'
    }
```

#### Maven
```xml
	<dependency>
	    <groupId>com.github.OkraScheduler</groupId>
	    <artifactId>OkraSpring</artifactId>
	    <version>x.y.z</version>
	</dependency>

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

### Quick start

Configure a scheduler
```java
public class MyScheduler {
    
    private Okra okra;
    ...
    public void initScheduler() {
        okra = new OkraSpringBuilder<DefaultOkraItem>()
                        .withMongoTemplate(new MongoTemplate(client, "schedulerBenchmark"))
                        .withDatabase("schedulerBenchmark")
                        .withCollection("schedulerCollection")
                        .withExpiration(5, TimeUnit.MINUTES)
                        .withItemClass(DefaultOkraItem.class)
                        .build();        
    }
    ...    
}
```

Then, use this scheduler to retrieve scheduled items...

```java
public class MyScheduler {
    
    private Okra okra;
    ...    
    public void retrieveLoop() {
        while (running) {
            Optional<DefaultOkraItem> scheduledOpt = okra.poll();
                if (scheduled.isPresent()) {
                    doSomeWork(scheduledOpt.get());                
                }    
        }
    }
    ...    
}
```
#### Build

To build:

```bash
$ git clone git@github.com:fernandonogueira/okra.git
$ cd okra
$ mvn install -DskipTests
```
[![Build Status](https://travis-ci.org/OkraScheduler/OkraSpring.svg?branch=master)](https://travis-ci.org/OkraScheduler/OkraSpring)

### LICENSE
```
MIT License

Copyright (c) 2016 Fernando Nogueira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
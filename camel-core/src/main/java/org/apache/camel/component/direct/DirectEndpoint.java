/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.direct;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.util.ObjectHelper;

/**
 * Represents a direct endpoint that synchronously invokes the consumer of the
 * endpoint when a producer sends a message to it.
 *
 * @version 
 */
@UriEndpoint(scheme = "direct", consumerClass = DirectConsumer.class)
public class DirectEndpoint extends DefaultEndpoint {

    private volatile Map<String, DirectConsumer> consumers;
    @UriParam
    private boolean block;
    @UriParam
    private long timeout = 30000L;

    public DirectEndpoint() {
        this.consumers = new HashMap<String, DirectConsumer>();
    }

    public DirectEndpoint(String endpointUri, Component component) {
        this(endpointUri, component, new HashMap<String, DirectConsumer>());
    }

    public DirectEndpoint(String uri, Component component, Map<String, DirectConsumer> consumers) {
        super(uri, component);
        this.consumers = consumers;
    }

    public Producer createProducer() throws Exception {
        if (block) {
            return new DirectBlockingProducer(this);
        } else {
            return new DirectProducer(this);
        }
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer answer = new DirectConsumer(this, processor);
        configureConsumer(answer);
        return answer;
    }

    public boolean isSingleton() {
        return true;
    }

    public void addConsumer(DirectConsumer consumer) {
        String key = consumer.getEndpoint().getKey();
        consumers.put(key, consumer);
    }

    public void removeConsumer(DirectConsumer consumer) {
        String key = consumer.getEndpoint().getKey();
        consumers.remove(key);
    }

    public boolean hasConsumer(DirectConsumer consumer) {
        String key = consumer.getEndpoint().getKey();
        return consumers.containsKey(key);
    }

    public DirectConsumer getConsumer() {
        String key = getKey();
        return consumers.get(key);
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    protected String getKey() {
        String uri = getEndpointUri();
        if (uri.indexOf('?') != -1) {
            return ObjectHelper.before(uri, "?");
        } else {
            return uri;
        }
    }
}

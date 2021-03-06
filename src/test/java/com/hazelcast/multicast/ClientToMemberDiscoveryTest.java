/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.multicast;

import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(HazelcastSerialClassRunner.class)
public class ClientToMemberDiscoveryTest extends HazelcastTestSupport {

    Config serverConfig;
    HazelcastInstance instance1;
    HazelcastInstance instance2;
    public final TestHazelcastFactory factory = new TestHazelcastFactory();

    @Before
    public void setup() {
        String serverXmlFileName = "hazelcast-multicast-plugin.xml";

        InputStream xmlResource = MulticastDiscoveryStrategy.class.getClassLoader().getResourceAsStream(serverXmlFileName);
        serverConfig = new XmlConfigBuilder(xmlResource).build();
    }

    @Test
    public void clientTest() {
        instance1 = factory.newHazelcastInstance(serverConfig);
        instance2 = factory.newHazelcastInstance(serverConfig);

        final HazelcastInstance client = factory.newHazelcastClient();

        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Set<Member> members = client.getCluster().getMembers();
                assertEquals(2, members.size());
            }
        });
        factory.shutdownAll();
    }
}

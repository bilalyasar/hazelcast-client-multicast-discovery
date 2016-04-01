package com.hazelcast.multicast;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

/**
 * Created by bilal on 31/03/16.
 */
@RunWith(HazelcastSerialClassRunner.class)
public class MemberToMemberDiscoveryTest extends HazelcastTestSupport {

    Config config;
    HazelcastInstance[] instances;

    @Before
    public void setup() {
        String xmlFileName = "hazelcast-multicast-plugin.xml";
        InputStream xmlResource = MulticastDiscoveryStrategy.class.getClassLoader().getResourceAsStream(xmlFileName);
        config = new XmlConfigBuilder(xmlResource).build();


    }


    @Test
    public void formClusterWithTwoMembersTest() throws InterruptedException {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        instances = factory.newInstances(config);
        assertClusterSizeEventually(2, instances[0]);
        instances[0].shutdown();
        instances[1].shutdown();
    }

}

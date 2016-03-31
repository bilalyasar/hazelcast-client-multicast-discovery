package com.hazelcast.multicast;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Created by bilal on 09/03/16.
 */
public class MulticastDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return MulticastDiscoveryStrategy.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        return new MulticastDiscoveryStrategy(discoveryNode,properties);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        return null;
    }
}

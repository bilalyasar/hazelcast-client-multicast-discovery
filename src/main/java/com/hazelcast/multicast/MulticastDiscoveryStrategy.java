package com.hazelcast.multicast;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;


public class MulticastDiscoveryStrategy implements DiscoveryStrategy {

    private DiscoveryNode discoveryNode;
    private MulticastSocket multicastSocket;
    Thread t;
    private Map<String, Comparable> properties;
    private static final int DATA_OUTPUT_BUFFER_SIZE = 1024;
    boolean isClient;

    private MulticastDiscoveryReceiver multicastDiscoveryReceiver;
    private MulticastDiscoverySender multicastDiscoverySender;

    public MulticastDiscoveryStrategy(DiscoveryNode discoveryNode, Map<String, Comparable> properties) {
        this.discoveryNode = discoveryNode;
        this.properties = properties;

    }

    private void initializeMulticastSocket() {
        try {
            int port = getOrDefault(MulticastProperties.PORT, 54327);
            String group = getOrDefault(MulticastProperties.GROUP, "224.2.2.3");
            multicastSocket = new MulticastSocket(port);
            multicastSocket.setReuseAddress(true);
            multicastSocket.setTimeToLive(255);
            multicastSocket.setReceiveBufferSize(64 * DATA_OUTPUT_BUFFER_SIZE);
            multicastSocket.setSendBufferSize(64 * DATA_OUTPUT_BUFFER_SIZE);
            multicastSocket.setSoTimeout(3000);
            multicastSocket.joinGroup(InetAddress.getByName(group));
            multicastDiscoverySender = new MulticastDiscoverySender(discoveryNode, multicastSocket);
            multicastDiscoveryReceiver = new MulticastDiscoveryReceiver(multicastSocket);
            if (discoveryNode != null) {
                isClient = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void start() {
        initializeMulticastSocket();
        if (!isClient) {
            t = new Thread(multicastDiscoverySender);
            t.start();
        }
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        DiscoveryNode discoveryNode = null;
        MemberInfo memberInfo = multicastDiscoveryReceiver.receive();
        if (memberInfo == null) return null;
        ArrayList<DiscoveryNode> arrayList = new ArrayList<DiscoveryNode>();
        try {
            discoveryNode = new SimpleDiscoveryNode(new Address(memberInfo.getHost(), memberInfo.getPort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        arrayList.add(discoveryNode);
        return arrayList;
    }

    @Override
    public void destroy() {
        t.stop();
    }

    private <T extends Comparable> T getOrNull(PropertyDefinition property) {
        return getOrDefault(property, null);
    }

    private <T extends Comparable> T getOrDefault(PropertyDefinition property, T defaultValue) {

        if (properties == null || property == null) {
            return defaultValue;
        }

        Comparable value = properties.get(property.key());
        if (value == null) {
            return defaultValue;
        }

        return (T) value;
    }


}

package com.hazelcast.multicast;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;


public class MulticastDiscoveryStrategy implements DiscoveryStrategy {

    private MulticastSocket multicastSocket;
    private DatagramPacket datagramPacketSend;
    private DatagramPacket datagramPacketReceive;
    private ByteArraySerializer byteArraySerializer;
    private static final int THREAD_CLOSE_TIMEOUT = 5;
    private static final int DATA_OUTPUT_BUFFER_SIZE = 1024;
    
    private MulticastDiscoveryReceiver multicastDiscoveryReceiver;
    private MulticastDiscoverySender multicastDiscoverySender;

    public MulticastDiscoveryStrategy(DiscoveryNode discoveryNode, Map<String, Comparable> properties) {
        try {
            multicastSocket = new MulticastSocket(54327);
            multicastSocket.setReuseAddress(true);
            multicastSocket.setTimeToLive(255);
            multicastSocket.setReceiveBufferSize(64 * 1024);
            multicastSocket.setSendBufferSize(64 * 1024);
            multicastSocket.setSoTimeout(1000);
            multicastSocket.joinGroup(InetAddress.getByName("224.2.2.3"));
            multicastDiscoverySender = new MulticastDiscoverySender(discoveryNode, multicastSocket);
            multicastDiscoveryReceiver = new MulticastDiscoveryReceiver(multicastSocket);
            if (discoveryNode != null) {
                new Thread(multicastDiscoverySender).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void start() {
        try {
            datagramPacketSend = new DatagramPacket(new byte[0], 0, InetAddress
                    .getByName("224.2.2.3"), 54327);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        System.out.println("discover nodes");
        MemberInfo memberInfo = multicastDiscoveryReceiver.receive();
        if (memberInfo == null) return null;
        ArrayList<DiscoveryNode> arrayList = new ArrayList<DiscoveryNode>();
        DiscoveryNode discoveryNode = null;
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

    }

}

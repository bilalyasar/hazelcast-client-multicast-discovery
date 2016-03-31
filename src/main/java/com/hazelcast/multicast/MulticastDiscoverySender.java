package com.hazelcast.multicast;

import com.hazelcast.spi.discovery.DiscoveryNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by bilal on 30/03/16.
 */
public class MulticastDiscoverySender implements Runnable {

    DiscoveryNode discoveryNode;
    MulticastSocket multicastSocket;
    MemberInfo memberInfo;
    DatagramPacket datagramPacket;

    public MulticastDiscoverySender(DiscoveryNode discoveryNode, MulticastSocket multicastSocket) throws IOException {
        this.discoveryNode = discoveryNode;
        this.multicastSocket = multicastSocket;
        if (discoveryNode != null)
            memberInfo = new MemberInfo(discoveryNode.getPublicAddress().getHost(), discoveryNode.getPublicAddress().getPort());
        initDatagramPacket();
    }

    private void initDatagramPacket() throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(memberInfo);
        byte[] yourBytes = bos.toByteArray();
        datagramPacket = new DatagramPacket(yourBytes, yourBytes.length, InetAddress
                .getByName("224.2.2.3"), 54327);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void send() throws IOException {
        multicastSocket.send(datagramPacket);
    }
}

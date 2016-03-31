package com.hazelcast.multicast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastDiscoveryReceiver {

    MulticastSocket multicastSocket;
    private static final int DATAGRAM_BUFFER_SIZE = 64 * 1024;
    DatagramPacket datagramPacketReceive = new DatagramPacket(new byte[DATAGRAM_BUFFER_SIZE], DATAGRAM_BUFFER_SIZE);

    public MulticastDiscoveryReceiver(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    public MemberInfo receive() {
        try {
            Object o;
            multicastSocket.receive(datagramPacketReceive);
            byte[] data = datagramPacketReceive.getData();
            int length = datagramPacketReceive.getLength();
            int offset = datagramPacketReceive.getOffset();
            MemberInfo memberInfo = null;
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                o = in.readObject();
                memberInfo = (MemberInfo) o;
            } finally {
                try {
                    bis.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }
                return memberInfo;
            }

        } catch (Exception e) {
        }
        return null;
    }
}

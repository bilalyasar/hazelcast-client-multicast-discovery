package com.hazelcast.multicast;

import java.io.Serializable;

/**
 * Created by bilal on 30/03/16.
 */
public class MemberInfo implements Serializable {

    private String host;
    private int port;

    public MemberInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}

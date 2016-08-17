package com.nicholassavilerobinson;

import java.net.InetAddress;
import java.util.Date;

public class SampleCacheNode {

    private final InetAddress address;
    private final Date created;

    public SampleCacheNode(final InetAddress address) {
        this(address, new Date());
    }

    public SampleCacheNode(final InetAddress address, final Date created) {
        this.address = address;
        this.created = created; // Storing "created" and not "expires" allows expiration to be changed at run-time
    }

    public Date getCreated() {
        return created;
    }

    public InetAddress getAddress() {
        return address;
    }

}

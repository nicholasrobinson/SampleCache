package com.nicholassavilerobinson;

import org.junit.Test;

import java.net.InetAddress;
import java.util.Date;

import static org.junit.Assert.*;

public class SampleCacheNodeTest {

    // Check that cache node can store the address and created date
    @Test
    public void testNodeConstructor() throws Exception {
        InetAddress address = InetAddress.getByName("127.0.0.1");
        Date created = new Date();
        SampleCacheNode node = new SampleCacheNode(address, created);
        assertEquals(address, node.getAddress());
        assertEquals(created, node.getCreated());
    }

    // Check that cache node can automatically set created date
    @Test
    public void testNodeSimpleConstructor() throws Exception {
        InetAddress address = InetAddress.getByName("192.168.0.1");
        Date beforeCreated = new Date();
        SampleCacheNode node = new SampleCacheNode(address);
        Date nodeCreated = node.getCreated();
        Date afterCreated = new Date();
        assertEquals(address, node.getAddress());
        assertTrue(
                (beforeCreated.equals(nodeCreated) || beforeCreated.before(nodeCreated)) &&
                (afterCreated.equals(nodeCreated) || afterCreated.after(nodeCreated))
        );
    }

}
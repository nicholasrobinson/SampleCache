package com.nicholassavilerobinson;

import java.net.InetAddress;

public interface AddressCache {
    /**
     * Adds the given {@link InetAddress} and returns {@code true} on success.
     */
    boolean offer(InetAddress address);
    
    /**
     * Returns {@code true} if the given {@link InetAddress} 
     * is in the {@link AddressCache}.
     */
    boolean contains(InetAddress address);
    
    /**
     * Removes the given {@link InetAddress} and returns {@code true}
     * on success.
     */
    boolean remove(InetAddress address);
    
    /**
     * Returns the most recently added {@link InetAddress} and returns 
     * {@code null} if the {@link AddressCache} is empty.
     */
    InetAddress peek();
    
    /**
     * Removes and returns the most recently added {@link InetAddress} and  
     * returns {@code null} if the {@link AddressCache} is empty.
     */
    InetAddress remove();
    
    /**
     * Retrieves and removes the most recently added {@link InetAddress},
     * waiting if necessary until an element becomes available.
     */
    InetAddress take() throws InterruptedException;
    
    /**
     * Closes the {@link AddressCache} and releases all resources.
     */
    void close();
    
    /**
     * Returns the number of elements in the {@link AddressCache}.
     */
    int size();
    
    /**
     * Returns {@code true} if the {@link AddressCache} is empty.
     */
    boolean isEmpty();
}

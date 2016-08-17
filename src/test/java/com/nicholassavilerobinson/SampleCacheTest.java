package com.nicholassavilerobinson;

import org.junit.Test;

import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SampleCacheTest {

    /** Check that SampleCache::size() works correctly
     *
     * Returns the number of elements in the {@link com.nicholassavilerobinson.AddressCache}.
     */
    @Test
    public void testSize() throws Exception {
        final SampleCache cache = new SampleCache();
        // Initial cache size should be 0
        assertEquals(cache.size(), 0);
        // Adding addresses should increase cache size
        cache.offer(InetAddress.getByName("127.0.0.1"));
        assertEquals(1, cache.size());
        cache.offer(InetAddress.getByName("192.168.0.1"));
        assertEquals(2, cache.size());
        // Removing addreses should decrease cache size
        cache.remove();
        assertEquals(1, cache.size());
        cache.remove();
        assertEquals(0, cache.size());
        // Removing addresses from an empty cache should not affect size
        cache.remove();
        assertEquals(0, cache.size());
        cache.close();
    }

    /** Check that cache SampleCache::isEmpty() works correctly
     *
     * Returns {@code true} if the {@link com.nicholassavilerobinson.AddressCache} is empty.
     */
    @Test
    public void testIsEmpty() throws Exception {
        final SampleCache cache = new SampleCache();
        // Initial cache should be empty
        assertTrue(cache.isEmpty());
        // Adding addresses should make cache not empty
        cache.offer(InetAddress.getByName("127.0.0.1"));
        assertFalse(cache.isEmpty());
        // Removing an address should make cache empty again
        cache.remove();
        assertTrue(cache.isEmpty());
        // Removing an address from an empty cache should still be empty
        cache.remove();
        assertTrue(cache.isEmpty());
        cache.close();
    }

    /** Check that SampleCache::offer(InetAddress address) works correctly
     *
     * Adds the given {@link InetAddress} and returns {@code true} on success.
     */
    @Test
    public void testOffer() throws Exception {
        final SampleCache cache = new SampleCache();
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        // Check that address can be offered and located/examined/removed
        assertTrue(cache.offer(address));
        assertTrue(cache.contains(address));
        assertEquals(address, cache.peek());
        assertEquals(address, cache.remove());
        cache.close();
    }

    /** Check that SampleCache::remove() methods work correctly
     *
     * SampleCache::remove()
     * Removes and returns the most recently added {@link java.net.InetAddress} and returns {@code null} if the
     * {@link com.nicholassavilerobinson.AddressCache} is empty.
     *
     * and
     *
     * SampleCache::remove(InetAddress address)
     * Removes the given {@link java.net.InetAddress} and returns {@code true} on success.
     */
    @Test
    public void testRemove() throws Exception {
        final SampleCache cache = new SampleCache();
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        cache.offer(address);
        final InetAddress address2 = InetAddress.getByName("192.168.0.1");
        cache.offer(address2);
        // Check that remove returns the most recently added address
        assertEquals(address2, cache.remove());
        // Check that targeted remove returns true
        assertTrue(cache.remove(address));
        // Check that remove returns null
        assertNull(cache.remove());
        // Check that targeted remove returns false
        assertFalse(cache.remove(address));
        cache.close();

    }

    /** Check that SampleCache::contains(InetAddress address) works correctly
     *
     * Returns {@code true} if the given {@link InetAddress} is in the {@link com.nicholassavilerobinson.AddressCache}.
     */
    @Test
    public void testContains() throws Exception {
        final SampleCache cache = new SampleCache();
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        cache.offer(address);
        // Ensure duplicate addresses are tolerated
        final InetAddress address2 = InetAddress.getByName("127.0.0.1");
        cache.offer(address2);
        final InetAddress address3 = InetAddress.getByName("192.168.0.1");
        assertTrue(cache.contains(address));
        assertFalse(cache.contains(address3));
        cache.remove();
        assertTrue(cache.contains(address2));
        cache.close();
    }

    /** Check that SampleCache::peek() works correctly
     *
     * Returns the most recently added {@link InetAddress} and returns {@code null} if the
     * {@link com.nicholassavilerobinson.AddressCache} is empty.
     */
    @Test
    public void testPeek() throws Exception {
        final SampleCache cache = new SampleCache();
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        cache.offer(address);
        final InetAddress address2 = InetAddress.getByName("192.168.0.1");
        cache.offer(address2);
        // Returns the most recently added {@link InetAddress}
        assertEquals(address2, cache.peek());
        // returns {@code null} if the {@link AddressCache} is empty.
        cache.remove();
        cache.remove();
        assertNull(cache.peek());
        cache.close();
    }

    /** Check that SampleCache::take() works correctly
     *
     * Retrieves and removes the most recently added {@link java.net.InetAddress}, waiting if necessary until an
     * element becomes available.
     */
    @Test
    public void testTake() throws Exception {
        final SampleCache cache = new SampleCache();
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        cache.offer(address);
        final InetAddress address2 = InetAddress.getByName("192.168.0.1");
        cache.offer(address2);
        final InetAddress address3 = InetAddress.getByName("192.168.0.2");
        // Returns the most recently added {@link InetAddress}
        assertEquals(address2, cache.take());
        assertEquals(address, cache.take());
        // Waits until an element becomes available
        final Timer timer = new Timer("delayedOffer");
        class DelayedOffer extends TimerTask {
            private final SampleCache cache;
            private final InetAddress address;
            protected DelayedOffer(SampleCache cache, InetAddress address) {
                this.cache = cache;
                this.address = address;
            }
            public void run() {
                cache.offer(address);
            }
        }
        final long delayMilliseconds = 1000L;
        final Date beforeWait = new Date();
        timer.schedule(new DelayedOffer(cache, address3), delayMilliseconds);
        assertEquals(address3, cache.take());
        final Date afterWait = new Date();
        assertTrue(afterWait.getTime() - beforeWait.getTime() >= delayMilliseconds);
        cache.close();
    }

    /** Check that SampleCache::close() works correctly
     *
     * Closes the {@link com.nicholassavilerobinson.AddressCache} and releases all resources.
     */
    @Test
    public void testClose() throws Exception {
        final SampleCache cache = new SampleCache();
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        cache.offer(address);
        final InetAddress address2 = InetAddress.getByName("192.168.0.1");
        cache.offer(address2);
        // Verify that a closed cache is empty
        cache.close();
        assertTrue(cache.isEmpty());
    }

    /** Check that SampleCache's internal cleanup task works correctly
     *
     * A cleanup task runs every 5 seconds to evict expired addresses
     */
    @Test
    public void testCleanupTask() throws Exception {
        final SampleCache cache = new SampleCache();
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        final InetAddress address2 = InetAddress.getByName("192.168.0.1");
        final long cachingTime = cache.getCachingTime();
        final long lessThanCachingTime = cachingTime - 100L;
        // Verify that cleanup task only evicts expired addresses
        cache.offer(address);
        TimeUnit.MILLISECONDS.sleep(lessThanCachingTime);
        cache.offer(address2);
        assertEquals(2, cache.size());
        TimeUnit.MILLISECONDS.sleep(lessThanCachingTime);
        assertEquals(1, cache.size());
        TimeUnit.MILLISECONDS.sleep(cachingTime);
        assertTrue(cache.isEmpty());
        cache.close();
    }

}
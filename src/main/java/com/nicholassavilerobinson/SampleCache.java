package com.nicholassavilerobinson;

import java.net.InetAddress;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Implementation of AddressCache interface
 */
public class SampleCache implements AddressCache {

    private static final long CLEANUP_TASK_INTERVAL = 5000L;

    private static final long DEFAULT_CACHING_TIME = 5000L;

    private LinkedBlockingDeque<SampleCacheNode> cache;

    private ConcurrentHashMap<InetAddress, Integer> containsMap;

    private final ScheduledExecutorService cleanupScheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> cleanupTaskHandle;

    private final long cachingTime;

    public SampleCache() {
        this(DEFAULT_CACHING_TIME);
    }

    public SampleCache(final long cachingTime) {
        this.cachingTime = cachingTime;
        cache = new LinkedBlockingDeque<>();
        containsMap = new ConcurrentHashMap<>();
        startCleanupTask();
    }

    // Return user configured caching time
    public long getCachingTime() {
        return cachingTime;
    }

    /**
     * Adds the given {@link java.net.InetAddress} and returns {@code true} on success.
     *
     * Asymptotic complexity: O(1)
     *
     * @param address InetAddress to be added
     */
    @Override
    public synchronized boolean offer(final InetAddress address) {
        addAddressToContainsMap(address);
        return cache.offerFirst(new SampleCacheNode(address));
    }

    /**
     * Returns {@code true} if the given {@link java.net.InetAddress} is in the {@link com.nicholassavilerobinson.AddressCache}.
     *
     * Asymptotic complexity: O(1)
     *
     * @param address InetAddress to search for
     */
    @Override
    public boolean contains(final InetAddress address) {
        return containsMap.containsKey(address);
    }

    /**
     * Removes the given {@link java.net.InetAddress} and returns {@code true} on success.
     *
     * Asymptotic complexity: O(n)
     *
     * @param address InetAddress to be removed
     */
    @Override
    public synchronized boolean remove(final InetAddress address) {
        if (contains(address)) {
            removeAddressFromContainsMap(address);
            for (SampleCacheNode node : cache) {
                if (node.getAddress().equals(address)) {
                    return cache.remove(node);
                }
            }
        }
        return false;
    }

    /**
     * Returns the most recently added {@link java.net.InetAddress} and returns {@code null} if the
     * {@link com.nicholassavilerobinson.AddressCache} is empty.
     *
     * Asymptotic complexity: O(1)
     */
    @Override
    public InetAddress peek() {
        try {
            return cache.peek().getAddress();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    /**
     * Removes and returns the most recently added {@link java.net.InetAddress} and returns {@code null} if the
     * {@link com.nicholassavilerobinson.AddressCache} is empty.
     *
     * Asymptotic complexity: O(1)
     */
    @Override
    public synchronized InetAddress remove() {
        try {
            final InetAddress address = cache.remove().getAddress();
            removeAddressFromContainsMap(address);
            return address;
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    /**
     * Retrieves and removes the most recently added {@link java.net.InetAddress}, waiting if necessary until an
     * element becomes available.
     *
     * Asymptotic complexity: O(1)
     */
    @Override
    public InetAddress take() throws InterruptedException {
        final InetAddress address = cache.take().getAddress();
        removeAddressFromContainsMap(address);
        return address;
    }

    /**
     * Closes the {@link com.nicholassavilerobinson.AddressCache} and releases all resources.
     *
     * Asymptotic complexity: O(n)
     */
    @Override
    public synchronized void close() {
        cleanupTaskHandle.cancel(true);
        cache.clear();
        containsMap.clear();
    }

    /**
     * Returns the number of elements in the {@link com.nicholassavilerobinson.AddressCache}.
     *
     * Asymptotic complexity: O(1)
     */
    @Override
    public int size() {
        return cache.size();
    }

    /**
     * Returns {@code true} if the {@link com.nicholassavilerobinson.AddressCache} is empty.
     *
     * Asymptotic complexity: O(1)
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Utility method to evict expired addresses from the cache
     *
     * Asymptotic complexity: O(n) [where n is the number of expired addresses]
     */
    private synchronized void evictExpiredAddresses() {
        final long now = new Date().getTime();
        while (!cache.isEmpty() && (now - cache.peekLast().getCreated().getTime() > cachingTime)) {
            removeAddressFromContainsMap(cache.removeLast().getAddress());
        }
    }

    // Utility method to update contains map for O(1) SampleCache::contains() complexity
    private synchronized void addAddressToContainsMap(InetAddress address) {
        try {
            final Integer oldContainsMapCount = containsMap.get(address);
            containsMap.put(address, oldContainsMapCount != null ? oldContainsMapCount + 1 : 1);
        } catch (NullPointerException ignored) {
        }
    }

    // Utility method to update contains map for O(1) SampleCache::contains() complexity
    private synchronized void removeAddressFromContainsMap(InetAddress address) {
        try {
            final Integer containsMapCount = containsMap.get(address);
            if (containsMapCount == null || containsMapCount <= 1) {
                containsMap.remove(address);
            } else {
                containsMap.put(address, containsMapCount - 1);
            }
        } catch (NullPointerException ignored) {
        }
    }

    // A cleanup task that runs every CLEANUP_TASK_INTERVAL milliseconds to evict expired addresses
    private void startCleanupTask() {
        final Runnable cleanupTask = new Runnable() {
            public void run() {
                evictExpiredAddresses();
            }
        };
        cleanupTaskHandle = cleanupScheduler.scheduleAtFixedRate(cleanupTask, CLEANUP_TASK_INTERVAL, CLEANUP_TASK_INTERVAL, MILLISECONDS);
    }

}

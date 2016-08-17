#Overview

Java 8 sample code demonstrating implementation and unit-tests for an InetAddress cache.

The cache has a "Last-In-First-Out" (LIFO) retrieval policy and a "First-In-First-Out" (FIFO) eviction policy. Methods such as peek(), remove() and take() retrieve the most recently added element and an internal cleanup task that in periodic intervals removes the oldest elements from the cache.

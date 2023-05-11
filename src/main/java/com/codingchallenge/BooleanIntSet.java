package com.codingchallenge;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class BooleanIntSet implements IntSet {

    private static final int DEFAULT_BUCKET_SIZE = 64;

    private final AtomicInteger totalUnique = new AtomicInteger(0);
    private final AtomicInteger totalDuplicate = new AtomicInteger(0);

    private final int bucketSize;
    private final boolean[][] buckets;
    private final Writer logWriter;

    public BooleanIntSet(int capacity) {
        this(capacity, DEFAULT_BUCKET_SIZE, null);
    }

    public BooleanIntSet(int capacity, Writer logWriter) {
        this(capacity, DEFAULT_BUCKET_SIZE, logWriter);
    }

    public BooleanIntSet(int capacity, int bucketSize, Writer logWriter) {
        this.bucketSize = bucketSize;
        this.buckets = new boolean[capacity / bucketSize][];
        this.logWriter = logWriter;
    }

    public static void main(String[] args) {
        IntSet intSet = new BooleanIntSet(1_000_000_000);

        Instant now = Instant.now();
        for (int i = 0; i < 1_000_000_000; i++) {
            intSet.add(i);
        }
        System.out.println("runtime (ms) " + (Math.abs(Instant.now().toEpochMilli() - now.toEpochMilli())));

        long heap = Runtime.getRuntime().totalMemory();
        long heapMax = Runtime.getRuntime().maxMemory();
        long heapFree = Runtime.getRuntime().freeMemory();
        System.out.println("heap (MB) " + heap / 1_000_000);
        System.out.println("heap max (MB) " + heapMax / 1_000_000);
        System.out.println("heap free (MB) " + heapFree / 1_000_000);

        // Microsoft Windows 10 Pro x64-based PC 10.0.19044 Build 19044
        // Processor Intel(R) Core(TM) i7-10700K CPU @ 3.80GHz, 3801 Mhz, 8 Core(s), 16 Logical Processor(s)
        // Total Physical Memory 31.9 GB

        // runtime (ms) 9601
        // heap (MB) 1778
        // heap max (MB) 8562
        // heap free (MB) 358
    }

    @Override
    public synchronized void add(int val) {
        int idx = val / this.bucketSize;
        if (this.buckets[idx] == null) {
            this.buckets[idx] = new boolean[this.bucketSize];
        }
        int mod = val % this.bucketSize;
        boolean currVal = this.buckets[idx][mod];
        if (currVal) {
            totalDuplicate.incrementAndGet();
        } else {
            this.buckets[idx][mod] = true;
            totalUnique.incrementAndGet();
            if (this.logWriter != null) {
                String num = StringUtils.leftPad(String.valueOf(val), 9, "0") + "\n";
                try {
                    logWriter.write(num);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Stats getStats() {
        return new Stats(this.totalUnique.get(), this.totalDuplicate.get());
    }

}

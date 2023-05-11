package com.codingchallenge;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BitWiseIntSet implements IntSet {

    private final List<Long> values = new ArrayList<>();
    private final AtomicInteger totalUnique = new AtomicInteger(0);
    private final AtomicInteger totalDuplicate = new AtomicInteger(0);

    private final Writer logWriter;

    public BitWiseIntSet() {
        this.logWriter = null;
    }

    public BitWiseIntSet(Writer logWriter) {
        this.logWriter = logWriter;
    }

    public static void main(String[] args) {
        IntSet intSet = new BitWiseIntSet();

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

        // runtime (ms) 19734
        // heap (MB) 2577
        // heap max (MB) 8562
        // heap free (MB) 1093
    }

    @Override
    public synchronized void add(int val) {
        int idx = val / 63;
        int bit = val % 63;

        if (has(idx, bit)) {
            this.totalDuplicate.incrementAndGet();
        } else {
            this.totalUnique.incrementAndGet();
        }

        synchronized (this) {
            while (values.size() <= idx) {
                values.add(0L);
            }
            values.set(idx, values.get(idx) | (1L << bit));
        }

        if (this.logWriter != null) {
            String num = StringUtils.leftPad(String.valueOf(val), 9, "0") + "\n";
            try {
                logWriter.write(num);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean has(int idx, int bit) {
        return idx < this.values.size() && ((this.values.get(idx) >> bit) & 1) == 1;
    }

    @Override
    public Stats getStats() {
        return new Stats(this.totalUnique.get(), this.totalDuplicate.get());
    }

}

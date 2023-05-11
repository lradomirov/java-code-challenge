package com.codingchallenge;

public class Stats {

    private final int totalUniqueRecords;
    private final int totalDuplicateRecords;

    public Stats(int totalUniqueRecords, int totalDuplicateRecords) {
        this.totalUniqueRecords = totalUniqueRecords;
        this.totalDuplicateRecords = totalDuplicateRecords;
    }

    public int getTotalUniqueRecords() {
        return this.totalUniqueRecords;
    }

    public int getTotalDuplicateRecords() {
        return this.totalDuplicateRecords;
    }

}

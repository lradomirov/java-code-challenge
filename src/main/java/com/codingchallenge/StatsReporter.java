package com.codingchallenge;

public class StatsReporter {

    private final IntSet intSet;

    private Stats previousStats;

    public StatsReporter(IntSet intSet) {
        this.intSet = intSet;
    }

    public String getStatsOutput() {
        Stats latestStats = this.intSet.getStats();

        int relativeUnique = this.previousStats != null ?
                latestStats.getTotalUniqueRecords() - previousStats.getTotalUniqueRecords() : latestStats.getTotalUniqueRecords();
        int relativeDuplicate = this.previousStats != null ?
                latestStats.getTotalDuplicateRecords() - previousStats.getTotalDuplicateRecords() : latestStats.getTotalDuplicateRecords();
        int totalUnique = latestStats.getTotalUniqueRecords();

        this.previousStats = latestStats;

        return "received " + relativeUnique + " unique, " + relativeDuplicate + " duplicate numbers. total unique: " + totalUnique;
    }

}

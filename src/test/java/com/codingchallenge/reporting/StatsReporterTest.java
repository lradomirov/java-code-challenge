package com.codingchallenge.reporting;

import com.codingchallenge.Stats;
import com.codingchallenge.StatsReporter;
import com.codingchallenge.IntSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatsReporterTest {

    @Test
    void shouldReturnProperlyFormattedStatsOutputString() {
        StatsReporter statsReporter = new StatsReporter(new IntSetMock(10, 5));
        Assertions.assertEquals("received 10 unique, 5 duplicate numbers. total unique: 10", statsReporter.getStatsOutput());
    }

    @Test
    void shouldReturnDiffStatsOutputAfterStatsChange() {
        IntSet intSetMock = new IntSetMock(10, 5);
        StatsReporter statsReporter = new StatsReporter(intSetMock);

        String initialStatsOutput = statsReporter.getStatsOutput();
        intSetMock.add(1);
        intSetMock.add(2);
        String diffStatsOutput = statsReporter.getStatsOutput();

        Assertions.assertEquals("received 10 unique, 5 duplicate numbers. total unique: 10", initialStatsOutput);
        Assertions.assertEquals("received 1 unique, 1 duplicate numbers. total unique: 11", diffStatsOutput);
    }

    private static class IntSetMock implements IntSet {

        private int totalUniqueRecords;
        private int totalDuplicateRecords;

        IntSetMock(int totalUniqueRecords, int totalDuplicateRecords) {
            this.totalUniqueRecords = totalUniqueRecords;
            this.totalDuplicateRecords = totalDuplicateRecords;
        }

        @Override
        public void add(int val) {
            if (val % 2 == 0) {
                totalUniqueRecords += 1;
            } else {
                totalDuplicateRecords += 1;
            }
        }

        @Override
        public Stats getStats() {
            return new Stats(this.totalUniqueRecords, this.totalDuplicateRecords);
        }
    }

}

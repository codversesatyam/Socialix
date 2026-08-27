package com.example.socialix.models;

import java.util.List;
import java.util.Map;

public class AnalyticsModel {
    private long totalReach;
    private double growthPercentage;
    private List<DataPoint> chartPoints;
    private Map<String, Long> platformBreakdown;

    public static class DataPoint {
        private String label;
        private long value;

        public String getLabel() { return label; }
        public long getValue() { return value; }
    }

    public long getTotalReach() { return totalReach; }
    public double getGrowthPercentage() { return growthPercentage; }
    public List<DataPoint> getChartPoints() { return chartPoints; }
    public Map<String, Long> getPlatformBreakdown() { return platformBreakdown; }
}
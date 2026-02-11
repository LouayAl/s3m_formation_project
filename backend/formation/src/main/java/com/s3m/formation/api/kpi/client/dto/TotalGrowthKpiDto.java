package com.s3m.formation.api.kpi.client.dto;

import java.util.List;
import java.util.Map;

public class TotalGrowthKpiDto {
    private List<String> categories; // months
    private List<SeriesData> series; // Top Formation & Autres
    private Map<String, String> topFormationsByMonth; // tooltip info

    // getters / setters

    public static class SeriesData {
        private String name;
        private List<Double> data;

        public SeriesData(String name, List<Double> data) {
            this.name = name;
            this.data = data;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<Double> getData() { return data; }
        public void setData(List<Double> data) { this.data = data; }
    }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public List<SeriesData> getSeries() { return series; }
    public void setSeries(List<SeriesData> series) { this.series = series; }

    public Map<String, String> getTopFormationsByMonth() { return topFormationsByMonth; }
    public void setTopFormationsByMonth(Map<String, String> topFormationsByMonth) { this.topFormationsByMonth = topFormationsByMonth; }
}

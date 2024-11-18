package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BenchmarkGraph {

    public static void main(String[] args) throws IOException {
        generateCharts();
    }

    public static void generateCharts() throws IOException {
        DefaultCategoryDataset tsHashMapDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset concHashMapDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset combinedDataset = new DefaultCategoryDataset();

        // Parse the JMH result JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(Files.newBufferedReader(Paths.get("jmh-result.json")));

        for (JsonNode benchmarkNode : rootNode) {
            String benchmarkName = benchmarkNode.get("benchmark").asText();
            JsonNode primaryMetricNode = benchmarkNode.get("primaryMetric");
            if (primaryMetricNode != null) {
                JsonNode rawDataNode = primaryMetricNode.get("rawData");
                if (rawDataNode != null && rawDataNode.isArray()) {
                    JsonNode iterationData = rawDataNode.get(0);
                    for (int i = 0; i < iterationData.size(); i++) {
                        double throughput = iterationData.get(i).asDouble();
                        String iterationLabel = "Iteration " + (i + 1);

                        if (benchmarkName.contains("benchmarkThreadSafeHashMap")) {
                            tsHashMapDataset.addValue(throughput, "ThreadSafeHashMap", iterationLabel);
                            combinedDataset.addValue(throughput, "ThreadSafeHashMap", iterationLabel);
                        } else if (benchmarkName.contains("benchmarkConcurrentHashMap")) {
                            concHashMapDataset.addValue(throughput, "ConcurrentHashMap", iterationLabel);
                            combinedDataset.addValue(throughput, "ConcurrentHashMap", iterationLabel);
                        }
                    }
                }
            }
        }

        // Create charts
        JFreeChart tsHashMapChart = ChartFactory.createBarChart(
                "ThreadSafeHashMap Benchmark",
                "Iterations",
                "Throughput (ops/ms)",
                tsHashMapDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // Customize the color of bars for ThreadSafeHashMap chart
        CategoryPlot tsPlot = tsHashMapChart.getCategoryPlot();
        BarRenderer tsRenderer = (BarRenderer) tsPlot.getRenderer();
        tsRenderer.setSeriesPaint(0, Color.BLUE);

        JFreeChart concHashMapChart = ChartFactory.createBarChart(
                "ConcurrentHashMap Benchmark",
                "Iterations",
                "Throughput (ops/ms)",
                concHashMapDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        JFreeChart combinedChart = ChartFactory.createBarChart(
                "Combined Benchmark Results",
                "Iterations",
                "Throughput (ops/ms)",
                combinedDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // Save charts as PNG files
        ChartUtils.saveChartAsPNG(new File("ThreadSafeHashMap_Benchmark.png"), tsHashMapChart, 800, 600);
        ChartUtils.saveChartAsPNG(new File("ConcurrentHashMap_Benchmark.png"), concHashMapChart, 800, 600);
        ChartUtils.saveChartAsPNG(new File("Combined_Benchmark.png"), combinedChart, 800, 600);

        // Display charts in a window
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.add(new ChartPanel(tsHashMapChart));
        panel.add(new ChartPanel(concHashMapChart));
        panel.add(new ChartPanel(combinedChart));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Benchmark Results");
        frame.setSize(800, 1200);
        frame.add(panel);
        frame.setVisible(true);
    }
}

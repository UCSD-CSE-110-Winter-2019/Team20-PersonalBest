package cse110.ucsd.team20_personalbest.graph;

import com.google.common.graph.Graph;

import java.util.ArrayList;

import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class MockGraphManager extends GraphManager {

    public MockGraphManager(ComboLineColumnChartView chartView, int numCols){
        super(chartView, numCols);
        chart = chartView;
        this.numCols = numCols;

        intendedSteps = new ArrayList<>();
        unintendedSteps = new ArrayList<>();
        formattedUSteps = new ArrayList<>();
        goalData = new ArrayList<>();

        if(numCols >= maxColNumberWithLabels){
            hasLabels = false;
        }
    }
}

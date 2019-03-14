package cse110.ucsd.team20_personalbest.graph;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cse110.ucsd.team20_personalbest.goal.GoalDataRequestManager;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class GraphManager implements GraphManagerInterface {

    public static final int USTEP_COLOR = Color.parseColor("#33B5E5"); // blue
    public static final int ISTEP_COLOR = Color.parseColor("#FFBB33"); // orange
    public static final int LINE_COLOR = Color.parseColor("#dd1616"); // red

    private ComboLineColumnChartView chart;
    private ComboLineColumnChartData data;

    private int numCols;
    private int maxColNumberWithLabels = 10;

    private boolean hasLabels = true;

    GoalDataRequestManager goalDataRequestManager;

    private ArrayList<Integer> intendedSteps;
    private ArrayList<Integer> unintendedSteps;
    private ArrayList<Integer> goalData;

    private ArrayList<Integer> formattedUSteps;

    String TAG = "GraphManager";

    public GraphManager(ComboLineColumnChartView chartView, int numCols){
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

    public void setGoalRequestManager(GoalDataRequestManager goalDataRequestManager){
        goalDataRequestManager = goalDataRequestManager;
        goalData = goalDataRequestManager.getGoalDataArray();
    }


    public void draw(){
        //use formatted arraylists
        //For stacked bar graph
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        //For goal line
        List<PointValue> line = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();

        // for proper framing of graph from y = 0
        ArrayList<PointValue> zeroLine = new ArrayList<>();
        zeroLine.add(new PointValue(0, 0));
        lines.add(new Line(zeroLine));

        // gets column data for each day
        for (int i = 0; i < numCols; ++i) {
            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(formattedUSteps.get(i), USTEP_COLOR));
            values.add(new SubcolumnValue(intendedSteps.get(i), ISTEP_COLOR));

            Column column = new Column(values);

            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(false);

            columns.add(column);
        }

        //Add goal data to the line
        for (int i = 0; i < numCols; i++) {
            if(goalData.size() == i) goalData.add(0);
            int goalValue = goalData.get(i);
            int j = i - 1;
            while(goalValue == 0 && j >= 0){
                goalValue = goalData.get(j);
                j--;
            }

            line.add(new PointValue(i, goalValue));
        }

        Line lineObj = new Line(line);
        lineObj.setColor(LINE_COLOR);
        lines.add(lineObj);

        ColumnChartData columnData = new ColumnChartData(columns);
        LineChartData lineData = new LineChartData(lines);

        data = new ComboLineColumnChartData(columnData, lineData);

        // Set stacked flag.
        columnData.setStacked(true);

        Axis axisX = new Axis().setName("Day of Week");
        Axis axisY = new Axis().setName("Number of Steps").setHasLines(true);

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setComboLineColumnChartData(data);
    }


    public void formatData(){
        for(int i = 0; i < numCols; i++){
            if(formattedUSteps.size() == i) formattedUSteps.add(0);
            if(unintendedSteps.size() == i) unintendedSteps.add(0);
            if(intendedSteps.size() == i) intendedSteps.add(0);
            if(goalData.size() == i) goalData.add(0);
        }

        for (int i = 0; i < numCols; i++) {
            int rawUsteps = unintendedSteps.get(i) -  intendedSteps.get(i);
            int unintendedSteps = rawUsteps > 0 ? rawUsteps : 0;
            formattedUSteps.add(i, unintendedSteps);
        }
    }

    public void setNumCols(int cols){
        numCols = cols;
    }

    public void updateIntendedData(ArrayList<Integer> newData){
        Log.d(TAG, "Updating Intended Steps Data");
        intendedSteps = newData;
        formatData();
    }

    public void updateUnintendedData(ArrayList<Integer> newData){
        Log.d(TAG, "Updating Unintended Steps Data");
        unintendedSteps = newData;
        formatData();
    }
}


package cse110.ucsd.team20_personalbest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

import static android.content.Context.MODE_PRIVATE;
import static cse110.ucsd.team20_personalbest.MainActivity.sdrm;

public class GraphFragment extends Fragment {

    // colors for the stacked chart
    public static final int USTEP_COLOR = Color.parseColor("#33B5E5"); // blue
    public static final int ISTEP_COLOR = Color.parseColor("#FFBB33"); // orange
    public static final int LINE_COLOR = Color.parseColor("#dd1616"); // red

    public static final String[] DAYS_OF_WEEK = new String[]{"Sun", "Mon", "Tues", "Wed",
            "Thurs", "Fri", "Sat"};
    public static final String[] DAYS_OF_WEEK_LONG = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};
    public static final String[] SUBCOLUMN = new String[]{"unintentional", "intentional"};

    private ComboLineColumnChartView chart;
    private ComboLineColumnChartData data;
    private SharedPreferences sharedPreferences;
    private ArrayList<Integer> lastWeeksGoals;


    public GraphFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(false);
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        chart = (ComboLineColumnChartView) rootView.findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        lastWeeksGoals = getWeeksGoals(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        generateData();


        return rootView;
    }


    // Generates the graph
    private void generateData() {

        ArrayList<Integer> walks = MainActivity.sdrm.getWeek();
        ArrayList<Integer> steps = MainActivity.dailysteps.getHistory();

        for(int i = 0; i < walks.size(); i++){
            steps.set(i, steps.get(i) - walks.get(i));
        }

        int numColumns = 7;

        List<Column> columns = new ArrayList<Column>();
        List<PointValue> line = new ArrayList<PointValue>();
        List<Line> lines = new ArrayList<Line>();
        List<SubcolumnValue> values;

        // gets goal line data for each day
        Log.d("Graph Data", lastWeeksGoals.toString());
        for (int i = 0; i < numColumns; i++) {

            line.add(new PointValue(i, lastWeeksGoals.get(i)));
        }
        Line lineObj = new Line(line);
        lineObj.setColor(LINE_COLOR);
        lines.add(lineObj);


        // gets column data for each day
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();

            // intended walk TODO add actual intended walk data
            values.add(new SubcolumnValue(steps.get(i), USTEP_COLOR));

            // unintended walk TODO add actual unintended walk data
            values.add(new SubcolumnValue(walks.get(i), ISTEP_COLOR));

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData columnData = new ColumnChartData(columns);
        LineChartData lineData = new LineChartData(lines);

        data = new ComboLineColumnChartData(columnData, lineData);

        // Set stacked flag.
        columnData.setStacked(true);

        // gets day of week values
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
            axisValues.add(new AxisValue(i).setLabel(DAYS_OF_WEEK[i]));
        }

        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);

        axisX.setName("Day of Week");
        axisY.setName("Number of Steps");

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setComboLineColumnChartData(data);
    }

    public ArrayList<Integer> getWeeksGoals(SharedPreferences sharedPreferences){
        ArrayList<Integer> data = new ArrayList<>();
        data.add(sharedPreferences.getInt("Sunday goal", 0));
        data.add(sharedPreferences.getInt("Monday goal", 0));
        data.add(sharedPreferences.getInt("Tuesday goal", 0));
        data.add(sharedPreferences.getInt("Wednesday goal", 0));
        data.add(sharedPreferences.getInt("Thursday goal", 0));
        data.add(sharedPreferences.getInt("Friday goal", 0));
        data.add(sharedPreferences.getInt("Saturday goal", 0));
        Log.d("Goal data", data.toString());
        return data;
    }

    private class ValueTouchListener implements ComboLineColumnChartOnValueSelectListener {

        @Override
        public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            String message = DAYS_OF_WEEK_LONG[columnIndex] + "'s " + SUBCOLUMN[subcolumnIndex] + " steps: " + Math.round(value.getValue());
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getActivity(), "Goal is " + Math.round(value.getY()) + " steps", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // does nothing
        }
    }


}

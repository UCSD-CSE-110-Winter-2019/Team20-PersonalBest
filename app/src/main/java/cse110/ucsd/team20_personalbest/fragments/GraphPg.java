package cse110.ucsd.team20_personalbest.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import cse110.ucsd.team20_personalbest.history.DailyStepCountHistory;
import cse110.ucsd.team20_personalbest.R;
import cse110.ucsd.team20_personalbest.walk.SessionDataRequestManager;
import cse110.ucsd.team20_personalbest.goal.GoalDataRequestManager;
import cse110.ucsd.team20_personalbest.graph.GraphManager;
import lecho.lib.hellocharts.listener.ComboLineColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

import static android.content.Context.MODE_PRIVATE;

public class GraphPg extends Fragment implements Observer, GraphPgInterface {

    static boolean gotUnintendedSteps;

    ComboLineColumnChartView chart;
    private ComboLineColumnChartData data;
    private SharedPreferences sharedPreferences;
    private ArrayList<Integer> lastWeeksGoals;

    private final String[] SUBCOLUMN = {"Unintended", "Intended"};

    private final String[] DAYS_OF_WEEK_LONG = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    ArrayList<Integer> intendedSteps;
    private ArrayList<Integer> unintendedSteps;

    DailyStepCountHistory dailyStepCountHistory;
    SessionDataRequestManager sessionDataRequestManager;
    GoalDataRequestManager goalDataRequestManager;

    GraphManager graphManager;

    int numCols = 7;
    int sessionsReturned = 0;

    public GraphPg() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        setHasOptionsMenu(false);
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        chart = (ComboLineColumnChartView) rootView.findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        graphManager = new GraphManager(chart, numCols);
        goalDataRequestManager = new GoalDataRequestManager(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        dailyStepCountHistory = new DailyStepCountHistory(this.getActivity(), GoogleSignIn.getLastSignedInAccount(this.getActivity().getBaseContext()));
        sessionDataRequestManager = new SessionDataRequestManager(this.getActivity(), GoogleSignIn.getLastSignedInAccount(this.getActivity().getBaseContext()));

        intendedSteps = new ArrayList<>();
        unintendedSteps = new ArrayList<>();

        setValues();

        return rootView;
    }


    @Override
    public void setValues() {

            this.goalDataRequestManager.requestGoals(Calendar.getInstance().getTimeInMillis(), numCols);

            this.graphManager.setGoalRequestManager(goalDataRequestManager);

            //lastWeeksGoals = getWeeksGoals(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
            //intendedSteps = getWeeksSteps(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

            this.dailyStepCountHistory.addObserver(this);
            this.sessionDataRequestManager.addObserver(this);

            this.dailyStepCountHistory.requestHistory(Calendar.getInstance().getTimeInMillis(), numCols);
            this.sessionDataRequestManager.requestSessions(Calendar.getInstance().getTimeInMillis(), numCols);
    }


    // Generates the graph
//    private void generateData(int numCols) {
//
//        ArrayList<Integer> steps = unintendedSteps;
//        ArrayList<Integer> uSteps = new ArrayList<>();
//
//        for(int i = 0; i < numCols; i++){
//            if(steps.size() == i) steps.add(0);
//            if(unintendedSteps.size() == i) unintendedSteps.add(0);
//            if(intendedSteps.size() == i) intendedSteps.add(0);
//        }
//
//        for (int i = 0; i < numCols; i++) {
//            //uSteps.add(i, steps.get(i) - walks.get(i));
//            int rawUsteps = steps.get(i) -  intendedSteps.get(i);
//            int unintendedSteps = rawUsteps > 0 ? rawUsteps : 0;
//            uSteps.add(i, unintendedSteps);
//        }
//
//        Log.d("Graph Unintended Steps", "Calculated unintended steps a single time.");
//
//        Log.d("Graph Unintended Steps", Arrays.toString(uSteps.toArray()));
//        //Log.d("Graph Intended Steps", Arrays.toString(walks.toArray()));
//        Log.d("Graph Intended Steps", Arrays.toString(intendedSteps.toArray()));
//        Log.d("Graph Total Steps", Arrays.toString(steps.toArray()));
//
//        int numColumns = numCols;
//
//        List<Column> columns = new ArrayList<Column>();
//        List<PointValue> line = new ArrayList<PointValue>();
//        List<Line> lines = new ArrayList<Line>();
//        List<SubcolumnValue> values;
//
//        Goal g = new Goal(this.getActivity());
//        int currentDay = g.getCurrentDay();
//        Log.d("Graph Goal", "Displaying saved goals until today, " + g.getCurrentDay() + " = " + DAYS_OF_WEEK_LONG[g.getCurrentDay()]);
//
//        // sets current day's goal as goal for future days
//        Integer goalValue = 0;
//        Integer defaultGoal = g.getGoal();
//
//        // gets goal data for each day
//        Log.d("Graph Data", lastWeeksGoals.toString());
//        for (int i = 0; i < numColumns; i++) {
//            goalValue = i <= currentDay ? lastWeeksGoals.get(i) : defaultGoal;
//            line.add(new PointValue(i, goalValue));
//        }
//        Line lineObj = new Line(line);
//        lineObj.setColor(LINE_COLOR);
//        lines.add(lineObj);
//
//        // for proper framing of graph from y = 0
//        ArrayList<PointValue> zeroLine = new ArrayList<>();
//        zeroLine.add(new PointValue(0, 0));
//        lines.add(new Line(zeroLine));
//
//
//        // gets column data for each day
//        for (int i = 0; i < numColumns; ++i) {
//
//            values = new ArrayList<SubcolumnValue>();
//            values.add(new SubcolumnValue(uSteps.get(i), USTEP_COLOR));
//            //values.add(new SubcolumnValue(walks.get(i), ISTEP_COLOR));
//            values.add(new SubcolumnValue(intendedSteps.get(i), ISTEP_COLOR));
//
//            Column column = new Column(values);
//            //TODO disable labels for the monthly summary when its done
//            column.setHasLabels(true);
//            column.setHasLabelsOnlyForSelected(false);
//            columns.add(column);
//        }
//
//        ColumnChartData columnData = new ColumnChartData(columns);
//        LineChartData lineData = new LineChartData(lines);
//
//        data = new ComboLineColumnChartData(columnData, lineData);
//
//        // Set stacked flag.
//        columnData.setStacked(true);
//
//        // gets day of week values
//        List<AxisValue> axisValues = new ArrayList<AxisValue>();
//        for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
//            axisValues.add(new AxisValue(i).setLabel(DAYS_OF_WEEK[i]));
//        }
//
//        Axis axisX = new Axis(axisValues);
//        Axis axisY = new Axis().setHasLines(true);
//
//        axisX.setName("Day of Week");
//        axisY.setName("Number of Steps");
//
//        data.setAxisXBottom(axisX);
//        data.setAxisYLeft(axisY);
//
//        chart.setComboLineColumnChartData(data);
//    }

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

//    public ArrayList<Long> getWeeksSteps(SharedPreferences sharedPreferences){
//        ArrayList<Long> data = new ArrayList<>();
//        data.add(sharedPreferences.getLong("Sunday walks", 0));
//        data.add(sharedPreferences.getLong("Monday walks", 0));
//        data.add(sharedPreferences.getLong("Tuesday walks", 0));
//        data.add(sharedPreferences.getLong("Wednesday walks", 0));
//        data.add(sharedPreferences.getLong("Thursday walks", 0));
//        data.add(sharedPreferences.getLong("Friday walks", 0));
//        data.add(sharedPreferences.getLong("Saturday walks", 0));
//        Log.d("Intended steps data", data.toString());
//        return data;
//    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("Graph", "Graph has been notified, updating graph");
        if(observable instanceof DailyStepCountHistory) {
            graphManager.updateUnintendedData((ArrayList<Integer>) o);
            graphManager.draw();

        }
        if(observable instanceof SessionDataRequestManager) {
            graphManager.updateIntendedData((ArrayList<Integer>) o);
            sessionsReturned++;
            if( sessionsReturned >= numCols){
                graphManager.draw();
            }
        }

    }

    private class ValueTouchListener implements ComboLineColumnChartOnValueSelectListener {

        @Override
        public void onColumnValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {

            String message = /*DAYS_OF_WEEK_LONG[columnIndex] + "'s " +*/ SUBCOLUMN[subcolumnIndex] + " Steps: " + Math.round(value.getValue());
            //if (numCols == 7)
            //    message = DAYS_OF_WEEK_LONG[columnIndex] + "'s " + message;
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPointValueSelected(int lineIndex, int pointIndex, PointValue value) {
            if (value.getY() != 0)
                Toast.makeText(getActivity(), "Goal is " + Math.round(value.getY()) + " steps", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // does nothing
        }
    }
}
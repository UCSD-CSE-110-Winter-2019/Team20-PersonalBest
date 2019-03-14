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

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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

public class GraphPg extends Fragment implements Observer {

    static boolean gotUnintendedSteps;

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

    private ArrayList<Integer> intendedSteps;
    private ArrayList<Integer> unintendedSteps;

    private DailyStepCountHistory dailyStepCountHistory;
    private SessionDataRequestManager sessionDataRequestManager;
    private GoalDataRequestManager goalDataRequestManager;

    private GraphManager graphManager;

    private int numCols = 7;
    private int sessionsReturned = 0;


    public GraphPg() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        setHasOptionsMenu(false);
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        chart = (ComboLineColumnChartView) rootView.findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        goalDataRequestManager = new GoalDataRequestManager(this.getActivity());
        goalDataRequestManager.requestGoals(Calendar.getInstance().getTimeInMillis(), numCols);

        graphManager = new GraphManager(chart, numCols);
        graphManager.setGoalRequestManager(goalDataRequestManager);

        //lastWeeksGoals = getWeeksGoals(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        //intendedSteps = getWeeksSteps(this.getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        intendedSteps = new ArrayList<>();
        unintendedSteps = new ArrayList<>();

        dailyStepCountHistory = new DailyStepCountHistory(this.getActivity(), GoogleSignIn.getLastSignedInAccount(this.getActivity().getBaseContext()));
        sessionDataRequestManager = new SessionDataRequestManager(this.getActivity(), GoogleSignIn.getLastSignedInAccount(this.getActivity().getBaseContext()));


        dailyStepCountHistory.addObserver(this);
        sessionDataRequestManager.addObserver(this);

        dailyStepCountHistory.requestHistory(Calendar.getInstance().getTimeInMillis(), numCols);
        sessionDataRequestManager.requestSessions(Calendar.getInstance().getTimeInMillis(), numCols);

        return rootView;
    }

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
            if (numCols == 7)
                message = DAYS_OF_WEEK_LONG[columnIndex] + "'s " + message;
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

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

package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class HistoryToArrayConverter extends Observable implements Observer {

    private Activity activity;

    private DailyStepCountHistory dailyStepCountHistory;
    private SessionDataRequestManager sessionDataRequestManager;

    private ArrayList<Integer> intendedSteps;
    private ArrayList<Integer> unintendedSteps;

    private int numDays = 28;
    private int returnedSessions = 0;
    private long[] data;

    public HistoryToArrayConverter(Activity activity){
        this.activity = activity;
        dailyStepCountHistory = new DailyStepCountHistory(activity, GoogleSignIn.getLastSignedInAccount(activity));
        sessionDataRequestManager = new SessionDataRequestManager(activity, GoogleSignIn.getLastSignedInAccount(activity));

        intendedSteps = new ArrayList<>();
        unintendedSteps = new ArrayList<>();

        dailyStepCountHistory.addObserver(this);
        sessionDataRequestManager.addObserver(this);

        dailyStepCountHistory.requestHistory(Calendar.getInstance().getTimeInMillis(), numDays);
        sessionDataRequestManager.requestSessions(Calendar.getInstance().getTimeInMillis(), numDays);

        data = new long[2 */*types of steps*/ numDays + 1 /*For timestamp*/ + 1 /*For goal*/];
    }

    public void requestHistory(){
        dailyStepCountHistory.requestHistory(Calendar.getInstance().getTimeInMillis(), numDays);
        sessionDataRequestManager.requestSessions(Calendar.getInstance().getTimeInMillis(), numDays);
    }

    public HistoryStructure getData(){
        return new HistoryStructure().setData(data);
    }

    private void formatArray(){
        //Set first item to current time or date
        data[0] = Calendar.getInstance().getTimeInMillis();
        data[1] = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("savedGoal", 17);

        for(int i = 0; i < numDays; i++){
            if(unintendedSteps.size() == i) unintendedSteps.add(0);
            if(intendedSteps.size() == i) intendedSteps.add(0);

            data[i + 2] = unintendedSteps.get(i);
            data[numDays + i + 2] = intendedSteps.get(i);
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof DailyStepCountHistory){
            unintendedSteps = (ArrayList<Integer> ) o;
            formatArray();
            setChanged();
            notifyObservers("unintended");
        }
        if(observable instanceof  SessionDataRequestManager){
            intendedSteps = (ArrayList<Integer> ) o;
            returnedSessions++;
            if(returnedSessions >= numDays){
                formatArray();
                setChanged();
                notifyObservers("intended");
            }
        }
    }
}

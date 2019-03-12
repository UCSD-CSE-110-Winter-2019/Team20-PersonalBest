package cse110.ucsd.team20_personalbest;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class HistoryToArrayConverter extends Observable implements Observer {

    private DailyStepCountHistory dailyStepCountHistory;
    private SessionDataRequestManager sessionDataRequestManager;

    private ArrayList<Integer> intendedSteps;
    private ArrayList<Integer> unintendedSteps;

    private int numDays = 28;
    private int returnedSessions = 0;
    private long[] data;

    public HistoryToArrayConverter(Activity activity){
        dailyStepCountHistory = new DailyStepCountHistory(activity, GoogleSignIn.getLastSignedInAccount(activity.getBaseContext()));
        sessionDataRequestManager = new SessionDataRequestManager(activity, GoogleSignIn.getLastSignedInAccount(activity.getBaseContext()));

        intendedSteps = new ArrayList<>();
        unintendedSteps = new ArrayList<>();

        dailyStepCountHistory.addObserver(this);
        sessionDataRequestManager.addObserver(this);

        dailyStepCountHistory.requestHistory(Calendar.getInstance().getTimeInMillis(), numDays);
        sessionDataRequestManager.requestSessions(Calendar.getInstance().getTimeInMillis(), numDays);

        data = new long[2 * numDays + 1];
    }

    public long[] getData(){
        return data;
    }

    public void formatArray(){
        //Set first item to current time or date
        data[0] = Calendar.getInstance().getTimeInMillis();

        for(int i = 0; i < numDays; i++){
            data[i + 1] = unintendedSteps.get(i);
            data[numDays + i + 1] = intendedSteps.get(i);
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof DailyStepCountHistory){
            unintendedSteps = (ArrayList<Integer> ) o;
            formatArray();
        }
        if(observable instanceof  SessionDataRequestManager){
            intendedSteps = (ArrayList<Integer> ) o;
            returnedSessions++;
            if(returnedSessions >= numDays){
                formatArray();
                setChanged();
                notifyObservers();
            }
        }
    }
}

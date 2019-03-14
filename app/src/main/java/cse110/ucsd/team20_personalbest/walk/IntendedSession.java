package cse110.ucsd.team20_personalbest.walk;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class IntendedSession implements SessionInterface {

    private Session session;
    private GoogleSignInAccount google;
    private Activity activity;

    private long starting;
    private long ending;
    private long steps;
    private long startingSteps;
    private String TAG = "Intended Session";

    // instantiate a new session

    public IntendedSession(long startTime, Activity a, GoogleSignInAccount googleSignin, long startingSteps) {

        //create the data identifier for the session

        this.session = new Session.Builder()
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setActivity(FitnessActivities.WALKING)
                .build();

        Log.i(TAG, "New session built with id: " + session.getIdentifier());

        this.google = googleSignin;
        this.activity = a;
        this.starting = startTime;
        this.startingSteps = startingSteps;

        Task<Void> response = Fitness.getSessionsClient(activity, google)
                .startSession(session);

        Log.i(TAG, "Session (" + session.getIdentifier() + ") started at: " + startTime);

        return;
    }

    public boolean startSession(long startTime){
        return true;
    }

    public boolean endSession(long endTime) {

        ending = endTime;
        Task<List<Session>> response = Fitness.getSessionsClient(activity, google)
                .stopSession(session.getIdentifier());
        Log.i(TAG, "Session (" + session.getIdentifier() + ") ended at: " + endTime);
        return true;
    }

    public long getStartingSteps(){
        return startingSteps;
    }

    public Session getSession() {
        return session;
    }

    public long returnSteps(long endingSteps) {
        steps = endingSteps - startingSteps;
        return steps;
    }

} // end class
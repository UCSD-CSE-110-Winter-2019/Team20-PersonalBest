package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;

import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class IntendedSession implements SessionInterface{

    private String fitnessServiceKey = "GOOGLE_FIT";
    public static final String TAG = "BasicSessions";
    public static final String SESSION_NAME = "Intended Walk";
    public static final String SESSION_DESC = "Intended Walk description";
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private FitnessService fitnessService;

    private Session session;
    private GoogleSignInAccount google;
    private Activity activity;

    private long starting;
    private long ending;
    private long steps;
    private long startingSteps;

    // instantiate a new session

    public IntendedSession(long startTime, Activity a, GoogleSignInAccount googleSignin, long startingSteps) {

        //create the data identifier for the session

        this.session = new Session.Builder()
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setActivity(FitnessActivities.WALKING)
                .build();

        this.google = googleSignin;
        this.activity = a;
        this.starting = startTime;
        this.startingSteps = startingSteps;

        Task<Void> response = Fitness.getSessionsClient(activity, google)
                .startSession(session);
        return;
    }


    public boolean startSession(long startTime) {

       return true;
    }


    public boolean endSession(long endTime) {

        ending = endTime;

        Task<List<Session>> response = Fitness.getSessionsClient(activity, google)
                .stopSession(session.getIdentifier());

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
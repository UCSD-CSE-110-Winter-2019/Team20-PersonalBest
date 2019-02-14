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

    public Session getSession() {
        return session;
    }

    public long returnSteps(long endingSteps) {


        steps = endingSteps - startingSteps;
        return steps;

        //USE TO REQUEST SESSIONS - UNUSED IN THIS CLASS
//        SessionReadRequest readRequest = new SessionReadRequest.Builder()
//                .setTimeInterval(starting/2, ending + 1000, TimeUnit.MILLISECONDS)
//                .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .build();
//
//        Fitness.getSessionsClient(activity, google)
//                .readSession(readRequest)
//                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
//                    @Override
//                    public void onSuccess(SessionReadResponse sessionReadResponse) {
//                        // Get a list of the sessions that match the criteria to check the result.
//                        List<Session> sessions = sessionReadResponse.getSessions();
//
//                        System.out.println("Sessions available: " + sessions.size());
//
//                        for (Session session: sessions) {
//                            // Process the sessions
//
//                            // Process the data sets for this session
//                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session);
//                            for (DataSet dataSet : dataSets) {
//                                //System.out.println("Dataset contains: " + dataSet.getDataPoints().size() + " points");
//                                if(dataSet.isEmpty())continue;
//                                steps = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
//                                //System.out.println("steps: " + dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS));
//
//                            }
//
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("Failed to read session data");
//                        return;
//                    }
//                });

    }

} // end class
package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
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

    // instantiate a new session

    public IntendedSession(long startTime, Activity a, GoogleSignInAccount googleSignin) {

        //create the data identifier for the session

        this.session = new Session.Builder()
                .setName(SESSION_NAME)
                .setIdentifier("identifier")
                .setDescription(SESSION_DESC)
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .build();

        this.google = googleSignin;
        this.activity = a;
        this.starting = startTime;

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

        // now insert the session into the fitness history
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .build();

        // insert the session into the history
        Fitness.getSessionsClient(activity, google)
                .insertSession(insertRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // At this point, the session has been inserted and can be read.
                        //do nothing
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        return;
                    }
                });

        return true;
    }

    public Session getSession() {
        return session;
    }

    public long returnSteps() {
        // given our session, return the number of steps achieved during it

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(starting, ending, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_SPEED)
                .setSessionName("Placeholder")
                .build();

        Fitness.getSessionsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .readSession(readRequest)
                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                    @Override
                    public void onSuccess(SessionReadResponse sessionReadResponse) {
                        // Get a list of the sessions that match the criteria to check the result.
                        List<Session> sessions = sessionReadResponse.getSessions();

                        for (Session session : sessions) {
                            // Process the sessions

                            // Process the data sets for this session
                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session);
                            for (DataSet dataSet : dataSets) {
                                if (dataSet.getDataType() == TYPE_STEP_COUNT_DELTA) {
                                    steps = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        return;
                    }
                });

        return steps;
    }

} // end class
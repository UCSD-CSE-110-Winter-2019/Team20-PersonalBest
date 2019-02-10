
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

import cse110.ucsd.team20_personalbest.SessionInterface;
import cse110.ucsd.team20_personalbest.fitness.FitnessService;

import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class ModifiedSession implements SessionInterface {

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

    public ModifiedSession(long start, long end) {
        starting = start;
        ending = end;

        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName(SESSION_NAME)
                .setDescription(SESSION_DESC)
                .setStartTime(starting, TimeUnit.MILLISECONDS)
                .setEndTime(ending, TimeUnit.MILLISECONDS)
                .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .build();
    } //end constructor

    public long returnSteps() {
        // given our session, return the number of steps achieved during it

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(starting, ending, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        Fitness.getSessionsClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .readSession(readRequest)
                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                    @Override
                    public void onSuccess(SessionReadResponse sessionReadResponse) {
                        // Get a list of the sessions that match the criteria to check the result.
                        List<Session> sessions = sessionReadResponse.getSessions();

                        for (Session session: sessions) {
                            // Process the sessions

                            // Process the data sets for this session
                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session);
                            for (DataSet dataSet : dataSets) {
//                                if (dataSet.getDataType() == TYPE_STEP_COUNT_DELTA)
                                steps = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
//                                }
                            }

                            //steps = dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
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


    @Override
    public boolean startSession(long startTime) {
        return false;
    }

    @Override
    public boolean endSession(long endTime) {
        return false;
    }

    @Override
    public Session getSession() {
        return null;
    }
}


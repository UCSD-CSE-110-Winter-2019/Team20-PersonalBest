package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SessionDataRequestManager {

    private Activity activity;
    private GoogleSignInAccount googleSignIn;
    private int steps;

    public SessionDataRequestManager(Activity activity, GoogleSignInAccount googleSignIn){
        this.activity = activity;
        this.googleSignIn = googleSignIn;
    }

    public int requestTotalSessionStepData(long startTime, long endTime){

        if(startTime < 1001) return -1;

        steps = 0;

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime - 1000, endTime + 1000, TimeUnit.MILLISECONDS)
                .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .build();

        Fitness.getSessionsClient(activity, googleSignIn)
                .readSession(readRequest)
                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                    @Override
                    public void onSuccess(SessionReadResponse sessionReadResponse) {
                        // Get a list of the sessions that match the criteria to check the result.
                        List<Session> sessions = sessionReadResponse.getSessions();

                        // Process the sessions
                        for (Session session: sessions) {

                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session);

                            // Process the data sets for this session
                            for (DataSet dataSet : dataSets) {
                                if(dataSet.isEmpty()) continue;

                                for(DataPoint dataPoint : dataSet.getDataPoints()){
                                    steps += dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed to read session data");
                        return;
                    }
                });
        return steps;
    }
}

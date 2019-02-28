package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class SessionDataRequestManager extends Observable {

    private Activity activity;
    private GoogleSignInAccount googleSignIn;
    private int index = 0;
    private int size;
    private ArrayList<Integer> sessions;
    private final String TAG = "SessionRequests";


    public SessionDataRequestManager(Activity activity, GoogleSignInAccount googleSignIn){
        this.activity = activity;
        this.googleSignIn = googleSignIn;
    }

    public ArrayList<Integer> getSessions(){
        return sessions;
    }

    public void requestSessions(long startTime, int numOfDays){

        sessions = new ArrayList<>(size);
        this.size = size;

        Calendar start = Calendar.getInstance();
        start.setTime(new Date(startTime));
        start.add(Calendar.DATE, -1 * numOfDays);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        for(int i = 0; i < size; i++) sessions.add(0);

        for(int i = 0; i < size; i++) {
            long startRequest = start.getTimeInMillis();
            Log.d(TAG, "Dates:" + start.toString());
            start.add(Calendar.DATE, 1);
            requestTotalSessionStepData(startRequest, start.getTimeInMillis(), i);
        }

        setChanged();
        notifyObservers(sessions);

    }

    private void requestTotalSessionStepData(long starttime, long endtime, final int i){
        if(starttime < 1001) return;

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(starttime - 1000, endtime + 1000, TimeUnit.MILLISECONDS)
                .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .enableServerQueries()
                .build();

        Task<SessionReadResponse> task = Fitness.getSessionsClient(activity, googleSignIn)
                .readSession(readRequest)
                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                    @Override
                    public void onSuccess(SessionReadResponse sessionReadResponse) {
                        // Get a list of the sessions that match the criteria to check the result.

                        int current = 0;

                        List<Session> sessions = sessionReadResponse.getSessions();

                        // Process the sessions
                        for (Session session : sessions) {
                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session);

                            // Process the data sets for this session
                            for (DataSet dataSet : dataSets) {
                                if (dataSet.isEmpty()) continue;

                                Log.d(TAG, "New dataset");

                                for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                    current += dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                    Log.d(TAG, "" + dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt());
                                }
                            }
                        }
                        Log.d(TAG, "Steps from one request:" + current);
                        SessionDataRequestManager.this.sessions.set(i, current);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Failed to read session data");
                        sessions.set(i, 0);
                    }
                });

    }

}

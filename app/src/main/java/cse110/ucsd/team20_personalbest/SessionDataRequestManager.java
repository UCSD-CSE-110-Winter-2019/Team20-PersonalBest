package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.support.annotation.NonNull;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SessionDataRequestManager {

    private Activity activity;
    private GoogleSignInAccount googleSignIn;
    private int index = 0;
    private int size;
    private ArrayList<Integer> week;


    public SessionDataRequestManager(Activity activity, GoogleSignInAccount googleSignIn, int size, long startTime){
        this.activity = activity;
        this.googleSignIn = googleSignIn;
        week = new ArrayList<>(size);
        for(int i = 0; i < size; i++) week.add(0);
        this.size = size;
        requestLastWeek(startTime);
    }

    public ArrayList<Integer> getWeek(){
        return week;
    }

    private void requestLastWeek(long startTime){

        System.out.println("+++please lord " + size);

        Calendar start = Calendar.getInstance();
        start.setTime(new Date(startTime));
        start.add( Calendar.DAY_OF_WEEK, -(start.get(Calendar.DAY_OF_WEEK)-1));
        start.set(Calendar.HOUR, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);


        for(int i = 0; i < size; i++) {
            long startRequest = start.getTimeInMillis();
            System.out.println("+++" + start.toString());
            start.add(Calendar.DATE, 1);
            requestTotalSessionStepData(startRequest, start.getTimeInMillis(), i);
        }

    }

    private void requestTotalSessionStepData(long starttime, long endtime, final int i){
        if(starttime < 1001) return;

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(starttime - 1000, endtime + 1000, TimeUnit.MILLISECONDS)
                .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .enableServerQueries()
                .build();

        Fitness.getSessionsClient(activity, googleSignIn)
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

                                for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                    current += dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                }
                            }
                        }
                        //System.out.println("+++Steps from one request:" + current);
                        week.set(i, current);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("+++Failed to read session data");
                        week.set(i, 0);
                    }
                });

    }

}

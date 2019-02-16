package cse110.ucsd.team20_personalbest;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DailyStepCountHistory {

    private ArrayList<Integer> data;
    private Activity activity;
    private GoogleSignInAccount googleSignIn;


    public DailyStepCountHistory(Activity activity, GoogleSignInAccount googleSignIn){
        this.activity = activity;
        this.googleSignIn = googleSignIn;
    }

    public ArrayList<Integer> getHistory(){
        return data;
    }

    private void format(long startTime){
        Calendar start = Calendar.getInstance();
        start.setTime(new Date(startTime));
        start.add( Calendar.DAY_OF_WEEK, -(start.get(Calendar.DAY_OF_WEEK)-1));
        start.set(Calendar.HOUR, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        long startOfWeek = start.getTimeInMillis();
        start.add(Calendar.DATE, 7);
        long endOfWeek = start.getTimeInMillis();

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startOfWeek, endOfWeek , TimeUnit.MILLISECONDS)
                        .build();

        Task<DataReadResponse> response = Fitness.getHistoryClient(activity, googleSignIn).readData(readRequest).addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {

                //Should be each day in the request
                for(Bucket bucket : dataReadResponse.getBuckets()){

                    List<DataSet> dataSets = bucket.getDataSets();
                    for(DataSet dataSet : dataSets) {
                        List<DataPoint> dataPoints = dataSet.getDataPoints();
                        for (DataPoint dataPoint : dataPoints) {
                            data.add(dataPoint.getValue(Field.FIELD_STEPS).asInt());
                            System.out.println("+++ Daily step count: " + dataPoint.getValue(Field.FIELD_STEPS).asString());
                        }
                    }
                }
            }
        });
    }
}

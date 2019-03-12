package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class DailyStepCountHistory extends Observable {

    private Activity activity;
    private GoogleSignInAccount googleSignIn;
    private ArrayList<Integer> data;
    private final String TAG = "StepHistory";

    public DailyStepCountHistory(Activity activity, GoogleSignInAccount googleSignIn){
        this.activity = activity;
        this.googleSignIn = googleSignIn;
        data = new ArrayList<>();
    }

    public void requestHistory( long startTime, int numOfDays){
        format(startTime, numOfDays);
    }

    public ArrayList<Integer> getHistory(){
        return data;
    }

    private void format(long startTime, int numOfDays){
        Calendar start = Calendar.getInstance();
        start.setTime(new Date(startTime));
        start.set(Calendar.HOUR_OF_DAY, 24);
        start.set(Calendar.MINUTE, 59);
        start.set(Calendar.SECOND, 59);
        start.set(Calendar.MILLISECOND, 0);
        long endOfWeek = start.getTimeInMillis();
        start.add(Calendar.DAY_OF_WEEK, -1 * numOfDays);
        long startOfWeek = start.getTimeInMillis();

        Log.d(TAG, "Requesting history from " + startOfWeek + " to " + endOfWeek);

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startOfWeek, endOfWeek , TimeUnit.MILLISECONDS)
                        .build();

        Log.d(TAG, "Read request has been built");

        final DailyStepCountHistory t = this;

        Task<DataReadResponse> response = Fitness.getHistoryClient(activity, googleSignIn).readData(readRequest).addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
            @Override
            public void onSuccess(DataReadResponse dataReadResponse) {

                Log.d(TAG, dataReadResponse.getBuckets().size() + " buckets received");

                //Should be each day in the request
                for(Bucket bucket : dataReadResponse.getBuckets()){

                    List<DataSet> dataSets = bucket.getDataSets();

                    for(DataSet dataSet : dataSets) {
                        List<DataPoint> dataPoints = dataSet.getDataPoints();
                        int steps = 0;
                        for (DataPoint dataPoint : dataPoints) {
                            steps = dataPoint.getValue(Field.FIELD_STEPS).asInt();
                            long durationInMillis = dataPoint.getStartTime(TimeUnit.MILLISECONDS);

                            Calendar time = Calendar.getInstance();
                            time.setTimeInMillis(durationInMillis);

                            Log.d(TAG, "Steps: " + steps  + " from " + time.toString());
                        }
                        data.add(steps);
                    }
                }
                t.setChanged();
                t.notifyObservers(data);
                Log.d(TAG, "Observers notified with " + data.toString() + " : " + t.countObservers());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to read step history");
            }
        });
    }

}

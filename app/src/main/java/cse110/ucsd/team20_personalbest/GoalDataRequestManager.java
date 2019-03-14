package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class GoalDataRequestManager {

    private Activity activity;
    private ArrayList<Integer> goalDataArray;
    private SharedPreferences sharedPreferences;

    private final String TAG = "GoalRequest";
    private int size;

    private String userEmail;

    public GoalDataRequestManager(){

    }

    public GoalDataRequestManager(Activity activity){
        this.activity = activity;
        this.sharedPreferences = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public ArrayList<Integer> getGoalDataArray() { return goalDataArray; }

    public void requestGoals(long startTime, int numOfDays) {

        this.size = numOfDays;
        goalDataArray = new ArrayList<>(size);

        Calendar start = Calendar.getInstance();
        start.setTime(new Date(startTime));
        start.add(Calendar.DATE, -1 * numOfDays + 1);

        for(int i = 0; i < size; i++) goalDataArray.add(0);

        Log.d(TAG, "Getting updated goal data for graph.");

        for(int i = 0; i < size; i++) {
            String key = "goal" + start.get(Calendar.YEAR) + start.get(Calendar.DAY_OF_YEAR);
            int goal = sharedPreferences.getInt(key, 0);

            Log.d(TAG, "Goal for " + (start.get(Calendar.MONTH) + 1) + "/" + start.get(Calendar.DAY_OF_MONTH) + ": " + goal);
            goalDataArray.set(i, goal);
            start.add(Calendar.DATE, 1);
        }
    }
}

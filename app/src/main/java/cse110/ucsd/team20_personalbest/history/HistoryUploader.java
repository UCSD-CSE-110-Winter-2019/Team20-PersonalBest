package cse110.ucsd.team20_personalbest.history;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

import cse110.ucsd.team20_personalbest.history.HistoryStructure;
import cse110.ucsd.team20_personalbest.history.HistoryToArrayConverter;

public class HistoryUploader implements Observer {

    public static boolean isRunning = false;
    private Activity activity;
    private HistoryToArrayConverter historyToArrayConverter;

    private boolean receivedIntended = false;
    private boolean receivedUnintended = false;

    private String TAG = "HistoryUploader";

    public HistoryUploader(Activity activity) {
        this.activity = activity;
        Log.d(TAG, "Starting the history requests");
        if(activity == null) return;
        historyToArrayConverter = new HistoryToArrayConverter(activity);
        historyToArrayConverter.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if(o.equals("intended")){
            receivedIntended = true;
            Log.d(TAG, "Intended steps received");
        }
        if(o.equals("unintended")){
            receivedUnintended = true;
            Log.d(TAG, "Unintended steps received");
        }

        if(receivedIntended && receivedUnintended){

            Log.d(TAG, "Attempting to upload data...");

            //UPLOAD HERE
            HistoryStructure data = historyToArrayConverter.getData();

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
            final String email = account.getEmail();
            if (email == null){
                Log.d(TAG, "Email was null");
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference history = db.collection("users")
                    .document(email)
                    .collection("history")
                    .document("data");


            history.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Upload successful, with email " + email);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Upload failed..., with email " + email);
                }
            });
        }

    }

}

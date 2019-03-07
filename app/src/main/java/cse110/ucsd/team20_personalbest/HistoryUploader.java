package cse110.ucsd.team20_personalbest;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

public class HistoryUploader implements Observer {

    public static boolean isRunning = false;
    private Activity activity;
    private HistoryToArrayConverter historyToArrayConverter;

    private boolean receivedIntended = false;
    private boolean receivedUnintended = false;

    private String TAG = "HistoryUploader";

    public HistoryUploader(Activity activity) {
        this.activity = activity;
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

            //TODO configure somewhere to request email from googlesigninoptions

            //TODO uncomment to upload data to firebase, use proper email

//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            DocumentReference history = db.collection("users")
//                    .document("dominic.simone@gmail.com")
//                    .collection("history")
//                    .document("data");
//
//
//            history.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "Upload successful");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d(TAG, "Upload failed...");
//                }
//            });
        }

    }

}

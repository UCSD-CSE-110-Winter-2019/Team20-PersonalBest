package cse110.ucsd.team20_personalbest;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Observable;

public class HistoryDownloader extends Observable {
    private String email;
    private String TAG = "HistoryDownloader";
    private ArrayList<Long> history;

    public HistoryDownloader(String email){

    }

    public void requestData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference history = db.collection("users")
                .document(email)
                .collection("history")
                .document("data");

        history.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Log.d(TAG, "History data does not exist");
                }else{
                    ArrayList<Long> history = (ArrayList<Long>) documentSnapshot.getData().get("data");
                    setChanged();
                    notifyObservers(history);
                }
            }
        });
    }
}

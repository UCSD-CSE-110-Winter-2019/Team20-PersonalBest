package cse110.ucsd.team20_personalbest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FBCommandCenter {
    final String USER_KEY = "users";
    final String FIRST_NAME_KEY = "firstName";
    final String LAST_NAME_KEY = "lastName";
    CollectionReference usersCollection;

    public FBCommandCenter() {
        usersCollection = FirebaseFirestore.getInstance().collection(USER_KEY);
    }

    void addUser(String userToAdd, final String fName, String lName) {

        Map<String, Object> dataToSave = new HashMap<String, Object>();
        dataToSave.put("firstName", fName);
        dataToSave.put("lastName", lName);
        usersCollection.document(userToAdd).set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUCCESSADDUSER", "User " + fName + " added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("FAILADDUSER", "User " + fName + " removed");
            }
        });

    }


    void userExists(String userToLookup) {

        DocumentReference docRef = FirebaseFirestore.getInstance().collection(USER_KEY).document(userToLookup);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }

        });


    }

}

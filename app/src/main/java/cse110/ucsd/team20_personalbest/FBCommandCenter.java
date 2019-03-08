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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cse110.ucsd.team20_personalbest.friends.FriendsContent;

public class FBCommandCenter {
    final String USER_KEY = "users";
    final String FIRST_NAME_KEY = "firstName";
    final String LAST_NAME_KEY = "lastName";
    CollectionReference usersCollection;
    DocumentReference user;
    FriendsContent fc;
    private Map<String, Object> dataToSave;

    public FBCommandCenter(String userToAdd, final String fName, String lName) {
        usersCollection = FirebaseFirestore.getInstance().collection(USER_KEY);

        dataToSave = new HashMap<String, Object>();
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
        user = usersCollection.document(userToAdd);
    }

    public void addFriend(String friendEmail) {
        fc.addItem(new FriendsContent.Friend(friendEmail));
        dataToSave.put("friends", fc.FRIENDS);
        user.set(dataToSave);
    }


    public boolean userExists(String userToLookup) {

        DocumentReference docRef = usersCollection.document(userToLookup);
        Task<DocumentSnapshot> ds = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }
        });
        return true;
    }

}

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

        usersCollection.document(userToAdd).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    dataToSave = documentSnapshot.getData();
                    for(int i = 0; i < ((List)documentSnapshot.getData().get("friends")).size(); i++) {
                        String friendEntry = ((List)documentSnapshot.getData().get("friends")).get(i).toString();
                        fc.addFriend(friendEntry.substring(6,friendEntry.length() - 1));
                    }
                }
                else {
                    dataToSave = new HashMap<String, Object>();
                    dataToSave.put("firstName", fName);
                    dataToSave.put("lastName", lName);
                    usersCollection.document(userToAdd).set(dataToSave);
                }
            }
        });

        //fc.FRIENDS = (List) dataToSave.get("friends");
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

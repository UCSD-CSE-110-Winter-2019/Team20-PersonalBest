package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
    private String friendEntry;
    String userEmail;
    MainActivity activity;

    public FBCommandCenter(String userToAdd, final String fName, String lName, MainActivity activity) {
        usersCollection = FirebaseFirestore.getInstance().collection(USER_KEY);
        this.activity = activity;
        userEmail = userToAdd;
        usersCollection.document(userToAdd).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    dataToSave = documentSnapshot.getData();
                    for(int i = 0; i < ((List)documentSnapshot.getData().get("friends")).size(); i++) {
                        friendEntry = ((List)documentSnapshot.getData().get("friends")).get(i).toString();
                        fc.addFriend(friendEntry);
                    }
                }
                else {
                    dataToSave = new HashMap<String, Object>();
                    dataToSave.put("firstName", fName);
                    dataToSave.put("lastName", lName);
                    dataToSave.put("friends", fc.FRIENDS);
                    dataToSave.put("friends_tobeadded", fc.FRIENDS_TOBEADDED);
                    usersCollection.document(userToAdd).set(dataToSave);
                }
            }
        });

        user = usersCollection.document(userToAdd);
    }

    public void updateFriends() {
        user.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                dataToSave = documentSnapshot.getData();
                for(int i = 0; i < ((List)documentSnapshot.getData().get("friends")).size(); i++) {
                    friendEntry = ((List)documentSnapshot.getData().get("friends")).get(i).toString();
                    fc.addFriend(friendEntry);
                }
            }
        });
    }

    public void addFriend(String friendEmail) {
            user.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    DocumentReference friendDocument = FirebaseFirestore.getInstance().collection(USER_KEY).document(friendEmail);
                    List ft = (List) (documentSnapshot.getData().get("friends"));
                    List ftba = (List) (documentSnapshot.getData().get("friends_tobeadded"));
                    friendDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()) {
                                if(ft.contains(friendEmail)) {
                                    activity.sendToast(friendEmail + " is ALREADY your friend!");
                                    return;
                                }
                                activity.sendToast("Friend Request sent to" + friendEmail);
                            }
                            else {
                                activity.sendToast(friendEmail + " does not use Personal Best !!!");
                                return;
                            }
                        }
                    });
                    if (ftba.contains(friendEmail)) {
                        ft.add(friendEmail);
                        user.update("friends", ft);
                        friendDocument.get().addOnSuccessListener(documentSnapshot12 -> {
                            List ft1 = (List)(documentSnapshot12.getData().get("friends"));
                            ft1.add(userEmail);
                            friendDocument.update("friends", ft1);
                        });
                        ftba.remove(friendEmail);
                        user.update("friends_tobeadded", ftba);
                    }
                    else {
                        friendDocument.get().addOnSuccessListener(documentSnapshot1 -> {
                            if (documentSnapshot1.exists()) {
                                List ftba1 = (List)(documentSnapshot1.getData().get("friends_tobeadded"));
                                if(ftba1.contains(userEmail))
                                    return;
                                ftba1.add(userEmail);
                                friendDocument.update("friends_tobeadded", ftba1);
                            }
                        });
                    }
                }
            });
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

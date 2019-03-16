package cse110.ucsd.team20_personalbest.friends;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cse110.ucsd.team20_personalbest.MainActivity;

public class MockFBCommandCenter implements FirebaseCommandCenterInterface {

    final String USER_KEY = "users";
    final String FIRST_NAME_KEY = "firstName";
    final String LAST_NAME_KEY = "lastName";

    CollectionReference usersCollection;
    DocumentReference user;
    FriendsContent fc;
    ArrayList<String> dataToSave;
    private String friendEntry;
    String userEmail;
    MainActivity activity;

    public MockFBCommandCenter(CollectionReference usersCollection, DocumentReference user, FriendsContent fc) {
        this.usersCollection = usersCollection;
        this.user = user;
        this.fc = fc;
    }

    public void setDataToSave(ArrayList<String> dataToSave) {
        this.dataToSave = dataToSave;
    }

    @Override
    public void updateFriends() {
        for (String friend : dataToSave) {
            fc.addFriend(friend);
        }
    }

    @Override
    public void addFriend(String friendEmail) {
        fc.addFriend(friendEmail);
    }

    @Override
    public boolean userExists(String userToLookup) {
        for (FriendsContent.Friend user : fc.FRIENDS) {
            if (user.toString().equals(userToLookup)) return true;
        }
        return false;
    }
}

package cse110.ucsd.team20_personalbest.ms2tests;


import android.app.Activity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cse110.ucsd.team20_personalbest.MainActivity;
import cse110.ucsd.team20_personalbest.friends.FBCommandCenter;
import cse110.ucsd.team20_personalbest.friends.FirebaseCommandCenterInterface;
import cse110.ucsd.team20_personalbest.friends.FriendsContent;
import cse110.ucsd.team20_personalbest.friends.MockFBCommandCenter;

import static cse110.ucsd.team20_personalbest.MainActivity.fbcc;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class FBCommandCenterTest {
    boolean flag;
    String a = "example1@example.com";
    String b = "example2@example.com";
    String c = "example3@example.com";
    String fName = "FirstExample";
    String lName = "LastExample";

    MockFBCommandCenter fbcc;
    CollectionReference usersCollection;
    DocumentReference user;
    FriendsContent fc;
    Activity activity;

    @Before
    public void setup() {
        //activity = Robolectric.setupActivity(MainActivity.class);
        //FirebaseApp.initializeApp(activity);

        usersCollection = mock(CollectionReference.class);
        user = mock(DocumentReference.class);
        fc = new FriendsContent();

        fbcc = new MockFBCommandCenter(usersCollection, user, fc);
    }

    @Test
    public void testAddFriends() {

        fbcc.addFriend(a);

        assertTrue(fc.FRIENDS.size() == 1);
        assertTrue(fc.FRIENDS.get(0).toString().equals(a));

        fbcc.addFriend(b);

        assertTrue(fc.FRIENDS.size() == 2);
        assertTrue(fc.FRIENDS.get(1).toString().equals(b));

        ArrayList<String> dataToSave = new ArrayList<>();
        dataToSave.add(a);
        dataToSave.add(b);
        dataToSave.add(c);

        fbcc.setDataToSave(dataToSave);
        fbcc.updateFriends();

        assertTrue(fc.FRIENDS.size() == 3);
        assertTrue(fc.FRIENDS.get(2).toString().equals(c));
    }

    @Test
    public void testUserExists() {

        //collectionReference = FirebaseFirestore.getInstance().collection("users");

        fbcc.addFriend(a);
        fbcc.addFriend(b);
        fbcc.addFriend(c);
        assertTrue(fbcc.userExists(a));
        assertTrue(fbcc.userExists(b));
        assertFalse(fbcc.userExists("doesNotExist"));
    }

}

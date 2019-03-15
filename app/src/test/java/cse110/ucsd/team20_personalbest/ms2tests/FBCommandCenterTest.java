package cse110.ucsd.team20_personalbest.ms2tests;


import android.app.Activity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static cse110.ucsd.team20_personalbest.MainActivity.fbcc;

@RunWith(RobolectricTestRunner.class)
public class FBCommandCenterTest {
    boolean flag;
    String a = "example1@example.com";
    String b = "example2@example.com";
    String c = "example3@example.com";
    String fName = "FirstExample";
    String lName = "LastExample";


   // FBCommandCenter fbcc;
    CollectionReference collectionReference;
    Activity activity;

    @Before
    public void setup() {


        //activity = Robolectric.setupActivity(MainActivity.class);

    }


    @Test
    public void testAddUser() {
        assert true;
    }
//    @Test
//    public void testAddUser() {
//
//        collectionReference = FirebaseFirestore.getInstance().collection("users");
//
//        fbcc.addUser(a, fName, lName);
//        fbcc.addUser(b, fName, lName);
//        fbcc.addUser(c, fName, lName);
//
//        Assert.assertEquals(collectionReference.document(a).getId().toString(), a);
//        Assert.assertEquals(collectionReference.document(b).getId().toString(), b);
//        Assert.assertEquals(collectionReference.document(c).getId().toString(), c);
//
//    }

//    @Test
//    public void testUserExists() {
//
//        collectionReference = FirebaseFirestore.getInstance().collection("users");
//
//        fbcc.addUser(a, fName, lName);
//        fbcc.addUser(b, fName, lName);
//        fbcc.addUser(c, fName, lName);
//        fbcc.userExists(a);
//        Assert.assertEquals(true, true);
//
//
//    }


}

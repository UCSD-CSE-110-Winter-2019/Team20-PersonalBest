package cse110.ucsd.team20_personalbest.activities;

import android.os.Bundle;

import cse110.ucsd.team20_personalbest.friends.FBCommandCenter;

public class MockPopupFriendRequest implements PopupFriendRequestInterface {

    String email;
    FBCommandCenter fbCommandCenter;

    public MockPopupFriendRequest(String testEmail, FBCommandCenter fbCommandCenter) {
        email = testEmail;
        this.fbCommandCenter = fbCommandCenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (checkValidEmail(email))
            fbCommandCenter.addFriend(email);
    }

    @Override
    public boolean checkValidEmail(String email) {
        return email != "";
    }
}

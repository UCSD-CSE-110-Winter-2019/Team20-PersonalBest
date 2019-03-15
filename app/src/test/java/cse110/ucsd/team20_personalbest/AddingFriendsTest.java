package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import cse110.ucsd.team20_personalbest.activities.MockPopupFriendRequest;
import cse110.ucsd.team20_personalbest.activities.PopupFriendRequest;
import cse110.ucsd.team20_personalbest.activities.PopupFriendRequestInterface;
import cse110.ucsd.team20_personalbest.friends.FBCommandCenter;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class AddingFriendsTest {

    FBCommandCenter fbCommandCenter;
    PopupFriendRequestInterface popupFriendRequest;
    String email;

    @Before
    public void setUp() throws Exception {
        fbCommandCenter = mock(FBCommandCenter.class);
        email = "Name";
        popupFriendRequest = new MockPopupFriendRequest(email, fbCommandCenter);
    }


    @Test
    public void test_addFriend() {
        popupFriendRequest.onCreate(mock(Bundle.class));
        verify(fbCommandCenter).addFriend(any());
    }
}

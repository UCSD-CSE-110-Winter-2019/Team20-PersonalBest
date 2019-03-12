package cse110.ucsd.team20_personalbest;

import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;

public class MockChatAdapter implements ChatAdapter {

    CollectionReference chat;

    public MockChatAdapter(String from){
    }

    @Override
    public void sendMessage(String message, EditText editText) {

    }

    @Override
    public void instantiate() {

    }

    @Override
    public void initMessageUpdateListener(TextView chatView) {

    }

    @Override
    public void subscribeToNotificationsTopic(ChatActivity activity) {
        System.out.print("Subscribed");
    }
}

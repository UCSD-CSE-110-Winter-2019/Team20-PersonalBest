package cse110.ucsd.team20_personalbest.chat;

import android.widget.EditText;
import android.widget.TextView;

import cse110.ucsd.team20_personalbest.activities.ChatActivity;

public interface ChatAdapter {

    public void sendMessage(String message, EditText editText);
    public void instantiate();
    public void initMessageUpdateListener(TextView chatView);
    public void subscribeToNotificationsTopic(ChatActivity activity);

}

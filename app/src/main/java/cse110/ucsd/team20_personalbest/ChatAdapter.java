package cse110.ucsd.team20_personalbest;

import android.widget.EditText;
import android.widget.TextView;

public interface ChatAdapter {

    public void sendMessage(String message, EditText editText);
    public void instantiate();
    public void initMessageUpdateListener(TextView chatView);
    public void subscribeToNotificationsTopic(ChatActivity activity);

}

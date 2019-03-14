package cse110.ucsd.team20_personalbest;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;

public class MockChatAdapter implements ChatAdapter {
    public String COLLECTION_KEY;
    public String DOCUMENT_KEY;
    public String MESSAGES_KEY;
    public String from;

    private String TAG = "MockChatAdapter";

    public MockChatAdapter(String from, String collectionKey, String documentKey, String messageKey){
        this.COLLECTION_KEY = collectionKey;
        this.DOCUMENT_KEY = documentKey;
        this.MESSAGES_KEY = messageKey;
        this.from = from;
    }

    @Override
    public void sendMessage(String message, EditText editText) {
        Log.d(TAG, "----------------Message "+message+" Sent------------------");
    }

    @Override
    public void instantiate() {
        Log.d(TAG, "----------------Instantiated------------------");
        Log.d(TAG, "----------------Current Firebase Tree------------------");
        Log.d(TAG, "--"+COLLECTION_KEY+"-->"+DOCUMENT_KEY+"-->"+MESSAGES_KEY+"--------------");
    }

    @Override
    public void initMessageUpdateListener(TextView chatView) {
        Log.d(TAG, "----------------Message Update Listener Initiated------------------");
    }

    @Override
    public void subscribeToNotificationsTopic(ChatActivity activity) {
        Log.d(TAG, "----------------Subscribed to "+DOCUMENT_KEY+"------------------");
    }
}

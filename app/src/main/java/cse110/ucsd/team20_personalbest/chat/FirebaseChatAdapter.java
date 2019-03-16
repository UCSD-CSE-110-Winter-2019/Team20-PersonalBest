package cse110.ucsd.team20_personalbest.chat;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cse110.ucsd.team20_personalbest.activities.ChatActivity;

import static android.content.ContentValues.TAG;

public class FirebaseChatAdapter implements ChatAdapter {
    CollectionReference chat;

    String COLLECTION_KEY;
    String DOCUMENT_KEY;
    String MESSAGES_KEY;
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";
    String from;

    public FirebaseChatAdapter(String from, String collectionKey, String documentKey, String messageKey){
        this.COLLECTION_KEY = collectionKey;
        this.DOCUMENT_KEY = documentKey;
        this.MESSAGES_KEY = messageKey;
        this.from = from;
    }

    public void instantiate(){
        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGES_KEY);
    }

    public void sendMessage(String message, EditText text) {

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, from);
        newMessage.put(TEXT_KEY, message);

        chat.add(newMessage).addOnSuccessListener(result -> {
            text.setText("");
            Log.d(TAG, "Message successfully sent");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    public void subscribeToNotificationsTopic(ChatActivity activity) {
        FirebaseMessaging.getInstance().subscribeToTopic(DOCUMENT_KEY)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG, msg);
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                        }
                );
    }

    public void initMessageUpdateListener(TextView chatView) {
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING).addSnapshotListener((newChatSnapShot, error) -> {
            if (error != null) {
                Log.e(TAG, error.getLocalizedMessage());
                return;
            }

            Log.d("ChatAdapter", "Chat has been ordered by timestamp");

            if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                documentChanges.forEach(change -> {
                    QueryDocumentSnapshot document = change.getDocument();
                    sb.append(document.get(FROM_KEY));
                    sb.append(":\n");
                    sb.append(document.get(TEXT_KEY));
                    sb.append("\n");
                    sb.append("---\n");
                });


                chatView.append(sb.toString());
            }
        });
    }

    public String getDocumentKey(){
        return DOCUMENT_KEY;
    }
}

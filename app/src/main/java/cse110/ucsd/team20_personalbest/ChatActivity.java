package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;

public class ChatActivity extends AppCompatActivity {
    String TAG = ChatActivity.class.getSimpleName();

    String COLLECTION_KEY = "chat logs";
    String DOCUMENT_KEY = "chat1";
    String MESSAGES_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;
    ChatAdapter fb;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences sharedpreferences = getSharedPreferences("FirebaseLabApp", Context.MODE_PRIVATE);

        from = sharedpreferences.getString(FROM_KEY, null);
        Log.d(TAG, "Name from last session: " + from);

//        chat = FirebaseFirestore.getInstance()
//                .collection(COLLECTION_KEY)
//                .document(DOCUMENT_KEY)
//                .collection(MESSAGES_KEY);


        String factoryKey = this.getIntent().getStringExtra("FACTORY_KEY");
        if(factoryKey == null) factoryKey = "the real one boyz";

        fb = ChatAdapterFactory.build(factoryKey, from, COLLECTION_KEY, DOCUMENT_KEY, MESSAGES_KEY);

        fb.initMessageUpdateListener((TextView) findViewById(R.id.chat));

        fb.subscribeToNotificationsTopic(this);

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        setName(sharedpreferences);
    }

    public void setName(final SharedPreferences sharedpreferences) {
        EditText nameView = findViewById((R.id.user_name));
        nameView.setText(from);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                from = s.toString();
                Log.d(TAG, "Name saved as: " + from);
                sharedpreferences.edit().putString(FROM_KEY, from).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void sendMessage() {
        if (from == null || from.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText messageView = findViewById(R.id.text_message);

        fb.sendMessage(messageView.getText().toString(), messageView);

//        Map<String, String> newMessage = new HashMap<>();
//        newMessage.put(FROM_KEY, from);
//        newMessage.put(TEXT_KEY, messageView.getText().toString());
//
//        chat.add(newMessage).addOnSuccessListener(result -> {
//            messageView.setText("");
//        }).addOnFailureListener(error -> {
//            Log.e(TAG, error.getLocalizedMessage());
//        });
    }

//    private void subscribeToNotificationsTopic() {
//        FirebaseMessaging.getInstance().subscribeToTopic(DOCUMENT_KEY)
//                .addOnCompleteListener(task -> {
//                            String msg = "Subscribed to notifications";
//                            if (!task.isSuccessful()) {
//                                msg = "Subscribe to notifications failed";
//                            }
//                            Log.d(TAG, msg);
//                            Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                );
//    }


//    private void initMessageUpdateListener() {
//        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING).addSnapshotListener((newChatSnapShot, error) -> {
//                    if (error != null) {
//                        Log.e(TAG, error.getLocalizedMessage());
//                        return;
//                    }
//
//                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
//                        StringBuilder sb = new StringBuilder();
//                        List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
//                        documentChanges.forEach(change -> {
//                            QueryDocumentSnapshot document = change.getDocument();
//                            sb.append(document.get(FROM_KEY));
//                            sb.append(":\n");
//                            sb.append(document.get(TEXT_KEY));
//                            sb.append("\n");
//                            sb.append("---\n");
//                        });
//
//
//                        TextView chatView = findViewById(R.id.chat);
//                        chatView.append(sb.toString());
//                    }
//                });
//    }

}

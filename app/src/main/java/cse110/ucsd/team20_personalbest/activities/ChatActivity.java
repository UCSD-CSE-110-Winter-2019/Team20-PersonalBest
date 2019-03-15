package cse110.ucsd.team20_personalbest.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cse110.ucsd.team20_personalbest.R;
import cse110.ucsd.team20_personalbest.chat.ChatAdapter;
import cse110.ucsd.team20_personalbest.chat.ChatAdapterFactory;

public class ChatActivity extends AppCompatActivity {
    String TAG = ChatActivity.class.getSimpleName();

    String COLLECTION_KEY = "chatlogs";
    String DOCUMENT_KEY;
    String MESSAGES_KEY = "messages";

    ChatAdapter fb;
    String from;

    public boolean testsub;
    public boolean msgsent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String factoryKey = this.getIntent().getStringExtra("FACTORY_KEY");

        if(factoryKey == null) factoryKey = "";
        SharedPreferences sp = getSharedPreferences("prefs", MODE_PRIVATE);
        from = this.getIntent().getStringExtra("UserName");
        if(from == null)
            from = sp.getString("UE", null);
        String friendKey = this.getIntent().getStringExtra("friend");
        friendKey = friendKey.substring(0,friendKey.indexOf('@'));
        if(from.compareTo(friendKey) >= 0)
            DOCUMENT_KEY = from + friendKey;
        else
            DOCUMENT_KEY =friendKey + from ;

        fb = ChatAdapterFactory.build(factoryKey, from, COLLECTION_KEY, DOCUMENT_KEY, MESSAGES_KEY);


        TextView t = (TextView) findViewById(R.id.friend_name);
        t.setText(friendKey);


        fb.initMessageUpdateListener((TextView) findViewById(R.id.chat));

        fb.subscribeToNotificationsTopic(this);

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

    }

    private void sendMessage() {
        if (from == null || from.isEmpty()) {
            Toast.makeText(this, "User Name Not Detected", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText messageView = findViewById(R.id.text_message);

        fb.sendMessage(messageView.getText().toString(), messageView);
    }
}

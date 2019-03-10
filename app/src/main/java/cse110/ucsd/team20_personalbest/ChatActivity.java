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

import org.w3c.dom.Text;

public class ChatActivity extends AppCompatActivity {
    String TAG = ChatActivity.class.getSimpleName();

    String COLLECTION_KEY = "chatlogs";
    String DOCUMENT_KEY;
    String MESSAGES_KEY = "messages";

    ChatAdapter fb;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String factoryKey = this.getIntent().getStringExtra("FACTORY_KEY");
        if(factoryKey == null) factoryKey = "";

        from = this.getIntent().getStringExtra("UserName");
        String friendKey = this.getIntent().getStringExtra("friend");
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

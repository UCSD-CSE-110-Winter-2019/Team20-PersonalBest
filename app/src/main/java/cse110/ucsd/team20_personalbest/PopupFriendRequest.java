package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import static cse110.ucsd.team20_personalbest.MainActivity.fbcc;

public class PopupFriendRequest extends AppCompatActivity {

    private EditText friendEmail;
    private Button Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_friend_request);

        ImageView closeBtn = (ImageView) findViewById(R.id.close);
        DisplayMetrics dm = new DisplayMetrics();

        friendEmail = findViewById(R.id.friendEmail);

        Submit = (Button) findViewById(R.id.confirmRequest);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendEmail.getText().toString() != "")
                    fbcc.addFriend(friendEmail.getText().toString());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

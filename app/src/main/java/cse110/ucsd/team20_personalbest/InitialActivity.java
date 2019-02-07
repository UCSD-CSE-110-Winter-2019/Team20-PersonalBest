package cse110.ucsd.team20_personalbest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);


        Button next = findViewById(R.id.initial_next_button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText enter_feet = findViewById(R.id.height_feet);
                EditText enter_inch = findViewById(R.id.height_inches);
                int height_ft = Integer.parseInt(enter_feet.getText().toString());
                int height_in = Integer.parseInt(enter_inch.getText().toString());

                if (height_ft > 20 || height_in > 12) {
                    Toast.makeText(InitialActivity.this, "Enter valid height", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("height", (12 * height_ft) + height_in);
                    editor.apply();

                }
            }
        });

    }
}

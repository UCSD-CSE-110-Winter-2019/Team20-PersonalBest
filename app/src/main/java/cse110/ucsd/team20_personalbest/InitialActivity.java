package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class InitialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        //Boolean walker = true;
/*
        RadioButton radioButton = findViewById(R.id.radio_runner);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*/
        Button next = findViewById(R.id.initial_next_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText enter_feet = findViewById(R.id.height_feet);
                EditText enter_inch = findViewById(R.id.height_inches);
                int height_ft = Integer.parseInt(enter_feet.getText().toString());
                int height_in = Integer.parseInt(enter_inch.getText().toString());

                if (height_ft > 7 || height_in > 12 || height_ft < 0 || height_in < 0) {
                    Toast.makeText(InitialActivity.this, "Enter valid height", Toast.LENGTH_SHORT).show();
                }

                else {
                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("height", (12 * height_ft) + height_in);
                    editor.apply();

                    startActivity(new Intent(InitialActivity.this, MainActivity.class));
                }
            }
        });
    }

    public void onRadioButtonClicked(View v){
        RadioGroup rg = (RadioGroup) findViewById(R.id.button_group);
        RadioButton rb_coldfusion = (RadioButton) findViewById(R.id.radio_walker);
        RadioButton rb_flex = (RadioButton) findViewById(R.id.radio_runner);

        // Is the current Radio Button checked?
        boolean checked = ((RadioButton) v).isChecked();

        switch(v.getId()){
            case R.id.radio_runner:
                if(checked)
                    rb_coldfusion.setTextColor(Color.RED);
                rb_flex.setTextColor(Color.GRAY);
                break;

            case R.id.radio_walker:
                if(checked)
                    rb_flex.setTextColor(Color.RED);
                rb_coldfusion.setTextColor(Color.GRAY);
                break;
        }
    }

}

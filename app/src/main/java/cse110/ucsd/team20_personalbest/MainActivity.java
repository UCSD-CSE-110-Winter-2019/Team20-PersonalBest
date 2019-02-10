package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.Calendar;
import java.util.Date;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView textViewSteps;
    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;

    private boolean intending = false;
    private Activity MAIN = this;
    private IntendedSession is;
    private ModifiedSession ms;
    private long current;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);
        textViewSteps = findViewById(R.id.textViewSteps);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(MainActivity mainActivity) {
                return new GoogleFitAdapter(mainActivity);
            }
        });

        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        fitnessService.setup();

        fitnessService.updateStepCount();

        //Height implementation here
        //if(height is not set)
        //Then go to the activity

        //button to record stops and starts

        Button btnStartStop = findViewById(R.id.startStop);
        btnStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (intending == false) {
                    //is = new IntendedSession( getTime(), MAIN, GoogleSignIn.getLastSignedInAccount(MAIN));
                    current = getTime();
                    intending = true;
                } else {

                    ms = new ModifiedSession(current, getTime());

                    //do things with is while we have it!

                    Toast.makeText(getApplicationContext(), "During this intended walk, you accomplished " +
                            ms.returnSteps() + "steps", Toast.LENGTH_LONG).show();

                    // return and set false
                    intending = false;
                }

            }
        });

    } // end onCreate

    public long getTime() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        return cal.getTimeInMillis();
    }

    public void setStepCount(long steps){
        textViewSteps.setText(String.valueOf(steps));
    }

}

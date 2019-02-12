package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.os.AsyncTask;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
    private Activity activity;

    private boolean updateSteps = true;
    private StepContainer sc;


    private boolean onWalk = false;
    private IntendedSession is;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_walks:
                    mTextMessage.setText(R.string.title_walks);
                    return true;
                case R.id.navigation_stats:
                    mTextMessage.setText(R.string.title_stats);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Boolean isFirstRun = getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        // runs initial activity
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, InitialActivity.class));
            getSharedPreferences("prefs", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();
        }

        activity = this;
        sc = new StepContainer();

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

        new ASyncStepUpdateRunner().execute();

        //Height implementation here
        //if(height is not set)
        //Then go to the activity

        new ASyncStepUpdateRunner().execute();


        //button to record stops and starts

        Button btnStartStop = findViewById(R.id.startStop);
        btnStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                    return;
                }

                if (onWalk == false) {
                    is = new IntendedSession( getTime(), activity, GoogleSignIn.getLastSignedInAccount(activity), sc.steps() );
                    onWalk = true;
                    Toast.makeText(getApplicationContext(), "Started walk", Toast.LENGTH_LONG).show();
                } else {

                    is.endSession(getTime());

                    Toast.makeText(getApplicationContext(), "During this intended walk, you accomplished " +
                            is.returnSteps(sc.steps()) + " steps", Toast.LENGTH_LONG).show();

                    onWalk = false;
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
        sc.setSteps((int) steps);
        textViewSteps.setText(String.valueOf(steps));
    }

    public void cancelUpdatingSteps(){
        updateSteps = false;
    }


    private class ASyncStepUpdateRunner extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            while(updateSteps) {
                try {
                    Thread.sleep(3000);
                    fitnessService.updateStepCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

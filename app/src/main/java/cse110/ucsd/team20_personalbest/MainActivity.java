package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity implements WalkPg.OnWalkPgListener{

    private TextView mTextMessage;
    private TextView textViewGoal;
    private TextView textViewSteps;
    private TextView textViewStats;
    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;
    private Activity activity;

    private int height;
    private final int DEF_HEIGHT = 70;
    private String walkOrRun = "Walk";

    private boolean updateSteps = true;
    private StepContainer sc;

    private FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFrag = new dashboard();
    Class fragmentClass;
    private FrameLayout frame;
    private Goal goal;
    private int fragID;
    private RTWalk rtStat;
    private int tempStep;

    private ArrayList<Walk> pastWalks = new ArrayList<Walk>(100);
    private CustomGauge pedometer;
    private Calendar startTime;
    private Calendar endTime;

    private boolean onWalk = false;
    private IntendedSession is;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            if(currentFrag != null)
                ft.remove(currentFrag);
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    fragmentClass = dashboard.class;
                    fragID = R.id.dashFrag;
                    frame.setVisibility(View.VISIBLE);
                    break;
                case R.id.navigation_walks:
                    mTextMessage.setText(R.string.title_walks);
                    fragmentClass = WalkPg.class;
                    fragID = R.id.walkFrag;
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_stats:
                    mTextMessage.setText(R.string.title_stats);
                    fragID = R.id.statFrag;
                    fragmentClass = StatPg.class;
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
                    fragmentClass = ProfilePg.class;
                    fragID = R.id.profileFrag;
                    frame.setVisibility(View.GONE);
                    break;
            }

            try {
                currentFrag = (Fragment) fragmentClass.newInstance();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            ft.replace(fragID, currentFrag).commit();
            return true;
        }
    };

    @Override
    public void onWalkPgSelected() {
        WalkPg walks = (WalkPg) getSupportFragmentManager().findFragmentById(R.id.walkFrag);
        if(walks != null) {
            walks.updateWalks(pastWalks);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Boolean isFirstRun = getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        // runs initial activity
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, InitialActivity.class));
        }

        // log height and walker/runner saved properly
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String json = sharedPreferences.getString("pastwalks", null);
        if(json != null) {
            Type type = new TypeToken<ArrayList<Walk>>() {
            }.getType();
            pastWalks = gson.fromJson(json, type);
        }

        height = sharedPreferences.getInt("height",DEF_HEIGHT);
        final int height = getSharedPreferences("prefs", MODE_PRIVATE).getInt("height", -1);
        boolean walker = getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("isWalker", true);
        System.err.println("Height: " + height + ", walker: " + walker + "."); // height in inches

        // for start/stop button
        if (!walker) walkOrRun = "Run";

        activity = this;
        sc = new StepContainer();

        frame = (FrameLayout) findViewById(R.id.mainScreen);
        mTextMessage = findViewById(R.id.message);
        pedometer = (CustomGauge) findViewById(R.id.gauge);
        textViewSteps = findViewById(R.id.textViewSteps);
        textViewGoal = findViewById(R.id.textViewGoal);
        textViewStats = findViewById(R.id.textViewStats);
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
        //new ASyncStepUpdateRunner().execute();

        //button to record stops and starts
        final Button btnStartStop = findViewById(R.id.startStop);

        //Goal stuff
        goal = new Goal(5000, true);
        setGoalCount(goal.getGoal());

        GoalObserver go = new GoalObserver(goal, this);
        sc.addObserver(go);

        // set button text and color
        setButton(btnStartStop, onWalk);

        btnStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                    return;
                }

                // starts walk
                if (onWalk == false) {

                    is = new IntendedSession( getTime(), activity, GoogleSignIn.getLastSignedInAccount(activity), sc.steps() );
                    onWalk = true;
                    Toast.makeText(getApplicationContext(), "Started walk", Toast.LENGTH_LONG).show();
                    startTime = Calendar.getInstance();
                    rtStat = new RTWalk(height, startTime);
                    tempStep = sc.steps();
                }

                // stops walk
                else {

                    is.endSession(getTime());
                    Toast.makeText(getApplicationContext(), "During this intended walk, you accomplished " +
                            is.returnSteps(sc.steps()) + " steps", Toast.LENGTH_LONG).show();

                    onWalk = false;
                    endTime = Calendar.getInstance();
                    pastWalks.add(new Walk(height, sc.steps() - tempStep, startTime, endTime));
                    rtStat = null;

                    String json = gson.toJson(pastWalks);
                    editor.putString("pastwalks", json);
                    editor.apply();
                }

                setButton(btnStartStop, onWalk);
            }
        });

    } // end onCreate

    public void setButton(Button btn, boolean onWalk) {
        if (onWalk) {
            btn.setBackgroundColor(Color.RED);
            btn.setText("Stop " + walkOrRun);
        }

        else {
            btn.setBackgroundColor(Color.GREEN);
            btn.setText("Start " + walkOrRun);
        }
    }

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

    public void setGoalCount(int goal){
        textViewGoal.setText((goal + ""));
        pedometer.setEndValue(goal);
    }

    public void sendToast(String string){
        Toast.makeText(activity, string, Toast.LENGTH_LONG).show();
    }

    private void updateRT (Calendar now) {
        if(rtStat != null) {
            textViewStats.setTextSize(20);
            textViewStats.setText(rtStat.updateStat(sc.steps() - tempStep, now));
        }
        else {
            textViewStats.setTextSize(50);
            textViewStats.setText("Stats");
        }
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
                    publishProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            pedometer.setValue(sc.steps());
            updateRT(Calendar.getInstance());
        }
    }
}

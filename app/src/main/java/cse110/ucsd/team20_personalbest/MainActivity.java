package cse110.ucsd.team20_personalbest;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements WalkPg.OnWalkPgListener {

    private FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFrag = new dashboard();
    Class fragmentClass;

    public StepContainer sc;
    private TextView textViewGoal;
    private MainActivity mainActivity;

    private Calendar cal;
    private Button changeStep;
    private EditText timeText;
    private Button changeTime;
    private long timeDiff = 0;
  
    private TextView textViewSteps;

    private static final String PREF_FILE = "prefs";

    private int yesterdaySteps = 1000;
    public boolean getStepsDone = false;

    private TextView textViewStats;

    private FitnessService fitnessService;
    private MainActivity activity;
    public static SessionDataRequestManager sdrm;
    public static DailyStepCountHistory dailysteps;

    private String walkOrRun = "Walk";

    private boolean updateSteps = true;

    private FrameLayout frame;
    private Goal goal;
    private int fragID;
    private RTWalk rtStat;
    private int tempStep;

    private ArrayList<Walk> pastWalks = new ArrayList<>(100);
    private CustomGauge pedometer;
    private Calendar startTime;
    private Calendar nowTime;

    private boolean onWalk = false;
    private IntendedSession is;

    private static final String TAG = "MainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            if(currentFrag != null)
                ft.remove(currentFrag);
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    fragmentClass = dashboard.class;
                    fragID = R.id.dashFrag;
                    frame.setVisibility(View.VISIBLE);
                    break;
                case R.id.navigation_walks:
                    fragmentClass = WalkPg.class;
                    fragID = R.id.walkFrag;
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_stats:
                    fragID = R.id.statFrag;
                    fragmentClass = GraphPg.class;
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_profile:
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
        Log.d(TAG, "Loading Walk Page");
        WalkPg walks = (WalkPg) getSupportFragmentManager().findFragmentById(R.id.walkFrag);
        if(walks != null) {
            Log.d(TAG, "Sending past walks");
            walks.updateWalks(pastWalks);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        sc = new StepContainer();

        boolean isFirstRun = getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        // runs initial activity
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, InitialActivity.class));
        }

        nowTime = Calendar.getInstance();
        nowTime.setTimeInMillis(nowTime.getTimeInMillis() - timeDiff);
        instantiateHistories(getTime(nowTime));


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

        final int height = getSharedPreferences("prefs", MODE_PRIVATE).getInt("height", 70);
        boolean walker = getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("isWalker", true);
        Log.i(TAG,"Height: " + height + ", walker: " + walker + "."); // height in inches

        // for start/stop button
        if (!walker) walkOrRun = "Run";

        activity = this;
        sc = new StepContainer();

        frame = (FrameLayout) findViewById(R.id.mainScreen);
      
        pedometer = findViewById(R.id.gauge);
        textViewSteps = findViewById(R.id.textViewSteps);
        textViewGoal = findViewById(R.id.textViewGoal);
        changeStep = findViewById(R.id.changeStep);
        changeTime = findViewById(R.id.changeTime);
        timeText = findViewById(R.id.changeTimeText);
        changeStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStepCount(sc.steps() + 500);
            }
        });
        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeText.getText().toString().isEmpty() || Long.parseLong(timeText.getText().toString()) == 0) {
                    timeDiff = 0;
                }
                else
                    timeDiff = Calendar.getInstance().getTimeInMillis() - Long.parseLong(timeText.getText().toString());
            }
        });
      
        sc = new StepContainer();
        textViewStats = (TextView) findViewById(R.id.textViewStats);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        mainActivity = this;
        frame = (FrameLayout) findViewById(R.id.mainScreen);
        textViewGoal = (TextView) findViewById(R.id.textViewGoal);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = fm.beginTransaction();

        String fitnessServiceKey = "GOOGLE_FIT";
        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(MainActivity mainActivity) {
                return new GoogleFitAdapter(mainActivity);
            }
        });


        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, activity);

        fitnessService.setup();
        fitnessService.updateStepCount();

        // gets steps
        executeAsyncTask(new ASyncStepUpdateRunner());

        // creates goal based on shared preferences
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() - timeDiff);
        goal = new Goal(this, cal);

        //goal = new Goal (2200, false);
        setGoalCount(goal.getGoal());

        // saves goal for today's graph
        goal.save(this, cal);

        GoalObserver go = new GoalObserver(goal, this);

        sc.addObserver(go);

        //button to record stops and starts
        final Button btnStartStop = findViewById(R.id.startStop);

        // set button text and color
        setButton(btnStartStop, onWalk);

        btnStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nowTime = Calendar.getInstance();
                nowTime.setTimeInMillis(nowTime.getTimeInMillis() - timeDiff);
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                    return;
                }

                // starts walk
                if (!onWalk) {
                    updateCal();

                    is = new IntendedSession( getTime(nowTime), activity, GoogleSignIn.getLastSignedInAccount(activity), sc.steps() );
                    onWalk = true;
                    Toast.makeText(getApplicationContext(), "Started " + walkOrRun, Toast.LENGTH_LONG).show();
                    startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(startTime.getTimeInMillis() - timeDiff);
                    rtStat = new RTWalk(height, startTime);
                    tempStep = sc.steps();
                }

                // stops walk
                else {
                    updateCal();

                    is.endSession(getTime(nowTime));
                    long steps = is.returnSteps(sc.steps());
                    Toast.makeText(getApplicationContext(), "During this intended walk, you accomplished " +
                            is.returnSteps(sc.steps()) + " steps", Toast.LENGTH_LONG).show();

                    goal.addIntendedSteps(is.returnSteps(sc.steps()));
                    goal.save(mainActivity, nowTime);

                    onWalk = false;
                    nowTime = Calendar.getInstance();
                    nowTime.setTimeInMillis(nowTime.getTimeInMillis() - timeDiff);
                    updateRT(nowTime);
                    pastWalks.add(new Walk(rtStat.getStat(), startTime));
                    rtStat = null;

                    String json = gson.toJson(pastWalks);
                    editor.putString("pastwalks", json);
                    editor.apply();
                }

                setButton(btnStartStop, onWalk);
            }
        });

    } // end onCreate


    public void updateCal() {
        cal = Calendar.getInstance();
        Date now = new Date();
        Log.d("Time", "Updating calender to the current time: " + cal.getTimeInMillis());
        cal.setTime(now);
    }

    public void setButton(Button btn, boolean onWalk) {
        if (onWalk) {
            btn.setBackgroundColor(Color.RED);
            String text = "Stop " + walkOrRun;
            btn.setText(text);
        }

        else {
            btn.setBackgroundColor(Color.GREEN);
            String text = "Start " + walkOrRun;
            btn.setText(text);
        }
    }

    public long getTime(Calendar cal) {
        Log.d("Time", "Getting time from calendar: " + cal.getTimeInMillis());
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

    public void updateGoal(int newGoal) {
        goal.setGoal(newGoal);
        Calendar cal = Calendar.getInstance();
        goal.save(this, cal);
    }

    public void setAutoGoal(boolean s) {
        goal.setAutoGoal(s);
    }

    public void sendToast(String string){
        Toast.makeText(activity, string, Toast.LENGTH_LONG).show();
    }

    private void updateRT (Calendar now) {
        Log.d(TAG, "Updating Real-Time stat");
        if(rtStat != null) {
            textViewStats.setTextSize(20);
            rtStat.updateStat(sc.steps() - tempStep, now);
            textViewStats.setText(rtStat.getStat());
        }
        else {
            textViewStats.setTextSize(50);
            textViewStats.setText("Stats");
        }
    }

    public void cancelUpdatingSteps(){
        updateSteps = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 134) {
                fitnessService.setup();
                fitnessService.updateStepCount();
              
                nowTime = Calendar.getInstance();
                nowTime.setTimeInMillis(nowTime.getTimeInMillis() - timeDiff);
                instantiateHistories(getTime(nowTime));
            }
        }
    }

    public boolean setYesterdaySteps(Calendar cal) {
        ArrayList<Integer> stepsArray = dailysteps.getHistory();
        if (stepsArray.size() == 0) return false;

        int yesterday = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (yesterday == -1) yesterday = 6; // rolls over
        yesterdaySteps = stepsArray.get(yesterday);

        Log.d("SubGoal", "Yesterday's steps: " + yesterdaySteps);
        return true;
    }


    public int getYesterdaySteps() {
        return yesterdaySteps;
    }

    private void instantiateHistories(long startTime){
        sdrm = instantiateSessionHistory(7, startTime);
        dailysteps = instantiateDailyHistory(startTime);
    }

    private SessionDataRequestManager instantiateSessionHistory(int dayRange, long timeToStart){
        if(GoogleSignIn.getLastSignedInAccount(activity) != null) {
            return new SessionDataRequestManager(activity, GoogleSignIn.getLastSignedInAccount(activity), dayRange, timeToStart);
        }
        return null;
    }

    private DailyStepCountHistory instantiateDailyHistory(long startTime){
        if(GoogleSignIn.getLastSignedInAccount(activity) != null) {
            return new DailyStepCountHistory(activity, GoogleSignIn.getLastSignedInAccount(activity), startTime);
        }
        return null;
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }


    private class ASyncStepUpdateRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Looper.prepare();
            while (updateSteps) {
                try {
                    Thread.sleep(1000);

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

            Log.d(TAG, "Updating pedometer to " + sc.steps() * 100 / goal.getGoal() + "%");
            pedometer.setValue(sc.steps() * 100 / goal.getGoal());

            nowTime = Calendar.getInstance();
            nowTime.setTimeInMillis(nowTime.getTimeInMillis() - timeDiff);
            updateRT(nowTime);
        }
    }
}

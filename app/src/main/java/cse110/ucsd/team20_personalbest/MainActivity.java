package cse110.ucsd.team20_personalbest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity implements WalkPg.OnWalkPgListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView textViewGoal;
    private TextView textViewSteps;

    SharedPreferences sharedpreferences;
    private static final String PREF_FILE = "prefs";

    private StepContainer sc;

    private GoogleApiClient mGoogleApiClient;
    private int yesterdaySteps = 1000;
    public boolean getStepsDone = false;

    private TextView textViewStats;

    private FitnessService fitnessService;
    private MainActivity activity;
    private SessionDataRequestManager sdrm;
    private DailyStepCountHistory dailysteps;

    private String walkOrRun = "Walk";

    private boolean updateSteps = true;

    private FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFrag = new dashboard();
    Class fragmentClass;
    private FrameLayout frame;
    private Goal goal;
    private int fragID;
    private RTWalk rtStat;
    private int tempStep;

    private ArrayList<Walk> pastWalks = new ArrayList<>(100);
    private CustomGauge pedometer;
    private Calendar startTime;

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
                    fragmentClass = StatPg.class;
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

        instantiateHistories(getTime());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();


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
        System.err.println("Height: " + height + ", walker: " + walker + "."); // height in inches

        // for start/stop button
        if (!walker) walkOrRun = "Run";

        executeAsyncTask(new ViewWeekStepCountTask());

        activity = this;
        sc = new StepContainer();

        frame = findViewById(R.id.mainScreen);
      
        pedometer = findViewById(R.id.gauge);
        textViewSteps = findViewById(R.id.textViewSteps);
        textViewGoal = findViewById(R.id.textViewGoal);

        sc = new StepContainer();
        textViewStats = findViewById(R.id.textViewStats);

        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


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

        // creates goal based on sharedpreferences
        Calendar cal = Calendar.getInstance();
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

                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                    return;
                }

                // starts walk
                if (!onWalk) {

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
                    updateRT(Calendar.getInstance());
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

    public void updateGoal(int newGoal) {
        goal.setGoal(newGoal);
        Calendar cal = Calendar.getInstance();
        goal.save(this, cal);
    }

    public void setAutoGoal(boolean s) {
        goal.setAutoGoal(s);
        System.out.println("AutoGoal: " + goal.useAutoGoal);

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

                instantiateHistories(getTime());
            }
        }
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

    private void saveYesterdaysData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.i("History", "Range Start: " + dateFormat.format(startTime));
        Log.i("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.i("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
    }

    private void showDataSet(DataSet dataSet) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i("History", "Data point:");
            Log.i("History", "\tType: " + dp.getDataType().getName());
            Log.i("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                yesterdaySteps = dp.getValue(field).asInt();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    public int getYesterdaySteps() {
        return yesterdaySteps;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.i("HistoryAPI", "onConnected");
    }

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            saveYesterdaysData();
            getStepsDone = true;
            return null;
        }
    }

    private class ASyncStepUpdateRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Looper.prepare();
            while (updateSteps) {
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

            Log.d(TAG, "Updating pedometer");

            pedometer.setValue(sc.steps());
            updateRT(Calendar.getInstance());
        }
    }
}

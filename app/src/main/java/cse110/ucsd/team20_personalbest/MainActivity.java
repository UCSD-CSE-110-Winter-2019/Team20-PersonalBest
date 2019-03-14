
package cse110.ucsd.team20_personalbest;

import android.annotation.TargetApi;
import android.app.NotificationManager;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import org.w3c.dom.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;
import cse110.ucsd.team20_personalbest.fitness.MockFitness;
import cse110.ucsd.team20_personalbest.friends.FriendsContent;
import pl.pawelkleczkowski.customgauge.CustomGauge;


import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MainActivity extends AppCompatActivity implements WalkPg.OnWalkPgListener, FriendFragment.OnListFragmentInteractionListener {

    private FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFrag = new dashboard();
    Class fragmentClass;

    public StepContainer sc;
    private TextView textViewGoal;
    private MainActivity mainActivity;
    public String fitnessServiceKey;
    private long timeDiff;
    public static FBCommandCenter fbcc;

    private Button changeStep;
    private EditText timeText;
    private Button changeTime;
    private NotificationManager notificationManager;
    private Ntfc ntfc;

    private TextView textViewSteps;

    private static final String PREF_FILE = "prefs";

    private int yesterdaySteps = 1000;
    public boolean getStepsDone = false;
    private int height;

    private TextView textViewStats;

    private FitnessService fitnessService;
    private MainActivity activity;
    public SessionDataRequestManager sdrm;
    public DailyStepCountHistory dailysteps;

    private String walkOrRun = "Walk";

    private boolean updateSteps = true;

    private FrameLayout frame;
    private Goal goal;
    private int fragID;
    private RTWalk rtStat;
    private int tempStep;
    private boolean dashboardVisible = true;
    private FloatingActionButton floatBtn;
    private boolean isFirstRun;

    private OurCal ourCal;

    private ArrayList<Walk> pastWalks = new ArrayList<>(100);
    private CustomGauge pedometer;
    private Calendar startTime;

    private boolean onWalk = false;
    private IntendedSession is;
    private GoogleSignInClient mGoogleSignInClient;
    public GoogleSignInAccount account;
    private static final String TAG = "MainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fm.beginTransaction();
            floatBtn = (FloatingActionButton) findViewById(R.id.floatBtn);
            if(currentFrag != null)
                ft.remove(currentFrag);
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    fragmentClass = dashboard.class;
                    fragID = R.id.dashFrag;
                    frame.setVisibility(View.VISIBLE);
                    floatBtn.hide();
                    dashboardVisible = true;
                    break;
                case R.id.navigation_walks:
                    fragmentClass = WalkPg.class;
                    fragID = R.id.walkFrag;
                    frame.setVisibility(View.GONE);
                    floatBtn.hide();
                    dashboardVisible = false;
                    break;
                case R.id.navigation_stats:
                    fragID = R.id.statFrag;
                    floatBtn.hide();
                    fragmentClass = GraphPg.class;
                    frame.setVisibility(View.GONE);
                    dashboardVisible = false;
                    break;
                case R.id.navigation_profile:
                    fragmentClass = ProfilePg.class;
                    fragID = R.id.profileFrag;
                    floatBtn.hide();
                    frame.setVisibility(View.GONE);
                    dashboardVisible = false;
                    break;
                case R.id.navigation_friend:
                    fragmentClass = FriendFragment.class;
                    fragID = R.id.friendFrag;
                    frame.setVisibility(View.GONE);
                    floatBtn.show();
                    dashboardVisible = false;
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
    public void onFriendPgSelected() {
        Log.d(TAG, "Loading Friend Page");
        FriendFragment fpg = (FriendFragment) getSupportFragmentManager().findFragmentById(R.id.friendFrag);
        if(fpg != null) {
            Log.d(TAG, "Sending email");
            String email = account.getEmail();
            email = email.substring(0,email.indexOf('@'));
            fpg.updateUserName(email);
        }
        fbcc.updateFriends();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatBtn = (FloatingActionButton) findViewById(R.id.floatBtn);
        floatBtn.hide();
        activity = this;
        sc = new StepContainer();

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PopupFriendRequest.class);
                intent.putExtra("email", account.getEmail());
                startActivity(intent);
            }
        });

        isFirstRun = getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        // for espresso tests
        if (getIntent().getStringExtra("service_key") != null && getIntent().getStringExtra("service_key").equals("MOCK_FIT")) {
            isFirstRun = true;
        }

        // runs initial activity
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, InitialActivity.class));
            Log.d(TAG, "Starting initial login page activity");
        }

        // calendar for current day
        ourCal = new OurCal(Calendar.getInstance(), 0);

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

        height = getSharedPreferences("prefs", MODE_PRIVATE).getInt("height", 70);
        boolean walker = getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("isWalker", true);
        Log.i(TAG,"Height: " + height + ", walker: " + walker + "."); // height in inches

        // for start/stop button
        if (!walker) walkOrRun = "Run";

        activity = this;
        sc = new StepContainer();

        frame = (FrameLayout) findViewById(R.id.mainScreen);

        pedometer = (CustomGauge) findViewById(R.id.gauge);
        textViewSteps = (TextView) findViewById(R.id.textViewSteps);
        textViewGoal = (TextView) findViewById(R.id.textViewGoal);
        changeStep = (Button) findViewById(R.id.changeStep);
        changeTime = (Button) findViewById(R.id.changeTime);
        timeText = (EditText) findViewById(R.id.changeTimeText);
        changeStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStepCount(sc.steps() + 500);
                Log.d(TAG, "Extra steps added; not added to google history");
                ntfc.push(238647667);
            }
        });
        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDiff = 0;
                if(!timeText.getText().toString().isEmpty() && Long.parseLong(timeText.getText().toString()) != 0) {
                    timeDiff = Calendar.getInstance().getTimeInMillis() - Long.parseLong(timeText.getText().toString());
                    Log.d(TAG, "Time Changed");}
                ourCal.setTimeDiff(timeDiff);
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


        fitnessServiceKey = getIntent().getStringExtra("service_key");
        if(fitnessServiceKey == null){
            fitnessServiceKey = "GOOGLE_FIT";
            FitnessServiceFactory.put("GOOGLE_FIT", new FitnessServiceFactory.BluePrint() {
                @Override
                public FitnessService create(MainActivity mainActivity) {
                    return new GoogleFitAdapter(mainActivity);
                }
            });

            fitnessService = FitnessServiceFactory.create(fitnessServiceKey, activity);
            fitnessService.setup();
            executeAsyncTask(new ASyncStepUpdateRunner());
        }
        else
            FitnessServiceFactory.put("MOCK_FIT", new FitnessServiceFactory.BluePrint() {
                @Override
                public FitnessService create(MainActivity mainActivity) {
                    return new MockFitness(mainActivity);
                }
            });

        // creates goal based on shared preferences
        goal = new Goal(this, ourCal.getCal());

        //goal = new Goal (2200, false);
        setGoalCount(goal.getGoal());

        // saves goal for today's graph
        goal.save(this, ourCal.getCal());

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
                    // updates time
                    ourCal.setCal(Calendar.getInstance());

                    is = new IntendedSession(ourCal.getTime(), activity, GoogleSignIn.getLastSignedInAccount(activity), sc.steps() );
                    onWalk = true;
                    height = getSharedPreferences("prefs", MODE_PRIVATE).getInt("height", 70);
                    Toast.makeText(getApplicationContext(), "Started " + walkOrRun, Toast.LENGTH_LONG).show();
                    startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(startTime.getTimeInMillis() - timeDiff);
                    rtStat = new RTWalk(height, startTime);
                    tempStep = sc.steps();
                }

                // stops walk
                else {

                    ourCal.setCal(Calendar.getInstance());

                    is.endSession(ourCal.getTime());
                    Toast.makeText(getApplicationContext(), "During this intended walk, you accomplished " +
                            is.returnSteps(sc.steps()) + " steps", Toast.LENGTH_LONG).show();

                    goal.addIntendedSteps(is.returnSteps(sc.steps()));
                    goal.save(mainActivity, ourCal.getCal());

                    onWalk = false;
                    updateRT();
                    pastWalks.add(new Walk(rtStat.getStat(), startTime));
                    rtStat = null;

                    String json = gson.toJson(pastWalks);
                    editor.putString("pastwalks", json);
                    editor.apply();
                }

                setButton(btnStartStop, onWalk);
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 134);


        ntfc = new Ntfc(this, "Goal Met", "Goal Met", "goal", getSystemService(NotificationManager.class));

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

    public FitnessService getFitnessService(){
        return fitnessService;
    }

    public void setStepCount(long steps){
        sc.setSteps((int) steps);
        textViewSteps.setText(String.valueOf(steps));
    }

    public void setGoalCount(int goal){
        textViewGoal.setText((goal + ""));
        pedometer.setEndValue(goal);
    }

    public void updateGoal(int newGoal, Calendar cal) {
        goal.setGoal(newGoal);
        goal.save(this, cal);
    }

    public void setAutoGoal(boolean s) {
        goal.setAutoGoal(s);
        goal.save(this, ourCal.getCal());
    }

    public void setMeetOnce(boolean b) {
        goal.setMeetOnce(b);
        goal.save(this, ourCal.getCal());
    }

    public boolean isDashboardVisible() {
        return dashboardVisible;
    }

    public void sendToast(String string){
        Toast.makeText(activity, string, Toast.LENGTH_LONG).show();
    }

    private void updateRT () {
        Log.d(TAG, "Updating Real-Time stat");
        if(rtStat != null) {
            Calendar EndTime = Calendar.getInstance();
            ourCal.setCal(Calendar.getInstance());
            textViewStats.setTextSize(20);
            EndTime.setTimeInMillis(EndTime.getTimeInMillis() - timeDiff);
            rtStat.updateStat(sc.steps() - tempStep,EndTime);
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

                ourCal.setCal(Calendar.getInstance());

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    account = task.getResult(ApiException.class);
                    fbcc = new FBCommandCenter(account.getEmail(), account.getGivenName(), account.getFamilyName(), this);
                } catch (ApiException e) {
                    e.printStackTrace();
                }

                if(GoogleSignIn.getLastSignedInAccount(activity) != null) {
                    Map<String, String> newUser = new HashMap<>();
                    CollectionReference user = FirebaseFirestore.getInstance()
                            .collection("users");
                }
            }
        }
    }



    public boolean setYesterdaySteps(Calendar cal) {
        if(dailysteps == null){
            return false;
        }
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

    public Calendar getOurCal() {
        return ourCal.getCal();
    }

    public Goal getGoal() {return goal;}

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
            pedometer.setValue(sc.steps() * 100 / goal.getGoal() > 100 ? 100 : sc.steps() * 100 / goal.getGoal());
            ourCal.setCal(Calendar.getInstance());
            updateRT();
        }
    }
}
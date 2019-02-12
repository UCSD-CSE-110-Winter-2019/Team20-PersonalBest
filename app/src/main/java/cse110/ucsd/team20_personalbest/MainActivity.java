package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.os.AsyncTask;
import android.widget.Toast;

import org.w3c.dom.Text;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFrag = new dashboard();
    Class fragmentClass;

    private StepContainer sc;
    private TextView textViewGoal;
    private MainActivity mainActivity;

    private TextView textViewSteps;
    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;
    private boolean updateSteps = true;

    private FrameLayout frame;
    private Goal goal;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    fragmentClass = dashboard.class;
                    frame.setVisibility(View.VISIBLE);
                    break;
                case R.id.navigation_walks:
                    mTextMessage.setText(R.string.title_walks);
                    fragmentClass = WalkPg.class;
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_stats:
                    mTextMessage.setText(R.string.title_stats);
                    fragmentClass = StatPg.class;
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
                    fragmentClass = ProfilePg.class;
                    frame.setVisibility(View.GONE);
                    break;
            }

            try {
                currentFrag = (Fragment) fragmentClass.newInstance();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.walkFrag, currentFrag).commit();
            return true;
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
        }

        mTextMessage = findViewById(R.id.message);
        textViewSteps = findViewById(R.id.textViewSteps);

        mainActivity = this;
        frame = (FrameLayout) findViewById(R.id.mainScreen);
        textViewGoal = (TextView) findViewById(R.id.textViewGoal);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = fm.beginTransaction();

      
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

        // log height and walker/runner saved properly
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        int height = getSharedPreferences("prefs", MODE_PRIVATE).getInt("height", -1);
        boolean walker = getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("isWalker", true);
        System.err.println("Height: " + height + ", walker: " + walker + ".");

        //Goal stuff
        sc = new StepContainer();

        goal = new Goal(17, true);
        setGoalCount(goal.getGoal());

        GoalObserver go = new GoalObserver(goal, this);
        sc.addObserver(go);

        //Height implementation here
        //if(height is not set)
        //Then go to the activity
    }

    public void setStepCount(long steps){
         sc.setSteps((int) steps);
        textViewSteps.setText(String.valueOf(steps));
    }


    public void setGoalCount(int goal){
        textViewGoal.setText((goal + ""));
    }

    public void sendToast(String string){
        Toast.makeText(mainActivity, string, Toast.LENGTH_LONG).show();
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

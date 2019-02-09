package cse110.ucsd.team20_personalbest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity{

    private TextView mTextMessage;
    private TextView textViewSteps;
    private TextView textViewGoal;

    private MainActivity mainActivity;

    private StepContainer sc;

    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;
    private boolean updateSteps = true;
    private Goal goal;

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

        mainActivity = this;

        mTextMessage = findViewById(R.id.message);
        textViewSteps = findViewById(R.id.textViewSteps);
        textViewGoal = findViewById(R.id.textViewGoal);

        sc = new StepContainer();

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

        //Set goal from shared preferences, or if first run set to 5000
        //Also update boolean if they've met the goal today
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

    public void cancelUpdatingSteps(){
        updateSteps = false;
    }

    public void sendToast(String string){
        Toast.makeText(mainActivity, string, Toast.LENGTH_LONG).show();
    }

    private class ASyncStepUpdateRunner extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Looper.prepare();
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

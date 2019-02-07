package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView textViewSteps;
    private TextView textViewGoal;

    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;
    private boolean updateSteps = true;
    private Goal goal;
    private long currentSteps;

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
        textViewGoal = findViewById(R.id.textViewGoal);

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
        goal = new Goal((int)currentSteps + 10);
        setGoalCount(goal.getGoal());

        //Height implementation here
        //if(height is not set)
        //Then go to the activity
    }

    public void setStepCount(long steps){
        currentSteps = steps;
        textViewSteps.setText(String.valueOf(steps));
    }

    public void setGoalCount(int goal){
        textViewGoal.setText((String.valueOf(goal)));
    }

    public void cancelUpdatingSteps(){
        updateSteps = false;
    }

    public void sendToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }


    private class ASyncStepUpdateRunner extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            while(updateSteps) {
                try {
                    Thread.sleep(3000);

                    fitnessService.updateStepCount();

                    if(goal.attemptCompleteGoal(currentSteps)){
                        sendToast("Congratulations on meeting your goal of " + goal.getGoal() + " steps!");
                        goal.setGoal(goal.getGoal() + 10);
                        setGoalCount((goal.getGoal()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}

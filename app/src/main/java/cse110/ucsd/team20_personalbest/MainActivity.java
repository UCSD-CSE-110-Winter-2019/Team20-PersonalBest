package cse110.ucsd.team20_personalbest;

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

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;
import cse110.ucsd.team20_personalbest.fitness.GoogleFitAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFrag = new dashboard();
    Class fragmentClass;

    private TextView textViewSteps;
    private String fitnessServiceKey = "GOOGLE_FIT";
    private FitnessService fitnessService;
    private boolean updateSteps = true;
    private FrameLayout frame;

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
                    fragmentClass = Walks.class;
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_stats:
                    mTextMessage.setText(R.string.title_stats);
                    frame.setVisibility(View.GONE);
                    break;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
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

        frame = (FrameLayout) findViewById(R.id.mainScreen);
        mTextMessage = (TextView) findViewById(R.id.message);
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

        //Height implementation here
        //if(height is not set)
        //Then go to the activity
    }
    public void setStepCount(long steps){
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

package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Rule;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.RuntimeEnvironment;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;

import static org.junit.Assert.assertEquals;


//@RunWith(RobolectricTestRunner.class)
public class TrackStepsUnitTest {

    private static final String TEST_SERVICE = "TEST_SERVICE";
    private String fitnessServiceKey = "GOOGLE_FIT";

    //private MainActivity activity;
    private TextView textViewSteps;
    private long nextStepCount;

    public ActivityTestRule<MainActivity> activity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws Exception {

        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {

            @Override

            public FitnessService create(MainActivity mainActivity) {
                return new TestFitnessService(mainActivity);
            }

        });

        //Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        //intent.putExtra(fitnessServiceKey, TEST_SERVICE);
        //activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();


        textViewSteps = activity.getActivity().findViewById(R.id.textViewSteps);
        nextStepCount = 1337;

    }

    //@Test
    //public void testInitialSteps() {
       // assertEquals("Steps", textViewSteps.getText().toString());
    //}

    private class TestFitnessService implements FitnessService {

        private static final String TAG = "[TestFitnessService]: ";
        private MainActivity mainActivity;

        public TestFitnessService(MainActivity mainCountActivity) {
            this.mainActivity = mainCountActivity;
        }

        @Override
        public int getRequestCode() {
            return 0;
        }

        @Override

        public void setup() {
            System.out.println(TAG + "setup");
        }

        @Override

        public void updateStepCount() {
            System.out.println(TAG + "updateStepCount");
            mainActivity.setStepCount(nextStepCount);
        }

    }



}

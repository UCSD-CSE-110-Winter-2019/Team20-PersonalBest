package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;
import cse110.ucsd.team20_personalbest.fitness.FitnessServiceFactory;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class StepCountActivityUnitTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private StepCountActivity activity;
    private TextView textSteps;
    private Button btnUpdateSteps;
    private long nextStepCount;

    @Before
    public void setUp() throws Exception {
        FitnessServiceFactory.put(TEST_SERVICE, new FitnessServiceFactory.BluePrint() {
            @Override
            public FitnessService create(StepCountActivity stepCountActivity) {
                return new TestFitnessService(stepCountActivity);
            }
        });

        Intent intent = new Intent(RuntimeEnvironment.application, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, TEST_SERVICE);
        activity = Robolectric.buildActivity(StepCountActivity.class, intent).create().get();

        textSteps = activity.findViewById(R.id.textSteps);
        btnUpdateSteps = activity.findViewById(R.id.buttonUpdateSteps);
        nextStepCount = 1337;
    }

    @Test
    public void testUpdateStepsButton() {
        assertEquals("steps will be shown here", textSteps.getText().toString());
        btnUpdateSteps.performClick();
        assertEquals("1337", textSteps.getText().toString());
    }

    private class TestFitnessService implements FitnessService {
        private static final String TAG = "[TestFitnessService]: ";
        private StepCountActivity stepCountActivity;

        public TestFitnessService(StepCountActivity stepCountActivity) {
            this.stepCountActivity = stepCountActivity;
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
            stepCountActivity.setStepCount(nextStepCount);
        }
    }
}
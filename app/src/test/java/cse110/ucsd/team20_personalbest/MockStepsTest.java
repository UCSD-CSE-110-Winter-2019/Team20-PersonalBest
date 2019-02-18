package cse110.ucsd.team20_personalbest;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.apache.tools.ant.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;


@RunWith(RobolectricTestRunner.class)
public class MockStepsTest {

    private TextView textView;
    private MainActivity activity;

    @Before
    public void onCreate_shouldInflateLayout() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, MainActivity.class);
        intent.putExtra("service_key", "MOCK_FIT");
        activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();

        textView = activity.findViewById(R.id.textViewSteps);
    }

    @Test
    public void testUpdateSteps(){
        FitnessService fitnessService = activity.getFitnessService();
        assert(textView.getText().toString().equals("Steps"));
        fitnessService.updateStepCount();
        assert(textView.getText().toString().equals(300 + ""));
    }
}
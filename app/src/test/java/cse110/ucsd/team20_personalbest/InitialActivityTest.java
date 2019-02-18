package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class InitialActivityTest {

    private Activity activity;
    private Button next;
    private EditText heightFeet;
    private EditText heightInches;
    private RadioButton walker;
    private RadioButton runner;
    private SharedPreferences sharedPreferences;

    @Before
    public void onCreate_shouldInflateLayout() throws Exception {
        Intent intent = new Intent(RuntimeEnvironment.application, InitialActivity.class);
        activity = Robolectric.buildActivity(InitialActivity.class, intent).create().get();

        heightFeet = activity.findViewById(R.id.height_feet);
        heightInches = activity.findViewById(R.id.height_inches);
        walker = activity.findViewById(R.id.radio_walker);
        runner = activity.findViewById(R.id.radio_runner);
        next = activity.findViewById(R.id.initial_next_button);

        sharedPreferences = activity.getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    @Test
    public void testSharedPrefs() {
        heightInches.setText("5");
        heightFeet.setText("5");
        walker.setActivated(true);
        runner.setActivated(false);
        next.performClick();

        int savedFeet = sharedPreferences.getInt("feet", 0);
        int savedInches = sharedPreferences.getInt("inches", 0);
        int savedHeight = sharedPreferences.getInt("height", 0);
        boolean savedIsWalker = sharedPreferences.getBoolean("isWalker", false);

        assert (savedFeet == 5);
        assert (savedInches == 5);
        assert (true == savedIsWalker);
        assert (savedHeight == 65);
    }

    @Test
    public void failToEnterData() {
        next.performClick();
        String warning = ShadowToast.getTextOfLatestToast();
        assert (warning.equals("Enter your height"));

        int savedFeet = sharedPreferences.getInt("feet", 0);
        int savedInches = sharedPreferences.getInt("inches", 0);
        int savedHeight = sharedPreferences.getInt("height", 0);
        assert (savedInches == 0);
        assert (savedFeet == 0);
        assert (savedHeight == 0);
    }

    @Test
    public void enterInvalidData() {
        heightFeet.setText("10");
        heightInches.setText("5");
        walker.setActivated(true);
        next.performClick();

        String warning = ShadowToast.getTextOfLatestToast();
        System.out.println(warning);
        assert (warning != null && warning.equals("Enter valid height"));

        heightFeet.setText("5");
        heightInches.setText("12");
        next.performClick();

        warning = ShadowToast.getTextOfLatestToast();
        assert (warning != null && warning.equals("Enter valid height"));

        heightFeet.setText("-1");
        heightInches.setText("12");
        next.performClick();

        warning = ShadowToast.getTextOfLatestToast();
        assert (warning != null && warning.equals("Enter valid height"));
    }
}

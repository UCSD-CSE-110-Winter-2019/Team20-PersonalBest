package cse110.ucsd.team20_personalbest;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class GoalDataRequestManagerTest {

    GoalDataRequestManager goalDataRequestManager;
    Calendar currentDay;
    SharedPreferences sharedPreferences;

    @Before
    public void setup() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext());

        currentDay = Calendar.getInstance();
        currentDay.setTimeInMillis(1396612800000l);

        sharedPreferences.edit().putInt("goal" + currentDay.get(Calendar.YEAR) + currentDay.get(Calendar.DAY_OF_YEAR), 5000).commit();
        currentDay.add(Calendar.DATE, 1);
        sharedPreferences.edit().putInt("goal" + currentDay.get(Calendar.YEAR) + currentDay.get(Calendar.DAY_OF_YEAR), 6000).commit();
        currentDay.add(Calendar.DATE, 1);
        sharedPreferences.edit().putInt("goal" + currentDay.get(Calendar.YEAR) + currentDay.get(Calendar.DAY_OF_YEAR), 7000).commit();
        currentDay.add(Calendar.DATE, 1);
        sharedPreferences.edit().putInt("goal" + currentDay.get(Calendar.YEAR) + currentDay.get(Calendar.DAY_OF_YEAR), 8000).commit();


        goalDataRequestManager = new GoalDataRequestManager(sharedPreferences);
    }

    @Test
    public void testRequestGoals() {
        goalDataRequestManager.requestGoals(currentDay.getTimeInMillis(), 5);

        ArrayList<Integer> actualGoals = new ArrayList<>();
        actualGoals.add(0);
        actualGoals.add(5000);
        actualGoals.add(6000);
        actualGoals.add(7000);
        actualGoals.add(8000);
        ArrayList<Integer> goals = goalDataRequestManager.getGoalDataArray();

        assertNotNull(goals);
        assertTrue(goals.equals(actualGoals));
    }
}
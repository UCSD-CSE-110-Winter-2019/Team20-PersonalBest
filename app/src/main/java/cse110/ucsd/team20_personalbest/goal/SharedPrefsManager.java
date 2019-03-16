package cse110.ucsd.team20_personalbest.goal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import cse110.ucsd.team20_personalbest.util.BoundValidity;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefsManager implements SharedPrefsManagerInterface {
    private int goal;
    private boolean useAutoGoal = true;
    private boolean ignored = false;
    private boolean met;
    private boolean displayedPopup = false;
    private boolean displayedSubGoal = false;
    private boolean popupForYesterday = false;
    private boolean popupCurrentlyOpen = false;
    private boolean meetOnce = true;
    private int currentDay;
    public long currentIntendedSteps;

    private String[] daysOfWeek = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    private static final int DEFAULT_GOAL_INCREMENT = 500;
    private static final int INITIAL_GOAL = 5000;

    private int autoGoalIncr = DEFAULT_GOAL_INCREMENT;

    private void loadVariables(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        goal = sharedpreferences.getInt("savedGoal", INITIAL_GOAL);
        met = sharedpreferences.getBoolean("metToday", false);
        useAutoGoal = sharedpreferences.getBoolean("autoGoal", true);
        currentDay = sharedpreferences.getInt("currentDay", -1);
        displayedPopup = sharedpreferences.getBoolean("displayedPopup", false);
        displayedSubGoal = sharedpreferences.getBoolean("displayedSubGoal", false);
        currentIntendedSteps = sharedpreferences.getLong("currentIntendedSteps", 0);
        meetOnce = sharedpreferences.getBoolean("meetOnlyOnce", true);
        ignored = sharedpreferences.getBoolean("ignored", false);

        Log.i("Goal", "Loading goal from sharedPreferences\n\tGoal: " + goal + "\n\tMet: " + met
                + "\n\tCurrent day: " + currentDay + "\n\tDisplayed popup: " + displayedPopup
                + "\n\tDisplayed sub goal: " + displayedSubGoal + "\n\tCurrent intended steps: " + currentIntendedSteps
                + "\n\tMeet goal only once: " + meetOnce + "\n\tIgnored: " + ignored);
    }

    // makes a goal based on the saved shared preferences
    public SharedPrefsManager(Context context) {
        loadVariables(context);
    }

    // makes a goal based on the saved shared preferences
    public SharedPrefsManager(Context context, Calendar cal) {
        loadVariables(context);

        // sets met to false if its the next day and displays goal met popup if goal was met
        // yesterday but the popup was not shown
        resetMetAndDisplayYesterdaysPopup(cal);
    }

    @Override
    public void resetMetAndDisplayYesterdaysPopup(Calendar cal) {
        // resets met each day
        int today = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if (currentDay != today && currentDay != -1) {
            if (currentDay == (today % 6) - 1 && met && !displayedPopup) {
                popupForYesterday = true;
                Log.i("Goal", "Met the goal yesterday but didn't show popup, yesterday's popup to be displayed");
            }
            currentIntendedSteps = 0;
            met = false;
            ignored = false;
            displayedPopup = false;
            displayedSubGoal = false;
            currentDay = today;
            Log.i("Goal", "First time the app has been opened today, saving current day");
        }
        else if (currentDay == -1) {
            Log.i("Goal", "First day this app has been opened, saving current day");
            currentDay = today;
        }
        else {
            Log.i("Goal", "App has already been opened, current day already saved");
        }
    }

    // for testing
    public SharedPrefsManager(int steps, boolean met) {
        goal = steps;
        this.met = met;
        currentDay = -1;
    }

    // for testing
    public SharedPrefsManager(int steps, boolean met, int nextautogoal){
        goal = steps;
        autoGoalIncr = nextautogoal;
        this.met = met;
        currentDay = -1;
    }

    // saves goal values to sharedpreferences
    @Override
    public void save(Context ma, Calendar cal) {
        SharedPreferences sharedpreferences = ma.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("savedGoal", goal);
        editor.putBoolean("metToday", met);
        editor.putInt("currentDay", currentDay);
        editor.putBoolean("displayedPopup", displayedPopup);
        editor.putBoolean("displayedSubGoal", displayedSubGoal);
        editor.putLong("currentIntendedSteps", currentIntendedSteps);
        editor.putBoolean("ignored", ignored);
        editor.putInt("goal" + cal.get(Calendar.YEAR) + cal.get(Calendar.DAY_OF_YEAR), goal);
        editor.apply();
        Log.i("Goal", "Saving goal to sharedPreferences\n\tGoal: " + goal + "\n\tMet: " + met
                + "\n\tCurrent day: " + currentDay + " = " + daysOfWeek[currentDay]
                + "\n\tDisplayed popup: " + displayedPopup + "\n\tDisplayed subgoal: " + displayedSubGoal
                + "\n\tCurrent intended steps: " + currentIntendedSteps + "\n\tMeet goal only once: " + meetOnce
                + "\n\tIgnored: " + ignored);


        saveIntendedStepsDay(ma, editor, cal);

        // saves today's goal for later graphing
        if (!met)
            saveGoalDay(ma, editor, cal);
        else
            Log.i("Goal", "Goal already met today, won't update today's goal for graph.");
    }

    @Override
    public void saveGoalDay(Context ma, SharedPreferences.Editor editor, Calendar cal) {
        String today = daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1];
        Log.i("Goal", "Saving current goal of " + goal + " to " + today + " for graph.");
        editor.putInt(today + " goal", goal);
        editor.apply();
    }

    @Override
    public void saveIntendedStepsDay(Context ma, SharedPreferences.Editor editor, Calendar cal) {
        String today = daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1];
        Log.i("Goal", "Saving intended steps of " + currentIntendedSteps + " to " + today + " for graph.");
        editor.putLong(today + " walks", currentIntendedSteps);
        editor.apply();
    }

    public boolean canSetAutomatically() {
        BoundValidity valid = new BoundValidity();
        return valid.autoGoal(goal + autoGoalIncr) && useAutoGoal;
    }

    // whether it is time to show a subgoal
    public boolean canShowSubGoal(Calendar calendar) {
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currMinute = calendar.get(Calendar.MINUTE);
        return currHour >= 20 && !displayedSubGoal; // Once after 8 pm
    }

    @Override
    public void displaySubGoal(Context context, int steps, int yesterdaySteps) {
        if (steps >= yesterdaySteps + 500) {
            // round down to nearest 500 steps
            int diff = (steps - yesterdaySteps) / 500 * 500;
            Toast.makeText(context, "You got about " + diff + " more steps than yesterday!", Toast.LENGTH_LONG).show();
            Log.i("SubGoal", "Subgoal met.");
        }
        else {
            Log.i("SubGoal", "Subgoal not met today");
        }
    }


    // getters and setters:

    public boolean getPopupForYesterday() {
        return popupForYesterday;
    }

    public void setDisplayedPopup(boolean b) {
        displayedPopup = b;
    }

    public void setPopupForYesterday(boolean b) {
        popupForYesterday = b;
    }

    public void setDisplayedSubGoal(boolean b) {
        displayedSubGoal = b;
    }

    public void setPopupCurrentlyOpen(boolean b) {
        popupCurrentlyOpen = b;
    }

    public boolean getMeetOnce() {
        return meetOnce;
    }

    public int nextAutoGoal() {
        return canSetAutomatically() ? goal + autoGoalIncr : goal;
    }

    public void setGoal(int val) {
        goal = val;
    }

    public boolean getIgnored() {return ignored;}

    public void setIgnored(boolean b) {ignored = b;}

    public void setMeetOnce(boolean b) {meetOnce = b;}

    public void setAutoGoal(boolean s) {useAutoGoal = s;}

    public void addIntendedSteps(long steps) {
        currentIntendedSteps += steps;
    }

    public long getCurrentIntendedSteps() {
        return currentIntendedSteps;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public boolean attemptCompleteGoal(long steps){
        return steps >= goal && !popupCurrentlyOpen;
    }

    // goal only counts as met if meetOnce is on.
    public boolean metToday(){
        return met;
    }

    public void meetGoal(boolean met) {
        this.met = met;
    }

    public int getGoal() {
        return goal;
    }
}
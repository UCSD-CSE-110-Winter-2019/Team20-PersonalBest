package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class Goal {
    private int goal;

    boolean useAutoGoal = true;
    private boolean met;
    boolean displayedPopup = false;
    boolean displayedSubGoal = false;
    boolean popupForYesterday = false;
    private int currentDay;
    public long currentIntendedSteps;

    private String[] daysOfWeek = {"Sunday", "Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    private static final int DEFAULT_GOAL_INCREMENT = 500;
    private static final int INITIAL_GOAL = 5000;
    private static final int DEFAULT_MAX_AUTO_GOAL = 15000;

    private int autoGoalIncr = DEFAULT_GOAL_INCREMENT;

    // makes a goal based on the saved shared preferences
    public Goal(Context context, Calendar cal) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        goal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getInt("savedGoal", INITIAL_GOAL);
        met = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("metToday", false);
        useAutoGoal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("autoGoal", true);
        currentDay = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getInt("currentDay", -1);
        displayedPopup = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("displayedPopup", false);
        displayedSubGoal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("displayedSubGoal", false);
        currentIntendedSteps = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getLong("currentIntendedSteps", 0);

        Log.i("Goal", "Loading goal from sharedPreferences\n\tGoal: " + goal + "\n\tMet: " + met
                + "\n\tCurrent day: " + currentDay + "\n\tDisplayed popup: " + displayedPopup
                + "\n\tDisplayed sub goal: " + displayedSubGoal + "\n\tCurrent intended steps: " + currentIntendedSteps);

        // sets met to false if its the next day and displays goal met popup if goal was met
        // yesterday but the popup was not shown
        resetMetAndDisplayYesterdaysPopup(cal);
    }

    public Goal(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        goal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getInt("savedGoal", INITIAL_GOAL);
        met = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("metToday", false);
        useAutoGoal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("autoGoal", true);
        currentDay = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getInt("currentDay", -1);
        displayedPopup = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("displayedPopup", false);
        displayedSubGoal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("displayedSubGoal", false);
        currentIntendedSteps = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getLong("currentIntendedSteps", 0);

        Log.i("Goal", "Loading goal from sharedPreferences\n\tGoal: " + goal + "\n\tMet: " + met
                + "\n\tCurrent day: " + currentDay + "\n\tDisplayed popup: " + displayedPopup
                + "\n\tDisplayed sub goal: " + displayedSubGoal + "\n\tCurrent intended steps: " + currentIntendedSteps);
    }

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

    // custom goal
    public Goal(int steps, boolean met) {
        goal = steps;
        this.met = met;
        currentDay = -1;
    }

    public Goal(int steps, boolean met, int nextautogoal){
        goal = steps;
        autoGoalIncr = nextautogoal;
        this.met = met;
        currentDay = -1;
    }

    public void addIntendedSteps(long steps) {
        currentIntendedSteps += steps;
    }

    public long getCurrentIntendedSteps() {
        return currentIntendedSteps;
    }

    public boolean autoGoal() {
        return useAutoGoal;
    }

    // saves goal values to sharedpreferences
    public void save(Context ma, Calendar cal) {
        SharedPreferences sharedpreferences = ma.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("savedGoal", goal);
        editor.putBoolean("metToday", met);
        editor.putInt("currentDay", currentDay);
        editor.putBoolean("displayedPopup", displayedPopup);
        editor.putBoolean("displayedSubGoal", displayedSubGoal);
        editor.putLong("currentIntendedSteps", currentIntendedSteps);
        editor.apply();
        Log.i("Goal", "Saving goal to sharedPreferences\n\tGoal: " + goal + "\n\tMet: " + met
                + "\n\tCurrent day: " + currentDay + " = " + daysOfWeek[currentDay]
                + "\n\tDisplayed popup: " + displayedPopup + "\n\tDisplayed subgoal: " + displayedSubGoal
                + "\n\tCurrent intended steps: " + currentIntendedSteps);

        saveIntendedStepsDay(ma, editor, cal);

        // saves today's goal for later graphing
        if (!met)
            saveGoalDay(ma, editor, cal);
        else
            Log.i("Goal", "Goal already met today, won't update today's goal for graph.");
    }

    public void saveGoalDay(Context ma, SharedPreferences.Editor editor, Calendar cal) {
        String today = daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1];
        Log.i("Goal", "Saving current goal of " + goal + " to " + today + " for graph.");
        editor.putInt(today + " goal", goal);
        editor.apply();
    }

    public void saveIntendedStepsDay(Context ma, SharedPreferences.Editor editor, Calendar cal) {
        String today = daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1];
        Log.i("Goal", "Saving intended steps of " + currentIntendedSteps + " to " + today + " for graph.");
        editor.putLong(today + " walks", currentIntendedSteps);
        editor.apply();
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public boolean attemptCompleteGoal(long steps){
        return steps >= goal && !met;
    }

    public boolean metToday(){
        return met;
    }

    public void meetGoal(boolean met) {
        this.met = met;
    }

    public int getGoal() {
        return goal;
    }

    public boolean canSetAutomatically() {
        int maxAutoGoal = DEFAULT_MAX_AUTO_GOAL;
        return goal + autoGoalIncr <= maxAutoGoal && useAutoGoal;
    }

    public int nextAutoGoal() {
        return canSetAutomatically() ? goal + autoGoalIncr : goal;
    }

    public void setGoal(int val) {
        goal = val;
    }

    public void setAutoGoal(boolean s) {useAutoGoal = s;}

    // whether it is time to show a subgoal
    public boolean canShowSubGoal(Calendar calendar) {
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currMinute = calendar.get(Calendar.MINUTE);
        return currHour >= 20 && !displayedSubGoal; // 8-8:20
    }

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
}
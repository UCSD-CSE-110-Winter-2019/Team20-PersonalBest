package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class Goal {
    private int goal;
    private boolean met = false;

    private static final int DEFAULT_GOAL_INCREMENT = 500;
    private static final int INITIAL_GOAL = 2000;
    private static final int DEFAULT_MAX_AUTO_GOAL = 15000;

    private int autoGoalIncr = DEFAULT_GOAL_INCREMENT;
    private int maxAutoGoal = DEFAULT_MAX_AUTO_GOAL;

    // makes a goal based on the saved shared preferences
    public Goal(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        goal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getInt("savedGoal", INITIAL_GOAL);
        met = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("metToday", false);
    }

    // custom goal
    public Goal(int steps, boolean met) {
        goal = steps;
        this.met = met;
    }

    public Goal(int steps, boolean met, int nextautogoal){
        goal = steps;
        autoGoalIncr = nextautogoal;
        this.met = met;
    }

    // saves goal values to sharedpreferences
    public void save(Context ma) {
        SharedPreferences sharedpreferences = ma.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("savedGoal", goal);
        //editor.putBoolean("metToday", met);
        editor.apply();
    }

    public boolean attemptCompleteGoal(long steps){
        return steps >= goal && !met;
    }

    public boolean metToday(){
        return met;
    }

    public void meetGoal() {
        met = true;
    }

    public int getGoal() {
        return goal;
    }

    public boolean canSetAutomatically() {
        return goal + autoGoalIncr <= maxAutoGoal;
    }

    public int nextAutoGoal() {
        return canSetAutomatically() ? goal + autoGoalIncr : goal;
    }

    public void setGoal(int val) {
        goal = val;
    }

    // whether it is time to show a subgoal
    public boolean canShowSubGoal(Calendar calendar) {
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currMinute = calendar.get(Calendar.MINUTE);
        return currHour == 20 && currMinute == 0; // 8 pm
    }

    public void displaySubGoal(Context context, int steps, int yesterdaySteps) {
        if (steps >= yesterdaySteps + 500) {
            // round down to nearest 500 steps
            int diff = (steps - yesterdaySteps) / 500 * 500;
            Toast.makeText(context, "You got about " + diff + " more steps than yesterday!", Toast.LENGTH_LONG).show();
        }
    }
}
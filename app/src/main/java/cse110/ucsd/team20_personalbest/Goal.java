package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Goal {
    private int goal;
    private boolean met = false;
    private boolean changeIgnored = false;

    private static final int GOAL_INCREMENT = 500;
    private static final int INITIAL_GOAL = 5000;
    private static final int MAX_AUTO_GOAL = 15000;

    // makes a goal based on the saved shared preferences
    public Goal(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("prefs", MODE_PRIVATE);
        goal = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getInt("savedGoal", INITIAL_GOAL);
        met = context.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("metToday", false);
    }

    // custom goal
    public Goal(int steps) {
        goal = steps;
    }

    public boolean attemptCompleteGoal(long steps){
        if(steps > goal && !met){
            met = true;
            return true;
        }else if (steps < goal)
            met = false;
        return false;
    }

    public boolean metToday(){
        return met;
    }

    public int getGoal() {
        return goal;
    }

    public boolean canSetAutomatically() {
        return goal + GOAL_INCREMENT <= MAX_AUTO_GOAL;
    }

    public int nextAutoGoal() {
        return canSetAutomatically() ? goal + GOAL_INCREMENT : goal;
    }

    public void setGoal(int val) {
        goal = val;
    }

    // once ignored, don't change goal for 1 week
    public void ignore() {
        changeIgnored = true;
    }

    public boolean isIgnored() {
        return changeIgnored;
    }

}
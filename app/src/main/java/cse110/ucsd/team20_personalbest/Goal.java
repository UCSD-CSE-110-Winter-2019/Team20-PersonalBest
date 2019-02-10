package cse110.ucsd.team20_personalbest;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Goal {
    private int goal;
    private boolean met = false;
    private boolean changeIgnored = false;

    private static final int DEFAULT_GOAL_INCREMENT = 500;
    private static final int INITIAL_GOAL = 5000;
    private static final int MAX_AUTO_GOAL = 15000;

    private int autoGoalIncr = DEFAULT_GOAL_INCREMENT;

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
    public void meetGoal() {
        met = true;
    }

    public int getGoal() {
        return goal;
    }

    public boolean canSetAutomatically() {
        return goal + autoGoalIncr <= MAX_AUTO_GOAL;
    }

    public int nextAutoGoal() {
        return canSetAutomatically() ? goal + autoGoalIncr : goal;
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
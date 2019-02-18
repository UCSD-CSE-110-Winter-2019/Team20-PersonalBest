package cse110.ucsd.team20_personalbest;

import android.util.Log;

public class BoundValidity {
    // inclusive
    private final int FEET_LOWER_BOUND = 0;
    private final int INCHES_LOWER_BOUND = 0;
    private final int GOAL_LOWER_BOUND = 0;

    // exclusive
    private final int FEET_UPPER_BOUND = 8;
    private final int INCHES_UPPER_BOUND = 12;
    private final int AUTO_GOAL_UPPER_BOUND = 15001;
    private final int MANUAL_GOAL_UPPER_BOUND = 50001;

    private final String TAG = "BoundValidity";

    BoundValidity () { }

    public boolean feetAndInches(int feet, int inches) {
        boolean valid = feet >= FEET_LOWER_BOUND && inches >= INCHES_LOWER_BOUND && feet < FEET_UPPER_BOUND && inches < INCHES_UPPER_BOUND;
        Log.d(TAG, "Checking validity of entered feet and inches.");
        return valid;
    }

    public boolean manualGoal(int goal) {
        boolean valid = goal >= GOAL_LOWER_BOUND && goal < MANUAL_GOAL_UPPER_BOUND;
        Log.d(TAG, "Checking validity of entered manual goal.");
        return valid;
    }

    public boolean autoGoal(int goal) {
        boolean valid = goal >= GOAL_LOWER_BOUND && goal < AUTO_GOAL_UPPER_BOUND;
        Log.d(TAG, "Checking validity of auto goal.");
        return valid;
    }
}

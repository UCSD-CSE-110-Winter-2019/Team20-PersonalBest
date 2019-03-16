package cse110.ucsd.team20_personalbest.goal;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public interface SharedPrefsManagerInterface {
    void resetMetAndDisplayYesterdaysPopup(Calendar cal);

    // saves goal values to sharedpreferences
    void save(Context ma, Calendar cal);
    void saveGoalDay(Context ma, SharedPreferences.Editor editor, Calendar cal);
    void saveIntendedStepsDay(Context ma, SharedPreferences.Editor editor, Calendar cal);
    void displaySubGoal(Context context, int steps, int yesterdaySteps);
}

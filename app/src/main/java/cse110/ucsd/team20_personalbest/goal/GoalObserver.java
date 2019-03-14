package cse110.ucsd.team20_personalbest.goal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import cse110.ucsd.team20_personalbest.MainActivity;
import cse110.ucsd.team20_personalbest.R;
import cse110.ucsd.team20_personalbest.goal.Goal;

public class GoalObserver implements Observer {

    private Goal goal;
    private MainActivity mainActivity;
    private boolean doneCalculatingYesterdaySteps;

    public GoalObserver(Goal gl, MainActivity ma){
        goal = gl;
        mainActivity = ma;
    }

    @Override
    public void update(Observable observable, Object o) {
        int currentsteps = (int) o;

        Log.d("Goal Observer", "Popup checks\n\tWill display a popup when on dashboard: " + (goal.attemptCompleteGoal(currentsteps) && (!goal.metToday() || !goal.getMeetOnce()) && !goal.getIgnored())
         + "\n\tMeetOnce: " + goal.getMeetOnce()
         + "\n\tDashboard visible: " + mainActivity.isDashboardVisible()
         + "\n\tGoal ignored today: " + goal.getIgnored());

        // if goal has been achieved and a goal hasn't already been met today or was met yesterday and not displayed
        if(goal.getPopupForYesterday()){

            mainActivity.sendToast("Congratulations! Yesterday, you met your goal of " + goal.getGoal() + " steps!");

            // popup was for yesterday, display false
            goal.setDisplayedPopup(false);
            goal.meetGoal(false);
            goal.setPopupForYesterday(false);
            goal.setPopupCurrentlyOpen(true);

            Log.i("Goal", "Toast message for meeting yesterday's goal.");

            // popup where user can choose automatic goal or set a manual goal
            createDialog();
        }

        // can display goal multiple times if the getMeetOnce is on
        else if(goal.attemptCompleteGoal(currentsteps) && (!goal.metToday() || !goal.getMeetOnce()) && mainActivity.isDashboardVisible() && !goal.getIgnored()){

            mainActivity.sendToast("Congratulations! You met your goal of " + goal.getGoal() + " steps!");

            // set displayed popup and met goal to true if the popup was for today's goal
            goal.setDisplayedPopup(true);
            goal.meetGoal(true);
            goal.setPopupForYesterday(false);
            goal.setPopupCurrentlyOpen(true);

            Log.i("Goal", "Popup for today's goal.");

            // popup where user can choose automatic goal or set a manual goal
            createDialog();
        }

        // current time
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        // gets yesterday's steps
        if (!doneCalculatingYesterdaySteps)
            doneCalculatingYesterdaySteps = mainActivity.setYesterdaySteps(cal);
        int yesterdaySteps = mainActivity.getYesterdaySteps();

        // if goal has not been completed today, display toast encouragement at 8pm
        if (goal.canShowSubGoal(cal) && !goal.metToday()) {
            if (doneCalculatingYesterdaySteps) {
                goal.displaySubGoal(mainActivity, currentsteps, yesterdaySteps);
                goal.setDisplayedSubGoal(true);
                goal.save(mainActivity, cal);
                Log.i("SubGoal","Displayed sub goal toast");
            } else {
                Log.i("SubGoal","Yesterday's steps not done calculating.");
            }
        }
    }

    private void createDialog() {

        String TAG = "Goal Popup";
        Log.i(TAG, "Popup being created");

        // creates the pop up
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("You met your goal!");
        builder.setMessage("Set a new goal?");

        // uses custom xml file
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.pop_up_layout, null);
        builder.setView(dialogLayout);
        builder.setCancelable(false);

        // gets references to buttons
        final RadioButton autoRadio = (RadioButton) dialogLayout.findViewById(R.id.goal_recommended_value);
        autoRadio.setText("Recommended Goal: " + goal.nextAutoGoal());
        final RadioButton customRadio = dialogLayout.findViewById(R.id.goal_custom_value);
        final EditText customText = dialogLayout.findViewById(R.id.goal_custom_value_edit_text);

        customText.setHint("" + goal.getGoal());

        // checks whether we can recommend a new automatic goal
        if (goal.canSetAutomatically()) {
            autoRadio.setChecked(true);
            Log.i(TAG, "Goal can be set automatically");
        }
        else {
            autoRadio.setEnabled(false);
            customRadio.setChecked(true);
            Log.i(TAG, "Goal cannot be set automatically");
        }

        builder.setPositiveButton("Set goal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String TAG = "Goal Popup/Set goal";
                int newGoal = goal.getGoal();
                Log.i(TAG, "Goal is being set");

                // user chose automatic goal
                if (autoRadio.isChecked())
                    newGoal = goal.nextAutoGoal();
                else {
                    String goalText = customText.getText().toString();
                    if (!goalText.isEmpty())
                        newGoal = Integer.parseInt(goalText);
                }

                goal.setGoal(newGoal);
                goal.meetGoal(true);
                mainActivity.setGoalCount((goal.getGoal()));
                goal.save(mainActivity, Calendar.getInstance());

                Log.i(TAG, "Goal cannot be met today any more \nnew goal has been created \ntime of goal has been saved  \nui updated");

                Toast.makeText(mainActivity, "New goal set to " + newGoal + "!", Toast.LENGTH_LONG).show();
                goal.setPopupCurrentlyOpen(false);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //goal.ignore();
                Toast.makeText(mainActivity, "Kept old goal.", Toast.LENGTH_LONG).show();

                goal.meetGoal(true);
                goal.setIgnored(true);
                goal.save(mainActivity, Calendar.getInstance());
                Log.i("Goal Popup/Ignore", "Goal cannot be met today any more \ngoal has been saved");
                goal.setPopupCurrentlyOpen(false);
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }
}

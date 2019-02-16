package cse110.ucsd.team20_personalbest;

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

public class GoalObserver implements Observer {

    private Goal goal;
    private MainActivity mainActivity;

    private boolean subGoalDisplayed;

    GoalObserver(Goal gl, MainActivity ma){
        goal = gl;
        mainActivity = ma;
        subGoalDisplayed = false;
    }

    @Override
    public void update(Observable observable, Object o) {
        //System.out.println("Updating observer");
        int currentsteps = (int) o;

        //System.out.println("Goal of " + currentsteps + "/" + goal.getGoal() + " complete: " + goal.attemptCompleteGoal(currentsteps));

        // if goal has been achieved and a goal hasn't already been met today or was met yesterday and not displayed
        if(goal.popupForYesterday){
            mainActivity.sendToast("Congratulations! Yesterday, you met your goal of " + goal.getGoal() + " steps!");

            // popup was for yesterday, display false
            goal.displayedPopup = false;
            goal.meetGoal(false);
            goal.popupForYesterday = false;

            // popup where user can choose automatic goal or set a manual goal
            createDialog();
        }

        else if(goal.attemptCompleteGoal(currentsteps) && !goal.metToday()){

            mainActivity.sendToast("Congratulations! You met your goal of " + goal.getGoal() + " steps!");

            // set displayed popup and met goal to true if the popup was for today's goal
            goal.displayedPopup = true;
            goal.meetGoal(true);
            goal.popupForYesterday = false;

            // popup where user can choose automatic goal or set a manual goal
            createDialog();
        }

        // current time
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 00);

        int yesterdaySteps = mainActivity.getYesterdaySteps();

        // if goal has not been completed today, display toast encouragement at 8pm
        if (goal.canShowSubGoal(cal) && !goal.metToday() && !subGoalDisplayed) {
            if (mainActivity.getStepsDone) {
                goal.displaySubGoal(mainActivity, currentsteps, yesterdaySteps);
                mainActivity.getStepsDone = false;
                subGoalDisplayed = true;
            } else {
                Log.i("SubGoal","Yesterday's steps not done calculating.");
            }
        }
    }

    private void createDialog() {

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
        }
        else {
            autoRadio.setEnabled(false);
            customRadio.setChecked(true);
        }

        builder.setPositiveButton("Set goal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int newGoal = goal.getGoal();

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

                Toast.makeText(mainActivity, "New goal set to " + newGoal + "!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //goal.ignore();
                Toast.makeText(mainActivity, "Kept old goal!", Toast.LENGTH_LONG).show();

                goal.meetGoal(true);
                goal.save(mainActivity, Calendar.getInstance());
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }
}

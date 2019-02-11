package cse110.ucsd.team20_personalbest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class GoalObserver implements Observer {

    private Goal goal;
    private MainActivity mainActivity;

    public GoalObserver(Goal gl, MainActivity ma){
        goal = gl;
        mainActivity = ma;
    }

    @Override
    public void update(Observable observable, Object o) {
        //System.out.println("Updating observer");
        int currentsteps = (int) o;

        //System.out.println("Goal of " + currentsteps + "/" + goal.getGoal() + " complete: " + goal.attemptCompleteGoal(currentsteps));

        // if goal has been achieved and a goal hasn't already been met today
        if(goal.attemptCompleteGoal(currentsteps)){

            mainActivity.sendToast("Congratulations! You met your goal of " + goal.getGoal() + " steps!");
            goal.meetGoal();

            // popup where user can choose automatic goal or set a manual goal
            createDialog();
        }
    }

    public void createDialog() {

        // creates the pop up
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("You met your goal!");
        builder.setMessage("Set a new goal?");

        // uses custom xml file
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.pop_up_layout, null);
        builder.setView(dialogLayout);

        // gets references to buttons
        final RadioButton autoRadio = (RadioButton) dialogLayout.findViewById(R.id.goal_recommended_value);
        autoRadio.setText("Recommended Goal: " + goal.nextAutoGoal());
        final RadioButton customRadio = (RadioButton) dialogLayout.findViewById(R.id.goal_custom_value);
        final EditText customText = (EditText) dialogLayout.findViewById(R.id.goal_custom_value_edit_text);

        // checks whether we can recommend a new automatic goal
        if (goal.canSetAutomatically()) {
            autoRadio.setChecked(true);
        }
        else {
            autoRadio.setEnabled(false);
            customRadio.setChecked(true);
            customText.setHint("15000");
        }

        builder.setPositiveButton("Set goal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int newGoal;

                // user chose automatic goal
                if (autoRadio.isChecked())
                    newGoal = goal.nextAutoGoal();
                else
                    newGoal = Integer.parseInt(customText.getText().toString());

                goal.setGoal(newGoal);
                goal.meetGoal();
                mainActivity.setGoalCount((goal.getGoal()));
                goal.save(mainActivity);

                Toast.makeText(mainActivity, "New goal set to " + newGoal + "!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //goal.ignore();
                Toast.makeText(mainActivity, "Kept old goal!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();

    }
}

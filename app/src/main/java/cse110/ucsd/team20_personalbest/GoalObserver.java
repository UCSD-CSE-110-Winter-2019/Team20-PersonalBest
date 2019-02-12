package cse110.ucsd.team20_personalbest;

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

        if(goal.attemptCompleteGoal(currentsteps)){
            mainActivity.sendToast("Congratulations on meeting your goal of " + goal.getGoal() + " steps!");
            //Put popup for auto goal here; follow lines will update goal and ui
            goal.setGoalAutomatically();
            mainActivity.setGoalCount((goal.getGoal()));
        }
    }
}

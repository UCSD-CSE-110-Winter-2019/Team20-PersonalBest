package cse110.ucsd.team20_personalbest;

public class Goal {
    private int goal;
    private boolean notMet = true;
    private int autoGoal = 500;

    public Goal(int steps) {
        goal = steps;
    }

    public Goal(int steps, int nextautogoal){
        goal = steps;
        autoGoal = nextautogoal;

    }

    public boolean attemptCompleteGoal(long steps){
        if(steps > goal && notMet){
            notMet = false;
            return true;
        }else if (steps < goal){
            notMet = true;
            return false;
        } else
            return false;
    }

    public boolean metToday(){
        return !notMet;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoalAutomatically(){
        goal = goal + autoGoal;
    }

    public void setGoal(int val) {
        goal = val;
    }


}
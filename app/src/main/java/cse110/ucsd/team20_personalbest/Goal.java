package cse110.ucsd.team20_personalbest;

public class Goal {
    private int goal;
    private boolean notMet = true;
    private int autoGoal = 500;
    boolean useAutoGoal = true;

    public Goal(int steps, boolean notMet) {
        goal = steps;
        this.notMet = notMet;
    }

    public Goal(int steps, boolean notMet, int nextautogoal){
        goal = steps;
        autoGoal = nextautogoal;
        this.notMet = notMet;
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
        //TODO handle goal cap of 15k
        goal = goal + autoGoal;
    }

    public void setGoal(int val) {
        goal = val;
    }

    public void setAutoGoal(boolean s) {useAutoGoal = s;}


}
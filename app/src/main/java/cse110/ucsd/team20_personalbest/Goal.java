package cse110.ucsd.team20_personalbest;

public class Goal {
    private int goal;
    private boolean notMet;

    public Goal(int steps) {
        goal = steps;
        notMet = false;
    }

    public boolean attemptCompleteGoal(long steps){
        if(steps > goal && notMet){
            notMet = true;
            return true;
        }else{
            notMet = false;
            return false;
        }
    }

    public boolean metToday(){
        return !notMet;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int val) {
        goal = val;
    }
}
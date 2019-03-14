package cse110.ucsd.team20_personalbest.walk;

import java.util.Observable;

public class StepContainer extends Observable {

    private int steps;

    public StepContainer(int st){
        steps = st;
    }

    public StepContainer(){
        steps = 0;
    }

    public int steps(){
        return steps;
    }

    public void setSteps(int newsteps){
        //System.out.println("Setting steps");
        steps = newsteps;
        setChanged();
        notifyObservers(newsteps);
    }
}

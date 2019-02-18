package cse110.ucsd.team20_personalbest.fitness;

import cse110.ucsd.team20_personalbest.MainActivity;

import cse110.ucsd.team20_personalbest.fitness.FitnessService;


public class MockFitness implements FitnessService {
    private int steps = 0;
    private MainActivity activity;

    public MockFitness(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getRequestCode() {
        return 0;
    }

    @Override
    public void setup() {
        //No setup necessary
    }

    public void setSteps(int s){
        steps = s;
    }

    @Override
    public void updateStepCount() {
        activity.setStepCount(300);
    }
}

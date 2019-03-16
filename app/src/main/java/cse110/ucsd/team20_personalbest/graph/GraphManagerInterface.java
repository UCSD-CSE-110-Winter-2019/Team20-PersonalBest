package cse110.ucsd.team20_personalbest.graph;

import java.util.ArrayList;

import cse110.ucsd.team20_personalbest.goal.GoalDataRequestManager;

public interface GraphManagerInterface {
    void setGoalRequestManager(GoalDataRequestManager goalDataRequestManager);
    void draw();
    void formatData();
    void updateIntendedData(ArrayList<Integer> newData);
    void updateUnintendedData(ArrayList<Integer> newData);
}

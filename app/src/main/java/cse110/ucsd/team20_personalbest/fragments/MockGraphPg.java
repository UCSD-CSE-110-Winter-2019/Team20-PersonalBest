package cse110.ucsd.team20_personalbest.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import cse110.ucsd.team20_personalbest.goal.GoalDataRequestManager;
import cse110.ucsd.team20_personalbest.history.DailyStepCountHistory;
import cse110.ucsd.team20_personalbest.walk.SessionDataRequestManager;

public class MockGraphPg implements GraphPgInterface {

    SessionDataRequestManager sessionDataRequestManager;
    DailyStepCountHistory dailyStepCountHistory;
    GoalDataRequestManager goalDataRequestManager;
    int currentTime;
    int numOfDays = 7;

    public MockGraphPg(int currentTime, GoalDataRequestManager goalDataRequestManager, DailyStepCountHistory dailyStepCountHistory,
                       SessionDataRequestManager sessionDataRequestManager) {
        this.dailyStepCountHistory =dailyStepCountHistory;
        this.goalDataRequestManager = goalDataRequestManager;
        this.sessionDataRequestManager = sessionDataRequestManager;
        this.currentTime = currentTime;
    }

    @Override
    public void setValues() {

        goalDataRequestManager.requestGoals(currentTime, numOfDays);

        dailyStepCountHistory.requestHistory(currentTime, numOfDays);
        sessionDataRequestManager.requestSessions(currentTime, numOfDays);
    }
}

package cse110.ucsd.team20_personalbest.ms2tests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;

import cse110.ucsd.team20_personalbest.activities.MockPopupFriendRequest;
import cse110.ucsd.team20_personalbest.activities.PopupFriendRequestInterface;
import cse110.ucsd.team20_personalbest.fragments.GraphPg;
import cse110.ucsd.team20_personalbest.fragments.GraphPgInterface;
import cse110.ucsd.team20_personalbest.fragments.MockGraphPg;
import cse110.ucsd.team20_personalbest.friends.FBCommandCenter;
import cse110.ucsd.team20_personalbest.goal.GoalDataRequestManager;
import cse110.ucsd.team20_personalbest.history.DailyStepCountHistory;
import cse110.ucsd.team20_personalbest.walk.SessionDataRequestManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SevenDayGraphTest {

    GraphPgInterface fragment;
    GoalDataRequestManager goalDataRequestManager;
    SessionDataRequestManager sessionDataRequestManager;
    DailyStepCountHistory dailyStepCountHistory;

    @Test
    public void test_sevenDay() {

        goalDataRequestManager = mock(GoalDataRequestManager.class);
        sessionDataRequestManager = mock(SessionDataRequestManager.class);
        dailyStepCountHistory = mock(DailyStepCountHistory.class);
        int currentTime = 0;

        fragment = new MockGraphPg(currentTime, goalDataRequestManager, dailyStepCountHistory, sessionDataRequestManager);

        fragment.setValues();

        // gets data from past 7 days starting at currentTime
        verify(goalDataRequestManager).requestGoals(currentTime,7);
        verify(sessionDataRequestManager).requestSessions(currentTime, 7);
        verify(dailyStepCountHistory).requestHistory(currentTime,7);
    }

}

package cse110.ucsd.team20_personalbest.ms2tests;

import android.content.Intent;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import cse110.ucsd.team20_personalbest.MainActivity;
import cse110.ucsd.team20_personalbest.activities.GraphMessageActivity;
import cse110.ucsd.team20_personalbest.fragments.GraphPgInterface;
import cse110.ucsd.team20_personalbest.fragments.MockGraphPg;
import cse110.ucsd.team20_personalbest.goal.GoalDataRequestManager;
import cse110.ucsd.team20_personalbest.graph.GraphManager;
import cse110.ucsd.team20_personalbest.history.ArrayToHistoryConverter;
import cse110.ucsd.team20_personalbest.history.DailyStepCountHistory;
import cse110.ucsd.team20_personalbest.walk.SessionDataRequestManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class MonthlySummaryTest {
    GraphMessageActivity graphActivity;
    MainActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(MainActivity.class);
        FirebaseApp.initializeApp(activity);

        Intent intent = new Intent(RuntimeEnvironment.application, GraphMessageActivity.class);
        intent.putExtra("FACTORY_KEY", "mock");
        intent.putExtra("friendEmail", "user2@team20pb.com");
        intent.putExtra("myEmail", "user1");

        graphActivity = Robolectric.buildActivity(GraphMessageActivity.class, intent).create().get();
    }

    @Test
    public void test_monthlyGraph() {

        GraphManager graphManager = mock(GraphManager.class);
        ArrayToHistoryConverter arrayToHistoryConverter = mock(ArrayToHistoryConverter.class);

        graphActivity.mockUpdate(graphManager, arrayToHistoryConverter);

        verify(arrayToHistoryConverter).getUnintendedSteps();
        verify(arrayToHistoryConverter).getIntendedSteps();
        verify(arrayToHistoryConverter).getGoal();

        verify(graphManager).updateUnintendedData(any());
        verify(graphManager).updateIntendedData(any());
        verify(graphManager).updateGoalData(any());

        verify(graphManager).draw();
    }
}

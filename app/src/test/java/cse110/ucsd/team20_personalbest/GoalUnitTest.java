package cse110.ucsd.team20_personalbest;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class GoalUnitTest {

    Goal goal;

    @Before
    public void setup() {
        goal = new Goal(200, false, 20);
    }

    @Test
    public void test_attemptCompleteGoal() {
        // goal not met
        int steps = 100;
        boolean canComplete = goal.attemptCompleteGoal(steps);
        assertFalse(canComplete);

        // goal met
        steps = 200;
        canComplete = goal.attemptCompleteGoal(steps);
        assertTrue(canComplete);

        // goal exceeded
        steps = 220;
        canComplete = goal.attemptCompleteGoal(steps);
        assertTrue(canComplete);

        // goal has already been met today
        goal.meetGoal(true);
        steps = 200;
        canComplete = goal.attemptCompleteGoal(steps);
        assertFalse(canComplete);
    }


    @Test
    public void test_canSetAutomatically() {
        // can set
        boolean canSet = goal.canSetAutomatically();
        assertTrue(canSet);

        // new goal larger than max
        goal.setGoal(15000);
        canSet = goal.canSetAutomatically();
        assertFalse(canSet);
    }

    @Test
    public void test_nextAutoGoal() {
        int nextGoal = goal.nextAutoGoal();
        assertEquals(nextGoal, 220);

        goal.setGoal(15000);
        nextGoal = goal.nextAutoGoal();
        assertEquals(nextGoal, 15000);
    }

    @Test
    public void test_canShowSubGoal() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 0);
        boolean canShow = goal.canShowSubGoal(c);
        assertTrue(canShow);

        c.set(Calendar.HOUR_OF_DAY, 2);
        canShow = goal.canShowSubGoal(c);
        assertFalse(canShow);

        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 1);
        canShow = goal.canShowSubGoal(c);
        assertFalse(canShow);
    }
}

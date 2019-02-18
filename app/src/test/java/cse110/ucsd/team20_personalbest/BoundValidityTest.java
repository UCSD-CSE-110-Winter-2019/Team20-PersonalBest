package cse110.ucsd.team20_personalbest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoundValidityTest {

    BoundValidity valid;
    int feet;
    int inches;
    int goal;

    @Before
    public void setup() {
        valid = new BoundValidity();
        feet = 5;
        inches = 10;
        goal = 5000;
    }

    @Test
    public void test_feetAndInches() {
        assertTrue(valid.feetAndInches(feet, inches));

        feet = 8;
        assertFalse(valid.feetAndInches(feet, inches));

        feet = 5;
        inches = 12;
        assertFalse(valid.feetAndInches(feet, inches));

        feet = -1;
        inches = 7;
        assertFalse(valid.feetAndInches(feet, inches));

        feet = 7;
        inches = 11;
        assertTrue(valid.feetAndInches(feet, inches));

        feet = 0;
        inches = 0;
        assertTrue(valid.feetAndInches(feet, inches));
    }

    @Test
    public void test_manualGoal() {
        assertTrue(valid.manualGoal(goal));

        goal = 0;
        assertTrue(valid.manualGoal(goal));

        goal = 50000;
        assertTrue(valid.manualGoal(goal));

        goal = -1;
        assertFalse(valid.manualGoal(goal));

        goal = 50001;
        assertFalse(valid.manualGoal(goal));
    }

    @Test
    public void test_autoGoal() {
        assertTrue(valid.autoGoal(goal));

        goal = 0;
        assertTrue(valid.autoGoal(goal));

        goal = 15000;
        assertTrue(valid.autoGoal(goal));

        goal = -1;
        assertFalse(valid.autoGoal(goal));

        goal = 15001;
        assertFalse(valid.autoGoal(goal));
    }
}

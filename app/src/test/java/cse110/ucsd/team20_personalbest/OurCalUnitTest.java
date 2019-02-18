package cse110.ucsd.team20_personalbest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Calendar;

public class OurCalUnitTest {
    OurCal ourCal;
    Calendar testCal;

    @Before
    public void setup() {
        testCal = Calendar.getInstance();
        testCal.setTimeInMillis(100000000);
        ourCal = new OurCal(testCal, 0);
    }

    @Test
    public void test_getTime() {

        assertEquals(ourCal.getTime(), 100000000);

        ourCal.setTimeDiff(100);
        assertEquals(ourCal.getTime(), 100000000);
    }

    @Test
    public void test_setCal() {

        ourCal.setCal(testCal);
        assertEquals(ourCal.getTime(), 100000000);

        testCal.setTimeInMillis(10000);
        ourCal.setTimeDiff(100);
        ourCal.setCal(testCal);
        assertEquals(ourCal.getTime(), 9900);
    }
}

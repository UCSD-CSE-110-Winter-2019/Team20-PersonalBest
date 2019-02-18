package cse110.ucsd.team20_personalbest;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentController;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;


public class WalkTest {

    private Walk newWalk;
    private RTWalk newRTWalk;
    private WalkPg newWalkPg;

    private int height = 70;
    private Calendar now = Calendar.getInstance();
    private Calendar OneHoutLater = Calendar.getInstance();

    @Test
    public void TestRTWalk() {
        newRTWalk = new RTWalk(height,now);
        assertEquals("Steps:  0\nDistance:  0.00 Miles\nSpeed: 0.00 MPH", newRTWalk.getStat());

        OneHoutLater.set(Calendar.HOUR, OneHoutLater.get(Calendar.HOUR) + 1);
        newRTWalk.updateStat(2000, OneHoutLater);
        assertEquals("Steps:  2000\nDistance:  0.91 Miles\nSpeed: 0.91 MPH", newRTWalk.getStat());
    }

    @Test
    public void TestWalk() {
        newRTWalk = new RTWalk(height,now);
        OneHoutLater.set(Calendar.HOUR, OneHoutLater.get(Calendar.HOUR) + 1);
        newRTWalk.updateStat(2000, OneHoutLater);
        newWalk = new Walk(newRTWalk.getStat(), now);
        assertEquals("Steps:  2000\nDistance:  0.91 Miles\nSpeed: 0.91 MPH", newWalk.getStat());

        MockCalendar mc = new MockCalendar(11,8,2019,10,13,0);
        newWalk = new Walk(newRTWalk.getStat(),mc.getInstance());
        Calendar mc1 = mc;
        System.out.println(mc1.get(mc.MONTH));
        assertEquals("08 /11 / 2019\n10:13 AM", newWalk.getTime());
    }

    @Test
    public void test_getTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1420468800000l);
        Walk walk = new Walk("Stats...", cal);
        String timeString = walk.getTime();
        String time = "01 /05 / 2015\n06:40 AM";
        assertEquals(timeString, time);
    }
}
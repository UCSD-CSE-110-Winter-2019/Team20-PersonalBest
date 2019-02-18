package cse110.ucsd.team20_personalbest;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class OurCal {

    Calendar cal;
    long timeDiff;

    public OurCal(Calendar cal, int timeDiff) {
        this.cal = cal;
        this.timeDiff = timeDiff;
    }

    // sets calendar variable's time to the time in the Calendar
    public void setCal(Calendar c) {
        Log.d("Time", "Updating calender to the current time: " + c.getTimeInMillis());

        cal.setTimeInMillis(c.getTimeInMillis() - timeDiff);
    }

    public void setTimeDiff(long td) {
        timeDiff = td;
    }

    public void setToCurrentTime() {
        cal = Calendar.getInstance();
    }

    public Calendar getCal() {
        return cal;
    }

    public long getTime() {
        Log.d("Time", "Getting time from calendar: " + cal.getTimeInMillis());

        return cal.getTimeInMillis();
    }

    public static long getTime(Calendar c) {
        Log.d("Time", "Getting time from calendar: " + c.getTimeInMillis());

        return c.getTimeInMillis();
    }

}

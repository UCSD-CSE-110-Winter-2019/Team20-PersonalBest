package cse110.ucsd.team20_personalbest;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class Walk {

    private String stat;
    private Calendar start;

    public Walk(String stat, Calendar st) {
        start = st;
        this.stat = stat;
    }

    public String getStat() {
        return stat;
    }

    public String getTime() {
        return String.format("%s /%s / %d\n%s:%s %s",
                start.get(start.MONTH) + 1 <10 ?
                        String.format("%02d", start.get(start.MONTH) + 1) :
                        start.get(start.MONTH) + 1,
                start.get(start.DATE)<10 ?
                        String.format("%02d", start.get(start.DATE)) :
                        start.get(start.DATE),
                start.get(start.YEAR),
                start.get(start.HOUR)<10 ?
                        String.format("%02d", start.get(start.HOUR)) :
                        start.get(start.HOUR),
                start.get(start.MINUTE)<10 ?
                        String.format("%02d", start.get(start.MINUTE)) :
                        start.get(start.MINUTE)
                , start.get(start.AM_PM) == start.AM ? "AM" : "PM");
    }

}

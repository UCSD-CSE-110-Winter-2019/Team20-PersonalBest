package cse110.ucsd.team20_personalbest;

import java.util.Calendar;

public class RTWalk {

    private int step;
    private int height;
    private double distMile, mph;
    private Calendar start;

    public RTWalk(int h, Calendar st) {
        height = h;
        start = st;
    }

    public void updateStat(int s, Calendar now) {
        step = s;
        distMile = s * (0.413 * height) / 12.000;
        mph = distMile / ((now.getTimeInMillis() - start.getTimeInMillis()) / 1000.000);

    }

    public String getStat() {
        return String.format("Steps:  %s\nDistance:  %.2f Miles\nSpeed: %.2f MPH",step,distMile * 0.000189393939,mph / 1.466);
    }
}

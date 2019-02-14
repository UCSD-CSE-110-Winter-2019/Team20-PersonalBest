package cse110.ucsd.team20_personalbest;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class Walk {

        //private int steps;
        //private double distMile, mph;
        private String stat;
        private Calendar start;
        //private Calendar end;


        public Walk(String stat, Calendar st) {
            //steps = s;
            start = st;
            this.stat = stat;
            //end = en;
            //distMile = s * (0.413 * h) / 12.000;
            //mph = distMile / ((en.getTimeInMillis() - st.getTimeInMillis()) / 1000.000);
        }

        /*public String getSteps() {
            return NumberFormat.getNumberInstance(Locale.US).format(steps);
        }

        public double getDist () {
            return distMile;
        }

        public double getSpeed() {
            return mph;
        }*/

        public String getStat() {
            //return String.format("Steps:  %s\nDistance:  %.2f Miles\nSpeed: %.2f MPH",getSteps(),getDist() * 0.000189393939,getSpeed() / 1.466);
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
                    , start.get(start.AM_PM) == Calendar.AM ? "AM" : "PM");
        }

}

package cse110.ucsd.team20_personalbest;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.time.*;

public class CalcHelper {

    protected class walk {
        private int height, steps, distMile, mph, startTime, endTime;
        private Calendar start;
        private Calendar end;


        public walk(int h, int s, Calendar st, Calendar en) {
            steps = s;
            distMile = (int)(s * (0.413 * h) / 12 * 0.000189393939);
            start = st;
            end = en;
            mph = (int)(distMile / ((en.getTimeInMillis() - st.getTimeInMillis())/1000/60/60));
        }

        public String getSteps() {
            return NumberFormat.getNumberInstance(Locale.US).format(steps);
        }

        public int getDist () {
            return distMile;
        }

        public int getSpeed() {
            return mph;
        }

        public String getDate() {
            return String.format("%d / %d / %d",start.get(start.DATE), start.get(start.MONTH), start.get(start.YEAR));
        }

        public String getTime() {
            return String.format("%d:%d %s", start.get(start.HOUR), start.get(start.MINUTE), start.get(start.AM_PM) == Calendar.AM? "AM": "PM");
        }
    }
}

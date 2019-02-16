package cse110.ucsd.team20_personalbest;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.time.*;

public class Walk {

        private int height, steps, distMile, mph, startTime, endTime;
        private Calendar start;
        private Calendar end;


        public Walk(int h, int s, Calendar st, Calendar en) {
            steps = s;
            distMile = (int)(s * (0.413 * h) / 12 * 0.000189393939);
            start = st;
            end = en;
            mph = (int)(distMile / (((en.getTimeInMillis()+3600000) - st.getTimeInMillis())/1000/60/60));
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

        public String getStat() {
            return String.format("Steps:  %s\nDistance:  %d miles\nSpeed: %d mph",getSteps(),getDist(),getSpeed());
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

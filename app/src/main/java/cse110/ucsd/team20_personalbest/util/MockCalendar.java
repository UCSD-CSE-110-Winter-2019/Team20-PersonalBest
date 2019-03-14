package cse110.ucsd.team20_personalbest.util;

import java.util.Calendar;

public class MockCalendar extends Calendar {

    public static final int DATE = 5;
    public static final int MONTH = 2;
    public static final int YEAR = 1;
    public static final int HOUR = 10;
    public static final int MINUTE = 12;
    public static final int AM_PM = 9;
    private static final int AM = 0;
    private static final int PM = 1;

    private static int date;
    private static int month;
    private static int year;
    private static int hour;
    private static int minute;
    private static int ampm;
    private long timeinMillis;

    public MockCalendar (int timemillis) {
        timeinMillis = timemillis;
    }

    public MockCalendar (int date, int month, int year, int hour, int minute, int am_pm) {
        this.date = date;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.hour = hour;
        this.minute = minute;
        ampm = am_pm;
    }

    public void setTimeinMillis(long timeinMillis) {
        this.timeinMillis = timeinMillis;
    }

    public long getTimeinMillis() {
        return timeinMillis;
    }

    public static MockCalendar getInstance() {
        return new MockCalendar(date, month, year, hour, minute, ampm);
    }

    @Override
    public int get(int field) {
        switch (field) {
            case DATE:
                return date;
            case MONTH:
                return month - 1;
            case YEAR:
                return year;
            case HOUR:
                return hour;
            case MINUTE:
                return minute;
            case AM_PM:
                return ampm;
        }
        return -1;
    }

    @Override
    public int getGreatestMinimum(int field) {
        return 0;
    }

    @Override
    public int getLeastMaximum(int field) {
        return 0;
    }

    @Override
    protected void computeTime() { }

    @Override
    protected void computeFields() { }

    @Override
    public void add(int field, int amount) { }

    @Override
    public void roll(int field, boolean up) { }

    @Override
    public int getMinimum(int field) {
        return 0;
    }

    @Override
    public int getMaximum(int field) {
        return 0;
    }
}

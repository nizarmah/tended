package me.nizarmah.tended;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

public class DateTime implements Serializable {

    private LinkedList<Integer> days;
    private int startHour, startMinute, endHour, endMinute;

    public DateTime(int startHour, int startMinute, int endHour, int endMinute, LinkedList<Integer> days) throws Exception {
        if (startHour < 8 || startHour > 20) throw new Exception();
        if (endHour < 8 || endHour > 20) throw new Exception();
        if (startMinute < 0 || startMinute >= 60) throw new Exception();
        if (endMinute < 0 || endMinute >= 60) throw new Exception();
        if (startHour > endHour || (startHour == endHour && startMinute >= endMinute))
            throw new Exception();
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        Collections.sort(days);
        this.days = days;
    }

    public LinkedList<Integer> getDays() {
        return days;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public String toString() {
        char[] c = {' ', ' ', 'M', 'T', 'W', 'R', 'F'};
        String str = "";
        for (int i : days) str += c[i];
        return str + String.format(" %02d:%02d - %02d:%02d", startHour, startMinute, endHour, endMinute);
    }

    public boolean checkInTen() { // checks if it is still class time after 10 minutes
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.getTimeInMillis() + (10 * 60 * 1000));
        return c.get(Calendar.HOUR_OF_DAY) <= this.endHour && c.get(Calendar.MINUTE) <= this.endMinute;
    }

    public long getNextClass() { // gets next class time in milliseconds
        Calendar c = Calendar.getInstance();
        while (c.get(Calendar.HOUR_OF_DAY) < endHour || c.get(Calendar.MINUTE) < endMinute)
            c.setTimeInMillis(c.getTimeInMillis() + (60 * 1000));
        int day = c.get(Calendar.DAY_OF_WEEK), i = (days.indexOf(day) + 1) / days.size();
        long diff = days.get(i) - day;
        if (diff == 0) diff = 7;
        diff *= 24 * 60 * 60 * 1000L;
        diff -= (endHour * 60 * 60 * 1000L) + (endMinute * 60 * 1000L) - (startHour * 60 * 60 * 1000L) - (startMinute * 60 * 1000L);
        c.setTimeInMillis(c.getTimeInMillis() + diff);
        return c.getTimeInMillis();
    }


    public boolean isToday() {
        return days.contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }
}
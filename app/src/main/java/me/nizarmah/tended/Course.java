package me.nizarmah.tended;

import android.app.PendingIntent;

import java.io.Serializable;
import java.util.Date;

public class Course implements Serializable {

    private int skips;
    private int abscences;
    private String name;
    private DateTime time;
    private String filePath;

    public Course() {}

    public Course(String name, DateTime time, String filePath) {
        this.name = name;
        this.time = time;
        this.filePath = filePath;

        this.skips = this.time.getDays().size() * 2;
        this.abscences = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSkipsLeft() {
        return skips;
    }

    public int getAbscences() {
        return abscences;
    }

    public void setAbscent() {
        this.skips--;
        this.abscences++;
    }

    public void updateSkips() {
        this.skips = this.time.getDays().size() * 2;
    }
}

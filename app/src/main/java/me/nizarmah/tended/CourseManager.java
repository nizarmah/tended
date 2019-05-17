package me.nizarmah.tended;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class CourseManager implements Serializable {
    private ArrayList<Course> courses;

    private static CourseManager courseManager;

    private File coursesFile;
    private static String coursesFileName =  "courses";

    public CourseManager(File internalStorage) {
        coursesFile = new File(internalStorage, coursesFileName);

        this.loadCourses();
    }

    // Default Memory Instance
    // ---------------------------------------------------------------------------------------------
    public static synchronized CourseManager getInstance(File internalStorage) {
        if (courseManager == null) {
            courseManager = new CourseManager(internalStorage);
        } return courseManager;
    }
    // ---------------------------------------------------------------------------------------------

    // Add Course
    // ---------------------------------------------------------------------------------------------
    public void addCourse(Course course) {
        this.courses.add(course);

        saveCourses();
    }
    // ---------------------------------------------------------------------------------------------

    // Add Course at Position
    // ---------------------------------------------------------------------------------------------
    public void addCourse(int index, Course course) {
        this.courses.add(index, course);

        saveCourses();
    }
    // ---------------------------------------------------------------------------------------------

    // Get Course at Position
    // ---------------------------------------------------------------------------------------------
    public Course getCourse(int index) {
        return courses.get(index);
    }
    // ---------------------------------------------------------------------------------------------

    // Remove Course at Position
    // ---------------------------------------------------------------------------------------------
    public Course removeCourse(int index) {
        Course course = this.courses.remove(index);

        saveCourses();

        return course;
    }
    // ---------------------------------------------------------------------------------------------

    // Set Course at Position
    // ---------------------------------------------------------------------------------------------
    public Course setCourse(int index, Course newCourse) {
        Course course = this.courses.set(index, newCourse);

        saveCourses();

        return course;
    }
    // ---------------------------------------------------------------------------------------------

    // Get Courses
    // ---------------------------------------------------------------------------------------------
    public ArrayList<Course> getCourses() {
        return this.courses;
    }
    // ---------------------------------------------------------------------------------------------

    // Load Courses
    // ---------------------------------------------------------------------------------------------
    private void loadCourses() {
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(coursesFile));
            this.courses = (ArrayList<Course>) stream.readObject();
            stream.close();
        } catch (Exception e) {
            this.courses = new ArrayList<>();
        }
    }
    // ---------------------------------------------------------------------------------------------

    // Save Courses
    // ---------------------------------------------------------------------------------------------
    public void saveCourses() {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(coursesFile));
            stream.writeObject(this.courses);
            stream.close();
        } catch (Exception e) {  }
    }
    // ---------------------------------------------------------------------------------------------

    // Clear Courses
    // ---------------------------------------------------------------------------------------------
    public void clearCourses() {
        this.courses = new ArrayList<Course>();

        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(coursesFile));
            stream.writeObject(this.courses);
            stream.close();
        } catch (Exception e) {  }
    }
    // ---------------------------------------------------------------------------------------------
}

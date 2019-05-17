package me.nizarmah.tended;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class EditCourseActivity extends AppCompatActivity {

    private Course course;
    private CourseManager courseManager;

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton floatingActionButton;

    private EditText courseName, courseSheet, startTime, endTime;
    private CheckedTextView mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox;

    private int userType, courseIndex;
    final private int FILE_SELECTED = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managecourse);

        Intent intent = getIntent();

        courseManager = CourseManager.getInstance(getFilesDir());

        // Floating Action Button Adds Course
        // -----------------------------------------------------------------------------------------
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutManageCourses);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCourseInfo();
            }
        });
        // -----------------------------------------------------------------------------------------

        TextView manageCourseTitle = (TextView) findViewById(R.id.manageCourseTitle);
        TextView manageCourseSubtitle = (TextView) findViewById(R.id.manageCourseSubtitle);

        courseName  = (EditText) findViewById(R.id.courseName);
        courseSheet = (EditText) findViewById(R.id.courseSheet);

        startTime = (EditText) findViewById(R.id.startTime);
        endTime   = (EditText) findViewById(R.id.endTime);

        mondayCheckbox    = (CheckedTextView) findViewById(R.id.mondayCheckbox);
        tuesdayCheckbox   = (CheckedTextView) findViewById(R.id.tuesdayCheckbox);
        wednesdayCheckbox = (CheckedTextView) findViewById(R.id.wednesdayCheckbox);
        thursdayCheckbox  = (CheckedTextView) findViewById(R.id.thursdayCheckbox);
        fridayCheckbox    = (CheckedTextView) findViewById(R.id.fridayCheckbox);

        userType = intent.getIntExtra("userType", 0);
        if (userType != 1)
            courseSheet.setVisibility(View.GONE);

        // Filling all fields
        // -----------------------------------------------------------------------------------------
        courseIndex   = intent.getIntExtra("courseIndex", 0);

        course = courseManager.getCourse(courseIndex);

        manageCourseTitle.setText("Edit Course");
        manageCourseSubtitle.setText("Edit a course in your list");

        if (course.getFilePath() != "")
            courseSheet.setText(course.getFilePath());

        courseName.setText(course.getName());

        String startTimeStr = "";
        startTimeStr += (course.getTime().getStartHour() < 10 ? "0" : "") + course.getTime().getStartHour();
        startTimeStr += ":" + (course.getTime().getStartMinute() < 10 ? "0" : "") + course.getTime().getStartMinute();
        startTime.setText(startTimeStr);

        String endTimeStr = "";
        endTimeStr += (course.getTime().getEndHour() < 10 ? "0" : "") + course.getTime().getEndHour();
        endTimeStr += ":" + (course.getTime().getEndMinute() < 10 ? "0" : "") + course.getTime().getEndMinute();
        endTime.setText(endTimeStr);

        if (course.getTime().getDays().contains(Calendar.MONDAY))
            mondayCheckbox.setChecked(true);
        if (course.getTime().getDays().contains(Calendar.TUESDAY))
            tuesdayCheckbox.setChecked(true);
        if (course.getTime().getDays().contains(Calendar.WEDNESDAY))
            wednesdayCheckbox.setChecked(true);
        if (course.getTime().getDays().contains(Calendar.THURSDAY))
            thursdayCheckbox.setChecked(true);
        if (course.getTime().getDays().contains(Calendar.FRIDAY))
            fridayCheckbox.setChecked(true);
        // -----------------------------------------------------------------------------------------
    }

    private void saveCourseInfo() {
        Course newCourse = course;
        LinkedList<Integer> days = new LinkedList<>();

        if (mondayCheckbox.isChecked())
            days.add(Calendar.MONDAY);
        if (tuesdayCheckbox.isChecked())
            days.add(Calendar.TUESDAY);
        if (wednesdayCheckbox.isChecked())
            days.add(Calendar.WEDNESDAY);
        if (thursdayCheckbox.isChecked())
            days.add(Calendar.THURSDAY);
        if (fridayCheckbox.isChecked())
            days.add(Calendar.FRIDAY);

        newCourse.setName(courseName.getText().toString());

        if(newCourse.getName().equals("")){
            Snackbar.make(coordinatorLayout, "Course name missing!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if(days.isEmpty()){
            Snackbar.make(coordinatorLayout, "No days specified!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        try{
            String startHour   = startTime.getText().toString().split(":")[0];
            String startMinute = startTime.getText().toString().split(":")[1];

            String endHour     = endTime.getText().toString().split(":")[0];
            String endMinute   = endTime.getText().toString().split(":")[1];

            newCourse.setTime(new DateTime(Integer.parseInt(startHour), Integer.parseInt(startMinute),
                    Integer.parseInt(endHour), Integer.parseInt(endMinute), days));
        } catch(Exception e) {
            Snackbar.make(coordinatorLayout, "Choose appropriate times!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        newCourse.setFilePath(courseSheet.getText().toString());

        if(userType == 1 && newCourse.getFilePath().equals("")){
            Snackbar.make(coordinatorLayout, "Sheet location missing!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        newCourse.updateSkips();

        courseManager.setCourse(courseIndex, newCourse);

        EditCourseActivity.super.onBackPressed();
    }

    public void openFileSelector(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select Course Sheet"), FILE_SELECTED);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECTED:
                if (resultCode == RESULT_OK) {
                    courseSheet.setText(data.getData().getPath());
                }
                break;
        }
    }

    // CheckTextView OnClick Listener
    // ---------------------------------------------------------------------------------------------
    public void toggleCheckedTextView(View view) {
        ((CheckedTextView) view).toggle();
    }
    // ---------------------------------------------------------------------------------------------

    // Go to Today's Courses Activity
    // ---------------------------------------------------------------------------------------------
    public void backtoMain(View view) {
        super.onBackPressed();
    }
    // ---------------------------------------------------------------------------------------------
}

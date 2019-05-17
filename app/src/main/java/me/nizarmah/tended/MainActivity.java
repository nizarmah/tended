package me.nizarmah.tended;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CoursesItemTouchHelper.RecyclerItemTouchHelperListener {

    private CourseManager courseManager;
    private ArrayList<Course> todaysCourses;

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton floatingActionButton;

    private RecyclerView coursesView;
    private CoursesAdapter coursesAdapter;

    private int userType;

    // Activity On Create Method
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        userType = intent.getIntExtra("userType", 0);

        // Setting Today's Date
        // -----------------------------------------------------------------------------------------
        TextView todayDate = (TextView) findViewById(R.id.todayDate);
        todayDate.setText(new SimpleDateFormat("EEEE, d MMMM").format(new Date()));
        // -----------------------------------------------------------------------------------------

        // Floating Action Button Adds Course
        // -----------------------------------------------------------------------------------------
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutMain);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });
        // -----------------------------------------------------------------------------------------

        // Initialize Courses Recycler View and Adapter
        // And Loading in Courses Data then Notifying Adapter
        // -----------------------------------------------------------------------------------------
        courseManager = CourseManager.getInstance(getFilesDir());
        todaysCourses = loadCoursesFromManager();

        coursesView = (RecyclerView) findViewById(R.id.coursesList);
        coursesAdapter = new CoursesAdapter(todaysCourses);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        coursesView.setLayoutManager(mLayoutManager);
        coursesView.setItemAnimator(new DefaultItemAnimator());
        coursesView.setAdapter(coursesAdapter);
        // -----------------------------------------------------------------------------------------

        // Setting Swipe to Delete to Recycler View
        // -----------------------------------------------------------------------------------------
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new CoursesItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(coursesView);
        // -----------------------------------------------------------------------------------------

        // Setting Recycler View Item On Click Listeners
        // -----------------------------------------------------------------------------------------
        coursesView.addOnItemTouchListener(new CoursesItemTouchListener(getApplicationContext(), coursesView, new CoursesItemTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Course course = courseManager.getCourse(position);

                if (course.getFilePath() != "") {
                    File file = new File(course.getFilePath());

                    // If File Does not exit, Delete File or Edit File
                    if (!file.exists()) {
                        // showing snack bar with Edit and Delete option
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, course.getName() + " has no attendance sheet!", Snackbar.LENGTH_LONG);
                        snackbar.setAction("DELETE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // delete is selected, delete the clicked item
                                courseManager.removeCourse(position);
                                coursesAdapter.removeItem(position);
                            }
                        });
                        snackbar.show();

                        return;
                    }

                    Uri fileUri = Uri.fromFile(file);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "application/vnd.ms-excel");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        // -----------------------------------------------------------------------------------------
    }
    // ---------------------------------------------------------------------------------------------

    // On Activity Resume, Refresh Courses List
    // ---------------------------------------------------------------------------------------------
    protected void onResume() {
        super.onResume();

        todaysCourses = loadCoursesFromManager();
        coursesAdapter = new CoursesAdapter(todaysCourses);
        coursesView.setAdapter(coursesAdapter);
    }
    // ---------------------------------------------------------------------------------------------

    // Read Today's Courses from Course Manager
    // ---------------------------------------------------------------------------------------------
    private ArrayList<Course> loadCoursesFromManager() {
        ArrayList<Course> courses = courseManager.getCourses();
        ArrayList<Course> todayCourses = new ArrayList<Course>();

        for (Course course : courses)
            if (course.getTime().isToday())
                todayCourses.add(course);

        return todayCourses;
    }
    // ---------------------------------------------------------------------------------------------

    // Courses View Recycler Swipe to Delete on Swipe
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CoursesAdapter.CoursesHolder) {
            if (direction == ItemTouchHelper.LEFT) {
                final int originalIndex = courseManager.getCourses().indexOf(todaysCourses.get(viewHolder.getAdapterPosition()));

                // backup of removed item for undo purpose
                final int deletedIndex = viewHolder.getAdapterPosition();
                final Course deletedItem = courseManager.removeCourse(originalIndex);

                // get the removed item name to display it in snack bar
                String name = deletedItem.getName();

                // remove the item from recycler view
                coursesAdapter.removeItem(viewHolder.getAdapterPosition());

                // showing snack bar with Undo option
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, name + " removed from courses!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        courseManager.addCourse(originalIndex, deletedItem);
                        coursesAdapter.restoreItem(deletedIndex, deletedItem);
                    }
                });
                snackbar.show();
            } else if (direction == ItemTouchHelper.RIGHT) {
                int originalPosition = courseManager.getCourses().indexOf(todaysCourses.get(position));

                Intent intent = new Intent(MainActivity.this, EditCourseActivity.class);
                intent.putExtra("userType", userType);
                intent.putExtra("courseIndex", originalPosition);
                startActivity(intent);
            }
        }
    }
    // ---------------------------------------------------------------------------------------------

    // Go to All Courses Activity
    // ---------------------------------------------------------------------------------------------
    public void listAllCourses(View view) {
        Intent intent = new Intent(MainActivity.this, CoursesActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }
    // ---------------------------------------------------------------------------------------------

    // Logout User
    // ---------------------------------------------------------------------------------------------
    public void logout(View view) {
        SharedPreferences sharedPreferences   = getSharedPreferences("sharedPreferences", 0);
        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
        sharedEditor.putInt("userType", -1);
        sharedEditor.putString("userID", null);
        sharedEditor.commit();

        courseManager.clearCourses();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    // ---------------------------------------------------------------------------------------------
}
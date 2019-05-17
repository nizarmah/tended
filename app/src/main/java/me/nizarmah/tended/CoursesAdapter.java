package me.nizarmah.tended;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesHolder> {

    private List<Course> coursesList;

    public class CoursesHolder extends RecyclerView.ViewHolder {

        public TextView numSkips, numAbscents;
        public TextView courseName, courseTime, courseDays;
        public RelativeLayout viewBackgroundDelete, viewBackgroundEdit, viewForeground;
        public LinearLayout studentsCourseInfo;

        public CoursesHolder(View view) {
            super(view);

            courseName = (TextView) view.findViewById(R.id.courseName);
            courseTime = (TextView) view.findViewById(R.id.courseTime);
            courseDays = (TextView) view.findViewById(R.id.courseDays);

            viewBackgroundDelete = view.findViewById(R.id.view_background_delete);
            viewBackgroundEdit   = view.findViewById(R.id.view_background_edit);

            viewForeground = view.findViewById(R.id.view_foreground);

            studentsCourseInfo = view.findViewById(R.id.studentsCourseInfo);

            numSkips    = (TextView) view.findViewById(R.id.numSkips);
            numAbscents = (TextView) view.findViewById(R.id.numAbscents);
        }
    }


    public CoursesAdapter(List<Course> coursesList) {
        this.coursesList = coursesList;
    }

    @Override
    public CoursesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_courselist, parent, false);

        return new CoursesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CoursesHolder holder, int position) {
        Course course = coursesList.get(position);

        holder.courseName.setText(course.getName());

        String time = "";
        time += course.getTime().getStartHour() + ":";
        time += ((course.getTime().getStartMinute() < 10) ? "0" : "")
                + course.getTime().getStartMinute();
        time += " - ";
        time += course.getTime().getEndHour() + ":";
        time += ((course.getTime().getEndMinute() < 10) ? "0" : "")
                + course.getTime().getEndMinute();
        holder.courseTime.setText(time);

        String days = "";
        if (course.getTime().getDays().contains(Calendar.MONDAY))
            days += "M ";
        if (course.getTime().getDays().contains(Calendar.TUESDAY))
            days += "T ";
        if (course.getTime().getDays().contains(Calendar.WEDNESDAY))
            days += "W ";
        if (course.getTime().getDays().contains(Calendar.THURSDAY))
            days += "R ";
        if (course.getTime().getDays().contains(Calendar.FRIDAY))
            days += "F ";
        holder.courseDays.setText(days);

        if (course.getFilePath().length() <= 0) {
            holder.studentsCourseInfo.setVisibility(View.VISIBLE);

            holder.numSkips.setText(course.getSkipsLeft() + "");
            holder.numAbscents.setText(course.getAbscences() + "");
        }
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public void removeItem(int position) {
        coursesList.remove(position);

        notifyItemRemoved(position);
    }

    public void restoreItem(int position, Course course) {
        coursesList.add(position, course);

        notifyItemInserted(position);
    }
}
package me.nizarmah.tended;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AttendanceAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent activityAttendance = new Intent(context, AttendanceActivity.class);
        context.startActivity(activityAttendance);
    }
}

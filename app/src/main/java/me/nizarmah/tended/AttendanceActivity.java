package me.nizarmah.tended;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class AttendanceActivity extends AppCompatActivity {

    private Message message;
    private MessageListener messageListener;

    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        progressBar = (ProgressBar) findViewById(R.id.attendanceBar);
        progressBar.setIndeterminate(true);

        sharedPreferences = getSharedPreferences("sharedPreferences", 0);

        final int userType  = sharedPreferences.getInt("userType", 0);
        final String userID = sharedPreferences.getString("userID", "201507797");

        final Context that = this;
        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);

                String[] messageContent = new String(message.getContent()).split(":");

                if (userType == 1 && messageContent[0].equals("Checking in")) {
                    Nearby.getMessagesClient(that).publish(new Message(("Checked in:" + userID).getBytes()));

                    Toast.makeText(that, "Checked in : " + userID, Toast.LENGTH_SHORT).show();
                } else if (messageContent[0].equals("Checked in")) {
                    Toast.makeText(that, "I was Checked in", Toast.LENGTH_SHORT).show();

                    if (messageContent[1].equals(userID)) {
                        // Set a New Attendance Alarm
                    }
                }
            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
            }
        };

        message = new Message(("Checking in:" + userID).getBytes());
    }

    protected void onStart() {
        super.onStart();

        Nearby.getMessagesClient(this).publish(message);
        Nearby.getMessagesClient(this).subscribe(messageListener);

        Toast.makeText(this, "Checking in", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        Nearby.getMessagesClient(this).unsubscribe(messageListener);

        super.onStop();
    }
}

package me.nizarmah.tended;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private int userType;
    private SharedPreferences sharedPreferences;

    private CoordinatorLayout coordinatorLayout;

    private EditText userID, userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("sharedPreferences", 0);

        userType = sharedPreferences.getInt("userType", -1);

        if (userType != -1) {
            Intent intent = new Intent(LoginActivity.this, AttendanceActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
            finish();
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutLogin);

        userID = (EditText) findViewById(R.id.userID);
        userEmail = (EditText) findViewById(R.id.userEmail);
        userPassword = (EditText) findViewById(R.id.userPassword);
    }

    public void login(View view) {
        if (userID.getText().length() == 0) {
            Snackbar.make(coordinatorLayout, "User id missing!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (userEmail.getText().length() == 0) {
            Snackbar.make(coordinatorLayout, "User email missing!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.getText().length() == 0) {
            Snackbar.make(coordinatorLayout, "User password missing!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (userEmail.getText().toString().equals("admin"))
            userType = 1;
        else userType = 0;

        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
        sharedEditor.putInt("userType", userType);
        sharedEditor.putString("userID", userID.getText().toString());
        sharedEditor.putString("userEmail", userEmail.getText().toString());
        sharedEditor.commit();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
        finish();
    }
}

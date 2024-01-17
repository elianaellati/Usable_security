package com.example.usable_security;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class view extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public ActionBarDrawerToggle actionBarDrawerToggle;

    public DrawerLayout drawerLayout;

    TextView taskName;
    TextView noteText;
    TextView dateEditText;
    TextView timeEditText;
    TextView reminderEditText;
    TextView repeatEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_view);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        tasks task = (tasks) intent.getSerializableExtra("task");
        taskName=findViewById(R.id.taskName);
        noteText=findViewById(R.id.noteText);
        dateEditText=findViewById(R.id.dateEditText);
        timeEditText=findViewById(R.id.timeEditText);
        reminderEditText=findViewById(R.id.reminderEditText);
        repeatEditText=findViewById(R.id.repeatEditText);


        taskName.setText(task.getName());
        noteText.setText(task.getNote());
        Date taskDate = task.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(taskDate);
        dateEditText.setText(formattedDate);
        timeEditText.setText(task.getTime());
        reminderEditText.setText(task.getReminder());
        repeatEditText.setText(task.getRepeat());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            Log.d("MenuItemClicked", "Item ID: " + item.getItemId());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_day:
                openMyDayIntent();
                break;
            case R.id.assigned:
                openAssignedIntent();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openAssignedIntent() {
        Intent intent = new Intent(this, AssignedActivity.class);
        startActivity(intent);
    }

    private void openMyDayIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}

package com.example.usable_security;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class importantActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private MenuItem notificationMenuItem;
    Map<String, tasks> taskMap = new HashMap<>();
    static int count=0;
    TextView imp;

    private ReminderUtils reminderUtils = new ReminderUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.important);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            displayTasks();
            Log.d("LoginInfo", "Refreshhh ");

            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
        });
        displayTasks();
        imp=findViewById(R.id.no_important);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#0F9D58"));


    }

    public void displayTasks() {
        RecyclerView recycler = findViewById(R.id.recycler_viewTasks);
        List<tasks> importantTasks = new ArrayList<>();
        List<tasks> taskList = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        User user = null;
        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJson, User.class);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Data");
        usersReference.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUsernameFound = false;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            User userr = userSnapshot.getValue(User.class);
                            Log.d("LoginInfo", "Ba7000000000000 " + userr.getEmail());
                            taskMap = userr.getTasks();
                            if (taskMap != null) {
                                for (Map.Entry<String, tasks> entry : taskMap.entrySet()) {
                                    if (entry.getValue().getCompleted() == false) {
                                        taskList.add(entry.getValue());
                                    }
                                    // Log the task details along with the formatted date
                                    Log.d("LoginInfo", "Ba7000000000000 " + entry.getValue().getName() + " " + entry.getValue().getDate());

                                }
                            }
                        }
                        recycler.setLayoutManager(new LinearLayoutManager(importantActivity.this));
                        adapter_important adapter = new adapter_important(importantTasks);
                        recycler.setAdapter(adapter);
                    }
                }


                // List<tasks> taskList = new ArrayList<>(taskMap.values());
                LocalDate currentDate = LocalDate.now();
                for (tasks task : taskList) {
                    Date taskDate = new Date(String.valueOf(task.getDate()));  // Assuming task.getDate() returns a long timestamp
                    LocalDate localTaskDate = taskDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    Log.d("LoginInfo", "fffffdad " + task.getName() + " :" + localTaskDate);
                    if (task.getImportant() == true && localTaskDate.equals(currentDate)) {
                        // Your task processing logic
                        String taskId = task.getId();
                        Log.d("LoginInfo", "fffffdad " + task.getName());
                       importantTasks.add(task);
                    }
                }

                if(importantTasks.isEmpty()){
                    imp.setVisibility(View.VISIBLE);
                }
                else{
                    imp.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_day:
                openMyDayIntent() ;
                break;
            case R.id.assigned:
                openAssignedIntent();
                break;
            case R.id.notification:
                openNotificationIntent();
                break;
            case R.id.nav_logout:
                openLogoutIntent();
                break;
            case R.id.tasks:
                openTasksIntent();
                break;
            case R.id.completed:
                openCompletedIntent();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    private void openCompletedIntent() {
        Intent intent = new Intent(this, completeActivity.class);
        startActivity(intent);
    }
    private void openAssignedIntent() {
        Intent intent = new Intent(this, AssignedActivity.class);
        startActivity(intent);
    }

    private void openNotificationIntent() {
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
    }

    private void openLogoutIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openTasksIntent() {
        Intent intent = new Intent(this, DisplayTask.class);
        startActivity(intent);
    }
    private void openMyDayIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            updateNotificationItem("Notification"+"("+count+")");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_bar, menu);
        notificationMenuItem = menu.findItem(R.id.notification);
        return true;
    }



    private void updateNotificationItem(String newTitle) {
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        Menu menu = navigationView.getMenu();
        MenuItem notificationItem = menu.findItem(R.id.notification);

        if (notificationItem != null) {
            notificationItem.setTitle(newTitle);
            SpannableString spannableString = new SpannableString(newTitle);
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), 0);
            notificationItem.setTitle(spannableString);

        }
    }

}
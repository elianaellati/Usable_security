package com.example.usable_security;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class  Share extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawerLayout;
    private MenuItem notificationMenuItem;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    TextView share;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        share=findViewById(R.id.no_share);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView textViewUsername = headerView.findViewById(R.id.user_name);

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);
            textViewUsername.setText(user.getEmail());
        }
        Intent intent = getIntent();
        tasks task = (tasks) intent.getSerializableExtra("task");

        User user=null;
        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJson, User.class);
        }
        RecyclerView recycler = findViewById(R.id.recycler_view);
        Map<String, contacts> contactsMap = user.contacts;
        List<contacts> contact = new ArrayList<>(contactsMap.values());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        ShareAdapter adapter = new ShareAdapter(contact,task,contactsMap);
        recycler.setAdapter(adapter);


        if(contact.isEmpty()){
            share.setVisibility(View.VISIBLE);
        }
        else{
            share.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            updateNotificationItem("Notification"+"("+count+")");
            return true;
        }
        return super.onOptionsItemSelected(item);
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



    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_day:
                openMyDayIntent() ;
                break;
            case R.id.assigned:
                openAssignedIntent();
                break;
            case R.id.nav_logout:
                openLogoutIntent();
                break;
            case R.id.tasks:
                openTasksIntent();
                break;
            case R.id.notification:
                openNotificationIntent();
                break;

            case R.id.completed:
                openCompletedIntent();
                break;
            case R.id.important:
                openImportantIntent();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void  openImportantIntent() {
        Intent intent = new Intent(this, importantActivity.class);
        startActivity(intent);
    }
    private void openCompletedIntent() {
        Intent intent = new Intent(this, completeActivity.class);
        startActivity(intent);
    }
    private void openLogoutIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void openAssignedIntent() {
        Intent intent = new Intent(this, AssignedActivity.class);
        startActivity(intent);
    }
    private void openMyDayIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openNotificationIntent() {
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
    }


    private void  openTasksIntent() {
        Intent intent = new Intent(this, DisplayTask.class);
        startActivity(intent);
    }

}
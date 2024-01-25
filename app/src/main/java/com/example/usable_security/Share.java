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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  Share extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawerLayout;
    private MenuItem notificationMenuItem;
    Map<String, contacts> contactsMap=new HashMap<>();
    public ActionBarDrawerToggle actionBarDrawerToggle;
    TextView share;
    User user = null;
    Gson gson = new Gson();
    tasks task;
    Map<String,contacts> contacct=new HashMap<>();
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        share=findViewById(R.id.no_share);
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
        task = (tasks) intent.getSerializableExtra("task");

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        displayContacts();

        swipeRefreshLayout.setOnRefreshListener(() -> {

            displayContacts();
            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            updateNotificationItem("Notification"+"("+HomeActivity.count+")");
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
    public void displayContacts() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Data");
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");

        if (!userJson.isEmpty()) {
            user = gson.fromJson(userJson, User.class);
            Log.d("Info", "ELIANANANANANANANANANANANANAN" + user.getUsername());
        }

        usersReference.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User userr = userSnapshot.getValue(User.class);
                        if (userr != null) {
                            contacct = userr.getContacts();
                            user.setContacts(contacct);
                            SharedPreferences.Editor editor = preferences.edit();
                            String updatedUserJson = gson.toJson(user);
                            editor.putString("user", updatedUserJson);
                            editor.apply();
                        }
                    }
                }
                RecyclerView recycler = findViewById(R.id.recycler_view);
                Map<String, contacts> contactsMap = user.getContacts();
                List<contacts> contact = new ArrayList<>(contactsMap.values());
                recycler.setLayoutManager(new LinearLayoutManager(Share.this));
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });
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
package com.example.usable_security;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log class for logging
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

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

public class details extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText edtName;
    EditText edtNote;
    EditText edtDate;
    ImageView editDate;

    ImageView editTime;

    EditText edtTime;
    public DrawerLayout drawerLayout;

    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        tasks task = (tasks) intent.getSerializableExtra("task");
        Log.d("tatata", task.getName());
        edtName=findViewById(R.id.taskName);
        edtNote=findViewById(R.id.noteText);
        edtDate=findViewById(R.id.dateEditText);
        editDate =findViewById(R.id.editDate);
        editTime =findViewById(R.id.editTimeImage);
        edtTime=findViewById(R.id.timeEditText);

        edtName.setText(task.getName());
        edtNote.setText(task.getNote());
        edtTime.setText(task.getTime());


        Date taskDate = task.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taskDate);

        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            // The task's date is today
            edtDate.setText("Today");
        } else {
            // The task's date is not today, so check if it's tomorrow
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            if (calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
                // The task's date is tomorrow
                edtDate.setText("Tomorrow");
            } else {
                edtDate.setText(taskDate.toString());

            }
        }


        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Calendar instance with the taskDate
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(task.getDate());

                // Create a DatePickerDialog and set the initial date to the taskDate
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        details.this,  // Use details.this as the context
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Set the selected date to the taskDate
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                // Update the taskDate with the selected date
                                task.setDate(calendar.getTime());

                                // Update the UI to show the selected date
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String selectedDateString = dateFormat.format(calendar.getTime());
                                edtDate.setText(selectedDateString);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });


       editTime.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {

               Calendar calendar = Calendar.getInstance();
               int hour = calendar.get(Calendar.HOUR_OF_DAY);
               int minute = calendar.get(Calendar.MINUTE);

               // Split the task's time into hour and minute
               String[] timeParts = task.getTime().split(":");
               if (timeParts.length == 2) {
                   hour = Integer.parseInt(timeParts[0]);
                   minute = Integer.parseInt(timeParts[1]);
               }



               TimePickerDialog timePickerDialog = new TimePickerDialog(
                       details.this,
                       new TimePickerDialog.OnTimeSetListener() {
                           @Override
                           public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                               // Handle the selected time (hourOfDay and minute)
                               // You can update a TextView or store the selected time in a variable
                               // For example:
                               String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                               task.setTime(selectedTime);
                           }
                       },
                       hour,
                       minute,

                       true
               );

               // Show the TimePickerDialog
               timePickerDialog.show();

           }
       });




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
                openMyDayIntent() ;
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

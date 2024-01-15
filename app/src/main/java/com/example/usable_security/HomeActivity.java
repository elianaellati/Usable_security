package com.example.usable_security;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public DrawerLayout drawerLayout;

    public ActionBarDrawerToggle actionBarDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        displayTasks();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener( this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  ActionBar actionBar;
       // actionBar = getSupportActionBar();


        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#0F9D58"));

        Button simpleButton= findViewById(R.id.addButton);
        simpleButton.setBackgroundColor(Color.BLACK);



        Button addTaskButton = findViewById(R.id.addButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tasks task=new tasks();

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
                View titleView = inflater.inflate(R.layout.title_dialogue, null);
                builder.setCustomTitle(titleView);

                final EditText input = new EditText(HomeActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Enter task title");
                LinearLayout layout = new LinearLayout(HomeActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(input);

                LinearLayout iconLayout = new LinearLayout(HomeActivity.this);
                iconLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(30, 0, 60, 0);
                layout.addView(iconLayout);

//                ImageButton calendarButton = new ImageButton(HomeActivity.this);
//                calendarButton.setImageResource(R.drawable.calendar);
//                calendarButton.setBackgroundColor(Color.WHITE);
//                calendarButton.setLayoutParams(params);
//
//                calendarButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        LayoutInflater inflater = getLayoutInflater();
//                        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
//                        Spinner spinner = dialogView.findViewById(R.id.dateSpinner);
//                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HomeActivity.this,
//                                R.array.date_options_array, android.R.layout.simple_spinner_item);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinner.setAdapter(adapter);
//                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
//                        dialogBuilder.setView(dialogView)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String selectedItem = (String) spinner.getSelectedItem();
//                                        if (selectedItem.equals("Today")) {
//                                            // Get today's date
//                                            Calendar calendar = Calendar.getInstance();
//                                            int year = calendar.get(Calendar.YEAR);
//                                            int month = calendar.get(Calendar.MONTH);
//                                            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                                            task.setDate(new Date(year - 1900, month, dayOfMonth));
//                                            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//
//                                            Toast.makeText(HomeActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
//                                        } else if (selectedItem.equals("Tomorrow")) {
//                                            // Get tomorrow's date
//                                            Calendar calendar = Calendar.getInstance();
//                                            calendar.add(Calendar.DAY_OF_MONTH, 1); // Add one day for tomorrow
//                                            int year = calendar.get(Calendar.YEAR);
//                                            int month = calendar.get(Calendar.MONTH);
//                                            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                                            task.setDate(new Date(year - 1900, month, dayOfMonth));
//                                            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//                                            Toast.makeText(HomeActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
//
//                                        } else if (selectedItem.equals("Pick a Date")) {
//                                            // Show a DatePickerDialog to pick a custom date
//                                            Calendar calendar = Calendar.getInstance();
//                                            int year = calendar.get(Calendar.YEAR);
//                                            int month = calendar.get(Calendar.MONTH);
//                                            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                                            DatePickerDialog datePickerDialog = new DatePickerDialog(
//                                                    HomeActivity.this,
//                                                    new DatePickerDialog.OnDateSetListener() {
//                                                        @Override
//                                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                                            task.setDate(new Date(year - 1900, monthOfYear, dayOfMonth));
//                                                            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                                            Toast.makeText(HomeActivity.this, selectedDate, Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    },
//                                                    year, month, dayOfMonth
//                                            );
//                                            datePickerDialog.show();
//                                        }
//                                    }
//                                })
//                          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Handle Cancel button click
//                                dialog.cancel();
//                            }
//                        })
//                                .show();
//
//
//                    }
//                });
//
//                        iconLayout.addView(calendarButton);

                // Create an ImageButton for the clock icon
                ImageButton clockButton = new ImageButton(HomeActivity.this);

                clockButton.setImageResource(R.drawable.clock);
                clockButton.setBackgroundColor(Color.WHITE);
                clockButton.setLayoutParams(params);




                clockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the current time
                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                HomeActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Handle the selected time (hourOfDay and minute)
                                        // You can update a TextView or store the selected time in a variable
                                        // For example:
                                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                        task.setTime(selectedTime);
                                        Toast.makeText(HomeActivity.this, selectedTime, Toast.LENGTH_SHORT).show();
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

                iconLayout.addView(clockButton);


                ImageButton repeat = new ImageButton(HomeActivity.this);
                repeat.setImageResource(R.drawable.repeat);
                repeat.setBackgroundColor(Color.WHITE);
                repeat.setLayoutParams(params);

                repeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
                        Spinner spinner = dialogView.findViewById(R.id.dateSpinner);

                        // Create an ArrayAdapter using the string array and a default spinner layout
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                HomeActivity.this,
                                R.array.repeat,
                                android.R.layout.simple_spinner_item
                        );
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                        dialogBuilder.setView(dialogView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String selectedItem = (String) spinner.getSelectedItem();
                                        if (selectedItem.equals("custom")) {
                                            // Create an EditText for entering the number of days
                                            EditText daysEditText = new EditText(HomeActivity.this);
                                            daysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            daysEditText.setHint("Enter number of days");

                                            // Create a Spinner for selecting the unit (daily, weekly, monthly, yearly)
                                            Spinner unitSpinner = new Spinner(HomeActivity.this);
                                            ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                                                    HomeActivity.this,
                                                    R.array.repeat_units,
                                                    android.R.layout.simple_spinner_item
                                            );
                                            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            unitSpinner.setAdapter(unitAdapter);

                                            // Create a LinearLayout to contain the EditText and Spinner
                                            LinearLayout customRepeatLayout = new LinearLayout(HomeActivity.this);
                                            customRepeatLayout.setOrientation(LinearLayout.VERTICAL);
                                            customRepeatLayout.addView(daysEditText);
                                            customRepeatLayout.addView(unitSpinner);

                                            // Show the custom repeat input dialog with the EditText and Spinner
                                            AlertDialog.Builder customRepeatInputDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                                            customRepeatInputDialogBuilder.setTitle("Custom Repeat")
                                                    .setMessage("Enter custom repeat details:")
                                                    .setView(customRepeatLayout)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String days = daysEditText.getText().toString();
                                                            String unit = (String) unitSpinner.getSelectedItem();
                                                            task.setRepeat(days+" "+unit);
                                                            Toast.makeText(HomeActivity.this, "Repeat every " + days + " " + unit, Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Handle Cancel button click
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            task.setRepeat(selectedItem);
                                            Toast.makeText(HomeActivity.this, "Repeat every " + selectedItem, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle Cancel button click
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                });



                iconLayout.addView(repeat);


                ImageButton notify = new ImageButton(HomeActivity.this);
                notify.setImageResource(R.drawable.notify);
                notify.setBackgroundColor(Color.WHITE);
                notify.setLayoutParams(params);


                notify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
                        Spinner spinner = dialogView.findViewById(R.id.dateSpinner);

                        // Create an ArrayAdapter using the string array and a default spinner layout
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                HomeActivity.this,
                                R.array.reminders,
                                android.R.layout.simple_spinner_item
                        );
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                        dialogBuilder.setView(dialogView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String notify = (String) spinner.getSelectedItem();
                                        if (notify.equals("custom")) {
                                            // Create an EditText for entering the number of days
                                            EditText daysEditText = new EditText(HomeActivity.this);
                                            daysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            daysEditText.setHint("Enter number of days");

                                            // Create a Spinner for selecting the unit (daily, weekly, monthly, yearly)
                                            Spinner unitSpinner = new Spinner(HomeActivity.this);
                                            ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                                                    HomeActivity.this,
                                                    R.array.remind_units,
                                                    android.R.layout.simple_spinner_item
                                            );
                                            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            unitSpinner.setAdapter(unitAdapter);

                                            // Create a LinearLayout to contain the EditText and Spinner
                                            LinearLayout customRepeatLayout = new LinearLayout(HomeActivity.this);
                                            customRepeatLayout.setOrientation(LinearLayout.VERTICAL);
                                            customRepeatLayout.addView(daysEditText);
                                            customRepeatLayout.addView(unitSpinner);

                                            // Show the custom repeat input dialog with the EditText and Spinner
                                            AlertDialog.Builder customRepeatInputDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                                            customRepeatInputDialogBuilder.setTitle("Custom Reminder")
                                                    .setMessage("Enter custom reminder details:")
                                                    .setView(customRepeatLayout)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String days = daysEditText.getText().toString();
                                                            String unit = (String) unitSpinner.getSelectedItem();
                                                            task.setReminder(days+" "+unit);
                                                            Toast.makeText(HomeActivity.this, "Remind before " + days + " " + unit, Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Handle Cancel button click
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            task.setReminder(notify);
                                            Toast.makeText(HomeActivity.this, "Remind before " + notify, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle Cancel button click
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                });

                iconLayout.addView(notify);


                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String taskText = input.getText().toString();
                        task.setName(taskText);
                        Calendar currentCalendar = Calendar.getInstance();
                        int currentYear = currentCalendar.get(Calendar.YEAR);
                        int currentMonth = currentCalendar.get(Calendar.MONTH);
                        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
                        Date currentDate = new Date(currentYear - 1900, currentMonth, currentDayOfMonth);
                        task.setDate(currentDate);
                        String id=User.key;
                        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(id).child("tasks");
                        DatabaseReference newTaskRef = userTasksRef.push();
                        newTaskRef.setValue(task);
                        String taskId = newTaskRef.getKey();
                        Log.d("Tskkk",taskId);
                        task.setId(taskId);
                        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                        String userJson = preferences.getString("user", "");
                        User updateuser=null;
                        if (!userJson.isEmpty()) {
                            Gson gson = new Gson();
                            updateuser = gson.fromJson(userJson, User.class);
                            updateuser.addTaskToMap(newTaskRef.getKey(),task);
                            String userrJson = gson.toJson( updateuser);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user",  userrJson);
                            editor.apply();

                        }
                        displayTasks();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });




        TextView txtView = findViewById(R.id.currentDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        txtView.setText(currentDate);


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
            case R.id.assigned:
                openAssignedIntent();
                break;
            case R.id.notification:
                openNotificationIntent();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void openNotificationIntent() {
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
    }
    private void openAssignedIntent() {
        Intent intent = new Intent(this, AssignedActivity.class);
        startActivity(intent);
    }




    public void displayTasks() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        User user = null;
        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJson, User.class);
        }
        RecyclerView recycler = findViewById(R.id.recycler_viewTasks);
    Map<String, tasks> tasksMap = user.getTasks();
        if (tasksMap != null) {
            List<tasks> taskList = new ArrayList<>(tasksMap.values());
        Calendar currentCalendar = Calendar.getInstance();
        List<tasks> todayTasks = new ArrayList<>();

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        Date currentDate = new Date(currentYear - 1900, currentMonth, currentDayOfMonth);
            for (tasks task : taskList) {

                if (task.getDate().equals(currentDate)  && task.getShared()==0) {

                    task.toString();
                    String taskId = task.getId();
                    todayTasks.add(task);
                }
            }
            recycler.setLayoutManager(new LinearLayoutManager(this));
            adapter_tasks adapter = new adapter_tasks(todayTasks);
            recycler.setAdapter(adapter);
        }

    }

}
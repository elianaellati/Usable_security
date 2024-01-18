package com.example.usable_security;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DisplayTask extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    Map<String,tasks> taskMap=new HashMap<>();

    private ReminderUtils reminderUtils = new ReminderUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaytask);
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

                task.setReminder("None");
                task.setRepeat("None");

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

                task.setTime(selectedTime);
                Toast.makeText(DisplayTask.this, selectedTime, Toast.LENGTH_SHORT).show();


                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayTask.this);
                LayoutInflater inflater = DisplayTask.this.getLayoutInflater();
                View titleView = inflater.inflate(R.layout.title_dialogue, null);
                builder.setCustomTitle(titleView);

                final EditText input = new EditText(DisplayTask.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Enter task title");
                LinearLayout layout = new LinearLayout(DisplayTask.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(input);

                LinearLayout iconLayout = new LinearLayout(DisplayTask.this);
                iconLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(30, 0, 60, 0);
                layout.addView(iconLayout);

                // Create an ImageButton for the clock icon
                ImageButton clockButton = new ImageButton(DisplayTask.this);

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
                                DisplayTask.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Handle the selected time (hourOfDay and minute)
                                        // You can update a TextView or store the selected time in a variable
                                        // For example:
                                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                        task.setTime(selectedTime);
                                        Toast.makeText(DisplayTask.this, selectedTime, Toast.LENGTH_SHORT).show();
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


                ImageButton repeat = new ImageButton(DisplayTask.this);
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
                                DisplayTask.this,
                                R.array.repeat,
                                android.R.layout.simple_spinner_item
                        );
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DisplayTask.this);
                        dialogBuilder.setView(dialogView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String selectedItem = (String) spinner.getSelectedItem();
                                        if (selectedItem.equals("custom")) {
                                            // Create an EditText for entering the number of days
                                            EditText daysEditText = new EditText(DisplayTask.this);
                                            daysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            daysEditText.setHint("Enter number of days");

                                            // Create a Spinner for selecting the unit (daily, weekly, monthly, yearly)
                                            Spinner unitSpinner = new Spinner(DisplayTask.this);
                                            ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                                                    DisplayTask.this,
                                                    R.array.repeat_units,
                                                    android.R.layout.simple_spinner_item
                                            );
                                            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            unitSpinner.setAdapter(unitAdapter);

                                            // Create a LinearLayout to contain the EditText and Spinner
                                            LinearLayout customRepeatLayout = new LinearLayout(DisplayTask.this);
                                            customRepeatLayout.setOrientation(LinearLayout.VERTICAL);
                                            customRepeatLayout.addView(daysEditText);
                                            customRepeatLayout.addView(unitSpinner);

                                            // Show the custom repeat input dialog with the EditText and Spinner
                                            AlertDialog.Builder customRepeatInputDialogBuilder = new AlertDialog.Builder(DisplayTask.this);
                                            customRepeatInputDialogBuilder.setTitle("Custom Repeat")
                                                    .setMessage("Enter custom repeat details:")
                                                    .setView(customRepeatLayout)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String days = daysEditText.getText().toString();
                                                            String unit = (String) unitSpinner.getSelectedItem();
                                                            task.setRepeat(days+" "+unit);
                                                            Toast.makeText(DisplayTask.this, "Repeat every " + days + " " + unit, Toast.LENGTH_SHORT).show();
                                                            onCustomRepeatSelected(Integer.parseInt(days),unit,task);
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
                                            onRepeatSelected(selectedItem,task);
                                            Toast.makeText(DisplayTask.this, "Repeat every " + selectedItem, Toast.LENGTH_SHORT).show();
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


                ImageButton notify = new ImageButton(DisplayTask.this);
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
                                DisplayTask.this,
                                R.array.reminders,
                                android.R.layout.simple_spinner_item
                        );
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DisplayTask.this);
                        dialogBuilder.setView(dialogView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String notify = (String) spinner.getSelectedItem();

                                        Calendar currentCalendar = Calendar.getInstance();
                                        currentCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        int currentYear = currentCalendar.get(Calendar.YEAR);
                                        int currentMonth = currentCalendar.get(Calendar.MONTH);
                                        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
                                        Date currentDate = new Date(currentYear - 1900, currentMonth, currentDayOfMonth);
                                        task.setDate(currentDate);
                                        task.setName(input.getText().toString());

                                        if (notify.equals("custom")) {
                                            // Create an EditText for entering the number of days
                                            EditText daysEditText = new EditText(DisplayTask.this);
                                            daysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            daysEditText.setHint("Enter number of days");

                                            // Create a Spinner for selecting the unit (daily, weekly, monthly, yearly)
                                            Spinner unitSpinner = new Spinner(DisplayTask.this);
                                            ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                                                    DisplayTask.this,
                                                    R.array.remind_units,
                                                    android.R.layout.simple_spinner_item
                                            );
                                            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            unitSpinner.setAdapter(unitAdapter);

                                            // Create a LinearLayout to contain the EditText and Spinner
                                            LinearLayout customRepeatLayout = new LinearLayout(DisplayTask.this);
                                            customRepeatLayout.setOrientation(LinearLayout.VERTICAL);
                                            customRepeatLayout.addView(daysEditText);
                                            customRepeatLayout.addView(unitSpinner);

                                            // Show the custom repeat input dialog with the EditText and Spinner
                                            AlertDialog.Builder customRepeatInputDialogBuilder = new AlertDialog.Builder(DisplayTask.this);
                                            customRepeatInputDialogBuilder.setTitle("Custom Reminder")
                                                    .setMessage("Enter custom reminder details:")
                                                    .setView(customRepeatLayout)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String days = daysEditText.getText().toString();
                                                            String unit = (String) unitSpinner.getSelectedItem();
                                                            task.setReminder(days+" "+unit);
                                                            Toast.makeText(DisplayTask.this, "Remind before " + days + " " + unit, Toast.LENGTH_SHORT).show();
                                                            long timeInMillis = reminderUtils.calculateReminderTime(task.getDate(), "custom", Integer.parseInt(days),unit,task.time);
                                                            int requestCode1 = 1;
                                                            ReminderManager.setReminder(DisplayTask.this, timeInMillis, task,requestCode1);
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
                                        } else if(!notify.equals("custom") && !notify.equals("none")) {
                                            task.setReminder(notify);
                                            Toast.makeText(DisplayTask.this, "Remind before " + notify, Toast.LENGTH_SHORT).show();
                                            long timeInMillis = reminderUtils.calculateReminderTime(task.getDate(), task.getReminder(),0,"", task.time);
                                            int requestCode1 = 1;
                                            ReminderManager.setReminder(DisplayTask.this, timeInMillis, task,requestCode1);

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
                        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        currentCalendar.set(Calendar.MINUTE, 0);
                        currentCalendar.set(Calendar.SECOND, 0);
                        currentCalendar.set(Calendar.MILLISECOND, 0);

                        Date currentDate = currentCalendar.getTime();
                        task.setDate(currentDate);
                        Log.d("Dateeeee",String.valueOf(task.getDate()));
                        String id=User.key;
                        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(id).child("tasks");
                        DatabaseReference newTaskRef = userTasksRef.push();
                        newTaskRef.setValue(task);
                        String taskId = newTaskRef.getKey();
                        Log.d("Tskkk",taskId);
                        Log.d("Adddedddddddddddddd",taskId);
                        displayTasks();
                        task.setId(taskId);
                        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                        String userJson = preferences.getString("user", "");
                        User updateuser=null;
                        if (!userJson.isEmpty()) {
                            Gson gson = new Gson();
                            updateuser = gson.fromJson(userJson, User.class);
                            updateuser.addTaskToMap(newTaskRef.getKey(),task);
                            Log.d("kkkkkkkkkkkkkkkkk",taskId);
                            String userrJson = gson.toJson( updateuser);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user",  userrJson);
                            editor.apply();
                            displayTasks();
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
            case R.id.my_day:
                openMydayIntent();
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

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void  openMydayIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    private void  openLogoutIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void openNotificationIntent() {
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
    }
    private void openAssignedIntent() {
        Intent intent = new Intent(this, AssignedActivity.class);
        startActivity(intent);
    }




//    public void displayTasks() {
//
//        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
//        userTasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Map<String, tasks> taskMap = new HashMap<>();
//                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
//                    tasks task = taskSnapshot.getValue(tasks.class);
//                    if (task != null) {
//                        taskMap.put(taskSnapshot.getKey(), task);
//                        SharedPreferen ces preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//                        String userJson = preferences.getString("user", "");
//                        User user = null;
//                        if (!userJson.isEmpty()) {
//                            Gson gson = new Gson();
//                            user = gson.fromJson(userJson, User.class);
//                        }
//                        user.setTasks(taskMap);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", databaseError.toException());
//            }
//        });
//
//        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//        String userJson = preferences.getString("user", "");
//        User user = null;
//        if (!userJson.isEmpty()) {
//            Gson gson = new Gson();
//            user = gson.fromJson(userJson, User.class);
//        }
//        RecyclerView recycler = findViewById(R.id.recycler_viewTasks);
//        Map<String, tasks> tasksMap = user.getTasks();
//        if (tasksMap != null) {
//            List<tasks> taskList = new ArrayList<>(tasksMap.values());
//        Calendar currentCalendar = Calendar.getInstance();
//        List<tasks> todayTasks = new ArrayList<>();
//
//        int currentYear = currentCalendar.get(Calendar.YEAR);
//        int currentMonth = currentCalendar.get(Calendar.MONTH);
//        int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
//        Date currentDate = new Date(currentYear - 1900, currentMonth, currentDayOfMonth);
//            for (tasks task : taskList) {
//
//                Calendar taskCalendar = Calendar.getInstance();
//                taskCalendar.setTime(task.getDate());
//                int taskYear = taskCalendar.get(Calendar.YEAR);
//                int taskMonth = taskCalendar.get(Calendar.MONTH);
//                int taskDayOfMonth = taskCalendar.get(Calendar.DAY_OF_MONTH);
//                Date taskDate = new Date(taskYear - 1900, taskMonth, taskDayOfMonth);
//
//                if (taskDate==currentDate  && task.getShared()==0) {
//                    task.toString();
//                    todayTasks.add(task);
//                }
//                else{
//                    Log.d("jfjfjfjfjj",task.getName()+ ":::"+ task.getDate());
//                }
//            }
//            recycler.setLayoutManager(new LinearLayoutManager(this));
//            adapter_tasks adapter = new adapter_tasks(todayTasks);
//            if(!todayTasks.isEmpty()){
//                Log.d("ksksksk","DDDDD");
//            }
//            recycler.setAdapter(adapter);
//        }
//
//    }

    public void displayTasks() {
        RecyclerView recycler = findViewById(R.id.recycler_viewTasks);
        List<tasks> todayTasks = new ArrayList<>();
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
                                    taskList.add(entry.getValue());
                                    // Log the task details along with the formatted date
                                    Log.d("LoginInfo", "Ba7000000000000 " + entry.getValue().getName() + " " + entry.getValue().getDate());

                                }
                            }
                        }
                        recycler.setLayoutManager(new LinearLayoutManager(DisplayTask.this));
                        DisplayAdapter adapter = new  DisplayAdapter (todayTasks);
                        recycler.setAdapter(adapter);
                    }
                }


                Log.d("LoginInfo", "Elianaaaaaaaaaaaaa");
                // List<tasks> taskList = new ArrayList<>(taskMap.values());
                Calendar currentCalendar = Calendar.getInstance();
                int currentYear = currentCalendar.get(Calendar.YEAR);
                int currentMonth = currentCalendar.get(Calendar.MONTH);
                int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
                Date currentDate = new Date(currentYear - 1900, currentMonth, currentDayOfMonth);
                for (tasks task : taskList) {
                    if (task.getShared() == 0) {
                        task.toString();
                        String taskId = task.getId();
                        Log.d("LoginInfo", "Aloooos " + task.getName());
                        todayTasks.add(task);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onCustomRepeatSelected(int customRepeatInterval, String customRepeatUnit,tasks task) {
        // Calculate the next due date based on the custom repeat interval and unit
        Calendar calendar = Calendar.getInstance();
        calendar.add(getCalendarUnitFromCustomRepeatUnit(customRepeatUnit), customRepeatInterval);
        Date nextDueDate = calendar.getTime();
        tasks newTask=new tasks();
        newTask.setName(task.name);
        newTask.setDate(nextDueDate);
        newTask.setRepeat(customRepeatInterval+" "+customRepeatUnit);
        newTask.setRepeat(task.repeat);
        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
        DatabaseReference newTaskRef = userTasksRef.push();
        newTaskRef.setValue(newTask);
    }

    // Helper method to convert custom repeat units to Calendar units
    private int getCalendarUnitFromCustomRepeatUnit(String customRepeatUnit) {
        switch (customRepeatUnit) {
            case "Days":
                return Calendar.DAY_OF_MONTH;
            case "Weeks":
                return Calendar.WEEK_OF_YEAR;
            case "Month":
                return Calendar.MONTH;
            case "Year":
                return Calendar.YEAR;
            default:
                throw new IllegalArgumentException("Invalid custom repeat unit: " + customRepeatUnit);
        }
    }
    public void onRepeatSelected(String selectedRepeatOption,tasks task) {
        int repeatInterval;
        String repeatUnit;

        switch (selectedRepeatOption) {
            case "None":
                // Set the repeat interval to 0 for no repetition
                repeatInterval = 0;
                // Set the repeat unit to an empty string or null, as there's no repetition
                repeatUnit = "";
                break;
            case "Daily":
                // Set the repeat interval to 1 for daily repetition
                repeatInterval = 1;
                // Set the repeat unit to "day"
                repeatUnit = "day";
                break;
            case "Weekly":
                // Set the repeat interval to 1 for weekly repetition
                repeatInterval = 1;
                // Set the repeat unit to "week"
                repeatUnit = "week";
                break;
            case "Monthly":
                // Set the repeat interval to 1 for monthly repetition
                repeatInterval = 1;
                // Set the repeat unit to "month"
                repeatUnit = "month";
                break;
            case "Yearly":
                // Set the repeat interval to 1 for yearly repetition
                repeatInterval = 1;
                // Set the repeat unit to "year"
                repeatUnit = "year";
                break;
            default:
                throw new IllegalArgumentException("Invalid repeat option: " + selectedRepeatOption);
        }

        // Calculate the next due date based on the selected repeat option
        Calendar calendar = Calendar.getInstance();
        calendar.add(getCalendarUnitFromRepeatUnit(repeatUnit), repeatInterval);
        Date nextDueDate = calendar.getTime();
        tasks newTask=new tasks();
        newTask.setName(task.name);
        newTask.setDate(nextDueDate);
        newTask.setRepeat(task.repeat);
        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
        DatabaseReference newTaskRef = userTasksRef.push();
        newTaskRef.setValue(newTask);

    }

    // Helper method to convert repeat units to Calendar units
    private int getCalendarUnitFromRepeatUnit(String repeatUnit) {
        switch (repeatUnit) {
            case "None":
                return -1;
            case "day":
                return Calendar.DAY_OF_MONTH;
            case "week":
                return Calendar.WEEK_OF_YEAR;
            case "month":
                return Calendar.MONTH;
            case "year":
                return Calendar.YEAR;
            default:
                throw new IllegalArgumentException("Invalid repeat unit: " + repeatUnit);
        }
    }

}

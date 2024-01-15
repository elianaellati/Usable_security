package com.example.usable_security;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log; // Import Log class for logging
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class details extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    EditText edtName;
    EditText edtNote;
    EditText edtDate;
    ImageView editDate;

    ImageView editTime;

    EditText edtTime;
    Button edtBtn;
    public DrawerLayout drawerLayout;
    private SharedPreferences preferences;
    public ActionBarDrawerToggle actionBarDrawerToggle;
     String  nameBeforeUpdate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);
        preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE); // Initialize preferences
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
        edtName = findViewById(R.id.taskName);
        edtNote = findViewById(R.id.noteText);
        edtDate = findViewById(R.id.dateEditText);
        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTimeImage);
        edtTime = findViewById(R.id.timeEditText);
        edtBtn = findViewById(R.id.editButton);

        edtName.setText(task.getName());
        edtNote.setText(task.getNote());
        edtTime.setText(task.getTime());


        Spinner spinner = findViewById(R.id.spinner);
        String taskReminder = task.getReminder();

        List<String> reminderList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.reminders)));
        if (!reminderList.contains(taskReminder)) {
            reminderList.add(taskReminder);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                details.this,
                android.R.layout.simple_spinner_item,
                reminderList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int position = adapter.getPosition(taskReminder);
        spinner.setSelection(position);


        Spinner spinner2 = findViewById(R.id.spinner2);
        String taskRepeat = task.getRepeat();

        List<String> repeatList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.repeat)));
        if (!repeatList.contains(taskRepeat)) {
            repeatList.add(taskRepeat);
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                details.this,
                android.R.layout.simple_spinner_item,
                repeatList
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        int position2 = adapter2.getPosition(taskRepeat);
        spinner2.setSelection(position2);


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


        editTime.setOnClickListener(new View.OnClickListener() {
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


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String notify = (String) spinner.getSelectedItem();
                if (notify.equals("custom")) {
                    // Create an EditText for entering the number of days
                    EditText daysEditText = new EditText(details.this);
                    daysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    daysEditText.setHint("Enter number of days");

                    // Create a Spinner for selecting the unit (daily, weekly, monthly, yearly)
                    Spinner unitSpinner = new Spinner(details.this);
                    ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                            details.this,
                            R.array.remind_units,
                            android.R.layout.simple_spinner_item
                    );
                    unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    unitSpinner.setAdapter(unitAdapter);

                    // Create a LinearLayout to contain the EditText and Spinner
                    LinearLayout customRepeatLayout = new LinearLayout(details.this);
                    customRepeatLayout.setOrientation(LinearLayout.VERTICAL);
                    customRepeatLayout.addView(daysEditText);
                    customRepeatLayout.addView(unitSpinner);


                    AlertDialog.Builder customRepeatInputDialogBuilder = new AlertDialog.Builder(details.this);
                    customRepeatInputDialogBuilder.setTitle("Custom Reminder")
                            .setMessage("Enter custom reminder details:")
                            .setView(customRepeatLayout)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String days = daysEditText.getText().toString();
                                    String unit = (String) unitSpinner.getSelectedItem();
                                    task.setReminder(days + " " + unit);

                                    if (!reminderList.contains(task.getReminder())) {
                                        reminderList.add(task.getReminder()); // Add the new custom reminder to the list
                                        adapter.notifyDataSetChanged(); // Notify the adapter that the dataset has changed
                                        spinner.setSelection(adapter.getCount() - 1); // Set the selection to the last item (the custom reminder)
                                    }

                                    Toast.makeText(details.this, "Remind before " + days + " " + unit, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(details.this, "Remind before " + notify, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String selectedRepeat = adapter.getItem(position);
            }
        });


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selection here
                String selectedRepeat = adapter2.getItem(position);
                if (selectedRepeat.equals("custom")) {
                    // Create an EditText for entering the number of days
                    EditText daysEditText = new EditText(details.this);
                    daysEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    daysEditText.setHint("Enter number of days");

                    // Create a Spinner for selecting the unit (daily, weekly, monthly, yearly)
                    Spinner unitSpinner = new Spinner(details.this);
                    ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                            details.this,
                            R.array.repeat_units,
                            android.R.layout.simple_spinner_item
                    );
                    unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    unitSpinner.setAdapter(unitAdapter);

                    // Create a LinearLayout to contain the EditText and Spinner
                    LinearLayout customRepeatLayout = new LinearLayout(details.this);
                    customRepeatLayout.setOrientation(LinearLayout.VERTICAL);
                    customRepeatLayout.addView(daysEditText);
                    customRepeatLayout.addView(unitSpinner);

                    // Show the custom repeat input dialog with the EditText and Spinner
                    AlertDialog.Builder customRepeatInputDialogBuilder = new AlertDialog.Builder(details.this);
                    customRepeatInputDialogBuilder.setTitle("Custom Repeat")
                            .setMessage("Enter custom repeat details:")
                            .setView(customRepeatLayout)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String days = daysEditText.getText().toString();
                                    String unit = (String) unitSpinner.getSelectedItem();
                                    task.setRepeat(days + " " + unit);

                                    if (!repeatList.contains(task.getRepeat())) {
                                        repeatList.add(task.getRepeat()); // Add the new custom reminder to the list
                                        adapter2.notifyDataSetChanged(); // Notify the adapter that the dataset has changed
                                        spinner2.setSelection(adapter2.getCount() - 1); // Set the selection to the last item (the custom reminder)
                                    }
                                    Toast.makeText(details.this, "Repeat every " + days + " " + unit, Toast.LENGTH_SHORT).show();
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
                    task.setRepeat(selectedRepeat);
                    Toast.makeText(details.this, "Repeat every " + selectedRepeat, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
            }
        });


        edtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userJson = preferences.getString("user", "");
                User user = null;

                if (!userJson.isEmpty()) {
                    Gson gson = new Gson();
                    user = gson.fromJson(userJson, User.class);
                    Map<String, tasks> taskMap = user.getTasks();
                    for (Map.Entry<String, tasks> entry : taskMap.entrySet()) {
                        Log.d("Taskkkkkkkk", task.getName());
                        nameBeforeUpdate=task.getName();
                        if (entry.getValue().getName().compareToIgnoreCase(task.getName()) == 0) {

                            editForSharedContacts(user, task);

                            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
                            userTasksRef.child(entry.getKey()).child("name").setValue(edtName.getText().toString());
                            userTasksRef.child(entry.getKey()).child("note").setValue(edtNote.getText().toString());
                            userTasksRef.child(entry.getKey()).child("reminder").setValue(task.getReminder());
                            userTasksRef.child(entry.getKey()).child("repeat").setValue(task.getRepeat());
                            userTasksRef.child(entry.getKey()).child("time").setValue(task.getTime());
                            task.setName(edtName.getText().toString());
                            task.setNote(edtNote.getText().toString());
                            User updateuser = null;
                            updateuser = gson.fromJson(userJson, User.class);
                            updateuser.editTask(entry.getKey(), task);
                            String userrJson = gson.toJson(updateuser);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user", userrJson);
                            editor.apply();

                            Intent intent = new Intent(details.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }

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


    private void editForSharedContacts(User user, tasks task) {
        Log.d("hello", "hieiei");
        Log.d("name", "" + user.getName());
        Map<String, contacts> contactsMap = user.getContacts();
        ArrayList<String> contactEmails = new ArrayList<>();
        if (contactsMap != null) {
            for (Map.Entry<String, contacts> entry : contactsMap.entrySet()) {
                if (entry.getValue().getShared() == 1) {
                    Log.d("name", "" + entry.getValue().getName());
                    String email = entry.getValue().getEmail();
                    if (email != null && !email.isEmpty()) {
                        contactEmails.add(email);
                        Log.d("email", email);
                    }
                }
            }
            // Now you can use the contactEmails list for further processing
        } else {
            Log.d("contacts", "Contacts map is null");
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Data");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User currentUser = userSnapshot.getValue(User.class);
                    Log.d("UserData", "Snapshot key: " + userSnapshot.getKey());
                    Log.d("UserData", "Current user: " + currentUser);
                    if (currentUser != null) {
                        String userEmail = currentUser.getEmail();
                        Log.d("UserEmail", "Current user email: " + userEmail);

                        if (userEmail != null && contactEmails.contains(userEmail)) {
                            Log.d("TaskFound", "Task found for user with email: " + userEmail);
                            Map<String, tasks> userTasks = currentUser.getTasks();
                            if (userTasks != null) {
                                for (Map.Entry<String, tasks> entry : userTasks.entrySet()) {
                                    tasks userTask = entry.getValue();
                                    if (userTask.getName().equals(nameBeforeUpdate)) {
                                        Log.d("TaskFound", "Found matching task: " + userTask.getName());
                                        DatabaseReference userTasksRef = userSnapshot.child("tasks").getRef();
                                        userTasksRef.child(entry.getKey()).child("name").setValue(edtName.getText().toString());
                                        userTasksRef.child(entry.getKey()).child("note").setValue(edtNote.getText().toString());
                                        userTasksRef.child(entry.getKey()).child("reminder").setValue(task.getReminder());
                                        userTasksRef.child(entry.getKey()).child("repeat").setValue(task.getRepeat());
                                        userTasksRef.child(entry.getKey()).child("time").setValue(task.getTime());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error reading data from Firebase", error.toException());
            }
        });
    }




}
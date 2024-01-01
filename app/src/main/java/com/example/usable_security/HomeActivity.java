package com.example.usable_security;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;

    public ActionBarDrawerToggle actionBarDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
        ActionBar actionBar;
        actionBar = getSupportActionBar();


        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#0F9D58"));

        Button simpleButton= findViewById(R.id.addButton);
        simpleButton.setBackgroundColor(Color.BLACK);



        Button addTaskButton = findViewById(R.id.addButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                iconLayout.setGravity(Gravity.CENTER);
                layout.addView(iconLayout);

                ImageButton calendarButton = new ImageButton(HomeActivity.this);
                calendarButton.setImageResource(R.drawable.calendar);

                calendarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
                        Spinner spinner = dialogView.findViewById(R.id.dateSpinner);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HomeActivity.this,
                                R.array.date_options_array, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                        dialogBuilder.setView(dialogView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String selectedItem = (String) spinner.getSelectedItem();
                                        if (selectedItem.equals("Pick a Date")) {
                                            Calendar calendar = Calendar.getInstance();
                                            int year = calendar.get(Calendar.YEAR);
                                            int month = calendar.get(Calendar.MONTH);
                                            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                                            DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this,
                                                    new DatePickerDialog.OnDateSetListener() {
                                                        @Override
                                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                            // Handle the selected date (e.g., display it in a TextView)
                                                            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                                            // Example:
                                                            // dateTextView.setText(selectedDate);
                                                        }
                                                    }, year, month, dayOfMonth);
                                            datePickerDialog.show();
                                        } else if (selectedItem.equals("AnotherValue")) {
                                            // Do something else if "AnotherValue" is selected
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


                iconLayout.addView(calendarButton);

                // Create an ImageButton for the clock icon
                ImageButton clockButton = new ImageButton(HomeActivity.this);
                clockButton.setImageResource(R.drawable.clock); // Set the clock icon image
                clockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
                        Spinner spinner = dialogView.findViewById(R.id.dateSpinner);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HomeActivity.this,
                                R.array.reminders, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                        dialogBuilder.setView(dialogView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String selectedItem = (String) spinner.getSelectedItem();
                                        if (selectedItem.equals("Pick a Date")) {
                                        } else if (selectedItem.equals("AnotherValue")) {
                                            // Do something else if "AnotherValue" is selected
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
                iconLayout.addView(clockButton);


                ImageButton repeat = new ImageButton(HomeActivity.this);
                repeat.setImageResource(R.drawable.repeat);
                repeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {





                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
                        Spinner spinner = dialogView.findViewById(R.id.dateSpinner);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HomeActivity.this,
                                R.array.repeat, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                        dialogBuilder.setView(dialogView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String selectedItem = (String) spinner.getSelectedItem();
                                        if (selectedItem.equals("Pick a Date")) {

                                        } else if (selectedItem.equals("AnotherValue")) {
                                            // Do something else if "AnotherValue" is selected
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

                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String taskText = input.getText().toString();

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


        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView txtView = findViewById(R.id.currentDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        txtView.setText(currentDate);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
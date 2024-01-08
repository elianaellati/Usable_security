package com.example.usable_security;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log class for logging

import androidx.appcompat.app.AppCompatActivity;

public class details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.task_details);
        Intent intent = getIntent();
        tasks task = (tasks) intent.getSerializableExtra("task");
        Log.d("tatata", task.getName()); // Use Log.d for logging
    }
}

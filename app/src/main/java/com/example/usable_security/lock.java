package com.example.usable_security;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class lock extends AppCompatActivity {
    private int seconds = 0;
    private boolean running = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock);
        Log.d("LoginInfo", "FFFFFFFFFFFFFFFFF " + MainActivity.duaration);
        checkInstance(savedInstanceState);
        runTimer();
    }

    private void checkInstance(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            seconds = savedInstanceState.getInt("SECONDS");
            running = savedInstanceState.getBoolean("RUNNING");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("SECONDS", seconds);
        outState.putBoolean("RUNNING", running);


    }



    private void runTimer() {
        final TextView txtTime = findViewById(R.id.txtTime);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = hours + " : " + minutes + " : " + secs;
                txtTime.setText(time);

                if (running) {
                    seconds++;

                    if (seconds == MainActivity.duaration) {
                        Intent intent = new Intent(lock.this, MainActivity.class);
                        startActivity(intent);
                        running = false;
                        MainActivity.duaration=MainActivity.duaration+(60*5);
                        MainActivity.count=1;
                        MainActivity.attempt=0;
                    }

                }

                handler.postDelayed(this, 1000);
            }
        });
    }



 /*   public void btnStartOnClick(View view) {
        running = true;
    }

    public void btnStopOnClick(View view) {
        running = false;
    }

    public void btnResetOnClick(View view) {
        running = false;
        seconds = 0;
    }*/
}

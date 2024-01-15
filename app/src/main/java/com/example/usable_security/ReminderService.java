//package com.example.usable_security;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class ReminderService extends Service {
//    private Timer timer;
//    private SharedPreferences sharedPreferences;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                // Check for tasks that require reminders
//                List<tasks> tasks = // Retrieve tasks from SharedPreferences
//
//                for (tasks task : tasks) {
//                    if (isReminderTime(task)) {
//                        showReminderNotification(task);
//                    }
//                }
//            }
//        }, 0, 60000); // Check every minute for reminders
//
//        return START_STICKY;
//    }
//
//    private List<Task> getTasksFromSharedPreferences() {
//        List<Task> tasks = new ArrayList<>();
//
//        // Retrieve tasks from SharedPreferences
//        // Example: Assuming you have stored tasks with keys like "task1_reminder", "task2_reminder", etc.
//        // You can loop through these keys to retrieve the tasks
//
//        // Example code to retrieve a task (you may need to adapt this based on your actual implementation):
//        String taskReminder1 = sharedPreferences.getString("task1_reminder", "");
//        if (!taskReminder1.isEmpty()) {
//            Task task1 = new Task();
//            task1.setReminderOption(taskReminder1);
//            tasks.add(task1);
//        }
//
//        // Repeat the above for other tasks
//
//        return tasks;
//    }
//
//    // ... (isReminderTime and showReminderNotification methods remain the same)
//}

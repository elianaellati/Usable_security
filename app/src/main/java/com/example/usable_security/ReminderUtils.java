package com.example.usable_security;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class ReminderUtils {
    public static long calculateReminderTime(Date dueDate, String reminderOption, int customValue, String customUnit,String taskTime) {


        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        } else {

            String[] timeParts = taskTime.split(":");
            if (timeParts.length != 2) {
                throw new IllegalArgumentException("Invalid task time format: " + taskTime);
            }
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dueDate);

            // Set the time of the task
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);

            long timeDifferenceMillis = calendar.getTimeInMillis() - System.currentTimeMillis();

            switch (reminderOption) {
                case "None":
                    return -1; // No reminder
                case "5 minutes before":
                    calendar.add(Calendar.MINUTE, -5);
                    break;
                case "15 minutes before":
                    calendar.add(Calendar.MINUTE, -15);
                    break;
                case "30 minutes before":
                    calendar.add(Calendar.MINUTE, -30);
                    break;
                case "1 hour before":
                    calendar.add(Calendar.HOUR_OF_DAY, -1);
                    break;
                case "2 hour before":
                    calendar.add(Calendar.HOUR_OF_DAY, -2);
                    break;
                case "1 day before":
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case "2 days before":
                    calendar.add(Calendar.DAY_OF_MONTH, -2);
                    break;
                case "1 month before":
                    calendar.add(Calendar.MONTH, -1);
                    break;
                case "custom":
                    // Handle custom reminder
                    switch (customUnit) {
                        case "Minute":
                            Log.d("kkk",customUnit);
                            calendar.add(Calendar.MINUTE, -customValue);
                            break;
                        case "Hour":
                            calendar.add(Calendar.HOUR_OF_DAY, -customValue);
                            break;
                        case "Day":
                            calendar.add(Calendar.DAY_OF_MONTH, -customValue);
                            break;
                        case "Week":
                            calendar.add(Calendar.WEEK_OF_MONTH, -customValue);
                            break;
                        case "Month":
                            calendar.add(Calendar.MONTH, -customValue);
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid custom unit: " + customUnit);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid reminder option: " + reminderOption);
            }

            long reminderTimeMillis = calendar.getTimeInMillis();
            return reminderTimeMillis;
        }
    }
}

package com.example.usable_security;

import java.util.Date;

public class tasks {

    String id;

     String name;
     Date date;

     Boolean important=false;

     Boolean completed=false;
     String time;

    public Boolean getImportant() {
        return important;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }



    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    String repeat;
     String reminder; public tasks(){}

    public tasks(String name, Date date, String time, String repeat, String reminder) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.repeat = repeat;
        this.reminder = reminder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}

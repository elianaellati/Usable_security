package com.example.usable_security;

import java.io.Serializable;
import java.util.Date;

public class tasks implements Serializable {
    int access=1;

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    int shared =0;
    int status=0;

    public String getShareduser() {
        return shareduser;
    }

    public void setShareduser(String shareduser) {
        this.shareduser = shareduser;
    }

    String shareduser=null;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String email=null;
    String id;

     String name;
     Date date;

     String note="";

     Boolean important=false;

     Boolean completed=false;
     String time;

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }



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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

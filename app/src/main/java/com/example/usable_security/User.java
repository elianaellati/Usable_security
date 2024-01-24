package com.example.usable_security;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private String username;

    private String email;
    private  String name;




    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    static String key;
    public Map<String,contacts> contacts=new HashMap<>();
    public Map<String,tasks> tasks=new HashMap<>();

    public void setUsername(String username) {
        this.username = username;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }



    public String getEmail() {
        return email;
    }
    public Map<String, contacts> getContacts() {
        return contacts;
    }
    public void setContacts(Map<String, contacts> contacts) {
        this.contacts = contacts;
    }

    public Map<String, tasks> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, tasks> tasks) {
        this.tasks = tasks;
    }




    public User(String name, String username,String email) {
        this.name = name;
        this.username = username;

        this.email = email;

    }


    public void addContactToMap(String contactId, contacts contact) {
        contacts.put(contactId,contact);
    }

    public void addTaskToMap(String taskId, tasks task) {
        tasks.put(taskId, task);
    }

    public void removeTaskFromMap(String taskId, tasks task) {
        tasks.remove(taskId, task);
    }

    public void editTask(String taskId, tasks updatedTask) {
            tasks.put(taskId, updatedTask);
        }

    }

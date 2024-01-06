package com.example.usable_security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usable_security.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class  AssignedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawerLayout;

    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assigned_page);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        displayContacts();

        Button add =findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailInputDialog();
            }
        });

    }
    public void displayContacts(){
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        User user=null;
        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
             user = gson.fromJson(userJson, User.class);
        }
        RecyclerView recycler = findViewById(R.id.recycler_view);
        Map<String, contacts> contactsMap = user.contacts;
        String [] name=new String[contactsMap.size()];
        String [] email=new String[contactsMap.size()];
        int i=0;
        for (Map.Entry<String, contacts> entry : contactsMap.entrySet()) {
            String contactId = entry.getKey();
            contacts contactt = entry.getValue();
            String contactName = contactt.getName();
            String contactEmail = contactt.getEmail();
            Log.d("LoginInfo", "Login successful. Username: " + contactEmail);
            name[i]=contactName;
            email[i]=contactEmail;
            ++i;
        }
        recycler.setLayoutManager(new LinearLayoutManager(this));
        Adapter adapter = new Adapter(name,email);
        recycler.setAdapter(adapter);
    }
    private void showEmailInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.email_dialogue, null);
        builder.setView(dialogView);

        EditText editTextEmail = dialogView.findViewById(R.id.editEmail);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredEmail = editTextEmail.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersReference = database.getReference("Data");
                usersReference.orderByChild("email").equalTo(enteredEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isEmailFound= false;

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null  ) {
                                    isEmailFound = true;
                                    String id=User.key;
                                    Log.d("Info", "EMAIl found:" + user.getUsername());
                                    Log.d("Info", "EMAIl found:" + id);
                                    contacts contact = new contacts(user.getName(), user.getEmail());

                                    SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                    String userJson = preferences.getString("user", "");
                                    Gson gson = new Gson();
                                    User  updateuser = gson.fromJson(userJson, User.class);;
                                    if (updateuser.getEmail().compareTo(enteredEmail)!=0 && user.getContacts().containsValue(contact.email)) {
                                        DatabaseReference userContactsRef = FirebaseDatabase.getInstance().getReference().child("Data").child(id).child("contacts");
                                        DatabaseReference newContactRef = userContactsRef.push();
                                        newContactRef.setValue(contact);
                                        updateuser.addContactToMap(newContactRef.getKey(),contact);
                                        String userrJson = gson.toJson( updateuser);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("user",  userrJson);
                                        editor.apply();
                                    }

                                    displayContacts();
                                }
                            }

                        } else {
                            // Username does not exist in the database
                            Log.d("Info", "EMAIl not found: " + enteredEmail);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors here
                        Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                openMyDayIntent() ;
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


}

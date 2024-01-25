package com.example.usable_security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.usable_security.R;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  AssignedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawerLayout;
    static User user = null;
    static Gson gson = new Gson();
    static User updateuser;

    Map<String, contacts> contacct=new HashMap<>();
    private MenuItem notificationMenuItem;

    Map<String, contacts> contactsMap=new HashMap<>();
   static  SharedPreferences preferences;
     static String userJson;
    List<contacts> filteredContacts = new ArrayList<>();
    List<contacts> allContacts= new ArrayList<>();
    TextView cont;
    SearchView searchView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String,contacts> contacct=new HashMap<>();

        setContentView(R.layout.assigned_page);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        cont=findViewById(R.id.no_contact);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUsername = headerView.findViewById(R.id.user_name);

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");


        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);
            textViewUsername.setText(user.getEmail());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        userJson = preferences.getString("user", "");
        swipeRefreshLayout.setOnRefreshListener(() -> {
            displayContacts();
            Log.d("LoginInfo", "Refreshhh " );
            swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
        });
        displayContacts();

        Button add =findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailInputDialog();
            }
        });

    }
   public void displayContacts() {
       List<contacts>contactlist= new ArrayList<>();
       FirebaseDatabase database = FirebaseDatabase.getInstance();
       DatabaseReference usersReference = database.getReference("Data");
       if (!userJson.isEmpty()) {

           user = gson.fromJson(userJson, User.class);
           Log.d("Info", "ELIANANANANANANANANANANANANAN" + user.getUsername());
       }

       usersReference.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if (dataSnapshot.exists()) {

                   for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                       User userr = userSnapshot.getValue(User.class);
                       contacct = userr.getContacts();
                       if (contacct != null) {
                           for (Map.Entry<String, contacts> entry : contacct.entrySet()) {
                            contactlist.add(entry.getValue());
                           }

                       }
                   }
               }
               user.setContacts(contacct);
               SharedPreferences.Editor editor = preferences.edit();
               String updatedUserJson = gson.toJson(user);
               editor.putString("user", updatedUserJson);
               editor.apply();
               if(contactlist.isEmpty()){
                   Log.d("Info", "233333333333");
                   cont.setVisibility(View.VISIBLE);
               }
               else{
                   cont.setVisibility(View.GONE);
               }
               RecyclerView recycler = findViewById(R.id.recycler_view);
               recycler.setLayoutManager(new LinearLayoutManager(AssignedActivity.this));
               Adapter adapter = new Adapter(contactlist);
               recycler.setAdapter(adapter);
                searchView = findViewById(R.id.searchView);

                allContacts = new ArrayList<>(contactlist);
                filteredContacts = new ArrayList<>(allContacts);

           }


           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               // Handle errors here
               Log.e("FirebaseError", "Error: " + databaseError.getMessage());
           }
       });
     /* if(contactsMap != null && !contactsMap.isEmpty()) {
           for (Map.Entry<String, contacts> entry : contactsMap.entrySet()) {
               contactlist.add(entry.getValue());
           }
       }*/



       //String[] name = new String[filteredContacts.size()];
      // String[] email = new String[filteredContacts.size()];

      /* for (int i = 0; i < filteredContacts.size(); i++) {
           contacts contact = filteredContacts.get(i);
           name[i] = contact.getName();
           email[i] = contact.getEmail();
       }*/


       searchView = findViewById(R.id.searchView);
       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {

               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {

               newText = newText.toLowerCase();
               filteredContacts.clear();

               if (newText.isEmpty()) {
                   filteredContacts.addAll(allContacts);
               } else {

                   for (contacts contact : allContacts) {
                       if (contact.getName().toLowerCase().contains(newText) ||
                               contact.getEmail().toLowerCase().contains(newText)) {
                           filteredContacts.add(contact);
                       }
                   }
               }
               updateRecyclerView(filteredContacts);
               return true;
           }
       });
   }

    private void updateRecyclerView(List<contacts> contactsList) {


        Adapter adapter = new Adapter(contactsList);
        RecyclerView recycler = findViewById(R.id.recycler_view);
        recycler.setAdapter(adapter);
    }




    private void showEmailInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.email_dialogue, null);
        TextView status = dialogView.findViewById(R.id.status);
        builder.setView(dialogView);
                EditText editTextEmail = dialogView.findViewById(R.id.editEmail);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersReference = database.getReference("Data");
                builder.setPositiveButton("OK", null);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setCancelable(false);
              AlertDialog dialog = builder.create();
               dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                     @Override
                                     public void onShow(DialogInterface dialogInterface) {
                                         Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                                         positiveButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 String enteredEmail = editTextEmail.getText().toString();
                                                 usersReference.orderByChild("email").equalTo(enteredEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                         Log.d("Info", "EMAIl found:0000000" + enteredEmail);
                                                         if (dataSnapshot.exists()) {
                                                             for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                                 User user = userSnapshot.getValue(User.class);
                                                                   if (user != null  ) {
                                                                     String id=User.key;
                                                                     Log.d("Info", "EMAIl found:0000000" + user.getUsername());
                                                                     Log.d("Info", "EMAIl found:9999999" + id);
                                                                     contacts contact = new contacts(user.getName(), user.getEmail());
                                                                     int flagg=0;
                                                                    updateuser = gson.fromJson(userJson, User.class);
                                                                     Map<String,contacts> contacct=updateuser.getContacts();
                                                                     for(Map.Entry<String,contacts> entry: contacct.entrySet() ){
                                                                         Log.d("Info", "GLOOOOOOO" + user.getEmail());
                                                                         Log.d("Info", "5555555555555555555" + entry.getValue().getEmail());
                                                                         if(entry.getValue().getEmail().compareToIgnoreCase(enteredEmail)==0){
                                                                             flagg=1;
                                                                         }
                                                                     }


                                                                     if(flagg==1){
                                                                         AlreadyExistDialog();
                                                                     }
                                                                     if (updateuser.getEmail().compareToIgnoreCase(enteredEmail)!=0 && flagg==0 ) {
                                                                         DatabaseReference userContactsRef = FirebaseDatabase.getInstance().getReference().child("Data").child(id).child("contacts");
                                                                         DatabaseReference newContactRef = userContactsRef.push();
                                                                         Log.d("Info", "Elianaaaaaaaaaaaaa" + user.getUsername());
                                                                         newContactRef.setValue(contact);
                                                                         updateuser.addContactToMap(newContactRef.getKey(),contact);
                                                                         String userrJson = gson.toJson( updateuser);
                                                                         SharedPreferences.Editor editor = preferences.edit();
                                                                         editor.putString("user",  userrJson);
                                                                         editor.apply();
                                                                         ADDEDDialog();
                                                                     }

                                                                      dialog.dismiss();

                                                                       status.setText("");
                                                                 }
                                                             displayContacts();

                                                             }

                                                         }


                                                         else {
                                                             status.setText("Email address not found");
                                                             editTextEmail.setText("");
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

                                             }
                                         });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            updateNotificationItem("Notification"+"("+HomeActivity.count+")");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_bar, menu);
        notificationMenuItem = menu.findItem(R.id.notification);
        return true;
    }



    private void updateNotificationItem(String newTitle) {
        NavigationView navigationView = findViewById(R.id.navigation_bar);
        Menu menu = navigationView.getMenu();
        MenuItem notificationItem = menu.findItem(R.id.notification);

        if (notificationItem != null) {
            notificationItem.setTitle(newTitle);
            SpannableString spannableString = new SpannableString(newTitle);
            spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), 0);
            notificationItem.setTitle(spannableString);

        }
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_day:
                openMyDayIntent() ;
                break;
            case R.id.assigned:
                openAssignedIntent();
                break;
            case R.id.nav_logout:
                openLogoutIntent();
                break;
            case R.id.tasks:
                openTasksIntent();
                break;
            case R.id.notification:
                openNotificationIntent();
                break;
            case R.id.completed:
                openCompletedIntent();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void openCompletedIntent() {
        Intent intent = new Intent(this, completeActivity.class);
        startActivity(intent);
    }
    private void openLogoutIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void openAssignedIntent() {
        Intent intent = new Intent(this, AssignedActivity.class);
        startActivity(intent);
    }
    private void openMyDayIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openNotificationIntent() {
        Intent intent = new Intent(this, Notification.class);
        startActivity(intent);
    }


    private void  openTasksIntent() {
        Intent intent = new Intent(this, DisplayTask.class);
        startActivity(intent);
    }
    private void AlreadyExistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.exist, null);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                builder.setView(dialogView);
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                    }
                }, 2*1000);


            }
        });
    }

    private void ADDEDDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add, null);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                builder.setView(dialogView);
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                    }
                }, 2*1000);


            }
        });
    }



















}

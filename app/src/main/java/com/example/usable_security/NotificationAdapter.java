package com.example.usable_security;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<tasks> tasks;
    private tasks task;
    Map<String,tasks>usertasks;
    private Context context;
    private SharedPreferences preferences;

    public NotificationAdapter(List<tasks> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationcardview,
                parent, false);
        context = parent.getContext();
        return new NotificationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        String foundKey=null;
        Button accept = cardView.findViewById(R.id.btnAccept);
        task= tasks.get(holder.getAdapterPosition());
        TextView namee = (TextView)cardView.findViewById(R.id.contactname);
        namee.setText("     Shared From "+tasks.get(position).getShareduser());
        TextView task= (TextView)cardView.findViewById(R.id.task);
        task.setText("     "+"Task "+tasks.get(position).getName());
         accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersReference = database.getReference("Data");
                usersReference.orderByChild("email").equalTo(tasks.get(position).getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isEmailFound = false;

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                Map<String,contacts>contact=user.getContacts();
                               usertasks=user.getTasks();
                                for( Map.Entry<String,contacts> entry:contact.entrySet()){
                                   if( entry.getValue().getEmail().compareToIgnoreCase(tasks.get(position).getEmail())==0){
                                       isEmailFound=true;
                                    }
                                }
                                for(Map.Entry<String,tasks> entry: usertasks.entrySet()){
                                    if(entry.getValue().getName().compareToIgnoreCase(tasks.get(position).getName())==0){
                                        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
                                        userTasksRef.child(entry.getKey()).child("shared").setValue(0);
                                        userTasksRef.child(entry.getKey()).child("name").setValue(tasks.get(position).getName());
                                        userTasksRef.child(entry.getKey()).child("note").setValue(tasks.get(position).getNote());
                                        userTasksRef.child(entry.getKey()).child("reminder").setValue(tasks.get(position).getReminder());
                                        userTasksRef.child(entry.getKey()).child("repeat").setValue(tasks.get(position).getRepeat());
                                        userTasksRef.child(entry.getKey()).child("time").setValue(tasks.get(position).getTime());
                                        userTasksRef.child(entry.getKey()).child("time").setValue(tasks.get(position).getEmail());
                                    }
                                }

                            }

                            if (!isEmailFound) {

                                contacts contact = new contacts(tasks.get(position).getShareduser(),tasks.get(position).getEmail());
                                DatabaseReference userContactsRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("contacts");
                                DatabaseReference newContactRef = userContactsRef.push();
                                newContactRef.setValue(contact);
                                preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                String userJson = preferences.getString("user", "");
                                Gson gson = new Gson();
                                User updateuser = gson.fromJson(userJson, User.class);
                                updateuser.addContactToMap(newContactRef.getKey(),contact);
                                String userrJson = gson.toJson( updateuser);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("user",  userrJson);
                                editor.apply();
                            }
                            notifyItemRemoved(position);
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

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }

    }
}

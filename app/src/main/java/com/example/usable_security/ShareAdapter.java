package com.example.usable_security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class ShareAdapter
        extends RecyclerView.Adapter<ShareAdapter.ViewHolder>{

    private List<contacts> contact;
    private contacts contactt;
    private SharedPreferences preferences;
    private Context context;
    private tasks task;
    private String keycontact;
    private String name;
    Map<String, contacts> contactsMap;

    public ShareAdapter(List<contacts> contact,tasks task, Map<String, contacts> contactsMap){
        this.contactsMap=contactsMap;
        this.task=task;
       this.contact=contact;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.sharecardview,
                parent,
                false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        String foundKey=null;
        ImageButton share = cardView.findViewById(R.id.shareImage);
        contactt = contact.get(holder.getAdapterPosition());
        TextView namee = (TextView)cardView.findViewById(R.id.name);
        namee.setText(contact.get(position).getName());
        TextView emaill = (TextView)cardView.findViewById(R.id.email);
        emaill.setText(contact.get(position).getEmail());
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialogue();

            }
            });

    }

    @Override
    public int getItemCount() {
        return contact.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }

    }
    public void ShareDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.access_dialogue, null);
        builder.setView(dialogView);
        CheckBox view = dialogView.findViewById(R.id.view);
        CheckBox edit = dialogView.findViewById(R.id.edit);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                findKey();
                searchtheuser();
                task.setShared(1);
                if(view.isChecked() && edit.isChecked()){
                    task.setAccess(1);
                }else if(view.isChecked()){
                    Log.d("LoginInfo", "Incorrect password for username: " );
                    task.setAccess(0);
                }else{
                    task.setAccess(1);
                }
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



    public void findKey(){
        for(Map.Entry<String,contacts> entry: contactsMap.entrySet()) {
            if(entry.getValue().getEmail().compareToIgnoreCase(contactt.getEmail())==0){
                keycontact=entry.getKey();
            }
        }
    }
    public void searchtheuser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Data");
        preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        Gson gson = new Gson();
        User storeduser = gson.fromJson(userJson, User.class);
        name=storeduser.getName();
        usersReference.orderByChild("email").equalTo(contactt.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUsernameFound = false;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        String userKey = userSnapshot.getKey();
                        isUsernameFound = true;
                        task.setShared(1);
                        task.setShareduser(name);
                        task.setEmail(storeduser.getEmail());
                        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(userKey).child("tasks");
                        DatabaseReference newTaskRef = userTasksRef.push();
                        newTaskRef.setValue(task);
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("contacts");
                        userRef.child(keycontact).child("shared").setValue(1);
                    }
                }

                if (!isUsernameFound) {

                    Log.d("LoginInfo", "Incorrect password for username: " );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
}
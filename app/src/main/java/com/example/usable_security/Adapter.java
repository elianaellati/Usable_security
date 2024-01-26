package com.example.usable_security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Adapter
        extends RecyclerView.Adapter<Adapter.ViewHolder>{

    private List<contacts> contacts;
    private Context context;
    private AssignedActivity assignedActivity;
    contacts contactt;

    public Adapter( List<contacts> contacts ,AssignedActivity assignedActivity){
       this.contacts=contacts;
       this.assignedActivity=assignedActivity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned,
                parent,
                false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView namee = (TextView)cardView.findViewById(R.id.name);
        namee.setText(contacts.get(position).getName());
        TextView emaill = (TextView)cardView.findViewById(R.id.email);
        emaill.setText(contacts.get(position).getEmail());
        ImageButton deleteImage = cardView.findViewById(R.id.delete);
        contactt= contacts.get(holder.getAdapterPosition());

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog before deleting the task
                contactt= contacts.get(holder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete this task?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Iterate over the tasks and delete the matching one from the database
                        contactt= contacts.get(holder.getAdapterPosition());
                         AssignedActivity.preferences=context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                        AssignedActivity.userJson = AssignedActivity.preferences.getString("user", "");

                        if (!  AssignedActivity.userJson.isEmpty()) {

                            AssignedActivity.user = AssignedActivity.gson.fromJson( AssignedActivity.userJson, User.class);

                            Map<String, contacts> contactMap =AssignedActivity.user.getContacts();

                            if(!contactMap.isEmpty()) {
                                for (Map.Entry<String, contacts> entry : contactMap.entrySet()) {
                                    Log.d("LoginInfo", "Refreshhh "+entry.getValue().getName() );
                                    String Name = entry.getValue().getName();
                                    if (Name != null && entry.getValue().getName().compareToIgnoreCase(contactt.getName()) == 0) {
                                        DatabaseReference usercontactRef = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Data")
                                                .child(User.key)
                                                .child("contacts");
                                        AssignedActivity.updateuser = AssignedActivity.gson.fromJson(AssignedActivity.userJson, User.class);
                                        Map<String, contacts> contactsMap =   AssignedActivity.updateuser.getContacts();
                                        Iterator<Map.Entry<String, contacts>> iterator = contactsMap.entrySet().iterator();
                                        Log.d("LoginInfo", "787878787878779898898" +contactt.getName());
                                        while (iterator.hasNext()) {
                                            Map.Entry<String, contacts> entryy = iterator.next();
                                            if (entryy.getValue().getEmail().compareToIgnoreCase(contactt.getEmail())==0) {
                                                iterator.remove();
                                                Log.d("LoginInfo", "7856666666666666666666666666" );
                                            }
                                        }

                                        for (Map.Entry<String, contacts> entryy :   AssignedActivity.updateuser.getContacts().entrySet()) {
                                            Log.d("LoginInfo", "78779898898" + entryy.getValue().getName());
                                        }

                                        String updatedUserJson = AssignedActivity.gson.toJson(AssignedActivity.updateuser);
                                        SharedPreferences.Editor editor = AssignedActivity.preferences.edit();
                                        editor.putString("user", updatedUserJson);
                                        editor.apply();
                                        usercontactRef.child(entry.getKey()).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        contacts.remove(holder.getAdapterPosition()); // Remove the deleted task from the list
                                                        notifyItemRemoved(holder.getAdapterPosition()); // Notify the adapter of the removed item
                                                        notifyItemRangeChanged(holder.getAdapterPosition(), contacts.size()); // Notify the adapter that the data set has changed
                                                        dialog.dismiss();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle any errors that may occur
                                                        Log.e("DeleteTask", "Error deleting task from database", e);

                                                    }
                                                });
                                        assignedActivity.displayContacts();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "No" button, do nothing
                        dialog.dismiss();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });




    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }

    }
}
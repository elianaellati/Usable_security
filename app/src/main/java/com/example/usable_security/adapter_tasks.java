package com.example.usable_security;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class adapter_tasks extends RecyclerView.Adapter<adapter_tasks.ViewHolder> {

    private List<tasks> tasks;
    private Context context;
    private tasks task;
    private AlertDialog detailsDialog;
    private AlertDialog viewDialog;
    Map<String,contacts> contactsMap=new HashMap<>();
    ArrayList<String> contactEmails = new ArrayList<>();



    public adapter_tasks(List<tasks> tasks) {
        this.tasks = tasks;
    }
    String id;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }


    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        final String[] id = {User.key};
        CardView cardView = holder.cardView;
        TextView namee = cardView.findViewById(R.id.name);
        namee.setText(tasks.get(position).getName());
        Log.d("userId", id[0]);

        task = tasks.get(holder.getAdapterPosition());

//        ImageButton starButton = cardView.findViewById(R.id.starButton);
//        if(task.getImportant()){
//            starButton.setImageResource(R.drawable.ic_star_filled);
//            starButton.setTag("filled");
//        }

        ImageButton starButton = holder.cardView.findViewById(R.id.starButton);
        if (task.getImportant()) {
            starButton.setImageResource(R.drawable.ic_star_filled);
            starButton.setTag("filled");
        } else {
            starButton.setImageResource(R.drawable.ic_star_outline);
            starButton.setTag("outline");
        }

        ImageButton details=cardView.findViewById(R.id.detailsButton);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = tasks.get(holder.getAdapterPosition());
                if(task.getAccess()==1){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialogue_details, null);
                builder.setView(dialogView);

                ImageView editImage = dialogView.findViewById(R.id.editImage);
                TextView editText = dialogView.findViewById(R.id.editText);
                editImage.setImageResource(R.drawable.edit_icon);
                editText.setText("Edit");

                ImageView deleteImage = dialogView.findViewById(R.id.deleteImage);
                TextView deleteText = dialogView.findViewById(R.id.deleteText);
                deleteImage.setImageResource(R.drawable.delete_icon);
                deleteText.setText("Delete");

                ImageView shareImage = dialogView.findViewById(R.id.shareImage);
                TextView shareText = dialogView.findViewById(R.id.shareText);
                shareImage.setImageResource(R.drawable.share_icon);
                shareText.setText("Share");

                AlertDialog dialog = builder.create();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = 150; // Set your desired width here
                dialog.getWindow().setAttributes(layoutParams);

                detailsDialog = builder.create();
                detailsDialog.show();
          // Set click listeners for the options
                editImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition(); // Get the position of the clicked item
                        if (position != RecyclerView.NO_POSITION) {
                            tasks clickedTask = tasks.get(position); // Retrieve the corresponding task from the list
                            Intent intent = new Intent(context, details.class);
                            intent.putExtra("task", clickedTask);
                            context.startActivity(intent);
                            dialog.dismiss(); // Dismiss the dialog after handling the click
                            detailsDialog.dismiss();
                        }
                    }
                });


                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Show a confirmation dialog before deleting the task
                        task = tasks.get(holder.getAdapterPosition());
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm Deletion");
                        builder.setMessage("Are you sure you want to delete this task?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Iterate over the tasks and delete the matching one from the database

                                SharedPreferences preferences=context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                String userJson = preferences.getString("user", "");
                                User user = null;
                                if (!userJson.isEmpty()) {
                                    Gson gson = new Gson();
                                    user = gson.fromJson(userJson, User.class);
                                    User updateuser=null;
                                    Map<String, tasks> taskMap = user.getTasks();

                                    if(!taskMap.isEmpty()) {
                                        for (Map.Entry<String, tasks> entry : taskMap.entrySet()) {
                                            String taskName = entry.getValue().getName();
                                            if (taskName != null && entry.getValue().getName().compareToIgnoreCase(task.getName()) == 0) {
                                                DatabaseReference userTasksRef = FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child("Data")
                                                        .child(User.key)
                                                        .child("tasks");
                                                updateuser = gson.fromJson(userJson, User.class);
                                                String userrJson = gson.toJson(updateuser);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                updateuser.removeTaskFromMap(entry.getKey(), task);
                                                editor.putString("user", userrJson);
                                                editor.apply();
                                                userTasksRef.child(entry.getKey()).removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Task deleted successfully
                                                                tasks.remove(holder.getAdapterPosition()); // Remove the deleted task from the list
                                                                notifyItemRemoved(holder.getAdapterPosition()); // Notify the adapter of the removed item
                                                                notifyItemRangeChanged(holder.getAdapterPosition(), tasks.size()); // Notify the adapter that the data set has changed
                                                                dialog.dismiss();
                                                                detailsDialog.dismiss();// Dismiss the dialog after handling the click
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Handle any errors that may occur
                                                                Log.e("DeleteTask", "Error deleting task from database", e);

                                                            }
                                                        });
                                                break; // Exit the loop after deleting the task
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
                                detailsDialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });



                shareImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tasks clickedTask = tasks.get(position); // Retrieve the corresponding task from the list
                        Intent intent = new Intent(context, Share.class);
                        intent.putExtra("task", clickedTask);
                        context.startActivity(intent);
                        dialog.dismiss();
                        detailsDialog.dismiss();
                    }
                });

            }
         else if (task.getAccess()==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialogue_view, null);
                    builder.setView(dialogView);
                    ImageView viewImage = dialogView.findViewById(R.id.view);
                    TextView editText = dialogView.findViewById(R.id.viewText);
                    viewImage.setImageResource(R.drawable.view);
                    editText.setText("View");

                    ImageView deleteImage = dialogView.findViewById(R.id.deleteImage);
                    TextView deleteText = dialogView.findViewById(R.id.deleteText);
                    deleteImage.setImageResource(R.drawable.delete_icon);
                    deleteText.setText("Delete");

                    viewDialog = builder.create();
                    viewDialog.show();


                    viewImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = holder.getAdapterPosition(); // Get the position of the clicked item
                            if (position != RecyclerView.NO_POSITION) {
                                tasks clickedTask = tasks.get(position); // Retrieve the corresponding task from the list
                                Intent intent = new Intent(context, view.class);
                                intent.putExtra("task", clickedTask);
                                context.startActivity(intent);
                                viewDialog.dismiss(); // Dismiss the dialog after handling the click
                            }
                        }
                    });


                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Show a confirmation dialog before deleting the task
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Confirm Deletion");
                            builder.setMessage("Are you sure you want to delete this task?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Iterate over the tasks and delete the matching one from the database
                                    SharedPreferences preferences=context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                    String userJson = preferences.getString("user", "");
                                    User user = null;
                                    if (!userJson.isEmpty()) {
                                        Gson gson = new Gson();
                                        user = gson.fromJson(userJson, User.class);
                                        Map<String, tasks> taskMap = user.getTasks();

                                        for (Map.Entry<String, tasks> entry : taskMap.entrySet()) {
                                            Log.d("deleted","bfgf"+entry.getValue().getName());
                                            if (entry.getValue().getName().compareToIgnoreCase(task.getName()) == 0) {

                                                DatabaseReference userTasksRef = FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child("Data")
                                                        .child(User.key)
                                                        .child("tasks");

                                                User updateuser = null;
                                                updateuser = gson.fromJson(userJson, User.class);
                                                String userrJson = gson.toJson( updateuser);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                updateuser.removeTaskFromMap(entry.getKey(),task);
                                                editor.putString("user",  userrJson);
                                                editor.apply();
                                                userTasksRef.child(entry.getKey()).removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Task deleted successfully
                                                                Log.d("deleted","bfgf");
                                                                tasks.remove(holder.getAdapterPosition()); // Remove the deleted task from the list
                                                                notifyItemRemoved(holder.getAdapterPosition()); // Notify the adapter of the removed item
                                                                notifyItemRangeChanged(holder.getAdapterPosition(), tasks.size()); // Notify the adapter that the data set has changed
                                                                dialog.dismiss();
                                                                viewDialog.dismiss();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Handle any errors that may occur
                                                                Log.e("DeleteTask", "Error deleting task from database", e);

                                                            }
                                                        });
                                                break; // Exit the loop after deleting the task
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
                                    viewDialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
                }
        });
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = tasks.get(holder.getAdapterPosition());
                if (starButton.getTag() == null || "outline".equals(starButton.getTag())) {
                    starButton.setImageResource(R.drawable.ic_star_filled);
                    starButton.setTag("filled");
                    task.setImportant(true);
                    Log.d("hshshs", task.getImportant().toString());
                } else {
                    starButton.setImageResource(R.drawable.ic_star_outline);
                    starButton.setTag("outline");
                    task.setImportant(false);
                    Log.d("hereeeee", String.valueOf(task.getImportant()));
                }


                //tasks.set(holder.getAdapterPosition(),task);
                //String ID=null;
                SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                String userJson = preferences.getString("user", "");
                User user = null;

                if (!userJson.isEmpty()) {
                    Gson gson = new Gson();
                    user = gson.fromJson(userJson, User.class);
                    Map<String, tasks> taskMap = user.getTasks();
                    for (Map.Entry<String, tasks> entry : taskMap.entrySet()) {
                        Log.d("Taskkkkkkkk", task.getName());
                        String taskName = entry.getValue().getName();
                        if (taskName != null && taskName.equalsIgnoreCase(task.getName())) {
                            editForSharedContacts(user,  task);
                            Log.d("status", String.valueOf(task.getImportant()));
                            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
                            userTasksRef.child(entry.getKey()).child("important").setValue(task.getImportant())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Firebase", "Database update successful");

                                            } else {
                                                Log.e("Firebase", "Database update failed: " + task.getException());
                                            }
                                        }
                                    });
                        }
                    }
                }




            }
        });



        CheckBox circularCheckbox = cardView.findViewById(R.id.circular_checkbox);
        circularCheckbox.setButtonDrawable(R.drawable.checkbox_circle);
        circularCheckbox.setPadding(16, 16, 16, 16);
        circularCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task = tasks.get(holder.getAdapterPosition());
                if (isChecked) {
                    // Checkbox is checked, change its color to green
                    circularCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00796B")));
                    task.setCompleted(true);
                    tasks.remove(holder.getAdapterPosition()); // Remove the task from the list
                    notifyItemRemoved(holder.getAdapterPosition()); // Notify the adapter of the removed item
                    notifyItemRangeChanged(holder.getAdapterPosition(), tasks.size()); // Notify the adapter of the range change
                } else {
                    // Checkbox is unchecked, change its color to the default color
                    circularCheckbox.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
                    task.setCompleted(false);

                }
               // tasks.set(holder.getAdapterPosition(), task);
                String ID=null;
                SharedPreferences preferences= context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                String userJson = preferences.getString("user", "");
                User user=null;

                if (!userJson.isEmpty()) {
                    Gson gson = new Gson();
                    user = gson.fromJson(userJson, User.class);
                    Map<String,tasks>taskMap=user.getTasks();
                    for(Map.Entry<String,tasks> entry: taskMap.entrySet()){
                        Log.d("Taskkkkkkkk",task.getName());
                        String taskName = entry.getValue().getName();
                        if (taskName != null && entry.getValue().getName().compareToIgnoreCase(task.getName())==0){
                            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
                            userTasksRef.child(entry.getKey()).child("completed").setValue(task.getCompleted());
                            editForSharedContacts(user,  task);
                        }
                    }
                }
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


    private void editForSharedContacts(User user, tasks task) {
        Log.d("hello", "hieiei");
        Log.d("name", "" + user.getName());
        List<contacts> contactList = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        if (!userJson.isEmpty()) {
            Gson gson = new Gson();
            user = gson.fromJson(userJson, User.class);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Data");
        usersReference.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUsernameFound = false;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            User userr = userSnapshot.getValue(User.class);
                            Log.d("LoginInfo", "Ba7000000000000 " + userr.getEmail());
                            contactsMap = userr.getContacts();
                            if (contactsMap != null) {
                                for (Map.Entry<String, contacts> entry : contactsMap.entrySet()) {
                                    contactList.add(entry.getValue());
                                    Log.d("LoginInfo", "contact " + entry.getValue().getEmail());

                                }
                            }
                            for (contacts entry:contactList) {
                                Log.d("ooooooooooooooooooooo", "ppoll" );
                                if (entry.getShared() == 1) {
                                    Log.d("name", "" + entry.getName());
                                    String email = entry.getEmail();
                                    if (email != null && !email.isEmpty()) {
                                        contactEmails.add(email);
                                        Log.d("email", email);
                                    }
                                }
                            }
                            // Now you can use the contactEmails list for further processing
                        } else {
                            Log.d("contacts", "Contacts map is null");
                        }

                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Data");
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    User currentUser = userSnapshot.getValue(User.class);
                                    Log.d("UserData", "Snapshot key: " + userSnapshot.getKey());
                                    Log.d("UserData", "Current user: " + currentUser);
                                    if (currentUser != null) {
                                        String userEmail = currentUser.getEmail();
                                        Log.d("UserEmail", "Current user email: " + userEmail);
                                        Log.d("UserEmail", "Boolean: " + contactEmails.contains(userEmail));
                                        if (userEmail != null && contactEmails.contains(userEmail)) {
                                            Log.d("maybveee", "Task found for user with email: " + userEmail);
                                            Map<String, tasks> userTasks = currentUser.getTasks();
                                            if (userTasks != null) {
                                                for (Map.Entry<String, tasks> entry : userTasks.entrySet()) {
                                                    tasks userTask = entry.getValue();
                                                    Log.d("TasfffkFound", "check " + userTask.getName());
                                                    if (userTask.getName().equals(task.getName())) {
                                                        Log.d("TaskFound", "Found matching task: " + userTask.getName());
                                                        DatabaseReference userTasksRef = userSnapshot.child("tasks").getRef();
                                                        userTasksRef.child(entry.getKey()).child("completed").setValue(task.getCompleted());
                                                        userTasksRef.child(entry.getKey()).child("important").setValue(task.getImportant());

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Error reading data from Firebase", error.toException());
                            }
                        });
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}

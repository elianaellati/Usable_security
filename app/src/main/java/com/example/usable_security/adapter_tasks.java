package com.example.usable_security;

import android.content.Context;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class adapter_tasks extends RecyclerView.Adapter<adapter_tasks.ViewHolder> {

    private List<tasks> tasks;
    private Context context;
    private tasks task;



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


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String[] id = {User.key};
        CardView cardView = holder.cardView;
        TextView namee = cardView.findViewById(R.id.name);
        namee.setText(tasks.get(position).getName());
        Log.d("userId", id[0]);
         task = tasks.get(holder.getAdapterPosition());
        ImageButton starButton = cardView.findViewById(R.id.starButton);

        ImageButton details=cardView.findViewById(R.id.detailsButton);


        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Create a custom layout inflater for the dialog items
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialogue_details, null);
                builder.setView(dialogView);

//                 Set up the dialog options with images and text
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


                // Set click listeners for the options
                editImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, details.class);
                        intent.putExtra("task", task);
                        context.startActivity(intent);
                        dialog.dismiss(); // Dismiss the dialog after handling the click
                    }
                });

                deleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle the click event for the "Delete" option
                        // Add your delete logic here
                        dialog.dismiss(); // Dismiss the dialog after handling the click
                    }
                });

                shareImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle the click event for the "Share" option
                        // Add your share logic here
                        dialog.dismiss(); // Dismiss the dialog after handling the click
                    }
                });

                dialog.show();
            }
        });


        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (starButton.getTag() == null || starButton.getTag().equals("outline")) {
                    starButton.setImageResource(R.drawable.ic_star_filled);
                    starButton.setTag("filled");
                    task.setImportant(true);
                    Log.d("hshshs",task.getImportant().toString());
                } else {
                    starButton.setImageResource(R.drawable.ic_star_outline);
                    starButton.setTag("outline");
                    task.setImportant(false);
                }
                tasks.set(holder.getAdapterPosition(),task);
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
                        if(entry.getValue().getName().compareToIgnoreCase(task.getName())==0){
                            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
                            userTasksRef.child(entry.getKey()).child("important").setValue(task.getImportant());
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
                if (isChecked) {
                    // Checkbox is checked, change its color to green
                    circularCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00796B")));
                    task.setCompleted(true);
                } else {
                    // Checkbox is unchecked, change its color to the default color
                    circularCheckbox.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                    task.setCompleted(false);
                }
                tasks.set(holder.getAdapterPosition(), task);
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
                        if(entry.getValue().getName().compareToIgnoreCase(task.getName())==0){
                            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("tasks");
                            userTasksRef.child(entry.getKey()).child("completed").setValue(task.getCompleted());
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



}

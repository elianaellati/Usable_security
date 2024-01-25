package com.example.usable_security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ShareAdapter
        extends RecyclerView.Adapter<ShareAdapter.ViewHolder> implements Executor{
    int flag = 0;
    int flagg = 0;
    int enter=0;
    FirebaseAuth auth;
    CheckBox vieww;
    private BiometricPrompt biometricPrompt;
    CheckBox edit;
    int counter = 0;
    private int clickedPosition = -1;
    User storeduser;
    private List<contacts> contact;
    private contacts contactt;
    private SharedPreferences preferences;
    private Context context;
    private tasks task;
    int attempt = 0;
    private String keycontact;
    private String name;
    Map<String, contacts> contactsMap;
    private TextInputLayout emailInputLayout;

    public ShareAdapter(List<contacts> contact, tasks task, Map<String, contacts> contactsMap) {
        this.contactsMap = contactsMap;
        this.task = task;
        this.contact = contact;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.sharecardview,
                parent,
                false);
        auth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        String foundKey = null;
        ImageButton share = cardView.findViewById(R.id.shareImage);
        contactt = contact.get(position);
        Log.d("LoginInfo", "elianaaa" + contact.get(position).getName());
        TextView namee = (TextView) cardView.findViewById(R.id.name);
        namee.setText(contact.get(position).getName());
        TextView emaill = (TextView) cardView.findViewById(R.id.email);
        emaill.setText(contact.get(position).getEmail());
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedPosition = position;

                ShareDialogue(clickedPosition);

            }
        });

    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    @Override
    public void execute(Runnable runnable) {

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }

    }

    public void ShareDialogue(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.access_dialogue, null);
        builder.setView(dialogView);
        vieww = dialogView.findViewById(R.id.view);
        edit = dialogView.findViewById(R.id.edit);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               BiometricPrompt biometricPrompt = getPrompt(position);
               biometricPrompt.authenticate(getPromptInfo());


                if (flag == 2) {
                    Log.d("LoginInfo", "Keyyyyyyy" + contact.get(position).getName());
                    findKey();
                    searchtheuser(position);
                    task.setShared(1);

                    if (vieww.isChecked() && edit.isChecked()) {
                        task.setAccess(1);
                    } else if (vieww.isChecked()) {
                        Log.d("LoginInfo", "Incorrect password for username: ");
                        task.setAccess(0);
                    } else {
                        task.setAccess(1);
                    }

                }



              /*  if(attempt!=2) {
                    PasswordDialogue(position);
                }
              //  BiometricPrompt biometricPrompt = getPrompt(position);
             //   biometricPrompt.authenticate(getPromptInfo());*/
            /*  if (flag == 2) {


                    Log.d("LoginInfo", "Keyyyyyyy" + contact.get(position).getName());
                    findKey();
                    searchtheuser(position);
                    task.setShared(1);

                    if (vieww.isChecked() && edit.isChecked()) {
                        task.setAccess(1);
                    } else if (vieww.isChecked()) {
                        Log.d("LoginInfo", "Incorrect password for username: ");
                        task.setAccess(0);
                    } else {
                        task.setAccess(1);
                    }
                }*/

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


    public void findKey() {
        for (Map.Entry<String, contacts> entry : contactsMap.entrySet()) {
            if (entry.getValue().getEmail().compareToIgnoreCase(contactt.getEmail()) == 0) {
                keycontact = entry.getKey();
                Log.d("LoginInfo", "Keyyyyyyy" + keycontact);

            }
        }
    }

    public void searchtheuser(int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Data");
        preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        Gson gson = new Gson();
        storeduser = gson.fromJson(userJson, User.class);
        name = storeduser.getName();
        usersReference.orderByChild("email").equalTo(contact.get(position).getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUsernameFound = false;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        Map<String, tasks> taskk = user.getTasks();
                        String userKey = userSnapshot.getKey();
                        isUsernameFound = true;
                        task.setShared(1);
                        task.setShareduser(name);
                        task.setEmail(storeduser.getEmail());
                        if (taskk != null) {
                            for (Map.Entry<String, tasks> entry : taskk.entrySet()) {
                                Log.d("LoginInfo", "Incorrect password for username: " + entry.getValue().getName());
                                Log.d("LoginInfo", "Incorrect password for username: " + entry.getValue().equals(task));
                                if (entry.getValue().getName().compareToIgnoreCase(task.getName()) == 0) {
                                    Log.d("LoginInfo", "Ba700000000000000000 ");
                                    flagg = 1;
                                    break;
                                }
                            }
                        }
                        if(flagg==1){
                            Log.d("LoginInfo", "Bavavaahahhahaha");
                            Alreadyshared();
                        }
                        if (flagg == 0) {
                            DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference().child("Data").child(userKey).child("tasks");
                            DatabaseReference newTaskRef = userTasksRef.push();
                            newTaskRef.setValue(task);
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Data").child(User.key).child("contacts");
                            userRef.child(keycontact).child("shared").setValue(1);
                            SuccessDialog();
                        }
                    }
                }

                if (!isUsernameFound) {

                    Log.d("LoginInfo", "Incorrect password for username: ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private BiometricPrompt getPrompt(int position) {

        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d("LoginInfo", "CODE" + errString);
                ++counter;
                if (counter == 3) {
                    PasswordDialogue(position);
                    biometricPrompt.cancelAuthentication();
                }
                if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    PasswordDialogue(position);
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                flag = 2;

            }

            @Override
            public void onAuthenticationFailed() {
                ++counter;
                super.onAuthenticationFailed();
                if (counter == 3) {
                    PasswordDialogue(position);
                    biometricPrompt.cancelAuthentication();
                }


            }
        };

        biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor, callback);
        return biometricPrompt;
    }


    private BiometricPrompt.PromptInfo getPromptInfo() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate using your biometric data")
                .setNegativeButtonText("Cancel")
                .build();
    }



    public void PasswordDialogue(int position) {
        enter=1;
        preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String userJson = preferences.getString("user", "");
        Gson gson = new Gson();

        storeduser = gson.fromJson(userJson, User.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.password_dialogue, null);
        builder.setView(dialogView);
        EditText Password = dialogView.findViewById(R.id.EditPassword);
        emailInputLayout = dialogView.findViewById(R.id.passw);

        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented due to the TextWatcher interface
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed, but must be implemented due to the TextWatcher interface
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    // If there is text in the EditText, remove the hint from the TextInputLayout
                    emailInputLayout.setHint(null);
                } else {
                    // If the EditText is empty, restore the hint in the TextInputLayout
                    emailInputLayout.setHint("Enter your password");
                }
            }
        });

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
                auth = FirebaseAuth.getInstance();
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        preferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                        String userJson = preferences.getString("user", "");
                        Gson gson = new Gson();
                        storeduser = gson.fromJson(userJson, User.class);
                        String password = Password.getText().toString();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersReference = database.getReference("Data");

                        if (!password.equals("") && attempt != 2) {
                            Log.d("LoginInfo", "YAMOOOO77");
                            Executor executor = Executors.newSingleThreadExecutor();
                            auth.signInWithEmailAndPassword(storeduser.getEmail(), password)
                                    .addOnCompleteListener(executor, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> taskk) {
                                            if (taskk.isSuccessful()) {
                                                                        Log.d("LoginInfo", "YAMOOOO99");
                                                                        findKey();
                                                                        searchtheuser(position);
                                                                        task.setShared(1);

                                                                        if (vieww.isChecked() && edit.isChecked()) {
                                                                            task.setAccess(1);
                                                                        } else if (vieww.isChecked()) {
                                                                            task.setAccess(0);
                                                                        } else {
                                                                            task.setAccess(1);
                                                                        }
                                                                        attempt = 2;
                                                                        dialog.dismiss();

                                                                    } else {
                                                                        ++attempt;
                                                                        if (attempt == 2) {
                                                                            dialog.dismiss();
                                                                            showAttemptDialog();
                                                                        }
                                                                        Password.setText(""); // Clear the password field
                                                                    }




                                        }
                                    });
                        }
                    }
                });
            }
        });

        dialog.show();
    }


    private void showAttemptDialog() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialogTheme));

                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.attempt, null);
                builder.setView(dialogView);
                builder.setCancelable(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Code to be executed after the delay
                        Intent intent = new Intent(context, lock.class);
                        context.startActivity(intent);
                    }
                }, 3 * 1000);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    private void SuccessDialog() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialogTheme));

                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.right, null);
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
                }, 3*1000);


            }
        });
    }

    private void Alreadyshared() {
        Log.d("LoginInfo", "MARIAAAAAAAAAAAAAAA");
      new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialogTheme));

                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.already, null);
                builder.setView(dialogView);
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                            Log.d("LoginInfo", "HALAAAAAAAAAAAAAAAAA");
                            alertDialog.dismiss();

                    }
                }, 3*1000);


            }
        });
    }

}





                       /*  usersReference.orderByChild("username").equalTo(storeduser.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 boolean isUsernameFound = false;
                                 if (dataSnapshot.exists()) {
                                     for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                         User user = userSnapshot.getValue(User.class);
                                         Log.d("LoginInfo", "ba88888 " + user.getName());
                                         if (user != null) {
                                             if (user.getPassword().equals(password)) {
                                                 Log.d("LoginInfo", "Keyyyyyyy" + contact.get(position).getName());
                                                 findKey();
                                                 searchtheuser(position);
                                                 task.setShared(1);

                                                 if (vieww.isChecked() && edit.isChecked()) {
                                                     task.setAccess(1);
                                                 } else if (vieww.isChecked()) {
                                                     Log.d("LoginInfo", "Incorrect password for username: ");
                                                     task.setAccess(0);
                                                 } else {
                                                     task.setAccess(1);
                                                 }
                                                 attempt = 2;
                                                 // Dismiss the dialog only if the condition is met
                                                 if (attempt == 2) {
                                                     dialog.dismiss();
                                                 }
                                             } else {
                                                 ++attempt;
                                                 if (attempt == 2) {
                                                     dialog.dismiss();
                                                     showAttemptDialog();
                                                 }
                                                 Password.setText(""); // Clear the password field
                                             }
                                         }
                                     }
                                 } else {

                                 }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {
                                 // Handle errors here
                                 Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                             }
                         });*/











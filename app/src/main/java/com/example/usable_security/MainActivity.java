package com.example.usable_security;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.concurrent.Executor;

// ... imports ...


public class MainActivity extends AppCompatActivity {
static int attempt=0;
static int count=3;
static long duaration=60;

    TextView status;
    TextView forgotPassword;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        status = findViewById(R.id.status);
        forgotPassword = findViewById(R.id.forgotPassword);


        TextInputEditText usernameEditText = findViewById(R.id.usernameEditText);
        TextInputEditText passwordEditText = findViewById(R.id.EditPassword);
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInputLayout);
        TextInputLayout usernameInputLayout = findViewById(R.id.usernameInputLayout);
        auth = FirebaseAuth.getInstance();
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // When the EditText gains focus, clear the hint
                    passwordInputLayout.setHintEnabled(false);
                } else {
                    // When the EditText loses focus, restore the hint if no text is entered
                    if (passwordEditText.getText().toString().isEmpty()) {
                        passwordInputLayout.setHintEnabled(true);
                    }
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the login page (replace LoginActivity.class with your actual login activity)
                Intent intent = new Intent(MainActivity.this, forgotPassword.class);
                startActivity(intent);
            }
        });


        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // When the EditText gains focus, clear the hint
                    usernameInputLayout.setHintEnabled(false);
                } else {
                    // When the EditText loses focus, restore the hint if no text is entered
                    if (usernameEditText.getText().toString().isEmpty()) {
                        usernameInputLayout.setHintEnabled(true);
                    }
                }
            }
        });


        EditText Password = findViewById(R.id.EditPassword);
//        Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        TextView loginLink = findViewById(R.id.signupLink);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                //  startActivity(intent);
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        Button loginn = findViewById(R.id.buttonlogin);
        loginn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Username = findViewById(R.id.usernameEditText);
                String email = Username.getText().toString().trim();
                String password = Password.getText().toString().trim();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersReference = database.getReference("Data");

                   if (!email.equals("") && !password.equals("")) {
                       usersReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               boolean isUsernameFound = false;

                               if (dataSnapshot.exists()) {
                                   for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                       User user = userSnapshot.getValue(User.class);
                                       Log.d("LoginInfo", "ba88888 " + user.getName());
                                       if (user != null) {
                                           if (user.getPassword().equals(password)) {

                                               String userId = userSnapshot.getKey();
                                               User.key = userId;
                                               SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                               SharedPreferences.Editor editor = preferences.edit();
                                               editor.clear();
                                               Gson gson = new Gson();
                                               String userJson = gson.toJson(user);
                                               editor.putString("user", userJson);
                                               editor.apply();
                                               Log.d("LoginInfo", "Login successful. Username: " + user.getEmail());
                                               Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                               startActivity(intent);
                                               isUsernameFound = true;
                                               break; // No need to continue checking other users
                                           } else {
                                               ++attempt;
                                               if(attempt==count){
                                                   Intent intent = new Intent(MainActivity.this, lock.class);
                                                   startActivity(intent);
                                               }
                                               Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   }
//                                if (!isUsernameFound) {
//                                    // Password does not match or the username is found but with incorrect password
//                                    Log.d("LoginInfo", "Incorrect password for username: " + password);
//                                    Toast.makeText(MainActivity.this, "Incorrect password" , Toast.LENGTH_SHORT).show();
//                                }
                               } else {
                                   // Username does not exist in the database

                                   Toast.makeText(MainActivity.this, "Username not found: " + username, Toast.LENGTH_SHORT).show();
                               }
                           }

                if (email.isEmpty()) {
                    TextView errorMessageTextView = findViewById(R.id.errorMessageTextView2);
                    errorMessageTextView.setText("Username is required");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }

                if (password.isEmpty()) {
                    TextView errorMessageTextView = findViewById(R.id.errorMessageTextView4);
                    errorMessageTextView.setText("Password is required");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }

                if (!email.equals("") && !password.equals("")) {


                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User is successfully authenticated
                                        Log.d("LoginInfo", "ba88888 ");
                                        usersReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                        User user = userSnapshot.getValue(User.class);
                                                        Log.d("LoginInfo", "ba88888 " + user.getName());
                                                        String userId = userSnapshot.getKey();
                                                            Log.d("LoginInfo", "jsjsjsj " + userId);
                                                            User.key = userId;
                                                            SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.clear();
                                                            Gson gson = new Gson();
                                                            String userJson = gson.toJson(user);
                                                            editor.putString("user", userJson);
                                                            editor.apply();
                                                            Log.d("LoginInfo", "Login successful. Username: " + user.getEmail());
                                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                            return; // No need to continue checking other users
                                                        }
                                                    }
                                                // Handle the case where the user data is not found
                                                Log.d("LoginInfo", "User data not found");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Handle errors here
                                                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                                            }
                                        });
                                    } else {
                                        // Authentication failed
                                        status.setText("Incorrect username or password");
                                    }
                                }
                            });

                }
            }
        });
    }
}
//                       usersReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
//                           @Override
//                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                               boolean isUsernameFound = false;
//
//                               if (dataSnapshot.exists()) {
//                                   for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                                       User user = userSnapshot.getValue(User.class);
//                                       Log.d("LoginInfo", "ba88888 " + user.getName());
//                                       if (user != null) {
//
//
//                                           if (user.getPassword().equals(password)) {
//                                               String userId = userSnapshot.getKey();
//                                               User.key = userId;
//                                               SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//                                               SharedPreferences.Editor editor = preferences.edit();
//                                               editor.clear();
//                                               Gson gson = new Gson();
//                                               String userJson = gson.toJson(user);
//                                               editor.putString("user", userJson);
//                                               editor.apply();
//                                               Log.d("LoginInfo", "Login successful. Username: " + user.getEmail());
//                                               Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                                               startActivity(intent);
//                                               isUsernameFound = true;
//                                               break; // No need to continue checking other users
//                                           } else {
//                                               status.setText("Incorrect password");
//                                           }
//                                       }
//                                   }
////                                if (!isUsernameFound) {
////                                    // Password does not match or the username is found but with incorrect password
////                                    Log.d("LoginInfo", "Incorrect password for username: " + password);
////                                    Toast.makeText(MainActivity.this, "Incorrect password" , Toast.LENGTH_SHORT).show();
////                                }
//                               } else {
//                                   // Username does not exist in the database
//                                   Log.d("LoginInfo", "Username not found: " + username);
//                                   status.setText("Username not found");
//                               }
//                           }
//
//
//                           @Override
//                           public void onCancelled(@NonNull DatabaseError databaseError) {
//                               // Handle errors here
//                               Log.e("FirebaseError", "Error: " + databaseError.getMessage());
//                           }
//                       });
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.concurrent.Executor;

// ... imports ...

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        EditText Password=findViewById(R.id.EditPassword);
        Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
       Button loginn=findViewById(R.id.buttonlogin);
        loginn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {EditText Username=findViewById(R.id.usernameEditText);
                String username=Username.getText().toString();
                String password=Password.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersReference = database.getReference("Data");
                usersReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isUsernameFound = false;

                        if (dataSnapshot.exists()) {

                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);

                                if (user != null && user.getPassword().equals(password)) {

                                    String userId =  userSnapshot.getKey();
                                    User.key=userId;
                                    SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    Gson gson = new Gson();
                                    String userJson = gson.toJson(user);
                                    editor.putString("user", userJson);
                                    editor.apply();

                                    Log.d("LoginInfo", "Login successful. Username: " + user.getEmail());

                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    isUsernameFound = true;
                                    break; // No need to continue checking other users
                                }
                            }

                            if (!isUsernameFound) {
                                // Password does not match or the username is found but with incorrect password
                                Log.d("LoginInfo", "Incorrect password for username: " + password);
                            }
                        } else {
                            // Username does not exist in the database
                            Log.d("LoginInfo", "Username not found: " + username);
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



}

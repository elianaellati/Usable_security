package com.example.usable_security;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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
                /*Log.d("MainActivity", "This is a debug message");
                FirebaseDatabase database =FirebaseDatabase.getInstance();
                DatabaseReference myRef=database.getReference("message");
                myRef.setValue("hellooooo,World")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Write was successful!
                                Log.d("MainActivity", "This is a debug message");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                Log.e(TAG, "Error writing data", e);
                            }
                        });*/
                // Open the login page (replace LoginActivity.class with your actual login activity)
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
       Button loginn=findViewById(R.id.buttonlogin);
        loginn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Username=findViewById(R.id.usernameEditText);
                String username=Username.getText().toString();
                String password=Password.getText().toString();
                if(!username.isEmpty()&& !password.isEmpty()){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dataRef = database.getReference("Data");
                    dataRef.orderByChild("Username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean usernameFound = false;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                usernameFound = true;

                                String storedPassword = snapshot.child("Password").getValue(String.class);
                                String storedusername=snapshot.child("Username").getValue(String.class);
                                if ( storedPassword.equals(password) && storedusername.equals(storedusername)) {
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);

                                } else {
                                    // Username found, but password does not match
                                    Log.d("FirebaseData", "Password does not match");
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors if any
                            Log.e("FirebaseData", "Error retrieving data", databaseError.toException());
                        }
                    });
                }
            }
    });



    }



}

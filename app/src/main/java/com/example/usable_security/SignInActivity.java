package com.example.usable_security;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class SignInActivity extends AppCompatActivity {

    int flag = 0;

    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView loginLink = findViewById(R.id.login);
         status=findViewById(R.id.status);
        EditText Password = findViewById(R.id.EditPassword);
        Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button signup = findViewById(R.id.buttonsignup);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the login page (replace LoginActivity.class with your actual login activity)
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Button btn = findViewById(R.id.buttonverify);
        btn.setOnClickListener(view -> {
            BiometricPrompt biometricPrompt = getPrompt();
            biometricPrompt.authenticate(getPromptInfo());
        });
        //  signup.setOnClickListener(view -> {

//
        EditText Name = findViewById(R.id.nameEditText);
        EditText Username = findViewById(R.id.usernameEditText);
        EditText Email = findViewById(R.id.emailEditText);
//
//
//
//                if(flag==0){
//              String name=Name.getText().toString();
//              String username=Username.getText().toString();
//              String email=Email.getText().toString();
//              String password=Password.getText().toString();
//          if(!name.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
//          HashMap<String,Object> hashmap=new HashMap<>();
//          hashmap.put("Name",name);
//          hashmap.put("Username", username);
//          hashmap.put("Email",email);
//          hashmap.put("Password",password);
//          FirebaseDatabase database =FirebaseDatabase.getInstance();
//          DatabaseReference Ref= database.getReference("Data");;
//          String key=Ref.push().getKey();
//          hashmap.put("key",key);
//          Ref.child(key).setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
//              @Override
//              public void onComplete(@NonNull Task<Void> task) {
//                  Toast.makeText(SignInActivity.this,"Added",Toast.LENGTH_SHORT).show();
//
//
//              }
//          });
//          Name.getText().clear();
//          Email.getText().clear();
//          Password.getText().clear();
//          Username.getText().clear();
//          Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
//          startActivity(intent);
//      }
//
        //  }
        // });


        signup.setOnClickListener(view -> {
            // Get user input (name, email, password, etc.)
            String name = Name.getText().toString();
            String username = Username.getText().toString();
            String email = Email.getText().toString();
            String password = Password.getText().toString();

            // Create a new user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign up success, send verification email to the user
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                sendEmailVerification(user, name,username,email,password);
                            }
                        } else {
                            // Sign up failed, handle the error (e.g., display an error message)
                            Log.e(TAG, "Failed to sign up: " + task.getException());
                        }
                    });
        });


    }

    private BiometricPrompt getPrompt() {

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                flag = 1;
                notifyUser(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                flag = 2;
                notifyUser("Authentication Succeeded!");
            }

            @Override
            public void onAuthenticationFailed() {
                flag = 1;
                super.onAuthenticationFailed();
                notifyUser("Authentication Failed!");
            }
        };

        return new BiometricPrompt(this, executor, callback);
    }

    private BiometricPrompt.PromptInfo getPromptInfo() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Authenticate using your biometric data")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                    } else {
                        // Handle the case where sending email verification fails
                        Log.e(TAG, "Failed to send email verification.", task.getException());
                    }
                });
    }


    private void sendEmailVerification(FirebaseUser user, String name, String username, String email, String password) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Email verification sent, handle the success (e.g., show a message to the user)
                        Log.d(TAG, "Email verification sent.");
                        addUserToDatabase(name, username, email,password);
                        // Schedule a task to check email verification status after one minute
                        checkEmailVerificationStatus(user, name, username, email, password);
                    } else {
                        // Email verification failed, handle the error (e.g., show a message to the user)
                        Log.e(TAG, "Failed to send email verification: " + task.getException());
                    }
                });
    }

//    private void checkEmailVerificationStatus(FirebaseUser user, String name, String username, String email, String password) {
//        user.reload().addOnCompleteListener(reloadTask -> {
//            if (reloadTask.isSuccessful()) {
//                FirebaseUser reloadedUser = FirebaseAuth.getInstance().getCurrentUser();
//                if (reloadedUser != null && reloadedUser.isEmailVerified()) {
//                    // The user has verified their email within one minute
//                    // Add the user to the database here
//                    addUserToDatabase(name, username, email, password);
//                    Log.e(TAG,"ADDED");
//                } else {
//                    // The user's email is not verified yet
//                    // You can handle this case by prompting the user to verify their email again or providing an option to resend the verification email
//                    Log.e(TAG, "Email not verified within one minute, consider resending verification email");
//                }
//            } else {
//                // Failed to reload user
//                Log.e(TAG, "Failed to reload user: " + reloadTask.getException());
//                // Handle the error
//            }
//        });
//    }


    private void checkEmailVerificationStatus(FirebaseUser user, String name, String username, String email, String password) {
        status.setText("Waiting for email verification...");
        new Handler().postDelayed(() -> {
            user.reload().addOnCompleteListener(reloadTask -> {
                if (reloadTask.isSuccessful()) {
                    FirebaseUser reloadedUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (reloadedUser != null && reloadedUser.isEmailVerified()) {
                        // The user has verified their email within one minute
                        // Add the user to the database here
                        addUserToDatabase(name, username, email, password);

                    } else {
                        // The user's email is not verified within one minute
                        // You can handle this case by prompting the user to verify their email again or providing an option to resend the verification email
                        Log.e(TAG, "Email not verified within one minute");

                    }
                } else {
                    // Failed to reload user
                    Log.e(TAG, "Failed to reload user: " + reloadTask.getException());
                    // Handle the error
                }
            });
        }, 30000); // 60,000 milliseconds = 1 minute
    }


    private void addUserToDatabase(String name, String username, String email, String password) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Data");
        String userId = usersReference.push().getKey();
        User user = new User(name,username,password,email);
        usersReference.child(userId).setValue(user);

    }

}

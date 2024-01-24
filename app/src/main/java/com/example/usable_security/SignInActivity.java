package com.example.usable_security;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class SignInActivity extends AppCompatActivity {

    int flag = 0;

    TextView status;

    EditText Name;
    EditText Username;
    EditText Email;
    EditText Password;
    TextView bio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView loginLink = findViewById(R.id.login);
        User.key=null;
        status = findViewById(R.id.status);
        Password = findViewById(R.id.EditPassword);
        bio=findViewById(R.id.bio);
        Button signup = findViewById(R.id.buttonsignup);

        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInputLayout);
        TextInputEditText passwordEditText = findViewById(R.id.EditPassword);

        TextInputEditText usernameEditText = findViewById(R.id.usernameEditText);
        TextInputLayout usernameInputLayout = findViewById(R.id.usernameInputLayout);

        TextInputEditText nameEditText = findViewById(R.id.nameEditText);
        TextInputLayout nameInputLayout = findViewById(R.id.nameInputLayout);

        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputLayout emailInputLayout = findViewById(R.id.emailInputLayout);

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

        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // When the EditText gains focus, clear the hint
                    nameInputLayout.setHintEnabled(false);
                } else {
                    // When the EditText loses focus, restore the hint if no text is entered
                    if (nameEditText.getText().toString().isEmpty()) {
                        nameInputLayout.setHintEnabled(true);
                    }
                }
            }
        });
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // When the EditText gains focus, clear the hint
                    emailInputLayout.setHintEnabled(false);
                } else {
                    // When the EditText loses focus, restore the hint if no text is entered
                    if (emailEditText.getText().
                            toString().isEmpty()) {
                        emailInputLayout.setHintEnabled(true);
                    }
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the login page (replace LoginActivity.class with your actual login activity)
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btn = findViewById(R.id.buttonverify);
        btn.setOnClickListener(view -> {
            BiometricPrompt biometricPrompt = getPrompt();
            biometricPrompt.authenticate(getPromptInfo());
        });
        //  signup.setOnClickListener(view -> {

//
        Name = findViewById(R.id.nameEditText);
        Username = findViewById(R.id.usernameEditText);
        Email = findViewById(R.id.emailEditText);
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
            String name = Name.getText().toString().trim();
            String username = Username.getText().toString().trim();
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();

            if (name.isEmpty()) {
                TextView errorMessageTextView = findViewById(R.id.errorMessageTextView1);
                errorMessageTextView.setText("Name is required");
                errorMessageTextView.setVisibility(View.VISIBLE);
            }

            if (username.isEmpty()) {
                TextView errorMessageTextView = findViewById(R.id.errorMessageTextView2);
                errorMessageTextView.setText("Username is required");
                errorMessageTextView.setVisibility(View.VISIBLE);
            }
            if (email.isEmpty()) {
                TextView errorMessageTextView = findViewById(R.id.errorMessageTextView3);
                errorMessageTextView.setText("Email is required");
                errorMessageTextView.setVisibility(View.VISIBLE);
            }
            if (password.isEmpty()) {
                TextView errorMessageTextView = findViewById(R.id.errorMessageTextView4);
                errorMessageTextView.setText("Password is required");
                errorMessageTextView.setVisibility(View.VISIBLE);
            }

            if(flag==0){
                bio.setVisibility(View.VISIBLE);
            }

            if (!username.equals("") && !password.equals("") && !email.equals("") && !password.equals("") && flag == 2) {

                // Create a new user with email and password
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign up success, send verification email to the user
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    sendEmailVerification(user, name, username, email, password);
                                }
                            } else {
                                // Sign up failed, handle the error (e.g., display an error message)
                                Log.e(TAG, "Failed to sign up: " + task.getException().getMessage());
                                status.setText(task.getException().getMessage());
                            }
                        });
            }
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


//    private void sendEmailVerification(FirebaseUser user) {
//        user.sendEmailVerification()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Log.d(TAG, "Email sent.");
//                    } else {
//                        // Handle the case where sending email verification fails
//                        Log.e(TAG, "Failed to send email verification.", task.getException());
//                    }
//                });
//    }


    private void sendEmailVerification(FirebaseUser user, String name, String username, String email, String password) {
        if (!user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email verification sent, handle the success (e.g., show a message to the user)
                            Log.d(TAG, "Email verification sent.");
                            //   addUserToDatabase(name, username, email,password);
                            // Schedule a task to check email verification status after one minute
                            checkEmailVerificationStatus(user, name, username, email, password);
                        } else {
                            // Email verification failed, handle the error (e.g., show a message to the user)
                            Log.e(TAG, "Failed to send email verification: " + task.getException());
                        }
                    });
        }
    }


    private void checkEmailVerificationStatus(FirebaseUser user, String name, String username, String email, String password) {
        status.setText("Waiting for email verification...");
        new Handler().postDelayed(() -> {
            user.reload().addOnCompleteListener(reloadTask -> {
                if (reloadTask.isSuccessful()) {
                    FirebaseUser reloadedUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (reloadedUser != null && reloadedUser.isEmailVerified() && flag == 2) {
                        addUserToDatabase(name, username, email);

                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "Email not verified within one minute");
                        showResendEmailDialog(email, name, username, password);
                    }
                } else {
                    // Failed to reload user
                    Log.e(TAG, "Failed to reload user: " + reloadTask.getException());
                    // Handle the error
                }
            });
        }, 30000); // 60,000 milliseconds = 1 minute
    }


    private void addUserToDatabase(String name, String username, String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("Data");

        // Generate a unique user ID
        String userId = usersReference.push().getKey();

        User user = new User(name, username,email);


        // Store the user information in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        usersReference.child(userId).setValue(user);
        User.key = userId;
        Log.d("LoginInfo", "IDDD" + User.key);
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString("user", userJson);
        editor.commit();
        Log.d("LoginInfo", "Login successful. Username: " + user.getEmail());
    }
    private void showResendEmailDialog(String email, String name, String username, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resend Email Verification")
                .setMessage("Haven't received the verification email? Click below to resend.")
                .setPositiveButton("Resend", (dialog, which) -> {
                    // Resend the verification email
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        resendVerificationEmail(user.getEmail());
                    } else {
                        // Handle the case where the user is null or not signed in
                        Toast.makeText(SignInActivity.this, "User not signed in", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Cancel the dialog
                    dialog.dismiss();
                })
                .show();
    }


//    private void resendEmailVerification(FirebaseUser user) {
//        user.sendEmailVerification()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Email verification sent successfully
//                        Log.d(TAG, "Email verification resent.");
//                        Toast.makeText(this, "Email verification resent", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Email verification sending failed
//                        Log.e(TAG, "Failed to resend email verification: " + task.getException());
//                        if (task.getException() instanceof FirebaseTooManyRequestsException) {
//                            // Handle the case where too many requests have been made
//                            // Wait and try again later
//                            Toast.makeText(this, "Too many requests. Please try again later.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Handle other types of exceptions
//                            Toast.makeText(this, "Failed to resend email verification", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//    private void resendEmailVerification(FirebaseUser user) {
//        AuthCredential credential = EmailAuthProvider.getCredentialWithLink(email, emailLink);
//
//// Re-authenticate the user with the credential
//        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            // User has been successfully re-authenticated
//                            Log.d(TAG, "User re-authenticated successfully");
//                        } else {
//                            // Re-authentication failed
//                            Log.e(TAG, "Failed to re-authenticate user: " + task.getException());
//                        }
//                    }
//                });
//
//    }

    private void resendVerificationEmail(String email) {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Verification email sent to " + email,
                                Toast.LENGTH_SHORT).show();
                        Log.d("Verification", "Verification email sent to " + email);
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(getApplicationContext(),
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
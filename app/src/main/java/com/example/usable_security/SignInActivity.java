package com.example.usable_security;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.Executor;

public class SignInActivity extends AppCompatActivity {

    int flag =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView loginLink = findViewById(R.id.login);
        EditText Password=findViewById(R.id.EditPassword);
        Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button signup=findViewById(R.id.buttonsignup);
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
        signup.setOnClickListener(view -> {
            EditText Name =findViewById(R.id.nameEditText);
            EditText Username=findViewById(R.id.usernameEditText);
            EditText Email=findViewById(R.id.emailEditText);
            if(flag==2){
              String name=Name.getText().toString();
              String username=Username.getText().toString();
              String email=Email.getText().toString();
              String password=Password.getText().toString();
          if(!name.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
          HashMap<String,Object> hashmap=new HashMap<>();
          hashmap.put("Name",name);
          hashmap.put("Username", username);
          hashmap.put("Email",email);
          hashmap.put("Password",password);
          FirebaseDatabase database =FirebaseDatabase.getInstance();
          DatabaseReference Ref= database.getReference("Data");;
          String key=Ref.push().getKey();
          hashmap.put("key",key);
          Ref.child(key).setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Toast.makeText(SignInActivity.this,"Added",Toast.LENGTH_SHORT).show();


              }
          });
          Name.getText().clear();
          Email.getText().clear();
          Password.getText().clear();
          Username.getText().clear();
          Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
          startActivity(intent);
      }

            }
        });


    }
    private BiometricPrompt getPrompt() {

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                flag=1;
                notifyUser(errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                flag=2;
                notifyUser("Authentication Succeeded!");
            }

            @Override
            public void onAuthenticationFailed() {
                flag=1;
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
}

package com.example.usable_security;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

    public class forgotPassword extends AppCompatActivity {

        //Declaration
        Button btnReset, btnBack;
        EditText edtEmail;
        ProgressBar progressBar;
        FirebaseAuth mAuth;
        String strEmail;
        private TextInputLayout emailInputLayout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.forgot_password);

            //Initializaton
            emailInputLayout = findViewById(R.id.emailInputLayout);
            btnReset = findViewById(R.id.btnReset);
            edtEmail = findViewById(R.id.emailEditText);
            progressBar = findViewById(R.id.forgetPasswordProgressbar);

            mAuth = FirebaseAuth.getInstance();



            edtEmail.addTextChangedListener(new TextWatcher() {
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
                        emailInputLayout.setHint("Enter your Email");
                    }
                }
            });

            //Reset Button Listener
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strEmail = edtEmail.getText().toString().trim();
                    if (!TextUtils.isEmpty(strEmail)) {
                        ResetPassword();
                    } else {
                        edtEmail.setError("Email field can't be empty");
                    }
                }
            });


        }

        private void ResetPassword() {
            progressBar.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.INVISIBLE);

            mAuth.sendPasswordResetEmail(strEmail)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(forgotPassword.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(forgotPassword.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(forgotPassword.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            btnReset.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

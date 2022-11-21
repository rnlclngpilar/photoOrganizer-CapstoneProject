package com.example.pixelsort;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextView registerBack;
    TextView availableAccount;
    EditText enterName;
    EditText enterEmail;
    EditText enterPassword;
    Button signUp;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBack = (TextView) findViewById(R.id.registerBack);
        availableAccount = (TextView) findViewById(R.id.availableAccount);

        registerBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });

        availableAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        enterName = (EditText) findViewById(R.id.name);
        enterEmail = (EditText) findViewById(R.id.email);
        enterPassword = (EditText) findViewById(R.id.password);
        signUp = (Button) findViewById(R.id.signUp);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Photos.class));
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String name = enterName.getText().toString();
                String email = enterEmail.getText().toString();
                String password = enterPassword.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    enterName.setError("Name is required.");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    enterEmail.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    enterPassword.setError("Password is required");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG","User profile is created for " + userID);
                                }
                            });

                            Toast.makeText(Register.this, "User created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Photos.class));
                        } else {
                            Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
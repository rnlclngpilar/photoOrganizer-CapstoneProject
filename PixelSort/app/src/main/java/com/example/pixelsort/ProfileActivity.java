package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ImageView profile;
    ImageView photos;
    ImageView search;
    ImageView albums;
    TextView logOut;
    Button save;

    EditText name;
    EditText username;
    EditText email;
    EditText phone;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseAuth mAuth;
    FirebaseFirestore fdb;
    FirebaseUser user;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //*****************************NAVIGATION BAR********************************
        profile = (ImageView) findViewById(R.id.profile);
        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        save = (Button) findViewById(R.id.save);

        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);

        fdb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
            }
        });


        //*************************************************************************
        logOut = (TextView) findViewById(R.id.logOut);

        logOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        showProfileInfo();

    }

    private void saveInfo(){
        if (user != null) {
            String getName = name.getText().toString();
            String getUsername = username.getText().toString();
            String getEmail = email.getText().toString();
            String getPhone = phone.getText().toString();

            DocumentReference documentReference = fdb.collection("users").document(userID);
            Map<String,Object> profileStats = new HashMap<>();

            if (!getName.isEmpty()) {
                profileStats.put("name", getName);
            }
            if (!getUsername.isEmpty()) {
                profileStats.put("username", getUsername);
            }
//            if (!getEmail.isEmpty()) {
//                profileStats.put("email", getEmail);
//            }
            if (!getPhone.isEmpty()) {
                profileStats.put("phone", getPhone);
            }

            documentReference.update(profileStats).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("TAG","User profile statistics have been updated" + userID);
                    Toast.makeText(ProfileActivity.this, "Your profile statistics have been updated", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(ProfileActivity.this, "Error: unable to retrieve profile details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProfileInfo(){
        if (user != null) {
            String emailDetail = user.getEmail();
            email.setText(emailDetail);

            fdb.collection("users")
                    .document(userID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                    String nameDetail = document.getString("name");
                                    String usernameDetail = document.getString("username");
                                    String emailDetail = document.getString("email");
                                    String phoneDetail = document.getString("phone");

                                    name.setText(nameDetail);
                                    username.setText(usernameDetail);
//                                    email.setText(emailDetail);
                                    phone.setText(phoneDetail);

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

        }else{
            Toast.makeText(ProfileActivity.this, "Error: unable to retrieve profile details.", Toast.LENGTH_SHORT).show();
        }


    }

}
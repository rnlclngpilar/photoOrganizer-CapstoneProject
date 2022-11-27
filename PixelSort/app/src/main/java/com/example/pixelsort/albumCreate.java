package com.example.pixelsort;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class albumCreate extends AppCompatActivity {

    TextView albumBack;

    photosGallery galleryPhotos;
    List<Image> imagePath;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_create);

        Toast.makeText(albumCreate.this, "Please select which pictures to save to album.", Toast.LENGTH_SHORT).show();


        albumBack = findViewById(R.id.albumBack);
        albumBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(albumCreate.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        imagePath = new ArrayList<>();

        RecyclerView recyclerCreateAlbum = findViewById(R.id.recyclerCreateAlbum);

        GridLayoutManager manager = new GridLayoutManager(albumCreate.this, 4);
        recyclerCreateAlbum.setLayoutManager(manager);

        galleryPhotos = new photosGallery(albumCreate.this, imagePath);
        recyclerCreateAlbum.setAdapter(galleryPhotos);

        // display photos here
        loadURLS();
    }

    private void loadURLS() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        imagePath.add(Glide.with().load(dataSnapshot.getValue().toString()));
                        //imagePath.add(dataSnapshot.getValue().toString());
                    }

                    galleryPhotos.setUpdatedImages(imagePath);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        DatabaseReference dbRef = fDatabase.getReference().child(userID).child("images");
        dbRef.addValueEventListener(listener);
    }
}
package com.example.pixelsort;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class albumCreate extends AppCompatActivity {

    TextView albumBack;
    TextView saveAlbum;
    RecyclerView recyclerCreateAlbum;

    photosGallery photosGallery;
    photosGallery selectedGallery;

    GridLayoutManager manager;

    ArrayList<String> imagePath = new ArrayList<>();
    ArrayList<String> selectedImage = new ArrayList<>();

    final String origin = "albums";
    String userID;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_create);

        albumBack = (TextView) findViewById(R.id.albumBack);
        saveAlbum = (TextView) findViewById(R.id.saveAlbum);
        recyclerCreateAlbum = (RecyclerView) findViewById(R.id.recyclerCreateAlbum);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        //*****************************BUTTONS********************************

        albumBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(albumCreate.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        saveAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                selectedImage = selectedGallery.getSelectedImg();

                if (selectedImage == null) {
                    Toast.makeText(albumCreate.this, "Please select image(s)", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(albumCreate.this, "SAVED", Toast.LENGTH_SHORT).show();
//                    saveAlbum(selectedImage);
                }

//                Intent intent = new Intent(albumCreate.this, AlbumsActivity.class);
//                intent.putStringArrayListExtra("selectedImage", selectedImage);
//                startActivity(intent);

            }
        });

        //*******************************************************************
        Toast.makeText(albumCreate.this, "Please select which pictures to save to album.", Toast.LENGTH_SHORT).show();

        manager = new GridLayoutManager(albumCreate.this, 4);
        selectedGallery = new photosGallery(albumCreate.this);

        loadImages();
    }

    private void loadImages() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        //Image image = dataSnapshot.getValue(Image.class);
                        //imagePath.add(image);
//                        imagePath.add(Glide.with().load(dataSnapshot.getValue().toString()));
                        imagePath.add(dataSnapshot.getValue().toString());
                    }
                    photosGallery = new photosGallery(albumCreate.this, imagePath, origin);
                    photosGallery.setUpdatedImages(imagePath);

                    recyclerCreateAlbum.setLayoutManager(manager);
                    recyclerCreateAlbum.setAdapter(photosGallery);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(albumCreate.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        DatabaseReference dbRef = fDatabase.getReference().child(userID).child("images");
        dbRef.addValueEventListener(listener);
    }
}
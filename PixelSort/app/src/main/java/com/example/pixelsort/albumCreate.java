package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class albumCreate extends AppCompatActivity {

    TextView albumBack;
    TextView saveAlbum;
    TextView albumName;
    ProgressBar imageProgress;
    RecyclerView recyclerCreateAlbum;

    photosGallery photosGallery;
    photosGallery selectedGallery;

    GridLayoutManager manager;

    List<Image> imagePath = new ArrayList<>();
    List<Image> selectedImages = new ArrayList<>();

    final String origin = "albums";
    String userID;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_create);

        albumBack = (TextView) findViewById(R.id.albumBack);
        saveAlbum = (TextView) findViewById(R.id.saveAlbum);
        albumName = (TextView) findViewById(R.id.albumName);
        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);
        recyclerCreateAlbum = (RecyclerView) findViewById(R.id.recyclerCreateAlbum);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);

        manager = new GridLayoutManager(albumCreate.this, 4);

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
                selectedImages = photosGallery.getSelectedImg();

                if (selectedImages != null && !selectedImages.isEmpty()) {
                    Toast.makeText(albumCreate.this, "Saved to database!", Toast.LENGTH_SHORT).show();
                    saveAlbum(selectedImages, albumName.getText().toString());

                }else {
                    Toast.makeText(albumCreate.this, "Please select image(s)!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //*******************************************************************
        Toast.makeText(albumCreate.this, "Please select which pictures to save to album.", Toast.LENGTH_SHORT).show();
        //loadImages();

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;
                        //image.setKey(snapshot.getKey());
                        imagePath.add(image);
                    }

                    photosGallery.notifyDataSetChanged();
                    photosGallery = new photosGallery(albumCreate.this, imagePath, origin);
                    photosGallery.setUpdatedImages(imagePath);

                    recyclerCreateAlbum.setLayoutManager(manager);
                    recyclerCreateAlbum.setAdapter(photosGallery);

                    //imageProgress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(albumCreate.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                //imageProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void saveAlbum(List<Image> selectedImages, String alName){
//        Toast.makeText(albumCreate.this, this.selectedImages.toString(), Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        storageReference = storage.getReference();
        userID = mAuth.getCurrentUser().getUid();

        Map<String, Object> album = new HashMap<>();
        album.put(alName, selectedImages);

        fStore.collection("users").document(userID)
                .collection("albums")
                .add(album).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(albumCreate.this, "Successfully created album!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(albumCreate.this, AlbumsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error uploading Albums...");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    /*
    private void loadImages() {
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;
                        image.setKey(snapshot.getKey());
                        imagePath.add(image);
                    }

                    photosGallery.notifyDataSetChanged();

                    imageProgress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(albumCreate.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                imageProgress.setVisibility(View.INVISIBLE);
            }
        });
    }
     */
}
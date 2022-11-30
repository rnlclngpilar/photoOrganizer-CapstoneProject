package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class albumCreate extends AppCompatActivity {

    TextView albumBack;
    TextView saveAlbum;
    TextView albumName;
    ProgressBar imageProgress;
    RecyclerView recyclerCreateAlbum;

    photosAdapter photosAdapter;

    GridLayoutManager manager;

    List<Image> imagePath = new ArrayList<>();
    List<Image> selectedImages = new ArrayList<>();

    final String origin = "albums";
    String userID;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;

    private DatabaseReference databaseReferenceIMG;
    private DatabaseReference databaseReferenceALBM;
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

        databaseReferenceALBM = FirebaseDatabase.getInstance().getReference("albums/" + userID);
        databaseReferenceIMG = FirebaseDatabase.getInstance().getReference("images/" + userID);

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
                selectedImages = photosAdapter.getSelectedImg();

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

        valueEventListener = databaseReferenceIMG.addValueEventListener(new ValueEventListener() {
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

                    //photosAdapter.notifyDataSetChanged();
                    photosAdapter = new photosAdapter(albumCreate.this, imagePath, origin);
                    photosAdapter.setUpdatedImages(imagePath);

                    recyclerCreateAlbum.setLayoutManager(manager);
                    recyclerCreateAlbum.setAdapter(photosAdapter);

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

    private void saveAlbum(List<Image> selectedImages, String alName){
//        Toast.makeText(albumCreate.this, this.selectedImages.toString(), Toast.LENGTH_SHORT).show();
        if (selectedImages != null) {
            mAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();
            fDatabase = FirebaseDatabase.getInstance();
            userID = mAuth.getCurrentUser().getUid();

            String albumID = UUID.randomUUID().toString();

            Map<String, Object> album = new HashMap<>();
            album.put("album_id", albumID);
            album.put("album_name", alName);
            album.put("images", selectedImages);
            album.put("thumbnail", selectedImages.get(0).getImageURL());

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

            Image image = new Image();
            String album_id = databaseReferenceALBM.push().getKey();
            assert album_id != null;
            image.setKey(albumID);
            databaseReferenceALBM.child(albumID).setValue(album);

        }else{
            Toast.makeText(this, "No Images selected", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceIMG.removeEventListener(valueEventListener);
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

                    photosAdapter.notifyDataSetChanged();

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
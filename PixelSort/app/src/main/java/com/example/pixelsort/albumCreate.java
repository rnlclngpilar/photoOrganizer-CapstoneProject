package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    ImageView photos;
    ImageView search;
    ImageView albums;
    ImageView albumBack;
    LinearLayout saveAlbum;
    EditText albumName;
    ProgressBar imageProgress;
    RecyclerView recyclerCreateAlbum;

    photosAdapter photosAdapter;

    GridLayoutManager manager;

    List<Image> imagePath = new ArrayList<>();
    List<Image> selectedImages = new ArrayList<>();

    final String origin = "albumCreate";
    String userID;

    Boolean originAlbum;
    String alID;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;

    private DatabaseReference databaseReferenceIMG;
    private DatabaseReference databaseReferenceALBMCREATE;
    private DatabaseReference databaseReferenceALBMVIEW;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_create);

        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        albumBack = (ImageView) findViewById(R.id.albumBack);
        saveAlbum = (LinearLayout) findViewById(R.id.saveAlbum);
        albumName = (EditText) findViewById(R.id.albumName);
        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);
        recyclerCreateAlbum = (RecyclerView) findViewById(R.id.recyclerCreateAlbum);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        manager = new GridLayoutManager(albumCreate.this, 4);

        Bundle intentExtra = getIntent().getExtras();

        try{
            alID = intentExtra.getString("albumID");
           originAlbum = intentExtra.getBoolean("originAlbum");
        } catch(Exception ex){
            originAlbum = false;
            alID = "";
        }

        databaseReferenceIMG = FirebaseDatabase.getInstance().getReference("images/" + userID);
        databaseReferenceALBMCREATE = FirebaseDatabase.getInstance().getReference("albums/" + userID);
        databaseReferenceALBMVIEW = FirebaseDatabase.getInstance().getReference("albums/" + userID + "/" + alID + "/images");

        //*****************************BUTTONS********************************

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(albumCreate.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(albumCreate.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(albumCreate.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

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
                String albumNameEnter = albumName.getText().toString();

                if (TextUtils.isEmpty(albumNameEnter)) {
                    albumName.setError("Name is required.");
                    return;
                }

                if (originAlbum != false && alID != ""){

                }else{
                    selectedImages = photosAdapter.getSelectedImg();

                    if (selectedImages != null && !selectedImages.isEmpty()) {
                        Toast.makeText(albumCreate.this, "Saved to database!", Toast.LENGTH_SHORT).show();
                        saveAlbum(selectedImages, albumName.getText().toString());

                    }else {
                        Toast.makeText(albumCreate.this, "Please select image(s)!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //*******************************************************************

        if (originAlbum != false && alID != ""){
            String alName = intentExtra.getString("albumName");
            albumName.setText(alName);
            saveAlbum.setVisibility(View.INVISIBLE);

            valueEventListener = databaseReferenceALBMVIEW.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int index = 0;

                    if (snapshot != null && snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Image image = snapshot.child("" + index).getValue(Image.class);
                            assert image != null;
                            imagePath.add(image);

                            index++;
                        }


                    }

                    System.out.println("imagePath" + imagePath);

                    photosAdapter = new photosAdapter(albumCreate.this, imagePath, "albumView");
                    photosAdapter.setUpdatedImages(imagePath);

                    recyclerCreateAlbum.setLayoutManager(manager);
                    recyclerCreateAlbum.setAdapter(photosAdapter);

                    imageProgress.setVisibility(View.INVISIBLE);


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, error.getMessage());
                    imageProgress.setVisibility(View.INVISIBLE);
                }
            });

        }else {
            Toast.makeText(albumCreate.this, "Please select which pictures to save to album.", Toast.LENGTH_SHORT).show();

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
                            System.out.println("fromCreateALBUM" + image);

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
//                    Toast.makeText(albumCreate.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, error.getMessage());
                    imageProgress.setVisibility(View.INVISIBLE);
                }
            });
        }
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

//            Image albums = new Image(albumID, alName, selectedImages, selectedImages.get(0).getImageURL());
            Album albums = new Album();
            String album_id = databaseReferenceALBMCREATE.push().getKey();
            assert album_id != null;
            albums.setKey(albumID);
            databaseReferenceALBMCREATE.child(albumID).setValue(album);

        }else{
            Toast.makeText(this, "No Images selected", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceIMG.removeEventListener(valueEventListener);
        databaseReferenceALBMCREATE.removeEventListener(valueEventListener);
        databaseReferenceALBMVIEW.removeEventListener(valueEventListener);
    }

}
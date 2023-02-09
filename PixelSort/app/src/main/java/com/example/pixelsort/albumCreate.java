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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class albumCreate extends AppCompatActivity {

    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    LinearLayout albumBack;
    LinearLayout saveAlbum;
    LinearLayout editAlbum;
    EditText albumName;
    ProgressBar imageProgress;
    RecyclerView recyclerCreateAlbum;

    photosAdapter photosAdapter;

    GridLayoutManager manager;

    List<Image> selectedImages = new ArrayList<>();
    List<Image> imagePath = new ArrayList<>();
    List<Image> updatedImagePath = new ArrayList<>();
    List<Image> availableImagePath = new ArrayList<>();


    final String origin = "albumCreate";
    String userID;

    Boolean originAlbum;
    String albumID;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;

    private DatabaseReference databaseReferenceIMG;
    private DatabaseReference databaseReferenceALBMCREATE;
    private DatabaseReference databaseReferenceALBMVIEW;
    private ValueEventListener valueEventListener;

    private List<Image> imageToBeDeleted = new ArrayList<>();
    private List<Image> imageToBeAdded = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_create);

        photos = (LinearLayout) findViewById(R.id.photos);
        search = (LinearLayout) findViewById(R.id.search);
        albums = (LinearLayout) findViewById(R.id.albums);
        albumBack = (LinearLayout) findViewById(R.id.albumBack);
        saveAlbum = (LinearLayout) findViewById(R.id.saveAlbum);
        editAlbum = (LinearLayout) findViewById(R.id.editAlbum);
        albumName = (EditText) findViewById(R.id.albumName);
        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);
        recyclerCreateAlbum = (RecyclerView) findViewById(R.id.recyclerCreateAlbum);

        LinearLayout editOptions = findViewById(R.id.editOptions);

        LinearLayout selectOptions = findViewById(R.id.selectOptions);
        LinearLayout selectImage = findViewById(R.id.selectImage);
        LinearLayout deleteImage = findViewById(R.id.deleteImage);

        LinearLayout addOptions = findViewById(R.id.addOptions);
        LinearLayout addImage = findViewById(R.id.addImage);
        LinearLayout continueAdd = findViewById(R.id.continueAdd);

        LinearLayout updateOptions = findViewById(R.id.updateOptions);
        LinearLayout updateAlbums = findViewById(R.id.updateAlbums);

        LinearLayout cancelOptions = findViewById(R.id.cancelOptions);
        LinearLayout cancelEdit = findViewById(R.id.cancelEdit);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        manager = new GridLayoutManager(albumCreate.this, 4);

        Bundle intentExtra = getIntent().getExtras();

        try{
            albumID = intentExtra.getString("albumID");
            originAlbum = intentExtra.getBoolean("fromAlbum");
        } catch(Exception ex){
            originAlbum = false;
            albumID = "";
        }

        databaseReferenceIMG = FirebaseDatabase.getInstance().getReference("images/" + userID);
        databaseReferenceALBMCREATE = FirebaseDatabase.getInstance().getReference("albums/" + userID);
        databaseReferenceALBMVIEW = FirebaseDatabase.getInstance().getReference("albums/" + userID + "/" + albumID + "/images");

        if (originAlbum == false && albumID == ""){
            Toast.makeText(albumCreate.this, "Please select which pictures to save to album.", Toast.LENGTH_SHORT).show();
            saveAlbum.setVisibility(View.VISIBLE);
            editAlbum.setVisibility(View.INVISIBLE);
            editOptions.setVisibility(View.GONE);
            saveAlbum.bringToFront();

            createAlbum();
        }else {
            String alName = intentExtra.getString("albumName");
            albumName.setText(alName);
            saveAlbum.setVisibility(View.INVISIBLE);
            editAlbum.setVisibility(View.VISIBLE);
            editOptions.setVisibility(View.VISIBLE);
            editAlbum.bringToFront();

            showAlbum();
            updatedImagePath = imagePath;
            availableImagePath = getAvailableImage();
        }

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

                if (originAlbum == false && albumID == ""){
                    selectedImages = photosAdapter.getSelectedImg();

                    if (selectedImages != null && !selectedImages.isEmpty()) {
                        Toast.makeText(albumCreate.this, "Saved to albums!", Toast.LENGTH_SHORT).show();
                        saveAlbum(selectedImages, albumName.getText().toString());

                    }else {
                        Toast.makeText(albumCreate.this, "No image(s) are selected!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        editAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editOptions.getVisibility() == View.GONE){
                    editOptions.setVisibility(View.VISIBLE);
                }else{
                    editOptions.setVisibility(View.GONE);
                    cancelEdit.callOnClick();
                }
            }
        });

        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOptions.setBackgroundResource(R.drawable.sort_background_2);
                cancelOptions.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancelOptions.setBackgroundResource(0);
                    }
                }, 250);

                Toast.makeText(albumCreate.this, "Changes has been Dismissed!", Toast.LENGTH_SHORT).show();

                selectOptions.setBackgroundResource(0);
                addOptions.setBackgroundResource(0);

                deleteImage.setVisibility(View.GONE);
                continueAdd.setVisibility(View.GONE);

                showAlbum();
            }
        });

        updateAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOptions.setBackgroundResource(R.drawable.sort_background_2);
                updateOptions.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateOptions.setBackgroundResource(0);
                    }
                }, 500);

                updateAlbum();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdatedAlbum();

                if (selectOptions.getBackground() == null){
                    addOptions.setBackgroundResource(0);
                    continueAdd.setVisibility(View.GONE);

                    selectOptions.setBackgroundResource(R.drawable.sort_background_2);
                    deleteImage.setVisibility(View.VISIBLE);

                    photosAdapter = new photosAdapter(albumCreate.this, updatedImagePath, "albumSelect");
                    photosAdapter.setUpdatedImages(updatedImagePath);
                    recyclerCreateAlbum.setLayoutManager(manager);
                    recyclerCreateAlbum.setAdapter(photosAdapter);

                }else{
                    selectOptions.setBackgroundResource(0);
                    deleteImage.setVisibility(View.GONE);
                    showUpdatedAlbum();
                }

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addOptions.getBackground() == null){
                    selectOptions.setBackgroundResource(0);
                    deleteImage.setVisibility(View.GONE);

                    addOptions.setBackgroundResource(R.drawable.sort_background_2);
                    continueAdd.setVisibility(View.VISIBLE);

                    photosAdapter = new photosAdapter(albumCreate.this, availableImagePath, "albumAddImages");
                    photosAdapter.setUpdatedImages(availableImagePath);
                    recyclerCreateAlbum.setLayoutManager(manager);
                    recyclerCreateAlbum.setAdapter(photosAdapter);

                }else{
                    addOptions.setBackgroundResource(0);
                    continueAdd.setVisibility(View.GONE);
                    showUpdatedAlbum();
                }

            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage();
                selectImage.callOnClick();
            }
        });

        continueAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImages();
                addImage.callOnClick();
            }
        });

    } //end of onCreate()

    private List<Image> getAvailableImage(){
        valueEventListener = databaseReferenceIMG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List imageIDs = new ArrayList();
                for (Image item : updatedImagePath) {
                    imageIDs.add(item.getImageId());
                }

                if (snapshot != null && snapshot.hasChildren()) {
                    availableImagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;

                        if (!imageIDs.contains(image.getImageId())) {
                            availableImagePath.add(image);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });

        return availableImagePath;
    }

    private void addImages(){
        selectedImages = photosAdapter.getSelectedImg();
        if (selectedImages != null && !selectedImages.isEmpty()){
            imageToBeAdded = selectedImages;
            Log.d(TAG, "added");

            for (Image item : imageToBeAdded) {
                updatedImagePath.add(item);
            }
            imageToBeAdded.clear();
            showUpdatedAlbum();
            availableImagePath = getAvailableImage();

        }else{
            Toast.makeText(albumCreate.this, "Select an image(s) to be removed", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteImage(){
        selectedImages = photosAdapter.getSelectedImg();
        if (selectedImages != null && !selectedImages.isEmpty()){
            imageToBeDeleted = selectedImages;
            Log.d(TAG, "deleted");

            for (Image item : imageToBeDeleted) {
                updatedImagePath.remove(item);
            }
            imageToBeDeleted.clear();
            showUpdatedAlbum();
            availableImagePath = getAvailableImage();

        }else{
            Toast.makeText(albumCreate.this, "Select an image(s) to be removed", Toast.LENGTH_LONG).show();
        }
    }

    private void updateAlbum(){
        if (!updatedImagePath.isEmpty() && updatedImagePath != null){
            mAuth = FirebaseAuth.getInstance();
            fStore = FirebaseFirestore.getInstance();
            fDatabase = FirebaseDatabase.getInstance();
            userID = mAuth.getCurrentUser().getUid();

            fStore.collection("users").document(userID)
                    .collection("albums").whereEqualTo("album_id", albumID)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String databaseAlbumID = document.getId();

                                    System.out.println(updatedImagePath);
                                    Album album = new Album(albumID, albumName.getText().toString(), updatedImagePath.get(0).getImageURL(), updatedImagePath);

                                    Map<String, Object> albumImages = new HashMap<>();
                                    albumImages.put("album_id", album.getAlbum_id());
                                    albumImages.put("album_name", album.getAlbum_name());
                                    albumImages.put("images", album.getImage());
                                    albumImages.put("thumbnail", album.getThumbnail());

                                    fStore.collection("users").document(userID)
                                            .collection("albums").document(databaseAlbumID)
                                            .update(albumImages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "Document update successful!");
                                                    Toast.makeText(albumCreate.this, "Successfully Updated album!", Toast.LENGTH_SHORT).show();
                                                    databaseReferenceALBMCREATE.child(album.getAlbum_id()).setValue(albumImages);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);

                                                }
                                            });
                                }
                            }
                        }
                    });
        }else {
            Toast.makeText(albumCreate.this, "Album must not be empty!", Toast.LENGTH_SHORT).show();

        }
    }

    private void showUpdatedAlbum(){
        Log.d(TAG, "UPDATED IMAGES " + updatedImagePath);
        Toast.makeText(albumCreate.this, "NOTE: you must SAVE to update your changes.", Toast.LENGTH_LONG).show();

        photosAdapter = new photosAdapter(albumCreate.this, updatedImagePath, "albumView");
        photosAdapter.setUpdatedImages(updatedImagePath);

        recyclerCreateAlbum.setLayoutManager(manager);
        recyclerCreateAlbum.setAdapter(photosAdapter);
    }

    private void showAlbum(){
        valueEventListener = databaseReferenceALBMVIEW.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;
                        imagePath.add(image);
                    }
                }
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
    }

    private void createAlbum(){
        valueEventListener = databaseReferenceIMG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;
                        imagePath.add(image);
                    }

                    photosAdapter = new photosAdapter(albumCreate.this, imagePath, origin);
                    photosAdapter.setUpdatedImages(imagePath);

                    recyclerCreateAlbum.setLayoutManager(manager);
                    recyclerCreateAlbum.setAdapter(photosAdapter);

                    imageProgress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
                imageProgress.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void saveAlbum(List<Image> selectedImages, String alName){
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        String albumID = UUID.randomUUID().toString();
        Album album = new Album(albumID, alName, selectedImages.get(0).getImageURL(), selectedImages);

        Map<String, Object> albumImages = new HashMap<>();
        albumImages.put("album_id", album.getAlbum_id());
        albumImages.put("album_name", album.getAlbum_name());
        albumImages.put("images", album.getImage());
        albumImages.put("thumbnail", album.getThumbnail());

        fStore.collection("users").document(userID)
                .collection("albums")
                .add(albumImages).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(albumCreate.this, "Successfully created album!", Toast.LENGTH_SHORT).show();
                        databaseReferenceALBMCREATE.child(album.getAlbum_id()).setValue(albumImages);

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
        databaseReferenceIMG.removeEventListener(valueEventListener);
        databaseReferenceALBMCREATE.removeEventListener(valueEventListener);
        databaseReferenceALBMVIEW.removeEventListener(valueEventListener);
    }
}
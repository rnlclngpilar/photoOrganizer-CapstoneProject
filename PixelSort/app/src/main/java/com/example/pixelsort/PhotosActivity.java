package com.example.pixelsort;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotosActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;

    ImageView profile;
    ImageView photos;
    ImageView search;
    ImageView albums;
    Button addPhoto;
    ProgressBar imageProgress;

    List<Image> imagePath = new ArrayList<>();
    RecyclerView recyclerGalleryImages;
    photosGallery galleryPhotos;
    GridLayoutManager manager;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;

    String userID;
    final String origin = "photos";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        profile = (ImageView) findViewById(R.id.profile);
        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        addPhoto = (Button) findViewById(R.id.addPhoto);
        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);
        recyclerGalleryImages = findViewById(R.id.recyclerGalleryImages);

        imagePath = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        //*****************************NAVIGATION BAR********************************

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosActivity.this, addPhotos.class);
                startActivity(intent);
            }
        });

        //*****************************Gallery Images********************************

        manager = new GridLayoutManager(PhotosActivity.this, 4);
        recyclerGalleryImages.setLayoutManager(manager);

        loadImages();

//        requestPermissions();
//        prepareRecyclerView();
    }

    private void loadImages(){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        imagePath.add(image);
                    }

                    galleryPhotos = new photosGallery(PhotosActivity.this, imagePath, origin);
                    recyclerGalleryImages.setAdapter(galleryPhotos);
                    imageProgress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PhotosActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                imageProgress.setVisibility(View.INVISIBLE);
            }
        };

        DatabaseReference dbRef = fDatabase.getReference().child(userID).child("images");
        dbRef.addValueEventListener(listener);
    }



//    private boolean checkPermission() {
//        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissions() {
//        if (checkPermission()) {
//            Toast.makeText(this, "Permissions granted..", Toast.LENGTH_SHORT).show();
//            getImagePath();
//        } else {
//            requestPermission();
//        }
//    }

//    private void requestPermission() {
//        //on below line we are requesting the read external storage permissions.
//        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//    }

//    private void prepareRecyclerView() {
//        galleryPhotos = new photosGallery(PhotosActivity.this, imagePath);
//
//        GridLayoutManager manager = new GridLayoutManager(PhotosActivity.this, 3);
//
//        recyclerGalleryImages.setLayoutManager(manager);
//        recyclerGalleryImages.setAdapter(galleryPhotos);
//    }

//    private void getImagePath() {
//        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
//
//        if (isSDPresent) {
//
//            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
//            final String orderBy = MediaStore.Images.Media._ID;
//
//            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
//            int count = cursor.getCount();
//
//            for (int i = 0; i < count; i++) {
//                cursor.moveToPosition(i);
//                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//
//                imagePath.add(cursor.getString(dataColumnIndex));
//            }
//
////            galleryPhotos.notif`
////            yDataSetChanged();
//
//            cursor.close();
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if (storageAccepted) {
//                        Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show();
//                        getImagePath();
//                    } else {
//                        Toast.makeText(this, "Permissions Denied, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//        }
//    }

}
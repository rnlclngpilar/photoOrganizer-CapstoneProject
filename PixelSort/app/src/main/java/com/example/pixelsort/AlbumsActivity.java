package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumsActivity extends AppCompatActivity {

    ImageView profile;
    ImageView photos;
    ImageView search;
    ImageView albums;
    Button createNewAlbum;
    RecyclerView recyclerAlbums;

    private static final int PICK_IMAGE_MULTIPLE = 1;
    List<Image> albumPath = new ArrayList<>();

    albumsGallery albumsGallery;

    String userID;
    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    DatabaseReference databaseReference;
    FirebaseFirestore fStore;

    GridLayoutManager manager;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        //*****************************NAVIGATION BAR********************************
        profile = (ImageView) findViewById(R.id.profile);
        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        recyclerAlbums = (RecyclerView) findViewById(R.id.recylerAlbums);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumsActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumsActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumsActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        //*******************************NEW ALBUMS CREATION********************************
        createNewAlbum = findViewById(R.id.createNewAlbum);
        createNewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumsActivity.this, albumCreate.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("albums/" + userID);
        fStore = FirebaseFirestore.getInstance();

        manager = new GridLayoutManager(AlbumsActivity.this, 1);
        recyclerAlbums.setLayoutManager(manager);

//        Log.d(TAG, "ALBUMPATH " + albumPath);

        fStore.collection("users")
                .document(userID)
                .collection("albums")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            Image albumData = new Image(
                                    snapshot.getString("album_name"),
                                    snapshot.getString("thumbnail")
                            );
                            albumPath.add(albumData);
                        }
                        albumsGallery = new albumsGallery(AlbumsActivity.this, albumPath);
                        albumsGallery.setUpdatedAlbums(albumPath);

                        recyclerAlbums.setLayoutManager(manager);
                        recyclerAlbums.setAdapter(albumsGallery);

//                        albumsGallery.notifyDataSetChanged();

//                        Log.d(TAG, "ALBUMPATH " + albumPath);

                    }
                });


//        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot != null && snapshot.hasChildren()) {
//                    albumPath.clear();
////                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
////                        Image image = dataSnapshot.getValue(Image.class);
////                        assert image != null;
////                        albumPath.add(image);
////                    }
//
//
//
////                    imageProgress.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(AlbumsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
////                imageProgress.setVisibility(View.INVISIBLE);
//            }
//        });

//        Log.d(TAG, "ALBUMPATH " + albumPath.size());



//        createNewAlbum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
//            }
//        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        try {
//            // When an Image is picked
//            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
//                    && null != data) {
//                // Get the Image from data
//
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                ArrayList<String> imagesEncodedList = new ArrayList<String>();
//                String imageEncoded;
//                if(data.getData()!=null){
//
//                    Uri mImageUri=data.getData();
//
//                    // Get the cursor
//                    Cursor cursor = getContentResolver().query(mImageUri,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    imageEncoded  = cursor.getString(columnIndex);
//                    cursor.close();
//
//                } else {
//                    if (data.getClipData() != null) {
//                        ClipData mClipData = data.getClipData();
//                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                        for (int i = 0; i < mClipData.getItemCount(); i++) {
//
//                            ClipData.Item item = mClipData.getItemAt(i);
//                            Uri uri = item.getUri();
//                            mArrayUri.add(uri);
//                            // Get the cursor
//                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                            // Move to first row
//                            cursor.moveToFirst();
//
//                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            imageEncoded  = cursor.getString(columnIndex);
//                            imagesEncodedList.add(imageEncoded);
//                            cursor.close();
//
//                        }
//                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
//                    }
//                }
//            } else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

}
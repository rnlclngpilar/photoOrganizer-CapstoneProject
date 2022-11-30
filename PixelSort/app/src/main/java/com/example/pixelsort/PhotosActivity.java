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
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PhotosActivity extends AppCompatActivity implements photosAdapter.OnItemClickListener{
    private static final int PERMISSION_REQUEST_CODE = 200;

    ImageView profile;
    ImageView photos;
    ImageView search;
    ImageView albums;
    Button addPhoto;
    ProgressBar imageProgress;

    List<Image> imagePath = new ArrayList<>();
    RecyclerView recyclerGalleryImages;
    photosAdapter photosAdapter;
    GridLayoutManager manager;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;

    String userID;
    String databaseImageID;
    final String origin = "photos";

    private FirebaseStorage firebaseStorage;
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

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        photosAdapter = new photosAdapter(PhotosActivity.this, imagePath, origin);
        recyclerGalleryImages.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener(PhotosActivity.this);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);

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

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    imagePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;
                        imagePath.add(image);
                    }
                    photosAdapter.setUpdatedImages(imagePath);
                    photosAdapter.notifyDataSetChanged();

//                    Log.d(TAG, "IMAGEPATH: " + imagePath);

                    imageProgress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PhotosActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                imageProgress.setVisibility(View.INVISIBLE);
            }
        });

//        requestPermissions();
//        prepareRecyclerView();
    }

    @Override
    public void onDeleteClick(int position) {
        Image image = imagePath.get(position);
        final String key = image.getKey();

        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(image.getImageURL());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseReference.child(key).removeValue();
                fStore.collection("users")
                        .document(userID)
                        .collection("images")
                        .whereEqualTo("image_id", key)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                databaseImageID = document.getId();

                                fStore.collection("users")
                                        .document(userID)
                                        .collection("images")
                                        .document(databaseImageID)
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
                Toast.makeText(PhotosActivity.this, "Image has been deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

//    private void loadImages(){
//        ValueEventListener listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot != null && snapshot.hasChildren()) {
//                    imagePath.clear();
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        //Image image = dataSnapshot.getValue(Image.class);
//                        //imagePath.add(image);
//                        //imagePath.add(dataSnapshot.getValue().toString());
//                    }
//
//                    photosAdapter = new photosAdapter(PhotosActivity.this, imagePath, origin);
//                    photosAdapter.setUpdatedImages(imagePath);
//
//                    recyclerGalleryImages.setLayoutManager(manager);
//                    recyclerGalleryImages.setAdapter(photosAdapter);
//
//                    imageProgress.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(PhotosActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                imageProgress.setVisibility(View.INVISIBLE);
//            }
//        };
//
//        DatabaseReference dbRef = fDatabase.getReference().child(userID).child("images");
//        dbRef.addValueEventListener(listener);
//    }



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
//        galleryPhotos = new photosAdapter(PhotosActivity.this, imagePath);
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
package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PhotosActivity extends AppCompatActivity implements photosAdapter.OnItemClickListener{
    private static final int PERMISSION_REQUEST_CODE = 200;

    ImageView profile;
    ImageView photos;
    ImageView search;
    ImageView albums;
    ImageView addPhoto;
    ImageView archives;
    Button sortPhotos;
    public static Button selectPhotos;
    public static Button removeSelection;
    public static CheckBox qualityCheck;
    Button deletePhotos;
    ProgressBar imageProgress;
    LinearLayout deleteOptions;

    List<Image> imagePath = new ArrayList<>();
    RecyclerView recyclerGalleryImages;
    photosAdapter photosAdapter;
    GridLayoutManager manager;
    SharedPreferences sharedPreferences;
    Boolean selectClicked = false;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;

    String userID;
    String databaseImageID;
    final String origin = "photos";

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private DatabaseReference addArchiveReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        profile = (ImageView) findViewById(R.id.profile);
        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        addPhoto = (ImageView) findViewById(R.id.addPhoto);
        archives = (ImageView) findViewById(R.id.archives);
        sortPhotos = (Button) findViewById(R.id.sortPhotos);
        selectPhotos = (Button) findViewById(R.id.selectPhotos);
        deletePhotos = (Button) findViewById(R.id.deletePhotos);
        removeSelection = (Button) findViewById(R.id.removeSelection);
        qualityCheck = (CheckBox) findViewById(R.id.qualityCheck);
        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);
        recyclerGalleryImages = (RecyclerView) findViewById(R.id.recyclerGalleryImages);
        deleteOptions = (LinearLayout) findViewById(R.id.deleteOptions);

        sharedPreferences = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String sorting = sharedPreferences.getString("Sort", "newest");

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        fStore = FirebaseFirestore.getInstance();

        photosAdapter = new photosAdapter(PhotosActivity.this, imagePath, origin);
        recyclerGalleryImages.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener(PhotosActivity.this);

        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);
        addArchiveReference = FirebaseDatabase.getInstance().getReference("archives/" + userID);

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

        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotosActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });

        sortPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSortDialog();
            }
        });

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*
        deletePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PhotosActivity.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
         */


        //*****************************Gallery Images********************************

        if (!qualityCheck.isChecked()) {
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.hasChildren()) {
                        imagePath.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Image image = dataSnapshot.getValue(Image.class);
                            assert image != null;
                            imagePath.add(image);
                            photosAdapter.setUpdatedImages(imagePath);
                            photosAdapter.notifyDataSetChanged();
                        }

//                    Log.d(TAG, "IMAGEPATH: " + imagePath);

                        if (sorting.equals("newest")) {
                            Collections.reverse(imagePath);
                            manager = new GridLayoutManager(PhotosActivity.this, 4);
                        } else if (sorting.equals("oldest")) {
                            manager = new GridLayoutManager(PhotosActivity.this, 4);
                        }
                        recyclerGalleryImages.setLayoutManager(manager);

                        imageProgress.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PhotosActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    imageProgress.setVisibility(View.INVISIBLE);
                }
            });
        }

        qualityCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot != null && snapshot.hasChildren()) {
                                imagePath.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Image image = dataSnapshot.getValue(Image.class);
                                    assert image != null;
                                        if (image.getHighQuality()) {
                                            imagePath.add(image);
                                            photosAdapter.setUpdatedImages(imagePath);
                                            photosAdapter.notifyDataSetChanged();
                                        }
                                }

//                    Log.d(TAG, "IMAGEPATH: " + imagePath);

                                if (sorting.equals("newest")) {
                                    Collections.reverse(imagePath);
                                    manager = new GridLayoutManager(PhotosActivity.this, 4);
                                } else if (sorting.equals("oldest")) {
                                    manager = new GridLayoutManager(PhotosActivity.this, 4);
                                }
                                recyclerGalleryImages.setLayoutManager(manager);

                                imageProgress.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(PhotosActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            imageProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot != null && snapshot.hasChildren()) {
                                imagePath.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Image image = dataSnapshot.getValue(Image.class);
                                    assert image != null;
                                    imagePath.add(image);
                                    photosAdapter.setUpdatedImages(imagePath);
                                    photosAdapter.notifyDataSetChanged();
                                }

//                    Log.d(TAG, "IMAGEPATH: " + imagePath);

                                if (sorting.equals("newest")) {
                                    Collections.reverse(imagePath);
                                    manager = new GridLayoutManager(PhotosActivity.this, 4);
                                } else if (sorting.equals("oldest")) {
                                    manager = new GridLayoutManager(PhotosActivity.this, 4);
                                }
                                recyclerGalleryImages.setLayoutManager(manager);

                                imageProgress.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(PhotosActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            imageProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });


//        requestPermissions();
//        prepareRecyclerView();
    }

    @Override
    public void showOptions(Boolean isSelected, int position) {
        if (isSelected) {
            deleteOptions.setVisibility(View.VISIBLE);

            deletePhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(PhotosActivity.this, "test", Toast.LENGTH_SHORT).show();
                    onDeleteClick(position);
                    imagePath.remove(imagePath.get(position));
                }
            });
        } else {
            deleteOptions.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteClick(int position) {
        Image image = imagePath.get(position);
        final String key = image.getKey();
        String imageId = UUID.randomUUID().toString();

        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(image.getImageURL());
        CollectionReference toPath = fStore.collection("users").document(userID).collection("archives");

        moveImageDocument(toPath, position);

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                addArchiveReference.child(key).setValue(image);
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

    public void moveImageDocument(CollectionReference toPath, int position) {
        String archiveID = UUID.randomUUID().toString();
        Image image = imagePath.get(position);
        final String key = image.getKey();

        Map<String, Object> archive = new HashMap<>();
        archive.put("archive_id", archiveID);
        archive.put("image_id", key);
        archive.put("images", image.getImageURL());
        archive.put("timestamp", FieldValue.serverTimestamp());
        toPath.add(archive).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error deleting document");
            }
        });
    }

    private void showSortDialog() {
        String[] sortingOptions = {"Newest", "Oldest"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by").setItems(sortingOptions, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Sort", "newest");
                    editor.apply();
                    recreate();
                } else if (i == 1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Sort", "oldest");
                    editor.apply();
                    recreate();
                }
            }
        });

        builder.show();
    }

}
package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.firebase.database.Query;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PhotosActivity extends AppCompatActivity implements photosAdapter.OnItemClickListener, sortAdapter.OnItemClickListener {
    private static final int PERMISSION_REQUEST_CODE = 200;

    ImageView profile;
    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    public static ImageView addPhoto;
    ImageView archives;
    ImageView sortPhotosUpIcon;
    public static LinearLayout sortPhotos;
    public static Button selectPhotos;
    public static Button removeSelection;
    public static CheckBox qualityCheck;
    LinearLayout deletePhotos;
    ProgressBar imageProgress;
    LinearLayout deleteOptions;
    public static LinearLayout selectOptions;
    LinearLayout navbar;
    LinearLayout linearLayout;
    Boolean selectSort = true;

    public static LinearLayout sortBackground;
    Button sortNewest;
    Button sortOldest;
    Button sortDays;
    Button sortMonths;
    Button sortYears;

    List<Image> imagePath = new ArrayList<>();
    List<Image> selectedImageOptions = new ArrayList<>();
    List<Image> NewestImagePath = new ArrayList<>();
    List<Image> OldestImagePath = new ArrayList<>();
    List<Image> dayImagePath = new ArrayList<>();
    List<Image> monthImagePath = new ArrayList<>();
    List<Image> yearImagePath = new ArrayList<>();
    ArrayList<Integer> yearAdded = new ArrayList<Integer>();
    public static RecyclerView recyclerGalleryImages;
    RecyclerView recyclerSortOptions;
    photosAdapter photosAdapter;
    public static GridLayoutManager manager;
    SharedPreferences sharedPreferences;
    Boolean selectClicked = false;
    ArrayList<String> dataSource;
    LinearLayoutManager linearLayoutManager;
    sortAdapter sortAdapters;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;

    String userID;
    String databaseImageID;
    final String origin = "photos";

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private DatabaseReference dateReference;
    private DatabaseReference addArchiveReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        profile = (ImageView) findViewById(R.id.profile);
        photos = (LinearLayout) findViewById(R.id.photos);
        search = (LinearLayout) findViewById(R.id.search);
        albums = (LinearLayout) findViewById(R.id.albums);
        addPhoto = (ImageView) findViewById(R.id.addPhoto);
        archives = (ImageView) findViewById(R.id.archives);
        sortPhotosUpIcon = (ImageView) findViewById(R.id.sortPhotosUpIcon);
        sortPhotos = (LinearLayout) findViewById(R.id.sortPhotos);
        selectPhotos = (Button) findViewById(R.id.selectPhotos);
        deletePhotos = (LinearLayout) findViewById(R.id.deletePhotos);
        removeSelection = (Button) findViewById(R.id.removeSelection);
        qualityCheck = (CheckBox) findViewById(R.id.qualityCheck);
        imageProgress = (ProgressBar) findViewById(R.id.imageProgress);
        recyclerGalleryImages = (RecyclerView) findViewById(R.id.recyclerGalleryImages);
        recyclerSortOptions = (RecyclerView) findViewById(R.id.recyclerSortOptions);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        navbar = (LinearLayout) findViewById(R.id.navbar);
        deleteOptions = (LinearLayout) findViewById(R.id.deleteOptions);
        selectOptions = (LinearLayout) findViewById(R.id.selectOptions);
        sortBackground = (LinearLayout) findViewById(R.id.sortBackground);
        /*
        sortNewest = (Button) findViewById(R.id.sortNewest);
        sortOldest = (Button) findViewById(R.id.sortOldest);
        sortDays = (Button) findViewById(R.id.sortDays);
        sortMonths = (Button) findViewById(R.id.sortMonths);
        sortYears = (Button) findViewById(R.id.sortYears);
         */

        dataSource = new ArrayList<>();
        dataSource.add("Newest");
        dataSource.add("Oldest");
        dataSource.add("Days");
        dataSource.add("Months");
        dataSource.add("Years");

        linearLayoutManager = new LinearLayoutManager(PhotosActivity.this, LinearLayoutManager.HORIZONTAL, false);
        sortAdapters = new sortAdapter(PhotosActivity.this, dataSource);
        recyclerSortOptions.setLayoutManager(linearLayoutManager);
        recyclerSortOptions.setAdapter(sortAdapters);

        sharedPreferences = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String sorting = sharedPreferences.getString("Sort", "default");

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        fStore = FirebaseFirestore.getInstance();

        photosAdapter = new photosAdapter(PhotosActivity.this, imagePath, origin);
        recyclerGalleryImages.setAdapter(photosAdapter);

        photosAdapter.setOnItemClickListener(PhotosActivity.this);
        sortAdapters.setOnItemClickListener(PhotosActivity.this);

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
                if (selectSort) {
                    sortPhotosUpIcon.setRotation(180);
                    sortBackground.setVisibility(View.VISIBLE);
                    selectSort = false;
                } else {
                    sortPhotosUpIcon.setRotation(360);
                    sortBackground.setVisibility(View.GONE);
                    selectSort = true;
                }
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

        if (sorting.equals("day")) {

        } else if (sorting.equals("month")) {

        } else if (sorting.equals("year")) {

        }

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

                        manager = new GridLayoutManager(PhotosActivity.this, 4);
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

                                manager = new GridLayoutManager(PhotosActivity.this, 4);
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

                                manager = new GridLayoutManager(PhotosActivity.this, 4);
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
            navbar.setVisibility(View.GONE);

            deletePhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedImageOptions = photosAdapter.getSelectedImageOptions();
                        onDeleteClick(position);
                        imagePath.remove(imagePath.get(position));
                }
            });
        } else {
            deleteOptions.setVisibility(View.GONE);
            navbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteClick(int position) {
        for (int i = 0; i < selectedImageOptions.size(); i++) {
            Image image = selectedImageOptions.get(i);
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
                                                            Toast.makeText(PhotosActivity.this, "Photo has been sent to archives", Toast.LENGTH_SHORT).show();
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

        Calendar calendar = Calendar.getInstance();

        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + 1);
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        image.setArchiveId(archiveID);
        image.setKey(key);
        Map<String, Object> archive = new HashMap<>();
        archive.put("archive_id", archiveID);
        archive.put("image_id", key);
        archive.put("images", image.getImageURL());
        archive.put("day", day);
        archive.put("month", month);
        archive.put("year", year);
        archive.put("archive_timer_day", 0);
        archive.put("archive_timer_hour", 0);
        archive.put("archive_timer_minute", 0);
        archive.put("archive_timer_second", 0);
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

    @Override
    public void onSortNewest() {
        sortPhotosUpIcon.setRotation(360);
                selectSort = true;
                sortBackground.setVisibility(View.GONE);
                Image image = new Image();


                Query query = databaseReference.orderByChild("reverseTimeTagInteger");
                ArrayList<Long> NewestInteger = new ArrayList<Long>();
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.hasChildren()) {
                            NewestImagePath.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Image image = dataSnapshot.getValue(Image.class);
                                NewestImagePath.add(image);
                                NewestInteger.add(image.getTimeTagInteger());
                                photosAdapter.setUpdatedImages(NewestImagePath);
                                photosAdapter.notifyDataSetChanged();
                            }
                            manager = new GridLayoutManager(PhotosActivity.this, 4);
                            recyclerGalleryImages.setLayoutManager(manager);

                            imageProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onSortOldest() {
        sortPhotosUpIcon.setRotation(360);
        selectSort = true;
                sortBackground.setVisibility(View.GONE);
                Image image = new Image();

                Query query = databaseReference.orderByChild("timeTagInteger");
                ArrayList<Long> OldestInteger = new ArrayList<Long>();
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.hasChildren()) {
                            OldestImagePath.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Image image = dataSnapshot.getValue(Image.class);
                                OldestImagePath.add(image);
                                OldestInteger.add(image.getTimeTagInteger());
                                photosAdapter.setUpdatedImages(OldestImagePath);
                                photosAdapter.notifyDataSetChanged();
                            }
                            manager = new GridLayoutManager(PhotosActivity.this, 4);
                            recyclerGalleryImages.setLayoutManager(manager);

                            imageProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onSortDays() {

    }

    @Override
    public void onSortMonths() {

    }

    @Override
    public void onSortYears() {
        sortPhotosUpIcon.setRotation(360);
        selectSort = true;
        sortBackground.setVisibility(View.GONE);
        Image image = new Image();

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    yearImagePath.clear();
                    //yearAdded.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        //yearImagePath.add(image);
                        assert image != null;
                        String year = image.getYear();
                        String month = image.getMonth();
                        String day = image.getDay();

                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID + year + month + day);

                        valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot != null && snapshot.hasChildren()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Image imageDate = dataSnapshot.getValue(Image.class);
                                        yearImagePath.add(imageDate);
                                        assert imageDate != null;
                                        yearAdded.add(Integer.parseInt(imageDate.getYear()));
                                        photosAdapter.setUpdatedImages(yearImagePath);
                                        photosAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    // Change year sort to integer instead of string value
                    Collections.sort(yearAdded);

                    manager = new GridLayoutManager(PhotosActivity.this, 4);
                    recyclerGalleryImages.setLayoutManager(manager);

                    imageProgress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showSortDialog() {
        String[] sortingOptions = {"Default", "Day", "Month", "Year"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by").setItems(sortingOptions, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Sort", "default");
                    editor.apply();
                    recreate();
                } else if (i == 1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Sort", "day");
                    editor.apply();
                    recreate();
                } else if (i == 2) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Sort", "month");
                    editor.apply();
                    recreate();
                } else if (i == 3) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Sort", "year");
                    editor.apply();
                    recreate();
                }
            }
        });

        builder.show();
    }

}
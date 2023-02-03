package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PhotosActivity extends AppCompatActivity implements photosAdapter.OnItemClickListener, sortAdapter.OnItemClickListener, sortTimeAdapter.OnItemClickListener {
    private static final int PERMISSION_REQUEST_CODE = 200;

    ImageView currentPage;
    ImageView profile;
    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    public static ImageView addPhoto;
    ImageView archives;
    ImageView sortPhotosUpIcon;
    TextView yearTimeline;
    TextView monthTimeline;
    TextView dayTimeline;
    public static LinearLayout sortPhotos;
    public static Button selectPhotos;
    public static Button removeSelection;
    public static CheckBox qualityCheck;
    LinearLayout deletePhotos;
    LinearLayout archivePhotos;
    ProgressBar imageProgress;
    LinearLayout deleteOptions;
    LinearLayout sortTimeline;
    LinearLayout sortNewest;
    LinearLayout sortOldest;
    LinearLayout sortTime;
    LinearLayout sortObjects;
    public static LinearLayout selectOptions;
    LinearLayout navbar;
    LinearLayout linearLayout;
    Boolean selectSort = true;
    String yearId = UUID.randomUUID().toString();

    public static LinearLayout sortBackground;

    List<Image> imagePath = new ArrayList<>();
    List<Image> selectedImageOptions = new ArrayList<>();
    List<Image> NewestImagePath = new ArrayList<>();
    List<Image> OldestImagePath = new ArrayList<>();
    List<Image> dayImagePath = new ArrayList<>();
    List<Image> monthImagePath = new ArrayList<>();
    List<Sorting> sortingYearPath = new ArrayList<Sorting>();
    List<Sorting> sortingMonthPath = new ArrayList<Sorting>();
    List<Sorting> sortingDayPath = new ArrayList<Sorting>();
    ArrayList<Integer> yearAdded = new ArrayList<Integer>();
    public static RecyclerView recyclerGalleryImages;
    public static RecyclerView recyclerSortImages;
    public static RecyclerView recyclerSortMonthImages;
    RecyclerView recyclerSortOptions;
    photosAdapter photosAdapter;

    sortTimeAdapter sortTimeAdapter;
    sortMonthAdapter sortMonthAdapter;
    public static GridLayoutManager manager;
    public static GridLayoutManager managerSort;
    public static GridLayoutManager managerSortMonth;
    SharedPreferences sharedPreferences;
    Boolean selectClicked = false;
    ArrayList<String> dataSource;
    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager linearLayoutManagerSort;
    sortAdapter sortAdapters;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;

    String userID;
    String databaseImageID;
    final String origin = "photos";
    final String originYear = "yearSorting";

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private DatabaseReference dateReference;
    private DatabaseReference addArchiveReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        currentPage = (ImageView) findViewById(R.id.currentPage);
        profile = (ImageView) findViewById(R.id.profile);
        photos = (LinearLayout) findViewById(R.id.photos);
        search = (LinearLayout) findViewById(R.id.search);
        albums = (LinearLayout) findViewById(R.id.albums);
        addPhoto = (ImageView) findViewById(R.id.addPhoto);
        archives = (ImageView) findViewById(R.id.archives);
        //sortPhotosUpIcon = (ImageView) findViewById(R.id.sortPhotosUpIcon);
        //sortPhotos = (LinearLayout) findViewById(R.id.sortPhotos);
        selectPhotos = (Button) findViewById(R.id.selectPhotos);
        deletePhotos = (LinearLayout) findViewById(R.id.deletePhotos);
        archivePhotos = (LinearLayout) findViewById(R.id.archivePhotos);
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
        recyclerSortImages = (RecyclerView) findViewById(R.id.recyclerSortImages);
        recyclerSortMonthImages = (RecyclerView) findViewById(R.id.recyclerSortMonthImages);
        sortTimeline = (LinearLayout) findViewById(R.id.sortTimeline);
        yearTimeline = (TextView) findViewById(R.id.yearTimeline);
        monthTimeline = (TextView) findViewById(R.id.monthTimeline);
        dayTimeline = (TextView) findViewById(R.id.dayTimeline);
        sortNewest = (LinearLayout) findViewById(R.id.sortNewest);
        sortOldest = (LinearLayout) findViewById(R.id.sortOldest);
        sortTime = (LinearLayout) findViewById(R.id.sortTime);
        sortObjects = (LinearLayout) findViewById(R.id.sortObjects);

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

        sortTimeAdapter = new sortTimeAdapter(PhotosActivity.this, sortingYearPath);
        recyclerSortImages.setAdapter(sortTimeAdapter);

        sortMonthAdapter = new sortMonthAdapter(PhotosActivity.this, sortingMonthPath);
        recyclerSortMonthImages.setAdapter(sortMonthAdapter);

        photosAdapter.setOnItemClickListener(PhotosActivity.this);
        sortAdapters.setOnItemClickListener(PhotosActivity.this);
        sortTimeAdapter.setOnItemClickListener(PhotosActivity.this);

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

        /*
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
         */

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        sortNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSortNewest();
            }
        });

        sortOldest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSortOldest();
            }
        });

        sortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSortYears();
            }
        });

        sortObjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        yearTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSortYears();
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
            Calendar calendar = Calendar.getInstance();

            String yearSort = "yearsort";
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.hasChildren()) {
                        imagePath.clear();
                        monthImagePath.clear();
                        dayImagePath.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Image image = dataSnapshot.getValue(Image.class);
                            assert image != null;
                            imagePath.add(image);
                            image.setYearId(yearId);
                            photosAdapter.setUpdatedImages(imagePath);
                            photosAdapter.notifyDataSetChanged();

                            if (image.getMonth() == month) {
                                monthImagePath.add(image);
                            }

                            if (image.getDay() == day) {
                                dayImagePath.add(image);
                            }
                        }

                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID);
                        if (imagePath.size() >= 1) {
                            Map<String, Object> yearAdd = new HashMap<>();
                            yearAdd.put("year_id", yearId);
                            yearAdd.put("year", year);
                            yearAdd.put("images", imagePath);
                            yearAdd.put("thumbnail", imagePath.get(0).getImageURL());
                            Sorting yearSorting = new Sorting();
                            String yearTimeSort = dateReference.push().getKey();
                            assert yearTimeSort != null;
                            yearSorting.setKey(yearId);
                            yearSorting.setYear(year);
                            //yearSorting.setSorting_id(yearId);
                            dateReference.child(yearSort).removeValue();
                            dateReference.child(yearSort).child(yearId).setValue(yearAdd);
                        }

                        String monthId = UUID.randomUUID().toString();

                        if (monthImagePath.size() >= 1) {
                            Map<String, Object> monthAdd = new HashMap<>();
                            monthAdd.put("month_id", monthId);
                            monthAdd.put("month", month);
                            monthAdd.put("images", monthImagePath);
                            monthAdd.put("thumbnail", monthImagePath.get(0).getImageURL());

                            dateReference.child("monthSorting").child(year).child(month).removeValue();
                            dateReference.child("monthSorting").child(year).child(month).child(monthId).setValue(monthAdd);
                        }

                        String dayId = UUID.randomUUID().toString();

                        if (dayImagePath.size() >= 1) {
                            Map<String, Object> dayAdd = new HashMap<>();
                            dayAdd.put("day_id", dayId);
                            dayAdd.put("day", day);
                            dayAdd.put("images", dayImagePath);
                            dayAdd.put("thumbnail", dayImagePath.get(0).getImageURL());

                            dateReference.child("daySorting").child(year).child(month).child(day).removeValue();
                            dateReference.child("daySorting").child(year).child(month).child(day).child(dayId).setValue(dayAdd);
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
            currentPage.setVisibility(View.GONE);

            deletePhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedImageOptions = photosAdapter.getSelectedImageOptions();
                    onDeleteClick(position);
                    imagePath.remove(imagePath.get(position));
                }
            });

            archivePhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedImageOptions = photosAdapter.getSelectedImageOptions();
                    onArchiveClick(position);
                    imagePath.remove(imagePath.get(position));
                }
            });

        } else {
            deleteOptions.setVisibility(View.GONE);
            navbar.setVisibility(View.VISIBLE);
            currentPage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onArchiveClick(int position) {
        for (int i = 0; i < selectedImageOptions.size(); i++) {
            Image image = selectedImageOptions.get(i);
            final String key = image.getKey();
            String imageId = UUID.randomUUID().toString();

            CollectionReference toPath = fStore.collection("users").document(userID).collection("archives");
            moveImageDocument(toPath, position, image);

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
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted (ARCHIVED)!");
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

        }
    }

    @Override
    public void onDeleteClick(int position) {
        for (int i = 0; i < selectedImageOptions.size(); i++) {
            Image image = selectedImageOptions.get(i);
            final String key = image.getKey();

            StorageReference imageRef = firebaseStorage.getReferenceFromUrl(image.getImageURL());

            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
//                    addArchiveReference.child(key).setValue(image);
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
                                                            Toast.makeText(PhotosActivity.this, "Photo has been permanently deleted!", Toast.LENGTH_SHORT).show();
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

    public void moveImageDocument(CollectionReference toPath, int position, Image image) {
        String archiveID = UUID.randomUUID().toString();
//        Image image = imagePath.get(position);
        final String key = image.getKey();
        final String imageURL = image.getImageURL();

        Calendar calendar = Calendar.getInstance();

        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + 1);
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        image.setArchiveId(archiveID);
        image.setKey(key);
        image.setImageURL(imageURL);

        Map<String, Object> archive = new HashMap<>();
        archive.put("archive_id", image.getArchiveId());
        archive.put("image_id", image.getKey());
        archive.put("image_url", image.getImageURL());
        archive.put("day", day);
        archive.put("month", month);
        archive.put("year", year);
//        archive.put("archive_timer_day", 0);
//        archive.put("archive_timer_hour", 0);
//        archive.put("archive_timer_minute", 0);
//        archive.put("archive_timer_second", 0);
        archive.put("timestamp", FieldValue.serverTimestamp());
        toPath.add(archive).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(PhotosActivity.this, "Photo has been sent to archives", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error sending document to archive");
            }
        });
    }

    @Override
    public void onSortNewest() {
        //sortPhotosUpIcon.setRotation(360);
                selectSort = true;
                //sortBackground.setVisibility(View.GONE);
                Image image = new Image();

        recyclerSortImages.setVisibility(View.GONE);
        recyclerSortMonthImages.setVisibility(View.GONE);
        sortTimeline.setVisibility(View.GONE);
        recyclerGalleryImages.setVisibility(View.VISIBLE);

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
        //sortPhotosUpIcon.setRotation(360);
        selectSort = true;
                //sortBackground.setVisibility(View.GONE);
                Image image = new Image();

        recyclerSortImages.setVisibility(View.GONE);
        recyclerSortMonthImages.setVisibility(View.GONE);
        sortTimeline.setVisibility(View.GONE);
        recyclerGalleryImages.setVisibility(View.VISIBLE);

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
        //sortPhotosUpIcon.setRotation(360);
        selectSort = true;
        //sortBackground.setVisibility(View.GONE);
        Image image = new Image();
        Sorting sorting = new Sorting();

        recyclerSortImages.setVisibility(View.VISIBLE);
        recyclerGalleryImages.setVisibility(View.GONE);
        recyclerSortMonthImages.setVisibility(View.GONE);
        sortTimeline.setVisibility(View.VISIBLE);

        yearTimeline.setBackgroundColor(Color.parseColor("#3448db"));
        monthTimeline.setBackgroundColor(Color.parseColor("#3478db"));

        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("yearsort");

        managerSort = new GridLayoutManager(PhotosActivity.this, 1);
        recyclerSortImages.setLayoutManager(managerSort);

        valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    sortingYearPath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Sorting sortingYears = dataSnapshot.getValue(Sorting.class);
                        assert sortingYears != null;
                        sortingYearPath.add(sortingYears);
                        //yearAdded.add(Integer.parseInt(imageDate.getYear()));
                    }

                    sortTimeAdapter.setUpdatedAlbums(sortingYearPath);
                    recyclerSortImages.setAdapter(sortTimeAdapter);

                    imageProgress.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onYearClick(int position) {
        recyclerSortImages.setVisibility(View.GONE);
        recyclerSortMonthImages.setVisibility(View.VISIBLE);
        sortTimeline.setVisibility(View.VISIBLE);

        yearTimeline.setBackgroundColor(Color.parseColor("#3478db"));
        monthTimeline.setBackgroundColor(Color.parseColor("#3448db"));

        sortingMonthPath.clear();
        for (int i = 1; i <= 12; i++) {
            String month = Integer.toString(i);
            dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("monthSorting").child("2023").child(month);

            managerSortMonth = new GridLayoutManager(PhotosActivity.this, 1);
            recyclerSortMonthImages.setLayoutManager(managerSortMonth);

            valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Sorting sortingMonths = dataSnapshot.getValue(Sorting.class);
                            assert sortingMonths != null;
                            sortingMonthPath.add(sortingMonths);
                            //yearAdded.add(Integer.parseInt(imageDate.getYear()));
                        }

                        sortMonthAdapter.setUpdatedAlbums(sortingMonthPath);
                        recyclerSortMonthImages.setAdapter(sortMonthAdapter);

                        imageProgress.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
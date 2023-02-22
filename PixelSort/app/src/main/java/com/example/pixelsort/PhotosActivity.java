package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
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
import java.util.Objects;
import java.util.UUID;

public class PhotosActivity extends AppCompatActivity implements photosAdapter.OnItemClickListener, sortAdapter.OnItemClickListener, sortTimeAdapter.OnItemClickListener, sortMonthAdapter.OnItemClickListener, sortDayAdapter.OnItemClickListener, sortObjectsAdapter.OnItemClickListener {
    private static final int PERMISSION_REQUEST_CODE = 200;

    ImageView currentPage;
    ImageView profile;
    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    LinearLayout imageOptions;
    LinearLayout imageOptionsArea;
    public static ImageView addPhoto;
    ImageView archives;
    ImageView sortPhotosUpIcon;
    TextView yearTimeline;
    TextView monthTimeline;
    TextView dayTimeline;
    public static TextView photosAmount;
    public static LinearLayout sortPhotos;
    public static Button selectPhotos;
    public static Button removeSelection;
    public static CheckBox qualityCheck;
    LinearLayout deletePhotos;
    LinearLayout archivePhotos;
    ProgressBar imageProgress;
    static LinearLayout deleteOptions;
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
    String sortingTime;
    String sortingMonthTime;

    List<Image> imagePath = new ArrayList<>();
    List<ArrayList<String>> confidenceLevels = new ArrayList<>();
    List<Image> imageRedundancy = new ArrayList<>();
    List<Image> selectedImageOptions = new ArrayList<>();
    List<Image> NewestImagePath = new ArrayList<>();
    List<Image> OldestImagePath = new ArrayList<>();
    List<Image> dayImagePath = new ArrayList<>();
    List<Image> allDayImagePath = new ArrayList<>();
    List<Image> yearImagePath = new ArrayList<>();
    List<Image> monthImagePath = new ArrayList<>();
    List<Sorting> sortingYearPath = new ArrayList<Sorting>();
    List<Sorting> sortingMonthPath = new ArrayList<Sorting>();
    List<Sorting> sortingDayPath = new ArrayList<Sorting>();
    List<Image> sortingAllDayPath = new ArrayList<Image>();
    List<Sorting> sortingObjectsPath = new ArrayList<Sorting>();
    List<Image> sortingAllObjectsPath = new ArrayList<Image>();
    ArrayList<Integer> yearAdded = new ArrayList<Integer>();
    public static RecyclerView recyclerGalleryImages;
    public static RecyclerView recyclerSortImages;
    public static RecyclerView recyclerSortMonthImages;
    public static RecyclerView recyclerSortDayImages;

    public static RecyclerView recyclerSortAllDayImages;
    public static RecyclerView recyclerSortObjects;
    public static RecyclerView recyclerSortAllObjects;
    public static RecyclerView recyclerImageRedundancy;
    RecyclerView recyclerSortOptions;
    photosAdapter photosAdapter;

    sortTimeAdapter sortTimeAdapter;
    sortMonthAdapter sortMonthAdapter;
    sortDayAdapter sortDayAdapter;
    sortAllDayAdapter sortAllDayAdapter;
    sortObjectsAdapter sortObjectsAdapter;
    sortAllObjectsAdapter sortAllObjectsAdapter;
    imageCheckerAdapter imageCheckAdapter;
    public static GridLayoutManager manager;
    public static GridLayoutManager managerSort;
    public static GridLayoutManager managerSortMonth;
    public static GridLayoutManager managerSortDay;
    public static GridLayoutManager managerSortAllDay;
    public static GridLayoutManager managerSortObjects;
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
    private DatabaseReference keywordReference;
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
        recyclerSortDayImages = (RecyclerView) findViewById(R.id.recyclerSortDayImages);
        recyclerSortAllDayImages = (RecyclerView) findViewById(R.id.recyclerSortAllDayImages);
        recyclerImageRedundancy = (RecyclerView) findViewById(R.id.recyclerImageRedundancy);
        photosAmount = (TextView) findViewById(R.id.photosAmount);
        imageOptions = (LinearLayout) findViewById(R.id.imageOptions);
        imageOptionsArea = (LinearLayout) findViewById(R.id.imageRedundancy);
        recyclerSortObjects = (RecyclerView) findViewById(R.id.recyclerSortObjects);
        recyclerSortAllObjects = (RecyclerView) findViewById(R.id.recyclerSortAllObjects);

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

        sortDayAdapter = new sortDayAdapter(PhotosActivity.this, sortingDayPath);
        recyclerSortDayImages.setAdapter(sortDayAdapter);

        sortAllDayAdapter = new sortAllDayAdapter(PhotosActivity.this, sortingAllDayPath);
        recyclerSortAllDayImages.setAdapter(sortAllDayAdapter);

        sortObjectsAdapter = new sortObjectsAdapter(PhotosActivity.this, sortingObjectsPath);
        recyclerSortObjects.setAdapter(sortObjectsAdapter);

        sortAllObjectsAdapter = new sortAllObjectsAdapter(PhotosActivity.this, sortingAllObjectsPath);
        recyclerSortAllObjects.setAdapter(sortAllObjectsAdapter);

        imageCheckAdapter = new imageCheckerAdapter(PhotosActivity.this, imageRedundancy);
        recyclerImageRedundancy.setAdapter(imageCheckAdapter);

        photosAdapter.setOnItemClickListener(PhotosActivity.this);
        sortAdapters.setOnItemClickListener(PhotosActivity.this);
        sortTimeAdapter.setOnItemClickListener(PhotosActivity.this);
        sortMonthAdapter.setOnItemClickListener(PhotosActivity.this);
        sortDayAdapter.setOnItemClickListener(PhotosActivity.this);
        sortObjectsAdapter.setOnItemClickListener(PhotosActivity.this);

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

        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        sortNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortNewest.setBackgroundColor(Color.parseColor("#70fdcb6e"));
                sortOldest.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortTime.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortObjects.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                onSortNewest();
            }
        });

        sortOldest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortOldest.setBackgroundColor(Color.parseColor("#70fdcb6e"));
                sortNewest.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortTime.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortObjects.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                onSortOldest();
            }
        });

        sortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTime.setBackgroundColor(Color.parseColor("#70fdcb6e"));
                sortNewest.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortOldest.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortObjects.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                photosAmount.setVisibility(View.GONE);
                onSortYears();
            }
        });

        sortObjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortObjects.setBackgroundColor(Color.parseColor("#70fdcb6e"));
                sortNewest.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortOldest.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                sortTime.setBackgroundColor(Color.parseColor("#A9f9fafa"));
                photosAmount.setVisibility(View.GONE);
                recyclerSortImages.setVisibility(View.GONE);
                recyclerSortMonthImages.setVisibility(View.GONE);
                recyclerSortDayImages.setVisibility(View.GONE);
                recyclerSortAllDayImages.setVisibility(View.GONE);
                recyclerGalleryImages.setVisibility(View.GONE);
                sortTimeline.setVisibility(View.GONE);
                recyclerSortAllObjects.setVisibility(View.GONE);
                recyclerSortObjects.setVisibility(View.VISIBLE);

                keywordReference = FirebaseDatabase.getInstance().getReference("keywords/" + userID);

                managerSortObjects = new GridLayoutManager(PhotosActivity.this, 2);
                recyclerSortObjects.setLayoutManager(managerSortObjects);

                valueEventListener = keywordReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.hasChildren()) {
                            sortingObjectsPath.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Sorting sortingObjects = dataSnapshot.getValue(Sorting.class);
                                assert sortingObjects != null;
                                sortingObjectsPath.add(sortingObjects);

                                sortObjectsAdapter.setUpdatedAlbums(sortingObjectsPath);
                                recyclerSortObjects.setAdapter(sortObjectsAdapter);

                                imageProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //StateListDrawable gradientYearDrawable = (StateListDrawable) yearTimeline.getBackground().mutate();
        //StateListDrawable gradientMonthDrawable = (StateListDrawable) monthTimeline.getBackground().mutate();
        //StateListDrawable gradientDayDrawable = (StateListDrawable) dayTimeline.getBackground().mutate();

        yearTimeline.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                yearTimeline.setClickable(false);
                monthTimeline.setClickable(true);
                dayTimeline.setClickable(true);
                yearTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_item_background));
                monthTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_month_background));
                dayTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_day_background));
                onSortYears();
            }
        });

        monthTimeline.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                recyclerSortImages.setVisibility(View.GONE);
                recyclerSortDayImages.setVisibility(View.GONE);
                recyclerSortAllDayImages.setVisibility(View.GONE);
                recyclerSortMonthImages.setVisibility(View.VISIBLE);
                sortTimeline.setVisibility(View.VISIBLE);

                yearTimeline.setClickable(true);
                monthTimeline.setClickable(false);
                dayTimeline.setClickable(true);

                yearTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_year_unselected_background));
                monthTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_month_selected_background));
                dayTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_day_background));

                sortingMonthPath.clear();
                for (int i = 2022; i <= 2024; i++) {
                    for (int j = 1; j <= 12; j++) {
                        String years = Integer.toString(i);
                        String month = Integer.toString(j);
                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("monthSorting").child(years).child(month);

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
            }
        });

        dayTimeline.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                recyclerSortImages.setVisibility(View.GONE);
                recyclerGalleryImages.setVisibility(View.GONE);
                recyclerSortMonthImages.setVisibility(View.GONE);
                recyclerSortDayImages.setVisibility(View.VISIBLE);
                sortTimeline.setVisibility(View.VISIBLE);
                yearTimeline.setClickable(true);
                monthTimeline.setClickable(true);
                dayTimeline.setClickable(false);

                yearTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_year_unselected_background));
                monthTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_month_background));
                dayTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_day_selected_background));

                sortingDayPath.clear();
                for (int i = 2022; i <= 2024; i++) {
                    for (int j = 1; j <= 12; j++) {
                        for (int k = 1; k <= 31; k++) {
                            String years = Integer.toString(i);
                            String months = Integer.toString(j);
                            String days = Integer.toString(k);
                            dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("daySorting").child(years).child(months).child(days);

                            managerSortDay = new GridLayoutManager(PhotosActivity.this, 1);
                            recyclerSortDayImages.setLayoutManager(managerSortDay);

                            valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot != null && snapshot.hasChildren()) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Sorting sortingDays = dataSnapshot.getValue(Sorting.class);
                                            assert sortingDays != null;
                                            sortingDayPath.add(sortingDays);
                                            //yearAdded.add(Integer.parseInt(imageDate.getYear()));
                                        }

                                        sortDayAdapter.setUpdatedAlbums(sortingDayPath);
                                        recyclerSortDayImages.setAdapter(sortDayAdapter);

                                        imageProgress.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    }
                }
        });

        final boolean[] imageOptionsSelected = {false};

        imageOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageOptionsSelected[0]) {
                    imageOptionsArea.setVisibility(View.VISIBLE);
                    imageOptionsSelected[0] = true;
                } else if (imageOptionsSelected[0]) {
                    imageOptionsArea.setVisibility(View.GONE);
                    imageOptionsSelected[0] = false;
                }
            }
        });


        //*****************************Gallery Images********************************

        if (sorting.equals("day")) {

        } else if (sorting.equals("month")) {

        } else if (sorting.equals("year")) {

        }

        // Loop through all the document fields and check if there exists two confidence values that are the same
        // If there are two or more, add all but the first one into a different array for confirmation and delete those images

        // When uploading images, check if the confidence values already exist within the database. If they do, then the image is a duplicate image

        if (!qualityCheck.isChecked()) {
            Calendar calendar = Calendar.getInstance();

            String yearSort = "yearsort";
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

            keywordReference = FirebaseDatabase.getInstance().getReference("keywords/" + userID);
            List<Image> keywordArray = new ArrayList<>();
            List<String> usedKeywords = new ArrayList<>();
            final boolean[] keywordUsed = {false};

            onSortNewest();

            Query query = databaseReference.orderByChild("reverseTimeTagInteger");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.hasChildren()) {
                        imagePath.clear();
                        yearImagePath.clear();
                        monthImagePath.clear();
                        dayImagePath.clear();
                        confidenceLevels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Image image = dataSnapshot.getValue(Image.class);
                            assert image != null;
                            imagePath.add(image);
                            image.setYearId(yearId);
                            confidenceLevels.add(image.getConfidence());
                            //photosAdapter.setUpdatedImages(imagePath);
                            //photosAdapter.notifyDataSetChanged();

                            if (Objects.equals(image.getYear(), year)) {
                                yearImagePath.add(image);
                            }

                            if (Objects.equals(image.getMonth(), month) && Objects.equals(image.getYear(), year)) {
                                monthImagePath.add(image);
                            }

                            if (Objects.equals(image.getDay(), day) && Objects.equals(image.getMonth(), month) && Objects.equals(image.getYear(), year)) {
                                dayImagePath.add(image);
                            }
                        }

                        for (int i = 0; i < imagePath.size(); i++) {
                            for (int j = 0; j < imagePath.get(i).getKeywords().size(); j++) {
                                String currentKeywords = imagePath.get(i).getKeywords().get(j);
                                String currentKeyword = currentKeywords.substring(0, currentKeywords.length() - 1);

                                if (usedKeywords.size() == 0) {
                                    usedKeywords.add(currentKeyword);
                                    //keywordArray.add(imagePath.get(i));

                                } else {
                                    boolean keywordContained = usedKeywords.contains(currentKeyword);
                                    if (keywordContained) {
                                        continue;
                                    } else {

                                        usedKeywords.add(currentKeyword);
                                    }
                                }

                                String keywordsId = UUID.randomUUID().toString();

                                for (int k = 0; k < imagePath.size(); k++) {
                                    if (imagePath.get(k).getKeywords().contains(currentKeywords)) {
                                        keywordArray.add(imagePath.get(i));
                                    }
                                }

                                if (keywordArray.size() >= 1) {
                                    Map<String, Object> keywordsAdd = new HashMap<>();
                                    keywordsAdd.put("keyword_id", keywordsId);
                                    keywordsAdd.put("keyword", currentKeyword);
                                    keywordsAdd.put("images", keywordArray);
                                    keywordsAdd.put("thumbnail", keywordArray.get(0).getImageURL());
                                    //keywordReference.child(keywordsId).removeValue();
                                    keywordReference.child(currentKeyword).setValue(keywordsAdd);
                                }
                            }

                            keywordArray.clear();
                        }

                        int counter = 0;
                        int imageInstance = 0;
                        boolean imageCounter = false;
                        ArrayList<Integer> jValues = new ArrayList<>();
                        imageRedundancy.clear();
                        for (int i = 0; i < confidenceLevels.size(); i++) {
                            for (int j = i; j < confidenceLevels.size(); j++) {
                                if (confidenceLevels.get(i).equals(confidenceLevels.get(j))) {
                                    if (i == j) {
                                        continue;
                                    }

                                    jValues.add(j);

                                    for (int k = 0; k < jValues.size(); k++) {
                                        if (jValues.get(k) == j) {
                                            imageInstance++;
                                        }
                                    }

                                    if (imageInstance > 1) {
                                        imageCounter = false;
                                    } else {
                                        imageCounter = true;
                                    }

                                    if (!imageCounter) {
                                        imageInstance = 0;
                                        continue;
                                    } else if (imageCounter){
                                        counter++;
                                        imageRedundancy.add(imagePath.get(j));
                                        imageInstance = 0;
                                    }
                                }
                            }
                        }

                        Toast.makeText((Context) PhotosActivity.this, "Duplicate images" + counter, Toast.LENGTH_SHORT).show();

                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID);

                        String yearsId = UUID.randomUUID().toString();

                        if (yearImagePath.size() >= 1) {
                            Map<String, Object> yearAdd = new HashMap<>();
                            yearAdd.put("year_id", yearsId);
                            yearAdd.put("year", year);
                            yearAdd.put("images", yearImagePath);
                            yearAdd.put("thumbnail", yearImagePath.get(0).getImageURL());
                            //Sorting yearSorting = new Sorting();
                            //String yearTimeSort = dateReference.push().getKey();
                            //assert yearTimeSort != null;
                            //yearSorting.setKey(yearId);
                            //yearSorting.setYear(year);
                            //yearSorting.setSorting_id(yearId);
                            dateReference.child("yearSorting").child(year).removeValue();
                            dateReference.child("yearSorting").child(year).child(yearsId).setValue(yearAdd);
                        }

                        String monthId = UUID.randomUUID().toString();

                        if (monthImagePath.size() >= 1) {
                            Map<String, Object> monthAdd = new HashMap<>();
                            monthAdd.put("month_id", monthId);
                            monthAdd.put("month", month);
                            monthAdd.put("year", year);
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
                            dayAdd.put("month", month);
                            dayAdd.put("year", year);
                            dayAdd.put("images", dayImagePath);
                            dayAdd.put("thumbnail", dayImagePath.get(0).getImageURL());

                            dateReference.child("daySorting").child(year).child(month).child(day).removeValue();
                            dateReference.child("daySorting").child(year).child(month).child(day).child(dayId).setValue(dayAdd);
                        }

//                    Log.d(TAG, "IMAGEPATH: " + imagePath);

                        //manager = new GridLayoutManager(PhotosActivity.this, 4);
                        //recyclerGalleryImages.setLayoutManager(manager);

                        manager = new GridLayoutManager(PhotosActivity.this, 4);
                        recyclerImageRedundancy.setLayoutManager(manager);

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
            addPhoto.setVisibility(View.VISIBLE);
            navbar.setVisibility(View.VISIBLE);
            currentPage.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onArchiveClick(int position) {
        for (int i = 0; i < selectedImageOptions.size(); i++) {
            Image image = selectedImageOptions.get(i);
            final String key = image.getKey();
            String imageId = UUID.randomUUID().toString();
            final boolean[] imageRemoved = {false};
            final boolean[] imageDayRemoved = {false};
            boolean imageMonthRemoved = false;

            CollectionReference toPath = fStore.collection("users").document(userID).collection("archives");
            moveImageDocument(toPath, position, image);

            addArchiveReference.child(key).setValue(image);
            databaseReference.child(key).removeValue();

                        String yearTime = image.getYear();
                        String monthTime = image.getMonth();
                        String dayTime = image.getDay();
                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("allDays").child(yearTime).child(monthTime).child(dayTime);
                        dateReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("allDays").child(yearTime).child(monthTime).child(dayTime);
                                valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot != null && snapshot.hasChildren()) {
                                        } else {
                                                dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("daySorting").child(yearTime).child(monthTime).child(dayTime);
                                                dateReference.removeValue();

                                            dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("daySorting").child(yearTime).child(monthTime);
                                            valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot != null && snapshot.hasChildren()) {
                                                    } else {
                                                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("monthSorting").child(yearTime).child(monthTime);
                                                        dateReference.removeValue();

                                                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("monthSorting").child(yearTime);
                                                        valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot != null && snapshot.hasChildren()) {
                                                                } else {
                                                                    dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("yearSorting").child(yearTime);
                                                                    dateReference.removeValue();
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });


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
        removeSelection.callOnClick();
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
                    String yearTime = image.getYear();
                    String monthTime = image.getMonth();
                    String dayTime = image.getDay();
                    dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("allDays").child(yearTime).child(monthTime).child(dayTime);
                    dateReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("allDays").child(yearTime).child(monthTime).child(dayTime);
                            valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot != null && snapshot.hasChildren()) {
                                    } else {
                                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("daySorting").child(yearTime).child(monthTime).child(dayTime);
                                        dateReference.removeValue();

                                        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("daySorting").child(yearTime).child(monthTime);
                                        valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot != null && snapshot.hasChildren()) {
                                                } else {
                                                    dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("monthSorting").child(yearTime).child(monthTime);
                                                    dateReference.removeValue();

                                                    dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("monthSorting").child(yearTime);
                                                    valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot != null && snapshot.hasChildren()) {
                                                            } else {
                                                                dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("yearSorting").child(yearTime);
                                                                dateReference.removeValue();
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
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
        removeSelection.callOnClick();
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
        recyclerSortDayImages.setVisibility(View.GONE);
        recyclerSortAllDayImages.setVisibility(View.GONE);
        sortTimeline.setVisibility(View.GONE);
        recyclerSortObjects.setVisibility(View.GONE);
        recyclerSortAllObjects.setVisibility(View.GONE);
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
        recyclerSortDayImages.setVisibility(View.GONE);
        recyclerSortAllDayImages.setVisibility(View.GONE);
        sortTimeline.setVisibility(View.GONE);
        recyclerSortObjects.setVisibility(View.GONE);
        recyclerSortAllObjects.setVisibility(View.GONE);
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

    @SuppressLint("UseCompatLoadingForDrawables")
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
        recyclerSortDayImages.setVisibility(View.GONE);
        recyclerSortAllDayImages.setVisibility(View.GONE);
        recyclerSortObjects.setVisibility(View.GONE);
        recyclerSortAllObjects.setVisibility(View.GONE);
        sortTimeline.setVisibility(View.VISIBLE);
        yearTimeline.setClickable(false);
        monthTimeline.setClickable(true);
        dayTimeline.setClickable(true);

        yearTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_item_background));
        monthTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_month_background));
        dayTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_day_background));

        sortingYearPath.clear();

        for (int i = 2000; i <= 2100; i++) {
            String years = Integer.toString(i);
            dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("yearSorting").child(years);

            managerSort = new GridLayoutManager(PhotosActivity.this, 1);
            recyclerSortImages.setLayoutManager(managerSort);

            valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.hasChildren()) {
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
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onYearClick(int position) {
        recyclerSortImages.setVisibility(View.GONE);
        recyclerGalleryImages.setVisibility(View.GONE);
        recyclerSortDayImages.setVisibility(View.GONE);
        recyclerSortAllDayImages.setVisibility(View.GONE);
        recyclerSortObjects.setVisibility(View.GONE);
        recyclerSortAllObjects.setVisibility(View.GONE);
        recyclerSortMonthImages.setVisibility(View.VISIBLE);
        sortTimeline.setVisibility(View.VISIBLE);
        yearTimeline.setClickable(true);
        monthTimeline.setClickable(false);
        dayTimeline.setClickable(true);

        yearTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_year_unselected_background));
        monthTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_month_selected_background));
        dayTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_day_background));

        Sorting sorting = sortingYearPath.get(position);
        sortingTime = sorting.getYear();

        sortingMonthPath.clear();
            for (int j = 1; j <= 12; j++) {
                String month = Integer.toString(j);
                dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("monthSorting").child(sortingTime).child(month);

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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onMonthClick(int position) {
        recyclerSortImages.setVisibility(View.GONE);
        recyclerGalleryImages.setVisibility(View.GONE);
        recyclerSortMonthImages.setVisibility(View.GONE);
        recyclerSortAllDayImages.setVisibility(View.GONE);
        recyclerSortObjects.setVisibility(View.GONE);
        recyclerSortAllObjects.setVisibility(View.GONE);
        recyclerSortDayImages.setVisibility(View.VISIBLE);
        sortTimeline.setVisibility(View.VISIBLE);
        yearTimeline.setClickable(true);
        monthTimeline.setClickable(true);
        dayTimeline.setClickable(false);

        yearTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_year_unselected_background));
        monthTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_month_background));
        dayTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_day_selected_background));

        Sorting sortingMonths = sortingMonthPath.get(position);
        sortingMonthTime = sortingMonths.getMonth();

        sortingDayPath.clear();
        for (int j = 1; j <= 31; j++) {
            String days = Integer.toString(j);
            dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("daySorting").child(sortingTime).child(sortingMonthTime).child(days);

            managerSortDay = new GridLayoutManager(PhotosActivity.this, 1);
            recyclerSortDayImages.setLayoutManager(managerSortDay);

            valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.hasChildren()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Sorting sortingDays = dataSnapshot.getValue(Sorting.class);
                            assert sortingDays != null;
                            sortingDayPath.add(sortingDays);
                            //yearAdded.add(Integer.parseInt(imageDate.getYear()));
                        }

                        sortDayAdapter.setUpdatedAlbums(sortingDayPath);
                        recyclerSortDayImages.setAdapter(sortDayAdapter);

                        imageProgress.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onDayClick(int position) {
        recyclerSortImages.setVisibility(View.GONE);
        recyclerGalleryImages.setVisibility(View.GONE);
        recyclerSortMonthImages.setVisibility(View.GONE);
        recyclerSortDayImages.setVisibility(View.GONE);
        recyclerSortObjects.setVisibility(View.GONE);
        recyclerSortAllObjects.setVisibility(View.GONE);
        recyclerSortAllDayImages.setVisibility(View.VISIBLE);
        sortTimeline.setVisibility(View.VISIBLE);
        yearTimeline.setClickable(true);
        monthTimeline.setClickable(true);
        dayTimeline.setClickable(false);

        yearTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_year_unselected_background));
        monthTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_month_background));
        dayTimeline.setBackground(getResources().getDrawable(R.drawable.timeline_day_selected_background));

        Sorting sortingDays = sortingDayPath.get(position);
        String sortingDayTime = sortingDays.getDay();

            dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("allDays").child(sortingTime).child(sortingMonthTime).child(sortingDayTime);

            managerSortAllDay = new GridLayoutManager(PhotosActivity.this, 4);
            recyclerSortAllDayImages.setLayoutManager(managerSortAllDay);

            valueEventListener = dateReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null && snapshot.hasChildren()) {
                        sortingAllDayPath.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Image image = dataSnapshot.getValue(Image.class);
                            //Sorting sortingAllDays = dataSnapshot.getValue(Sorting.class);
                            assert image != null;
                            sortingAllDayPath.add(image);
                            //yearAdded.add(Integer.parseInt(imageDate.getYear()));
                        }

                        sortAllDayAdapter.setUpdatedAlbums(sortingAllDayPath);
                        recyclerSortAllDayImages.setAdapter(sortAllDayAdapter);
                        sortAllDayAdapter.notifyDataSetChanged();

                        imageProgress.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    @Override
    public void onObjectClick(int position, String keyword) {
        recyclerSortObjects.setVisibility(View.GONE);
        recyclerSortAllObjects.setVisibility(View.VISIBLE);

        managerSortAllDay = new GridLayoutManager(PhotosActivity.this, 4);
        recyclerSortAllObjects.setLayoutManager(managerSortAllDay);

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    sortingAllObjectsPath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;
                        if (image.getKeywords().contains(keyword + "\n")) {
                            sortingAllObjectsPath.add(image);
                        } else {
                            continue;
                        }
                    }

                    sortAllObjectsAdapter.setUpdatedAlbums(sortingAllObjectsPath);
                    recyclerSortAllObjects.setAdapter(sortAllObjectsAdapter);
                    sortAllObjectsAdapter.notifyDataSetChanged();

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
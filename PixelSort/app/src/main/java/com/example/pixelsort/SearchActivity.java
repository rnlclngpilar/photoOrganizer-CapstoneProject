package com.example.pixelsort;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ImageView profile;
    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    ImageView archives;
    TextView albumsSelected;
    TextView imagesSelected;

    DatabaseReference databaseReference;
    DatabaseReference keywordReference;
    FirebaseAuth mAuth;
    String userID;
    List<Image> searchedImages;
    List<Image> myImages = new ArrayList<>();
    List<Sorting> sortingObjectsPath = new ArrayList<>();
    List<Sorting> myObjectImages = new ArrayList<>();
    final String origin = "Albums";
    boolean selectedImage = true;
    RecyclerView recyclerSearchImages;
    SearchView searchBar;
    searchAdapter searchAdapt;
    GridLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //*****************************NAVIGATION BAR********************************
        profile = (ImageView) findViewById(R.id.profile);
        photos = (LinearLayout) findViewById(R.id.photos);
        search = (LinearLayout) findViewById(R.id.search);
        albums = (LinearLayout) findViewById(R.id.albums);
        archives = (ImageView) findViewById(R.id.archives);
        recyclerSearchImages = (RecyclerView) findViewById(R.id.recyclerSearchImages);
        searchBar = (SearchView) findViewById(R.id.searchBar);
        albumsSelected = (TextView) findViewById(R.id.albumsSelected);
        imagesSelected = (TextView) findViewById(R.id.imagesSelected);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);
        keywordReference = FirebaseDatabase.getInstance().getReference("keywords/" + userID);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });

        albumsSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImage = false;
                albumsSelected.setBackgroundColor(Color.parseColor("#34495E"));
                albumsSelected.setTextColor(Color.parseColor("#ECF0F1"));
                imagesSelected.setBackgroundColor(Color.parseColor("#ECF0F1"));
                imagesSelected.setTextColor(Color.parseColor("#34495E"));
                searchedImages.clear();
                keywordReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.hasChildren()) {
                            sortingObjectsPath.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Sorting sortingObjects = dataSnapshot.getValue(Sorting.class);
                                assert sortingObjects != null;
                                sortingObjectsPath.add(sortingObjects);

                                searchAdapt = new searchAdapter(SearchActivity.this, sortingObjectsPath, origin);
                                recyclerSearchImages.setAdapter(searchAdapt);

                                manager = new GridLayoutManager(SearchActivity.this, 2);
                                recyclerSearchImages.setLayoutManager(manager);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        imagesSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImage = true;
                albumsSelected.setBackgroundColor(Color.parseColor("#ECF0F1"));
                albumsSelected.setTextColor(Color.parseColor("#34495E"));
                imagesSelected.setBackgroundColor(Color.parseColor("#34495E"));
                imagesSelected.setTextColor(Color.parseColor("#ECF0F1"));
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            searchedImages = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                searchedImages.add(dataSnapshot.getValue(Image.class));
                            }

                            searchAdapt = new searchAdapter(SearchActivity.this, searchedImages);
                            recyclerSearchImages.setAdapter(searchAdapt);

                            manager = new GridLayoutManager(SearchActivity.this, 4);
                            recyclerSearchImages.setLayoutManager(manager);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this, "Error loading images from the database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //*************************************************************************

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (databaseReference != null) {
            if (selectedImage) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            searchedImages = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                searchedImages.add(dataSnapshot.getValue(Image.class));
                            }

                            searchAdapt = new searchAdapter(SearchActivity.this, searchedImages);
                            recyclerSearchImages.setAdapter(searchAdapt);

                            manager = new GridLayoutManager(SearchActivity.this, 4);
                            recyclerSearchImages.setLayoutManager(manager);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this, "Error loading images from the database", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                keywordReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null && snapshot.hasChildren()) {
                            sortingObjectsPath.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Sorting sortingObjects = dataSnapshot.getValue(Sorting.class);
                                assert sortingObjects != null;
                                sortingObjectsPath.add(sortingObjects);

                                searchAdapt = new searchAdapter(SearchActivity.this, sortingObjectsPath, origin);
                                recyclerSearchImages.setAdapter(searchAdapt);

                                manager = new GridLayoutManager(SearchActivity.this, 2);
                                recyclerSearchImages.setLayoutManager(manager);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this, "Error loading images from the database", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        imagesSelected.setEnabled(true);
        albumsSelected.setEnabled(true);

        if (searchBar != null) {
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    //search(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    imagesSelected.setEnabled(false);
                    albumsSelected.setEnabled(false);
                    if (s.equals("")) {
                        onStart();
                    }
                    search(s);
                    return true;
                }
            });
        }
    }

    private void search(String word) {
        if (selectedImage) {
            myImages.clear();
            for (Image object : searchedImages) {
                for (int i = 0; i < object.getKeywords().size(); i++) {
                    if (object.getKeywords().get(i).toLowerCase().contains(word.toLowerCase())) {
                        if (!myImages.contains(object)) {
                            myImages.add(object);
                        }
                    }
                }
            }
            searchAdapt = new searchAdapter(SearchActivity.this, myImages);
            recyclerSearchImages.setAdapter(searchAdapt);
        } else {
            myObjectImages.clear();
            for (Sorting object : sortingObjectsPath) {
                    if (object.getKeyword().toLowerCase().contains(word.toLowerCase())) {
                        if (!myObjectImages.contains(object)) {
                            myObjectImages.add(object);
                        }
                }
            }
            searchAdapt = new searchAdapter(SearchActivity.this, myObjectImages, origin);
            recyclerSearchImages.setAdapter(searchAdapt);
        }
    }
}
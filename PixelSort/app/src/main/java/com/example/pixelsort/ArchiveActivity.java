package com.example.pixelsort;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {

    ImageView profile;
    ImageView photos;
    ImageView search;
    ImageView albums;
    ImageView archives;
    ImageView archiveOptions;

    List<Image> archivePath = new ArrayList<>();
    RecyclerView recyclerArchiveImages;
    archiveAdapter photosAdapter;
    GridLayoutManager manager;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;

    String userID;

    private DatabaseReference addArchiveReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        profile = (ImageView) findViewById(R.id.profile);
        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        archives = (ImageView) findViewById(R.id.archives);
        archiveOptions = (ImageView) findViewById(R.id.archiveOptions);
        recyclerArchiveImages = (RecyclerView) findViewById(R.id.recyclerArchiveImages);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        photosAdapter = new archiveAdapter(ArchiveActivity.this, archivePath);
        recyclerArchiveImages.setAdapter(photosAdapter);

        addArchiveReference = FirebaseDatabase.getInstance().getReference("archives/" + userID);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });

        valueEventListener = addArchiveReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    archivePath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Image image = dataSnapshot.getValue(Image.class);
                        assert image != null;
                        archivePath.add(image);
                    }
                    photosAdapter.setUpdatedImages(archivePath);
                    photosAdapter.notifyDataSetChanged();

                    manager = new GridLayoutManager(ArchiveActivity.this, 4);
                    recyclerArchiveImages.setLayoutManager(manager);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ArchiveActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
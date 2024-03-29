package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AlbumsActivity extends AppCompatActivity implements albumsAdapter.OnItemClickListener{

    ImageView profile;
    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    ImageView archives;
    static ImageView createNewAlbum;
    RecyclerView recyclerAlbums;
    ProgressBar imageProgressAl;

    public static Button selectAlbums;
    public static LinearLayout selectOptions;
    public static Button removeSelection;

    static LinearLayout deleteOptions;
    LinearLayout deleteAlbums;

    LinearLayout footernav;
    ImageView currentPage;

    List<Album> selectedAlbumsOptions = new ArrayList<>();
    List<Album> albumPath = new ArrayList<Album>();
    albumsAdapter albumsAdapter;

    String userID;
    String databaseAlbumID;

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
        photos = (LinearLayout) findViewById(R.id.photos);
        search = (LinearLayout) findViewById(R.id.search);
        albums = (LinearLayout) findViewById(R.id.albums);
        archives = (ImageView) findViewById(R.id.archives);
        imageProgressAl = (ProgressBar) findViewById(R.id.imageProgressAl);
        recyclerAlbums = (RecyclerView) findViewById(R.id.recylerAlbums);

        selectAlbums = (Button) findViewById(R.id.selectAlbums);
        selectOptions = (LinearLayout) findViewById(R.id.selectOptions);
        removeSelection = (Button) findViewById(R.id.removeSelection);

        deleteOptions = (LinearLayout) findViewById(R.id.deleteOptions);
        deleteAlbums = (LinearLayout) findViewById(R.id.deleteAlbums);

        footernav = (LinearLayout) findViewById(R.id.footernav);
        currentPage = (ImageView) findViewById(R.id.currentPage);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        albumsAdapter = new albumsAdapter(AlbumsActivity.this, albumPath);
        recyclerAlbums.setAdapter(albumsAdapter);

        albumsAdapter.setOnItemClickListener(AlbumsActivity.this);

        databaseReference = FirebaseDatabase.getInstance().getReference("albums/" + userID);

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

        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumsActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });

        selectAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        manager = new GridLayoutManager(AlbumsActivity.this, 2);
        recyclerAlbums.setLayoutManager(manager);

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChildren()) {
                    albumPath.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Album album = dataSnapshot.getValue(Album.class);
                        assert album != null;
                        albumPath.add(album);
                    }
                    albumsAdapter.setUpdatedAlbums(albumPath);
                    recyclerAlbums.setAdapter(albumsAdapter);
                    albumsAdapter.notifyDataSetChanged();

                    imageProgressAl.setVisibility(View.INVISIBLE);
//                    Log.d(TAG, "ALBUMPATH " + albumPath.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AlbumsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                imageProgressAl.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void showOptions(Boolean isSelected, int position) {
        if (isSelected) {
            deleteOptions.setVisibility(View.VISIBLE);
            footernav.setVisibility(View.GONE);
            currentPage.setVisibility(View.GONE);

            deleteAlbums.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedAlbumsOptions = albumsAdapter.getSelectedAlbums();
                    onDeleteClick(position);
                    albumPath.remove(albumPath.get(position));
                }
            });

        } else {
            createNewAlbum.setVisibility(View.VISIBLE);
            footernav.setVisibility(View.VISIBLE);
            currentPage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteClick(int position) {
        for (int i = 0; i < selectedAlbumsOptions.size(); i++) {
            Album album = albumPath.get(i);
            final String album_id = album.getAlbum_id();

            databaseReference.child(album_id).removeValue();
            fStore.collection("users")
                    .document(userID)
                    .collection("albums")
                    .whereEqualTo("album_id", album_id)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    databaseAlbumID = document.getId();

                                    fStore.collection("users")
                                            .document(userID)
                                            .collection("albums")
                                            .document(databaseAlbumID)
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

                                Toast.makeText(AlbumsActivity.this, "Albums has been deleted", Toast.LENGTH_SHORT).show();

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
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

}
package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArchiveActivity extends AppCompatActivity implements archiveAdapter.OnItemClickListener {

    ImageView profile;
    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    ImageView archives;
    ImageView archiveOptions;
    public static Button selectPhotos;
    public static Button removeSelection;
    public static LinearLayout selectOptions;
    LinearLayout deletePhotos;
    LinearLayout unarchivePhotos;
    LinearLayout deleteOptions;
    LinearLayout navbar;

    List<Image> archivePath = new ArrayList<>();
    List<Image> selectedImageOptions = new ArrayList<>();
    RecyclerView recyclerArchiveImages;
    archiveAdapter archiveAdapter;
    GridLayoutManager manager;

    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;

    String userID;
    String archiveDocumentId;

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private DatabaseReference addArchiveReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        profile = (ImageView) findViewById(R.id.profile);
        photos = (LinearLayout) findViewById(R.id.photos);
        search = (LinearLayout) findViewById(R.id.search);
        albums = (LinearLayout) findViewById(R.id.albums);
        archives = (ImageView) findViewById(R.id.archives);
        archiveOptions = (ImageView) findViewById(R.id.archiveOptions);
        removeSelection = (Button) findViewById(R.id.removeSelection);
        selectPhotos = (Button) findViewById(R.id.selectPhotos);
        recyclerArchiveImages = (RecyclerView) findViewById(R.id.recyclerArchiveImages);
        navbar = (LinearLayout) findViewById(R.id.navbar);
        deletePhotos = (LinearLayout) findViewById(R.id.deletePhotos);
        unarchivePhotos = (LinearLayout) findViewById(R.id.unarchivePhotos);
        deleteOptions = (LinearLayout) findViewById(R.id.deleteOptions);
        selectOptions = (LinearLayout) findViewById(R.id.selectOptions);

        archiveAdapter = new archiveAdapter(ArchiveActivity.this, archivePath);
        recyclerArchiveImages.setAdapter(archiveAdapter);
        archiveAdapter.setOnItemClickListener(ArchiveActivity.this);

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);
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


        /*
        selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotos.setBackgroundColor(Color.parseColor("#ECF0F1"));
                selectPhotos.setTextColor(Color.parseColor("#000000"));
                selectOptions.setVisibility(View.VISIBLE);
            }
        });
         */

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
                    archiveAdapter.setUpdatedImages(archivePath);
                    archiveAdapter.notifyDataSetChanged();

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
    public void showOptions(Boolean isSelected, int position) {
        if (isSelected) {
            deleteOptions.setVisibility(View.VISIBLE);
            navbar.setVisibility(View.GONE);

            unarchivePhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedImageOptions = archiveAdapter.getSelectedImageOptions();
                    onUnarchiveClick(position);
                    archivePath.remove(archivePath.get(position));
                }
            });

            deletePhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedImageOptions = archiveAdapter.getSelectedImageOptions();
                    onDelete(position);
                    archivePath.remove(archivePath.get(position));
                }
            });
        } else {
            deleteOptions.setVisibility(View.GONE);
            navbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUnarchiveClick(int position) {
        for (int i = 0; i < selectedImageOptions.size(); i++) {
            Image image = selectedImageOptions.get(i);
            final String key = image.getKey();
            String imageId = UUID.randomUUID().toString();

            CollectionReference toPath = fStore.collection("users").document(userID).collection("images");
            moveImageDocument(toPath, position, image);

            databaseReference.child(key).setValue(image);
            addArchiveReference.child(key).removeValue();

            fStore.collection("users")
                    .document(userID)
                    .collection("archives")
                    .whereEqualTo("image_id", key)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    archiveDocumentId = document.getId();

                                    fStore.collection("users")
                                            .document(userID)
                                            .collection("archives")
                                            .document(archiveDocumentId)
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "DocumentSnapshot successfully (UNARCHIVED)!");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting document", e);
                                                }
                                            });
                                }
                            }
                        }
                    });

            Toast.makeText(ArchiveActivity.this, "Image has been unarchived", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDelete(int position) {
        for (int i = 0; i < selectedImageOptions.size(); i++) {
            Image image = selectedImageOptions.get(i);
            final String key = image.getKey();
            String imageId = UUID.randomUUID().toString();

            StorageReference imageRef = firebaseStorage.getReferenceFromUrl(image.getImageURL());
            imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    addArchiveReference.child(key).removeValue();

                    fStore.collection("users")
                            .document(userID)
                            .collection("archives")
                            .whereEqualTo("image_id", key)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            archiveDocumentId = document.getId();

                                            fStore.collection("users")
                                                    .document(userID)
                                                    .collection("archives")
                                                    .document(archiveDocumentId)
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
                                    }
                                }
                            });
                }
            });

            Toast.makeText(ArchiveActivity.this, "Image has been deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    public void moveImageDocument(CollectionReference toPath, int position, Image image) {
        String imageId = UUID.randomUUID().toString();
//        Image image = archivePath.get(position);
        final String key = image.getKey();

        image.setKey(key);
        Map<String, Object> userImages = new HashMap<>();
        userImages.put("image_id", image.getImageId());
        userImages.put("image_url", image.getImageURL());
        userImages.put("keywords", image.getKeywords());
        userImages.put("day", image.getDay());
        userImages.put("month", image.getMonth());
        userImages.put("year", image.getYear());
        userImages.put("high_quality", image.getHighQuality());

        toPath.add(userImages).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error unarchiving document");
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
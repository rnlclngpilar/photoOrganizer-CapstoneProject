package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class addPhotos extends AppCompatActivity {

    ImageView photos;
    ImageView search;
    ImageView albums;
    ImageView viewPhoto;
    Button browsePhotos;
    Button uploadPhoto;
    ProgressBar progressBar;
    String userID;
    private Uri imageSelected;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseDatabase fDatabase;
    private StorageTask uploadImageTask;
    ArrayList<String> keywordsArray;
    private ImageLabeler imageLabeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photos);

        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        viewPhoto = (ImageView) findViewById(R.id.viewPhoto);
        browsePhotos = (Button) findViewById(R.id.browsePhotos);
        uploadPhoto = (Button) findViewById(R.id.uploadPhoto);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        keywordsArray = new ArrayList<>();

        imageLabeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build());

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addPhotos.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addPhotos.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addPhotos.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        browsePhotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadImageTask != null && uploadImageTask.isInProgress()) {
                    Toast.makeText(addPhotos.this, "Image upload is in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageSelected = data.getData();
            //viewPhoto.setImageURI(imageSelected);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageSelected);
                InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
                imageLabeler.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> imageLabels) {
                        if (imageLabels.size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            for (ImageLabel label : imageLabels) {
                                //builder.append(label.getText()).append(" : ").append(label.getConfidence()).append("\n");
                                builder.append(label.getText()).append("\n");
                                keywordsArray.add(builder.toString());
                                builder.delete(0, builder.length());
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

                viewPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage() {
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        storageReference = storage.getReference();
        userID = mAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference("images/" + userID);
        databaseReference = fDatabase.getReference().child(userID).child("images");

        if (imageSelected != null) {
            StorageReference fileReference = storageReference.child(UUID.randomUUID().toString() + "." + getFileExtension(imageSelected));
            uploadImageTask = fileReference.putFile(imageSelected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    },500);

                    Toast.makeText(addPhotos.this, "Image was uploaded successfully", Toast.LENGTH_SHORT).show();
                    Image image = new Image(taskSnapshot.getUploadSessionUri().toString(), keywordsArray, String.valueOf(FieldValue.serverTimestamp()));
                    String imageID = databaseReference.push().getKey();
                    databaseReference.child(imageID).setValue(image);

                    Map<String, Object> userImages = new HashMap<>();
                    userImages.put("image_url", taskSnapshot.getUploadSessionUri().toString());
                    userImages.put("keywords", keywordsArray);
                    userImages.put("timestamp", FieldValue.serverTimestamp());
                    fStore.collection("users").document(userID).collection("images").add(userImages);

                    Intent intent = new Intent(addPhotos.this, PhotosActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(addPhotos.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(addPhotos.this, "No file was selected", Toast.LENGTH_SHORT).show();
        }

        /*
        databaseReference = fDatabase.getReference().child(userID).child("images");

        if (imageSelected != null) {



            StorageReference ref = storageReference.child("images/" + userID);

            UploadTask uploadTask = ref.putFile(imageSelected);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        Toast.makeText(addPhotos.this, "Photo Uploading", Toast.LENGTH_SHORT).show();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String image_url = String.valueOf(downloadUri);

                        Map<String, Object> userImages = new HashMap<>();
                        userImages.put("image_url", image_url);
                        userImages.put("keywords", keywordsArray);
                        userImages.put("timestamp", FieldValue.serverTimestamp());
                        fStore.collection("users").document(userID).collection("images").add(userImages);

                        ref.child(UUID.randomUUID().toString()).putFile(imageSelected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "Photo uploaded");
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        databaseReference.push().setValue(uri.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Photo not uploaded successfully");
                                    }
                                });
                            }
                        });

                        Toast.makeText(addPhotos.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(addPhotos.this, PhotosActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(addPhotos.this, "Error uploading photo, please try again", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error uploading photo", e);
                }
            });
        } else {
            return;
        }
         */
    }
}
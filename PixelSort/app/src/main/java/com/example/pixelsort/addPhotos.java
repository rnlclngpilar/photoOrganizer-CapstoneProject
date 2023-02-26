package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class addPhotos extends AppCompatActivity {

    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    LinearLayout browsePhotos;
    LinearLayout removePhotos;
    public static TextView recyclerCount;
    LinearLayout uploadPhoto;
    RecyclerView recyclerViewPhoto;
    ImageView archives;
    ImageView profile;
    ProgressBar progressBar;
    String userID;
    private Uri imageSelected;
    uploadAdapter uploadAdapter;
    public static GridLayoutManager manager;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference dateReference;
    DatabaseReference addArchiveReference;
    FirebaseDatabase fDatabase;
    private StorageTask uploadImageTask;
    ArrayList<String> keywordsArray;
    ArrayList<String> confidenceArray;
    List<Image> monthPhotos = new ArrayList<>();
    ArrayList<Uri> imageSelectedList = new ArrayList<Uri>();
    Map<String, Object> userImages = new HashMap<>();
    Image image = new Image();
    private ImageLabeler imageLabeler;
    int imageQualityWidth;
    int imageQualityHeight;
    int counter;
    Boolean highQuality = false;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photos);

        photos = (LinearLayout) findViewById(R.id.photos);
        search = (LinearLayout) findViewById(R.id.search);
        albums = (LinearLayout) findViewById(R.id.albums);
        browsePhotos = (LinearLayout) findViewById(R.id.browsePhotos);
        removePhotos = (LinearLayout) findViewById(R.id.removePhotos);
        uploadPhoto = (LinearLayout) findViewById(R.id.uploadPhoto);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        archives = (ImageView) findViewById(R.id.archives);
        profile = (ImageView) findViewById(R.id.profile);
        recyclerViewPhoto = (RecyclerView) findViewById(R.id.recyclerViewPhoto);
        recyclerCount = (TextView) findViewById(R.id.recyclerCount);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("images/" + userID);
        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);
        addArchiveReference = FirebaseDatabase.getInstance().getReference("archives/" + userID);

        uploadAdapter = new uploadAdapter(addPhotos.this, imageSelectedList);
        recyclerViewPhoto.setAdapter(uploadAdapter);

        manager = new GridLayoutManager(addPhotos.this, 4);
        recyclerViewPhoto.setLayoutManager(manager);

        keywordsArray = new ArrayList<>();
        confidenceArray = new ArrayList<>();

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

        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addPhotos.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addPhotos.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        browsePhotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(intent, 3);
                //imageSelectedList.clear();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        removePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelectedList.clear();

                recyclerCount.setText("");

                uploadAdapter.setUpdatedAlbums(imageSelectedList);
                recyclerViewPhoto.setAdapter(uploadAdapter);
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadImageTask != null && uploadImageTask.isInProgress()) {
                    Toast.makeText(addPhotos.this, "Image upload is in progress", Toast.LENGTH_SHORT).show();
                } else {
                    counter = counter + 1;

                    if (imageSelectedList != null) {
                        for (int imageCount = 0; imageCount < imageSelectedList.size(); imageCount++) {
                            int finalImageCount = imageCount;
                            String imageId = UUID.randomUUID().toString();
                            Uri individualImage = imageSelectedList.get(imageCount);

                            StorageReference fileReference = storageReference.child(imageId + "." + getFileExtension(imageSelected));
                            uploadImageTask = fileReference.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 5000);

                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUrl = uri;
                                            String image_url = String.valueOf(downloadUrl);

                                            setImageMetadata(individualImage, imageId, image_url, downloadUrl);
                                        }
                                    });
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
                                    if (finalImageCount == imageSelectedList.size() - 1) {
                                        if (progress >= 100) {
                                            Intent intent = new Intent(addPhotos.this, PhotosActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int dataClip = data.getClipData().getItemCount();
                int currentImageSelected = 0;
                while (currentImageSelected < dataClip) {
                    imageSelected = data.getClipData().getItemAt(currentImageSelected).getUri();
                    imageSelectedList.add(imageSelected);
                    currentImageSelected = currentImageSelected + 1;
                }

                Toast.makeText(addPhotos.this, "You have selected " + imageSelectedList.size() + " images", Toast.LENGTH_SHORT).show();
            } else {
                imageSelected = data.getData();
                imageSelectedList.add(imageSelected);
            }

            uploadAdapter.setUpdatedAlbums(imageSelectedList);
            recyclerViewPhoto.setAdapter(uploadAdapter);
        }

        /*
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageSelected = data.getData();
            //viewPhoto.setImageURI(imageSelected);
            Picasso.get().load(imageSelected).into(viewPhoto);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageSelected);
                imageQualityWidth = bitmap.getWidth();
                imageQualityHeight = bitmap.getHeight();
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

                //viewPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

         */
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("viewingImages", imageSelectedList);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        imageSelectedList = savedInstanceState.getParcelableArrayList("viewingImages");

        uploadAdapter.setUpdatedAlbums(imageSelectedList);
        recyclerViewPhoto.setAdapter(uploadAdapter);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(Map<String, Object> userImages, Image image, String imageId) {
        if (highQuality) { // add to images
            databaseReference.child(imageId).setValue(image);
            fStore.collection("users").document(userID).collection("images").add(userImages);
            Toast.makeText(addPhotos.this, "Upload successful", Toast.LENGTH_SHORT).show();


        } else { //  add to archive
            Toast.makeText(addPhotos.this, "Low quality image has been sent to archives", Toast.LENGTH_SHORT).show();
            addArchiveReference.child(imageId).setValue(image);
            CollectionReference toPath = fStore.collection("users").document(userID).collection("archives");
            moveImageDocument(toPath, image);
        }
    }


    public void setImageMetadata(Uri individualImage, String imageId, String image_url, Uri downloadUrl){
        keywordsArray.clear();
        confidenceArray.clear();
        userImages.clear();
        image = new Image();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), individualImage);
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

            imageLabeler.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                @Override
                public void onSuccess(List<ImageLabel> imageLabels) {
                    if (imageLabels.size() > 0) {
                        StringBuilder builder = new StringBuilder();

                        for (ImageLabel label : imageLabels) {
                            builder.append(label.getText()).append("\n");
                            keywordsArray.add(builder.toString());
                            confidenceArray.add(builder + String.valueOf(label.getConfidence()));
                            builder.delete(0, builder.length());
                        }
                    }

                    if (bitmap.getWidth() < 900 && bitmap.getHeight() < 900) {
                        highQuality = false;
                    } else {
                        highQuality = true;
                    }

                    Calendar calendar = Calendar.getInstance();
                    String second = String.valueOf(calendar.get(Calendar.SECOND));
                    String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                    String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                    String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                    String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                    String year = String.valueOf(calendar.get(Calendar.YEAR));
                    String timeTag = year + month + day + hour + minute;
                    long timeTagInteger = Long.parseLong(timeTag);
                    long reverseTimeTagInteger = -timeTagInteger;

                    userImages = new HashMap<>();
                    userImages.put("image_id", imageId);
                    userImages.put("image_url", image_url);
                    userImages.put("keywords", keywordsArray);
                    userImages.put("confidence", confidenceArray);
                    userImages.put("day", day);
                    userImages.put("month", month);
                    userImages.put("year", year);
                    userImages.put("time_tag", timeTagInteger);
                    userImages.put("time_tag_reverse", reverseTimeTagInteger);
                    userImages.put("high_quality", highQuality);

                    image = new Image(downloadUrl.toString(), keywordsArray, confidenceArray, day, month, year, timeTagInteger, reverseTimeTagInteger, highQuality);
                    image.setImageId(imageId);
                    image.setKey(imageId);

                    Log.d(TAG, "Keywords: " + keywordsArray);
                    Log.d(TAG, "Confidence: " + confidenceArray);
                    Log.d(TAG, "USERIMAGE: " + userImages);
                    Log.d(TAG, "IMAGE: " + image);

                    // Upload to firebase
                    uploadImage(userImages, image, imageId);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveImageDocument(CollectionReference toPath, Image image) {
        String archiveID = UUID.randomUUID().toString();
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
        archive.put("timestamp", FieldValue.serverTimestamp());
        toPath.add(archive);
    }



}
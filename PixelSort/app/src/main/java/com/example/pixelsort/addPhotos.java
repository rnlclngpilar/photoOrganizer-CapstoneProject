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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    DatabaseReference addArchiveReference;
    FirebaseDatabase fDatabase;
    private StorageTask uploadImageTask;
    ArrayList<String> keywordsArray;
    private ImageLabeler imageLabeler;
    int imageQualityWidth;
    int imageQualityHeight;
    Boolean highQuality = false;
    private static final int PICK_IMAGE_REQUEST = 1;

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

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("images/" + userID);
        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);
        addArchiveReference = FirebaseDatabase.getInstance().getReference("archives/" + userID);

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
                //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(intent, 3);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadImageTask != null && uploadImageTask.isInProgress()) {
                    Toast.makeText(addPhotos.this, "Image upload is in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                    Intent intent = new Intent(addPhotos.this, PhotosActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        if (imageSelected != null) {
            String imageId = UUID.randomUUID().toString();
            StorageReference fileReference = storageReference.child(imageId + "." + getFileExtension(imageSelected));
            uploadImageTask = fileReference.putFile(imageSelected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;

                            String image_url = String.valueOf(downloadUrl);

                            if (imageQualityWidth < 900 && imageQualityHeight < 900) {
                                highQuality = false;
                            } else {
                                highQuality = true;
                            }

                            Calendar calendar = Calendar.getInstance();

                            String second = String.valueOf(calendar.get(Calendar.SECOND));
                            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                            String month = String.valueOf(calendar.get(Calendar.MONTH));
                            String year = String.valueOf(calendar.get(Calendar.YEAR));

                            String timeTag = year + month + day + hour + minute;
                            long timeTagInteger = Long.parseLong(timeTag);
                            long reverseTimeTagInteger = -timeTagInteger;

                            //int year = Integer.parseInt(yearString);

                            Map<String, Object> userImages = new HashMap<>();
                            userImages.put("image_id", imageId);
                            userImages.put("image_url", image_url);
                            userImages.put("keywords", keywordsArray);
                            userImages.put("day", day);
                            userImages.put("month", month);
                            userImages.put("year", year);
                            userImages.put("time_tag", timeTagInteger);
                            userImages.put("time_tag_reverse", reverseTimeTagInteger);
                            userImages.put("high_quality", highQuality);

                            if (PhotosActivity.qualityCheck.isChecked()) {
                                if (highQuality) {
                                    fStore.collection("users").document(userID).collection("images").add(userImages);
                                } else {
                                    fStore.collection("users").document(userID).collection("archives").add(userImages);
                                }
                            } else {
                                fStore.collection("users").document(userID).collection("images").add(userImages);
                            }

                            if (PhotosActivity.qualityCheck.isChecked()) {
                                if (highQuality) {
                                    Toast.makeText(addPhotos.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    Image image = new Image(downloadUrl.toString(), keywordsArray, day, month, year, timeTagInteger, reverseTimeTagInteger, highQuality);
                                    image.setImageId(imageId);
                                    image.setTimeTagInteger(timeTagInteger);
                                    String imageID = databaseReference.push().getKey();
                                    assert imageID != null;
                                    image.setKey(imageId);
                                    databaseReference.child(imageId).setValue(image);
                                } else {
                                    Toast.makeText(addPhotos.this, "Low quality image has been sent to archives", Toast.LENGTH_SHORT).show();
                                    Image image = new Image(downloadUrl.toString(), keywordsArray, day, month, year, timeTagInteger, reverseTimeTagInteger, highQuality);
                                    image.setImageId(imageId);
                                    image.setTimeTagInteger(timeTagInteger);
                                    String imageID = addArchiveReference.push().getKey();
                                    assert imageID != null;
                                    image.setKey(imageId);
                                    addArchiveReference.child(imageId).setValue(image);
                                }
                            } else {
                                Toast.makeText(addPhotos.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                Image image = new Image(downloadUrl.toString(), keywordsArray, day, month, year, timeTagInteger, reverseTimeTagInteger, highQuality);
                                image.setImageId(imageId);
                                image.setTimeTagInteger(timeTagInteger);
                                String imageID = databaseReference.push().getKey();
                                assert imageID != null;
                                image.setKey(imageId);
                                databaseReference.child(imageId).setValue(image);
                            }
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
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


}
package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    public static TextView uploadProgress;
    public static TextView uploadCount;

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
        uploadProgress = (TextView) findViewById(R.id.uploadProgress);
        uploadCount = (TextView) findViewById(R.id.uploadCount);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
//        fStore = FirebaseFirestore.getInstance();

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
                uploadProgress.setText("");

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
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    });

                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String image_url = String.valueOf(uri);
                                            setImageMetadata(individualImage, imageId, image_url);
                                        }
                                    });

                                    calculateQualityTask myTask = new calculateQualityTask(individualImage, imageId);
                                    myTask.execute();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(addPhotos.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    // Update the progress bar
                                    double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                                    progressBar.setProgress((int) progress);

                                    // Show the progress of upload
                                    if (progress >= 100){
                                        uploadCount.setText(finalImageCount + 1 +" / " + imageSelectedList.size() + " Uploaded");

                                        if (finalImageCount + 1 == imageSelectedList.size()) {
                                            Toast.makeText(addPhotos.this, "Upload successful. NOTE: Processing of a redundant image may require additional computational time." , Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(addPhotos.this, PhotosActivity.class);
                                            startActivity(intent);
                                        }
                                    }else{
                                        uploadProgress.setText("Progress " + String.format("%.1f", progress) + "%");
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
        Calendar calendar = Calendar.getInstance();
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        databaseReference.child(imageId).setValue(image);

        dateReference = FirebaseDatabase.getInstance().getReference("dates/" + userID);
        dateReference.child("allDays").child(year).child(month).child(day).child(imageId).setValue(image);

        DocumentReference fStore = FirebaseFirestore.getInstance().collection("users").document(userID).collection("images").document(imageId);
        fStore.set(userImages);


//        if (image.getHighQuality()) { // add to images
//
//        } else { //  add to archive
//            addArchiveReference.child(imageId).setValue(image);
//            CollectionReference toPath = fStore.collection("users").document(userID).collection("archives");
//            moveImageDocument(toPath, image);
//        }
    }

    private void moveImageDocument(CollectionReference toPath, Image image) {
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

    private void setImageMetadata(Uri individualImage, String imageId, String image_url){
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

                    image = new Image(
                            image_url,
                            keywordsArray, confidenceArray,
                            day, month, year,
                            timeTagInteger, reverseTimeTagInteger);
                    image.setImageId(imageId);
                    image.setKey(imageId);

                    Log.d(TAG, "Keywords: " + keywordsArray);
//                    Log.d(TAG, "Confidence: " + confidenceArray);
//                    Log.d(TAG, "USERIMAGE: " + userImages);
//                    Log.d(TAG, "IMAGE: " + image);

                    // Upload to firebase
                    uploadImage(userImages, image, imageId);

                    // Clear after upload
                    keywordsArray.clear();
                    confidenceArray.clear();
                    userImages.clear();
                    image = new Image();
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

    class calculateQualityTask extends AsyncTask<Void, Void, Double> {
        private Uri individualImage;
        private String imageID;

        public calculateQualityTask(Uri individualImage, String imageID) {
            this.individualImage = individualImage;
            this.imageID = imageID;
        }

        @Override
        protected Double doInBackground(Void... params) {
            // perform background operation here

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), individualImage);

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                int colorPixels = 0;
                int nonColorPixels = 0;
                int saturationSum = 0;
                int colorIntensitySum = 0;

                // Split the image into several vertical strips
                int numThreads = 4;
                int stripWidth = width / numThreads;
                List<ProcessStripThread> threads = new ArrayList<>();
                for (int i = 0; i < numThreads; i++) {
                    int startX = i * stripWidth;
                    int endX = (i == numThreads - 1) ? width : (i + 1) * stripWidth;
                    ProcessStripThread thread = new ProcessStripThread(bitmap, startX, endX, 0, height);
                    thread.start();
                    threads.add(thread);
                }

                // Wait for all threads to finish
                try {
                    for (ProcessStripThread thread : threads) {
                        thread.join();
                        colorPixels += thread.getColorPixels();
                        nonColorPixels += thread.getNonColorPixels();
                        saturationSum += thread.getSaturationSum();
                        colorIntensitySum += thread.getColorIntensitySum();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                double colorPercentage = ((double) colorPixels / (double) (colorPixels + nonColorPixels)) * 100;
                double averageSaturation = ((double) saturationSum / (double) colorPixels) / 255.0;
                int averageColorIntensity = (int) ((double) colorIntensitySum / (double) colorPixels);

                // Calculate the resolution of the image
                int resolution = width * height;

                double qualityScore = 0.0;
                if (colorPercentage > 95) {
                    // For colored images, the quality score is based on the average saturation and color intensity
                    qualityScore = (averageSaturation * 100.0) + (averageColorIntensity / 2.55);
                } else {
                    // For monochrome images, the quality score is based on the overall contrast and resolution
                    double contrast = ((double) (255 - nonColorPixels) / (double) (colorPixels + nonColorPixels));
                    qualityScore = (contrast * 100.0) + (resolution / 10000.0);
                }


                Log.d(TAG,"Quality Score = " + qualityScore + ", High Quality = " + highQuality);

                // If the quality score is above the threshold, the image is considered high quality
                // double qualityThreshold = 70.0;
                // return (qualityScore >= qualityThreshold);
                return qualityScore;


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Double result) {
            // update UI thread with result
            Calendar calendar = Calendar.getInstance();
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String year = String.valueOf(calendar.get(Calendar.YEAR));


            DocumentReference fStoreRef = FirebaseFirestore.getInstance().collection("users").document(userID).collection("images").document(imageID);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("images/" + userID).child(imageID);
            DatabaseReference dateRef = FirebaseDatabase.getInstance().getReference("dates/" + userID).child("allDays").child(year).child(month).child(day).child(imageID);


            double qualityThreshold = 70.0;
            highQuality = (result >= qualityThreshold) ? true : false;

            Map<String, Object> updates = new HashMap<>();
            updates.put("qualityScore", result);
            updates.put("highQuality", highQuality);

            fStoreRef.update(updates);
            dbRef.updateChildren(updates);
            dateRef.updateChildren(updates);
        }

    }

}

// Multi-Threading (used in getting imageQuality)
class ProcessStripThread extends Thread {
    private Bitmap bitmap;
    private int startX, endX, startY, endY;
    private int colorPixels, nonColorPixels, saturationSum, colorIntensitySum;

    public ProcessStripThread(Bitmap bitmap, int startX, int endX, int startY, int endY) {
        this.bitmap = bitmap;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    public int getColorPixels() {
        return colorPixels;
    }

    public int getNonColorPixels() {
        return nonColorPixels;
    }

    public int getSaturationSum() {
        return saturationSum;
    }

    public int getColorIntensitySum() {
        return colorIntensitySum;
    }

    @Override
    public void run() {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int pixel = bitmap.getPixel(x, y);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Check if the pixel is a color pixel or a non-color pixel
                int maxDiff = 20;
                if (Math.abs(red - green) < maxDiff &&
                        Math.abs(red - blue) < maxDiff &&
                        Math.abs(green - blue) < maxDiff) {
                    nonColorPixels++;
                } else {
                    colorPixels++;

                    // Calculate the saturation of the pixel
                    float[] hsv = new float[3];
                    Color.RGBToHSV(red, green, blue, hsv);
                    int saturation = Math.round(hsv[1] * 255.0f);
                    saturationSum += saturation;

                    // Calculate the color intensity of the pixel
                    int colorIntensity = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                    colorIntensitySum += colorIntensity;
                }
            }
        }
    }
}

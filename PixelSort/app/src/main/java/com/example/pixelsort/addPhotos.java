package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class addPhotos extends AppCompatActivity {

    LinearLayout photos;
    LinearLayout search;
    LinearLayout albums;
    LinearLayout browsePhotos;
    LinearLayout removePhotos;
    public static TextView recyclerCount;
    public static TextView uploadProgress;
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

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), individualImage);
                                highQuality = determineQuality(bitmap);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

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

                                }
                            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String image_url = String.valueOf(uri);
                                            setImageMetadata(individualImage, imageId, image_url);

                                            // Show the progress of upload
                                            double progress = 100.0 * task.getResult().getBytesTransferred() / task.getResult().getTotalByteCount();
                                            uploadProgress.setText(finalImageCount + 1 + " / " + imageSelectedList.size() + " Uploaded     " + String.format("%.1f", progress) + "% Done");
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
                                    // Update the progress bar
                                    double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                                    progressBar.setProgress((int) progress);

                                    // Show the progress of upload
                                    uploadProgress.setText(finalImageCount + 1 + " / " + imageSelectedList.size() + " Uploaded     " + String.format("%.1f", progress) + "% Done");

                                    if (finalImageCount + 1 == imageSelectedList.size() && progress >= 100) {
                                        Toast.makeText(addPhotos.this, "Upload successful. NOTE: low quality images will be sent to archives.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(addPhotos.this, PhotosActivity.class);
                                        startActivity(intent);
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
        if (image.getHighQuality()) { // add to images
            databaseReference.child(imageId).setValue(image);
            fStore.collection("users").document(userID).collection("images").add(userImages);

        } else { //  add to archive
            addArchiveReference.child(imageId).setValue(image);
            CollectionReference toPath = fStore.collection("users").document(userID).collection("archives");
            moveImageDocument(toPath, image);
        }
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

                    // Determine if image is High Quality
//                    ImageAssessment imgAssessment = getImageQualityAssessment(individualImage);
                    highQuality =  determineQuality(bitmap);

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

                    image = new Image(
                            image_url,
                            keywordsArray, confidenceArray,
                            day, month, year,
                            timeTagInteger, reverseTimeTagInteger,
                            highQuality);
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

    public boolean determineQuality(Bitmap bitmap) {
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

        Log.d(TAG,"Quality Score: " + qualityScore);

        // If the quality score is above the threshold, the image is considered high quality
        double qualityThreshold = 70.0;
        return (qualityScore >= qualityThreshold);
    }



    private ImageAssessment getImageQualityAssessment(Uri individualImage) {
        ImageAssessment imgAssessment = new ImageAssessment();

        try {
            Bitmap originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(individualImage));

            // Compression
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Bitmap compressedBitmap = BitmapFactory.decodeStream(inputStream);

            // Noise Addition
            Bitmap noisyBitmap = addNoiseToBitmap(compressedBitmap, 10);

            int width = imgAssessment.width = originalBitmap.getWidth();
            int height = imgAssessment.height = originalBitmap.getHeight();
            float density = this.getResources().getDisplayMetrics().density;
            long fileSizeInBytes = originalBitmap.getByteCount();

            imgAssessment.dpi =  (width * density / height);;
            imgAssessment.sizeKB = fileSizeInBytes / 1024.0;
            imgAssessment.sizeMB = fileSizeInBytes / (1024.0 * 1024.0);

//            imgAssessment.mse = getMSEorPSNR(originalBitmap, noisyBitmap, width, height, "mse");
//            imgAssessment.psnr = getMSEorPSNR(originalBitmap, noisyBitmap, width, height,"psnr");
//            imgAssessment.ssim = getSSIM(originalBitmap, noisyBitmap, width, height);


            Log.d(TAG, "dimension: " + height + " " + width);
            Log.d(TAG, "DPI: " + imgAssessment.dpi);
            Log.d(TAG, "sizeKB: " + imgAssessment.sizeKB);
            Log.d(TAG, "sizeMB: " + imgAssessment.sizeMB);

            Log.d(TAG, "MSE (lower is better): " + imgAssessment.mse);
            Log.d(TAG, "PSNR (HQ[30-50dB): " + imgAssessment.psnr);
            Log.d(TAG, "SSIM (HQ[>0.97]): " + imgAssessment.ssim);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return imgAssessment;
    }

    private static Bitmap addNoiseToBitmap(Bitmap bitmap, int noiseLevel) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap noisyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(noisyBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);

        int numPixels = width * height;
        int[] pixels = new int[numPixels];
        noisyBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        Random random = new Random();
        for (int i = 0; i < numPixels; i++) {
            if (random.nextInt(100) < noiseLevel) {
                int color = pixels[i];
                int alpha = Color.alpha(color);
                int red = Color.red(color) + random.nextInt(256) - 128;
                int green = Color.green(color) + random.nextInt(256) - 128;
                int blue = Color.blue(color) + random.nextInt(256) - 128;
                pixels[i] = Color.argb(alpha, red, green, blue);
            }
        }

        noisyBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return noisyBitmap;
    }

    private static double getMSEorPSNR(Bitmap originalImg, Bitmap alteredImg, int width, int height, String assessment){
        double mseR = 0, mseG = 0, mseB = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelOriginal = originalImg.getPixel(x, y);
                int pixelCompressed = alteredImg.getPixel(x, y);

                int rOriginal = (pixelOriginal >> 16) & 0xff;
                int gOriginal = (pixelOriginal >> 8) & 0xff;
                int bOriginal = pixelOriginal & 0xff;

                int rCompressed = (pixelCompressed >> 16) & 0xff;
                int gCompressed = (pixelCompressed >> 8) & 0xff;
                int bCompressed = pixelCompressed & 0xff;

                double mseRCurrent = Math.pow((rOriginal - rCompressed), 2);
                double mseGCurrent = Math.pow((gOriginal - gCompressed), 2);
                double mseBCurrent = Math.pow((bOriginal - bCompressed), 2);

                mseR += mseRCurrent;
                mseG += mseGCurrent;
                mseB += mseBCurrent;
            }
        }

        double mse = (mseR + mseG + mseB) / (3 * height * width);
        double psnr = 20 * Math.log10(255 / Math.sqrt(mse));

        if (assessment == "mse"){
            return mse;
        } else {
            return psnr;
        }

    }

    private static double getSSIM(Bitmap orginalImg, Bitmap alteredImg, int width, int height){
        // Compute SSIM
        // ----------------------------------------------------------------
        double[] m1 = computeMean(orginalImg, width, height);
        double[] m2 = computeMean(alteredImg, width, height);

        double[] var1 = computeVariance(orginalImg, m1, width, height);
        double[] var2 = computeVariance(alteredImg, m2, width, height);
        double[] covar = computeCovariance(orginalImg, alteredImg, m1, m2, width, height);

        double k1 = 0.01;
        double k2 = 0.03;
        double c1 = Math.pow((255 * k1), 2);
        double c2 = Math.pow((255 * k2), 2);

        double ssim = ((2 * m1[0] * m2[0] + c1) * (2 * covar[0] + c2)) /
                ((Math.pow(m1[0], 2) + Math.pow(m2[0], 2) + c1) *
                        (var1[0] + var2[0] + c2));
        return ssim;
    }


    private static double[] computeMean(Bitmap bmp, int width, int height) {
        double[] mean = new double[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bmp.getPixel(x, y);
                mean[0] += (double) ((pixel >> 16) & 0xff); // Red
                mean[1] += (double) ((pixel >> 8) & 0xff); // Green
                mean[2] += (double) (pixel & 0xff); // Blue
            }
        }
        double numPixels = (double) (width * height);
        mean[0] /= numPixels;
        mean[1] /= numPixels;
        mean[2] /= numPixels;
        return mean;
    }

    private static double[] computeVariance(Bitmap bmp, double[] mean, int width, int height) {
        double[] var = new double[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bmp.getPixel(x, y);
                var[0] += Math.pow(((pixel >> 16) & 0xff) - mean[0], 2); // Red
                var[1] += Math.pow(((pixel >> 8) & 0xff) - mean[1], 2); // Green
                var[2] += Math.pow((pixel & 0xff) - mean[2], 2); // Blue
            }
        }
        double numPixels = (double) (width * height);
        var[0] /= numPixels;
        var[1] /= numPixels;
        var[2] /= numPixels;
        return var;
    }

    private static double[] computeCovariance(Bitmap bmp1, Bitmap bmp2, double[] mean1, double[] mean2, int width, int height) {
        double[] cov = new double[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel1 = bmp1.getPixel(x, y);
                int pixel2 = bmp2.getPixel(x, y);
                cov[0] += (((pixel1 >> 16) & 0xff) - mean1[0]) * (((pixel2 >> 16) & 0xff) - mean2[0]); // Red
                cov[1] += (((pixel1 >> 8) & 0xff) - mean1[1]) * (((pixel2 >> 8) & 0xff) - mean2[1]); // Green
                cov[2] += ((pixel1 & 0xff) - mean1[2]) * ((pixel2 & 0xff) - mean2[2]); // Blue
            }
        }
        double numPixels = (double) (width * height);
        cov[0] /= numPixels;
        cov[1] /= numPixels;
        cov[2] /= numPixels;
        return cov;
    }

}

// Used for storing SSIM/MSE/PSNR/etc values
class ImageAssessment {
    double ssim;    // Structural Similarity Index (SSIM)
    double mse;     // Mean Squared Error (MSE)
    double psnr;    // Peak Signal to Noise Ratio (PSNR)
    double dpi;     // Dots Per Inch (DPI)

    int width;
    int height;

    double sizeKB;
    double sizeMB;

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

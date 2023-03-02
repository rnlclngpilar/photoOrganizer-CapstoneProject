package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class archiveAdapter extends RecyclerView.Adapter<archiveAdapter.ViewHolder>  {
    private Context context;
    private List<Image> archivePath = new ArrayList<>();
    private List<Image> selectedImage = new ArrayList<>();
    private List<Image> selectedImageOptions = new ArrayList<>();
    private OnItemClickListener mListener;
    Boolean selectActive = false;
    int counter = 0;
    Image imageSelected = new Image();

    String userID;
    String imageArchive;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference addArchiveReference;
    FirebaseDatabase fDatabase;
    FirebaseStorage firebaseStorage;

    public archiveAdapter(Context context, List<Image> archivePath) {
        this.context = context;
        this.archivePath = archivePath;
    }

    public void setUpdatedImages(List<Image> imagePath) {
        this.archivePath = imagePath;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public archiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new archiveAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull archiveAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image = archivePath.get(position);
        Glide.with(context).load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(holder.images);

        holder.timerArchive.bringToFront();
        holder.timerArchive.setVisibility(View.VISIBLE);

        holder.filterImage.setVisibility(View.GONE);
        holder.removeImage.setVisibility(View.GONE);

        ArchiveActivity.selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectActive = true;
                ArchiveActivity.selectPhotos.setBackgroundColor(Color.parseColor("#ECF0F1"));
                ArchiveActivity.selectPhotos.setTextColor(Color.parseColor("#000000"));
                ArchiveActivity.selectOptions.setVisibility(View.VISIBLE);
            }
        });

        ArchiveActivity.removeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectActive = false;
                counter = 0;

//                    holder.filterImage.setVisibility(View.GONE);
//                    holder.removeImage.setVisibility(View.GONE);

                ArchiveActivity.selectPhotos.setBackgroundColor(Color.parseColor("#34495e"));
                ArchiveActivity.selectPhotos.setTextColor(Color.parseColor("#ffffff"));
                ArchiveActivity.selectOptions.setVisibility(View.GONE);
                imageSelected.setSelected(false);
                mListener.showOptions(false, position);
                selectedImageOptions.clear();

                notifyDataSetChanged();
            }
        });

        holder.bind(image, holder);

//        if (archivePath.size() > 0) {
//            new CountDownTimer(4000 /*604800000*/, 1000) {
//                public void onTick(long millisUntilFinished) {
//                        if (archivePath.size() > 0) {
//                            long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
//                            millisUntilFinished -= TimeUnit.DAYS.toMillis(day);
//
//                            long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
//                            millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);
//
//                            long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
//                            millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);
//
//                            long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
//
//                            if (day >= 1) {
//                                holder.timerArchive.setText(day + " days");
//                            } else if (day < 1 && hour >= 1) {
//                                holder.timerArchive.setText(hour + " hours");
//                            } else if (hour < 1) {
//                                holder.timerArchive.setText(minute + " : " + second);
//                            }
//                        } else {
//                            cancel();
//                        }
//                }
//
//                public void onFinish() {
//                    // Delete the image from the database
//                    mAuth = FirebaseAuth.getInstance();
//                    fDatabase = FirebaseDatabase.getInstance();
//                    userID = mAuth.getCurrentUser().getUid();
//                    fStore = FirebaseFirestore.getInstance();
//
//                    Image image = archivePath.get(position);
//                    final String key = image.getKey();
//                    addArchiveReference = FirebaseDatabase.getInstance().getReference("archives/" + userID).child(key);
//                    fStore.collection("users")
//                            .document(userID)
//                            .collection("archives")
//                            .whereEqualTo("image_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                            imageArchive = document.getId();
//
//                                            fStore.collection("users")
//                                                    .document(userID)
//                                                    .collection("archives")
//                                                    .document(imageArchive)
//                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void unused) {
//                                                            addArchiveReference.removeValue();
//                                                        }
//                                                    });
//                                        }
//                                    }
//                                }
//                            });
//                }
//            }.start();
//        }
//        Picasso.get().load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).fit().centerCrop().into(holder.images);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectActive) {
                    imageSelected.setSelected(false);
                    if (holder.removeImage.getVisibility() == View.GONE) {
                        holder.filterImage.bringToFront();
                        holder.filterImage.setVisibility(View.VISIBLE);
                        holder.removeImage.bringToFront();
                        holder.removeImage.setVisibility(View.VISIBLE);
                        counter++;

                        selectedImageOptions.add(archivePath.get(holder.getAbsoluteAdapterPosition()));
                        imageSelected.setSelected(true);
                        if (counter > 0) {
                            mListener.showOptions(true, position);
                        }
                    } else if (holder.removeImage.getVisibility() == View.VISIBLE) {
                        holder.filterImage.bringToFront();
                        holder.filterImage.setVisibility(View.GONE);
                        holder.removeImage.bringToFront();
                        holder.removeImage.setVisibility(View.GONE);
                        counter--;

                        selectedImageOptions.remove(archivePath.get(holder.getAbsoluteAdapterPosition()));
                        imageSelected.setSelected(true);
                        if (counter <= 0) {
                            mListener.showOptions(false, position);
                        }
                    }
                } else {
                    Intent intent = new Intent(context, photosScaler.class);
                    intent.putExtra("imgPath", String.valueOf(archivePath.get(position).getImageURL()));
                    context.startActivity(intent);
                }
                //return true;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView images;
        ImageView removeImage;
        ImageView addImage;
        ImageView filterImage;
        TextView timerArchive;

        RecyclerView recyclerArchiveImages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
            removeImage = itemView.findViewById(R.id.removeImage);
            addImage = itemView.findViewById(R.id.addImage);
            filterImage = itemView.findViewById(R.id.filterImage);
            timerArchive = itemView.findViewById(R.id.timerArchive);
            recyclerArchiveImages = itemView.findViewById(R.id.recyclerArchiveImages);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onUnarchiveClick(position);
                    mListener.onDelete(position);
                }
            }
        }

        public void bind(Image image, archiveAdapter.ViewHolder holder) {
            // Delete the image from the database
            mAuth = FirebaseAuth.getInstance();
            fDatabase = FirebaseDatabase.getInstance();
            userID = mAuth.getCurrentUser().getUid();
            fStore = FirebaseFirestore.getInstance();

            String key = image.getKey();

            addArchiveReference = FirebaseDatabase.getInstance().getReference("archives/" + userID).child(key);
            fStore.collection("users")
                .document(userID)
                .collection("archives")
                .whereEqualTo("image_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    imageArchive = document.getId();
                                    String image_url = document.getString("image_url");
//                                        Log.d(TAG, "image URL: " + url);
//                                        Log.d(TAG, "image ID: " + imageArchive);

                                    Timestamp timestamp = document.getTimestamp("timestamp");
//                                    Log.d(TAG, "TIMESTAMP: " + timestamp);

                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(timestamp.toDate());
                                    long timeInMillis = cal.getTimeInMillis();
                                    long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

                                    long differenceInMillis = currentTimeInMillis - timeInMillis;
                                    long differenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis);
                                    long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

                                    long timeLeft = 30 - differenceInDays;

                                    holder.timerArchive.setText("" + timeLeft + " days");

                                    if(timeLeft <= 0) {
                                        // the timestamp is more than 30 days old
                                        Log.d(TAG, "Time Remaining (days): " + timeLeft);

                                        // Delete from Firestore Database
                                        fStore.collection("users")
                                                .document(userID)
                                                .collection("archives")
                                                .document(imageArchive)
                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "Deleted from database");
                                                        addArchiveReference.removeValue();  // Delete from Realtime Database
                                                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image_url);
                                                        imageRef.delete();
                                                    }
                                                });

                                    }else{
                                        // the timestamp is less than 30 days old
                                        Log.d(TAG, "Time Remaining (days): " + timeLeft);


                                    }
                                }

                            }
                        }

                    }
                });

        }
    }

    @Override
    public int getItemCount() {
        return archivePath.size();
    }

    public List<Image> getSelectedImg(){
        return selectedImage;
    }

    public List<Image> getSelectedImageOptions() { return selectedImageOptions; }


    public interface OnItemClickListener {
        void onUnarchiveClick(int position);
        void onDelete(int position);
        void showOptions(Boolean isSelected, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class archiveAdapter extends RecyclerView.Adapter<archiveAdapter.ViewHolder>  {
    private Context context;
    private List<Image> archivePath = new ArrayList<>();
    private List<Image> selectedImage = new ArrayList<>();
    private OnItemClickListener mListener;

    String userID;
    String imageArchive;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference addArchiveReference;
    FirebaseDatabase fDatabase;

    public archiveAdapter(Context context, List<Image> archivePath) {
        this.context = context;
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
        holder.filterImage.bringToFront();
        holder.filterImage.setVisibility(View.VISIBLE);
        holder.timerArchive.bringToFront();
        holder.timerArchive.setVisibility(View.VISIBLE);

        new CountDownTimer(20000 /*604800000*/, 1000) {
            public void onTick(long millisUntilFinished) {
                long day = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(day);

                long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hour);

                long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minute);

                long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                if (day >= 1) {
                    holder.timerArchive.setText(day + " days");
                } else if (day < 1 && hour >= 1) {
                    holder.timerArchive.setText(hour + " hours");
                } else if (hour < 1) {
                    holder.timerArchive.setText(minute + " : " + second);
                }
            }

            public void onFinish() {
                // Delete the image from the database
                mAuth = FirebaseAuth.getInstance();
                fDatabase = FirebaseDatabase.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                fStore = FirebaseFirestore.getInstance();

                Image image = archivePath.get(position);
                final String key = image.getKey();
                addArchiveReference = FirebaseDatabase.getInstance().getReference("archives/" + userID).child(key);
                fStore.collection("users")
                        .document(userID)
                        .collection("archives")
                        .whereEqualTo("image_id", key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        imageArchive = document.getId();

                                        fStore.collection("users")
                                                .document(userID)
                                                .collection("archives")
                                                .document(imageArchive)
                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        addArchiveReference.removeValue();
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            }
        }.start();
//        Picasso.get().load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).fit().centerCrop().into(holder.images);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, photosScaler.class);
                intent.putExtra("imgPath", String.valueOf(archivePath.get(position).getImageURL()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return archivePath.size();
    }

    public List<Image> getSelectedImg(){
        return selectedImage;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView images;
        ImageView removeImage;
        ImageView addImage;
        ImageView filterImage;
        TextView timerArchive;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
            removeImage = itemView.findViewById(R.id.removeImage);
            addImage = itemView.findViewById(R.id.addImage);
            filterImage = itemView.findViewById(R.id.filterImage);
            timerArchive = itemView.findViewById(R.id.timerArchive);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onDeleteClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

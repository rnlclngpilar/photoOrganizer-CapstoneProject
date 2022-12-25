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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class archiveAdapter extends RecyclerView.Adapter<archiveAdapter.ViewHolder>  {
    private Context context;
    private List<Image> archivePath = new ArrayList<>();
    private List<Image> selectedImage = new ArrayList<>();
    private OnItemClickListener mListener;

    String userID;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
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

        new CountDownTimer(200000, 1000) {
            public void onTick(long millisUntilFinished) {
                holder.timerArchive.setText("" + (int) millisUntilFinished);
            }

            public void onFinish() {
                // Delete the image from the database
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

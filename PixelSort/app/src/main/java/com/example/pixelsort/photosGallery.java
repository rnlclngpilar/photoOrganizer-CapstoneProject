package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class photosGallery extends RecyclerView.Adapter<photosGallery.ViewHolder>  {
    private Context context;
    private ArrayList<String> imagePath;
    String userID;
    private Uri imageSelected;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseDatabase fDatabase;

    public photosGallery(Context context, ArrayList<String> imagePath) {
        this.context = context;
        this.imagePath = imagePath;
    }

    public void setUpdatedImages(ArrayList<String> imagePath) {
        this.imagePath = imagePath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public photosGallery.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new photosGallery.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull photosGallery.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        Picasso.get().load(imagePath.get(position)).placeholder(R.drawable.ic_launcher_background).into(holder.images);
        Glide.with(context).load(imagePath.get(position)).placeholder(R.drawable.ic_launcher_background).into(holder.images);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (holder.removeImage.getVisibility() == View.GONE) {
                    holder.filterImage.bringToFront();
                    holder.filterImage.setVisibility(View.VISIBLE);
                    holder.removeImage.bringToFront();
                    holder.removeImage.setVisibility(View.VISIBLE);
                } else if (holder.removeImage.getVisibility() == View.VISIBLE) {
                    holder.filterImage.bringToFront();
                    holder.filterImage.setVisibility(View.GONE);
                    holder.removeImage.bringToFront();
                    holder.removeImage.setVisibility(View.GONE);
                }
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, photosDetail.class);
                intent.putExtra("imgPath", imagePath.get(holder.getAbsoluteAdapterPosition()));
                context.startActivity(intent);
            }
        });

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                storage = FirebaseStorage.getInstance();
                fDatabase = FirebaseDatabase.getInstance();
                storageReference = storage.getReference();
                userID = mAuth.getCurrentUser().getUid();

                databaseReference = fDatabase.getReference().child(userID).child("images");

                StorageReference ref = storageReference.child("images/" + userID);

                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Successfully delete image");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error deleting image");
                    }
                });

                imagePath.remove(imagePath.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        /*
        File imgFile = new File(imagePath.get(holder.getAbsoluteAdapterPosition()));

        if (imgFile.exists()) {
            Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.images);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, photosDetail.class);
                    intent.putExtra("imgPath", imagePath.get(holder.getAbsoluteAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
         */
    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView images;
        ImageView removeImage;
        ImageView filterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
            removeImage = itemView.findViewById(R.id.removeImage);
            filterImage = itemView.findViewById(R.id.filterImage);
        }
    }
}

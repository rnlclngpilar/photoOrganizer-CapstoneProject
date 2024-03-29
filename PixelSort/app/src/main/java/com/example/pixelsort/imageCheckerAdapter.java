package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class imageCheckerAdapter extends RecyclerView.Adapter<imageCheckerAdapter.ViewHolder> {

    private Context context;
    private List<Image> imageRedundancy;
    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;

    String userID;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private DatabaseReference dateReference;
    private DatabaseReference keywordReference;
    private DatabaseReference redundancyReference;
    private DatabaseReference addArchiveReference;

    public imageCheckerAdapter(PhotosActivity context, List<Image> imageRedundancy) {
        this.context = context;
        this.imageRedundancy = imageRedundancy;
    }

    @NonNull
    @Override
    public imageCheckerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new imageCheckerAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull imageCheckerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image = imageRedundancy.get(position);
        Glide.with(context).load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(holder.images);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        redundancyReference = FirebaseDatabase.getInstance().getReference("redundancy/" + userID);

        holder.removeImage.setVisibility(View.VISIBLE);
        holder.removeImage.bringToFront();

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageRedundancy.remove(imageRedundancy.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                String imageId = image.getImageId();
                redundancyReference.child(imageId).removeValue();

                Glide.with(context).load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(holder.images);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageRedundancy.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView images;
        ImageView removeImage;
        ImageView addImage;
        ImageView filterImage;
        TextView yearAdded;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            images = itemView.findViewById(R.id.images);
            removeImage = itemView.findViewById(R.id.removeImage);
            addImage = itemView.findViewById(R.id.addImage);
            filterImage = itemView.findViewById(R.id.filterImage);
            yearAdded = itemView.findViewById(R.id.yearAdded);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}

package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.ViewHolder>{
    private Context context;
    List<Image> searchedImages;
    List<Sorting> sortingObjectsPath = new ArrayList<>();
    private String origin;

    public searchAdapter(Context context, List<Image> searchedImages) {
        this.context = context;
        this.searchedImages = searchedImages;
    }

    public searchAdapter(Context context, List<Sorting> sortingObjectsPath, String origin) {
        this.context = context;
        this.sortingObjectsPath = sortingObjectsPath;
        this.origin = origin;
    }

    @NonNull
    @Override
    public searchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (origin == "Albums") {
            return new searchAdapter.ViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.album_item,parent,false)
            );
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull searchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (origin == "Albums") {
            Sorting sorting = sortingObjectsPath.get(position);
            Picasso.get().load(sorting.getThumbnail()).placeholder(R.drawable.ic_launcher_background).fit().centerCrop().into(holder.albumImage);

            holder.albumText.bringToFront();
            holder.albumText.setText(sorting.getKeyword());
        } else {
            Image image = searchedImages.get(position);
            Picasso.get().load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).fit().centerCrop().into(holder.images);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, photosScaler.class);
                    intent.putExtra("imgPath", String.valueOf(searchedImages.get(position).getImageURL()));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (origin == "Albums") {
            return sortingObjectsPath.size();
        } else {
            return searchedImages.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;
        TextView yearAdded;
        ImageView albumImage;
        TextView albumText;
        ImageView removeImage;
        ImageView filterImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            images = itemView.findViewById(R.id.images);
            yearAdded = itemView.findViewById(R.id.yearAdded);
            albumImage = itemView.findViewById(R.id.albumImage);
            albumText = itemView.findViewById(R.id.albumText);
            removeImage = itemView.findViewById(R.id.removeImage);
            filterImage = itemView.findViewById(R.id.filterImage);
        }
    }
}

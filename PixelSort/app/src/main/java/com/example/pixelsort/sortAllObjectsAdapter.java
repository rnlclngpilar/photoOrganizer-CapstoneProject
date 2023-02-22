package com.example.pixelsort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class sortAllObjectsAdapter extends RecyclerView.Adapter<sortAllObjectsAdapter.ViewHolder> {

    private Context context;
    private List<Image> sortingAllObjectsPath;

    public sortAllObjectsAdapter(PhotosActivity context, List<Image> sortingAllObjectsPath) {
        this.context = context;
        this.sortingAllObjectsPath = sortingAllObjectsPath;
    }

    public void setUpdatedAlbums( List<Image> sortingAllObjectsPath) {
        this.sortingAllObjectsPath = sortingAllObjectsPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public sortAllObjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new sortAllObjectsAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull sortAllObjectsAdapter.ViewHolder holder, int position) {
        Image sortingAllDay = sortingAllObjectsPath.get(position);
        Glide.with(context).load(sortingAllDay.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(holder.images);
    }

    @Override
    public int getItemCount() {
        return sortingAllObjectsPath.size();
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

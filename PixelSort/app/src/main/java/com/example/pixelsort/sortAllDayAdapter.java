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

import com.bumptech.glide.Glide;

import java.util.List;

public class sortAllDayAdapter extends RecyclerView.Adapter<sortAllDayAdapter.ViewHolder> {
    private Context context;
    private List<Image> sortingAllDayPath;
    //private sortAllDayAdapter.OnItemClickListener mListener;

    public sortAllDayAdapter(PhotosActivity context, List<Image> sortingAllDayPath) {
        this.context = context;
        this.sortingAllDayPath = sortingAllDayPath;
    }

    public void setUpdatedAlbums( List<Image> sortingAllDayPath) {
        this.sortingAllDayPath = sortingAllDayPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public sortAllDayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new sortAllDayAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull sortAllDayAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image sortingAllDay = sortingAllDayPath.get(position);
        Glide.with(context).load(sortingAllDay.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(holder.images);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, photosScaler.class);
                intent.putExtra("imgPath", String.valueOf(sortingAllDayPath.get(position).getImageURL()));
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return sortingAllDayPath.size();
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

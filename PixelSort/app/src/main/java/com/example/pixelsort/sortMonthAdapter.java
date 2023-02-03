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

public class sortMonthAdapter extends RecyclerView.Adapter<sortMonthAdapter.ViewHolder> {

    private Context context;
    private List<Sorting> sortingMonthPath;

    public sortMonthAdapter(PhotosActivity context, List<Sorting> sortingMonthPath) {
        this.context = context;
        this.sortingMonthPath = sortingMonthPath;
    }

    public void setUpdatedAlbums( List<Sorting> sortingMonthPath) {
        this.sortingMonthPath = sortingMonthPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public sortMonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new sortMonthAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.album_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull sortMonthAdapter.ViewHolder holder, int position) {
        Sorting sortingMonth = sortingMonthPath.get(position);
        Glide.with(context).load(sortingMonth.getThumbnail()).placeholder(R.drawable.ic_launcher_background).into(holder.albumImage);

        holder.albumText.bringToFront();
        holder.albumText.setText(sortingMonth.getMonth());
    }

    @Override
    public int getItemCount() {
        return sortingMonthPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView albumImage;
        TextView albumText;
        ImageView removeImage;
        ImageView filterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.albumImage);
            albumText = itemView.findViewById(R.id.albumText);
            removeImage = itemView.findViewById(R.id.removeImage);
            filterImage = itemView.findViewById(R.id.filterImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}

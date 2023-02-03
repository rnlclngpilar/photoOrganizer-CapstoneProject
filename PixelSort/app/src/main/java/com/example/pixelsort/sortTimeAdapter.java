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

import java.util.List;

public class sortTimeAdapter extends RecyclerView.Adapter<sortTimeAdapter.ViewHolder>{

    private Context context;
    private List<Sorting> sortingPath;
    private sortTimeAdapter.OnItemClickListener mListener;

    public sortTimeAdapter(PhotosActivity context, List<Sorting> sortingPath) {
        this.context = context;
        this.sortingPath = sortingPath;
    }

    public void setUpdatedAlbums( List<Sorting> sortingPath) {
        this.sortingPath = sortingPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public sortTimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new sortTimeAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.album_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull sortTimeAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Sorting sorting = sortingPath.get(position);
        Glide.with(context).load(sorting.getThumbnail()).placeholder(R.drawable.ic_launcher_background).into(holder.albumImage);

        holder.albumText.bringToFront();
        holder.albumText.setText(sorting.getYear());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onYearClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sortingPath.size();
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
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onYearClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onYearClick(int position);
    }

    public void setOnItemClickListener(sortTimeAdapter.OnItemClickListener listener) {mListener = listener;}
}

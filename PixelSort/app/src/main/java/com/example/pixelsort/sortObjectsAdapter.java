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

public class sortObjectsAdapter extends RecyclerView.Adapter<sortObjectsAdapter.ViewHolder> {

    private Context context;
    private List<Sorting> objectPath;
    private sortObjectsAdapter.OnItemClickListener mListener;

    public sortObjectsAdapter(PhotosActivity context, List<Sorting> objectPath) {
        this.context = context;
        this.objectPath = objectPath;
    }

    public void setUpdatedAlbums( List<Sorting> objectPath) {
        this.objectPath = objectPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public sortObjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new sortObjectsAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.album_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull sortObjectsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Sorting sorting = objectPath.get(position);
        Glide.with(context.getApplicationContext()).load(sorting.getThumbnail()).placeholder(R.drawable.ic_launcher_background).into(holder.albumImage);

        holder.albumText.bringToFront();
        holder.albumText.setText(sorting.getKeyword());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onObjectClick(position, sorting.getKeyword());
            }
        });
    }

    @Override
    public int getItemCount() {
        return objectPath.size();
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
                String keyword = String.valueOf(getAdapterPosition());
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onObjectClick(position, keyword);
                }
            }
        }

    }

    public interface OnItemClickListener {
        void onObjectClick(int position, String keyword);
    }

    public void setOnItemClickListener(sortObjectsAdapter.OnItemClickListener listener) {mListener = listener;}
}

package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class albumsAdapter extends RecyclerView.Adapter<albumsAdapter.ViewHolder> {
    private Context context;
    private List<Album> albumPath;
    private OnItemClickListener mListener;

    public albumsAdapter(AlbumsActivity context, List<Album> albumPath) {
        this.context = context;
        this.albumPath = albumPath;
    }

    public void setUpdatedAlbums( List<Album> albumPath) {
        this.albumPath = albumPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public albumsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.album_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull albumsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Album album = albumPath.get(position);
        Glide.with(context).load(album.getThumbnail()).placeholder(R.drawable.ic_launcher_background).into(holder.albumImage);

        holder.albumText.bringToFront();
        holder.albumText.setText(album.getAlbum_name());

//        Log.d(TAG, "STUFF " + album.getAlbumName() + " " + album.getThumbnail());
//        Toast.makeText(context, "" + album.getAlbumName(), Toast.LENGTH_SHORT).show();

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (holder.removeImage.getVisibility() == View.GONE) {
                    holder.removeImage.bringToFront();
                    holder.removeImage.setVisibility(View.VISIBLE);
                } else if (holder.removeImage.getVisibility() == View.VISIBLE) {
                    holder.removeImage.bringToFront();
                    holder.removeImage.setVisibility(View.GONE);
                }
                return true;

            }
        });

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDeleteClick(position);

                albumPath.remove(albumPath.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {return albumPath.size();}

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView albumImage;
        TextView albumText;
        ImageView removeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.albumImage);
            albumText = itemView.findViewById(R.id.albumText);
            removeImage = itemView.findViewById(R.id.removeImage);

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

    public void setOnItemClickListener(OnItemClickListener listener) {mListener = listener;}

}

package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

public class albumsAdapter extends RecyclerView.Adapter<albumsAdapter.ViewHolder> {
    private Context context;
    private List<Album> albumPath;
    private List<Album> selectedAlbum = new ArrayList<>();
    private OnItemClickListener mListener;

    Album albumSelected = new Album();

    Boolean selectActive = false;
    int counter = 0;

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

        holder.filterImage.setVisibility(View.GONE);
        holder.removeImage.setVisibility(View.GONE);

        AlbumsActivity.selectAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectActive = true;
                AlbumsActivity.selectAlbums.setBackgroundColor(Color.parseColor("#ECF0F1"));
                AlbumsActivity.selectAlbums.setTextColor(Color.parseColor("#000000"));
                //PhotosActivity.sortPhotos.setClickable(false);
                AlbumsActivity.createNewAlbum.setVisibility(View.GONE);
                AlbumsActivity.selectOptions.setVisibility(View.VISIBLE);
            }
        });

        AlbumsActivity.removeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectActive = false;
                counter = 0;

                AlbumsActivity.selectAlbums.setBackgroundColor(Color.parseColor("#34495e"));
                AlbumsActivity.selectAlbums.setTextColor(Color.parseColor("#ffffff"));
                //PhotosActivity.sortPhotos.setClickable(true);
                AlbumsActivity.selectOptions.setVisibility(View.GONE);
                AlbumsActivity.deleteOptions.setVisibility(View.GONE);
                AlbumsActivity.createNewAlbum.setVisibility(View.VISIBLE);
                albumSelected.setSelected(false);
                mListener.showOptions(false, position);
                selectedAlbum.clear();

                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectActive){
                    albumSelected.setSelected(false);
                    holder.filterImage.bringToFront();
                    holder.removeImage.bringToFront();

                    if (holder.removeImage.getVisibility() == View.GONE) {
                        holder.filterImage.setVisibility(View.VISIBLE);
                        holder.removeImage.setVisibility(View.VISIBLE);
                        counter++;

                        selectedAlbum.add(albumPath.get(holder.getAbsoluteAdapterPosition()));
                        albumSelected.setSelected(true);
                        if (counter > 0) {
                            mListener.showOptions(true, position);
                        }

                    } else if (holder.removeImage.getVisibility() == View.VISIBLE) {
                        holder.filterImage.setVisibility(View.GONE);
                        holder.removeImage.setVisibility(View.GONE);
                        counter--;

                        selectedAlbum.remove(albumPath.get(holder.getAbsoluteAdapterPosition()));
                        albumSelected.setSelected(true);
                        if (counter <= 0) {
                            mListener.showOptions(false, position);
                        }
                    }
                }else {
                    Intent intent = new Intent(context, albumCreate.class);
                    intent.putExtra("originAlbum", true);
                    intent.putExtra("albumName", album.getAlbum_name());
                    intent.putExtra("albumID", album.getAlbum_id());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {return albumPath.size();}

    public List<Album> getSelectedAlbums() {
        return selectedAlbum;
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
                    mListener.onDeleteClick(position);
                }
            }
        }

    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void showOptions(Boolean isSelected, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {mListener = listener;}

}

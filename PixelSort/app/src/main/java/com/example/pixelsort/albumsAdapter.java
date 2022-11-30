package com.example.pixelsort;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class albumsAdapter extends RecyclerView.Adapter<albumsAdapter.ViewHolder> {

    private Context context;
    private List<Image> albumPath;
    private String albumName;
    private String thumbnail;

    public albumsAdapter(Context context, List<Image> albumPath, String albumName, String thumbnail) {
        this.context = context;
        this.albumPath = albumPath;
        this.albumName = albumName;
        this.thumbnail = thumbnail;
    }

    public albumsAdapter(AlbumsActivity context, List<Image> albumPath) {
        this.context = context;
        this.albumPath = albumPath;
    }

    public void setUpdatedAlbums( List<Image> albumPath) {
        this.albumPath = albumPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.album_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Toast.makeText(context, albumPath.toString(), Toast.LENGTH_SHORT).show();

        Image album = albumPath.get(position);

        Glide.with(context).load(album.getThumbnail()).placeholder(R.drawable.ic_launcher_background).into(holder.albumImage);
        holder.albumText.setText(album.getAlbumName());

        Log.d(TAG, "Stuff " + album.getAlbumName() + " " + album.getThumbnail());
//        Toast.makeText(context, "" + album.getAlbumName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView albumImage;
        TextView albumText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.albumImage);
            albumText = itemView.findViewById(R.id.albumText);
        }
    }
}

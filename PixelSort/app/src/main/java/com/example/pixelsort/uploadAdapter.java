package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class uploadAdapter extends RecyclerView.Adapter<uploadAdapter.ViewHolder> {

    private Context context;
    private List<Uri> uploadAddImages;
    //private sortAllDayAdapter.OnItemClickListener mListener;

    public uploadAdapter(addPhotos context, List<Uri> uploadAddImages) {
        this.context = context;
        this.uploadAddImages = uploadAddImages;
    }

    public void setUpdatedAlbums( List<Uri> uploadAddImages) {
        this.uploadAddImages = uploadAddImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public uploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new uploadAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull uploadAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri uploadImages = uploadAddImages.get(position);
        Glide.with(context).load(uploadImages).placeholder(R.drawable.ic_launcher_background).into(holder.images);

        addPhotos.recyclerCount.setText(getItemCount() + " Images Selected");

//        addPhotos.uploadProgress.setText(0 + " / " + getItemCount() + " Uploaded     " + 0 + "% Done");
        addPhotos.uploadCount.setText(0 +" / " + getItemCount() + " Uploaded");
        addPhotos.uploadProgress.setText("Progress " + 0.0 + "%");

        holder.removeImage.setVisibility(View.VISIBLE);
        holder.removeImage.bringToFront();

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAddImages.remove(uploadAddImages.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

                Glide.with(context).load(uploadImages).placeholder(R.drawable.ic_launcher_background).into(holder.images);
                addPhotos.recyclerCount.setText(getItemCount() + " Images Selected");

                addPhotos.uploadCount.setText(0 +" / " + getItemCount() + " Uploaded");
                addPhotos.uploadProgress.setText("Progress " + 0.0 + "%");
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadAddImages.size();
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

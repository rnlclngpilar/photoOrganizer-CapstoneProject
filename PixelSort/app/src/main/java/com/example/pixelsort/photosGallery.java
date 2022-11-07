package com.example.pixelsort;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class photosGallery extends RecyclerView.Adapter<photosGallery.ViewHolder>  {
    private Context context;
    private ArrayList<String> imagePath;

    public photosGallery(Context context, ArrayList<String> imagePath) {
        this.context = context;
        this.imagePath = imagePath;
    }

    @NonNull
    @Override
    public photosGallery.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new photosGallery.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull photosGallery.ViewHolder holder, int position) {
        File imgFile = new File(imagePath.get(holder.getAbsoluteAdapterPosition()));

        if (imgFile.exists()) {
            Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.images);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, photosDetail.class);
                    intent.putExtra("imgPath", imagePath.get(holder.getAbsoluteAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView images;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
        }
    }
}

package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.ViewHolder>{
    private Context context;
    List<Image> searchedImages;

    public searchAdapter(Context context, List<Image> searchedImages) {
        this.context = context;
        this.searchedImages = searchedImages;
    }

    @NonNull
    @Override
    public searchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image = searchedImages.get(position);
        Picasso.get().load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).fit().centerCrop().into(holder.images);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, photosScaler.class);
                intent.putExtra("imgPath", String.valueOf(searchedImages.get(position).getImageURL()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchedImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            images = itemView.findViewById(R.id.images);
        }
    }
}

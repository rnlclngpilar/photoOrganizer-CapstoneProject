package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class photosAdapter extends RecyclerView.Adapter<photosAdapter.ViewHolder>  {
    private Context context;
    private String origin;
    private List<Image> imagePath = new ArrayList<>();
    private List<Album> albumPath = new ArrayList<>();
    private List<Image> selectedImage = new ArrayList<>();
    private List<Image> selectedImageOptions = new ArrayList<>();
    private OnItemClickListener mListener;
    Boolean selectClicked = false;
    Boolean selectActive = false;
    Image imageSelected = new Image();
    int counter = 0;

    String userID;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseDatabase fDatabase;

    public photosAdapter(Context context, List<Image> imagePath, String origin) {
        this.context = context;
        this.imagePath = imagePath;
        this.origin = origin;
    }

    public photosAdapter(albumCreate context, List<Album> albumPath, String origin) {
        this.context = context;
        this.albumPath = albumPath;
        this.origin = origin;
    }

    public void setUpdatedImages(List<Image> imagePath) {
        this.imagePath = imagePath;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public photosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new photosAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull photosAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image = imagePath.get(position);
        Glide.with(context).load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).into(holder.images);
//        Picasso.get().load(image.getImageURL()).placeholder(R.drawable.ic_launcher_background).fit().centerCrop().into(holder.images);


            PhotosActivity.selectPhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectActive = true;
                    PhotosActivity.selectPhotos.setBackgroundColor(Color.parseColor("#ECF0F1"));
                    PhotosActivity.selectPhotos.setTextColor(Color.parseColor("#000000"));
                }
            });

        PhotosActivity.removeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectActive = false;
                counter = 0;
                holder.filterImage.setVisibility(View.GONE);
                holder.removeImage.setVisibility(View.GONE);
                PhotosActivity.selectPhotos.setBackgroundColor(Color.parseColor("#34495e"));
                PhotosActivity.selectPhotos.setTextColor(Color.parseColor("#ffffff"));
                imageSelected.setSelected(false);
                mListener.showOptions(false, position);
            }
        });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!selectActive) {
                        Intent intent = new Intent(context, photosScaler.class);
                        intent.putExtra("imgPath", String.valueOf(imagePath.get(position).getImageURL()));
                        context.startActivity(intent);
                    }
                }
            });

            if (origin == "photos") {
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (selectActive) {
                                imageSelected.setSelected(false);
                                if (holder.removeImage.getVisibility() == View.GONE) {
                                    holder.filterImage.bringToFront();
                                    holder.filterImage.setVisibility(View.VISIBLE);
                                    holder.removeImage.bringToFront();
                                    holder.removeImage.setVisibility(View.VISIBLE);
                                    counter++;

                                    selectedImageOptions.add(imagePath.get(holder.getAbsoluteAdapterPosition()));
                                    imageSelected.setSelected(true);
                                    if (counter > 0) {
                                        mListener.showOptions(true, position);
                                    }
                                } else if (holder.removeImage.getVisibility() == View.VISIBLE) {
                                    holder.filterImage.bringToFront();
                                    holder.filterImage.setVisibility(View.GONE);
                                    holder.removeImage.bringToFront();
                                    holder.removeImage.setVisibility(View.GONE);
                                    counter--;

                                    selectedImageOptions.remove(imagePath.get(holder.getAbsoluteAdapterPosition()));
                                    imageSelected.setSelected(true);
                                    if (counter <= 0) {
                                        mListener.showOptions(false, position);
                                    }
                                }
                            }
                            return true;
                        }
                    });

                holder.removeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onDeleteClick(position);

                        imagePath.remove(imagePath.get(position));
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                });
            } else if (origin == "albumCreate") {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (holder.addImage.getVisibility() == View.GONE) {
                            holder.filterImage.bringToFront();
                            holder.filterImage.setVisibility(View.VISIBLE);
                            holder.addImage.bringToFront();
                            holder.addImage.setVisibility(View.VISIBLE);

                            selectedImage.add(imagePath.get(holder.getAbsoluteAdapterPosition()));
//                        Toast.makeText(context, selectedImage.toString(), Toast.LENGTH_SHORT).show();

                        } else if (holder.addImage.getVisibility() == View.VISIBLE) {
                            holder.filterImage.bringToFront();
                            holder.filterImage.setVisibility(View.GONE);
                            holder.addImage.bringToFront();
                            holder.addImage.setVisibility(View.GONE);

                            selectedImage.remove(imagePath.get(holder.getAbsoluteAdapterPosition()));

                        }
                        return true;
                    }

                });
            }
    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }

    public List<Image> getSelectedImg(){
        return selectedImage;
    }

    public List<Image> getSelectedImageOptions() { return selectedImageOptions; }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView images;
        ImageView removeImage;
        ImageView addImage;
        ImageView filterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
            removeImage = itemView.findViewById(R.id.removeImage);
            addImage = itemView.findViewById(R.id.addImage);
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

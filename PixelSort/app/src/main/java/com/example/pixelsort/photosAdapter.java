package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private List<Sorting> sortingYearPath = new ArrayList<>();
    private List<Image> selectedImageAlbums = new ArrayList<>();
    private List<Image> selectedImageOptions = new ArrayList<>();
    private OnItemClickListener mListener;
    Boolean selectClicked = false;
    Boolean selectActive = false;
    Image imageSelected = new Image();
    int counter = 0;
    int countImagesSelected = 0;

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

    public photosAdapter(PhotosActivity context, List<Sorting> sortingYearPath, String origin) {
        this.context = context;
        this.sortingYearPath = sortingYearPath;
        this.origin = origin;
    }

    public void setUpdatedImages(List<Image> imagePath) {
        this.imagePath = imagePath;
        this.notifyDataSetChanged();
    }

    public void setUpdatedYearImages(List<Sorting> sortingYearPath) {
        this.sortingYearPath = sortingYearPath;
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

        holder.filterImage.setVisibility(View.GONE);
        holder.removeImage.setVisibility(View.GONE);

        PhotosActivity.selectPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectActive = true;
                PhotosActivity.selectPhotos.setBackgroundColor(Color.parseColor("#ECF0F1"));
                PhotosActivity.selectPhotos.setTextColor(Color.parseColor("#000000"));
                //PhotosActivity.sortPhotos.setClickable(false);
                PhotosActivity.addPhoto.setVisibility(View.GONE);
                PhotosActivity.selectOptions.setVisibility(View.VISIBLE);
                PhotosActivity.imageOptions.setVisibility(View.GONE);

            }
        });

        PhotosActivity.removeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectActive = false;
                counter = 0;

                PhotosActivity.selectPhotos.setBackgroundColor(Color.parseColor("#34495e"));
                PhotosActivity.selectPhotos.setTextColor(Color.parseColor("#ffffff"));
                //PhotosActivity.sortPhotos.setClickable(true);
                countImagesSelected = 0;
                PhotosActivity.selectOptionsAmount.setText(countImagesSelected + " Item(s) selected");
                PhotosActivity.selectOptions.setVisibility(View.GONE);
                PhotosActivity.deleteOptions.setVisibility(View.GONE);
                PhotosActivity.addPhoto.setVisibility(View.VISIBLE);
                imageSelected.setSelected(false);
                mListener.showOptions(false, position);
                selectedImageOptions.clear();

                notifyDataSetChanged();
            }
        });

        if (origin == "photos") {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectActive) {
                        imageSelected.setSelected(false);
                        holder.filterImage.bringToFront();
                        holder.removeImage.bringToFront();

                        if (holder.removeImage.getVisibility() == View.GONE) {
                            holder.filterImage.setVisibility(View.VISIBLE);
                            holder.removeImage.setVisibility(View.VISIBLE);
                            counter++;
                            countImagesSelected++;
                            PhotosActivity.selectOptionsAmount.setText(countImagesSelected + " Item(s) selected");

                            selectedImageOptions.add(imagePath.get(holder.getAbsoluteAdapterPosition()));
                            imageSelected.setSelected(true);
                            if (counter > 0) {
                                mListener.showOptions(true, position);
                            }
                        } else if (holder.removeImage.getVisibility() == View.VISIBLE) {
                            holder.filterImage.setVisibility(View.GONE);
                            holder.removeImage.setVisibility(View.GONE);
                            counter--;
                            countImagesSelected--;
                            PhotosActivity.selectOptionsAmount.setText(countImagesSelected + " Item(s) selected");

                            selectedImageOptions.remove(imagePath.get(holder.getAbsoluteAdapterPosition()));
                            imageSelected.setSelected(true);
                            if (counter <= 0) {
                                PhotosActivity.removeSelection.callOnClick();
                                mListener.showOptions(false, position);
                            }
                        }
                    } else {
                        Intent intent = new Intent(context, photosScaler.class);
                        intent.putExtra("imgPath", String.valueOf(imagePath.get(position).getImageURL()));
                        context.startActivity(intent);
                    }
                    //return true;
                }
            });

        } else if (origin == "albumCreate") {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.filterImage.bringToFront();
                    holder.addImage.bringToFront();

                    if (holder.addImage.getVisibility() == View.GONE) {
                        holder.filterImage.setVisibility(View.VISIBLE);
                        holder.addImage.setVisibility(View.VISIBLE);

                        selectedImageAlbums.add(imagePath.get(holder.getAbsoluteAdapterPosition()));
//                        Toast.makeText(context, selectedImage.toString(), Toast.LENGTH_SHORT).show();

                    } else if (holder.addImage.getVisibility() == View.VISIBLE) {
                        holder.filterImage.setVisibility(View.GONE);
                        holder.addImage.setVisibility(View.GONE);

                        selectedImageAlbums.remove(imagePath.get(holder.getAbsoluteAdapterPosition()));
                    }
                }
            });

        } else if (origin == "albumView"){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, photosScaler.class);
                    intent.putExtra("imgPath", String.valueOf(imagePath.get(position).getImageURL()));
                    context.startActivity(intent);

                }
            });
        } else if (origin == "albumSelect"){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.filterImage.bringToFront();
                    holder.removeImage.bringToFront();

                    if (holder.removeImage.getVisibility() == View.GONE){
                        holder.filterImage.setVisibility(View.VISIBLE);
                        holder.removeImage.setVisibility(View.VISIBLE);

                        selectedImageAlbums.add(imagePath.get(holder.getAbsoluteAdapterPosition()));

                    }else if (holder.removeImage.getVisibility() == View.VISIBLE) {
                        holder.filterImage.setVisibility(View.GONE);
                        holder.removeImage.setVisibility(View.GONE);

                        selectedImageAlbums.remove(imagePath.get(holder.getAbsoluteAdapterPosition()));

                    }
                }
            });
        }else if (origin == "albumAddImages"){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.filterImage.bringToFront();
                    holder.addImage.bringToFront();

                    if (holder.addImage.getVisibility() == View.GONE) {
                        holder.filterImage.setVisibility(View.VISIBLE);
                        holder.addImage.setVisibility(View.VISIBLE);

                        selectedImageAlbums.add(imagePath.get(holder.getAbsoluteAdapterPosition()));

                    } else if (holder.addImage.getVisibility() == View.VISIBLE) {
                        holder.filterImage.setVisibility(View.GONE);
                        holder.addImage.setVisibility(View.GONE);

                        selectedImageAlbums.remove(imagePath.get(holder.getAbsoluteAdapterPosition()));
                    }

                }
            });
        }

        if (origin == "yearSorting") {
            holder.yearAdded.setVisibility(View.VISIBLE);
        } else {
            holder.yearAdded.setVisibility(View.GONE);
        }

        PhotosActivity.photosAmount.setVisibility(View.VISIBLE);
        PhotosActivity.photosAmount.setText(getItemCount() + " Images");
    }

    @Override
    public int getItemCount() {
        return imagePath.size();
    }

    public List<Image> getSelectedImg(){
        return selectedImageAlbums;
    }

    public List<Image> getSelectedImageOptions() { return selectedImageOptions; }

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
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onDeleteClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onArchiveClick(int position);
        void onDeleteClick(int position);
        void showOptions(Boolean isSelected, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

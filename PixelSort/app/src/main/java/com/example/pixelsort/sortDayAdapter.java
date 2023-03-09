package com.example.pixelsort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sortDayAdapter extends RecyclerView.Adapter<sortDayAdapter.ViewHolder> {
    private Context context;
    private List<Sorting> sortingDayPath;
    private sortDayAdapter.OnItemClickListener mListener;

    public sortDayAdapter(PhotosActivity context, List<Sorting> sortingDayPath) {
        this.context = context;
        this.sortingDayPath = sortingDayPath;
    }

    public void setUpdatedAlbums( List<Sorting> sortingDayPath) {
        this.sortingDayPath = sortingDayPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public sortDayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new sortDayAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.time_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull sortDayAdapter.ViewHolder holder, int position) {
        Sorting sortingDay = sortingDayPath.get(position);
        Glide.with(context.getApplicationContext()).load(sortingDay.getThumbnail()).placeholder(R.drawable.ic_launcher_background).into(holder.albumImage);

        holder.albumText.bringToFront();

        ArrayList<String> monthDates = new ArrayList<>(Arrays.asList("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"));

        holder.albumText.setText(sortingDay.getYear() + " " + monthDates.get(Integer.parseInt(sortingDay.getMonth()) - 1) + " " + sortingDay.getDay());
    }

    @Override
    public int getItemCount() {
        return sortingDayPath.size();
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
                    mListener.onDayClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onDayClick(int position);
    }

    public void setOnItemClickListener(sortDayAdapter.OnItemClickListener listener) {mListener = listener;}
}

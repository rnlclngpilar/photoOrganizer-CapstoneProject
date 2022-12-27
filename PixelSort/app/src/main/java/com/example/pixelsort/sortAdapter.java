package com.example.pixelsort;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class sortAdapter extends RecyclerView.Adapter<sortAdapter.ViewHolder>{
    private Context context;
    ArrayList<String> data;
    private DatabaseReference databaseReference;
    String userID;
    FirebaseAuth mAuth;
    FirebaseDatabase fDatabase;
    FirebaseFirestore fStore;
    private ValueEventListener valueEventListener;
    List<Image> NewestImagePath = new ArrayList<>();
    private OnItemClickListener mListener;

    public sortAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public sortAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new sortAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.sort_option_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull sortAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.sortOptionText.setText(data.get(position));

        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("images/" + userID);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    mListener.onSortNewest();
                } else if (position == 1) {
                    mListener.onSortOldest();
                } else if (position == 2) {
                    mListener.onSortDays();
                } else if (position == 3) {
                    mListener.onSortMonths();
                } else if (position == 4) {
                    mListener.onSortYears();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sortOptionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sortOptionText = itemView.findViewById(R.id.sortOptionText);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onSortNewest();
                    mListener.onSortOldest();
                    mListener.onSortDays();
                    mListener.onSortMonths();
                    mListener.onSortYears();
                }
            }
        }

    }

    public interface OnItemClickListener {
        void onSortNewest();
        void onSortOldest();
        void onSortDays();
        void onSortMonths();
        void onSortYears();
    }

    public void setOnItemClickListener(sortAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
}

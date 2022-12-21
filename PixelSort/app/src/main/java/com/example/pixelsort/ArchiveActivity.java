package com.example.pixelsort;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ArchiveActivity extends AppCompatActivity {

    ImageView profile;
    ImageView photos;
    ImageView search;
    ImageView albums;
    ImageView archives;
    ImageView archiveOptions;
    RecyclerView recyclerArchiveImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        profile = (ImageView) findViewById(R.id.profile);
        photos = (ImageView) findViewById(R.id.photos);
        search = (ImageView) findViewById(R.id.search);
        albums = (ImageView) findViewById(R.id.albums);
        archives = (ImageView) findViewById(R.id.archives);
        archiveOptions = (ImageView) findViewById(R.id.archiveOptions);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, PhotosActivity.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArchiveActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });
    }
}
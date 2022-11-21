package com.example.pixelsort;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;

public class photosDetail extends AppCompatActivity {

    String imgPath;
    ImageView imgView;
    ScaleGestureDetector scaleGestureDetector;

    private float mScaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_detail);

        imgPath = getIntent().getStringExtra("imgPath");
        imgView = findViewById(R.id.photoView);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        File imgFile = new File(imgPath);

        if (imgFile.exists()) {
            Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(imgView);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            imgView.setScaleX(mScaleFactor);
            imgView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
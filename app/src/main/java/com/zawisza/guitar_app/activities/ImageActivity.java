package com.zawisza.guitar_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.TouchImageView;

public class ImageActivity extends AppCompatActivity {

    TouchImageView imViewedImage;
    ImageView imageView;

    Functions functions = new Functions();

    Intent intent;
    Intent intent_to_change_activity;

    private final String TAG = "Rajd - ImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imViewedImage = findViewById(R.id.imageActivity_imageView);
        imageView = findViewById(R.id.imageActivity_imageView_close);

        intent = getIntent();

        functions.loadImageFromStorage(intent.getStringExtra("image"), imViewedImage, this.getApplicationContext(), intent.getStringExtra("dir"));

        imageView.setOnClickListener(v -> {
            if(intent.getStringExtra("activity").equals("UserActivity")){
                intent_to_change_activity = new Intent(this, UserActivity.class);
            }else{
                intent_to_change_activity = new Intent(this, AdminActivity.class);
            }

            if(intent.getStringExtra("dir").equals("routes")){
                intent_to_change_activity.putExtra("fragment", "Routes");
            }else{
                intent_to_change_activity.putExtra("fragment", "Content");
                intent_to_change_activity.putExtra("documentID", intent.getStringExtra("image"));
            }

            startActivity(intent_to_change_activity);
            this.finish();
        });
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");

        if(intent.getStringExtra("activity").equals("UserActivity")){
            intent_to_change_activity = new Intent(this, UserActivity.class);
        }else{
            intent_to_change_activity = new Intent(this, AdminActivity.class);
        }

        if(intent.getStringExtra("dir").equals("routes")){
            intent_to_change_activity.putExtra("fragment", "Routes");
        }else{
            intent_to_change_activity.putExtra("fragment", "Content");
            intent_to_change_activity.putExtra("documentID", intent.getStringExtra("image"));
        }

        startActivity(intent_to_change_activity);
        this.finish();
    }

}


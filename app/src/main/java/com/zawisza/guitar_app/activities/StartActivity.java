package com.zawisza.guitar_app.activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.auth.User;
import com.zawisza.guitar_app.R;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "Rajd - StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                if (isNetworkAvailable()) {
                    changeActivity();
                    t.cancel();
                }
            }

            },500,3000
        );

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void changeActivity(){
        startActivity(new Intent(this, UserActivity.class));
        this.finish();
    }


}
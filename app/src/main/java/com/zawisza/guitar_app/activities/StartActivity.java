package com.zawisza.guitar_app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zawisza.guitar_app.R;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "Guitar-Master - StartActivity";
    private static final String NAME = "permission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        //Check and request permission to record audio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        }

        //Check notification permission
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if(notificationManagerCompat.areNotificationsEnabled()){
            Log.d(TAG,"Permission Notification granted");
        }else{
            Log.d(TAG,"Permission Notification denied");

            SharedPreferences settings = getSharedPreferences(NAME, 0);
            int number = settings.getInt(NAME, -1);

            Log.d(TAG, "Number : " + number);

            SharedPreferences.Editor editor = settings.edit();

            if(number == 2){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Zezwolisz na powiadomienia?");
                builder.setPositiveButton("Oczywiście", (dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);

                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                    startActivity(intent);
                });
                builder.setNegativeButton("Może później", (dialogInterface, i) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                editor.putInt(NAME, 0);
            }else{
                Log.d(TAG, "Add 1 to number");
                number++;
                Log.d(TAG, "Number : " + number);
                editor.putInt(NAME, number);
            }
            editor.apply();
        }

        //Check if internet is online
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
        //Check if user was logged
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG,"User found. Changing to MainActivity");
            startActivity(new Intent(this, AdminActivity.class));
            this.finish();
        }else{
            Log.d(TAG,"User not found. Changing to UserActivity");
            startActivity(new Intent(this, UserActivity.class));
            this.finish();
        }
    }



}
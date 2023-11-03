package com.zawisza.guitar_app.activities;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.databinding.ActivityUserBinding;
import com.zawisza.guitar_app.fragments.Chords.ChordsFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.SoundMeter;
import com.zawisza.guitar_app.fragments.Metronome.MetronomeFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;

public class UserActivity extends BaseActivity {



    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Open StartActivity");

        userBinding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(userBinding.getRoot());

        bottomNavigationView = findViewById(R.id.nav_view);

        button_add = findViewById(R.id.button_add);
        button_login = findViewById(R.id.button_login);
        titleTextView = findViewById(R.id.activity_text);
        backTextView = findViewById(R.id.back_text);


        Variables.setButton_add(button_add);
        Variables.setButton_login(button_login);
        Variables.setTitleTextView(titleTextView);
        Variables.setBackTextView(backTextView);


        //Open SongBookFragment clicking notification
        Intent intentFromFragment = getIntent();
        if (intentFromFragment.getStringExtra("fragment") != null &&
                intentFromFragment.getStringExtra("fragment").equals("Content")) {
            replaceFragment(new SongbookFragment(), intentFromFragment.getStringExtra("documentID"), R.id.frameLayout);
            titleTextView.setText(getString(R.string.titleSongBook));
            backTextView.setText(getString(R.string.Empty));
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
        }

        listeners(R.id.frameLayout);

        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.androidNotification));

        checkNotification(R.id.frameLayout);

        // Create channel to show notifications.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.channelId);
            String channelName = getString(R.string.channelName);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setSound(notification, audioAttributes);
            channel.enableLights(true);
            channel.enableVibration(true);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        bindings();
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void bindings() {
        userBinding.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_guitarpick:
                    if(switch_number != 1) {
                        Log.d(TAG, "Change to GuitarPickFragment");
                        switch_number = 1;
                        replaceFragment(new GuitarPickFragment(), 1, R.id.frameLayout);
                    }
                    break;

                case R.id.nav_metronome:
                    if (switch_number != 2) {
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to MetronomeFragment");
                        if (switch_number != 1) {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 1, R.id.frameLayout);
                        } else {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 2, R.id.frameLayout);
                        }
                    }
                    break;

                case R.id.nav_songbook:
                    if (switch_number == 3) {
                        smoothBackToFirstItem(findViewById(R.id.rv));
                    }else{
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to SongBookFragment");
                        if (switch_number > 3 ) {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 1, R.id.frameLayout);
                        } else {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 2, R.id.frameLayout);
                        }
                    }
                    break;

                case R.id.nav_chords:
                    if (switch_number == 4) {
                        smoothBackToFirstItem(findViewById(R.id.rv));
                    }else{
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to ChordsFragment");
                        if (switch_number > 4 ) {
                            switch_number = 4;
                            replaceFragment(new ChordsFragment(), 1, R.id.frameLayout);
                        } else {
                            switch_number = 4;
                            replaceFragment(new ChordsFragment(), 2, R.id.frameLayout);
                        }
                    }
            }
            return true;
        });
    }
}
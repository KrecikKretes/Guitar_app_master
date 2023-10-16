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
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.databinding.ActivityUserBinding;
import com.zawisza.guitar_app.fragments.Content.ContentFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.SoundMeter;
import com.zawisza.guitar_app.fragments.Metronome.MetronomeFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;

public class UserActivity extends BaseActivity {



    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Open StartActivity");


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
        if(intentFromFragment.getStringExtra("fragment") != null &&
                intentFromFragment.getStringExtra("fragment").equals("Content")){
            replaceFragment(new SongbookFragment(), intentFromFragment.getStringExtra("documentID"), R.id.frameLayout);
            titleTextView.setText(getString(R.string.titleSongBook));
            backTextView.setText(getString(R.string.Empty));
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
        }

        listeners(R.id.frameLayout);

        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.androidNotification));

        checkNotification();

        // Create channel to show notifications.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId  =  getString(R.string.channelId);
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




    //Change image in back button


    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void bindings() {
        Functions functions = new Functions();
        userBinding.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_guitarpick:
                    if(switch_number != 1){
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to GuitarPickFragment");
                        switch_number = 1;
                        replaceFragment(new GuitarPickFragment(),1, R.id.frameLayout);
                    } else {
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_metronome:
                    if (switch_number != 2) {
                        if (switch_number != 1) {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 1, R.id.frameLayout);
                        } else {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 2, R.id.frameLayout);
                        }
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_songbook:
                    if (switch_number != 3) {
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to SongBookFragment");
                        if (switch_number > 3 ) {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 1, R.id.frameLayout);
                        } else {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 2, R.id.frameLayout);
                        }
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;
            }
            return true;
        });
    }

    @SuppressLint("SetTextI18n")
    private void checkNotification() {
        if(getIntent().getExtras() != null){
            Log.d(TAG,"Have Extras");
            outerloop:
            for(String key : getIntent().getExtras().keySet()){
                Log.d(TAG, "key=  "+key);
                if(key.equals("collection")){
                    Log.d(TAG,"Have collection");
                    Log.d(TAG, "Collection = "+getIntent().getExtras().getString("collection"));
                    for(String key2 : getIntent().getExtras().keySet()) {
                        if ((getIntent().getExtras().getString("collection")).equals(getString(R.string.collectionNotification))) {
                            if (key2.equals("documentID")) {
                                Log.d(TAG, "DocumentID found");
                                Log.d(TAG, "DocumentID = " + getIntent().getExtras().getString("documentID"));
                                Log.d(TAG, "Change to Content");
                                replaceFragment(new ContentFragment(), getIntent().getExtras().getString("documentID"), R.id.frameLayout);
                                titleTextView.setText("Ogłoszenie");
                                backTextView.setText("Ogłoszenia");
                                button_login.setBackgroundResource(R.drawable.back_icon);
                                break outerloop;
                            } else {
                                Log.d(TAG, "Not found documentID");
                            }
                        }
                    }
                }else{
                    Log.d(TAG,"Haven't collection");
                }
            }
        }else{
            Log.d(TAG,"Haven't Extras");
            replaceFragment(new GuitarPickFragment(),1, R.id.frameLayout);
        }
    }
}
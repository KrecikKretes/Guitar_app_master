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
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.databinding.ActivityUserBinding;
import com.zawisza.guitar_app.fragments.Content.ContentFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.SoundMeter;
import com.zawisza.guitar_app.fragments.Login.LoginFragment;
import com.zawisza.guitar_app.fragments.Metronome.MetronomeFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;

import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    ActivityUserBinding binding;
    private int switch_number = 1;
    private static final String TAG = "Guitar-Master - UserActivity";


    Button button_add;
    Button button_login;
    TextView titleTextView;
    TextView backTextView;

    BottomNavigationView bottomNavigationView;



    //Change Fragments or close app by clicking back buttons
    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed(){
        FragmentManager mgr = getSupportFragmentManager();
        if(mgr.getBackStackEntryCount() == 1 ||
                mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName() == null){
            super.onBackPressed();
            super.onBackPressed();
        }else{
            Log.d(TAG,mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName());
            switch (Objects.requireNonNull(
                    mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName())){
                case "Guitar-Master - GuitarPickFragment":
                    titleTextView.setText(getString(R.string.titleGuitarPick));
                    mgr.popBackStack();
                    break;
                case "Guitar-Master - MetronomeFragment":
                    titleTextView.setText(R.string.titleMetronome);
                    mgr.popBackStack();
                    break;
                case "Guitar-Master - SongbookFragment":
                    titleTextView.setText(R.string.titleSongBook);
                    mgr.popBackStack();
                    break;
                default:
                    break;
            }
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
            backTextView.setText(getString(R.string.Empty));

        }
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Open StartActivity");


        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
            replaceFragment(new SongbookFragment(), intentFromFragment.getStringExtra("documentID"));
            titleTextView.setText(getString(R.string.titleSongBook));
            backTextView.setText(getString(R.string.Empty));
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
        }

        listeners();

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


    //Change Fragment by click back button
    @SuppressLint("SetTextI18n")
    private void backToFragment(){
        if(backTextView.getText().equals(getString(R.string.Empty))){
            titleTextView.setText(getString(R.string.titleLogin));
            switch(switch_number){
                case 1:
                    backTextView.setText(getString(R.string.titleGuitarPick));
                    break;
                case 2:
                    backTextView.setText(getString(R.string.titleMetronome));
                    break;
                case 3:
                    backTextView.setText(getString(R.string.titleSongBook));
                    break;
            }
            switch_number = 0;
            replaceFragment(new LoginFragment(),2);
        }else{
            switch(switch_number){
                case 1:
                    replaceFragment(new GuitarPickFragment(), 2);
                    break;
                case 2:
                    replaceFragment(new MetronomeFragment(), 2);
                    break;
                case 3:
                    replaceFragment(new SongbookFragment(), 2);
                    break;
            }
            backTextView.setText(getString(R.string.Empty));
        }
    }

    @SuppressLint("SetTextI18n")
    private void listeners() {
        button_login.setOnClickListener(view -> {
            backToFragment();
            changeSetting(button_login);
        });

        backTextView.setOnClickListener(view -> {
            backToFragment();
            changeSetting(button_login);
        });
    }

    //Change image in back button
    private void changeSetting(Button button){
        if(switch_number == 0){
            button.setBackgroundResource(R.drawable.back_icon);
        }else{
            button.setBackgroundResource(R.drawable.person_profile_image_icon);
        }
        button.setScaleX(0.5f);
        button.setScaleY(0.5f);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void bindings() {
        Functions functions = new Functions();
        binding.navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_guitarpick:
                    if(switch_number != 1 ||  Variables.isIsContent()){
                        Log.d(TAG,"Change to GuitarPickFragment");
                        switch_number = 1;
                        Variables.setIsContent(false);
                        titleTextView.setText(getString(R.string.titleGuitarPick));
                        backTextView.setText(getString(R.string.Empty));
                        replaceFragment(new GuitarPickFragment(),1);
                    } else {
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_metronome:

                    if (switch_number != 2 || Variables.isIsContent()) {
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to MetronomeFragment");
                        if (switch_number != 1) {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 1);
                        } else {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 2);
                        }
                        Variables.setIsContent(false);
                        titleTextView.setText(getString(R.string.titleMetronome));
                        backTextView.setText(getString(R.string.Empty));
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_songbook:

                    if (switch_number != 3 || Variables.isIsContent()) {
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to SongBookFragment");
                        if (switch_number > 3 ) {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 1);
                        } else {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 2);
                        }
                        Variables.setIsContent(false);
                        titleTextView.setText(getString(R.string.titleSongBook));
                        backTextView.setText(getString(R.string.Empty));
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
                                replaceFragment(new ContentFragment(), getIntent().getExtras().getString("documentID"));
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
            replaceFragment(new GuitarPickFragment(),1);
        }
    }

    private void replaceFragment(Fragment fragment, int direction){
        String TAG = null;

        switch(switch_number){
            case 0:
                TAG = "Guitar-Master - LoginFragment";
                break;
            case 1:
                TAG = "Guitar-Master - GuitarPickFragment";
                titleTextView.setText(getString(R.string.titleGuitarPick));
                break;
            case 2:
                TAG = "Guitar-Master - MetronomeFragment";
                titleTextView.setText(getString(R.string.titleMetronome));

                break;
            case 3:
                TAG = "Guitar-Master - SongBookFragment";
                titleTextView.setText(getString(R.string.titleSongBook));
                break;
        }

        if(direction == 1){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .addToBackStack(TAG)
                    .replace(R.id.frameLayout, fragment, TAG)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                    .addToBackStack(TAG)
                    .replace(R.id.frameLayout, fragment, TAG)
                    .commit();
        }
    }

    private void replaceFragment(Fragment fragment, String id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("documentID", id);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack("Guitar-Master - ContentFragment");
        fragmentTransaction.commit();
    }
}
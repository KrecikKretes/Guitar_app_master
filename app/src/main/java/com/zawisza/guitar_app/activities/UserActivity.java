package com.zawisza.guitar_app.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.Images;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.databinding.ActivityUserBinding;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.Content.ContentFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.SoundMeter;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;
import com.zawisza.guitar_app.fragments.Metronome.MetronomeFragment;
import com.zawisza.guitar_app.fragments.Login.LoginFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    ActivityUserBinding binding;
    private int switch_number = 1;

    Button button_add;
    Button button_login;
    TextView titleTextView;
    TextView backTextView;
    Boolean isLoginFragment = false;

    private static final String TAG = "Rajd - UserActivity";

    private Images images = new Images();

    BottomNavigationView bottomNavigationView;

    private final String NAME = "permission";

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG,"User found. Changing to Main Activity");
            Intent intent = new Intent(UserActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed(){
        FragmentManager mgr = getSupportFragmentManager();
        if(mgr.getBackStackEntryCount() == 1 || mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName() == null){
            super.onBackPressed();
            super.onBackPressed();
        }else{
            Log.d(TAG,mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName());
            switch (Objects.requireNonNull(mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName())){
                case "Rajd - ActivFragment":
                    titleTextView.setText("Ogłoszenia");
                    mgr.popBackStack();
                    break;
                case "Rajd - LinksFragment":
                    titleTextView.setText("Zapisy");
                    mgr.popBackStack();
                    break;
                case "Rajd - FAQFragment":
                    titleTextView.setText("FAQ");
                    mgr.popBackStack();
                    break;
                case "Rajd - CalendarFragment":
                    titleTextView.setText("Harmonogram");
                    mgr.popBackStack();
                    break;
                case "Rajd - RoutesFragment":
                    titleTextView.setText("Trasy");
                    mgr.popBackStack();
                    break;
                default:
                    break;
            }
            button_login.setBackgroundResource(Variables.getIcon_user());
            backTextView.setText("");

        }
    }


    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Open Start Activity");

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        boolean isNotificationEnabled = notificationManagerCompat.areNotificationsEnabled();

        if(!isNotificationEnabled){
            Log.d(TAG,"Permission denied");

            SharedPreferences settings = getSharedPreferences(NAME, 0);
            int number = settings.getInt(NAME, -1);


            Log.d(TAG, "Number : " + number);

            SharedPreferences.Editor editor = settings.edit();

            if(number == 2){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Zezwolisz na powiadomienia?");
                //builder.setMessage("");
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
            editor.commit();

        }else{
            Log.d(TAG,"Permission granted");
        }



        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        button_add = findViewById(R.id.button_add);
        button_login = findViewById(R.id.button_login);
        titleTextView = findViewById(R.id.activity_text);
        backTextView = findViewById(R.id.back_text);

        bottomNavigationView = findViewById(R.id.nav_view);

        Variables.setButton_add(button_add);
        Variables.setButton_login(button_login);
        Variables.setTitleTextView(titleTextView);
        Variables.setBackTextView(backTextView);

        Intent intentFromFragment = getIntent();
        if(intentFromFragment.getStringExtra("fragment") != null && intentFromFragment.getStringExtra("fragment").equals("Content")){
            replaceFragment(new ContentFragment(), intentFromFragment.getStringExtra("documentID"));
            titleTextView.setText("Ogłoszenie");
            backTextView.setText("Ogłoszenia");
            button_login.setBackgroundResource(Variables.getIcon_back());
        }




        listeners();

        FirebaseMessaging.getInstance().subscribeToTopic(Variables.getTopic_to_android());

        checkNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "Rajd 2023";
            String channelName = "Fcm notifications";
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

    @SuppressLint("SetTextI18n")
    private void backToActivity(){
        if(isLoginFragment){
            switch(switch_number){
                case 1:
                    titleTextView.setText("Ogłoszenia");
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                            .replace(R.id.frameLayout, new GuitarPickFragment(), "Rajd - ActivFragment")
                            .commit();
                    break;

                case 2:
                    titleTextView.setText("Zapisy");
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                            .replace(R.id.frameLayout, new MetronomeFragment(), "Rajd - LinksFragment")
                            .commit();
                    break;
                case 3:
                    titleTextView.setText("FAQ");
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                            .replace(R.id.frameLayout, new SongbookFragment(),"Rajd - FAQFragment")
                            .commit();
                    break;
            }
            backTextView.setText("");
            isLoginFragment = false;
        }else{
            titleTextView.setText("Logowanie");

            switch(switch_number){
                case 1:
                    backTextView.setText("Ogłoszenia");
                    break;
                case 2:
                    backTextView.setText("Zapisy");
                    break;
                case 3:
                    backTextView.setText("FAQ");
                    break;
                case 4:
                    backTextView.setText("Harmonogram");
                    break;
                case 5:
                    backTextView.setText("Trasy");
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .addToBackStack("Rajd - LoginFragment")
                    .replace(R.id.frameLayout, new LoginFragment())
                    .commit();
            isLoginFragment = true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void listeners() {
        button_add.setVisibility(View.INVISIBLE);
        button_login.setOnClickListener(view -> {
            changeSetting(button_login);
            backToActivity();
        });

        backTextView.setOnClickListener(view -> {
            changeSetting(button_login);
            backToActivity();
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Images> imagesArrayList = new ArrayList<>();

    }

    private void changeSetting(Button button){

        if(isLoginFragment){
            button.setBackgroundResource(Variables.getIcon_user());
        }else{
            button.setBackgroundResource(Variables.getIcon_back());
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
                    if(switch_number != 1 || isLoginFragment || Variables.isIsContent()){
                        Log.d(TAG,"Change to ActivFragment");
                        switch_number = 1;
                        backTextView.setText("");
                        titleTextView.setText("Ogłoszenia");
                        Variables.setIsContent(false);
                        replaceFragment(new GuitarPickFragment(),1);
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_metronome:

                    if (switch_number != 2 || isLoginFragment || Variables.isIsContent()) {
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to LinksFragment");
                        if (switch_number != 1) {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 1);
                        }
                        else {
                            switch_number = 2;
                            backTextView.setText("");
                            replaceFragment(new MetronomeFragment(), 2);
                        }
                        Variables.setIsContent(false);
                        titleTextView.setText("Zapisy");
                        backTextView.setText("");
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_songbook:

                    if (switch_number != 3 || isLoginFragment || Variables.isIsContent()) {
                        if (switch_number > 3 ) {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 1);
                        }
                        else {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 2);
                        }
                        Variables.setIsContent(false);
                        titleTextView.setText("SongBook");
                        backTextView.setText("");
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;
            }
            isLoginFragment = true;
            changeSetting(button_login);
            isLoginFragment = false;
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
                        if ((getIntent().getExtras().getString("collection")).equals("Announcements")) {
                            if (key2.equals("documentID")) {
                                Log.d(TAG, "DocumentID found");
                                Log.d(TAG, "DocumentID = " + getIntent().getExtras().getString("documentID"));
                                Log.d(TAG, "Change to Content");
                                replaceFragment(new ContentFragment(), getIntent().getExtras().getString("documentID"));
                                titleTextView.setText("Ogłoszenie");
                                backTextView.setText("Ogłoszenia");
                                button_login.setBackgroundResource(Variables.getIcon_back());
                                break outerloop;
                            } else {
                                Log.d(TAG, "Not found documentID");
                            }
                        } else {
                            if ((getIntent().getExtras().getString("collection")).equals("Enrollments")) {
                                Log.d(TAG, "Change to Links");
                                titleTextView.setText("Zapis");
                                backTextView.setText("Zapisy");
                                button_login.setBackgroundResource(Variables.getIcon_back());
                                replaceFragment(new MetronomeFragment(),1);
                                break outerloop;
                            } else {
                                Log.d(TAG, "Bad collection");
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
            case 1:
                TAG = "Rajd - ActivFragment";
                break;
            case 2:
                TAG = "Rajd - LinksFragment";
                break;
            case 3:
                TAG = "Rajd - FAQFragment";
                break;
            case 4:
                TAG = "Rajd - CalendarFragment";
                break;
            case 5:
                TAG = "Rajd - RoutesFragment";
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
        fragmentTransaction.addToBackStack("Rajd - ContentFragment");
        fragmentTransaction.commit();
    }
}
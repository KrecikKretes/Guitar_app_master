package com.zawisza.guitar_app.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.databinding.ActivityAdminBinding;
import com.zawisza.guitar_app.fragments.Content.ContentFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.SoundMeter;
import com.zawisza.guitar_app.fragments.Login.LogoutFragment;
import com.zawisza.guitar_app.fragments.Metronome.MetronomeFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;


public class AdminActivity extends BaseActivity{

    public static Context contextOfApplication;
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    /*
    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed(){
        FragmentManager mgr = getSupportFragmentManager();
        if(mgr.getBackStackEntryCount() == 1 ||
                mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2 ).getName() == null){
            super.onBackPressed();
            super.onBackPressed();
        }else{
            Log.d(TAG,mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName());
            switch (Objects.requireNonNull(
                    mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName())){
                case "Guitar-Master - GuitarPickFragment":
                    titleTextView.setText(getString(R.string.titleGuitarPick));
                    button_add.setVisibility(View.INVISIBLE);
                    mgr.popBackStack();
                    break;
                case "Guitar-Master - MetronomeFragment":
                    titleTextView.setText(getString(R.string.titleMetronome));
                    button_add.setVisibility(View.INVISIBLE);
                    mgr.popBackStack();
                    break;
                case "Guitar-Master - SongBookFragment":
                    titleTextView.setText(getString(R.string.titleSongBook));
                    button_add.setVisibility(View.VISIBLE);
                    mgr.popBackStack();
                    break;
                default:
                    break;
            }
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
            backTextView.setText(getString(R.string.Empty));
        }
    }

     */

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"Open Admin Activity");

        contextOfApplication = getApplicationContext();

        adminBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(adminBinding.getRoot());

        button_add = findViewById(R.id.button_add);
        button_login = findViewById(R.id.button_login);
        titleTextView = findViewById(R.id.activity_text);
        backTextView = findViewById(R.id.back_text);

        replaceFragment(new GuitarPickFragment(), 1);

        Variables.setButton_add(button_add);
        Variables.setButton_login(button_login);
        Variables.setTitleTextView(titleTextView);
        Variables.setBackTextView(backTextView);

        Intent intentFromFragment = getIntent();
        if(intentFromFragment.getStringExtra("fragment") != null && intentFromFragment.getStringExtra("fragment").equals("Content")){
            replaceFragment(new ContentFragment(), intentFromFragment.getStringExtra("documentID"), R.id.frameLayout_login);
            titleTextView.setText(getString(R.string.titleSongBook));
            backTextView.setText(getString(R.string.Empty));
            button_add.setVisibility(View.VISIBLE);
            button_login.setBackgroundResource(R.drawable.back_icon);
        }


        listeners(R.id.frameLayout_login);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.androidNotification));

        checkNotification();


        bindings();
    }


    @SuppressLint("SetTextI18n")
    private void backToFragment(){
        button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
        backTextView.setText(getString(R.string.Empty));
        switch(switch_number){
            case 0:
                button_login.setBackgroundResource(R.drawable.back_icon);
                titleTextView.setText("Wylogowanie");
                button_add.setVisibility(View.INVISIBLE);

                changeBackText();

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                        .addToBackStack("Rajd - LogoutFragment")
                        .replace(R.id.frameLayout_login, new LogoutFragment())
                        .commit();
                break;
            case 1:
                replaceFragment(new GuitarPickFragment(), 2);
                button_add.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        .replace(R.id.frameLayout_login, new GuitarPickFragment())
                        .commit();
                break;

            case 2:
                titleTextView.setText("Zapisy");
                button_add.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        .replace(R.id.frameLayout_login, new MetronomeFragment())
                        .commit();
                break;
            case 3:
                titleTextView.setText("FAQ");
                button_add.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                        .replace(R.id.frameLayout_login, new SongbookFragment())
                        .commit();
                break;
            }
    }

    @SuppressLint("SetTextI18n")
    private void changeBackText(){
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
    }



    //Change image in back button



    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void bindings() {
        Functions functions = new Functions();
        adminBinding.navViewLogin.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_guitarpick:
                    if(switch_number != 1){
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to GuitarPickFragment");
                        switch_number = 1;
                        replaceFragment(new GuitarPickFragment(),1);
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_metronome:
                    if(switch_number != 2){
                        if (switch_number == 1) {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 2);
                        }
                        else {
                            switch_number = 2;
                            replaceFragment(new MetronomeFragment(), 1);
                        }
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_songbook:
                    if(switch_number != 3){
                        SoundMeter.t1.cancel();
                        Log.d(TAG,"Change to SongBookFragment");
                        if (switch_number < 3 ) {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 2);
                        }
                        else {
                            switch_number = 3;
                            replaceFragment(new SongbookFragment(), 1);
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
        String id;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(this, UserActivity.class);
            if(getIntent().getExtras() != null) {
                outerloop:
                for(String key : getIntent().getExtras().keySet()){
                    Log.d(TAG, "");
                    Log.d(TAG, "key = "+key);
                    if(key.equals("collection")){
                        Log.d(TAG,"Have collection");
                        Log.d(TAG, "Collection = "+getIntent().getExtras().getString("collection"));
                        if ((getIntent().getExtras().getString("collection")).equals("Announcements")) {
                            Log.d(TAG, "Announcements found");
                            for(String key2 : getIntent().getExtras().keySet()) {
                                if (key2.equals("documentID")) {
                                    id = (String) getIntent().getExtras().get("documentID");
                                    Log.d(TAG, "ID = " + id);
                                    intent.putExtra("documentID", id);
                                    intent.putExtra("collection","Announcements");
                                    Log.d(TAG, "Open StartActivity");
                                    break outerloop;
                                }
                            }
                            Log.d(TAG, "DocumentID not found");
                        } else {
                            if ((getIntent().getExtras().getString("collection")).equals("Enrollments")) {
                                Log.d(TAG, "Enrollments found");
                                intent.putExtra("collection","Enrollments");
                                break;
                            } else {
                                Log.d(TAG, "Bad collection");
                            }
                        }

                    }else{
                        Log.d(TAG,"Haven't collection");
                    }
                }
            }
            startActivity(intent);
            this.finish();
        }else{
            if(getIntent().getExtras() != null) {
                outerloop:
                for(String key : getIntent().getExtras().keySet()){
                    Log.d(TAG, "");
                    Log.d(TAG, "key=  "+key);
                    if(key.equals("collection")){
                        Log.d(TAG,"Have collection");
                        Log.d(TAG, "Collection = "+getIntent().getExtras().getString("collection"));
                        if ((getIntent().getExtras().getString("collection")).equals("Announcements")) {
                            Log.d(TAG, "Announcements found");
                            for(String key2 : getIntent().getExtras().keySet()) {
                                Log.d(TAG, "DocumentID = "+getIntent().getExtras().getString("documentID"));
                                if (key2.equals("documentID")) {
                                    Log.d(TAG, "DocumentID found");
                                    titleTextView.setText("Ogłoszenie");
                                    backTextView.setText("Ogłoszenia");
                                    button_login.setBackgroundResource(Variables.getIcon_back());
                                    button_add.setVisibility(View.INVISIBLE);
                                    replaceFragment(new ContentFragment(), getIntent().getExtras().getString("documentID"), R.id.frameLayout_login);
                                    break outerloop;
                                }
                            }
                            Log.d(TAG, "DocumentID not found");
                        } else {
                            if ((getIntent().getExtras().getString("collection")).equals("Enrollments")) {
                                Log.d(TAG, "Enrollments found");
                                replaceFragment(new MetronomeFragment(), 1);
                                break;
                            } else {
                                Log.d(TAG, "Bad collection");
                            }
                        }

                    }else{
                        Log.d(TAG,"Haven't collection");
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void replaceFragment(Fragment fragment, int direction){
        String tag = null;

        switch(switch_number){
            case 0:
                tag = "Guitar-Master - LogoutFragment";
                titleTextView.setText(getString(R.string.titleLogout));
                button_add.setVisibility(View.INVISIBLE);
                break;
            case 1:
                tag = "Guitar-Master - GuitarPickFragment";
                titleTextView.setText(getString(R.string.titleGuitarPick));
                button_add.setVisibility(View.INVISIBLE);
                break;
            case 2:
                tag = "Guitar-Master - MetronomeFragment";
                titleTextView.setText(getString(R.string.titleMetronome));
                button_add.setVisibility(View.INVISIBLE);
                break;
            case 3:
                tag = "Guitar-Master - SongBookFragment";
                titleTextView.setText(getString(R.string.titleSongBook));
                button_add.setVisibility(View.VISIBLE);
                break;
        }

        if(direction == 1){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .addToBackStack(TAG)
                    .replace(R.id.frameLayout_login, fragment, TAG)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                    .addToBackStack(TAG)
                    .replace(R.id.frameLayout_login, fragment, TAG)
                    .commit();
        }
    }
}
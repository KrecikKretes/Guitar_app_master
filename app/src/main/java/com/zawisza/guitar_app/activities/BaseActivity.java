package com.zawisza.guitar_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.databinding.ActivityAdminBinding;
import com.zawisza.guitar_app.databinding.ActivityUserBinding;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.Login.LoginFragment;
import com.zawisza.guitar_app.fragments.Metronome.MetronomeFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    protected ActivityUserBinding userBinding;
    protected ActivityAdminBinding adminBinding;
    protected int switch_number = 1;
    protected static final String TAG = "Guitar-Master";

    protected Button button_add;
    protected Button button_login;
    protected TextView titleTextView;
    protected TextView backTextView;

    protected BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void replaceFragment(Fragment fragment, String id, int idLayout){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("documentID", id);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(idLayout, fragment);
        fragmentTransaction.addToBackStack("Guitar-Master - SongBookFragment");
        fragmentTransaction.commit();
    }

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

    @SuppressLint("SetTextI18n")
    protected void listeners(int id_layout) {
        button_add.setOnClickListener(view -> {
            titleTextView.setText(R.string.titleContent);
            button_add.setVisibility(View.INVISIBLE);
            button_login.setBackgroundResource(R.drawable.back_icon);
            backTextView.setText(R.string.titleSongBook);

            switch_number = 4;

            button_login.setOnClickListener(view1 -> {
                changeSetting();
                backToFragment(id_layout);
            });

            backTextView.setOnClickListener(view1 -> {
                changeSetting();
                backToFragment(id_layout);
            });

        });

        button_login.setOnClickListener(view -> {
            switch_number = 0;
            backToFragment(id_layout);
        });

        backTextView.setOnClickListener(view -> {
            switch_number = 0;
            backToFragment(id_layout);
        });

    }

    private void changeSetting(){
        if(switch_number != 0 && switch_number != 4){
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
            backTextView.setText(R.string.Empty);
        }else{
            button_login.setBackgroundResource(R.drawable.back_icon);
            button_add.setVisibility(View.INVISIBLE);
        }
        button_login.setScaleX(0.5f);
        button_login.setScaleY(0.5f);
    }

    //Change Fragment by click back button
    @SuppressLint("SetTextI18n")
    private void backToFragment(int id_layout){
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
            replaceFragment(new LoginFragment(),2, id_layout);
        }else{
            switch(switch_number){
                case 1:
                    replaceFragment(new GuitarPickFragment(), 2, id_layout);
                    break;
                case 2:
                    replaceFragment(new MetronomeFragment(), 2, id_layout);
                    break;
                case 3:
                    replaceFragment(new SongbookFragment(), 2, id_layout);
                    break;
            }
            backTextView.setText(getString(R.string.Empty));
        }
    }


    protected void replaceFragment(Fragment fragment, int direction, int id_layout){
        String tag = null;
        switch(switch_number){
            case 0:
                tag = "Guitar-Master - LoginFragment";
                titleTextView.setText(getString(R.string.titleLogin));
                break;
            case 1:
                Log.d(TAG,"Change to GuitarPickFragment");
                tag = "Guitar-Master - GuitarPickFragment";
                titleTextView.setText(getString(R.string.titleGuitarPick));
                backTextView.setText(getString(R.string.Empty));
                break;
            case 2:
                Log.d(TAG,"Change to MetronomeFragment");
                tag = "Guitar-Master - MetronomeFragment";
                titleTextView.setText(getString(R.string.titleMetronome));
                backTextView.setText(getString(R.string.Empty));
                break;
            case 3:

                tag = "Guitar-Master - SongBookFragment";
                titleTextView.setText(getString(R.string.titleSongBook));
                backTextView.setText(getString(R.string.Empty));
                break;
        }

        if(direction == 1){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .addToBackStack(tag)
                    .replace(id_layout, fragment, tag)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                    .addToBackStack(tag)
                    .replace(id_layout, fragment, tag)
                    .commit();
        }
    }
}
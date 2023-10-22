package com.zawisza.guitar_app.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.databinding.ActivityAdminBinding;
import com.zawisza.guitar_app.databinding.ActivityUserBinding;
import com.zawisza.guitar_app.fragments.Content.ContentFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.Login.LoginFragment;
import com.zawisza.guitar_app.fragments.Login.LogoutFragment;
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

    private void changeTextInTextView(TextView textView, String text){
        textView.setText(text);
    }

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
            this.finishAffinity();
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
                case "Guitar-Master - SongBookFragment":
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

            button_login.setOnClickListener(view1 ->
                    backToFragment(id_layout));

            backTextView.setOnClickListener(view1 ->
                    backToFragment(id_layout));

        });

        button_login.setOnClickListener(view ->
                backToFragment(id_layout));

        backTextView.setOnClickListener(view ->
                backToFragment(id_layout));

    }

    //Change Fragment by click back button
    @SuppressLint("SetTextI18n")
    private void backToFragment(int id_layout){
        if(titleTextView.getText().equals(getString(R.string.titleLogin)) || titleTextView.getText().equals(getString(R.string.titleLogout))){
            Log.d(TAG, "Fragment");
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
            String titleGuitarPick = getString(R.string.titleGuitarPick);
            String titleMetronome = getString(R.string.titleMetronome);
            String titleSongBook = getString(R.string.titleSongBook);
            String text = (String) backTextView.getText();
            if (titleGuitarPick.equals(text)) {
                replaceFragment(new GuitarPickFragment(), 2, id_layout);
            } else if (titleMetronome.equals(text)) {
                replaceFragment(new MetronomeFragment(), 2, id_layout);
            } else if (titleSongBook.equals(text)) {
                replaceFragment(new SongbookFragment(), 2, id_layout);
            }
            backTextView.setText(getString(R.string.Empty));
        }else{
            Log.d(TAG, "LoginFragment or LogoutFragment");
            button_login.setBackgroundResource(R.drawable.back_icon);
            button_add.setVisibility(View.INVISIBLE);
            switch(switch_number){
                case 1:
                    changeTextInTextView(backTextView,getString(R.string.titleGuitarPick));
                    break;
                case 2:
                    changeTextInTextView(backTextView,getString(R.string.titleMetronome));
                    break;
                case 3:
                    changeTextInTextView(backTextView,getString(R.string.titleSongBook));
                    break;
            }
            switch_number = 0;
            if(id_layout == R.id.frameLayout){
                Log.d(TAG,"LoginFragment");
                titleTextView.setText(getString(R.string.titleLogin));
                replaceFragment(new LoginFragment(),2, id_layout);

            }else{
                Log.d(TAG,"LogoutFragment");
                titleTextView.setText(getString(R.string.titleLogout));
                replaceFragment(new LogoutFragment(),2, id_layout);

            }
        }
    }

    @SuppressLint("SetTextI18n")
    protected void checkNotification(int id_layout) {
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
                                replaceFragment(new ContentFragment(), getIntent().getExtras().getString("documentID"), id_layout);
                                titleTextView.setText(getIntent().getExtras().getString("songTitle"));
                                backTextView.setText(getString(R.string.titleSongBook));
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
            replaceFragment(new GuitarPickFragment(),1, id_layout);
        }
    }

    protected void smoothBackToFirstItem(RecyclerView recyclerView){
        recyclerView.smoothScrollToPosition(0);
    }

    protected void replaceFragment(Fragment fragment, int direction, int id_layout){
        String tag;
        if(fragment instanceof LoginFragment) {
            tag = "Guitar-Master - LoginFragment";
            titleTextView.setText(getString(R.string.titleLogin));
        }else{
            button_login.setBackgroundResource(R.drawable.person_profile_image_icon);
            backTextView.setText(getString(R.string.Empty));
            if(fragment instanceof GuitarPickFragment) {
                Log.d(TAG,"Change to GuitarPickFragment");
                tag = "Guitar-Master - GuitarPickFragment";
                titleTextView.setText(getString(R.string.titleGuitarPick));
            }else{
                if (fragment instanceof MetronomeFragment) {
                    Log.d(TAG, "Change to MetronomeFragment");
                    tag = "Guitar-Master - MetronomeFragment";
                    titleTextView.setText(getString(R.string.titleMetronome));
                } else {
                    Log.d(TAG, "Change to SongBookFragment");
                    tag = "Guitar-Master - SongBookFragment";
                    titleTextView.setText(getString(R.string.titleSongBook));
                }
            }
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
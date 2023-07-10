package com.zawisza.guitar_app.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.Images;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.databinding.ActivityAdminBinding;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.Content.ContentFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;
import com.zawisza.guitar_app.fragments.Metronome.MetronomeFragment;
import com.zawisza.guitar_app.fragments.Login.LogoutFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class AdminActivity extends AppCompatActivity{

    ActivityAdminBinding binding;
    private int switch_number = 1;

    public static Context contextOfApplication;
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    Button button_add;
    Button button_logout;
    TextView titleTextView;
    TextView backTextView;
    Boolean isLogoutFragment = false;

    private static final String TAG = "Rajd - AdminActivity";

    private Images images = new Images();


    @SuppressLint("SetTextI18n")
    @Override
    public void onBackPressed(){
        FragmentManager mgr = getSupportFragmentManager();
        Log.d(TAG, "" + mgr.getBackStackEntryCount());
        if(mgr.getBackStackEntryCount() == 1 || mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2 ).getName() == null){
            super.onBackPressed();
            super.onBackPressed();
        }else{
            Log.d(TAG,mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName());
            switch (Objects.requireNonNull(mgr.getBackStackEntryAt(mgr.getBackStackEntryCount() - 2).getName())){
                case "Rajd - ActivFragment":
                    titleTextView.setText("Ogłoszenia");
                    button_add.setVisibility(View.VISIBLE);
                    mgr.popBackStack();
                    break;
                case "Rajd - LinksFragment":
                    titleTextView.setText("Zapisy");
                    button_add.setVisibility(View.VISIBLE);
                    mgr.popBackStack();
                    break;
                case "Rajd - FAQFragment":
                    titleTextView.setText("FAQ");
                    button_add.setVisibility(View.INVISIBLE);
                    mgr.popBackStack();
                    break;
                case "Rajd - CalendarFragment":
                    titleTextView.setText("Harmonogram");
                    button_add.setVisibility(View.INVISIBLE);
                    mgr.popBackStack();
                    break;
                case "Rajd - RoutesFragment":
                    titleTextView.setText("Trasy");
                    button_add.setVisibility(View.INVISIBLE);
                    mgr.popBackStack();
                    break;
                default:
                    break;
            }
            button_logout.setBackgroundResource(Variables.getIcon_user());
            backTextView.setText("");
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"Open Admin Activity");

        contextOfApplication = getApplicationContext();

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        button_add = findViewById(R.id.button_add);
        button_logout = findViewById(R.id.button_login);
        titleTextView = findViewById(R.id.activity_text);
        backTextView = findViewById(R.id.back_text);

        replaceFragment(new GuitarPickFragment(), 1);

        Variables.setButton_add(button_add);
        Variables.setButton_login(button_logout);
        Variables.setTitleTextView(titleTextView);
        Variables.setBackTextView(backTextView);

        Intent intentFromFragment = getIntent();
        if(intentFromFragment.getStringExtra("fragment") != null && intentFromFragment.getStringExtra("fragment").equals("Content")){
            replaceFragment(new ContentFragment(), intentFromFragment.getStringExtra("documentID"));
            titleTextView.setText("Ogłoszenie");
            backTextView.setText("Ogłoszenia");
            button_add.setVisibility(View.INVISIBLE);
            button_logout.setBackgroundResource(Variables.getIcon_back());
        }


        listeners();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic(Variables.getTopic_to_android());

        checkNotification();


        bindings();
    }


    @SuppressLint("SetTextI18n")
    private void backToMainsFragment(){
        if(titleTextView.getText().equals("Wylogowanie") ||titleTextView.getText().equals("Dodaj ogłoszenie") || titleTextView.getText().equals("Dodaj zapisy")){
            button_logout.setBackgroundResource(Variables.getIcon_user());
            backTextView.setText("");
            switch(switch_number){
                case 1:
                    titleTextView.setText("Ogłoszenia");
                    button_add.setVisibility(View.VISIBLE);
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
            isLogoutFragment = false;

        }else{
            button_logout.setBackgroundResource(Variables.getIcon_back());
            titleTextView.setText("Wylogowanie");
            button_add.setVisibility(View.INVISIBLE);

            changeBackText();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .addToBackStack("Rajd - LogoutFragment")
                    .replace(R.id.frameLayout_login, new LogoutFragment())
                    .commit();
            isLogoutFragment = true;
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

    @SuppressLint("SetTextI18n")
    private void listeners() {
        button_add.setVisibility(View.VISIBLE);

        button_add.setOnClickListener(view -> {
            titleTextView.setText("Dodaj ogłoszenie");
            button_add.setVisibility(View.INVISIBLE);
            button_logout.setBackgroundResource(Variables.getIcon_back());
            backTextView.setText("Ogłoszenia");

            isLogoutFragment = true;

            button_logout.setOnClickListener(view1 -> {
                changeSetting();
                backToMainsFragment();
            });

            backTextView.setOnClickListener(view1 -> {
                changeSetting();
                backToMainsFragment();
            });

        });

        button_logout.setOnClickListener(view -> {
            isLogoutFragment = true;
            backToMainsFragment();
        });

        backTextView.setOnClickListener(view -> {
            isLogoutFragment = true;
            backToMainsFragment();
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference imageRef = db.collection(Variables.getImages());
        ArrayList<Images> imagesArrayList = new ArrayList<>();


        imageRef.addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Error", error.toString());
                        return;
                    }
                    assert value != null;
                    for(DocumentChange documentChange : value.getDocumentChanges()){

                        if(documentChange.getType() == DocumentChange.Type.ADDED){
                            images = documentChange.getDocument().toObject(Images.class);
                            imagesArrayList.add(images);
                            Log.d(TAG,"Adding Images");
                        }
                        if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                            Images newImages = documentChange.getDocument().toObject(Images.class);
                            Log.d(TAG,"Trying modified Images");
                            for(int i = 0; i< imagesArrayList.size(); i++){
                                if(newImages.getId().equals(imagesArrayList.get(i).getId())) {


                                    imagesArrayList.set(i, newImages);
                                    String id = imagesArrayList.get(i).getId();
                                    try {

                                        //Szukanie czy plik istnieje w pamieci
                                        File f = new File("/data/data/com.zawisza.raid/app_images", id + ".jpg");
                                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                                    } catch (FileNotFoundException e){
                                        Log.d(TAG, "File not download yet");
                                        break;
                                    }
                                    Log.d(TAG, "File found");
                                    //Dostanie sie do pliku w bazie
                                    String name;
                                    if (imagesArrayList.get(i).getCollection().equals("Routes")) {
                                        name = "routes";
                                    } else {
                                        name = "images";
                                    }
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(name);
                                    StorageReference fileReference;
                                    if (name.equals("routes")) {
                                        fileReference = storageReference.child(id);
                                    } else {
                                        fileReference = storageReference.child(id + ".jpg");
                                    }
                                    try {
                                        //Pobranie obrazu z bazy
                                        File localFile = File.createTempFile("images", "jpg");
                                        fileReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                                            ContextWrapper cw = new ContextWrapper(this);
                                            // path to /data/data/yourapp/app_data/images
                                            File directory = cw.getDir("images", Context.MODE_PRIVATE);
                                            Log.d(TAG, directory.getAbsolutePath());
                                            // Create imageDir
                                            File mypath = new File(directory, id + ".jpg");

                                            FileOutputStream fos = null;
                                            try {
                                                Log.d(TAG, "1");
                                                fos = new FileOutputStream(mypath);
                                                Log.d(TAG, "2");
                                                // Use the compress method on the BitMap object to write image to the OutputStream
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                                Log.d(TAG, "3");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Log.d(TAG, "sTIS error");
                                                Log.d(TAG, e.toString());
                                            } finally {
                                                try {
                                                    assert fos != null;
                                                    fos.close();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }).addOnFailureListener(e -> Log.d(TAG, e.toString()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.d(TAG, "gIFF error");
                                    }


                                    Log.d(TAG, "Complete modified Images");
                                    break;

                                }

                            }
                        }
                        if(documentChange.getType() == DocumentChange.Type.REMOVED){
                            Images newImages = documentChange.getDocument().toObject(Images.class);
                            Log.d(TAG,"Trying remove Images");
                            for(int i = 0; i< imagesArrayList.size(); i++){
                                if(newImages.getId().equals(imagesArrayList.get(i).getId())){
                                    imagesArrayList.remove(i);
                                    Log.d(TAG,"Complete remove Images");
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void changeSetting(){

        if(isLogoutFragment){
            button_logout.setBackgroundResource(Variables.getIcon_user());
            backTextView.setText("");
        }else{
            button_logout.setBackgroundResource(Variables.getIcon_back());
            button_add.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void bindings() {
        Functions functions = new Functions();
        binding.navViewLogin.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){

                case R.id.nav_guitarpick:
                    if(switch_number != 1 || isLogoutFragment || Variables.isIsContent()){
                        switch_number = 1;
                        replaceFragment(new GuitarPickFragment(),1);

                        button_add.setOnClickListener(view -> {
                            titleTextView.setText("Dodaj ogłoszenie");
                            button_add.setVisibility(View.INVISIBLE);
                            button_logout.setBackgroundResource(Variables.getIcon_back());

                            changeBackText();
                            isLogoutFragment = true;
                            button_logout.setOnClickListener(view1 ->
                                    backToMainsFragment());

                            backTextView.setOnClickListener(view1 ->
                                backToMainsFragment()
                            );
                        });
                    }else{
                        functions.smoothBackToFirstItem(findViewById(R.id.rv));
                    }
                    break;

                case R.id.nav_metronome:
                    if(switch_number != 2 || isLogoutFragment || Variables.isIsContent()){

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
                    if(switch_number != 3 || isLogoutFragment || Variables.isIsContent()){
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
            isLogoutFragment = true;
            changeSetting();
            isLogoutFragment = false;
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
                                    button_logout.setBackgroundResource(Variables.getIcon_back());
                                    button_add.setVisibility(View.INVISIBLE);
                                    replaceFragment(new ContentFragment(), getIntent().getExtras().getString("documentID"));
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
        String TAG = null;
        Variables.setIsContent(false);
        isLogoutFragment = false;
        button_add.setVisibility(View.INVISIBLE);
        backTextView.setText("");

        switch(switch_number){
            case 1:
                TAG = "Rajd - ActivFragment";
                button_add.setVisibility(View.VISIBLE);
                titleTextView.setText("Ogłoszenia");
                break;
            case 2:
                TAG = "Rajd - LinksFragment";
                button_add.setVisibility(View.VISIBLE);
                titleTextView.setText("Zapisy");
                break;
            case 3:
                TAG = "Rajd - FAQFragment";
                titleTextView.setText("FAQ");

                break;
            case 4:
                TAG = "Rajd - CalendarFragment";
                titleTextView.setText("Harmonogram");

                break;
            case 5:
                TAG = "Rajd - RoutesFragment";
                titleTextView.setText("Trasy");

                break;
        }

        Log.d(TAG, "%%%%%%%"+button_add.getVisibility());

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

    private void replaceFragment(Fragment fragment, String id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("documentID", id);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_login, fragment);
        fragmentTransaction.commit();
    }

}
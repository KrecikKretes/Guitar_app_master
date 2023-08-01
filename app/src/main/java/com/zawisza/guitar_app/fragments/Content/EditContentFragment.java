package com.zawisza.guitar_app.fragments.Content;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.activities.AdminActivity;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class EditContentFragment extends Fragment {

    private static final String TAG = "Rajd - EditContentFragment";

    private String id;

    private EditText editTextTitle;
    private EditText editTextSubTitle;
    private EditText editTextDes;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch isPrioritySwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch isNotificationSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch isReadySwitch;

    private Button addImage;
    private Button deleteImage;

    private Button edit;

    private String title;
    private String content;

    private ImageView imageView;
    private Uri imageUri;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private CollectionReference collectionReference = db.collection(Variables.getAnnouncements());
    private ProgressBar progressBar;

    @SuppressLint("StaticFieldLeak")
    private static Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_content, container, false);

        Bundle bundle = this.getArguments();
        assert bundle != null;
        id = bundle.getString("documentID");

        context = getContext();

        edit = view.findViewById(R.id.button_edit);

        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextSubTitle = view.findViewById(R.id.edit_text_subtitle);
        editTextDes = view.findViewById(R.id.edit_text_des);
        isReadySwitch = view.findViewById(R.id.isReadySwitch);
        isPrioritySwitch = view.findViewById(R.id.isPrioritySwitch);
        addImage = view.findViewById(R.id.button_image_add);
        deleteImage = view.findViewById(R.id.button_image_delete);

        imageView = view.findViewById(R.id.imageViewAdd);
        progressBar = view.findViewById(R.id.progress_bar_add);

        listeners();

        return view;
    }

    private void listeners() {

        addImage.setOnClickListener(view -> openFileChooser());

        deleteImage.setOnClickListener(view -> {
            Picasso.get().load((Uri) null).into(imageView);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        });

        isReadySwitch.setOnClickListener(view -> {
            if(isReadySwitch.isChecked()){
                edit.setVisibility(View.VISIBLE);
                edit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }else{
                edit.setVisibility(View.INVISIBLE);
                edit.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            }
        });

        edit.setOnClickListener(view -> dialogAdd());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        Picasso.get().load(imageUri).into(imageView);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }
            });


    private void dialogAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Czy na pewno chcesz zedytować aktywność?");
        //builder.setMessage("???");
        builder.setPositiveButton("Tak", (dialogInterface, i) -> {
            try {
                saveNote();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        builder.setNegativeButton("Nie", (dialogInterface, i) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveNote() throws UnsupportedEncodingException {

        title = editTextTitle.getText().toString();
        String subTitle = editTextSubTitle.getText().toString();
        content = editTextDes.getText().toString();

        /*
        DocumentReference documentReference = collectionReference.document(id);

        if(!title.trim().isEmpty()){
            documentReference.update("title", title);
        }
        if(!content.trim().isEmpty()){
            documentReference.update("content", content);
        }
        if(!subTitle.trim().isEmpty()){
            documentReference.update("subTitle", subTitle);
        }


         */
        if(imageView.getDrawable() != null) {
            addImageToDatabase(id);
        }

        Toast.makeText(context,"Zmieniono ogłoszenie",Toast.LENGTH_SHORT).show();

        replaceFragments(R.id.frameLayout_login);
    }

    @SuppressLint("LongLogTag")
    private void addImageToDatabase(String id) throws UnsupportedEncodingException {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images");
        if(imageUri != null){
            Log.d(TAG,imageUri.toString());
            StorageReference fileReference = storageReference.child(id + ".jpg" );
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(AdminActivity.getContextOfApplication().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            assert bmp != null;
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();


            fileReference.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setProgress(0), 5000);
                    }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show()).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressBar.setProgress((int)progress);
            });

        }else{
            Toast.makeText(context,"Nie dodano obrazu", Toast.LENGTH_SHORT).show();
            //db.collection(Variables.getAnnouncements()).document(id).update("isImage", false);
        }

    }

    private void replaceFragments(int id_layout){
        GuitarPickFragment fragment = new GuitarPickFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations( R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(id_layout, fragment)
                .commit();
    }

}

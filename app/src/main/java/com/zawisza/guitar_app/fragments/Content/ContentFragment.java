package com.zawisza.guitar_app.fragments.Content;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.activities.ImageActivity;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;


public class ContentFragment extends Fragment {

    private static final String TAG = "Rajd - ContentFragment";

    private FirebaseAuth auth;
    private TextView title;
    private TextView subtitle;
    private TextView content;
    private TextView delete;
    private ImageView imageView;

    private Context context;

    Button button_back;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        Log.d(TAG, "Start ContentFragment");

        Variables.getButton_add().setVisibility(View.INVISIBLE);

        Variables.setIsContent(true);

        context = getContext();

        auth = FirebaseAuth.getInstance();

        Functions functions = new Functions();

        Bundle bundle = this.getArguments();
        assert bundle != null;
        String id = bundle.getString("documentID");

        title = view.findViewById(R.id.title_show);
        subtitle = view.findViewById(R.id.subtitle_show);
        content = view.findViewById(R.id.content_show);
        delete = view.findViewById(R.id.content_button_delete);
        imageView = view.findViewById(R.id.image_show);

        Log.d(TAG, "ID: " + this.getId());

        button_back = view.findViewById(R.id.content_button_back);

        if(auth.getCurrentUser() != null) {
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.INVISIBLE);
        }

        Log.d(TAG, "DocumentID = " + id);

        CollectionReference collectionReference = db.collection(Variables.getAnnouncements());
        DocumentReference documentReference = collectionReference.document(id);

        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot value = task.getResult();
            if(value.exists()){

                title.setText(functions.newLinesRepairer(value.getString("title")));

                subtitle.setText(functions.newLinesRepairer(value.getString("subTitle")));

                content.setText(functions.newLinesRepairer(value.getString("content")));


                if(Boolean.TRUE.equals(value.getBoolean("isImage"))) {
                    Log.d(TAG,"Start");
                    functions.loadImageFromStorage(id, imageView, context, "images");
                }else{
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    params.width = 0;
                    params.height = 0;
                    imageView.setLayoutParams(params);
                }
            }


        });


        Buttons(id);


        return view;
    }

    public void Buttons(String id){

        delete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("Czy na pewno chcesz usunąć aktywność?");
            //builder.setMessage("???");
            builder.setPositiveButton("Tak", (dialogInterface, i) -> {
                Toast.makeText(getContext(),"Pomyślnie usunięto", Toast.LENGTH_LONG).show();
                CollectionReference collectionReference = db.collection(Variables.getAnnouncements());
                DocumentReference documentReference = collectionReference.document(id);

                documentReference.update("hidden",true);

                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_login,
                        new GuitarPickFragment()).commit();

            });
            builder.setNegativeButton("Nie", (dialogInterface, i) -> {
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        button_back.setOnClickListener(view ->
                changeUpMenu(auth.getCurrentUser() != null, requireActivity().getSupportFragmentManager())
        );
        imageView.setOnClickListener(view -> {
            Log.d(TAG,"Image click");

            Intent intent = new Intent(getContext(), ImageActivity.class);
            intent.putExtra("image", id);
            intent.putExtra("dir", "images");
            intent.putExtra("activity", getActivity().getClass().getSimpleName());

            startActivity(intent);
            requireActivity().finish();
        });
    }

    @SuppressLint("SetTextI18n")
    public void changeUpMenu(boolean isLogin, FragmentManager fragmentManager){
        Variables.getTitleTextView().setText("Ogłoszenia");
        Variables.getBackTextView().setText("");
        Variables.getButton_login().setBackgroundResource(Variables.getIcon_user());
        if(isLogin) {
            Variables.getButton_add().setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.frameLayout_login, new GuitarPickFragment())
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.frameLayout, new GuitarPickFragment())
                    .commit();
        }
    }
}
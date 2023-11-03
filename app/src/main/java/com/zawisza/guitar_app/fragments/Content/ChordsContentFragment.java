package com.zawisza.guitar_app.fragments.Content;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zawisza.guitar_app.Functions;
import com.zawisza.guitar_app.R;
import com.zawisza.guitar_app.Variables;
import com.zawisza.guitar_app.activities.ImageActivity;
import com.zawisza.guitar_app.fragments.Chords.ChordsFragment;
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;


public class ChordsContentFragment extends Fragment {

    private static final String TAG = "Guitar-Master - ChordsContentFragment";

    private FirebaseAuth auth;
    private TextView titleTextView;
    private TextView delete;
    private ImageView imageView;

    private Context context;

    Button button_back;

    private String chord;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chordscontent, container, false);

        Log.d(TAG, "Start ChordContentFragment");

        Variables.getButton_add().setVisibility(View.INVISIBLE);

        Variables.setIsContent(true);

        context = getContext();

        auth = FirebaseAuth.getInstance();

        Functions functions = new Functions();

        Bundle bundle = this.getArguments();
        assert bundle != null;
        chord = bundle.getString("chord");
        String code = bundle.getString("code");

        titleTextView = view.findViewById(R.id.title_show);
        imageView = view.findViewById(R.id.imageview_show);
        delete = view.findViewById(R.id.content_button_delete);
        button_back = view.findViewById(R.id.content_button_back);

        if(auth.getCurrentUser() != null) {
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.INVISIBLE);
        }

        Log.d(TAG, "Title = " + chord);

        titleTextView.setText(functions.newLinesRepairer(chord));
        Bitmap b1 = null;
        Bitmap b2 = null;

        Bitmap bitmapStart = BitmapFactory.decodeResource(getResources(), R.drawable.guitar_chors_empty)
                .copy(Bitmap.Config.ARGB_8888, true);

        for (int i = 0; i + 1 < code.length(); i+=2) {
            Log.d(TAG, String.valueOf(i));
            if(i == 0){
                b1 =  writeTextOnDrawable(code.charAt(i), code.charAt(i + 1), +1, bitmapStart);
            }else{
                b2 =  writeTextOnDrawable(code.charAt(i), code.charAt(i + 1), i/2+1,  b1);
                b1 = b2;
            }
        }
        imageView.setImageBitmap(b1);


        Buttons();


        return view;
    }

    public void Buttons(){

        delete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            builder.setTitle("Czy na pewno chcesz usunąć aktywność?");
            //builder.setMessage("???");
            builder.setPositiveButton("Tak", (dialogInterface, i) -> {
                Toast.makeText(getContext(),"Pomyślnie usunięto", Toast.LENGTH_LONG).show();
                CollectionReference collectionReference = db.collection(requireActivity().getString(R.string.collectionChords));
                //DocumentReference documentReference = collectionReference.document().;

                //documentReference.update("hidden",true);

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
    }

    @SuppressLint("SetTextI18n")
    public void changeUpMenu(boolean isLogin, FragmentManager fragmentManager){
        Variables.getTitleTextView().setText(getString(R.string.titleChords));
        Variables.getBackTextView().setText(getString(R.string.Empty));
        Variables.getButton_login().setBackgroundResource(Variables.getIcon_user());
        if(isLogin) {
            Variables.getButton_add().setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.frameLayout_login, new ChordsFragment())
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.frameLayout, new ChordsFragment())
                    .commit();
        }
    }

    private Bitmap writeTextOnDrawable(char finger, char threshold, int i, Bitmap bitmapStart) {
        Canvas canvas = new Canvas(bitmapStart);
        Bitmap fingerBM = null;
        int line = 580 - 45 - (i * 82);
        
        switch (finger){
            case '0':
            case '-':
                break;
            case '1':
                fingerBM = BitmapFactory.decodeResource(getResources(), R.drawable.chord1)
                        .copy(Bitmap.Config.ARGB_8888, true);
                break;
            case '2':
                fingerBM = BitmapFactory.decodeResource(getResources(), R.drawable.chord2)
                        .copy(Bitmap.Config.ARGB_8888, true);
                break;
            case '3':
                fingerBM = BitmapFactory.decodeResource(getResources(), R.drawable.chord3)
                        .copy(Bitmap.Config.ARGB_8888, true);
                break;
            case '4':
                fingerBM = BitmapFactory.decodeResource(getResources(), R.drawable.chord4)
                        .copy(Bitmap.Config.ARGB_8888, true);
                break;
            default:
                throw new IllegalStateException("Unexpected value FINGER: " + finger);
        }
        int line2 = 0;
        try{
            line2 =  70 + ((Integer.parseInt(String.valueOf(threshold))) - 1) * 120;
        }catch (NumberFormatException ignored) {

        };



        switch (threshold){
            case '0':
            case '-':
                break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
                canvas.drawBitmap(fingerBM, line,line2, null);
                break;
            default:
                throw new IllegalStateException("Unexpected value LINE: " + line);
        }

        return bitmapStart;
    }
}
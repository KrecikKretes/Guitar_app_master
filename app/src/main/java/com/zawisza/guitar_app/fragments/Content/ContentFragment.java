package com.zawisza.guitar_app.fragments.Content;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.zawisza.guitar_app.fragments.GuitarPick.GuitarPickFragment;
import com.zawisza.guitar_app.fragments.Songbook.SongbookFragment;


public class ContentFragment extends Fragment {

    private static final String TAG = "Guitar-Master - ContentFragment";

    private FirebaseAuth auth;
    private TextView titleTextView;
    private TextView subtitle;
    private TextView content;
    private TextView delete;
    private ImageView imageView;

    private Context context;

    Button button_back;

    private String title;

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
        title = bundle.getString("title");
        String accords = bundle.getString("accords");
        Boolean isTabs = bundle.getBoolean("isTabs");

        titleTextView = view.findViewById(R.id.title_show);
        content = view.findViewById(R.id.content_show);
        imageView = view.findViewById(R.id.imageview_show);
        delete = view.findViewById(R.id.content_button_delete);
        button_back = view.findViewById(R.id.content_button_back);

        if(auth.getCurrentUser() != null) {
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.INVISIBLE);
        }

        Log.d(TAG, "Title = " + title);

        titleTextView.setText(functions.newLinesRepairer(title));
        content.setText(functions.newLinesRepairer(accords));
        Bitmap b1 = null;
        Bitmap b2 = null;
        Log.d(TAG, String.valueOf(isTabs));
        if(isTabs){
            Bitmap bitmapStart = BitmapFactory.decodeResource(getResources(), R.drawable.start)
                    .copy(Bitmap.Config.ARGB_8888, true);
            Bitmap bitmapEnd = BitmapFactory.decodeResource(getResources(), R.drawable.end)
                    .copy(Bitmap.Config.ARGB_8888, true);



            for (int i = 0; i + 1 < accords.length(); i+=3) {
                Log.d(TAG, String.valueOf(i));
                if(accords.charAt(i + 2) == '.' && i ==0){
                    b1 =  writeTextOnDrawable(R.drawable.mid,accords.charAt(i), accords.charAt(i + 1), bitmapStart);
                }
                if(accords.charAt(i + 2) == '.' && i !=0){
                    b2 =  writeTextOnDrawable(R.drawable.mid,accords.charAt(i), accords.charAt(i + 1), b1);
                    b1 = b2;
                }

            }
            imageView.setImageBitmap(mergeBitmap(b1,bitmapEnd));


        }


        //Buttons(id);


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
                CollectionReference collectionReference = db.collection(requireActivity().getString(R.string.songbook));
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
        Variables.getTitleTextView().setText("SongBook");
        Variables.getBackTextView().setText("");
        Variables.getButton_login().setBackgroundResource(Variables.getIcon_user());
        if(isLogin) {
            Variables.getButton_add().setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.frameLayout_login, new SongbookFragment())
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .setCustomAnimations( R.anim.enter_left_to_right, R.anim.exit_left_to_right,  R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.frameLayout, new SongbookFragment())
                    .commit();
        }
    }

    private Bitmap writeTextOnDrawable(int drawableId, char line, char fret, Bitmap bitmapStart) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(context, 15));

        Rect textRect = new Rect();
        paint.getTextBounds(String.valueOf(fret), 0, 1, textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(context, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset


        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos1 = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        Log.d(TAG, "aa " + yPos1);
        Log.d(TAG, "bb " + canvas.getHeight());
        Log.d(TAG, "cc " + (paint.descent() + paint.ascent()) );

        int yPos;

        switch (line){
            case '1':
                yPos = 30;
                break;
            case '2':
                yPos = 75;
                break;
            case '3':
                yPos = 120;
                break;
            case '4':
                yPos = 165;
                break;
            case '5':
                yPos = 210;
                break;
            case '6':
                yPos = 255;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + line);
        }
        yPos = (int) ((yPos + 12) * 2 );

        canvas.drawText(String.valueOf(fret), xPos, yPos , paint);
        return mergeBitmap(bitmapStart,bm);
    }

    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

    public Bitmap mergeBitmap(Bitmap fr, Bitmap sc)
    {

        Bitmap comboBitmap;

        int width, height;

        width = fr.getWidth() + sc.getWidth();
        height = fr.getHeight();

        comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(comboBitmap);


        comboImage.drawBitmap(fr, 0f, 0f, null);
        comboImage.drawBitmap(sc, fr.getWidth(), 0f , null);

        return comboBitmap;

    }
}
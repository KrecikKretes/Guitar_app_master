package com.zawisza.guitar_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;


public class Functions {
    private final String TAG ="Guitar-Master - Functions";
    private Context context;

    public void smoothBackToFirstItem(RecyclerView recyclerView){
        recyclerView.smoothScrollToPosition(0);
    }

    public String newLinesRepairer(String text){
        text = text.replace("\\n ", Objects.requireNonNull(System.getProperty("line.separator")));
        text = text.replace(" \\n", Objects.requireNonNull(System.getProperty("line.separator")));
        text = text.replace("\\n", Objects.requireNonNull(System.getProperty("line.separator")));
        return text;
    }

    public void loadImageFromStorage(String id, ImageView imageView, Context context1, String dir) {
        Log.d(TAG,"\nStart lIFS");
        context = context1;
        File f;
        try {
            if(id.indexOf(".") > 0) {
                f = new File("/data/data/com.zawisza.guitar_app/app_images", id.substring(0, id.indexOf(".")) + ".jpg");
            }else{
                f = new File("/data/data/com.zawisza.guitar_app/app_images", id + ".jpg");
            }
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
            Log.d(TAG,"File found and set");
        }
        catch (FileNotFoundException e)
        {
            Log.d(TAG,"File not found");
            getImageFromFireStore(id, imageView, dir, context1);
        }
    }

    public void saveToInternalStorage(Bitmap bitmapImage, String id, String dir){
        Log.d(TAG,"Start sTIS");
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/images
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        Log.d(TAG,directory.getAbsolutePath());
        // Create imageDir

        File mypath;
        if(dir.equals("routes")){
            id = id.substring(0, id.indexOf("."));
        }
        mypath=new File(directory,id + ".jpg");

        FileOutputStream fos;
        try {
            Log.d(TAG,"1");
            fos = new FileOutputStream(mypath);
            Log.d(TAG,"2");
            Log.d(TAG, mypath.toString());
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Log.d(TAG,"3");
            //imageView.setImageBitmap(bitmapImage);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"sTIS error");
            Log.d(TAG,e.toString());
        }
        directory.getAbsolutePath();
    }

    public void getImageFromFireStore(String id, ImageView imageView, String dir , Context context1){
        Log.d(TAG,"\nStart gIFF");
        Log.d(TAG,"id : "+ id);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(dir);
        StorageReference fileReference;

        if(dir.equals("routes")){
            fileReference = storageReference.child(id);
        }else{
            fileReference = storageReference.child(id + ".jpg");
        }

        try {
            File localFile = File.createTempFile("images_", ".jpg");
            fileReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(localFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                Bitmap bmRotated = rotateBitmap(bitmap, orientation);

                Log.d(TAG,"gIFF local file");

                imageView.setImageBitmap(bmRotated);


                Log.d(TAG,"gIFF bitmap IN");
                saveToInternalStorage(bmRotated, id, dir);

            }).addOnFailureListener(e -> {
                Log.d(TAG, e.toString());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                params.width = 0;
                params.height = 0;
                imageView.setLayoutParams(params);
                Toast.makeText(context1,"Zdjęcie nie mogło się załadować", Toast.LENGTH_SHORT).show();
            });


        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"gIFF error");
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            params.width = 0;
            params.height = 0;
            imageView.setLayoutParams(params);
        } finally {
            Log.d(TAG,"gIFF end");
        }
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onItemClickAnimation(TextView[] textViewArray, ImageView arrow_image, ViewSwitcher viewSwitcher, Context context, String name, ImageView imageView){

        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.enter_up_to_down));
        viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.exit_down_to_up));

        viewSwitcher.showNext();

        Log.d(TAG,"get Height " + viewSwitcher.getHeight());
        Log.d(TAG, "get Measured Height " + viewSwitcher.getMeasuredHeight());
        Log.d(TAG, "");


        if(viewSwitcher.getHeight() != 0) {
            Log.d(TAG,"Zwijanie");

            arrow_image.setRotation(-90);

            ValueAnimator anim = ValueAnimator.ofInt(viewSwitcher.getMeasuredHeight(), 0);
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = viewSwitcher.getLayoutParams();
                layoutParams.height = val;
                viewSwitcher.setLayoutParams(layoutParams);
            });
            anim.setDuration(100);
            anim.start();


        }else {
            //Wysuwanie
            Log.d(TAG,"Wysuwanie");
            arrow_image.setRotation(90);

            float density = context.getResources().getDisplayMetrics().density;

            int height = 0;

            for (TextView textView : textViewArray) {
                Log.d(TAG, "TextView : " + textView.getText());
                if(textView.getText() != ""){
                    height += textView.getLineCount() * textView.getLineHeight();
                    Log.d(TAG, "H text: " + height);
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                    height += marginLayoutParams.topMargin;
                    Log.d(TAG, "H text + margin: " + height);
                    Log.d(TAG, "");
                }else{
                    //Log.d(TAG,"TextView is empty");
                    textView.setHeight(0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
                    params.setMargins(0,0,0,0);
                    textView.setLayoutParams(params);
                }
            }
            if(name.equals("FAQ")){
                height+=5;
            }else{
                if(name.equals("Calendar")){
                    height+=27;
                }else{
                    if(name.equals("Routes")){
                        Log.d(TAG,"H before image: "+ height);
                        height += imageView.getMeasuredHeight();
                        Log.d(TAG,"H after image: "+ height);
                        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)imageView.getLayoutParams();
                        height += marginLayoutParams.topMargin;
                        height+=618;
                    }
                }
            }


            Log.d(TAG,"H end: "+ height);

            ValueAnimator anim = ValueAnimator.ofInt(0, height);
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = viewSwitcher.getLayoutParams();
                layoutParams.height = val;
                viewSwitcher.setLayoutParams(layoutParams);
            });
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewGroup.LayoutParams layoutParams = viewSwitcher.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                }
            });
            anim.setDuration(100);
            anim.start();

        }

    }
}

package com.zawisza.guitar_app.fragments.GuitarPick;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.zawisza.guitar_app.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuitarPickFragment extends Fragment{

    private static final String TAG = "Guitar-Master - GuitarPickFragment";

    private Context context;
    private SoundMeter soundMete;

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private Tuning tuning;
    private int pitchIndex;
    private float lastFreq;

    private TextView textView;
    private TextView textView2;
    private ImageView imageView;
    private int[] compassUp = {0,0,0};
    private int[] compassDown = {0,0,0};

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"Open GuitarPickFragment");

        View view = inflater.inflate(R.layout.fragment_guitarpick, container, false);
        ImageView compassUpImage = view.findViewById(R.id.compasUp);
        ImageView compassDownImage = view.findViewById(R.id.compasDown);
        textView = view.findViewById(R.id.textView2);
        textView2 = view.findViewById(R.id.textView3);
        imageView = view.findViewById(R.id.arrow_guitarpick);
        context = getContext();
        tuning = Tuning.getTuning(context, Preferences.getString(context, getString(R.string.pref_tuning_key), getString(R.string.standard_tuning_val)));


        soundMete = new SoundMeter(context, getActivity());
        soundMete.init();
        soundMete.setPitchDetectionListerer(new SoundMeter.PitchDetectionListerer() {
            @Override
            public void onPitchDetected(float freq, double avgIntensity) {
                final int index = tuning.closestPitchIndex(freq);
                final Pitch pitch = tuning.pitches[index];
                double interval = 1200 * Utils.log2(freq / pitch.frequency); // interval in cents
                final float needlePos = (float) (interval / 100);
                final boolean goodPitch = Math.abs(interval) < 5.0;
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        // Stuff that updates the UI

                        int realFreq = 0;
                        if(freq - pitch.frequency > -1.5 && freq - pitch.frequency < 1.5){
                            realFreq = 0;
                        }else{
                            realFreq = (int)(freq - pitch.frequency + 1);
                        }
                        
                        if(freq > 60 && freq < 96.205){
                            imageView.setRotation(300 + realFreq * 2);
                            if(realFreq == 0){
                                compassUp[0] = 1;
                                changeCompass(compassUpImage, compassUp, true);
                            }
                        } else if (freq < 128.415) {
                            imageView.setRotation(240 + realFreq * 2);
                            if(realFreq == 0){
                                compassDown[0] = 1;
                                changeCompass(compassDownImage, compassDown, false);
                            }
                        } else if (freq < 171.415) {
                            imageView.setRotation(180 + realFreq * 2);
                            if(realFreq == 0){
                                compassDown[1] = 1;
                                changeCompass(compassDownImage, compassDown, false);
                            }
                        } else if (freq < 221.47) {
                            imageView.setRotation(120 + realFreq * 2);
                            if(realFreq == 0){
                                compassDown[2] = 1;
                                changeCompass(compassDownImage, compassDown, false);
                            }
                        } else if (freq < 288.285) {
                            imageView.setRotation(60 + realFreq *2 );
                            if(realFreq == 0){
                                compassUp[2] = 1;
                                changeCompass(compassUpImage, compassUp, true);
                            }
                        } else if (freq < 350) {
                            imageView.setRotation(realFreq *2);
                            if(realFreq == 0){
                                compassUp[1] = 1;
                                changeCompass(compassUpImage, compassUp, true);
                            }
                        }

                        textView.setText(pitch.name);

                        textView2.setText("" + realFreq);
                        Log.d(TAG,"Now  : " + realFreq);
                        Log.d(TAG,"Pitch  : " + pitch.frequency);
                    }
                });


                pitchIndex = index;
                lastFreq = freq;
            }

        });
        mExecutor.execute(soundMete);


        return view;
    }

    private void changeCompass(ImageView compass, int[] compassInt, boolean i){
        if(i){
            if(compassInt[0] == 1){
                if(compassInt[1] == 1){
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass17));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass14));
                    }
                }else{
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass15));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass11));
                    }
                }
            }else{
                if(compassInt[1] == 1){
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass16));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass12));
                    }
                }else{
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass13));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass1));
                    }
                }
            }
        }else{
            if(compassInt[0] == 1){
                if(compassInt[1] == 1){
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass27));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass24));
                    }
                }else{
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass26));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass21));
                    }
                }
            }else{
                if(compassInt[1] == 1){
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass25));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass22));
                    }
                }else{
                    if(compassInt[2] == 1){
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass23));
                    }else{
                        compass.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.compass2));
                    }
                }
            }
        }

    }
}

package com.zawisza.guitar_app.fragments.GuitarPick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zawisza.guitar_app.R;

import java.util.Timer;
import java.util.TimerTask;
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"Open GuitarPickFragment");

        View view = inflater.inflate(R.layout.fragment_guitarpick, container, false);
        textView = view.findViewById(R.id.textView2);
        textView2 = view.findViewById(R.id.textView3);
        imageView = view.findViewById(R.id.imageView3);
        context = getContext();
        tuning = Tuning.getTuning(context, Preferences.getString(context, getString(R.string.pref_tuning_key), getString(R.string.standard_tuning_val)));
        soundMete = new SoundMeter(context);
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
                        imageView.setRotation(90);
                        textView.setText(pitch.name);
                        textView2.setText("" + ((int)(freq - pitch.frequency + 1)));
                        Log.d(TAG,"Now  : " + freq);
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
}

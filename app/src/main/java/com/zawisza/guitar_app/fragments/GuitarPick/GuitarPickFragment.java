package com.zawisza.guitar_app.fragments.GuitarPick;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.zawisza.guitar_app.R;

import java.util.Timer;
import java.util.TimerTask;

public class GuitarPickFragment extends Fragment{

    private static final String TAG = "Guitar-Master - GuitarPickFragment";

    private Context context;
    private SoundMeter soundMete;
    public static Timer t1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"Open GuitarPickFragment");
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_guitarpick, container, false);
        soundMete = new SoundMeter(context);
        soundMete.start();
        t1 = new Timer();
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "SoundMete Amplitude : " + soundMete.getAmplitude());
            }
        },0, 100);

        return view;
    }
}

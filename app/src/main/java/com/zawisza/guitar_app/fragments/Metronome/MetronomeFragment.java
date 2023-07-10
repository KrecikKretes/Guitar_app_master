package com.zawisza.guitar_app.fragments.Metronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;

import com.zawisza.guitar_app.R;

import java.util.Timer;
import java.util.TimerTask;

public class MetronomeFragment extends Fragment {

    private static final String TAG = "GuitarApp - MetronomeFragment";

    private NumberPicker numberPicker;
    private Button startButton;
    private Button stopButton;
    private Button lampButton1, lampButton2, lampButton3, lampButton4;

    private Timer t1;
    private MediaPlayer mp1, mp2;
    private long time;
    private Boolean isTimerOn = false;

    private SharedPreferences saved_values;
    private int lamps[] = {0,0,0,0};
    int number_lamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_metronome, container, false);

        Context context = getContext();

        numberPicker = view.findViewById(R.id.metronome_fragment_numberpicker);
        startButton = view.findViewById(R.id.metronome_fragment_button_start);
        stopButton = view.findViewById(R.id.metronome_fragment_button_stop);
        lampButton1 = view.findViewById(R.id.metronome_fragment_button_lamp);
        lampButton2 = view.findViewById(R.id.metronome_fragment_button_lamp2);
        lampButton3 = view.findViewById(R.id.metronome_fragment_button_lamp3);
        lampButton4 = view.findViewById(R.id.metronome_fragment_button_lamp4);

        mp1 = MediaPlayer.create(getActivity(), R.raw.click);
        mp2 = MediaPlayer.create(getActivity(), R.raw.click);
        saved_values = PreferenceManager.getDefaultSharedPreferences(context);

        numberPickerOption();

        setListeners();

        return view;
    }

    private void setListeners() {
        numberPicker.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            SharedPreferences.Editor editor=saved_values.edit();
            editor.putInt("Metronome_value",numberPicker.getValue());
            editor.apply();


            time = 60000 / numberPicker.getValue();
            if(isTimerOn){
                offTimer();
                t1 = new Timer();
                onTimer(t1,lamps);
            }

        });

        startButton.setOnClickListener(view -> {
            isTimerOn = true;

            Log.d(TAG,"Click start button");
            t1 = new Timer();

            onTimer(t1,lamps);
        });

        stopButton.setOnClickListener(view -> {
            isTimerOn=false;
            offTimer();
        });

        lampButton1.setOnClickListener(view -> {
            if(lamps[0] == 2){
                lamps[0] = 0;
            }else{
                lamps[0]++;
            }
        });

        lampButton2.setOnClickListener(view -> {
            if(lamps[1] == 2){
                lamps[1] = 0;
            }else{
                lamps[1]++;
            }
        });

        lampButton3.setOnClickListener(view -> {
            if(lamps[2] == 2){
                lamps[2] = 0;
            }else{
                lamps[2]++;
            }
        });

        lampButton4.setOnClickListener(view -> {
            if(lamps[3] == 2){
                lamps[3] = 0;
            }else{
                lamps[3]++;
            }
        });
    }

    private void numberPickerOption(){
        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(1);
        numberPicker.setValue(saved_values.getInt("Metronome_value", 1));

        time = 60000 / numberPicker.getValue();
    }

    private void onTimer(Timer t, int lamp[]){
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if(lamp[number_lamp] == 0){
                    mp1.start();
                }
                if(lamp[number_lamp] == 1 ) {
                    mp2.start();
                }
                if(number_lamp == 3){
                    number_lamp=0;
                }else{
                    number_lamp++;
                }
                Log.d(TAG,"T" + number_lamp + " : " + time);
            }
        }, 0, (long) time);
    }

    private void offTimer(){
        number_lamp = 0;
        t1.cancel();
    }
}

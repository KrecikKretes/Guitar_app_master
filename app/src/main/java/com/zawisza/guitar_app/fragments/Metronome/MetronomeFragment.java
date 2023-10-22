package com.zawisza.guitar_app.fragments.Metronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.zawisza.guitar_app.R;

import java.util.Timer;
import java.util.TimerTask;

public class MetronomeFragment extends Fragment {

    private static final String TAG = "Guitar-Master - MetronomeFragment";

    private NumberPicker numberPicker;
    private Button startButton;
    private Button stopButton;
    private ImageButton lampButton1, lampButton2, lampButton3, lampButton4;

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
        mp2 = MediaPlayer.create(getActivity(), R.raw.click2);
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
            click(0, lampButton1);
        });

        lampButton2.setOnClickListener(view -> {
            click(1, lampButton2);
        });

        lampButton3.setOnClickListener(view -> {
            click(2, lampButton3);
        });

        lampButton4.setOnClickListener(view -> {
            click(3, lampButton4);
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
                    Log.d(TAG,"T" + number_lamp + " : mp1 \n" + time);
                    mp1.start();
                }else{
                    if(lamp[number_lamp] == 1 ) {
                        Log.d(TAG,"T" + number_lamp + " : mp2 \n" + time);
                        mp2.start();
                    }
                }

                if(number_lamp == 3){
                    number_lamp=0;
                }else{
                    number_lamp++;
                }
                //Log.d(TAG,"T" + number_lamp + " : " + time);
            }
        }, 0, (long) time);
    }

    private void offTimer(){
        number_lamp = 0;
        t1.cancel();
    }

    private void click(int lamp, ImageButton button){
        if(lamps[lamp] == 2){
            lamps[lamp] = 0;
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green));
        }else{
            lamps[lamp]++;
            if(lamps[lamp] == 1){
                button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.yellow));
            }else{
                button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.red));
            }
        }
    }
}

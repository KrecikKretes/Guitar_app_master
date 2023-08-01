package com.zawisza.guitar_app.fragments.GuitarPick;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Timer;
import java.util.TimerTask;

public class SoundMeter implements Runnable {

    private String TAG = "Guitar-Master - SoundMeter";
    private static final int[] SAMPLE_RATES = {44100, 22050, 16000, 11025, 8000};

    public interface PitchDetectionListerer{
        void onPitchDetected(float freq, double avgIntensity);
    }

    float lastComputedFreq = 0;
    private AudioRecord ar;
    private PitchDetectionListerer pitchDetectionListerer;

    public void setPitchDetectionListerer(PitchDetectionListerer pitchDetectionListerer1){
        pitchDetectionListerer = pitchDetectionListerer1;
    }


    private Context context;

    public SoundMeter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }


    private int minSize;
    public static Timer t1;




    public void init() {
        int bufSize = 16384;
        int avalaibleSampleRates = SAMPLE_RATES.length;
        int i = 0;
        do {
            int sampleRate = SAMPLE_RATES[i];
            int minBufSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            if (minBufSize != AudioRecord.ERROR_BAD_VALUE && minBufSize != AudioRecord.ERROR) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                ar = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, Math.max(bufSize, minBufSize * 4));
            }
            i++;
        }
        while (i < avalaibleSampleRates && (ar == null || ar.getState() != AudioRecord.STATE_INITIALIZED));
    }

    @Override
    public void run() {
        Log.d(TAG, "sampleRate= "+ar.getSampleRate());

        if(ar.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord not initialized");
        }
        ar.startRecording();
        int bufSize = 8192;
        short[] buffer = new short[bufSize];
        int sampleRate = ar.getSampleRate();

        t1 = new Timer();

        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                int read = ar.read(buffer, 0, bufSize);
                if (read > 0) {
                    double intensity = averageIntensity(buffer, read);
                    int maxZeroCrossing = (int) (250 * (read / bufSize) * (sampleRate / 44100.0));
                    if (intensity >= 50 && zeroCrossingCount(buffer) <= maxZeroCrossing) {
                        float freq = getPitch(buffer, read / 4, read, sampleRate, 50, 500);
                        if (Math.abs(freq - lastComputedFreq) <= 5f) {
                            pitchDetectionListerer.onPitchDetected(freq, intensity);
                        }
                        lastComputedFreq = freq;
                    }
                }
            }
        },0, 400);
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for (short s : buffer)
        {
            if (Math.abs(s) > max)
            {
                max = Math.abs(s);
            }
        }
        return max;
    }




    private float getPitch(short[] data, int windowSize, int frames, int sampleRate, int minFreq, int maxFreq) {
        float maxOffset = sampleRate /minFreq;
        float minOffset = sampleRate /maxFreq;

        int minSum = Integer.MAX_VALUE;
        int minSumLag = 0;

        for(int lag = (int) minOffset; lag <= maxOffset; lag++){
            int sum = 0;
            for(int i =0; i< windowSize; i++){
                int oldIndex = i - lag;
                int sample = ((oldIndex <0) ? data[frames + oldIndex] : data[oldIndex]);

                sum += Math.abs(sample - data[i]);
            }
            if(sum < minSum){
                minSum = sum;
                minSumLag = lag;
            }
        }
        return sampleRate / minSumLag;
    }

    private int zeroCrossingCount(short[] data) {
        int len = data.length;
        int count = 0;
        boolean prevValPositive = data[0] >= 0;
        for(int i = 1; i < len; i++){
            boolean positive = data[i] >= 0;
            if( prevValPositive != positive){
                count++;
            }
            prevValPositive = positive;
        }
        return count;
    }

    private double averageIntensity(short[] data, int frames) {
        double sum = 0;
        for(int i = 0; i < frames; i++){
            sum +=Math.abs(data[i]);
        }
        return sum / frames;
    }
}
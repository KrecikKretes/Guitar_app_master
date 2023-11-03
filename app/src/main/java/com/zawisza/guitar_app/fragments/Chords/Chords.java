package com.zawisza.guitar_app.fragments.Chords;

import android.support.annotation.NonNull;

public class Chords {

    private int no;
    private String chord;
    private String code;
    private String ton;

    public Chords() {
    }

    public Chords(int no, String chord, String code, String ton) {
        this.no = no;
        this.chord = chord;
        this.code = code;
        this.ton = ton;
    }

    public int getNo() {
        return no;
    }

    public String getChord() {
        return chord;
    }

    public String getCode() {
        return code;
    }

    public String getTon() {
        return ton;
    }

    @Override
    public String toString() {
        return "Chords{" +
                "chord='" + chord + '\'' +
                ", code='" + code + '\'' +
                ", ton='" + ton + '\'' +
                '}';
    }
}

package com.zawisza.guitar_app.fragments.Songbook;

import androidx.annotation.NonNull;

import com.google.firebase.database.PropertyName;

public class Songbook implements Comparable<Songbook>{

    private int no;
    private String title;
    private String accords;
    private boolean tabs;

    public Songbook() {
    }

    public Songbook(int no, String title, String accords, boolean tabs) {
        this.no = no;
        this.title = title;
        this.accords = accords;
        this.tabs = tabs;
    }
    

    public int getNo() {
        return no;
    }

    public String getTitle() {
        return title;
    }

    public String getAccords() {
        return accords;
    }

    @PropertyName("tabs")
    public boolean isTabs() {
        return tabs;
    }

    // overriding the compareTo method of Comparable class
    @Override public int compareTo(Songbook comparestu) {
        int comparenumber
                = ((Songbook)comparestu).getNo();

        //  For Ascending order
        return this.no - comparenumber;

        // For Descending order do like this
        // return compareage-this.studentage;
    }

    @NonNull
    @Override
    public String toString() {
        return "Title : " + title + "\nNo : " + no + "\nAccords : " + accords + "\nisTabs : " + String.valueOf(tabs);
    }
}



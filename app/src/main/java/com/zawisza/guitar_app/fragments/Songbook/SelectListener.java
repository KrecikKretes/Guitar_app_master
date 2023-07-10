package com.zawisza.guitar_app.fragments.Songbook;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public interface SelectListener {
    void onItemClick(TextView[] textView, ImageView imageView, ViewSwitcher viewSwitcher);

}

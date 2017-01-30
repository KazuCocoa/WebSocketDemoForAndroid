package com.kazucocoa.websocketdemoforandroid;

import android.widget.TextView;

public interface MainActivityView {
    void addText(TextView view, String data);
    void setText(TextView view, String data);
}

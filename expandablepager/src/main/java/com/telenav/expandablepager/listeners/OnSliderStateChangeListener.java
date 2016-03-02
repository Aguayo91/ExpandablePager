package com.telenav.expandablepager.listeners;

import android.view.View;

/**
 * Created by dima on 02/09/15.
 */
public interface OnSliderStateChangeListener {
    void onStateChanged(View page, int state);
    void onPageChanged(View page, int state);
}

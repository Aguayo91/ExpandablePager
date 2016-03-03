package com.telenav.expandablepager.listeners;

import android.view.View;

import com.telenav.expandablepager.SlidingContainer;

/**
 * Listener for slider state change events
 */
public interface OnSliderStateChangeListener {
    /**
     * Fires when slider state changes as a result of a slide. Ex: change from expanded state to collapsed state.
     * @param page view linked with the page whose state being changed
     * @param state new slider state
     */
    void onStateChanged(View page,@SlidingContainer.SliderState int state);

    /**
     * Fires when the state changes for a page different from the current page (most often the next page offscreen) or when the view size changes
     * @param page view linked with the page whose state being changed
     * @param state new slider state
     */
    void onPageChanged(View page,@SlidingContainer.SliderState int state);
}

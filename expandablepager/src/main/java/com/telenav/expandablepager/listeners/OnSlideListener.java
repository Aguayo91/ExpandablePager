package com.telenav.expandablepager.listeners;


/**
 * Listens for slide events.
 */
public interface OnSlideListener {
    /**
     * Fires each time the SlidingContainer is moving.
     * @param amount represents the current translationY of the container
     */
    void onSlide(float amount);
}

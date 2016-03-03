package com.telenav.expandablepager.listeners;

import java.util.List;

/**
 * Listener for ViewPager page selected events
 */
public interface OnItemSelectedListener {
    /**
     * Fires each time a page from the ViewPages is selected
     * @param items list of adapter items
     * @param index index of the selected item
     */
    void onItemSelected(List<?> items, int index);
}

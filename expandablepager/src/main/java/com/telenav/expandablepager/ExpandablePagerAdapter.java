package com.telenav.expandablepager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * POI details page adapter
 * Created by Dmitri on 08/05/2015.
 */
public class ExpandablePagerAdapter<T> extends PagerAdapter {

    protected List<T> items;

    public ExpandablePagerAdapter(List<T> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    protected View attach(ViewGroup container, View v, int position) {
        v.setId(ExpandablePager.INTERNAL_PAGE_ID + position);
        container.addView(v);
        return v;
    }
}

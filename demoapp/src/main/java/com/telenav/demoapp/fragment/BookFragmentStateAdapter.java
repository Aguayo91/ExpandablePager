package com.telenav.demoapp.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.telenav.demoapp.Book;
import com.telenav.expandablepager.adapter.ExpandableFragmentStatePagerAdapter;

import java.util.List;

public class BookFragmentStateAdapter extends ExpandableFragmentStatePagerAdapter<Book> {


    public BookFragmentStateAdapter(FragmentManager fm, List<Book> items) {
        super(fm, items);
    }

    @Override
    public Fragment getItem(int position) {
        return BookFragment.init(items.get(position));
    }
}

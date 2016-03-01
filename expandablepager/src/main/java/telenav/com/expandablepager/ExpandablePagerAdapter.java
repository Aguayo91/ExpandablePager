package telenav.com.expandablepager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import telenav.com.expandablepager.listeners.ViewTracker;

/**
 * POI details page adapter
 * Created by Dmitri on 08/05/2015.
 */
public class ExpandablePagerAdapter<T> extends PagerAdapter implements ViewTracker {

    protected List<T> items;

    private int clickToExpandId;

    private View.OnClickListener listener;

    public ExpandablePagerAdapter(List<T> items) {
        this.items = items;
    }

    void setClickToExpandId(View.OnClickListener listener, int clickToExpandId) {
        this.clickToExpandId = clickToExpandId;
        this.listener = listener;
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

    @Override
    public void register(View v, int position) {
        v.setId(ExpandablePager.INTERNAL_PAGE_ID + position);
        if (v.findViewById(clickToExpandId) != null)
            v.findViewById(clickToExpandId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onClick(v);
                }
            });
    }
}

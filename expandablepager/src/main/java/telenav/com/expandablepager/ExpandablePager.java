package telenav.com.expandablepager;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import telenav.com.expandablepager.listeners.OnItemSelectedListener;
import telenav.com.expandablepager.listeners.OnSliderStateChangeListener;

/**
 * Created by Dmitri on 06/05/2015.
 */
public class ExpandablePager extends SlidingContainer {

    public static final byte MODE_REGULAR = 0, MODE_COMPACT = 1, MODE_FIXED = 2;
    static final int INTERNAL_PAGE_ID = 12345;

    private ViewPager mPager;

    private float sliderStateThreshold;

    private OnItemSelectedListener onItemSelectedListener;

    private OnSliderStateChangeListener onSliderStateChangeListener;

    private int sliderState = STATE_COLLAPSED;

    private byte sliderMode = MODE_COMPACT;

    private float historicY;


    private int expandedHeight = 1800;

    private int collapsedHeight;

    private int clickToExpandId;

    private View.OnClickListener listener;

    public ExpandablePager(Context context) {
        super(context);
    }

    public ExpandablePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SliderState
    public int getSliderState() {
        return sliderState;
    }

    /**
     * Animates the container to the selected state.
     *
     * @param state - STATE_COLLAPSED, STATE_EXPANDED, STATE_HIDDEN
     */
    public void setSliderState(@SliderState int state) {
        sliderState = state;
        if (mPager.getAdapter().getCount() > 0)
            animateToState(state);
    }

    public void setHeights(int expanded, int collapsed) {
        expandedHeight = expanded;
        collapsedHeight = collapsed;
        sliderStateThreshold = expandedHeight / 2;
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return super.getLayoutParams();
    }

    @SliderMode
    public byte getSlideMode() {
        return sliderMode;
    }

    /**
     * 1 - full screen
     * 0 - compact
     *
     * @param mode
     */
    private void setSliderMode(@SliderMode byte mode) {
        switch (mode) {
            case MODE_COMPACT: // default buttons
                sliderMode = MODE_COMPACT;
                getLayoutParams().height = expandedHeight;
                setSlideValues((float) expandedHeight - collapsedHeight);
                //sliderStateThreshold = cSlidePoint;
                break;
            case MODE_REGULAR: // full screen
                sliderMode = MODE_REGULAR;
                getLayoutParams().height = ViewPager.LayoutParams.MATCH_PARENT;
                setSlideValues((float) ((ViewGroup) getParent()).getHeight());
                //sliderStateThreshold = rSlidePoint;
                break;
            case MODE_FIXED:
                sliderMode = MODE_FIXED;
                getLayoutParams().height = expandedHeight;
                setSlideValues((float) expandedHeight - collapsedHeight);
                break;
        }
        enableSlide(mode != MODE_FIXED);
    }

    public void setClickToExpand(int id) {
        listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateToState(STATE_EXPANDED);
            }
        };
        clickToExpandId = id;
    }

    public void setCurrentItem(int index) {
        mPager.setCurrentItem(index, false);
    }

    public void updateAdapter(ExpandablePagerAdapter adapter) {
        int index = mPager.getCurrentItem();
        setPagerAdapter(adapter);
        mPager.setCurrentItem(Math.min(index, adapter.getCount() - 1));
        if (onSliderStateChangeListener != null) {
            onSliderStateChangeListener.onPageChanged(getPage(mPager.getCurrentItem()), sliderState);
        }
    }

    public void init(ExpandablePagerAdapter adapter) {
        mPager = new ViewPager(getContext());
        mPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mPager);
        setPagerAdapter(adapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            boolean change = true;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (change) {
                    if (onSliderStateChangeListener != null) {
                        onSliderStateChangeListener.onPageChanged(getPage(mPager.getCurrentItem() + (position < mPager.getCurrentItem() ? -1 : 1)), sliderState);
                        change = !change;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (onItemSelectedListener != null)
                    onItemSelectedListener.onItemSelected(((ExpandablePagerAdapter) mPager.getAdapter()).items, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                change = true;
            }
        });
        if (expandedHeight > 0) {
            setSliderMode(sliderMode);
            setTranslationY(expandedHeight - collapsedHeight);
            setVisibility(View.VISIBLE);
            setState(sliderState);
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (onSliderStateChangeListener != null) {
                    onSliderStateChangeListener.onPageChanged(getPage(mPager.getCurrentItem()), sliderState);
                }
            }
        });
    }

    private void setPagerAdapter(ExpandablePagerAdapter adapter) {
        adapter.setClickToExpandId(listener, clickToExpandId);
        mPager.setAdapter(adapter);
    }

    public void setOnSliderStateChangeListener(OnSliderStateChangeListener onSliderStateChangeListener) {
        this.onSliderStateChangeListener = onSliderStateChangeListener;
    }

    private View getPage(int position) {
        return findViewById(INTERNAL_PAGE_ID + position);
    }

    @Override
    protected void notifySlideEvent(float yPosition) {
        super.notifySlideEvent(yPosition);
        if (historicY <= sliderStateThreshold && yPosition >= sliderStateThreshold) {
            //down
            if (sliderState != STATE_HIDDEN)
                sliderState = STATE_COLLAPSED;
            if (onSliderStateChangeListener != null) {
                onSliderStateChangeListener.onStateChanged(getPage(mPager.getCurrentItem()), sliderState);
            }
        } else if (historicY >= sliderStateThreshold && yPosition < sliderStateThreshold) {
            //up
            sliderState = STATE_EXPANDED;
            if (onSliderStateChangeListener != null) {
                onSliderStateChangeListener.onStateChanged(getPage(mPager.getCurrentItem()), sliderState);
            }
        }
        historicY = yPosition;
    }

    @Override
    protected void onSettled(int slideValueIndex) {
    }

    public void setCurrentPage(int page) {
        if (page < mPager.getAdapter().getCount())
            mPager.setCurrentItem(page);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.sliderState = sliderState;
        ss.sliderMode = sliderMode;

        return ss;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        switch (sliderState) {
            case STATE_COLLAPSED:
                historicY = h - collapsedHeight;
                break;
            case STATE_EXPANDED:
                historicY = 0;
                break;
            case STATE_HIDDEN:
                historicY = h;
                break;
        }
        setState(sliderState);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        sliderState = ss.sliderState;
        sliderMode = ss.sliderMode;
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_REGULAR, MODE_COMPACT, MODE_FIXED})
    public @interface SliderMode {
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        int sliderState;
        byte sliderMode;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.sliderState = in.readInt();
            this.sliderMode = in.readByte();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.sliderState);
            out.writeByte(this.sliderMode);
        }
    }
}

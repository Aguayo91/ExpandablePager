package com.telenav.expandablepager;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.telenav.expandablepager.listeners.OnItemSelectedListener;
import com.telenav.expandablepager.listeners.OnSliderStateChangeListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Layout that contains a ViewPager and can slide vertically between 2 states (expanded and collapsed). Should be aligned to the bottom of the screen.
 */
public class ExpandablePager extends SlidingContainer {

    public static final byte MODE_REGULAR = 0, MODE_FIXED = 1;
    static final int INTERNAL_PAGE_ID = 12345;

    private ViewPager mPager;

    private float sliderStateThreshold;

    private OnItemSelectedListener onItemSelectedListener;

    private OnSliderStateChangeListener onSliderStateChangeListener;

    private int sliderState = STATE_COLLAPSED;

    private byte sliderMode = MODE_REGULAR;

    private float historicY;

    private int collapsedHeight = (int) (80 * getResources().getDisplayMetrics().density);

    public ExpandablePager(Context context) {
        super(context);
        init();
    }

    public ExpandablePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandablePager, 0, 0);
        setAnimationDuration(a.getInt(R.styleable.ExpandablePager_animation_duration, 200));
        collapsedHeight = (int) a.getDimension(R.styleable.ExpandablePager_collapsed_height, 80 * getResources().getDisplayMetrics().density);
        a.recycle();
    }

    private void init() {
        mPager = new ViewPager(getContext());
        mPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        addView(mPager);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pinToBottom();
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

    /**
     * Move the layout to the bottom of the screen in case it was not moved in the xml file
     */
    private void pinToBottom() {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null) {
            if (params instanceof RelativeLayout.LayoutParams) {
                ((LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            } else if (params instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) params).gravity = Gravity.BOTTOM;
            } else if (params instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) params).gravity = Gravity.BOTTOM;
            }
        }
    }

    @SliderState
    public int getSliderState() {
        return sliderState;
    }

    /**
     * Animates the container to the selected state.
     * @param state - available value are: STATE_COLLAPSED, STATE_EXPANDED, STATE_HIDDEN
     */
    @Override
    public boolean animateToState(@SliderState int state) {
        sliderState = state;
        return mPager.getAdapter().getCount() > 0 && super.animateToState(state);
    }

    /**
     * Set the height of the pager in the collapsed state.
     * @param collapsed collapsed height in pixels
     */
    public void setCollapsedHeight(int collapsed) {
        collapsedHeight = collapsed;
    }

    /**
     * @return current slider mode
     */
    @SliderMode
    public byte getMode() {
        return sliderMode;
    }

    /**
     * Set slider mode
     */
    public void setMode(@SliderMode byte mode) {
        sliderMode = mode;
        if (mode == MODE_FIXED)
            setSliderMode(MODE_FIXED);
    }

    private void setSliderMode(@SliderMode byte mode) {
        switch (mode) {
            case MODE_REGULAR: // full screen
                int height = getHeight();
                sliderStateThreshold = height / 2;
                sliderMode = MODE_REGULAR;
                setStopValues((float) height - collapsedHeight);
                break;
            case MODE_FIXED:
                sliderStateThreshold = Integer.MAX_VALUE;
                sliderMode = MODE_FIXED;
                getLayoutParams().height = collapsedHeight;
                setStopValues(0f);
                break;
        }
        enableSlide(mode != MODE_FIXED);
    }

    public void setCurrentItem(int index, boolean smoothScroll) {
        mPager.setCurrentItem(index, smoothScroll);
    }

    public void setAdapter(ExpandablePagerAdapter adapter) {
        int index = mPager.getCurrentItem();
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(Math.min(index, adapter.getCount() - 1));
        mPager.post(new Runnable() {
            @Override
            public void run() {
                if (onSliderStateChangeListener != null) {
                    onSliderStateChangeListener.onPageChanged(getPage(mPager.getCurrentItem()), sliderState);
                }
            }
        });
    }

    public void setOnSliderStateChangeListener(OnSliderStateChangeListener onSliderStateChangeListener) {
        this.onSliderStateChangeListener = onSliderStateChangeListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
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

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.sliderState = sliderState;
        ss.sliderMode = sliderMode;
        if (mPager != null)
            ss.currentIndex = mPager.getCurrentItem();

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
        if (sliderMode == MODE_REGULAR) {
            setSliderMode(sliderMode);
        }
        setState(sliderState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        sliderState = ss.sliderState;
        sliderMode = ss.sliderMode;
        if (mPager != null)
            mPager.setCurrentItem(ss.currentIndex);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_REGULAR, MODE_FIXED})
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
        int currentIndex;
        int sliderState;
        byte sliderMode;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentIndex = in.readInt();
            this.sliderState = in.readInt();
            this.sliderMode = in.readByte();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.currentIndex);
            out.writeInt(this.sliderState);
            out.writeByte(this.sliderMode);
        }
    }
}

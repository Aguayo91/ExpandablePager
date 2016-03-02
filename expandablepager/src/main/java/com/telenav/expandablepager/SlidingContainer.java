package com.telenav.expandablepager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.telenav.expandablepager.listeners.OnSlideListener;

/**
 * Created by Dmitri on 10/11/2014.
 */
public class SlidingContainer extends RelativeLayout {

    public static final int STATE_COLLAPSED = 0, STATE_EXPANDED = 1, STATE_HIDDEN = -1;
    private static final int SLIDE_THRESHOLD_DIPS = 20;
    private final float DEFAULT_SLIDE_THRESHOLD;
    private float slideThreshold;
    private int viewHeight;
    private List<Float> slideValues = new ArrayList<>();
    private int slideValueIndex = 0;
    private float startYCoordinate;
    private float touchDelta;
    private float translated = 0;
    private OnSlideListener slideListener;
    private int duration = 200;

    public SlidingContainer(Context context) {
        super(context);
        slideThreshold = context.getResources().getDisplayMetrics().density * SLIDE_THRESHOLD_DIPS;
        DEFAULT_SLIDE_THRESHOLD = slideThreshold;
    }

    public SlidingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        slideThreshold = context.getResources().getDisplayMetrics().density * SLIDE_THRESHOLD_DIPS;
        DEFAULT_SLIDE_THRESHOLD = slideThreshold;
    }

    public void setSlideListener(OnSlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public void setAnimationDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (viewHeight == 0) {
            viewHeight = h;
            Iterator<Float> iter = slideValues.iterator();
            while (iter.hasNext()) {
                Float i = iter.next();
                if (i >= viewHeight || i < 0)
                    iter.remove();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !resize(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        resize(event);
        return true;
    }

    public void enableSlide(boolean enable) {
        slideThreshold = enable ? DEFAULT_SLIDE_THRESHOLD : Integer.MAX_VALUE;
    }

    private boolean resize(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        int stepSize = slideValues.size();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                startYCoordinate = ev.getRawY();
                translated = 0;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                touchDelta = (startYCoordinate - ev.getRawY());
                if (Math.abs(touchDelta) > slideThreshold) {
                    float startingPointY, nextPointY, maxDiff, tempDelta, auxDelta = 0;
                    tempDelta = touchDelta + (touchDelta < 0 ? 1 : -1) * slideThreshold;
                    startingPointY = slideValues.get(slideValueIndex);
                    if (!isUpwardGesture() && slideValueIndex >= 1) {
                        nextPointY = slideValues.get(slideValueIndex - 1);
                        maxDiff = nextPointY - slideValues.get(slideValueIndex);
                        auxDelta = Math.min(-tempDelta, maxDiff);
                    } else if (isUpwardGesture() && slideValueIndex < stepSize - 1) {
                        nextPointY = slideValues.get(slideValueIndex + 1);
                        maxDiff = nextPointY - slideValues.get(slideValueIndex);
                        auxDelta = Math.max(-tempDelta, maxDiff);
                    }
                    float preTranslated = translated;
                    translated = startingPointY + auxDelta;
                    setTranslationY(translated);
                    if (preTranslated != translated)
                        notifySlideEvent(translated);
                    return false;
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                if (Math.abs(touchDelta) > slideThreshold) {
                    if (!isUpwardGesture() && slideValueIndex > 0)
                        slideValueIndex--;
                    else if (isUpwardGesture() && slideValueIndex < stepSize - 1)
                        slideValueIndex++;
                    if (!slideValues.contains(translated)) {
                        animate(slideValues.get(slideValueIndex));
                    } else
                        onSettled(slideValueIndex);
                    startYCoordinate = -1;
                    touchDelta = 0;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }
        }
        return true;
    }

    protected void onSettled(int slideValueIndex) {
    }

    private boolean isUpwardGesture() {
        return touchDelta > 0;
    }

    private void animate(float amount) {
        animate(amount, duration, new LinearInterpolator());
    }

    private void animate(float amount, int duration) {
        animate(amount, duration, new FastOutSlowInInterpolator());
    }

    private void animate(final float amount, int duration, Interpolator interpolator) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, amount)
                .setDuration(duration);
        oa.setInterpolator(interpolator);
        oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                notifySlideEvent(Math.round(((Float) animation.getAnimatedValue())));
            }
        });
        oa.addListener(new CustomAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                onSettled(slideValueIndex);
            }
        });
        oa.start();
    }

    public List<Float> getSlideValues() {
        return slideValues;
    }

    /**
     * Stops sliding at the specified values. Slide value is subtracted from slider height
     *
     * @param slideValues
     */
    public void setSlideValues(Float... slideValues) {
        SortedSet<Float> s = new TreeSet<>(Collections.reverseOrder());
        s.addAll(Arrays.asList(slideValues));
        this.slideValues.clear();
        this.slideValues.addAll(s);
        this.slideValues.add(0f);
        slideValueIndex = 0;
    }

    public Float getCurrentSlideValue() {
        return slideValues.get(slideValueIndex);
    }

    /**
     * Sets the container position according to the provided state. The change is immediate.
     */
    public boolean setState(@SliderState int state) {
        if (!slideValues.isEmpty())
            switch (state) {
                case STATE_COLLAPSED:
                    setTranslationY(slideValues.get(0));
                    slideValueIndex = 0;
                    return true;
                case STATE_EXPANDED:
                    setTranslationY(0);
                    slideValueIndex = slideValues.size() - 1;
                    return true;
                case STATE_HIDDEN:
                    setTranslationY(getHeight());
                    return true;
            }
        return false;
    }

    public boolean animateToState(@SliderState int toState) {
        return animateToState(toState, duration);
    }

    /**
     * Animates the container to the selected state.
     */
    public boolean animateToState(@SliderState int toState, int duration) {
        if (!slideValues.isEmpty()) {
            switch (toState) {
                case STATE_COLLAPSED:
                    animate(slideValues.get(0), duration);
                    slideValueIndex = 0;
                    return true;
                case STATE_EXPANDED:
                    animate(0, duration);
                    slideValueIndex = slideValues.size() - 1;
                    return true;
                case STATE_HIDDEN:
                    animate(getHeight(), duration);
                    return true;
            }
        }
        return false;
    }

    protected void notifySlideEvent(float yPosition) {
        if (slideListener != null) {
            slideListener.onSlide(yPosition);
        }
    }

    public
    @SliderState
    int getState() {
        int translation = (int) getTranslationY();
        if (translation == 0)
            return STATE_EXPANDED;
        else if (translation == viewHeight)
            return STATE_HIDDEN;
        else if (slideValues.size() >= 2 && translation == slideValues.get(slideValues.size() - 2))
            return STATE_COLLAPSED;
        return STATE_COLLAPSED;//check this later
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_COLLAPSED, STATE_EXPANDED, STATE_HIDDEN})
    public @interface SliderState {
    }

    private static class CustomAnimationListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}
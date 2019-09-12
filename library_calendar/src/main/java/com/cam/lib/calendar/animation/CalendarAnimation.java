package com.cam.lib.calendar.animation;

import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.cam.lib.calendar.state.CalendarState;
import com.cam.lib.calendar.view.CalendarLayout;

/**
 * 日历动画
 */
public class CalendarAnimation extends Animation {

    private CalendarLayout mCalendarLayout;
    private CalendarState mState;
    private float mDistanceY;

    public CalendarAnimation(CalendarLayout calendarLayout, CalendarState state, float distanceY) {
        mCalendarLayout = calendarLayout;
        mState = state;
        mDistanceY = distanceY;
        setDuration(200);
        setInterpolator(new DecelerateInterpolator(1.5f));
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (mState == CalendarState.EXPAND) {
            mCalendarLayout.onCalendarScroll(mDistanceY);
        } else {
            mCalendarLayout.onCalendarScroll(-mDistanceY);
        }
    }

}

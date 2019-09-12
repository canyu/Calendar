package com.cam.lib.calendar.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.cam.lib.calendar.view.CalendarLayout;

/**
 * 监听日历滑动
 */
public class OnCalendarScrollListener extends GestureDetector.SimpleOnGestureListener {

    private CalendarLayout mCalendarLayout;

    public OnCalendarScrollListener(CalendarLayout calendarLayout) {
        mCalendarLayout = calendarLayout;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mCalendarLayout.onCalendarScroll(distanceY);
        return true;
    }
}
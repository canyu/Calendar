package com.cam.lib.calendar.pager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.cam.lib.calendar.adapter.RangeBaseAdapter;
import com.cam.lib.calendar.adapter.WeekAdapter;
import com.cam.lib.calendar.listener.InterfaceGetRangeView;
import com.cam.lib.calendar.listener.InterfaceGetSelectingDay;
import com.cam.lib.calendar.view.RangeBaseView;
import com.cam.lib.calendar.view.WeekView;

/**
 * 周视图的ViewPager
 * Created by yuCan on 17-3-16.
 */

public class WeekViewPager extends RangeViewPager {
    public WeekViewPager(Context context) {
        this(context, null);
    }

    public WeekViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initAdapter(final Context context, final TypedArray array) {
        mAdapter = new WeekAdapter(new InterfaceGetRangeView() {
            @Override
            public RangeBaseView getRangeView() {
                WeekView weekView = new WeekView(context, array);
                weekView.setOnDateClickListener(WeekViewPager.this);
                return weekView;
            }
        });
        mAdapter.setCurrentItemInterface(this);
        setAdapter(mAdapter);
        setCurrentItem(RangeBaseAdapter.INDEX_MIDDLE, false);
    }

    public WeekView getCurrentWeekView(){
        return (WeekView)mAdapter.getItem(getCurrentItem());
    }

    public void setGetSelectingDay(InterfaceGetSelectingDay mGetSelectingDay) {
        mAdapter.setSelectingDayInterface(mGetSelectingDay);
    }

    public void refreshWeekView(){
        mAdapter.refreshViewPager();
    }
}

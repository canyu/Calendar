package com.cam.lib.calendar.pager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.cam.lib.calendar.adapter.MonthAdapter;
import com.cam.lib.calendar.adapter.RangeBaseAdapter;
import com.cam.lib.calendar.listener.InterfaceGetRangeView;
import com.cam.lib.calendar.listener.InterfaceGetSelectingDay;
import com.cam.lib.calendar.listener.OnLastOrNextRangeClickListener;
import com.cam.lib.calendar.view.MonthView;
import com.cam.lib.calendar.view.RangeBaseView;

/**
 * 月视图的ViewPager
 * Created by yuCan on 17-7-10.
 */

public class MonthViewPager extends RangeViewPager implements OnLastOrNextRangeClickListener {

    private boolean mPostInvalidate = true;
    public MonthViewPager(Context context) {
        this(context, null);
    }

    public MonthViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initAdapter(final Context context, final TypedArray array) {
        mAdapter = new MonthAdapter(new InterfaceGetRangeView() {
            @Override
            public RangeBaseView getRangeView() {
                MonthView monthView = new MonthView(context, array);
                monthView.setOnDateClickListener(MonthViewPager.this);
                monthView.setLastOrNextClickListener(MonthViewPager.this);
                return monthView;
            }
        });
        mAdapter.setCurrentItemInterface(this);
        setAdapter(mAdapter);
        setCurrentItem(1, false);
    }

    @Override
    public void onClickLastRange(int year, int month, int day) {
        MonthView monthView = (MonthView) mAdapter.getItem(RangeBaseAdapter.INDEX_FIRST);
        if(monthView != null){
            monthView.setSelectYearMonth(year, month, day);
        }
        mPostInvalidate = false;
        setCurrentItem(RangeBaseAdapter.INDEX_FIRST, true);
    }

    @Override
    public void onClickNextRange(int year, int month, int day) {
        MonthView monthView = (MonthView) mAdapter.getItem(RangeBaseAdapter.INDEX_END);
        if(monthView != null){
            monthView.setSelectYearMonth(year, month, day);
        }
        mPostInvalidate = false;
        setCurrentItem(RangeBaseAdapter.INDEX_END, true);
    }

    public MonthView getCurrentMonthView(){
        return (MonthView)mAdapter.getItem(getCurrentItem());
    }

    public void setGetSelectingDay(InterfaceGetSelectingDay mInitMonthSelectDay){
        mAdapter.setSelectingDayInterface(mInitMonthSelectDay);
    }

    public void refreshMonthView(){
        mAdapter.refreshViewPager(mPostInvalidate);
        mPostInvalidate = true;
    }
}

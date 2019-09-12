package com.cam.lib.calendar.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cam.lib.calendar.listener.InterfaceGetRangeView;
import com.cam.lib.calendar.util.ClockUtil;
import com.cam.lib.calendar.view.WeekView;

/**
 * 周视图适配器
 * Created by yuCan on 17-3-16.
 */

public class WeekAdapter extends RangeBaseAdapter {

    private SparseArray<WeekView> mViews;

    public WeekAdapter(@NonNull InterfaceGetRangeView getRangeView) {
        super(getRangeView);
        mViews = new SparseArray<>();
    }

    @Override
    public WeekView getItem(int position) {
        return mViews.get(position);
    }

    @Override
    public void refreshIndex(int position, boolean postInvalidate) {
        WeekView holder = mViews.get(position);
        if(holder != null){
            int[] monthDay = mGetSelectingDayInterface.getSelectingDay(position);
            holder.setStartDay(ClockUtil.getFirstWeekDay(monthDay[0], monthDay[1], monthDay[2], false));
            holder.setSelectYearMonth(monthDay[0], monthDay[1], monthDay[2]);
            holder.postInvalidateRangeView(position == RangeBaseAdapter.INDEX_MIDDLE);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.remove(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        WeekView weekView = mViews.get(position);
        if (weekView == null) {
            weekView = instanceWeekView(position);
        }
        container.addView(weekView);
        return weekView;
    }

    private WeekView instanceWeekView(int position){
        WeekView weekView = (WeekView) mGetRangeViewInterface.getRangeView();
        int[] monthDay = mGetSelectingDayInterface.getSelectingDay(position);
        weekView.setStartDay(ClockUtil.getFirstWeekDay(monthDay[0], monthDay[1], monthDay[2], false));
        weekView.setId(position);
        weekView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekView.setSelectYearMonth(monthDay[0], monthDay[1], monthDay[2]);
        weekView.invalidateRangeView(position == RangeBaseAdapter.INDEX_MIDDLE);
        mViews.put(position, weekView);
        return weekView;
    }
}

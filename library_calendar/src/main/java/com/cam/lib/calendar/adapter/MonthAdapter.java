package com.cam.lib.calendar.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cam.lib.calendar.listener.InterfaceGetRangeView;
import com.cam.lib.calendar.view.MonthView;

/**
 * 月视图适配器
 * Created by yuCan on 17-7-10.
 */

public class MonthAdapter extends RangeBaseAdapter{

    private SparseArray<MonthView> mViews;

    public MonthAdapter(@NonNull InterfaceGetRangeView getRangeView) {
        super(getRangeView);
        mViews = new SparseArray<>();
    }

    @Override
    public void refreshIndex(int position, boolean postInvalidate){
        MonthView holder = mViews.get(position);
        if(holder != null){
            int[] monthDay = mGetSelectingDayInterface.getSelectingDay(position);
            holder.setSelectYearMonth(monthDay[0], monthDay[1], monthDay[2]);
            if(postInvalidate){
                holder.postInvalidateRangeView(position == RangeBaseAdapter.INDEX_MIDDLE);
            }else{
                holder.invalidateRangeView(position == RangeBaseAdapter.INDEX_MIDDLE);
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.get(position) == null) {
            int[] monthDay = mGetSelectingDayInterface.getSelectingDay(position);
            MonthView monthView = (MonthView)mGetRangeViewInterface.getRangeView();
            monthView.setId(position);
            monthView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            monthView.setSelectYearMonth(monthDay[0], monthDay[1], monthDay[2]);
            monthView.invalidateRangeView(position == RangeBaseAdapter.INDEX_MIDDLE);
            mViews.put(position, monthView);
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.remove(position);
    }

    @Override
    public MonthView getItem(int position) {
        return mViews.get(position);
    }

    public SparseArray<MonthView> getViews() {
        return mViews;
    }
}

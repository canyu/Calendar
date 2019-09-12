package com.cam.lib.calendar.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.cam.lib.calendar.listener.InterfaceGetRangeView;
import com.cam.lib.calendar.listener.InterfaceGetSelectingDay;
import com.cam.lib.calendar.listener.InterfaceSetCurrentItem;
import com.cam.lib.calendar.view.RangeBaseView;

/**
 * 视图适配器基类
 * Created by yuCan on 17-3-16.
 */

public abstract class RangeBaseAdapter extends PagerAdapter {

    private static final int INDEX_SIZE = 3;
    public static final int INDEX_FIRST = 0;
    public static final int INDEX_MIDDLE = 1;
    public static final int INDEX_END = 2;

    InterfaceGetSelectingDay mGetSelectingDayInterface;
    InterfaceGetRangeView mGetRangeViewInterface;
    private InterfaceSetCurrentItem mSetCurrentItemInterface;

    RangeBaseAdapter(@NonNull InterfaceGetRangeView mGetRangeView) {
        this.mGetRangeViewInterface = mGetRangeView;
    }

    public void setCurrentItemInterface(InterfaceSetCurrentItem setCurrentItemInterface){
        mSetCurrentItemInterface = setCurrentItemInterface;
    }

    public void setSelectingDayInterface(InterfaceGetSelectingDay getSelectingDay){
        mGetSelectingDayInterface = getSelectingDay;
    }

    @Override
    final public int getCount() {
        return INDEX_SIZE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void refreshViewPager(){
        refreshViewPager(true);
    }

    public void refreshViewPager(boolean postInvalidate) {
        refreshIndex(INDEX_MIDDLE, postInvalidate);
        if(mSetCurrentItemInterface != null){
            mSetCurrentItemInterface.setCurrentItemIfNeed(INDEX_MIDDLE);
        }
        refreshIndex(INDEX_FIRST, postInvalidate);
        refreshIndex(INDEX_END, postInvalidate);
    }

    public abstract RangeBaseView getItem(int position);

    public abstract void refreshIndex(int position, boolean postInvalidate);
}

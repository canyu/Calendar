package com.cam.lib.calendar.pager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

import com.cam.lib.calendar.R;
import com.cam.lib.calendar.adapter.RangeBaseAdapter;
import com.cam.lib.calendar.listener.InterfaceSetCurrentItem;
import com.cam.lib.calendar.listener.OnCalendarClickListener;
import com.cam.lib.calendar.listener.OnRangeClickListener;

/**
 * 日历视图的ViewPager基类
 * Created by yuCan on 17-3-16.
 */

public abstract class RangeViewPager extends ViewPager
        implements OnRangeClickListener, InterfaceSetCurrentItem {

    protected RangeBaseAdapter mAdapter;
    protected OnCalendarClickListener mOnCalendarClickListener;

    public RangeViewPager(Context context) {
        this(context, null);
    }

    public RangeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.RangeBaseView));
    }

    public void setOnCalendarClickListener(OnCalendarClickListener mOnCalendarClickListener) {
        this.mOnCalendarClickListener = mOnCalendarClickListener;
    }

    protected abstract void initAdapter(Context context, TypedArray array);
    @Override
    public void onClickThisRange(int year, int month, int day) {
        if(mOnCalendarClickListener != null){
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    @Override
    public void setCurrentItemIfNeed(int itemIndex) {
        if(getCurrentItem() != itemIndex){
            setCurrentItem(itemIndex, false);
        }
    }
}

package com.cam.lib.calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.cam.lib.calendar.R;
import com.cam.lib.calendar.listener.OnRangeClickListener;
import com.cam.lib.calendar.util.CalendarUtils;
import com.cam.lib.calendar.util.ClockUtil;

import java.util.Calendar;
import java.util.List;

/**
 * 日历视图基类
 * Created by yuCan on 17-3-36.
 */

public abstract class RangeBaseView extends View {

    protected Paint mPaint;
    protected Paint mLunarPaint;
    protected int mNormalDayColor;
    protected int mSelectDayColor;
    protected int mSelectBGColor;
    protected int mSelectBGTodayColor;
    protected int mCurrentDayColor;
    protected int mHintCircleColor;
    protected int mLunarTextColor;
    protected int mHolidayTextColor;
    protected int mLastOrNextRangeTextColor;
    protected int mCurrYear, mCurrMonth, mCurrDay;
    protected int mSelYear, mSelMonth, mSelDay;
    protected boolean mShowCurrentSelect = false;
    protected int mColumnSize, mRowSize, mSelectCircleSize;
    protected int mChildLineSize = 1;
    protected int mDaySize;
    protected int mLunarTextSize;
    protected int[] mHolidays;
    protected boolean mIsShowLunar;
    protected boolean mIsShowHint;
    protected boolean mIsShowHolidayHint;
    protected DisplayMetrics mDisplayMetrics;
    protected GestureDetector mGestureDetector;
    protected List<Integer> mTaskHintList;
    protected Bitmap mRestBitmap, mWorkBitmap;
    protected OnRangeClickListener mDateClickListener;

    public RangeBaseView(Context context) {
        this(context, null);
    }

    public RangeBaseView(Context context, TypedArray array) {
        this(context, array, null);
    }

    public RangeBaseView(Context context, TypedArray array, AttributeSet attrs) {
        this(context, array, attrs, 0);
    }

    public RangeBaseView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(array);
        initPaint();
        initGestureDetector();
    }

    private void initAttrs(TypedArray array/*, int year, int month, int day*/) {
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.RangeBaseView_selected_text_color, Color.parseColor("#FFFFFF"));
            mSelectBGColor = array.getColor(R.styleable.RangeBaseView_selected_circle_color, Color.parseColor("#E8E8E8"));
            mSelectBGTodayColor = array.getColor(R.styleable.RangeBaseView_selected_circle_today_color, Color.parseColor("#FF8594"));
            mNormalDayColor = array.getColor(R.styleable.RangeBaseView_normal_text_color, Color.parseColor("#575471"));
            mCurrentDayColor = array.getColor(R.styleable.RangeBaseView_today_text_color, Color.parseColor("#FF8594"));
            mHintCircleColor = array.getColor(R.styleable.RangeBaseView_hint_circle_color, Color.parseColor("#FE8595"));
            mLastOrNextRangeTextColor = array.getColor(R.styleable.RangeBaseView_last_or_next_range_text_color, Color.parseColor("#ACA9BC"));
            mLunarTextColor = array.getColor(R.styleable.RangeBaseView_lunar_text_color, Color.parseColor("#ACA9BC"));
            mHolidayTextColor = array.getColor(R.styleable.RangeBaseView_holiday_color, Color.parseColor("#A68BFF"));
            mDaySize = array.getInteger(R.styleable.RangeBaseView_day_text_size, 13);
            mLunarTextSize = array.getInteger(R.styleable.RangeBaseView_day_lunar_text_size, 8);
            mIsShowHint = array.getBoolean(R.styleable.RangeBaseView_show_task_hint, false);
            mIsShowLunar = array.getBoolean(R.styleable.RangeBaseView_show_lunar, false);
            mIsShowHolidayHint = array.getBoolean(R.styleable.RangeBaseView_show_holiday_hint, false);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#E8E8E8");
            mSelectBGTodayColor = Color.parseColor("#FF8594");
            mNormalDayColor = Color.parseColor("#575471");
            mCurrentDayColor = Color.parseColor("#FF8594");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLastOrNextRangeTextColor = Color.parseColor("#ACA9BC");
            mHolidayTextColor = Color.parseColor("#A68BFF");
            mDaySize = 13;
            mLunarTextSize = 8;
            mIsShowHint = false;
            mIsShowLunar = false;
            mIsShowHolidayHint = false;
        }
        mRestBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_rest_day);
        mWorkBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_work_day);
        mHolidays = CalendarUtils.getInstance(getContext()).getHolidays(mSelYear, mSelMonth + 1);
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
    }

    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

        mLunarPaint = new Paint();
        mLunarPaint.setAntiAlias(true);
        mLunarPaint.setTextSize(mLunarTextSize * mDisplayMetrics.scaledDensity);
        mLunarPaint.setColor(mLunarTextColor);
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        clearData();
        drawRange(canvas);
    }


    private void initSize() {
        mColumnSize = getWidth() / ClockUtil.DAYS_PER_WEEK;
        mRowSize = getHeight() / mChildLineSize;
        mSelectCircleSize = (int) (mColumnSize / 3.2);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }

    public void setSelectYearMonth(int year, int month, int day){
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    public void postInvalidateRangeView(boolean showCurrentSelect){
        mShowCurrentSelect = showCurrentSelect;
        postInvalidate();
    }

    public void invalidateRangeView(boolean showCurrentSelect){
        mShowCurrentSelect = showCurrentSelect;
        invalidate();
    }

    public int getSelectDay() {
        return mSelDay;
    }

    public int getSelectMonth() {
        return mSelMonth;
    }

    public int getSelectYear() {
        return mSelYear;
    }

    protected void clickThisRange(int year, int month, int day){
        if(mDateClickListener != null){
            mDateClickListener.onClickThisRange(year, month, day);
        }
    }

    public void setOnDateClickListener(OnRangeClickListener mDateClickListener) {
        this.mDateClickListener = mDateClickListener;
    }

    protected abstract void doClickAction(int x, int y);

    protected abstract void clearData();

    protected abstract void drawRange(Canvas canvas);
}

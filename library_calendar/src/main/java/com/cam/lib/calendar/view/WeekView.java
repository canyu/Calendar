package com.cam.lib.calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.cam.lib.calendar.R;
import com.cam.lib.calendar.util.CalendarUtils;
import com.cam.lib.calendar.util.ClockUtil;
import com.cam.lib.calendar.util.LunarCalendarUtils;

/**
 * 自定义周视图控件
 * Created by yuCan on 17-3-31.
 */

public class WeekView extends RangeBaseView {

    private String[] mHolidayOrLunarText;
    private int mStartDayTime;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, TypedArray array) {
        this(context, array, null);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs) {
        super(context, array, attrs);
        mChildLineSize = 1;
    }

    public void setStartDay(int dateTime){
        mStartDayTime = dateTime;
        int[] dayDetail = ClockUtil.getTimeToArray(dateTime);
        int holidays[] = CalendarUtils.getInstance(getContext()).getHolidays(dayDetail[0], dayDetail[1] + 1);
        int row = CalendarUtils.getWeekRow(dayDetail[0], dayDetail[1], dayDetail[2]);
        mHolidays = new int[ClockUtil.DAYS_PER_WEEK];
        System.arraycopy(holidays, row * ClockUtil.DAYS_PER_WEEK, mHolidays, 0, mHolidays.length);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }
    @Override
    protected void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        int[] dayDetail = ClockUtil.getTimeToArray(mStartDayTime + column * ClockUtil.SECONDS_PER_DAY);
        clickThisRange(dayDetail[0], dayDetail[1], dayDetail[2]);
    }

    @Override
    protected void clearData() {
        mHolidayOrLunarText = new String[ClockUtil.DAYS_PER_WEEK];
    }

    @Override
    protected void drawRange(Canvas canvas) {
        drawThisWeek(canvas);
        drawLunarText(canvas);
        drawHoliday(canvas);
    }

    private void drawThisWeek(Canvas canvas) {
        int[] dayDetail;
        for (int i = 0; i < 7; i++) {
            dayDetail = ClockUtil.getTimeToArray(mStartDayTime + i * ClockUtil.SECONDS_PER_DAY);
            String dayString = String.valueOf(dayDetail[2]);
            int startX = (int) (mColumnSize * i + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            if (mShowCurrentSelect && dayDetail[2] == mSelDay && ClockUtil.isValidDay(mSelYear, mSelMonth, mSelDay)) {
                int startRecX = mColumnSize * i;
                int endRecX = startRecX + mColumnSize;
                if (dayDetail[0] == mCurrYear && dayDetail[1] == mCurrMonth && dayDetail[2] == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                } else {
                    mPaint.setColor(mSelectBGColor);
                }
                canvas.drawCircle((startRecX + endRecX) / 2, mRowSize / 2, mSelectCircleSize, mPaint);
            }
            drawHintCircle(i, dayDetail[2], canvas);
            if(!ClockUtil.isValidDay(dayDetail[0], dayDetail[1], dayDetail[2])){
                mPaint.setColor(mLastOrNextRangeTextColor);
            }else if (mShowCurrentSelect && dayDetail[2] == mSelDay) {
                mPaint.setColor(mSelectDayColor);
            } else if (dayDetail[0] == mCurrYear &&
                    dayDetail[1] == mCurrMonth &&
                    dayDetail[2] == mCurrDay &&
                    mCurrYear == mSelYear) {
                mPaint.setColor(mCurrentDayColor);
            } else {
                mPaint.setColor(mNormalDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);
            mHolidayOrLunarText[i] = CalendarUtils.getHolidayFromSolar(dayDetail[0], dayDetail[1], dayDetail[2]);
        }
    }

    /**
     * 绘制农历
     */
    private void drawLunarText(Canvas canvas) {
        if (mIsShowLunar) {
            int[] dayDetail = ClockUtil.getTimeToArray(mStartDayTime);
            LunarCalendarUtils.Lunar lunar = LunarCalendarUtils.solarToLunar(
                    new LunarCalendarUtils.Solar(dayDetail[0], dayDetail[1] + 1, dayDetail[2]));
            int days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
            int day = lunar.lunarDay;
            for (int i = 0; i < 7; i++) {
                if (day > days) {
                    day = 1;
                    if (lunar.lunarMonth == 12) {
                        lunar.lunarMonth = 1;
                        lunar.lunarYear = lunar.lunarYear + 1;
                    }
                    days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
                }
                mLunarPaint.setColor(mHolidayTextColor);
                String dayString = mHolidayOrLunarText[i];
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarHoliday(lunar.lunarYear, lunar.lunarMonth, day);
                }
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarDayString(day);
                    mLunarPaint.setColor(mLunarTextColor);
                }
                int startX = (int) (mColumnSize * i + (mColumnSize - mLunarPaint.measureText(dayString)) / 2);
                int startY = (int) (mRowSize * 0.72 - (mLunarPaint.ascent() + mLunarPaint.descent()) / 2);
                canvas.drawText(dayString, startX, startY, mLunarPaint);
                day++;
            }
        }
    }

    private void drawHoliday(Canvas canvas) {
        if (mIsShowHolidayHint) {
            Rect rect = new Rect(0, 0, mRestBitmap.getWidth(), mRestBitmap.getHeight());
            Rect rectF = new Rect();
            int distance = (int) (mSelectCircleSize / 2.5);
            for (int i = 0; i < mHolidays.length; i++) {
                int column = i % 7;
                rectF.set(mColumnSize * (column + 1) - mRestBitmap.getWidth() - distance, distance, mColumnSize * (column + 1) - distance, mRestBitmap.getHeight() + distance);
                if (mHolidays[i] == 1) {
                    canvas.drawBitmap(mRestBitmap, rect, rectF, null);
                } else if (mHolidays[i] == 2) {
                    canvas.drawBitmap(mWorkBitmap, rect, rectF, null);
                }
            }
        }
    }

    /**
     * 绘制圆点提示
     */
    private void drawHintCircle(int column, int day, Canvas canvas) {
        if (mIsShowHint && mTaskHintList != null && mTaskHintList.size() > 0) {
            if (!mTaskHintList.contains(day)) return;
            mPaint.setColor(mHintCircleColor);
            float circleX = (float) (mColumnSize * column + mColumnSize * 0.5);
            float circleY = (float) (mRowSize * 0.75);
            canvas.drawCircle(circleX, circleY,
                    getResources().getDimensionPixelSize(R.dimen.task_circle_radius), mPaint);
        }
    }
}

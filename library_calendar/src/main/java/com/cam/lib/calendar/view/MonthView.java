package com.cam.lib.calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.cam.lib.calendar.R;
import com.cam.lib.calendar.listener.OnLastOrNextRangeClickListener;
import com.cam.lib.calendar.util.CalendarUtils;
import com.cam.lib.calendar.util.ClockUtil;
import com.cam.lib.calendar.util.LunarCalendarUtils;

/**
 * 自定义月视图控件
 * Created by yuCan on 17-3-31.
 */

public class MonthView extends RangeBaseView {

    private int mWeekRow; // 当前月份第几周
    private int[][] mDaysText;
    private String[][] mHolidayOrLunarText;
    private OnLastOrNextRangeClickListener mLastOrNextClickListener;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, TypedArray array) {
        this(context, array, null);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs) {
        super(context, array, attrs);
        mChildLineSize = ClockUtil.WEEKS_PER_MONTH;
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
        int row = y / mRowSize;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        int clickYear = mSelYear, clickMonth = mSelMonth;
        if (row == 0) {
            if (mDaysText[row][column] >= 23) {
                if (mSelMonth == 0) {
                    clickYear = mSelYear - 1;
                    clickMonth = 11;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth - 1;
                }
                if (mLastOrNextClickListener != null) {
                    mLastOrNextClickListener.onClickLastRange(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                clickThisRange(clickYear, clickMonth, mDaysText[row][column]);
            }
        } else {
            int monthDays = CalendarUtils.getMonthDayNum(mSelYear, mSelMonth);
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
                if (mSelMonth == 11) {
                    clickYear = mSelYear + 1;
                    clickMonth = 0;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth + 1;
                }
                if (mLastOrNextClickListener != null) {
                    mLastOrNextClickListener.onClickNextRange(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                clickThisRange(clickYear, clickMonth, mDaysText[row][column]);
            }
        }
    }

    @Override
    protected void clearData() {
        mDaysText = new int[ClockUtil.WEEKS_PER_MONTH][ClockUtil.DAYS_PER_WEEK];
        mHolidayOrLunarText = new String[ClockUtil.WEEKS_PER_MONTH][ClockUtil.DAYS_PER_WEEK];
    }

    public int getWeekRow() {
        return mWeekRow;
    }

    public void setLastOrNextClickListener(OnLastOrNextRangeClickListener mLastOrNextClickListener) {
        this.mLastOrNextClickListener = mLastOrNextClickListener;
    }

    @Override
    protected void drawRange(Canvas canvas) {
        drawLastMonth(canvas);
        drawThisMonth(canvas);
        drawNextMonth(canvas);
        drawLunarText(canvas);
        drawHoliday(canvas);
    }

    private void drawLastMonth(Canvas canvas) {
        int lastYear, lastMonth;
        if (mSelMonth == 0) {
            lastYear = mSelYear - 1;
            lastMonth = 11;
        } else {
            lastYear = mSelYear;
            lastMonth = mSelMonth - 1;
        }
        mPaint.setColor(mLastOrNextRangeTextColor);
        int monthDays = CalendarUtils.getMonthDayNum(lastYear, lastMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        int size = weekNumber - 1;
        for (int day = 0; day < size; day++) {
            mDaysText[0][day] = monthDays - weekNumber + day + 2;
            String dayString = String.valueOf(mDaysText[0][day]);
            int startX = (int) (mColumnSize * day + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
            mHolidayOrLunarText[0][day] = CalendarUtils.getHolidayFromSolar(lastYear, lastMonth, mDaysText[0][day]);
        }
    }

    private void drawThisMonth(Canvas canvas) {
        String dayString;
        int monthDays = CalendarUtils.getMonthDayNum(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        int day;
        for (int dayIndex = 0; dayIndex < monthDays; dayIndex++) {
            day = dayIndex + 1;
            dayString = String.valueOf(day);
            int column = (dayIndex + weekNumber - 1) % ClockUtil.DAYS_PER_WEEK;
            int row = (dayIndex + weekNumber - 1) / ClockUtil.DAYS_PER_WEEK;
            mDaysText[row][column] = dayIndex + 1;
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            if (mShowCurrentSelect && day == mSelDay && ClockUtil.isValidDay(mSelYear, mSelMonth, mSelDay)) {
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                if (mSelYear == mCurrYear && mCurrMonth == mSelMonth && dayIndex + 1 == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                } else {
                    mPaint.setColor(mSelectBGColor);
                }
                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
                mWeekRow = row + 1;
            }
            drawHintCircle(row, column, dayIndex + 1, canvas);
            if(!ClockUtil.isValidDay(mSelYear, mSelMonth, day)){
                mPaint.setColor(mLastOrNextRangeTextColor);
            }else if (mShowCurrentSelect && day == mSelDay) {
                mPaint.setColor(mSelectDayColor);
            } else if (day == mCurrDay &&
                    mCurrDay != mSelDay &&
                    mCurrMonth == mSelMonth &&
                    mCurrYear == mSelYear) {
                mPaint.setColor(mCurrentDayColor);
            } else {
                mPaint.setColor(mNormalDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);
            mHolidayOrLunarText[row][column] = CalendarUtils
                    .getHolidayFromSolar(mSelYear, mSelMonth, mDaysText[row][column]);
        }
    }

    private void drawNextMonth(Canvas canvas) {
        mPaint.setColor(mLastOrNextRangeTextColor);
        int monthDays = CalendarUtils.getMonthDayNum(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        int nextMonthDays = ClockUtil.DAYS_PER_MONTH - monthDays - weekNumber + 1;
        int nextMonth = mSelMonth + 1;
        int nextYear = mSelYear;
        if (nextMonth == 12) {
            nextMonth = 0;
            nextYear += 1;
        }
        for (int day = 0; day < nextMonthDays; day++) {
            int column = (monthDays + weekNumber - 1 + day) % ClockUtil.DAYS_PER_WEEK;
            int row = 5 - (nextMonthDays - day - 1) / ClockUtil.DAYS_PER_WEEK;
            try {
                mDaysText[row][column] = day + 1;
                mHolidayOrLunarText[row][column] = CalendarUtils.getHolidayFromSolar(nextYear, nextMonth, mDaysText[row][column]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dayString = String.valueOf(mDaysText[row][column]);
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
    }

    /**
     * 绘制农历
     */
    private void drawLunarText(Canvas canvas) {
        if (mIsShowLunar) {
            boolean isLeapLeft = false;
            int firstYear, firstMonth, firstDay;
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            if (weekNumber == 1) {
                firstYear = mSelYear;
                firstMonth = mSelMonth + 1;
                firstDay = 1;
            } else {
                int monthDays;
                if (mSelMonth == 0) {
                    firstYear = mSelYear - 1;
                    firstMonth = 11;
                    monthDays = CalendarUtils.getMonthDayNum(firstYear, firstMonth);
                    firstMonth = 12;
                } else {
                    firstYear = mSelYear;
                    firstMonth = mSelMonth - 1;
                    monthDays = CalendarUtils.getMonthDayNum(firstYear, firstMonth);
                    firstMonth = mSelMonth;
                }
                firstDay = monthDays - weekNumber + 2;
            }
            LunarCalendarUtils.Lunar lunar = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(firstYear, firstMonth, firstDay));
            int days;
            int day = lunar.lunarDay;
            int leapMonth = LunarCalendarUtils.leapMonth(lunar.lunarYear);
            days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
            if (lunar.isLeap) {
                days = 30;
            }
            for (int i = 0; i < ClockUtil.DAYS_PER_MONTH; i++) {
                int column = i % ClockUtil.DAYS_PER_WEEK;
                int row = i / ClockUtil.DAYS_PER_WEEK;
                if (day > days) {
                    day = 1;
                    if (lunar.lunarMonth == 12) {
                        lunar.lunarMonth = 1;
                        lunar.lunarYear = lunar.lunarYear + 1;
                    } else {
                        if (lunar.lunarMonth == leapMonth) {
                            if (!isLeapLeft) {
                                days = 30;
                                isLeapLeft = true;
                            } else {
                                lunar.lunarMonth++;
                                days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
                            }
                        } else {
                            lunar.lunarMonth++;
                            days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
                        }
                    }
                }
                if (row == 0 && mDaysText[row][column] >= 23 || row >= 4 && mDaysText[row][column] <= 14) {
                    mLunarPaint.setColor(mLunarTextColor);
                } else {
                    mLunarPaint.setColor(mHolidayTextColor);
                }
                String dayString = mHolidayOrLunarText[row][column];
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarHoliday(lunar.lunarYear, lunar.lunarMonth, day);
                }
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarDayString(day);
                    mLunarPaint.setColor(mLunarTextColor);
                }
                int startX = (int) (mColumnSize * column + (mColumnSize - mLunarPaint.measureText(dayString)) / 2);
                int startY = (int) (mRowSize * row + mRowSize * 0.72 - (mLunarPaint.ascent() + mLunarPaint.descent()) / 2);
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
                int column = i % ClockUtil.DAYS_PER_WEEK;
                int row = i / ClockUtil.DAYS_PER_WEEK;
                rectF.set(mColumnSize * (column + 1) - mRestBitmap.getWidth() - distance, mRowSize * row + distance, mColumnSize * (column + 1) - distance, mRowSize * row + mRestBitmap.getHeight() + distance);
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
    private void drawHintCircle(int row, int column, int day, Canvas canvas) {
        if (mIsShowHint && mTaskHintList != null && mTaskHintList.size() > 0) {
            if (!mTaskHintList.contains(day)) return;
            mPaint.setColor(mHintCircleColor);
            float circleX = (float) (mColumnSize * column + mColumnSize * 0.5);
            float circleY = (float) (mRowSize * row + mRowSize * 0.75);
            canvas.drawCircle(circleX, circleY,
                    getResources().getDimensionPixelSize(R.dimen.task_circle_radius), mPaint);
        }
    }
}

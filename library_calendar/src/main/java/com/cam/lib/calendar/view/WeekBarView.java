package com.cam.lib.calendar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.cam.lib.calendar.R;


/**
 * 日历视图的星期hint显示
 */
public class WeekBarView extends View {

    private int mWeekTextColor, mWeekendTextColor;
    private int mWeekSize;
    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;
    private String[] mWeekString;

    public WeekBarView(Context context) {
        this(context, null);
    }

    public WeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        @SuppressLint("Recycle") TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WeekBarView);
        mWeekTextColor = array.getColor(R.styleable.WeekBarView_week_text_color, Color.parseColor("#000000"));
        mWeekendTextColor = array.getColor(R.styleable.WeekBarView_week_text_color, Color.parseColor("#000000"));
        mWeekSize = array.getInteger(R.styleable.WeekBarView_week_text_size, 13);
        mWeekString = context.getResources().getStringArray(R.array.calendar_week);
    }

    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setColor(mWeekTextColor);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 30;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int columnWidth = width / 7;
        for (int i = 0; i < mWeekString.length; i++) {
            initPaintColor(i);
            String text = mWeekString[i];
            int fontWidth = (int) mPaint.measureText(text);
            int startX = columnWidth * i + (columnWidth - fontWidth) / 2;
            int startY = (int) (height / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(text, startX, startY, mPaint);
        }
    }

    private void initPaintColor(int position){
        switch (position){
            case 0:
            case 6:{
                mPaint.setColor(mWeekendTextColor);
            }
            break;
            default:{
                mPaint.setColor(mWeekTextColor);
            }
        }
    }
}

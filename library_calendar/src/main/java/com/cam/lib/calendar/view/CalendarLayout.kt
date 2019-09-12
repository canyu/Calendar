package com.cam.lib.calendar.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.RelativeLayout

import androidx.viewpager.widget.ViewPager

import com.cam.lib.calendar.R
import com.cam.lib.calendar.adapter.RangeBaseAdapter
import com.cam.lib.calendar.animation.CalendarAnimation
import com.cam.lib.calendar.listener.OnCalendarClickListener
import com.cam.lib.calendar.listener.OnCalendarScrollListener
import com.cam.lib.calendar.pager.MonthViewPager
import com.cam.lib.calendar.pager.WeekViewPager
import com.cam.lib.calendar.state.CalendarState
import com.cam.lib.calendar.util.CalendarUtils
import com.cam.lib.calendar.util.ClockUtil

import java.util.Calendar

/**
 * Created by yuCan on 17-7-10.
 */
class CalendarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mcvCalendar: MonthViewPager? = null
    private var wcvCalendar: WeekViewPager? = null
    private var rlMonthCalendar: RelativeLayout? = null
    private var rlScheduleList: RelativeLayout? = null
    private var rvScheduleList: ListView? = null

    private var mCurrentSelectYear: Int = 0
    private var mCurrentSelectMonth: Int = 0
    private var mCurrentSelectDay: Int = 0
    private var mRowSize: Int = 0
    private var mMinDistance: Int = 0
    private var mAutoScrollDistance: Int = 0
    private var mDefaultView: Int = 0
    private val mDownPosition = FloatArray(2)
    private var mIsScrolling = false

    private var mState: CalendarState? = null
    private var mOnCalendarClickListener: OnCalendarClickListener? = null
    private var mGestureDetector: GestureDetector? = null

    private val mMonthCalendarClickListener:OnCalendarClickListener = OnCalendarClickListener { year, month, day ->
        if (mCurrentSelectYear == year &&
            mCurrentSelectMonth == month &&
            mCurrentSelectDay == day) {
            return@OnCalendarClickListener
        }
        resetCurrentSelectDate(year, month, day)
        wcvCalendar?.let{
            it.setOnCalendarClickListener(null)
            it.refreshWeekView()
            mcvCalendar?.refreshMonthView()
            it.setOnCalendarClickListener(mWeekCalendarClickListener)
        }
    }

    private val mWeekCalendarClickListener:OnCalendarClickListener = OnCalendarClickListener { year, month, day ->
        if (mCurrentSelectYear == year &&
            mCurrentSelectMonth == month &&
            mCurrentSelectDay == day
        ) {
            return@OnCalendarClickListener
        }
        resetCurrentSelectDate(year, month, day)
        mcvCalendar?.let{
            it.setOnCalendarClickListener(null)
            it.refreshMonthView()
            wcvCalendar?.refreshWeekView()
            it.setOnCalendarClickListener(mMonthCalendarClickListener)
        }
    }

    private val isListViewReachTopEdge: Boolean
        get() {
            return rvScheduleList?.let{
                if (it.childCount == 0) {
                    return true
                }
                var result = false
                if (it.firstVisiblePosition == 0) {
                    val topChildView = it.getChildAt(0)
                    result = topChildView.top == it.paddingTop
                }
                result
            }?: true
        }

    init {
        initAttrs(context.obtainStyledAttributes(attrs, R.styleable.CalendarLayout))
        initDate()
        initGestureDetector()
    }

    private fun initAttrs(array: TypedArray) {
        mDefaultView = array.getInt(R.styleable.CalendarLayout_default_view, DEFAULT_MONTH)
        mState = CalendarState.EXPAND
        mRowSize = resources.getDimensionPixelSize(R.dimen.week_calendar_height)
        mMinDistance = resources.getDimensionPixelSize(R.dimen.calendar_min_distance)
        mAutoScrollDistance = resources.getDimensionPixelSize(R.dimen.auto_scroll_distance)
    }

    private fun initGestureDetector() {
        mGestureDetector = GestureDetector(context, OnCalendarScrollListener(this))
    }

    private fun initDate() {
        val calendar = Calendar.getInstance()
        resetCurrentSelectDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mcvCalendar = findViewById(R.id.mcvCalendar)
        wcvCalendar = findViewById(R.id.wcvCalendar)
        rlMonthCalendar = findViewById(R.id.rlMonthCalendar)
        rlScheduleList = findViewById(R.id.rlScheduleList)
        rvScheduleList = findViewById(R.id.rvScheduleList)
        mcvCalendar?.setGetSelectingDay { position ->
            when (position) {
                RangeBaseAdapter.INDEX_FIRST -> {
                    CalendarUtils.getLastMonthDay(
                        mCurrentSelectYear,
                        mCurrentSelectMonth,
                        mCurrentSelectDay
                    )
                }
                RangeBaseAdapter.INDEX_END -> {
                    CalendarUtils.getNextMonthDay(
                        mCurrentSelectYear,
                        mCurrentSelectMonth,
                        mCurrentSelectDay
                    )
                }
                else -> {
                    intArrayOf(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay)
                }
            }
        }
        wcvCalendar?.setGetSelectingDay { position ->
            when (position) {
                RangeBaseAdapter.INDEX_FIRST -> {
                    CalendarUtils.getLastWeekDay(
                        mCurrentSelectYear,
                        mCurrentSelectMonth,
                        mCurrentSelectDay
                    )
                }
                RangeBaseAdapter.INDEX_END -> {
                    CalendarUtils.getNextWeekDay(
                        mCurrentSelectYear,
                        mCurrentSelectMonth,
                        mCurrentSelectDay
                    )
                }
                else -> {
                    intArrayOf(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay)
                }
            }
        }
        bindingMonthAndWeekCalendar()
    }

    private fun bindingMonthAndWeekCalendar() {
        mcvCalendar?.setOnCalendarClickListener(mMonthCalendarClickListener)
        wcvCalendar?.setOnCalendarClickListener(mWeekCalendarClickListener)

        mcvCalendar?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            /**
             * 0 无需操作
             * 1 还原回去
             * 2 需要刷新
             */
            var needRefreshAfterScroll = 0

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                mcvCalendar?.let{
                    val monthView = it.currentMonthView
                    val year = monthView.selectYear
                    val month = monthView.selectMonth
                    var day = monthView.selectDay
                    if (mCurrentSelectYear == year &&
                        mCurrentSelectMonth == month &&
                        mCurrentSelectDay == day) {
                        return
                    }
                    if (ClockUtil.isDayBeforeFirstDay(year, month, day)) {
                        day++
                    }
                    if (!ClockUtil.isValidDay(year, month, day)) {
                        needRefreshAfterScroll = 1
                        return
                    }
                    needRefreshAfterScroll = 2
                    resetCurrentSelectDate(year, month, day)
                    wcvCalendar?.refreshWeekView()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (needRefreshAfterScroll == 1) {
                        mcvCalendar?.setCurrentItem(RangeBaseAdapter.INDEX_MIDDLE, true)
                    } else if (needRefreshAfterScroll == 2) {
                        mcvCalendar?.refreshMonthView()
                        if (mOnCalendarClickListener != null) {
                            mOnCalendarClickListener?.onClickDate(
                                mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay
                            )
                        }
                    }
                    needRefreshAfterScroll = 0
                }
            }
        })

        wcvCalendar?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            /**
             * 0 无需操作
             * 1 还原回去
             * 2 需要刷新
             */
            var needRefreshAfterScroll = 0

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                wcvCalendar?.let{
                    val weekView = it.currentWeekView
                    var year = weekView.selectYear
                    var month = weekView.selectMonth
                    var day = weekView.selectDay
                    if (mCurrentSelectYear == year &&
                        mCurrentSelectMonth == month &&
                        mCurrentSelectDay == day) {
                        return
                    }

                    if (ClockUtil.isDaySameWeekWithFirstDay(year, month, day)) {
                        year = ClockUtil.FIRST_DAY_YEAR
                        month = ClockUtil.FIRST_DAY_MONTH
                        day = ClockUtil.FIRST_DAY_DAY
                    }
                    if (!ClockUtil.isValidDay(year, month, day)) {
                        needRefreshAfterScroll = 1
                        return
                    }
                    needRefreshAfterScroll = 2
                    resetCurrentSelectDate(year, month, day)
                    mcvCalendar?.refreshMonthView()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (needRefreshAfterScroll == 1) {
                        wcvCalendar?.setCurrentItem(RangeBaseAdapter.INDEX_MIDDLE, true)
                    } else if (needRefreshAfterScroll == 2) {
                        wcvCalendar?.refreshWeekView()
                        if (mOnCalendarClickListener != null) {
                            mOnCalendarClickListener?.onClickDate(
                                mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay
                            )
                        }
                    }
                    needRefreshAfterScroll = 0
                }
            }
        })

        // 初始化视图
        if (mDefaultView == DEFAULT_MONTH) {
            wcvCalendar?.visibility = View.INVISIBLE
            mState = CalendarState.EXPAND
        } else if (mDefaultView == DEFAULT_WEEK) {
            wcvCalendar?.visibility = View.VISIBLE
            mState = CalendarState.CLOSE
            val calendar = Calendar.getInstance()
            val row = CalendarUtils.getWeekRow(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            rlMonthCalendar?.y = (-row * mRowSize).toFloat()
            rlScheduleList?.let{
                it.y = it.y - 5 * mRowSize
            }
        }
    }

    private fun resetCurrentSelectDate(year: Int, month: Int, day: Int) {
        mCurrentSelectYear = year
        mCurrentSelectMonth = month
        mCurrentSelectDay = day
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(heightMeasureSpec)
        rlScheduleList?.let{ resetViewHeight(it, height - mRowSize) }
        resetViewHeight(this, height)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun resetViewHeight(view: View, height: Int) {
        val layoutParams = view.layoutParams
        if (layoutParams.height != height) {
            layoutParams.height = height
            view.layoutParams = layoutParams
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mDownPosition[0] = ev.rawX
                mDownPosition[1] = ev.rawY
                mGestureDetector?.onTouchEvent(ev)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (mIsScrolling) {
            return true
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val x = ev.rawX
                val y = ev.rawY
                val distanceX = Math.abs(x - mDownPosition[0])
                val distanceY = Math.abs(y - mDownPosition[1])
                if (distanceY > mMinDistance && distanceY > distanceX * 2.0f) {
                    return if (y > mDownPosition[1])
                        mState == CalendarState.CLOSE && isListViewReachTopEdge
                    else
                        mState == CalendarState.EXPAND
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mDownPosition[0] = event.rawX
                mDownPosition[1] = event.rawY
                resetCalendarPosition()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                transferEvent(event)
                mIsScrolling = true
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                transferEvent(event)
                changeCalendarState()
                resetScrollingState()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun transferEvent(event: MotionEvent) {
        if (mState == CalendarState.CLOSE) {
            mcvCalendar?.visibility = View.VISIBLE
            wcvCalendar?.visibility = View.INVISIBLE
            mGestureDetector?.onTouchEvent(event)
        } else {
            mGestureDetector?.onTouchEvent(event)
        }
    }

    private fun changeCalendarState() {
        rlScheduleList?.let{
            val animation :CalendarAnimation
            if (it.y > mRowSize * 2 && it.y < it.height - mRowSize) { // 位于中间
                animation = getStateAnimation()
            } else if (it.y <= mRowSize * 2) { // 位于顶部
                animation = getExpandAnimation()
            } else {
                animation = getCloseAnimation()
            }
            it.startAnimation(animation)
        }
    }

    private fun getCloseAnimation(): CalendarAnimation {
        val animation = CalendarAnimation(this, CalendarState.CLOSE, mAutoScrollDistance.toFloat())
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                if (mState == CalendarState.CLOSE) {
                    mState = CalendarState.EXPAND
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        return animation
    }

    private fun getStateAnimation():CalendarAnimation{
        val animation = CalendarAnimation(this, mState, mAutoScrollDistance.toFloat())
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                changeState()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        return animation
    }

    private fun getExpandAnimation():CalendarAnimation{
        val animation = CalendarAnimation(this, CalendarState.EXPAND, mAutoScrollDistance.toFloat())
        animation.duration = 50
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                if (mState == CalendarState.EXPAND) {
                    changeState()
                } else {
                    resetCalendar()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        return animation
    }
    private fun resetCalendarPosition() {
        if (mState == CalendarState.EXPAND) {
            rlMonthCalendar?.y = 0f
            mcvCalendar?.let{rlScheduleList?.y = it.height.toFloat()}
        } else {
            rlMonthCalendar?.y = (-CalendarUtils.getWeekRow(
                mCurrentSelectYear,
                mCurrentSelectMonth,
                mCurrentSelectDay
            ) * mRowSize).toFloat()
            rlScheduleList?.y = mRowSize.toFloat()
        }
    }

    private fun resetCalendar() {
        if (mState == CalendarState.EXPAND) {
            mcvCalendar?.visibility = View.VISIBLE
            wcvCalendar?.visibility = View.INVISIBLE
        } else {
            mcvCalendar?.visibility = View.INVISIBLE
            wcvCalendar?.visibility = View.VISIBLE
        }
    }

    private fun changeState() {
        if (mState == CalendarState.EXPAND) {
            mState = CalendarState.CLOSE
            mcvCalendar?.visibility = View.INVISIBLE
            wcvCalendar?.visibility = View.VISIBLE
            mcvCalendar?.let {rlMonthCalendar?.y = ((1 - it.currentMonthView.weekRow) * mRowSize).toFloat()  }
        } else {
            mState = CalendarState.EXPAND
            mcvCalendar?.visibility = View.VISIBLE
            wcvCalendar?.visibility = View.INVISIBLE
            rlMonthCalendar?.y = 0f
        }
    }

    private fun resetScrollingState() {
        mDownPosition[0] = 0f
        mDownPosition[1] = 0f
        mIsScrolling = false
    }

    fun onCalendarScroll(distanceY: Float) {
        mcvCalendar?.let { calendar ->
            rlMonthCalendar?.let{monthCalendar ->
                rlScheduleList?.let{list ->
                    var distanceTmpY = distanceY
                    val monthView = calendar.currentMonthView
                    distanceTmpY = distanceTmpY.coerceAtMost(mAutoScrollDistance.toFloat())
                    val calendarDistanceY = distanceTmpY / 5.0f
                    val row = monthView.weekRow - 1
                    val calendarTop = -row * mRowSize
                    val scheduleTop = mRowSize
                    var calendarY = monthCalendar.y - calendarDistanceY * row
                    calendarY = calendarY.coerceAtMost(0f)
                    calendarY = calendarY.coerceAtLeast(calendarTop.toFloat())
                    rlMonthCalendar?.y = calendarY
                    var scheduleY = list.y - distanceTmpY
                    scheduleY = Math.min(scheduleY, calendar.height.toFloat())
                    scheduleY = Math.max(scheduleY, scheduleTop.toFloat())
                    rlScheduleList?.y = scheduleY
                }
            }
        }
    }

    fun setOnCalendarClickListener(onCalendarClickListener: OnCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener
    }

    fun setNewTime(time: Int) {
        var timeTmp = time
        if (timeTmp < ClockUtil.FIRST_DAY) {
            timeTmp = ClockUtil.FIRST_DAY
        }
        val timeDetail = ClockUtil.getTimeToArray(timeTmp)
        resetCurrentSelectDate(timeDetail[0], timeDetail[1], timeDetail[2])
        mcvCalendar?.refreshMonthView()
        wcvCalendar?.refreshWeekView()
    }

    companion object {

        private const val DEFAULT_MONTH = 0
        private const val DEFAULT_WEEK = 1
    }
}

package com.cam.calendar

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.cam.calendar.widget.ListHead
import com.cam.lib.calendar.listener.OnCalendarClickListener
import com.cam.lib.calendar.util.ClockUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() , OnCalendarClickListener {

    private var listHead: ListHead? = null

    private var mCurrentSelectYear: Int = 0
    private var mCurrentSelectMonth:Int = 0
    private var mCurrentSelectDay:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val head = ListHead(layoutInflater)
        listHead = head
        rvScheduleList.addHeaderView(head.listHead)
        Log.d("yucan", " MyCalendarListViewActivity(onCreate):  ")
        val strs = arrayOfNulls<String>(100)
        for (i in strs.indices) {
            strs[i] = String.format("第%d行", i)
        }
        rvScheduleList.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs)
        rvScheduleList.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                //                Intent intent = new Intent();
                //                intent.setClass(CalendarListViewActivity.this, CalendarScrollViewActivity.class);
                //                startActivity(intent);
            }
        //        rvScheduleList.setVisibility(View.INVISIBLE);
        tv_title.setOnClickListener { slSchedule.setNewTime(0) }
        tv_back_to_today.setOnClickListener {
            slSchedule.setNewTime(ClockUtil.getCurrentTime())
        }
        initDate()
    }


    private fun initDate() {
        val calendar = Calendar.getInstance()
        setCurrentSelectDate(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                Calendar.DAY_OF_MONTH
            )
        )
    }

    private fun setCurrentSelectDate(year: Int, month: Int, day: Int) {
        mCurrentSelectYear = year
        mCurrentSelectMonth = month
        mCurrentSelectDay = day
        //        if (mActivity instanceof MainActivity) {
        //            ((MainActivity) mActivity).resetMainTitleDate(year, month, day);
        //        }
    }

    override fun onClickDate(year: Int, month: Int, day: Int) {

    }
}

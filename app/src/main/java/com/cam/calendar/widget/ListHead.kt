package com.cam.calendar.widget

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import com.cam.calendar.R

/**
 *
 * Created by yuCan on 17-3-14.
 */

class ListHead(inflater: LayoutInflater) {

    var listHead: View
    private val mFilter: TextView

    init {
        listHead = inflater.inflate(RES_ID, null)
        mFilter = listHead.findViewById(R.id.tv_filter)
    }

    fun setFilterListener(listener: View.OnClickListener) {
        mFilter.setOnClickListener(listener)
    }

    companion object {
        private const val RES_ID = R.layout.list_head
    }
}

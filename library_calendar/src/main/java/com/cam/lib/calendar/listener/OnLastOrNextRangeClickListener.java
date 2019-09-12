package com.cam.lib.calendar.listener;

/**
 *上个月或下个月的点击监听
 * Created by yuCan on 17-3-31.
 */

public interface OnLastOrNextRangeClickListener {
    void onClickLastRange(int year, int month, int day);
    void onClickNextRange(int year, int month, int day);
}

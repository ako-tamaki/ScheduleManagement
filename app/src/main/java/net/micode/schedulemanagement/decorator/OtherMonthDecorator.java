package net.micode.schedulemanagement.decorator;

import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

// 装饰非当月日期
public class OtherMonthDecorator implements DayViewDecorator {
    private final int targetMonth; // 目标月份 (用来判断哪些日期是“非当月”)
    private final int targetYear; // 目标年份
    private final int textColor; // 非当月日期文本颜色

    public OtherMonthDecorator(int targetMonth, int targetYear, int textColor) {
        this.targetMonth = targetMonth;
        this.targetYear = targetYear;
        this.textColor = textColor;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // 判断传入的日期是否不是目标月份或年份
        return day.getMonth() != targetMonth || day.getYear() != targetYear;
    }

    @Override
    public void decorate(DayViewFacade view) {
        // 如果 shouldDecorate 返回 true，则应用非当月日期的样式
        view.addSpan(new ForegroundColorSpan(textColor)); // 文本颜色
    }
}
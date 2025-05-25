package net.micode.schedulemanagement.decorator;

import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

// 装饰当月日期
public class InCurrentMonthDecorator implements DayViewDecorator {
    private final int targetMonth; // 目标月份
    private final int targetYear; // 目标年份
    private final int textColor; // 当月日期文本颜色

    public InCurrentMonthDecorator(int targetMonth, int targetYear, int textColor) {
        this.targetMonth = targetMonth;
        this.targetYear = targetYear;
        this.textColor = textColor;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // 判断传入的日期是否是目标月份和年份
        return day.getMonth() == targetMonth && day.getYear() == targetYear;
    }

    @Override
    public void decorate(DayViewFacade view) {
        // 如果 shouldDecorate 返回 true，则应用当月日期的样式
        view.addSpan(new StyleSpan(Typeface.BOLD)); // 粗体
        view.addSpan(new ForegroundColorSpan(textColor)); // 文本颜色
    }
}
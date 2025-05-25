package net.micode.schedulemanagement.decorator;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class TodayDecorator implements DayViewDecorator {
    private final CalendarDay today;
    private final int bgColor;
    private final int textColor;

    public TodayDecorator(int bgColor, int textColor) {
        this.today = CalendarDay.today();
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(createCircleDrawable(bgColor));
        view.addSpan(new ForegroundColorSpan(textColor));
    }

    private Drawable createCircleDrawable(int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }
}
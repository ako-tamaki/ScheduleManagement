package net.micode.schedulemanagement.decorator;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectedDateDecorator implements DayViewDecorator {
    private CalendarDay selectedDay;
    private final int bgColor;

    public SelectedDateDecorator(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setSelectedDay(CalendarDay day) {
        this.selectedDay = day;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return selectedDay != null && day.equals(selectedDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(createCircleDrawable(bgColor));
    }

    private Drawable createCircleDrawable(int color) {
        // 同 TodayDecorator 中的实现
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }
}

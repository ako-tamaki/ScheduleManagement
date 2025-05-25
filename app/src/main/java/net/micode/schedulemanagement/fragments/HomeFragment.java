// 文件路径: fragments/HomeFragment.java
package net.micode.schedulemanagement.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import net.micode.schedulemanagement.R;

import net.micode.schedulemanagement.database.DAO.EventDAO;
import net.micode.schedulemanagement.database.DAO.UserDAO;
import net.micode.schedulemanagement.decorator.InCurrentMonthDecorator;
import net.micode.schedulemanagement.decorator.OtherMonthDecorator;
import net.micode.schedulemanagement.decorator.SelectedDateDecorator;
import net.micode.schedulemanagement.decorator.TodayDecorator;
import net.micode.schedulemanagement.entity.Event;
import net.micode.schedulemanagement.entity.User;
import net.micode.schedulemanagement.util.PreferenceManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
        implements OnMonthChangedListener, OnDateSelectedListener {

    private MaterialCalendarView calendarView;
    private SelectedDateDecorator selectedDecorator;
    private TodayDecorator todayDecorator;
    private DayViewDecorator inMonthDecorator;
    private DayViewDecorator otherMonthDecorator;
    private EventDAO eventDAO;
    private UserDAO userDAO;

    // 颜色常量
    private final int COLOR_CURRENT_MONTH_TEXT = Color.BLACK;
    private final int COLOR_OTHER_MONTH_TEXT = Color.LTGRAY;
    private final int COLOR_TODAY_BG = Color.BLUE;
    private final int COLOR_TODAY_TEXT = Color.WHITE;
    private final int COLOR_SELECTED_BG = Color.GRAY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化DAO
        eventDAO = new EventDAO(requireContext());
        userDAO = new UserDAO(requireContext());

        if (calendarView != null) {
            // 初始化装饰器
            selectedDecorator = new SelectedDateDecorator(COLOR_SELECTED_BG);
            todayDecorator = new TodayDecorator(COLOR_TODAY_BG, COLOR_TODAY_TEXT);
            updateCurrentMonthDecorator();

            calendarView.addDecorators(todayDecorator, selectedDecorator);
            calendarView.setOnDateChangedListener(this);
            calendarView.setOnMonthChangedListener(this);
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        updateCurrentMonthDecorator();
        widget.invalidateDecorators();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget,
                               @NonNull CalendarDay date, boolean selected) {
        if (date.getMonth() != widget.getCurrentDate().getMonth()) {
            widget.setCurrentDate(date, true);
        } else {
            selectedDecorator.setSelectedDay(date);
            widget.invalidateDecorators();
            showEventsDialog(date); // 新增点击事件处理
        }
    }

    // 显示事件对话框
    private void showEventsDialog(CalendarDay selectedDay) {
        String username = PreferenceManager.getUsername();
        User user = userDAO.getUserByUsername(username);
        if (user == null) return;

        LocalDate targetDate = LocalDate.of(
                selectedDay.getYear(),
                selectedDay.getMonth(),
                selectedDay.getDay()
        );

        List<Event> allEvents = eventDAO.getEventsByUserId(user.getId());
        List<Event> filteredEvents = new ArrayList<>();

        for (Event event : allEvents) {
            LocalDateTime start = event.getStartTime();
            LocalDateTime end = event.getEndTime();
            LocalDate eventStart = start.toLocalDate();
            LocalDate eventEnd = end.toLocalDate();

            if (!targetDate.isBefore(eventStart) && !targetDate.isAfter(eventEnd)) {
                filteredEvents.add(event);
            }
        }

        StringBuilder content = new StringBuilder();
        if (filteredEvents.isEmpty()) {
            content.append("该日期没有安排事件");
        } else {
            for (Event event : filteredEvents) {
                content.append("● ")
                        .append(event.getTitle())
                        .append("\nDur:")
                        .append(formatTime(event.getStartTime().toString()))  // 使用格式化后的时间
                        .append(" - ")
                        .append(formatTime(event.getEndTime().toString()))    // 使用格式化后的时间
                        .append("\n")
                        .append("Des:")
                        .append(event.getDescription())
                        .append("\n\n");
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(String.format("%d-%02d-%02d",
                        selectedDay.getYear(),
                        selectedDay.getMonth(),
                        selectedDay.getDay()))
                .setMessage(content.toString())
                .setPositiveButton("确定", null)
                .show();
    }

    // 辅助方法：格式化时间显示
    private String formatTime(String isoTime) {
        try {
            // 自定义解析格式，兼容 "yyyy-MM-dd'T'HH:mm"（无秒部分）
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(isoTime, parser);

            // 定义输出格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
            return dateTime.format(formatter);
        } catch (DateTimeParseException e) {
            Log.e("HomeFragment", "时间解析失败: " + isoTime, e);
            return "时间无效";
        }
    }

    private void updateCurrentMonthDecorator() {
        CalendarDay current = calendarView.getCurrentDate();
        int currentMonth = current.getMonth();
        int currentYear = current.getYear();

        if (inMonthDecorator != null) calendarView.removeDecorator(inMonthDecorator);
        if (otherMonthDecorator != null) calendarView.removeDecorator(otherMonthDecorator);

        inMonthDecorator = new InCurrentMonthDecorator(
                currentMonth, currentYear, COLOR_CURRENT_MONTH_TEXT
        );
        otherMonthDecorator = new OtherMonthDecorator(
                currentMonth, currentYear, COLOR_OTHER_MONTH_TEXT
        );

        calendarView.addDecorator(inMonthDecorator);
        calendarView.addDecorator(otherMonthDecorator);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        calendarView = null;
        selectedDecorator = null;
        todayDecorator = null;
        inMonthDecorator = null;
        otherMonthDecorator = null;
        eventDAO = null;
        userDAO = null;
    }
}
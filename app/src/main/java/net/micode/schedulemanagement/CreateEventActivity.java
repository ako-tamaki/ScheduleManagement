package net.micode.schedulemanagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import net.micode.schedulemanagement.database.DAO.EventDAO;
import net.micode.schedulemanagement.database.DAO.UserDAO;
import net.micode.schedulemanagement.entity.Event;
import net.micode.schedulemanagement.entity.User;
import net.micode.schedulemanagement.util.PreferenceManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    private TextInputEditText etTitle, etStartTime, etEndTime, etLocation, etDescription;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private String currentUsername;
    private UserDAO userDAO;
    private EventDAO eventDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // 初始化DAO
        userDAO = new UserDAO(this);
        eventDAO = new EventDAO(this);

        // 获取传递的用户名
        currentUsername = PreferenceManager.getUsername();
        if (currentUsername == null) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            finish();
        }

        initViews();
        setupTimePickers();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_title);
        etStartTime = findViewById(R.id.et_start_time);
        etEndTime = findViewById(R.id.et_end_time);
        etLocation = findViewById(R.id.et_location);
        etDescription = findViewById(R.id.et_description);
        Button btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> saveEvent());
    }

    private void setupTimePickers() {
        etStartTime.setOnClickListener(v -> showDateTimePicker(true));
        etEndTime.setOnClickListener(v -> showDateTimePicker(false));
    }

    private void showDateTimePicker(final boolean isStartTime) {
        final Calendar calendar = isStartTime ? startCalendar : endCalendar;
        new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int day) -> {
                    calendar.set(year, month, day);
                    new TimePickerDialog(
                            this,
                            (TimePicker view1, int hour, int minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                updateTimeDisplay(calendar, isStartTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateTimeDisplay(Calendar calendar, boolean isStartTime) {
        LocalDateTime dateTime = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        );

        String formatted = Event.localDateTimeToString(dateTime); // 使用统一格式化方法

        if (isStartTime) {
            etStartTime.setText(formatted);
        } else {
            etEndTime.setText(formatted);
        }
    }

    private void saveEvent() {
        try {
            String title = etTitle.getText().toString().trim();
            String start = etStartTime.getText().toString().trim();
            String end = etEndTime.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            // 输入验证
            if (title.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "请填写标题和时间", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取用户ID（添加空检查）
            User user = userDAO.getUserByUsername(currentUsername);
            if (user == null) {
                Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            long userId = user.getId();

            // 时间转换（捕获格式异常）
            LocalDateTime startTime, endTime;
            startTime = Event.stringToLocalDateTime(start);
            endTime = Event.stringToLocalDateTime(end);
           if (endTime.isBefore(startTime)) {
               Toast.makeText(this, "结束时间必须晚于开始时间", Toast.LENGTH_SHORT).show();
               return;
           }


            // 创建并保存事件
            Event event = new Event();
            event.setTitle(title);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setLocation(location);
            event.setDescription(description);
            event.setUserId(userId);

            long result = eventDAO.insertEvent(event);
            if (result != -1) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "保存时发生错误", Toast.LENGTH_SHORT).show();
            Log.e("CreateEvent", "保存事件失败", e);
        }
    }
}
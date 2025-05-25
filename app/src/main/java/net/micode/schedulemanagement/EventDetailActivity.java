package net.micode.schedulemanagement;
// EventDetailActivity.java

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import net.micode.schedulemanagement.database.DAO.EventDAO;
import net.micode.schedulemanagement.entity.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // 用于处理时间解析异常
import java.util.Calendar;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";
    public static final String EXTRA_EVENT_ID = "extra_event_id";

    private TextInputEditText editTextTitle;
    private TextInputEditText et_start_time;
    private TextInputEditText et_end_time;
    private TextInputEditText editTextLocation;
    private TextInputEditText editTextDescription;
    private Button buttonSave;

    private EventDAO eventDao;
    private long eventId = -1;
    private Event currentEvent;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventDao = new EventDAO(this);

        // 初始化视图
        editTextTitle = findViewById(R.id.edit_text_event_title);
        et_start_time = findViewById(R.id.et_start_time);
        et_end_time = findViewById(R.id.et_end_time);
        editTextLocation = findViewById(R.id.edit_text_event_location);
        editTextDescription = findViewById(R.id.edit_text_event_description);
        buttonSave = findViewById(R.id.button_save_event);

        // 获取事件ID
        eventId = getIntent().getLongExtra(EXTRA_EVENT_ID, -1);

        // 设置时间选择器点击事件
        et_start_time.setOnClickListener(v -> showDatePicker(true));
        et_end_time.setOnClickListener(v -> showDatePicker(false));

        // 加载事件详情或初始化新事件
        if (eventId != -1) {
            loadEventDetails(eventId);
        } else {
            currentEvent = new Event();
            // 设置默认时间为当前时间
            LocalDateTime now = LocalDateTime.now();
            currentEvent.setStartTime(now);
            currentEvent.setEndTime(now.plusHours(1));
            updateTimeButtons();
        }

        buttonSave.setOnClickListener(v -> saveEvent());
    }

    private void loadEventDetails(long id) {
        currentEvent = eventDao.getEventById(id);
        if (currentEvent != null) {
            editTextTitle.setText(currentEvent.getTitle());
            editTextLocation.setText(currentEvent.getLocation());
            editTextDescription.setText(currentEvent.getDescription());

            // 初始化日历对象
            if (currentEvent.getStartTime() != null) {
                startCalendar.set(
                        currentEvent.getStartTime().getYear(),
                        currentEvent.getStartTime().getMonthValue() - 1,
                        currentEvent.getStartTime().getDayOfMonth(),
                        currentEvent.getStartTime().getHour(),
                        currentEvent.getStartTime().getMinute()
                );
            }

            if (currentEvent.getEndTime() != null) {
                endCalendar.set(
                        currentEvent.getEndTime().getYear(),
                        currentEvent.getEndTime().getMonthValue() - 1,
                        currentEvent.getEndTime().getDayOfMonth(),
                        currentEvent.getEndTime().getHour(),
                        currentEvent.getEndTime().getMinute()
                );
            }

            updateTimeButtons();
        } else {
            Toast.makeText(this, "事件未找到", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void showTimePicker(final boolean isStartTime) {
        Calendar calendar = isStartTime ? startCalendar : endCalendar;

        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            // 更新事件对象
            LocalDateTime newTime = LocalDateTime.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,
                    minute
            );

            if (isStartTime) {
                currentEvent.setStartTime(newTime);
            } else {
                currentEvent.setEndTime(newTime);
            }

            updateTimeButtons();
        },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show();
    }
    private void showDatePicker(final boolean isStartTime) {
        Calendar calendar = isStartTime ? startCalendar : endCalendar;
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            showTimePicker(isStartTime);
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTimeButtons() {
        // 格式化时间显示
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (currentEvent.getStartTime() != null) {
            et_start_time.setText("开始时间: " + currentEvent.getStartTime().format(timeFormatter));
        }

        if (currentEvent.getEndTime() != null) {
            et_end_time.setText("结束时间: " + currentEvent.getEndTime().format(timeFormatter));
        }
    }

    private void saveEvent() {
        String title = editTextTitle.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // 验证输入
        if (title.isEmpty()) {
            editTextTitle.setError("请输入标题");
            return;
        }

        // 验证时间
        if (currentEvent.getStartTime() == null || currentEvent.getEndTime() == null) {
            Toast.makeText(this, "请设置开始和结束时间", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentEvent.getEndTime().isBefore(currentEvent.getStartTime())) {
            Toast.makeText(this, "结束时间不能早于开始时间", Toast.LENGTH_SHORT).show();
            return;
        }

        // 更新事件对象
        currentEvent.setTitle(title);
        currentEvent.setLocation(location);
        currentEvent.setDescription(description);

        // 保存到数据库
        long result;
        if (eventId == -1) {
            // 新增事件
            result = eventDao.insertEvent(currentEvent);
        } else {
            // 更新事件
            result = eventDao.updateEvent(currentEvent) > 0 ? 1 : -1;
        }

        if (result != -1) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}
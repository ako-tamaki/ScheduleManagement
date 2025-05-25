package net.micode.schedulemanagement.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String description;
    private long userId; // 新增字段

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter DB_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.CHINA);

    // 构造函数
    public Event() {
    }

    public Event(String title, LocalDateTime startTime, LocalDateTime endTime, String location, String description) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
    }

    // 带 ID 的构造函数，用于从数据库读取数据
    public Event(long id, String title, LocalDateTime startTime, LocalDateTime endTime, String location, String description) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
    }
    // 带 ID 的构造函数，用于从数据库读取数据
    public Event(long id, String title, LocalDateTime startTime, LocalDateTime endTime, String location, String description,long user_id) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.userId = user_id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public static LocalDateTime stringToLocalDateTime(String str) {
        return LocalDateTime.parse(str, DB_TIME_FORMATTER);
    }

    public static String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime.format(DB_TIME_FORMATTER);
    }
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

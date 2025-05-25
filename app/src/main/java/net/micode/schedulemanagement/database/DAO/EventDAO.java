package net.micode.schedulemanagement.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.micode.schedulemanagement.database.SMDBHelper;
import net.micode.schedulemanagement.entity.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private static final String TAG = "EventDAO";
    private final SMDBHelper dbHelper;

    public EventDAO(Context context) {
        dbHelper = SMDBHelper.getInstance(context.getApplicationContext());
    }

    // 插入事件（包含user_id）
    public long insertEvent(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SMDBHelper.COLUMN_EVENT_TITLE, event.getTitle());
        values.put(SMDBHelper.COLUMN_EVENT_START_TIME, Event.localDateTimeToString(event.getStartTime()));
        values.put(SMDBHelper.COLUMN_EVENT_END_TIME, Event.localDateTimeToString(event.getEndTime()));
        values.put(SMDBHelper.COLUMN_EVENT_LOCATION, event.getLocation());
        values.put(SMDBHelper.COLUMN_EVENT_DESCRIPTION, event.getDescription());
        values.put(SMDBHelper.COLUMN_EVENT_USER_ID, event.getUserId());

        long result = -1;
        db.beginTransaction();
        try {
            result = db.insert(SMDBHelper.TABLE_EVENT_INFO, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted event with ID: " + result);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting event", e);
        } finally {
            db.endTransaction();
        }
        return result;
    }

    // 根据ID查询事件（包含user_id）
    public Event getEventById(long eventId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Event event = null;
        Cursor cursor = null;

        try {
            cursor = db.query(
                    SMDBHelper.TABLE_EVENT_INFO,
                    new String[]{
                            SMDBHelper.COLUMN_EVENT_ID,
                            SMDBHelper.COLUMN_EVENT_TITLE,
                            SMDBHelper.COLUMN_EVENT_START_TIME,
                            SMDBHelper.COLUMN_EVENT_END_TIME,
                            SMDBHelper.COLUMN_EVENT_LOCATION,
                            SMDBHelper.COLUMN_EVENT_DESCRIPTION,
                            SMDBHelper.COLUMN_EVENT_USER_ID
                    },
                    SMDBHelper.COLUMN_EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                event = new Event(
                        cursor.getLong(cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EVENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EVENT_TITLE)),
                        Event.stringToLocalDateTime(cursor.getString(cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EVENT_START_TIME))),
                        Event.stringToLocalDateTime(cursor.getString(cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EVENT_END_TIME))),
                        cursor.getString(cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EVENT_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EVENT_DESCRIPTION))
                );
                event.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EVENT_USER_ID)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting event by ID", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return event;
    }

    // 更新事件（包含user_id）
    public int updateEvent(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SMDBHelper.COLUMN_EVENT_TITLE, event.getTitle());
        values.put(SMDBHelper.COLUMN_EVENT_START_TIME, Event.localDateTimeToString(event.getStartTime()));
        values.put(SMDBHelper.COLUMN_EVENT_END_TIME, Event.localDateTimeToString(event.getEndTime()));
        values.put(SMDBHelper.COLUMN_EVENT_LOCATION, event.getLocation());
        values.put(SMDBHelper.COLUMN_EVENT_DESCRIPTION, event.getDescription());
        values.put(SMDBHelper.COLUMN_EVENT_USER_ID, event.getUserId());

        int rowsAffected = 0;
        db.beginTransaction();
        try {
            rowsAffected = db.update(
                    SMDBHelper.TABLE_EVENT_INFO,
                    values,
                    SMDBHelper.COLUMN_EVENT_ID + " = ?",
                    new String[]{String.valueOf(event.getId())}
            );
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error updating event", e);
        } finally {
            db.endTransaction();
        }
        return rowsAffected;
    }

    // 删除事件
    public int deleteEvent(long eventId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;
        db.beginTransaction();
        try {
            rowsAffected = db.delete(
                    SMDBHelper.TABLE_EVENT_INFO,
                    SMDBHelper.COLUMN_EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)}
            );
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting event", e);
        } finally {
            db.endTransaction();
        }
        return rowsAffected;
    }

    // 获取所有事件（按开始时间排序）
    public List<Event> getAllEvents() {
        return getEventsWithQuery(null, null);
    }

    // 根据用户ID获取事件
    public List<Event> getEventsByUserId(long userId) {
        return getEventsWithQuery(
                SMDBHelper.COLUMN_EVENT_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
    }

    // 获取日期范围内的事件
    public List<Event> getEventsBetweenDates(LocalDateTime start, LocalDateTime end) {
        String startStr = Event.localDateTimeToString(start);
        String endStr = Event.localDateTimeToString(end);

        return getEventsWithQuery(
                SMDBHelper.COLUMN_EVENT_START_TIME + " BETWEEN ? AND ?",
                new String[]{startStr, endStr}
        );
    }

    // 通用查询方法
    private List<Event> getEventsWithQuery(String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Event> events = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    SMDBHelper.TABLE_EVENT_INFO,
                    new String[]{
                            SMDBHelper.COLUMN_EVENT_ID,
                            SMDBHelper.COLUMN_EVENT_TITLE,
                            SMDBHelper.COLUMN_EVENT_START_TIME,
                            SMDBHelper.COLUMN_EVENT_END_TIME,
                            SMDBHelper.COLUMN_EVENT_LOCATION,
                            SMDBHelper.COLUMN_EVENT_DESCRIPTION,
                            SMDBHelper.COLUMN_EVENT_USER_ID
                    },
                    selection,
                    selectionArgs,
                    null, null,
                    SMDBHelper.COLUMN_EVENT_START_TIME + " ASC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Event event = new Event(
                            cursor.getLong(0), // id
                            cursor.getString(1), // title
                            Event.stringToLocalDateTime(cursor.getString(2)), // startTime
                            Event.stringToLocalDateTime(cursor.getString(3)), // endTime
                            cursor.getString(4), // location
                            cursor.getString(5)  // description
                    );
                    event.setUserId(cursor.getLong(6)); // userId
                    events.add(event);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying events", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return events;
    }
}
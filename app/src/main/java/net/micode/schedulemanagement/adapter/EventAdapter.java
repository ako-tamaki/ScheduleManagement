package net.micode.schedulemanagement.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import net.micode.schedulemanagement.R;
import net.micode.schedulemanagement.entity.Event;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of Events.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private OnEventClickListener listener; // 点击事件监听器

    // 用于格式化显示在列表中的时间
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Interface for handling item click events.
     */
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    /**
     * Constructs a new EventAdapter.
     *
     * @param eventList The list of events to display.
     * @param listener  The click listener for event items.
     */
    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    /**
     * Updates the list of events and notifies the adapter to refresh the UI.
     *
     * @param newEventList The new list of events.
     */
    public void setEventList(List<Event> newEventList) {
        this.eventList = newEventList;
        notifyDataSetChanged(); // 通知数据已更改，刷新列表
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建 ViewHolder
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // 绑定数据到 ViewHolder
        Event currentEvent = eventList.get(position);

        // 格式化时间范围并设置给 TextView
        String timeRange = "";
        if (currentEvent.getStartTime() != null && currentEvent.getEndTime() != null) {
            timeRange = currentEvent.getStartTime().format(DISPLAY_FORMATTER) + " - " +
                    currentEvent.getEndTime().format(DISPLAY_FORMATTER);
        } else if (currentEvent.getStartTime() != null) {
            timeRange = "Starts: " + currentEvent.getStartTime().format(DISPLAY_FORMATTER);
        } else if (currentEvent.getEndTime() != null) {
            timeRange = "Ends: " + currentEvent.getEndTime().format(DISPLAY_FORMATTER);
        }
        holder.textViewTimeRange.setText(timeRange);

        // 设置事件标题
        holder.textViewTitle.setText(currentEvent.getTitle());

        // 设置点击监听器
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(currentEvent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // 返回列表项的总数
        return eventList.size();
    }

    /**
     * ViewHolder for an individual event item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTimeRange;
        public TextView textViewTitle;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // 获取布局中的视图引用
            textViewTimeRange = itemView.findViewById(R.id.text_view_event_time_range);
            textViewTitle = itemView.findViewById(R.id.text_view_event_title);
        }
    }
}

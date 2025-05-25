package net.micode.schedulemanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.micode.schedulemanagement.EventDetailActivity;
import net.micode.schedulemanagement.R;
import net.micode.schedulemanagement.adapter.EventAdapter;
import net.micode.schedulemanagement.database.DAO.EventDAO;
import net.micode.schedulemanagement.database.DAO.UserDAO;
import net.micode.schedulemanagement.entity.Event;
import net.micode.schedulemanagement.entity.User;
import net.micode.schedulemanagement.util.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private static final String TAG = "ScheduleFragment";

    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private EventDAO eventDAO;
    private UserDAO userDAO;
    private List<Event> eventList = new ArrayList<>();
    private TextView textViewEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDAO = new EventDAO(requireContext());
        userDAO = new UserDAO(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerViewEvents = view.findViewById(R.id.recycler_view_events_fragment);
        textViewEmpty = view.findViewById(R.id.text_view_empty);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        eventAdapter = new EventAdapter(eventList, this);
        recyclerViewEvents.setAdapter(eventAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentUserEvents();
    }

    private void loadCurrentUserEvents() {
        // 从 SharedPreferences 获取用户名
        String currentUsername = PreferenceManager.getUsername();

        if (currentUsername == null || currentUsername.isEmpty()) {
            Log.e(TAG, "User not logged in");
            showEmptyView(true);
            return;
        }

        User user = userDAO.getUserByUsername(currentUsername);
        if (user == null) {
            Log.e(TAG, "User not found: " + currentUsername);
            showEmptyView(true);
            return;
        }

        eventList = eventDAO.getEventsByUserId(user.getId());
        Log.d(TAG, "Loaded " + eventList.size() + " events for user: " + currentUsername);
        Collections.sort(eventList, (e1, e2) ->
                e2.getStartTime().compareTo(e1.getStartTime()) // 降序排列
        );

        eventAdapter.setEventList(eventList);
        showEmptyView(eventList.isEmpty());
    }

    private void showEmptyView(boolean show) {
        if (textViewEmpty != null) {
            textViewEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerViewEvents.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(requireContext(), EventDetailActivity.class);
        intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.getId());
        startActivity(intent);
    }
}
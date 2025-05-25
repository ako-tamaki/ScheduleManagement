// 文件路径: adapter/ViewPagerAdapter.java
package net.micode.schedulemanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import net.micode.schedulemanagement.fragments.HomeFragment;
import net.micode.schedulemanagement.fragments.ManualFragment;
import net.micode.schedulemanagement.fragments.ScheduleFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new ScheduleFragment();
            case 2: return new ManualFragment();
            default: throw new IllegalArgumentException("无效位置: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 总页数
    }
}
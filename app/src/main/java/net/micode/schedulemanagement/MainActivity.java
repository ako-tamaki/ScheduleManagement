// 文件路径: MainActivity.java
package net.micode.schedulemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import androidx.viewpager2.widget.ViewPager2;
import net.micode.schedulemanagement.adapter.ViewPagerAdapter;
import net.micode.schedulemanagement.decorator.TodayDecorator;
import net.micode.schedulemanagement.util.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnCreateEvent;
    private MaterialCalendarView calendarView;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(this);

        // 检查登录状态（防止直接通过Intent进入）
        if (!preferenceManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        String username = PreferenceManager.getUsername();
        Toast.makeText(this, "欢迎 " + username, Toast.LENGTH_SHORT).show();

        // 初始化 ViewPager2 和适配器
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // 绑定 TabLayout 和 ViewPager2
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("首页"); break;
                case 1: tab.setText("日程"); break;
                case 2: tab.setText("手册"); break;
            }
        }).attach();

        btnCreateEvent = findViewById(R.id.fab_create_event);
        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
//            intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // 添加确认对话框
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("确认注销")
                .setMessage("确定要退出当前账号吗？")
                .setPositiveButton("确定", (dialog, which) -> logout())
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    //注销功能
    private void logout() {
        preferenceManager.clearSession();
        redirectToLogin();
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
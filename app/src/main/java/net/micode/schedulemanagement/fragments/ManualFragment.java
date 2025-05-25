package net.micode.schedulemanagement.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.micode.schedulemanagement.MainActivity;
import net.micode.schedulemanagement.R;

public class ManualFragment extends Fragment {

    private TextView tv_content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_content = view.findViewById(R.id.tv_content);

        tv_content.setOnLongClickListener(v -> {
            Toast.makeText(requireActivity(),"再长按也没有东西了QAQ",Toast.LENGTH_SHORT).show();
            Log.d("ako","tamaki-ako remind:long click happened!!!");
            return true;
        });

        tv_content.setLongClickable(true);
        tv_content.setMovementMethod(null);       // 禁用链接点击
        tv_content.setTextIsSelectable(false);    // 禁用文本选择

        String content = "用户手册\n点击右下角按钮即可开始创建事件\n\nDeveloper:\ntamaki-ako,burtyang,\nKO8848,yuanxinxiangyang\n\n\n";
        tv_content.setText(content);
    }
}
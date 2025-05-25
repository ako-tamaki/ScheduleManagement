package net.micode.schedulemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import net.micode.schedulemanagement.database.DAO.UserDAO;
import net.micode.schedulemanagement.entity.User;
import net.micode.schedulemanagement.util.SecurityUtils;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword, etConfirmPassword, etEmail;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化DAO
        userDAO = new UserDAO(this);

        // 绑定视图
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etEmail = findViewById(R.id.et_email);

        // 注册按钮点击事件
        findViewById(R.id.btn_register).setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // 输入验证
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请填写所有必填项", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查用户名是否已存在
        if (userDAO.getUserByUsername(username) != null) {
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            return;
        }

        // 密码加密
        String hashedPassword = SecurityUtils.sha256(password);

        // 创建用户对象
        User newUser = new User(username, hashedPassword, email);

        // 插入数据库
        long result = userDAO.insertUser(newUser);
        if (result != -1) {
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            // 跳转到登录界面
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
        }
    }
}
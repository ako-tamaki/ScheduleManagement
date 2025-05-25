package net.micode.schedulemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import net.micode.schedulemanagement.util.PreferenceManager;
import net.micode.schedulemanagement.util.SecurityUtils;
import net.micode.schedulemanagement.database.DAO.UserDAO;
import net.micode.schedulemanagement.entity.User;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText et_username, et_password;
    private UserDAO userDAO;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化组件
        userDAO = new UserDAO(this);
        preferenceManager = new PreferenceManager(this);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);
        Button btnForgotPassword = findViewById(R.id.btn_forgot_password);

        // 检查登录状态
        if (preferenceManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // 登录按钮点击
        btnLogin.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isValidCredentials(username, password)) {
                // 保存登录状态
                preferenceManager.saveLoginStatus(true, username);
                navigateToMainActivity();
            }
        });

        // 注册按钮点击
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // 忘记密码点击
        btnForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgetPasswordActivity.class)));
    }

    private boolean isValidCredentials(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show();
            return false;
        }

        String hashedPassword = SecurityUtils.sha256(password);
        if (!hashedPassword.equals(user.getPassword())) {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
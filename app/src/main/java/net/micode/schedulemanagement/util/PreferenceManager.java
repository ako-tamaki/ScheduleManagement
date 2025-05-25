// PreferenceManager.java
package net.micode.schedulemanagement.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";

    private  static SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    // 保存登录状态和用户名
    public void saveLoginStatus(boolean isLoggedIn, String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // 检查是否已登录
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // 获取当前用户名
    public static String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // 清除登录信息（用于注销）
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
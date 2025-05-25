package net.micode.schedulemanagement.database.DAO;// dao/UserDao.java


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import net.micode.schedulemanagement.database.SMDBHelper;
import net.micode.schedulemanagement.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the User table.
 * Handles CRUD operations for User data.
 */
public class UserDAO {

    private static final String TAG = "UserDao"; // 用于日志输出

    private SMDBHelper dbHelper;

    /**
     * Constructs a new UserDao.
     *
     * @param context The application context to get the SMDBHelper instance.
     */
    public UserDAO(Context context) {
        // 获取 SMDBHelper 的单例实例
        dbHelper = SMDBHelper.getInstance(context.getApplicationContext());
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The User object to insert.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insertUser(User user) {
        // 获取可写数据库实例
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long newRowId = -1;

        // 使用 ContentValues 存储要插入的数据
        ContentValues values = new ContentValues();
        // 注意：这里假设 User 对象的 getter 方法是可用的
        values.put(SMDBHelper.COLUMN_USERNAME, user.getUsername());
        values.put(SMDBHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(SMDBHelper.COLUMN_EMAIL, user.getEmail());

        // 开始一个事务，确保插入操作的原子性
        db.beginTransaction();
        try {
            // 执行插入操作
            newRowId = db.insert(SMDBHelper.TABLE_USER_INFO, null, values);
            // 如果插入成功，设置事务成功
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted user: " + user.getUsername() + " with ID: " + newRowId);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user: " + user.getUsername(), e);
        } finally {
            // 结束事务
            db.endTransaction();
            // 不需要手动关闭 db，SMDBHelper 会管理连接
        }

        return newRowId;
    }

    /**
     * Retrieves a user from the database by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object if found, or null if not found.
     */
    public User getUserById(long userId) {
        // 获取可读数据库实例
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            // 执行查询操作
            cursor = db.query(
                    SMDBHelper.TABLE_USER_INFO,   // 表名
                    new String[]{SMDBHelper.COLUMN_USER_ID, SMDBHelper.COLUMN_USERNAME, SMDBHelper.COLUMN_PASSWORD, SMDBHelper.COLUMN_EMAIL}, // 查询所有列
                    SMDBHelper.COLUMN_USER_ID + " = ?", // WHERE _id = ?
                    new String[]{String.valueOf(userId)}, // ? 的值
                    null, // 不分组
                    null, // 不过滤分组
                    null  // 不排序
            );

            // 遍历查询结果
            if (cursor != null && cursor.moveToFirst()) {
                // 从 Cursor 中读取数据并创建 User 对象
                int idIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_USER_ID);
                int usernameIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_PASSWORD);
                int emailIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EMAIL);

                long id = cursor.getLong(idIndex);
                String username = cursor.getString(usernameIndex);
                String password = cursor.getString(passwordIndex);
                String email = cursor.getString(emailIndex);

                // 使用带 ID 的构造函数创建 User 对象
                user = new User(id, username, password, email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by ID: " + userId, e);
        } finally {
            // 关闭 Cursor，释放资源
            if (cursor != null) {
                cursor.close();
            }
            // 不需要手动关闭 db
        }

        return user;
    }

    /**
     * Retrieves a user from the database by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, or null if not found.
     */
    public User getUserByUsername(String username) {
        // 获取可读数据库实例
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            // 执行查询操作
            cursor = db.query(
                    SMDBHelper.TABLE_USER_INFO,   // 表名
                    new String[]{SMDBHelper.COLUMN_USER_ID, SMDBHelper.COLUMN_USERNAME, SMDBHelper.COLUMN_PASSWORD, SMDBHelper.COLUMN_EMAIL}, // 查询所有列
                    SMDBHelper.COLUMN_USERNAME + " = ?", // WHERE username = ?
                    new String[]{username}, // ? 的值
                    null, // 不分组
                    null, // 不过滤分组
                    null  // 不排序
            );

            // 遍历查询结果
            if (cursor != null && cursor.moveToFirst()) {
                // 从 Cursor 中读取数据并创建 User 对象
                int idIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_USER_ID);
                int usernameIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_PASSWORD);
                int emailIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EMAIL);

                long id = cursor.getLong(idIndex);
                String foundUsername = cursor.getString(usernameIndex);
                String password = cursor.getString(passwordIndex);
                String email = cursor.getString(emailIndex);

                // 使用带 ID 的构造函数创建 User 对象
                user = new User(id, foundUsername, password, email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by username: " + username, e);
        } finally {
            // 关闭 Cursor，释放资源
            if (cursor != null) {
                cursor.close();
            }
            // 不需要手动关闭 db
        }

        return user;
    }


    /**
     * Updates an existing user in the database.
     *
     * @param user The User object with updated data. The ID must be set.
     * @return The number of rows affected.
     */
    public int updateUser(User user) {
        // 获取可写数据库实例
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        // 使用 ContentValues 存储要更新的数据
        ContentValues values = new ContentValues();
        values.put(SMDBHelper.COLUMN_USERNAME, user.getUsername());
        values.put(SMDBHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(SMDBHelper.COLUMN_EMAIL, user.getEmail());

        // 开始一个事务
        db.beginTransaction();
        try {
            // 执行更新操作
            rowsAffected = db.update(
                    SMDBHelper.TABLE_USER_INFO, // 表名
                    values, // 要更新的数据
                    SMDBHelper.COLUMN_USER_ID + " = ?", // WHERE 子句
                    new String[]{String.valueOf(user.getId())} // WHERE 子句中的参数值
            );
            db.setTransactionSuccessful();
            Log.d(TAG, "Updated " + rowsAffected + " rows for user ID: " + user.getId());
        } catch (Exception e) {
            Log.e(TAG, "Error updating user ID: " + user.getId(), e);
        } finally {
            // 结束事务
            db.endTransaction();
            // 不需要手动关闭 db
        }

        return rowsAffected;
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return The number of rows affected.
     */
    public int deleteUser(long userId) {
        // 获取可写数据库实例
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = 0;

        // 开始一个事务
        db.beginTransaction();
        try {
            // 执行删除操作
            rowsAffected = db.delete(
                    SMDBHelper.TABLE_USER_INFO, // 表名
                    SMDBHelper.COLUMN_USER_ID + " = ?", // WHERE 子句
                    new String[]{String.valueOf(userId)} // WHERE 子句中的参数值
            );
            db.setTransactionSuccessful();
            Log.d(TAG, "Deleted " + rowsAffected + " rows for user ID: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user ID: " + userId, e);
        } finally {
            // 结束事务
            db.endTransaction();
            // 不需要手动关闭 db
        }

        return rowsAffected;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all User objects. Returns an empty list if no users are found.
     */
    public List<User> getAllUsers() {
        // 获取可读数据库实例
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> userList = new ArrayList<>();
        Cursor cursor = null;

        try {
            // 执行原始 SQL 查询
            cursor = db.rawQuery("SELECT * FROM " + SMDBHelper.TABLE_USER_INFO, null);

            // 遍历查询结果
            if (cursor != null && cursor.moveToFirst()) {
                // 获取列索引
                int idIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_USER_ID);
                int usernameIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_PASSWORD);
                int emailIndex = cursor.getColumnIndexOrThrow(SMDBHelper.COLUMN_EMAIL);

                do {
                    // 从 Cursor 中读取数据
                    long id = cursor.getLong(idIndex);
                    String username = cursor.getString(usernameIndex);
                    String password = cursor.getString(passwordIndex);
                    String email = cursor.getString(emailIndex);

                    // 使用带 ID 的构造函数创建 User 对象并添加到列表中
                    User user = new User(id, username, password, email);
                    userList.add(user);
                } while (cursor.moveToNext()); // 移动到下一行
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users", e);
        } finally {
            // 关闭 Cursor
            if (cursor != null) {
                cursor.close();
            }
            // 不需要手动关闭 db
        }

        return userList;
    }

    // 你可能还需要添加其他查询方法，例如根据用户名和密码查询用户用于登录验证等。
}
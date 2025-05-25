package net.micode.schedulemanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // 用于日志输出，方便调试

// 继承自 SQLiteOpenHelper，这是 Android 提供的一个用于管理数据库创建和版本管理的辅助类
public class SMDBHelper extends SQLiteOpenHelper {

    // =====================================================================
    // 单例模式相关变量和方法
    // =====================================================================
    // 静态变量，用于保存唯一的 SMDBHelper 实例
    private static SMDBHelper sInstance;

    // 私有构造方法，防止外部直接通过 new 创建实例
    // 构造方法需要 Context 参数来初始化父类 SQLiteOpenHelper
    private SMDBHelper(Context context) {
        // 使用 Application Context 可以避免内存泄漏，因为 Application Context 的生命周期与应用相同
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 公共的静态方法，用于获取 SMDBHelper 的唯一实例
    // 如果实例不存在，则创建一个新的实例
    // 第一次调用时需要传入 Context
    public static synchronized SMDBHelper getInstance(Context context) {
        // synchronized 关键字确保在多线程环境下，只有一个线程可以同时访问这段代码块，保证单例的唯一性
        if (sInstance == null) {
            // 如果实例为空，则创建一个新的实例
            sInstance = new SMDBHelper(context);
        }
        // 返回唯一的实例
        return sInstance;
    }

    // =====================================================================
    // 数据库常量和表结构定义 (与之前相同)
    // =====================================================================
    // 数据库文件的名称
    private static final String DATABASE_NAME = "sm_database.db";
    // 数据库版本号，当数据库结构发生变化时，需要增加版本号，以便触发 onUpgrade 方法
    private static final int DATABASE_VERSION = 2;

    // 用户表名
    public static final String TABLE_USER_INFO = "user_info";
    // 用户表列名
    public static final String COLUMN_USER_ID = "_id"; // 通常使用 _id 作为主键列名，这是 Android 推荐的
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";

    // 事件表名
    public static final String TABLE_EVENT_INFO = "event_info";
    // 事件表列名
    public static final String COLUMN_EVENT_ID = "_id"; // 事件表主键
    public static final String COLUMN_EVENT_TITLE = "title";
    // SQLite 不直接支持 LocalDateTime 类型，通常将其存储为文本（ISO 8601 格式）或长整型（时间戳）
    // 这里我们选择存储为文本，使用 ISO 8601 格式 (YYYY-MM-DD HH:MM:SS)
    public static final String COLUMN_EVENT_START_TIME = "start_time";
    public static final String COLUMN_EVENT_END_TIME = "end_time";
    public static final String COLUMN_EVENT_LOCATION = "location";
    public static final String COLUMN_EVENT_DESCRIPTION = "description";
    public static final String COLUMN_EVENT_USER_ID = "user_id";


    // =====================================================================
    // 数据库创建和升级逻辑 (与之前相同，只是现在构造方法是私有的)
    // =====================================================================

    // 这个方法在数据库第一次被创建时调用
    // 在这里执行创建表的 SQL 语句
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("SMDBHelper", "Creating database tables...");

        // SQL 语句：创建用户表 user_info
        // CREATE TABLE user_info (
        //     _id INTEGER PRIMARY KEY AUTOINCREMENT,
        //     username TEXT NOT NULL UNIQUE,
        //     password TEXT NOT NULL,
        //     email TEXT
        // );
        final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + TABLE_USER_INFO + " (" + // CREATE TABLE 关键字用于创建新表，后面跟着表名
                        COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // _id 列：
                        // INTEGER: 数据类型为整数
                        // PRIMARY KEY: 设置为主键，唯一标识表中的每一行数据
                        // AUTOINCREMENT: 使该列的值自动递增，每次插入新行时都会自动生成一个唯一的 ID
                        COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " + // username 列：
                        // TEXT: 数据类型为文本字符串
                        // NOT NULL: 约束，表示该列的值不能为空
                        // UNIQUE: 约束，表示该列的值在整个表中必须是唯一的
                        COLUMN_PASSWORD + " TEXT NOT NULL, " + // password 列：
                        // TEXT: 数据类型为文本字符串
                        // NOT NULL: 约束，表示该列的值不能为空
                        COLUMN_EMAIL + " TEXT" + // email 列：
                        // TEXT: 数据类型为文本字符串
                        // (没有 NOT NULL 或 UNIQUE 约束, 表示可以为空或重复)
                        ");"; // 结束 SQL 语句的分号

        // 执行创建用户表的 SQL 语句
        db.execSQL(SQL_CREATE_USER_TABLE);
        Log.i("SMDBHelper", "User table created.");

        // SQL 语句：创建事件表 event_info
        // CREATE TABLE event_info (
        //     _id INTEGER PRIMARY KEY AUTOINCREMENT,
        //     title TEXT NOT NULL,
        //     start_time TEXT NOT NULL, -- 存储为 ISO 8601 格式的文本
        //     end_time TEXT NOT NULL,   -- 存储为 ISO 8601 格式的文本
        //     location TEXT,
        //     description TEXT
        // );
        final String SQL_CREATE_EVENT_TABLE =
                "CREATE TABLE " + TABLE_EVENT_INFO + " (" + // CREATE TABLE 关键字用于创建新表，后面跟着表名
                        COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // _id 列：主键，自动递增整数
                        COLUMN_EVENT_TITLE + " TEXT NOT NULL, " + // title 列：文本，不能为空
                        COLUMN_EVENT_START_TIME + " TEXT NOT NULL, " + // start_time 列：文本，不能为空 (存储时间字符串)
                        COLUMN_EVENT_END_TIME + " TEXT NOT NULL, " + // end_time 列：文本，不能为空 (存储时间字符串)
                        COLUMN_EVENT_LOCATION + " TEXT, " + // location 列：文本 (可以为空)
                        COLUMN_EVENT_DESCRIPTION + " TEXT," +// description 列：文本 (可以为空)
                        COLUMN_EVENT_USER_ID + " INTEGER NOT NULL, " +  // 使用user_info._id作为外键
                        "FOREIGN KEY (" + COLUMN_EVENT_USER_ID + ") REFERENCES " +
                        TABLE_USER_INFO + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                        ");";

        // 执行创建事件表的 SQL 语句
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        Log.i("SMDBHelper", "Event table created.");
    }

    // 这个方法在数据库需要升级时调用 (当 DATABASE_VERSION 增加时)
    // 通常在这里执行 ALTER TABLE 语句来修改表结构，或者直接删除旧表重建
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("SMDBHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_INFO);
            onCreate(db);
        }
    }

    // 你可能还需要添加获取读写数据库实例的方法
    // 注意：这些方法现在通过单例实例调用
    public SQLiteDatabase getDatabase() {
        // 返回可写数据库实例
        return getWritableDatabase();
    }

    // 在应用关闭时，或者不再需要数据库连接时，可以调用关闭方法
    // 注意：通常不需要频繁关闭和打开数据库，SQLiteOpenHelper 会帮你管理
    // 但如果你确定不再需要数据库连接，可以调用父类的 close() 方法
    // @Override
    // public synchronized void close() {
    //     super.close();
    //     sInstance = null; // 将实例置空，以便下次调用 getInstance 时重新创建
    // }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;"); // 启用外键
        }
    }
}
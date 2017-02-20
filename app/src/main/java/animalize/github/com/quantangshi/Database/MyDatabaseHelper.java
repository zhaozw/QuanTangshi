package animalize.github.com.quantangshi.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;

import animalize.github.com.quantangshi.Data.Poem;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    // 检查数据库版本，sqlite数据库版本小于此值时，重新解压
    private static final int CHECK_TANGSHI_VERSION = 1;
    private static final String TAG = "MyDatabaseHelper";

    // 静态变量
    private static MyDatabaseHelper mHelper;
    private static int mPoemCount = -1;

    private MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase getDB(Context context) {
        SQLiteDatabase db;
        if (mHelper == null) {
            mHelper = new MyDatabaseHelper(context.getApplicationContext());

            // attach
            String mQuantangshi = MyAssetsDatabaseHelper.getDBPath(context, false);
            db = mHelper.getWritableDatabase();
            db.execSQL("ATTACH DATABASE '" +
                    mQuantangshi + "' AS 'tangshi';");

            // 检查版本
            Cursor cursor = db.rawQuery("SELECT value FROM tangshi.dbinfo WHERE name='ver'", null);
            cursor.moveToFirst();
            int db_ver = Integer.parseInt(cursor.getString(0));
            cursor.close();
            //Log.i(TAG, "数据库版本: " + db_ver);

            if (CHECK_TANGSHI_VERSION > db_ver) {
                // detach
                db.execSQL("DETACH DATABASE tangshi");

                // del file
                File file = new File(mQuantangshi);
                file.delete();

                // re create
                MyAssetsDatabaseHelper.getDBPath(context, true);

                // attach again
                db.execSQL("ATTACH DATABASE '" +
                        mQuantangshi + "' AS 'tangshi'");
            }

        } else {
            db = mHelper.getWritableDatabase();
        }

        return db;
    }

    // 总共有多少首诗
    public static synchronized int getPoemCount(Context context) {
        if (mPoemCount == -1) {
            SQLiteDatabase db = getDB(context);

            String sql = "SELECT count(*) FROM tangshi.poem";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            mPoemCount = c.getInt(0);
            c.close();
        }
        return mPoemCount;
    }

    // 得到指定id的诗
    public static synchronized Poem getPoemById(Context context, int id) {
        SQLiteDatabase db = getDB(context);

        String sql = "SELECT * FROM tangshi.poem WHERE id=?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(id)});
        c.moveToFirst();
        Poem p = null;
        try {
            p = new Poem(
                    c.getInt(c.getColumnIndex("id")),
                    new String(c.getBlob(c.getColumnIndex("title")), "utf-16LE"),
                    new String(c.getBlob(c.getColumnIndex("author")), "utf-16LE"),
                    new String(c.getBlob(c.getColumnIndex("txt")), "utf-16LE")
            );
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        c.close();

        return p;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // tag表
        String sql = "CREATE TABLE tag (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tag_name TEXT NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE INDEX tname_idx ON tag(tag_name);";
        db.execSQL(sql);

        // tag_map表
        sql = "CREATE TABLE tag_map (" +
                "pid INTEGER, " +
                "tid INTEGER);";
        db.execSQL(sql);

        sql = "CREATE INDEX pid_idx ON tag_map(pid);";
        db.execSQL(sql);

        sql = "CREATE INDEX tid_idx ON tag_map(tid);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

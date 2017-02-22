package animalize.github.com.quantangshi.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.Poem;
import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.MyApplication;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    // 检查数据库版本，sqlite数据库版本小于此值时，重新解压
    private static final int CHECK_TANGSHI_VERSION = 1;
    private static final String TAG = "MyDatabaseHelper";

    // 静态变量
    private static MyDatabaseHelper mHelper;
    private static SQLiteDatabase mDb;
    private static int mPoemCount = -1;

    private MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void init() {
        if (mHelper == null) {
            Context context = MyApplication.getContext();
            mHelper = new MyDatabaseHelper(context);

            // attach
            String mQuantangshi = MyAssetsDatabaseHelper.getDBPath(context, false);
            mDb = mHelper.getWritableDatabase();
            mDb.execSQL("ATTACH DATABASE '" +
                    mQuantangshi + "' AS 'tangshi';");

            // 检查版本
            Cursor cursor = mDb.rawQuery("SELECT value FROM tangshi.dbinfo WHERE name='ver'", null);
            cursor.moveToFirst();
            int db_ver = Integer.parseInt(cursor.getString(0));
            cursor.close();
            //Log.i(TAG, "数据库版本: " + db_ver);

            if (CHECK_TANGSHI_VERSION > db_ver) {
                // detach
                mDb.execSQL("DETACH DATABASE tangshi");

                // del file
                File file = new File(mQuantangshi);
                file.delete();

                // re create
                MyAssetsDatabaseHelper.getDBPath(context, true);

                // attach again
                mDb.execSQL("ATTACH DATABASE '" +
                        mQuantangshi + "' AS 'tangshi'");
            }
        }
    }

    // 总共有多少首诗
    public static synchronized int getPoemCount() {
        if (mPoemCount == -1) {
            init();

            String sql = "SELECT count(*) FROM tangshi.poem";
            Cursor c = mDb.rawQuery(sql, null);
            c.moveToFirst();
            mPoemCount = c.getInt(0);
            c.close();
        }
        return mPoemCount;
    }

    // 得到指定id的诗
    public static synchronized Poem getPoemById(int id) {
        init();

        String sql = "SELECT * FROM tangshi.poem WHERE id=?";
        Cursor c = mDb.rawQuery(sql, new String[]{String.valueOf(id)});
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

    // 得到tag list
    public static synchronized List<TagInfo> getTagsByPoem(int pid) {
        init();

        String sql = "SELECT tag.id, tag.name, tag.count " +
                "FROM tag, tag_map " +
                "WHERE tag_map.pid=? AND tag_map.tid=tag.id";
        Cursor c = mDb.rawQuery(sql, new String[]{String.valueOf(pid)});

        List<TagInfo> l = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                TagInfo ti = new TagInfo(
                        c.getInt(0),
                        c.getString(1),
                        c.getInt(2)
                );
                l.add(ti);
            } while (c.moveToNext());
        }
        c.close();

        return l;
    }

    // 给诗添加一个tag
    public static synchronized boolean addTagToPoem(String tag, int pid) {
        init();

        int tid = getTagID(tag);

        if (tid != -1) {
            // 已在tag表
            if (poemHasTagID(pid, tid)) {
                // 诗已存在此tag
                return false;
            } else {
                mDb.execSQL("BEGIN");

                // 添加到tag_map
                addTagMap(pid, tid);
                // count + 1
                int count = MyDatabaseHelper.getTagCount(tid);
                updateTagCount(tid, count + 1);

                mDb.execSQL("COMMIT");
            }
        } else {
            // 没在tag表
            mDb.execSQL("BEGIN");

            tid = MyDatabaseHelper.addTag(tag);
            MyDatabaseHelper.addTagMap(pid, tid);

            mDb.execSQL("COMMIT");
        }

        return true;
    }

    // 删除一个tag
    public static synchronized boolean delTagFromPoem(int pid, TagInfo info) {
        init();

        if (!poemHasTagID(pid, info.getId())) {
            // 没有
            return false;
        }

        mDb.execSQL("BEGIN");

        // 从tag_map表删除
        delFromTagMap(pid, info.getId());
        // count - 1
        int count = MyDatabaseHelper.getTagCount(info.getId());
        updateTagCount(info.getId(), count - 1);

        mDb.execSQL("COMMIT");

        return true;
    }

    // 从tag_map删除
    private static void delFromTagMap(int pid, int tid) {
        mDb.delete("tag_map",
                "pid=? AND tid=?",
                new String[]{String.valueOf(pid), String.valueOf(tid)});
    }


    // 返回tag id，-1为没有
    private static int getTagID(String tag) {
        String sql = "SELECT id FROM tag WHERE name=?";
        Cursor c = mDb.rawQuery(sql, new String[]{tag});
        if (!c.moveToFirst()) {
            c.close();
            return -1;
        }

        int tid = c.getInt(0);
        c.close();
        return tid;
    }

    // 诗是否有tag id
    private static boolean poemHasTagID(int pid, int tid) {
        String sql = "SELECT * FROM tag_map WHERE pid=? AND tid=?";
        Cursor c = mDb.rawQuery(sql,
                new String[]{String.valueOf(pid), String.valueOf(tid)}
        );
        if (!c.moveToFirst()) {
            c.close();
            return false;
        }

        c.close();
        return true;
    }

    // 添加到tag表，count设为1，返回tag id
    private static int addTag(String tag) {
        ContentValues cv = new ContentValues();
        cv.put("name", tag);
        cv.put("count", 1);

        return (int) mDb.insert("tag", null, cv);
    }

    // 得到tag count，-1为不存在
    private static int getTagCount(int tid) {
        String sql = "SELECT count FROM tag WHERE id=?";
        Cursor c = mDb.rawQuery(sql, new String[]{String.valueOf(tid)});
        if (!c.moveToFirst()) {
            c.close();
            return -1;
        }

        int count = c.getInt(0);
        c.close();
        return count;
    }

    // 更新tag count
    private static void updateTagCount(int tid, int count) {
        String sql = "UPDATE tag SET count=? WHERE id=?";
        mDb.execSQL(sql, new String[]{String.valueOf(count), String.valueOf(tid)});
    }

    // 添加到tag_map
    private static int addTagMap(int pid, int tid) {
        ContentValues cv = new ContentValues();
        cv.put("pid", pid);
        cv.put("tid", tid);

        return (int) mDb.insert("tag_map", null, cv);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // tag表
        String sql = "CREATE TABLE tag (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "count INTEGER);";
        db.execSQL(sql);

        sql = "CREATE INDEX tname_idx ON tag(name);";
        db.execSQL(sql);

        sql = "CREATE INDEX tcount_idx ON tag(count);";
        db.execSQL(sql);

        // tag_map表
        sql = "CREATE TABLE tag_map (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pid INTEGER," +
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

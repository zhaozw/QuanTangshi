package animalize.github.com.quantangshi.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.MyApplication;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDatabaseHelper";

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 3;

    private static final String ENCODING = "utf-16LE";

    // 静态变量
    private static MyDatabaseHelper mHelper;
    private static SQLiteDatabase mDb;
    private static int mPoemCount = -1;
    private static RawPoem mCachePoem;

    private MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void init() {
        if (mHelper == null) {
            Context context = MyApplication.getContext();
            mHelper = new MyDatabaseHelper(context);

            // create or update
            String mQuantangshi = MyAssetsDatabaseHelper.getDBPath(context, false);

            // attach
            mDb = mHelper.getWritableDatabase();
            mDb.execSQL("ATTACH DATABASE '" +
                    mQuantangshi + "' AS 'tangshi';");
        }
    }

    // 总共有多少首诗
    public static synchronized int getPoemCount() {
        if (mPoemCount != -1) {
            return mPoemCount;
        }

        init();

        String sql = "SELECT count(*) FROM tangshi.poem";
        Cursor c = mDb.rawQuery(sql, null);
        c.moveToFirst();
        mPoemCount = c.getInt(0);
        c.close();

        return mPoemCount;
    }

    // 随机一首
    public static synchronized RawPoem randomPoem() {
        init();

        int poemCount = MyDatabaseHelper.getPoemCount();
        Random rand = new Random();

        RawPoem p;
        do {
            int id = rand.nextInt(poemCount) + 1;
            p = MyDatabaseHelper.getPoemById(id);
        } while (p.getText().equals(""));

        return p;
    }

    // 得到指定id的诗
    public static synchronized RawPoem getPoemById(int id) {
        if (mCachePoem != null && mCachePoem.getId() == id) {
            return mCachePoem;
        }

        init();

        String sql = "SELECT * FROM tangshi.poem WHERE id=?";
        Cursor c = mDb.rawQuery(sql, new String[]{String.valueOf(id)});
        c.moveToFirst();
        RawPoem p = null;
        try {
            p = new RawPoem(
                    c.getInt(c.getColumnIndex("id")),
                    new String(c.getBlob(c.getColumnIndex("title")), ENCODING),
                    new String(c.getBlob(c.getColumnIndex("author")), ENCODING),
                    new String(c.getBlob(c.getColumnIndex("txt")), ENCODING)
            );
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        c.close();

        mCachePoem = p;
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

    // 得到所有tag
    public static synchronized List<TagInfo> getTags() {
        init();

        String sql = "SELECT id, name, count " +
                "FROM tag " +
                "ORDER BY count DESC, id ASC";
        Cursor c = mDb.rawQuery(sql, null);

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

        // 当引用为0时删除
        sql = "DELETE FROM tag WHERE id=? AND count<=0";
        mDb.execSQL(sql, new String[]{String.valueOf(tid)});
    }

    // 添加到tag_map
    private static int addTagMap(int pid, int tid) {
        ContentValues cv = new ContentValues();
        cv.put("pid", pid);
        cv.put("tid", tid);

        return (int) mDb.insert("tag_map", null, cv);
    }

    // 用tag列表搜索
    public static synchronized ArrayList<InfoItem> queryByTags(List<String> tags) {
        init();

        int max = tags.size() - 1;
        if (max == -1) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; ; i++) {
            sb.append('\'');
            sb.append(tags.get(i));
            sb.append('\'');
            if (i == max) {
                break;
            }
            sb.append(',');
        }

        String sql = "SELECT p.id, p.title, p.author " +
                "FROM tangshi.poem p " +
                "INNER JOIN tag_map tm " +
                "ON p.id = tm.pid " +
                "INNER JOIN tag t " +
                "ON tm.tid = t.id " +
                "WHERE t.name in (" + sb + ") " +
                "GROUP BY p.id " +
                "HAVING COUNT(DISTINCT t.id) = " + tags.size() + " " +
                "ORDER BY tm.id DESC";

        Cursor c = mDb.rawQuery(sql, null);
        ArrayList<InfoItem> l = new ArrayList<>();
        try {
            if (c.moveToFirst()) {
                do {
                    InfoItem ri = new InfoItem(
                            c.getInt(0),
                            new String(c.getBlob(1), ENCODING),
                            new String(c.getBlob(2), ENCODING)
                    );
                    l.add(ri);
                } while (c.moveToNext());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        c.close();

        return l;
    }

    // 得到最近列表
    public static synchronized ArrayList<InfoItem> getRecentList() {
        init();

        String sql = "SELECT pid, title, author " +
                "FROM recent " +
                "ORDER BY id DESC";
        Cursor c = mDb.rawQuery(sql, null);

        ArrayList<InfoItem> l = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                InfoItem ri = new InfoItem(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2)
                );
                l.add(ri);
            } while (c.moveToNext());
        }
        c.close();

        return l;
    }

    // 添加到最近列表
    public static synchronized void addToRecentList(InfoItem info, int limit) {
        init();

        // 已有的话，先删
        mDb.delete("recent", "pid=?", new String[]{String.valueOf(info.getId())});

        // add
        ContentValues cv = new ContentValues();
        cv.put("pid", info.getId());
        cv.put("title", info.getTitle());
        cv.put("author", info.getAuthor());
        cv.put("time", (int) (System.currentTimeMillis() / 1000));
        mDb.insert("recent", null, cv);

        // get count
        String sql = "SELECT count(*) FROM recent";
        Cursor c = mDb.rawQuery(sql, null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();

        if (count <= limit) {
            return;
        }

        // del old
        sql = "DELETE FROM recent " +
                "WHERE ID IN (SELECT ID " +
                "FROM recent " +
                "ORDER BY ID ASC " +
                "LIMIT ?);";
        int deltop = count - limit;
        mDb.execSQL(sql, new String[]{String.valueOf(deltop)});
    }

    // 得到邻近的
    public static synchronized ArrayList<InfoItem> getNeighbourList(int id,
                                                                    int window) {
        init();

        int left = id - window / 2;
        int right = id + window / 2;

        String sql = "SELECT id,title,author FROM tangshi.poem " +
                "WHERE ? <= id AND id <= ? " +
                "ORDER BY id";
        Cursor c = mDb.rawQuery(sql, new String[]{
                String.valueOf(left),
                String.valueOf(right)});

        ArrayList<InfoItem> l = new ArrayList<>();
        if (c.moveToFirst()) do {
            InfoItem ri;
            try {
                ri = new InfoItem(
                        c.getInt(0),
                        new String(c.getBlob(1), ENCODING),
                        new String(c.getBlob(2), ENCODING)
                );
                l.add(ri);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } while (c.moveToNext());
        c.close();

        return l;
    }

    // vacuum
    public static synchronized void vacuum() {
        init();
        mDb.execSQL("VACUUM");
    }

    // 备份数据库
    public static synchronized void backup(File target) {
        init();

        // VACUUM
        String sql = "VACUUM";
        mDb.execSQL(sql);

        // 关闭
        mHelper.close();
        mHelper = null;

        // 复制文件
        File dbFile = MyApplication
                .getContext()
                .getDatabasePath(DATABASE_NAME);

        copyFile(dbFile, target);

        // 重新打开
        init();
    }

    // 还原数据库
    public static synchronized void restore(File source) {
        init();

        // 关闭
        mHelper.close();
        mHelper = null;

        // 复制文件
        File dbFile = MyApplication
                .getContext()
                .getDatabasePath(DATABASE_NAME);

        copyFile(source, dbFile);

        // 重新打开
        init();
    }

    public static synchronized int getDBSize() {
        File dbFile = MyApplication
                .getContext()
                .getDatabasePath(DATABASE_NAME);

        try {
            int size;
            FileInputStream fis = new FileInputStream(dbFile);
            size = fis.available();
            fis.close();
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void copyFile(File src, File dst) {
        InputStream in;
        try {
            if (!dst.exists()) {
                dst.createNewFile();
            }

            in = new FileInputStream(src);

            OutputStream out = new FileOutputStream(dst);

            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // recent表, add in db ver 2
        sql = "CREATE TABLE recent (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pid INTEGER, " +
                "title TEXT, " +
                "author TEXT, " +
                "time INTEGER);";
        db.execSQL(sql);

        sql = "CREATE INDEX recent_pid_idx ON recent(pid);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql;

        if (oldVersion < 2) {
            // recent表
            sql = "CREATE TABLE recent (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "pid INTEGER, " +
                    "title TEXT, " +
                    "author TEXT, " +
                    "time INTEGER);";
            db.execSQL(sql);
        }

        if (oldVersion < 3) {
            sql = "CREATE INDEX recent_pid_idx ON recent(pid);";
            db.execSQL(sql);
        }
    }
}

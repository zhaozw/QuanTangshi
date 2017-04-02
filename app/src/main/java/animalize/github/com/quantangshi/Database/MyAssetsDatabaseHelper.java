package animalize.github.com.quantangshi.Database;

import android.content.Context;

public class MyAssetsDatabaseHelper extends com.readystatesoftware.sqliteasset.SQLiteAssetHelper {

    private static final String DATABASE_NAME = "tangshi.db";
    // 更新数据库时，递增此变量
    private static final int DATABASE_VERSION = 2;
    private static String mPath;

    private MyAssetsDatabaseHelper(Context context) {
        super(context,
                DATABASE_NAME,
                context.getFilesDir().getAbsolutePath(),
                null,
                DATABASE_VERSION);
        setForcedUpgrade();
    }

    public static String getDBPath(Context context, boolean reCreate) {
        if (mPath == null || reCreate) {
            MyAssetsDatabaseHelper db = new MyAssetsDatabaseHelper(context.getApplicationContext());
            db.getReadableDatabase();
            db.close();

            mPath = context.getFilesDir().getAbsolutePath() +
                    "/" +
                    DATABASE_NAME;
        }
        return mPath;
    }

}
package animalize.github.com.quantangshi.Database;

import android.content.Context;

public class MyAssetsDatabaseHelper extends com.readystatesoftware.sqliteasset.SQLiteAssetHelper {

    private static final String TAG = "MyAssetsDatabaseHelper";
    private static String mPath;

    private static final String DATABASE_NAME = "tangshi.db";
    private static final int DATABASE_VERSION = 1;

    private MyAssetsDatabaseHelper(Context context) {
        super(context,
                DATABASE_NAME,
                context.getFilesDir().getAbsolutePath(),
                null,
                DATABASE_VERSION);
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
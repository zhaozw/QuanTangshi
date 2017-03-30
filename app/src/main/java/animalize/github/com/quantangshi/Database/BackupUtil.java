package animalize.github.com.quantangshi.Database;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anima on 17-3-23.
 */

public class BackupUtil {
    private final static String backupDir = "QuanTangshi";
    private static String path;

    public static String getDirName() {
        return backupDir;
    }

    // 返回备份路径
    public static String getBackupDir() {
        if (path == null) {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard, backupDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            path = dir.getAbsolutePath();
        }
        return path;
    }

    private static File getFile() {
        String p = getBackupDir();

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
        String fn = "QTS" + dateFormat.format(now) + ".db";

        // 拼接
        File path1 = new File(p);
        File path2 = new File(path1, fn);

        return path2;
    }

    public static File backup() {
        File file = getFile();
        MyDatabaseHelper.backup(file);

        return file;
    }

    public static void Restore(String path) {
        File file = new File(path);
        MyDatabaseHelper.restore(file);

        // 重载最近列表
        RecentAgent.reload();
    }
}

package animalize.github.com.quantangshi.Database;

import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by anima on 17-3-23.
 */

public class BackupUtil {

    // 得到SD卡路径
    // 返回路径，SD卡不存在时返回null
    @Nullable
    public static String getSDPath() {
        //判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);

        if (sdCardExist) {
            //获取根目录
            File sdDir = Environment.getExternalStorageDirectory();
            return sdDir.getAbsolutePath();
        } else {
            return null;
        }
    }
}

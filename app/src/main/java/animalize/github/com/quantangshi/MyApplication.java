package animalize.github.com.quantangshi;

import android.app.Application;
import android.content.Context;

/**
 * Created by anima on 17-2-13.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}

package animalize.github.com.quantangshi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class OneFragmentActivity extends AppCompatActivity {

    // 生成Fragment对象
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(android.R.id.content);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}

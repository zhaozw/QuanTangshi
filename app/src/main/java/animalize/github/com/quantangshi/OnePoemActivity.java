package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class OnePoemActivity extends OneFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Intent i = getIntent();
        int id = i.getIntExtra("id", 0);
        return OnePoemFragment.newInstance(id);
    }

    public static void actionStart(Context context, int id){
        Intent i = new Intent(context, OnePoemActivity.class);
        i.putExtra("id", id);
        context.startActivity(i);
    }
}

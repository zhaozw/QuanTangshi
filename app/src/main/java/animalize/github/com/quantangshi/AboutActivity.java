package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutActivity extends AppCompatActivity {

    public static void actionStart(Context context) {
        Intent i = new Intent(context, AboutActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.about_toolbar);
        tb.setTitle("关于本应用");
        setSupportActionBar(tb);

        // 版本
        TextView tv = (TextView) findViewById(R.id.version);
        tv.setLines(2);

        String versionName = "版本：" + BuildConfig.VERSION_NAME + '\n';

        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        DateFormat df = new SimpleDateFormat("编译于：yyyy-MM-dd HH:mm");
        tv.setText(versionName + df.format(buildDate));

        // html
        tv = (TextView) findViewById(R.id.about_text);
        Spanned s;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = Html.fromHtml(getString(R.string.about),
                    Html.FROM_HTML_MODE_LEGACY);
        } else {
            s = Html.fromHtml(getString(R.string.about));
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(s);
    }
}

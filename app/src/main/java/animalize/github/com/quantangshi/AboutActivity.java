package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AboutActivity extends AppCompatActivity {

    private boolean isChecking = false;
    private Button checkButton;
    private TextView versionInfo;

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
        DateFormat df = new SimpleDateFormat("编译于：yyyy-MM-dd E HH:mm", Locale.getDefault());
        tv.setText(versionName + df.format(buildDate));

        // mail
        tv = (TextView) findViewById(R.id.about_mail);
        StringBuilder sb = new StringBuilder();
        sb.append("<u>反馈意见 mal");
        sb.append("incns@");
        sb.append("163");
        sb.append(".com</u>");
        Spanned s = Utils.getFromHtml(sb.toString());
        tv.setText(s);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(Intent.ACTION_SENDTO);
                StringBuilder sb = new StringBuilder();
                sb.append("mail");
                sb.append("to:");
                sb.append("mal");
                sb.append("incns@");
                sb.append("163");
                sb.append(".com");
                data.setData(Uri.parse(sb.toString()));
                data.putExtra(Intent.EXTRA_SUBJECT, "关于安卓应用《离线全唐诗》");
                data.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(data);
            }
        });

        // html
        tv = (TextView) findViewById(R.id.about_text);
        s = Utils.getFromHtml(getString(R.string.about));
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(s);

        // 检查更新
        checkButton = (Button) findViewById(R.id.check_update);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecking) {
                    return;
                }

                v.setEnabled(false);
                new CheckTask(AboutActivity.this).execute();
            }
        });

        versionInfo = (TextView) findViewById(R.id.ver_info);
    }

    public void updateUI(String s) {
        checkButton.setEnabled(true);

        if (s == null) {
            versionInfo.setText("检查失败");
        } else {
            versionInfo.setText("GitHub上最新版本: " + s);
        }

        versionInfo.setVisibility(View.VISIBLE);
    }

    private static class CheckTask extends AsyncTask<Void, Void, String> {
        private static final String verURL = "https://raw.githubusercontent.com/animalize/QuanTangshi/master/app/build.gradle";
        private WeakReference<AboutActivity> ref;

        public CheckTask(AboutActivity about) {
            ref = new WeakReference<>(about);
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            String html;
            try {
                Request request = new Request.Builder()
                        .url(verURL)
                        .build();

                Response response = client.newCall(request).execute();
                html = response.body().string();
            } catch (IOException e) {
                //e.printStackTrace();
                return null;
            }

            String p = "versionName \"(.*?)\"";

            Pattern pattern = Pattern.compile(p, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(html);
            if (!matcher.find()) {
                return null;
            }

            return matcher.group(1);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            AboutActivity about = ref.get();
            if (about == null) {
                return;
            }

            about.updateUI(s);
        }
    }
}

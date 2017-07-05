package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class StudyResultActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String PREFIX = "缩放比例：";
    private WebView webView;
    private LinearLayout ratioPanel;
    private Button ratioOK, ratioCancel;
    private TextView ratioText;
    private SeekBar ratioBar;
    private int ratio;

    public static void actionStart(Context context, String word, String url) {
        Intent i = new Intent(context, StudyResultActivity.class);
        i.putExtra("word", word);
        i.putExtra("url", url);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // intent
        Intent intent = getIntent();
        String word = intent.getStringExtra("word");
        String url = intent.getStringExtra("url");

        setContentView(R.layout.activity_study_result);

        // toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.browser_toolbar);
        tb.setTitle(word);
        setSupportActionBar(tb);

        // 要在setSupportActionBar之后
        tb.setOnMenuItemClickListener(this);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ratio = loadRatio();

        // webview
        webView = (WebView) findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(ratio);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
    }

    private void initWidgets() {
        if (ratioPanel != null) {
            return;
        }

        ratioPanel = (LinearLayout) findViewById(R.id.ratio_panel);

        ratioOK = (Button) findViewById(R.id.ratio_ok);
        ratioOK.setOnClickListener(this);

        ratioCancel = (Button) findViewById(R.id.ratio_cancel);
        ratioCancel.setOnClickListener(this);

        ratioText = (TextView) findViewById(R.id.ratio_text);
        ratioText.setText(PREFIX + ratio);

        ratioBar = (SeekBar) findViewById(R.id.ratio_bar);
        ratioBar.setProgress(ratio);
        ratioBar.setOnSeekBarChangeListener(this);
    }

    private int loadRatio() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        return pref.getInt("ratio", 100);
    }

    private void saveRatio(int ratio) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("ratio", ratio);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_font_ratio:
                initWidgets();
                ratioPanel.setVisibility(View.VISIBLE);
                break;

            case R.id.set_clear_caches:
                webView.clearCache(true);
                String s = "已清除本应用的WebView缓存。\n通常不必执行此操作。";
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ratio_cancel:
                ratioPanel.setVisibility(View.GONE);

                // 还原界面
                webView.getSettings().setTextZoom(ratio);
                ratioText.setText(PREFIX + ratio);
                ratioBar.setProgress(ratio);
                break;

            case R.id.ratio_ok:
                ratioPanel.setVisibility(View.GONE);

                // 保存设置
                ratio = webView.getSettings().getTextZoom();
                saveRatio(ratio);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = progress / 5;
        progress = progress * 5;
        ratioText.setText(PREFIX + progress);

        webView.getSettings().setTextZoom(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

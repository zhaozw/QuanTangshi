package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class StudyResultActivity extends AppCompatActivity {

    private WebView webView;

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

        webView = (WebView) findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(150);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
    }
}

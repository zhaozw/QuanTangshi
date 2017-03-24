package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.PoemWrapper;
import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;


public class StudyActivity extends AppCompatActivity implements View.OnClickListener, TagView.OnTagClickListener, RadioGroup.OnCheckedChangeListener {
    private final static String SAVE_ID = "poem_id";
    private final static String SAVE_WORDS = "search_words";

    private PoemWrapper poemWrapper;
    private int mode;

    private TextView title;
    private TextView author;
    private TextView text;

    private Button button_t;
    private Button button_s;

    private EditText edit_item;
    private TagContainerLayout items;

    private RadioGroup engines;

    public static void actionStart(Context context, int id) {
        Intent i = new Intent(context, StudyActivity.class);
        i.putExtra("id", id);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        title = (TextView) findViewById(R.id.poem_title);
        author = (TextView) findViewById(R.id.poem_author);
        text = (TextView) findViewById(R.id.poem_text);
        edit_item = (EditText) findViewById(R.id.item_edit);

        button_t = (Button) findViewById(R.id.button_t);
        button_t.setOnClickListener(this);

        button_s = (Button) findViewById(R.id.button_s);
        button_s.setOnClickListener(this);

        // 编辑按钮
        Button b = (Button) findViewById(R.id.add_item);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.edit_back);
        b.setOnClickListener(this);
        b = (Button) findViewById(R.id.edit_clear);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.edit_space);
        if (b != null) {
            b.setOnClickListener(this);
        }

        items = (TagContainerLayout) findViewById(R.id.items);
        items.setIsTagViewClickable(true);
        items.setOnTagClickListener(this);

        // 参数
        int id;
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt(SAVE_ID, 1);

            ArrayList<String> tags = savedInstanceState.getStringArrayList(SAVE_WORDS);
            items.setTags(tags);
        } else {
            Intent intent = getIntent();
            id = intent.getIntExtra("id", 1);
        }
        RawPoem poem = MyDatabaseHelper.getPoemById(id);
        poemWrapper = new PoemWrapper(poem);

        // 读取配置
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        mode = pref.getInt("mode", 1);
        int engine = pref.getInt("engine", 0);

        changeButtonMode(mode, false);
        if (savedInstanceState == null) {
            showPoem();
        }

        engines = (RadioGroup) findViewById(R.id.radioGroup);
        if (engine == 0) {
            engines.check(R.id.search_baidu);
        } else if (engine == 1) {
            engines.check(R.id.search_baiduhanyu);
        } else if (engine == 2) {
            engines.check(R.id.search_baidubaike);
        } else if (engine == 3) {
            engines.check(R.id.search_baidubaike_direct);
        } else if (engine == 4 && findViewById(R.id.search_baiduimg) != null) {
            engines.check(R.id.search_baiduimg);
        } else {
            // 正常情况下不会执行这里
            engines.check(R.id.search_baidu);
        }
        engines.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 保存信息
        outState.putInt(SAVE_ID, poemWrapper.getID());

        List<String> tags = items.getTags();
        outState.putStringArrayList(SAVE_WORDS, (ArrayList<String>) tags);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        showPoem();
    }

    private void showPoem() {
        setText(title, poemWrapper.getTitle(mode));
        setText(author, poemWrapper.getAuthor(mode));
        setText(text, poemWrapper.getText(mode));
    }

    private void setText(TextView view, final String str) {
        ArrayList<Position> list = new ArrayList<>();

        for (int i = 0; i < str.length(); i++) {
            final int temp_i = i;
            final char c = str.charAt(temp_i);
            if (c == '\n') {
                continue;
            }

            if (Character.isHighSurrogate(c) &&
                    temp_i + 1 < str.length() &&
                    Character.isLowSurrogate(str.charAt(temp_i + 1))) {
                list.add(new Position(temp_i, temp_i + 2));
                i += 1;
            } else {
                list.add(new Position(temp_i, temp_i + 1));
            }
        }

        SpannableString ss = new SpannableString(str);
        for (Position p : list) {
            String s = str.substring(p.begin, p.end);
            MyClickableSpan clickable = new MyClickableSpan(StudyActivity.this, s);

            ss.setSpan(clickable, p.begin, p.end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (view.getMovementMethod() == null) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
        view.setText(ss);
    }

    private void addToItem(String s) {
        String t = edit_item.getText().toString();
        t = t + s;

        edit_item.setText(t);
        edit_item.setSelection(t.length());
    }

    public void changeButtonMode(int mode, boolean save) {
        this.mode = mode;

        if (mode == 0) {
            button_t.setTextColor(Color.BLUE);
            button_s.setTextColor(Color.BLACK);
        } else {
            button_t.setTextColor(Color.BLACK);
            button_s.setTextColor(Color.BLUE);
        }

        if (save) {
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt("mode", mode);
            editor.apply();
        }
    }

    @Override
    public void onClick(View v) {
        String s;

        switch (v.getId()) {
            case R.id.button_t:
                changeButtonMode(0, true);
                showPoem();
                break;

            case R.id.button_s:
                changeButtonMode(1, true);
                showPoem();
                break;

            case R.id.add_item:
                s = edit_item.getText().toString().trim();
                if (!s.equals("")) {
                    items.addTag(s);
                }

                edit_item.setText("");
                break;

            case R.id.edit_space:
                s = edit_item.getText().toString();
                edit_item.setText(s + " ");
                edit_item.setSelection(edit_item.getText().length());
                break;

            case R.id.edit_back:
                s = edit_item.getText().toString();
                if (s.length() == 0) {
                    break;
                }

                char c = s.charAt(s.length() - 1);
                if (Character.isLowSurrogate(c) &&
                        s.length() >= 2 &&
                        Character.isHighSurrogate(s.charAt(s.length() - 2))) {
                    s = s.substring(0, s.length() - 2);
                } else {
                    s = s.substring(0, s.length() - 1);
                }

                edit_item.setText(s);
                edit_item.setSelection(edit_item.getText().length());
                break;

            case R.id.edit_clear:
                edit_item.setText("");
                edit_item.clearFocus();
                break;
        }
    }

    @Override
    public void onTagClick(int position, String text) {
        int engine = engines.getCheckedRadioButtonId();
        String url = null;

        switch (engine) {
            case R.id.search_baidu:
                url = "http://www.baidu.com/s?wd=" + text;
                break;
            case R.id.search_baiduhanyu:
                url = "http://hanyu.baidu.com/zici/s?wd=" + text;
                break;
            case R.id.search_baidubaike:
                url = "http://baike.baidu.com/search?word=" + text;
                break;
            case R.id.search_baidubaike_direct:
                url = "http://baike.baidu.com/item/" + text;
                break;
            case R.id.search_baiduimg:
                url = "http://image.baidu.com/search/wiseala?tn=wiseala&word=" + text;
                break;
        }

        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {
        items.removeTag(position);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        int engine = 0;
        switch (checkedId) {
            case R.id.search_baidu:
                engine = 0;
                break;
            case R.id.search_baiduhanyu:
                engine = 1;
                break;
            case R.id.search_baidubaike:
                engine = 2;
                break;
            case R.id.search_baidubaike_direct:
                engine = 3;
                break;
            case R.id.search_baiduimg:
                engine = 4;
                break;
        }

        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("engine", engine);
        editor.apply();
    }

    private static class Position {
        private int begin, end;

        public Position(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }
    }

    private static class MyClickableSpan extends ClickableSpan {
        private WeakReference<StudyActivity> weakRef;
        private String s;

        public MyClickableSpan(StudyActivity activity, String s) {
            this.weakRef = new WeakReference<>(activity);
            this.s = s;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.rgb(0, 0, 0));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            StudyActivity activity = weakRef.get();
            if (activity != null) {
                activity.addToItem(s);
            }
        }
    }
}

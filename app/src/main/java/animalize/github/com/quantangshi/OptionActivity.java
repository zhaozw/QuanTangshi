package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Data.Typeset;
import animalize.github.com.quantangshi.UIPoem.PoemView;

public class OptionActivity extends AppCompatActivity {

    private RawPoem samplePoem = new RawPoem(
            666,
            "在学习界面可以看到完整的诗标题，很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长",
            "诗的作者",
            "北斗七星高，哥舒夜带刀。\n故人西辞黄鹤楼，烟花三月下扬州。\n头上何所有，翠微盍叶垂鬓唇。\n朝避猛虎，夕避长蛇。"
    );
    private PoemView poemView;

    private CheckBox jumpToRead;

    private TextView titleLinesTextView;
    private SeekBar titleLinesSeekbar;

    private TextView titleSizeTextView;
    private SeekBar titleSizeSeekbar;

    private TextView textSizeTextView;
    private SeekBar textSizeSeekbar;

    private TextView lineSpaceTextView;
    private SeekBar lineSpaceSeekbar;

    private TextView lineBreakTextView;
    private SeekBar lineBreakSeekbar;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, OptionActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        poemView = (PoemView) findViewById(R.id.poem_view);
        poemView.setPoem(samplePoem, true);

        // 启动后跳转
        jumpToRead = (CheckBox) findViewById(R.id.jump_to_read);
        Context c = MyApplication.getContext();
        SharedPreferences sp = c.getSharedPreferences(
                "global",
                Context.MODE_PRIVATE);
        jumpToRead.setChecked(sp.getBoolean("jump", false));

        jumpToRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context c = MyApplication.getContext();
                SharedPreferences.Editor editor = c.getSharedPreferences(
                        "global",
                        Context.MODE_PRIVATE).edit();

                editor.putBoolean("jump", isChecked);
                editor.apply();
            }
        });

        // 标题行数
        titleLinesTextView = (TextView) findViewById(R.id.title_lines_text);
        titleLinesSeekbar = (SeekBar) findViewById(R.id.title_lines_seekbar);
        titleLinesSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                titleLinesTextView.setText("标题最大行数: " + progress);

                Typeset typeset = poemView.getTypeset();
                typeset.setTitleLines(progress);
                typeset.saveConfig();

                poemView.updateTypeset();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        titleLinesSeekbar.setProgress(poemView.getTypeset().getTitleLines());

        // 标题字体
        titleSizeTextView = (TextView) findViewById(R.id.title_size_text);
        titleSizeSeekbar = (SeekBar) findViewById(R.id.title_size_seekbar);
        titleSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                titleSizeTextView.setText("标题字体: " + progress);

                Typeset typeset = poemView.getTypeset();
                typeset.setTitleSize(progress);
                typeset.saveConfig();

                poemView.updateTypeset();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        titleSizeSeekbar.setProgress(poemView.getTypeset().getTitleSize());

        // 诗文字体
        textSizeTextView = (TextView) findViewById(R.id.text_size_text);
        textSizeSeekbar = (SeekBar) findViewById(R.id.text_size_seekbar);
        textSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSizeTextView.setText("诗文字体: " + progress);

                Typeset typeset = poemView.getTypeset();
                typeset.setTextSize(progress);
                typeset.saveConfig();

                poemView.updateTypeset();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        textSizeSeekbar.setProgress(poemView.getTypeset().getTextSize());

        // 行间距
        lineSpaceTextView = (TextView) findViewById(R.id.line_space_text);
        lineSpaceSeekbar = (SeekBar) findViewById(R.id.line_space_seekbar);
        lineSpaceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lineSpaceTextView.setText("行间距: " + progress);

                Typeset typeset = poemView.getTypeset();
                typeset.setLineSpace(progress);
                typeset.saveConfig();

                poemView.updateTypeset();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        lineSpaceSeekbar.setProgress(poemView.getTypeset().getLineSpace());

        // 换行
        lineBreakTextView = (TextView) findViewById(R.id.line_break_text);
        lineBreakSeekbar = (SeekBar) findViewById(R.id.line_break_seekbar);
        lineBreakSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lineBreakTextView.setText("换行字数: " + progress);

                Typeset typeset = poemView.getTypeset();
                typeset.setLineBreak(progress);
                typeset.saveConfig();

                poemView.updateTypeset();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        lineBreakSeekbar.setProgress(poemView.getTypeset().getLineBreak());
    }
}

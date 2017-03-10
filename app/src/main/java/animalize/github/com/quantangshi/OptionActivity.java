package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Data.Typeset;
import animalize.github.com.quantangshi.UIPoem.PoemView;

public class OptionActivity extends AppCompatActivity {

    private Typeset typeset = new Typeset();
    private RawPoem samplePoem = new RawPoem(
            666,
            "诗的标题，很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长",
            "诗的作者",
            "北斗七星高，哥舒夜带刀。\n故人西辞黄鹤楼，烟花三月下扬州。\n头上何所有，翠微盍叶垂鬓唇。\n朝避猛虎，夕避长蛇。"
    );
    private PoemView poemView;
    private TextView titleLinesTextView;
    private SeekBar titleLinesSeekbar;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, OptionActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        poemView = (PoemView) findViewById(R.id.poem_view);

        titleLinesTextView = (TextView) findViewById(R.id.title_lines_text);
        titleLinesSeekbar = (SeekBar) findViewById(R.id.title_lines_seekbar);
        titleLinesSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int max = progress + 1;
                titleLinesTextView.setText("标题最大行数: " + max);
                typeset.setTitleLines(max);
                poemView.setPoem(samplePoem);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}

package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class OnePoemActivity extends AppCompatActivity {

    private OnePoemFragment poemFragment;
    private TagFragment tagFragment;

    private TextView mPIDText;
    private Button mTButton;
    private Button mSButton;
    private Button mSpButton;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, OnePoemActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.poem_main);

        // 得到 诗fragment
        FragmentManager fm = getSupportFragmentManager();
        poemFragment = (OnePoemFragment) fm.findFragmentById(R.id.fragment_one_poem);
        tagFragment = (TagFragment) fm.findFragmentById(R.id.fragment_tag);

        // 控件、按钮
        mPIDText = (TextView) findViewById(R.id.textview_poem_id);

        mTButton = (Button) findViewById(R.id.button_t);
        mTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemFragment.setMode(0);
                updateUI(0);
            }
        });
        mSButton = (Button) findViewById(R.id.button_s);
        mSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemFragment.setMode(1);
                updateUI(1);
            }
        });
        mSpButton = (Button) findViewById(R.id.button_sp);
        mSpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemFragment.setMode(2);
                updateUI(2);
            }
        });

        // 下一首随机诗
        Button b = (Button) findViewById(R.id.next_random);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomPoem();
            }
        });

        randomPoem();

        // 配置SlidingUpPanelLayout
        //SlidingUpPanelLayout slide = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }

    private void randomPoem() {
        // 随机一首诗
        int pid = poemFragment.randomPoem();
        updateUI(poemFragment.getMode());
        mPIDText.setText(String.valueOf(pid));
        tagFragment.setPoemId(pid);
    }

    private void updateUI(int mode) {
        if (mode == 0) {
            mTButton.setTextColor(Color.BLUE);
            mSButton.setTextColor(Color.BLACK);
            mSpButton.setTextColor(Color.BLACK);
        } else if (mode == 1) {
            mTButton.setTextColor(Color.BLACK);
            mSButton.setTextColor(Color.BLUE);
            mSpButton.setTextColor(Color.BLACK);
        } else {
            mTButton.setTextColor(Color.BLACK);
            mSButton.setTextColor(Color.BLACK);
            mSpButton.setTextColor(Color.BLUE);
        }
    }
}

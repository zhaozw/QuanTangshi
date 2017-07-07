package animalize.github.com.quantangshi.UIPoem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.R;
import animalize.github.com.quantangshi.StudyActivity;


public class OnePoemActivity
        extends AppCompatActivity
        implements PoemController, View.OnClickListener, SlidingUpPanelLayout.PanelSlideListener {

    private final static int NO = 1;
    private final static int NEIGHBOR = 2;
    private final static int RECENT = 3;
    private final static int TAG = 4;

    private RawPoem currentPoem;

    private SlidingUpPanelLayout slider;
    // 供ANCHORED时调整height用
    private FrameLayout swichFrame;

    private PoemView poemView;
    private TagView tagView;
    private RecentView recentView;
    private NeighbourView neighbourView;

    private Button mDicButton;
    private Button mTButton;
    private Button mSButton;
    private Button mSpButton;

    private int currentView = TAG;
    private boolean collapsed = true;
    private Button neighborButton, recentButton, tagButton;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, OnePoemActivity.class);
        context.startActivity(i);
    }

    public static void actionStart(Context context, int id) {
        Intent i = new Intent(context, OnePoemActivity.class);
        i.putExtra("poem_id", id);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem_main);

        // intent
        Intent intent = getIntent();
        int intentID = intent.getIntExtra("poem_id", -1);

        // 得到 诗view
        poemView = (PoemView) findViewById(R.id.poem_view);
        tagView = (TagView) findViewById(R.id.tag_view);

        recentView = (RecentView) findViewById(R.id.recent_view);
        recentView.setPoemController(this);

        neighbourView = (NeighbourView) findViewById(R.id.neighbour_view);
        neighbourView.setPoemController(this);

        swichFrame = (FrameLayout) findViewById(R.id.switch_frame);

        slider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slider.addPanelSlideListener(this);

        // 显示tag
        tagButton = (Button) findViewById(R.id.show_tag);
        tagButton.setOnClickListener(this);

        // 显示最近列表
        recentButton = (Button) findViewById(R.id.show_recent);
        recentButton.setOnClickListener(this);

        // 显示邻近
        neighborButton = (Button) findViewById(R.id.show_neighbour);
        neighborButton.setOnClickListener(this);

        // 学习
        mDicButton = (Button) findViewById(R.id.start_study);
        mDicButton.setOnClickListener(this);

        // 繁体、简体、简体+
        mTButton = (Button) findViewById(R.id.button_t);
        mTButton.setOnClickListener(this);
        mSButton = (Button) findViewById(R.id.button_s);
        mSButton.setOnClickListener(this);
        mSpButton = (Button) findViewById(R.id.button_sp);
        mSpButton.setOnClickListener(this);

        // 下一首随机诗
        Button b = (Button) findViewById(R.id.next_random);
        b.setOnClickListener(this);

        // 读取配置
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        int mode = pref.getInt("mode", 2);
        // 模式
        setPoemMode(mode);

        // 读取诗
        boolean saveID;
        if (intentID == -1) {
            // load上回的
            int id = pref.getInt("poem_id", 1);
            toPoemByID(id);
            saveID = false;
        } else {
            toPoemByID(intentID);
            intent.removeExtra("poem_id");
            saveID = true;
        }

        // 各种UI
        boolean showPoem = savedInstanceState == null;
        updateUIForPoem(showPoem, saveID);
    }

    @Override
    public void setPoemID(int id) {
        if (currentPoem == null || currentPoem.getId() != id) {
            toPoemByID(id);
            updateUIForPoem(true, true);
        }
    }

    private void randomPoem() {
        // 随机一首诗
        currentPoem = MyDatabaseHelper.randomPoem();
    }

    public void toPoemByID(int id) {
        currentPoem = MyDatabaseHelper.getPoemById(id);
    }

    private void updateUIForPoem(boolean showPoem, boolean saveID) {
        // 诗
        poemView.setPoem(currentPoem, showPoem);

        // 最近
        recentView.setPoem(poemView.getInfoItem());
        recentView.LoadRecentList();

        // 邻近
        neighbourView.setPoem(currentPoem);
        neighbourView.loadNeighbour();

        // 显示tag
        tagView.setPoemId(currentPoem.getId());

        // 写入
        if (saveID) {
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putInt("poem_id", currentPoem.getId());
            editor.apply();
        }
    }

    private void setPoemModeSave(int mode) {
        setPoemMode(mode);

        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("mode", mode);
        editor.apply();
    }

    private void setPoemMode(int mode) {
        poemView.setMode(mode);

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

    private void setBold(Button b, String t) {
        SpannableString temp;
        temp = new SpannableString(t);
        temp.setSpan(new StyleSpan(Typeface.BOLD), 0, t.length(), 0);
        b.setText(temp);
    }

    private void setBoldButton() {
        setBoldButton(currentView);
    }

    private void setBoldButton(int mode) {
        if (collapsed) {
            mode = NO;
        }

        switch (mode) {
            case NO:
                neighborButton.setText("邻近");
                recentButton.setText("最近");
                tagButton.setText("标签");
                break;

            case NEIGHBOR:
                setBold(neighborButton, "邻近");
                recentButton.setText("最近");
                tagButton.setText("标签");
                break;

            case RECENT:
                neighborButton.setText("邻近");
                setBold(recentButton, "最近");
                tagButton.setText("标签");
                break;

            case TAG:
                neighborButton.setText("邻近");
                recentButton.setText("最近");
                setBold(tagButton, "标签");

                break;
        }
    }

    private void setView(int view) {
        currentView = view;

        switch (view) {
            case NEIGHBOR:
                neighbourView.setVisibility(View.VISIBLE);
                recentView.setVisibility(View.GONE);
                tagView.setVisibility(View.GONE);
                break;

            case RECENT:
                neighbourView.setVisibility(View.GONE);
                recentView.setVisibility(View.VISIBLE);
                tagView.setVisibility(View.GONE);
                break;

            case TAG:
                neighbourView.setVisibility(View.GONE);
                recentView.setVisibility(View.GONE);
                tagView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (slider.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("view", currentView);
        outState.putBoolean("collapsed", collapsed);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        poemView.setPoem(currentPoem, true);

        currentView = savedInstanceState.getInt("view");
        setView(currentView);

        collapsed = savedInstanceState.getBoolean("collapsed");
        setBoldButton();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_random:
                randomPoem();
                updateUIForPoem(true, true);
                break;

            case R.id.show_tag:
                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                setView(TAG);
                setBoldButton();
                break;

            case R.id.show_recent:
                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                setView(RECENT);
                setBoldButton();
                break;

            case R.id.show_neighbour:
                neighbourView.centerPosition();

                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                setView(NEIGHBOR);
                setBoldButton();
                break;

            case R.id.start_study:
                StudyActivity.actionStart(OnePoemActivity.this, currentPoem.getId());
                break;

            case R.id.button_t:
                poemView.setMode(0);
                setPoemModeSave(0);
                break;

            case R.id.button_s:
                poemView.setMode(1);
                setPoemModeSave(1);
                break;

            case R.id.button_sp:
                poemView.setMode(2);
                setPoemModeSave(2);
                break;
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
            return;
        } else if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
            Rect r = new Rect();
            if (swichFrame.getGlobalVisibleRect(r)) {
                swichFrame.getLayoutParams().height = r.height();
                swichFrame.requestLayout();
            }
            collapsed = false;
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            collapsed = true;
        } else {
            swichFrame.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT)
            );
            collapsed = false;
        }
        setBoldButton();
    }
}

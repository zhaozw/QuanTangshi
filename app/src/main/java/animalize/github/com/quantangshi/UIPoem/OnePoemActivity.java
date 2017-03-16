package animalize.github.com.quantangshi.UIPoem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
        implements PoemController {

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
        setContentView(R.layout.poem_main);

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
        slider.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
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
                } else {
                    swichFrame.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT)
                    );
                }
            }
        });

        // 显示tag
        Button b = (Button) findViewById(R.id.show_tag);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                recentView.setVisibility(View.GONE);
                neighbourView.setVisibility(View.GONE);
                tagView.setVisibility(View.VISIBLE);
            }
        });

        // 显示最近列表
        b = (Button) findViewById(R.id.show_drawer);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                tagView.setVisibility(View.GONE);
                neighbourView.setVisibility(View.GONE);
                recentView.setVisibility(View.VISIBLE);
            }
        });

        // 显示邻近
        b = (Button) findViewById(R.id.show_neighbour);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neighbourView.centerPosition();

                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                tagView.setVisibility(View.GONE);
                recentView.setVisibility(View.GONE);
                neighbourView.setVisibility(View.VISIBLE);
            }
        });

        // 查询
        mDicButton = (Button) findViewById(R.id.start_dic);
        mDicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyActivity.actionStart(OnePoemActivity.this, currentPoem.getId());
            }
        });

        // 繁体、简体、简体+
        mTButton = (Button) findViewById(R.id.button_t);
        mTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemView.setMode(0);
                setPoemModeSave(0);
            }
        });
        mSButton = (Button) findViewById(R.id.button_s);
        mSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemView.setMode(1);
                setPoemModeSave(1);
            }
        });
        mSpButton = (Button) findViewById(R.id.button_sp);
        mSpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemView.setMode(2);
                setPoemModeSave(2);
            }
        });

        // 下一首随机诗
        b = (Button) findViewById(R.id.next_random);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomPoem();
            }
        });

        // 读取配置
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        int id = pref.getInt("poem_id", -1);
        int mode = pref.getInt("mode", 2);

        // 模式
        setPoemMode(mode);

        if (intentID != -1) {
            toPoemByID(intentID);
        } else {
            // load上回的
            if (id != -1) {
                toPoemByID(id);
            } else {
                randomPoem();
            }
        }
    }

    @Override
    public void setPoemID(int id) {
        currentPoem = MyDatabaseHelper.getPoemById(id);
        updateUIForPoem();
    }

    private void randomPoem() {
        // 随机一首诗
        currentPoem = MyDatabaseHelper.randomPoem();
        updateUIForPoem();
    }

    public void toPoemByID(int id) {
        currentPoem = MyDatabaseHelper.getPoemById(id);
        updateUIForPoem();
    }

    private void updateUIForPoem() {
        // 显示此诗
        poemView.setPoem(currentPoem);

        // 最近
        recentView.setPoem(poemView.getInfoItem());
        recentView.LoadRecentList();

        // 邻近
        neighbourView.setPoem(currentPoem);
        neighbourView.loadNeighbour();

        // 显示tag
        tagView.setPoemId(currentPoem.getId());

        // 写入
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("poem_id", currentPoem.getId());
        editor.apply();
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

    @Override
    public void onBackPressed() {
        if (slider.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slider.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}

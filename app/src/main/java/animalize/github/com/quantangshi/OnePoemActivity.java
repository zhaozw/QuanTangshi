package animalize.github.com.quantangshi;

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
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import animalize.github.com.quantangshi.Data.Poem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;


public class OnePoemActivity
        extends AppCompatActivity
        implements PoemController {

    private Poem currentPoem;

    private SlidingUpPanelLayout slider;
    // 供ANCHORED时调整height用
    private FrameLayout swichFrame;

    private PoemView poemView;
    private TagView tagView;
    private RecentView recentView;
    private NeighbourView neighbourView;

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

        // 得到 诗view
        poemView = (PoemView) findViewById(R.id.poem_view);
        tagView = (TagView) findViewById(R.id.tag_view);

        recentView = (RecentView) findViewById(R.id.recent_view);
        recentView.setPoemController(this);

        neighbourView = (NeighbourView) findViewById(R.id.neighbour_view);
        neighbourView.setPoemController(this);


        slider = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slider.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
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
        swichFrame = (FrameLayout) findViewById(R.id.switch_frame);


        // 显示tag
        Button b = (Button) findViewById(R.id.show_tag);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                slider.setScrollableView(tagView);

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
                slider.setScrollableView(recentView);

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
                neighbourView.loadNeighbour();

                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                slider.setScrollableView(neighbourView);

                tagView.setVisibility(View.GONE);
                recentView.setVisibility(View.GONE);
                neighbourView.setVisibility(View.VISIBLE);
            }
        });

        // 诗id
        mPIDText = (TextView) findViewById(R.id.textview_poem_id);

        // 繁体、简体、简体+
        mTButton = (Button) findViewById(R.id.button_t);
        mTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemView.setMode(0);
                setPoemMode(0);
            }
        });
        mSButton = (Button) findViewById(R.id.button_s);
        mSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemView.setMode(1);
                setPoemMode(1);
            }
        });
        mSpButton = (Button) findViewById(R.id.button_sp);
        mSpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poemView.setMode(2);
                setPoemMode(2);
            }
        });

        // 下一首随机诗
        b = (Button) findViewById(R.id.next_random);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomPoem();
                recentView.scrollToTop();
            }
        });

        setPoemMode(2);
        // load上回的
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        int id = pref.getInt("poem_id", -1);
        if (id != -1) {
            toPoemByID(id);
        } else {
            randomPoem();
        }

        // 配置SlidingUpPanelLayout
        //SlidingUpPanelLayout slide = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
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
        // 写入
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("poem_id", currentPoem.getId());
        editor.apply();

        // 显示此诗
        poemView.setPoem(currentPoem);

        // 更新本活动的ui
        mPIDText.setText(String.valueOf(currentPoem.getId()));

        // 显示tag
        tagView.setPoemId(currentPoem.getId());

        // 添加已有诗
        recentView.setPoem(currentPoem);
        // 刷新最近列表
        recentView.LoadRecentList();

        // 清空邻近
        neighbourView.setPoemID(currentPoem.getId());
        neighbourView.loadNeighbour();
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

}

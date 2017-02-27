package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
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

    // 切换功能
    private LinearLayout neighbourPalace;

    // 邻近
    private RecyclerView neighbourList;
    private RVAdapter neighbourAdapter;

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

        // 供切换的功能界面
        neighbourPalace = (LinearLayout) findViewById(R.id.neighbour_palace);


        // 邻近的RecyclerView
        neighbourList = (RecyclerView) findViewById(R.id.neighbour_list);
        // 布局管理
        LinearLayoutManager lm = new LinearLayoutManager(this);
        neighbourList.setLayoutManager(lm);
        // adapter
        neighbourAdapter = new RVAdapter();
        neighbourList.setAdapter(neighbourAdapter);

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
                neighbourPalace.setVisibility(View.GONE);
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
                neighbourPalace.setVisibility(View.GONE);
                recentView.setVisibility(View.VISIBLE);
            }
        });

        // 显示邻近
        b = (Button) findViewById(R.id.show_neighbour);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadNeighbourList().execute(currentPoem.getId(), 80);

                if (slider.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    slider.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                slider.setScrollableView(neighbourPalace);

                tagView.setVisibility(View.GONE);
                recentView.setVisibility(View.GONE);
                neighbourPalace.setVisibility(View.VISIBLE);
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
        neighbourAdapter.clear();
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

    class LoadNeighbourList extends AsyncTask<Integer, Integer, ArrayList<InfoItem>> {

        @Override
        protected ArrayList<InfoItem> doInBackground(Integer... params) {
            int id = params[0];
            int window = params[1];
            return MyDatabaseHelper.getNeighbourList(id, window);
        }

        @Override
        protected void onPostExecute(ArrayList<InfoItem> infoItems) {
            neighbourAdapter.setArrayList(infoItems);
            neighbourList.scrollToPosition(
                    neighbourAdapter.centerPosition(currentPoem.getId()));
        }
    }

    public class RVAdapter
            extends RecyclerView.Adapter<RVAdapter.MyHolder> {

        private static final String TAG = "RVAdapter";
        private List<InfoItem> mRecentList;

        public void setArrayList(ArrayList<InfoItem> al) {
            mRecentList = al;
            notifyDataSetChanged();
        }

        public void clear() {
            mRecentList = null;
            notifyDataSetChanged();
        }

        public int centerPosition(int id) {
            return id - mRecentList.get(0).getId();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.recent_list_item, parent, false);
            final MyHolder holder = new MyHolder(v);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posi = holder.getAdapterPosition();
                    InfoItem ri = mRecentList.get(posi);

                    OnePoemActivity.this.toPoemByID(ri.getId());
                    OnePoemActivity.this.recentView.scrollToTop();
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            InfoItem ri = mRecentList.get(position);

            if (position % 2 == 0) {
                holder.root.setBackgroundColor(Color.rgb(0xff, 0xcc, 0xcc));
            } else {
                holder.root.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xff));
            }

            holder.order.setText(String.valueOf(position + 1));
            holder.title.setText(ri.getTitle());
            holder.author.setText(ri.getAuthor());
            holder.id.setText("" + ri.getId());
        }

        @Override
        public int getItemCount() {
            if (mRecentList == null) {
                return 0;
            }
            return mRecentList.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            private LinearLayout root;
            private TextView order;
            private TextView title;
            private TextView author;
            private TextView id;

            public MyHolder(View itemView) {
                super(itemView);

                root = (LinearLayout) itemView.findViewById(R.id.recent_item);
                order = (TextView) itemView.findViewById(R.id.recent_item_order);
                title = (TextView) itemView.findViewById(R.id.recent_item_title);
                author = (TextView) itemView.findViewById(R.id.recent_item_author);
                id = (TextView) itemView.findViewById(R.id.recent_item_id);
            }
        }
    }
}

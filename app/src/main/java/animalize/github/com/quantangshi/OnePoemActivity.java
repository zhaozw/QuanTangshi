package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.Poem;
import animalize.github.com.quantangshi.Data.RecentInfo;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;


public class OnePoemActivity extends AppCompatActivity {
    private Poem currentPoem;

    private OnePoemFragment poemFragment;
    private TagFragment tagFragment;
    private DrawerLayout drawerLayout;
    private RecyclerView recentList;
    private RecentAdapter recentAdapter;

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

        // 最近列表
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // RecyclerView
        recentList = (RecyclerView) findViewById(R.id.recent_list);
        // 布局管理
        LinearLayoutManager lm = new LinearLayoutManager(this);
        recentList.setLayoutManager(lm);
        // adapter
        recentAdapter = new RecentAdapter();
        recentList.setAdapter(recentAdapter);

        // 显示最近列表
        Button b = (Button) findViewById(R.id.show_drawer);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 诗id
        mPIDText = (TextView) findViewById(R.id.textview_poem_id);

        // 繁体、简体、简体+
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
        b = (Button) findViewById(R.id.next_random);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomPoem();
            }
        });

        randomPoem();

        // 最近列表
        ArrayList<RecentInfo> recent_list = MyDatabaseHelper.getRecentList();
        recentAdapter.setArrayList(recent_list);

        // 配置SlidingUpPanelLayout
        //SlidingUpPanelLayout slide = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }

    private void randomPoem() {
        // 随机一首诗
        currentPoem = poemFragment.randomPoem();

        // 添加
        MyDatabaseHelper.addToRecentList(currentPoem, 30);

        updateUI(poemFragment.getMode());
        mPIDText.setText(String.valueOf(currentPoem.getId()));
        tagFragment.setPoemId(currentPoem.getId());

        // 最近列表
        ArrayList<RecentInfo> recent_list = MyDatabaseHelper.getRecentList();
        recentAdapter.setArrayList(recent_list);
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

    public static class RecentAdapter
            extends RecyclerView.Adapter<RecentAdapter.MyHolder> {

        private static final String TAG = "RecentAdapter";
        private List<RecentInfo> mRecentList;

        public void setArrayList(ArrayList<RecentInfo> al) {
            mRecentList = al;
            notifyDataSetChanged();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.recent_list_item, parent, false);
            MyHolder holder = new MyHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            RecentInfo ri = mRecentList.get(position);

            if (position % 2 == 0) {
                holder.root.setBackgroundColor(Color.rgb(0xff, 0xcc, 0xcc));
            } else {
                holder.root.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xff));
            }

            holder.order.setText(String.valueOf(position + 1));
            holder.title.setText(ri.getTitle());
            holder.author.setText(ri.getAuthor());
        }

        @Override
        public int getItemCount() {
            return mRecentList.size();
        }

        public static class MyHolder extends RecyclerView.ViewHolder {
            private LinearLayout root;
            private TextView order;
            private TextView title;
            private TextView author;

            public MyHolder(View itemView) {
                super(itemView);

                root = (LinearLayout) itemView.findViewById(R.id.recent_item);
                order = (TextView) itemView.findViewById(R.id.recent_item_order);
                title = (TextView) itemView.findViewById(R.id.recent_item_title);
                author = (TextView) itemView.findViewById(R.id.recent_item_author);
            }
        }
    }
}

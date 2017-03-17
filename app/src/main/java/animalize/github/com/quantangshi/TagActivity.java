package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.Database.TagAgent;
import animalize.github.com.quantangshi.UIPoem.OnePoemActivity;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TagActivity extends AppCompatActivity {

    private List<TagInfo> mTagList = new ArrayList<>();
    private List<TagInfo> mAllTagList;
    private TagContainerLayout searchTags;
    private TagContainerLayout allTags;

    private LinearLayout layoutAll;
    private LinearLayout layoutResult;

    private RVAdapter resultAdapter;
    private RecyclerView rvResult;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, TagActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        searchTags = (TagContainerLayout) findViewById(R.id.search_tags);
        searchTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                searchTags.removeTag(position);
                mTagList.remove(position);
            }
        });

        // 所有tags 数组
        mAllTagList = TagAgent.getTagInfos();

        // 所有tags
        allTags = (TagContainerLayout) findViewById(R.id.all_tags);
        allTags.setIsTagViewClickable(true);
        allTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagInfo info = mAllTagList.get(position);
                TagActivity.this.clickOneAllTag(info);
            }

            @Override
            public void onTagLongClick(int position, String text) {
            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
        allTags.setTags(TagAgent.getTagsHasCount(mAllTagList));

        // 开始搜索
        Button bt = (Button) findViewById(R.id.search_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = searchTags.getTags();
                ArrayList<InfoItem> l = MyDatabaseHelper.queryByTags(list);

                resultAdapter.setArrayList(l);

                layoutAll.setVisibility(View.INVISIBLE);
                layoutResult.setVisibility(View.VISIBLE);

                List<String> tags = searchTags.getTags();
                searchTags.setEnableCross(false);
                searchTags.setTags(tags);
            }
        });

        // 退回
        bt = (Button) findViewById(R.id.back_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutResult.setVisibility(View.INVISIBLE);
                layoutAll.setVisibility(View.VISIBLE);

                List<String> tags = searchTags.getTags();
                searchTags.setEnableCross(true);
                searchTags.setTags(tags);
            }
        });

        layoutAll = (LinearLayout) findViewById(R.id.layout_search);
        layoutResult = (LinearLayout) findViewById(R.id.layout_result);

        rvResult = (RecyclerView) findViewById(R.id.rv_result);

        // 布局管理
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rvResult.setLayoutManager(lm);
        // adapter
        resultAdapter = new RVAdapter();
        rvResult.setAdapter(resultAdapter);
    }

    public void clickOneAllTag(TagInfo info) {
        if (mTagList.contains(info)) {
            return;
        }

        mTagList.add(info);
        searchTags.addTag(info.getName());
    }

    public class RVAdapter
            extends RecyclerView.Adapter<RVAdapter.MyHolder> {

        private List<InfoItem> mList;

        public void setArrayList(List<InfoItem> al) {
            mList = al;
            notifyDataSetChanged();
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
                    InfoItem ri = mList.get(posi);

                    OnePoemActivity.actionStart(TagActivity.this, ri.getId());
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(RVAdapter.MyHolder holder, int position) {
            InfoItem ri = mList.get(position);

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
            if (mList == null) {
                return 0;
            }
            return mList.size();
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

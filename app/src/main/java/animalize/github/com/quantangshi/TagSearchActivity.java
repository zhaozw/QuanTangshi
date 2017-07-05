package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.Database.TagAgent;
import animalize.github.com.quantangshi.ListViewPack.RVAdapter;
import animalize.github.com.quantangshi.UIPoem.OnePoemActivity;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TagSearchActivity extends AppCompatActivity {

    private boolean inResult = false;

    private List<TagInfo> mAllTagList;
    private TagContainerLayout searchTags;
    private TagContainerLayout allTags;

    private LinearLayout layoutAll;
    private LinearLayout layoutResult;

    private RVAdapter resultAdapter;
    private RecyclerView rvResult;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, TagSearchActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_search);

        // toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.tag_search_toolbar);
        tb.setTitle("标签搜索");
        setSupportActionBar(tb);

        // 要在setSupportActionBar之后
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            }
        });

        // 由系统恢复activity
        if (savedInstanceState != null) {
            ArrayList<String> tags = savedInstanceState.getStringArrayList("search_tags");
            searchTags.setTags(tags);
        }

        // 所有tags 数组
        mAllTagList = TagAgent.getAllTagInfos();
        if (mAllTagList.isEmpty()) {
            Toast.makeText(this, "尚未添加标签，请在添加后使用本功能。", Toast.LENGTH_LONG).show();
        }

        // 所有tags
        allTags = (TagContainerLayout) findViewById(R.id.all_tags);
        allTags.setIsTagViewClickable(true);
        allTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagInfo info = mAllTagList.get(position);
                TagSearchActivity.this.clickOneAllTag(info);
            }

            @Override
            public void onTagLongClick(int position, String text) {
            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
        allTags.setTags(TagAgent.getAllTagsHasCount());

        // 开始搜索
        Button bt = (Button) findViewById(R.id.search_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = searchTags.getTags();
                if (list.isEmpty()) {
                    Toast.makeText(TagSearchActivity.this,
                            "至少选择一个标签才能搜索",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<InfoItem> l = MyDatabaseHelper.queryByTags(list);

                resultAdapter.setArrayList(l);

                layoutAll.setVisibility(View.INVISIBLE);
                layoutResult.setVisibility(View.VISIBLE);

                List<String> tags = searchTags.getTags();
                searchTags.setEnableCross(false);
                searchTags.setTags(tags);

                inResult = true;
            }
        });

        // 退回
        bt = (Button) findViewById(R.id.back_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultToSearch();
            }
        });

        layoutAll = (LinearLayout) findViewById(R.id.layout_search);
        layoutResult = (LinearLayout) findViewById(R.id.layout_result);

        rvResult = (RecyclerView) findViewById(R.id.rv_result);

        // 布局管理
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rvResult.setLayoutManager(lm);

        // adapter
        resultAdapter = new RVAdapter() {
            @Override
            public void onItemClick(int pid) {
                OnePoemActivity.actionStart(TagSearchActivity.this, pid);
            }
        };
        rvResult.setAdapter(resultAdapter);
    }

    public void clickOneAllTag(TagInfo info) {
        if (searchTags.getTags().contains(info.getName())) {
            return;
        }

        searchTags.addTag(info.getName());
    }

    private void resultToSearch() {
        // 所有标签
        mAllTagList = TagAgent.getAllTagInfos();
        allTags.setTags(TagAgent.getAllTagsHasCount());

        // 可能被删除的搜索标签
        List<String> tmp = searchTags.getTags();
        if (!tmp.isEmpty()) {
            for (int posi = tmp.size() - 1; posi >= 0; posi--) {
                boolean pass = true;
                final String s = tmp.get(posi);

                for (TagInfo info : mAllTagList) {
                    if (s.equals(info.getName())) {
                        pass = false;
                        break;
                    }
                }
                if (pass) {
                    searchTags.removeTag(posi);
                }
            }
        }

        // 可见、不可见
        layoutResult.setVisibility(View.INVISIBLE);
        layoutAll.setVisibility(View.VISIBLE);

        // tag上的叉
        List<String> tags = searchTags.getTags();
        searchTags.setEnableCross(true);
        searchTags.setTags(tags);

        inResult = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        List<String> tags = searchTags.getTags();
        outState.putStringArrayList("search_tags", (ArrayList<String>) tags);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (inResult) {
            resultToSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

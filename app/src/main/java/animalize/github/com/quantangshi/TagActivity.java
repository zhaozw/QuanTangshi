package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.Database.TagAgent;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TagActivity extends AppCompatActivity {

    private List<TagInfo> mTagList = new ArrayList<>();
    private List<TagInfo> mAllTagList;
    private TagContainerLayout searchTags;
    private TagContainerLayout allTags;

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
                Toast.makeText(TagActivity.this, text, Toast.LENGTH_SHORT).show();
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

                StringBuilder sb = new StringBuilder();
                for (InfoItem info : l) {
                    sb.append("" + info.getId() + info.getTitle());
                }
                Toast.makeText(TagActivity.this,
                        sb,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickOneAllTag(TagInfo info) {
        if (mTagList.contains(info)) {
            return;
        }

        mTagList.add(info);
        searchTags.addTag(info.getName());
    }

}

package animalize.github.com.quantangshi.UIPoem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.TagAgent;
import animalize.github.com.quantangshi.R;
import co.lujun.androidtagview.TagContainerLayout;


public class TagView extends LinearLayout {
    private int mPid;

    private List<TagInfo> mTagList;
    private List<TagInfo> mAllTagList;
    private TagContainerLayout mPoemTags;
    private TagContainerLayout mAllTags;

    private EditText mEdit;

    private Button mAddTag;

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_tag, this);

        mEdit = (EditText) findViewById(R.id.tag_edit);

        mAddTag = (Button) findViewById(R.id.tag_add);
        mAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = mEdit.getText().toString().trim();
                if (tag == "") {
                    return;
                }

                TagAgent.addTagToPoem(tag, mPid);
                mEdit.setText("");

                setPoemId(mPid);
            }
        });

        // tag
        mPoemTags = (TagContainerLayout) findViewById(R.id.poem_tags);
        mPoemTags.setOnTagClickListener(new co.lujun.androidtagview.TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                final TagInfo info = mTagList.get(position);

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getContext());
                builder.setTitle("确认删除: " + info.getName() + "?");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TagView.this.removeTag(info);
                    }
                });
                builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        mAllTags = (TagContainerLayout) findViewById(R.id.all_tags);
        mAllTags.setIsTagViewClickable(true);
        mAllTags.setOnTagClickListener(new co.lujun.androidtagview.TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

                String tag = mAllTagList.get(position).getName();
                List<Integer> l = TagAgent.getPoemIDByTag(tag);

                for (int id : l) {
                    Toast.makeText(getContext(),
                            String.valueOf(id),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    public void removeTag(TagInfo info) {
        TagAgent.delTagFromPoem(mPid, info);

        Toast.makeText(getContext(),
                "删除: " + info.getName(),
                Toast.LENGTH_SHORT).show();

        setPoemId(mPid);
    }

    public void setPoemId(int pid) {
        mPid = pid;

        mTagList = TagAgent.getTagsInfo(pid);

        List<String> tags = TagAgent.getTagsNoCount(mTagList);
        mPoemTags.setTags(tags);

        setAllTags();
    }

    public void setAllTags() {
        mAllTagList = TagAgent.getTagInfos();
        mAllTags.setTags(TagAgent.getTagsHasCount(TagAgent.getTagInfos()));
    }
}

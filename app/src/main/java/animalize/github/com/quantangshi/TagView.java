package animalize.github.com.quantangshi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import co.lujun.androidtagview.TagContainerLayout;

/**
 * Created by anima on 17-2-27.
 */

public class TagView extends LinearLayout {
    private int mPid;

    private List<TagInfo> mTagList;
    private TagContainerLayout mPoemTags;
    private TagContainerLayout mAllTags;

    private boolean mRemoving = false;
    private EditText mEdit;

    private Button mAddTag;
    private Button mDelTag;

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

                MyDatabaseHelper.addTagToPoem(tag, mPid);
                mEdit.setText("");

                setPoemId(mPid);
            }
        });

        mDelTag = (Button) findViewById(R.id.tag_del);
        mDelTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDelState();

                List<String> t = mPoemTags.getTags();
                mPoemTags.setTags(t);
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
                TagInfo info = mTagList.get(position);
                TagView.this.removeTag(info);
            }
        });

        mAllTags = (TagContainerLayout) findViewById(R.id.all_tags);

    }


    public void setDelState() {
        if (mRemoving) {
            mPoemTags.setEnableCross(false);
            mRemoving = false;
            mDelTag.setText("删除");
        } else {
            mPoemTags.setEnableCross(true);
            mRemoving = true;
            mDelTag.setText("返回");
        }
    }

    public void removeTag(TagInfo info) {
        MyDatabaseHelper.delTagFromPoem(mPid, info);

        Toast.makeText(getContext(),
                "删除: " + info.getName(),
                Toast.LENGTH_SHORT).show();

        mRemoving = false;
        setDelState();
        setPoemId(mPid);
    }

    public void setPoemId(int pid) {
        mPid = pid;

        List<TagInfo> tagsinfo = MyDatabaseHelper.getTagsByPoem(pid);
        List<String> tags = new ArrayList<>();
        for (TagInfo info : tagsinfo) {
            String s;

            if (info.getCount() > 1) {
                s = info.getName() + "(" + info.getCount() + ")";
            } else {
                s = info.getName();
            }
            tags.add(s);
        }

        mTagList = tagsinfo;

        mPoemTags.setEnableCross(mRemoving);
        mPoemTags.setTags(tags);

        setAllTags();
    }

    public void setAllTags() {
        List<TagInfo> tagsinfo = MyDatabaseHelper.getTopTags(20);
        List<String> tags = new ArrayList<>();
        for (TagInfo info : tagsinfo) {
            String s;

            if (info.getCount() > 1) {
                s = info.getName() + "(" + info.getCount() + ")";
            } else {
                s = info.getName();
            }
            tags.add(s);
        }

        mAllTags.setTags(tags);
    }
}

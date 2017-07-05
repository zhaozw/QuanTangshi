package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.TagAgent;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TagManageActivity extends AppCompatActivity implements View.OnClickListener {
    private List<TagInfo> mAllTagList;
    private TagContainerLayout selectTag;
    private Button renameButton, delButton;

    private TextView currentTag;
    private EditText newName;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, TagManageActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_manage);

        // toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.tag_manage_toolbar);
        tb.setTitle("标签管理");
        setSupportActionBar(tb);

        // 要在setSupportActionBar之后
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentTag = (TextView) findViewById(R.id.current_tag);

        selectTag = (TagContainerLayout) findViewById(R.id.select_tag);
        selectTag.setIsTagViewClickable(true);
        selectTag.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagInfo info = mAllTagList.get(position);
                TagManageActivity.this.currentTag.setText(info.getName());

                renameButton.setEnabled(true);
                delButton.setEnabled(true);
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

        renameButton = (Button) findViewById(R.id.rename_tag);
        renameButton.setOnClickListener(this);

        delButton = (Button) findViewById(R.id.del_tag);
        delButton.setOnClickListener(this);

        newName = (EditText) findViewById(R.id.new_name);

        // 刷新标签
        refreshTags();

        if (mAllTagList.isEmpty()) {
            Toast.makeText(this, "尚未添加标签，请在添加后使用本功能。", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshTags() {
        renameButton.setEnabled(false);
        delButton.setEnabled(false);

        currentTag.setText("");
        newName.setText("");

        // 所有tags 数组
        mAllTagList = TagAgent.getAllTagInfos();
        selectTag.setTags(TagAgent.getAllTagsHasCount());
    }

    @Override
    public void onClick(View v) {
        String tag = currentTag.getText().toString();
        String newname = newName.getText().toString().trim();

        switch (v.getId()) {
            case R.id.rename_tag:
                if ("".equals(newname)) {
                    return;
                }

                AlertDialog.Builder d = new AlertDialog.Builder(this);
                d.setTitle("确认改名或合并操作");
                d.setMessage("是否将 " + tag + " 改名或合并到 " + newname + " ？");
                d.setCancelable(false);
                d.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = currentTag.getText().toString();
                        String newname = newName.getText().toString().trim();

                        TagAgent.renameTag(tag, newname);
                        refreshTags();

                        Toast.makeText(TagManageActivity.this,
                                "已执行改名、合并操作",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                d.setNegativeButton("取消", null);
                d.show();

                break;

            case R.id.del_tag:
                d = new AlertDialog.Builder(this);
                d.setTitle("确认删除标签");
                d.setMessage("是否将 " + tag + " 标签删除？");
                d.setCancelable(false);
                d.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = currentTag.getText().toString();

                        TagAgent.delTag(tag);
                        refreshTags();

                        Toast.makeText(TagManageActivity.this,
                                "已执行删除操作",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                d.setNegativeButton("取消", null);
                d.show();

                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

package animalize.github.com.quantangshi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import co.lujun.androidtagview.TagContainerLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class TagFragment extends Fragment {

    private static final String TAG = "TagFragment";
    private int mPid;
    private TagContainerLayout mTagContainer;
    private EditText mEdit;
    private Button mAddTag;
    private Button mDelTag;


    public TagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tag, container, false);

        mEdit = (EditText) v.findViewById(R.id.tag_edit);

        mAddTag = (Button) v.findViewById(R.id.tag_add);
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

        mDelTag = (Button) v.findViewById(R.id.tag_del);
        mDelTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> t = mTagContainer.getTags();
                mTagContainer.setEnableCross(true);
                mTagContainer.setTags(t);
            }
        });

        mTagContainer = (TagContainerLayout) v.findViewById(R.id.tagcontainerLayout);

        return v;
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

        mTagContainer.setTags(tags);
    }

}

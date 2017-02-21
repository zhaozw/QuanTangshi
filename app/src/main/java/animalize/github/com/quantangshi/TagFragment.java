package animalize.github.com.quantangshi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class TagFragment extends Fragment {

    private TagContainerLayout mTagContainer;
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

        mAddTag = (Button) v.findViewById(R.id.tag_add);

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
        mTagContainer.setTags("香山", "马驹桥", "燕郊");

        return v;
    }

}

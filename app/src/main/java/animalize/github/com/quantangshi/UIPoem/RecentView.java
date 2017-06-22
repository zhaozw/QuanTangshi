package animalize.github.com.quantangshi.UIPoem;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Database.RecentAgent;
import animalize.github.com.quantangshi.ListViewPack.RVAdapter;
import animalize.github.com.quantangshi.R;

/**
 * Created by anima on 17-2-27.
 */

public class RecentView extends LinearLayout {
    final static int recentLimit = 60;

    private PoemController mController;

    private RecyclerView mRecentList;
    private RVAdapter mRecentAdapter;

    public RecentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_recent, this);

        // 最近的RecyclerView
        mRecentList = (RecyclerView) findViewById(R.id.recent_list);

        // 布局管理
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mRecentList.setLayoutManager(lm);
        // adapter
        mRecentAdapter = new RVAdapter() {
            @Override
            public void onItemClick(int pid) {
                mController.setPoemID(pid);
            }
        };
        mRecentList.setAdapter(mRecentAdapter);

        TextView tv = (TextView) findViewById(R.id.recent_title);
        tv.setText("最近" + recentLimit + "首");
    }

    public void setPoemController(PoemController controller) {
        mController = controller;
    }

    public void setPoem(InfoItem info) {
        RecentAgent.addToRecent(info, recentLimit);
    }

    public void LoadRecentList() {
        List<InfoItem> infoItems = RecentAgent.getRecentList();
        mRecentAdapter.setArrayList(infoItems);
        mRecentList.scrollToPosition(0);
    }
}

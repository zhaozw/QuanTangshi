package animalize.github.com.quantangshi.UIPoem;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Database.RecentAgent;
import animalize.github.com.quantangshi.R;

/**
 * Created by anima on 17-2-27.
 */

public class RecentView extends LinearLayout {
    final static int recentLimit = 60;

    private RawPoem currentPoem;

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
        mRecentAdapter = new RVAdapter();
        mRecentList.setAdapter(mRecentAdapter);

        TextView tv = (TextView) findViewById(R.id.recent_title);
        tv.setText("最近" + recentLimit + "条");
    }

    public void setPoemController(PoemController controller) {
        mController = controller;
    }

    public void setPoem(RawPoem poem) {
        RecentAgent.addToRecent(poem, recentLimit);
    }

    public void LoadRecentList() {
        List<InfoItem> infoItems = RecentAgent.getRecentList();
        mRecentAdapter.setArrayList(infoItems);
        mRecentList.scrollToPosition(0);
    }


    public class RVAdapter
            extends RecyclerView.Adapter<RVAdapter.MyHolder> {

        private static final String TAG = "RVAdapter";
        private List<InfoItem> mRecentList;

        public void setArrayList(List<InfoItem> al) {
            mRecentList = al;
            notifyDataSetChanged();
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.recent_list_item, parent, false);
            final RVAdapter.MyHolder holder = new RVAdapter.MyHolder(v);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posi = holder.getAdapterPosition();
                    InfoItem ri = mRecentList.get(posi);

                    mController.setPoemID(ri.getId());
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(RVAdapter.MyHolder holder, int position) {
            InfoItem ri = mRecentList.get(position);

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
            if (mRecentList == null) {
                return 0;
            }
            return mRecentList.size();
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

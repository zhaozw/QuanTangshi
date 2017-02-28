package animalize.github.com.quantangshi.UIPoem;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.R;

/**
 * Created by anima on 17-2-27.
 */

public class NeighbourView extends LinearLayout {
    private PoemController mController;
    private int mId;

    private RecyclerView neighbourList;
    private RVAdapter neighbourAdapter;


    public NeighbourView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_neighbour, this);


        // 邻近的RecyclerView
        neighbourList = (RecyclerView) findViewById(R.id.neighbour_list);
        // 布局管理
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        neighbourList.setLayoutManager(lm);
        // adapter
        neighbourAdapter = new RVAdapter();
        neighbourList.setAdapter(neighbourAdapter);
    }

    public void setPoemController(PoemController controller) {
        mController = controller;
    }

    public int getPoemID() {
        return mId;
    }

    public void setPoemID(int id) {
        mId = id;
    }

    public void scrollToTop() {
        neighbourList.scrollToPosition(0);
    }

    public void loadNeighbour() {
        new LoadNeighbourList().execute(mId, 80);
    }

    public void clear() {
        neighbourAdapter.clear();
    }


    class LoadNeighbourList extends AsyncTask<Integer, Integer, ArrayList<InfoItem>> {

        @Override
        protected ArrayList<InfoItem> doInBackground(Integer... params) {
            int id = params[0];
            int window = params[1];
            return MyDatabaseHelper.getNeighbourList(id, window);
        }

        @Override
        protected void onPostExecute(ArrayList<InfoItem> infoItems) {
            neighbourAdapter.setArrayList(infoItems);
            neighbourAdapter.centerPosition(mId);
        }
    }

    public class RVAdapter
            extends RecyclerView.Adapter<RVAdapter.MyHolder> {

        private static final String TAG = "RVAdapter";
        private List<InfoItem> mRecentList;

        public void setArrayList(ArrayList<InfoItem> al) {
            mRecentList = al;
            notifyDataSetChanged();
        }

        public void clear() {
            mRecentList = null;
            notifyDataSetChanged();
        }

        public void centerPosition(int id) {
            NeighbourView.this.neighbourList.scrollToPosition(
                    id - mRecentList.get(0).getId()
            );
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
                    InfoItem ri = mRecentList.get(posi);

                    NeighbourView.this.mController.setPoemID(ri.getId());
                    NeighbourView.this.scrollToTop();
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            InfoItem ri = mRecentList.get(position);

            if (position % 2 == 0) {
                holder.root.setBackgroundColor(Color.rgb(0xff, 0xcc, 0xcc));
            } else {
                holder.root.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xff));
            }

            holder.order.setText("" +
                    (ri.getId() - NeighbourView.this.getPoemID())
            );
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

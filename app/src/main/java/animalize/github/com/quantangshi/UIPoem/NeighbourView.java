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
import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.R;

/**
 * Created by anima on 17-2-27.
 */

public class NeighbourView extends LinearLayout {
    private PoemController mController;
    private RawPoem poem;

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
        return poem.getId();
    }

    public void setPoem(RawPoem poem) {
        this.poem = poem;
    }

    public void centerPosition() {
        NeighbourView.this.neighbourList.scrollToPosition(
                poem.getId() - neighbourAdapter.getFirstId()
        );
    }

    public void loadNeighbour() {
        new LoadNeighbourList().execute(poem.getId(), 80);
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
            centerPosition();
        }
    }

    public class RVAdapter
            extends RecyclerView.Adapter<RVAdapter.MyHolder> {

        private List<InfoItem> mRecentList;

        public void setArrayList(ArrayList<InfoItem> al) {
            mRecentList = al;
            notifyDataSetChanged();
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
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            InfoItem ri = mRecentList.get(position);

            if (position == NeighbourView.this.poem.getId() -
                    mRecentList.get(0).getId()) {
                holder.root.setBackgroundColor(Color.rgb(0x99, 0xcc, 0x99));
            } else if (position % 2 == 0) {
                holder.root.setBackgroundColor(Color.rgb(0xff, 0xcc, 0xcc));
            } else {
                holder.root.setBackgroundColor(Color.rgb(0xcc, 0xcc, 0xff));
            }

            int temp = ri.getId() - NeighbourView.this.getPoemID();
            String order;
            if (temp > 0) {
                order = "+" + temp;
            } else {
                order = "" + temp;
            }
            holder.order.setText(order);
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

        public int getFirstId() {
            if (mRecentList == null) {
                return -1;
            }
            return mRecentList.get(0).getId();
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

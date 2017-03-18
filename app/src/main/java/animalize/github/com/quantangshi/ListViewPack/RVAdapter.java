package animalize.github.com.quantangshi.ListViewPack;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.R;


public abstract class RVAdapter
        extends RecyclerView.Adapter<RVAdapter.MyHolder> {

    private List<InfoItem> mList;

    public abstract void onItemClick(int pid);

    public void setArrayList(List<InfoItem> al) {
        mList = al;
        notifyDataSetChanged();
    }

    @Override
    public RVAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recent_list_item, parent, false);
        final RVAdapter.MyHolder holder = new RVAdapter.MyHolder(v);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posi = holder.getAdapterPosition();
                InfoItem ri = mList.get(posi);

                onItemClick(ri.getId());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(RVAdapter.MyHolder holder, int position) {
        InfoItem ri = mList.get(position);

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
        if (mList == null) {
            return 0;
        }
        return mList.size();
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

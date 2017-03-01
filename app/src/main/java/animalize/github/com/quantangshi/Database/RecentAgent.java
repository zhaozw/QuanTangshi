package animalize.github.com.quantangshi.Database;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Data.Poem;


public class RecentAgent {
    private static MyLinkedHashMap recentList;
    private static int limit = 0xffff;

    public static synchronized void addToRecent(final Poem poem, final int limit) {
        if (recentList == null) {
            loadRecentList();
        }
        // 设置最大容量
        RecentAgent.limit = limit;

        // 删已有的
        recentList.remove(poem.getId());

        // 添新的，会自动删过量的
        recentList.put(poem.getId(),
                new InfoItem(
                        poem.getId(),
                        poem.getTitle(),
                        poem.getAuthor()
                )
        );

        // 进数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyDatabaseHelper.addToRecentList(poem, limit);
            }
        }).start();
    }

    public static synchronized List<InfoItem> getRecentList() {
        if (recentList == null) {
            loadRecentList();
        }

        List<InfoItem> t = new ArrayList<>(recentList.values());

        // 反转
        Collections.reverse(t);
        return t;
    }

    private static void loadRecentList() {
        recentList = new MyLinkedHashMap();

        ArrayList<InfoItem> tempList = MyDatabaseHelper.getRecentList();
        // 反转
        Collections.reverse(tempList);

        // 添加到本地
        for (InfoItem info : tempList) {
            recentList.put(info.getId(), info);
        }
    }

    private static class MyLinkedHashMap extends LinkedHashMap {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > RecentAgent.limit;
        }
    }
}

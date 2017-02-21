package animalize.github.com.quantangshi.Database;


import java.util.List;

import animalize.github.com.quantangshi.Data.TagInfo;

public class TagManager {
    private TagManager singleton;

    private TagManager() {
        singleton = new TagManager();
    }

    public static List<TagInfo> getTagsByPoem(int pid) {
        return MyDatabaseHelper.getTagsByPoem(pid);
    }

    // 给诗添加一个tag
    public static boolean addTagToPoem(String tag, int pid) {
        int tid = MyDatabaseHelper.getTagID(tag);

        if (tid != -1) {
            // 已在tag表
            if (MyDatabaseHelper.poemHasTagID(pid, tid)) {
                // 诗已存在此tag
                return false;
            } else {
                // 添加到tag_map
                MyDatabaseHelper.addTagMap(pid, tid);
                // count + 1
                int count = MyDatabaseHelper.getTagCount(tid);
                MyDatabaseHelper.updateTagCount(tid, count + 1);
            }
        } else {
            // 没在tag表
            tid = MyDatabaseHelper.addTag(tag);
            MyDatabaseHelper.addTagMap(pid, tid);
        }

        return true;
    }
}

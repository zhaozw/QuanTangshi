package animalize.github.com.quantangshi.Database;

import java.util.ArrayList;
import java.util.List;

import animalize.github.com.quantangshi.Data.TagInfo;

/**
 * Created by anima on 17-3-17.
 */

public class TagAgent {

    public static synchronized List<TagInfo> getTagInfos() {
        return MyDatabaseHelper.getTags();
    }

    public static List<String> getTagsHasCount(List<TagInfo> list) {
        List<String> tags = new ArrayList<>();
        for (TagInfo info : list) {
            String s = info.getName();
            if (info.getCount() != 1) {
                s += "(" + info.getCount() + ")";
            }
            tags.add(s);
        }
        return tags;
    }

    public static List<String> getTagsNoCount(List<TagInfo> list) {
        List<String> tags = new ArrayList<>();
        for (TagInfo info : list) {
            String s = info.getName();
            tags.add(s);
        }
        return tags;
    }

    public static synchronized List<TagInfo> getTagsInfo(int pid) {
        List<TagInfo> tagsinfo = MyDatabaseHelper.getTagsByPoem(pid);
        return tagsinfo;
    }

    public static synchronized boolean addTagToPoem(String tag, int pid) {
        return MyDatabaseHelper.addTagToPoem(tag, pid);
    }

    public static synchronized boolean delTagFromPoem(int pid, TagInfo info) {
        return MyDatabaseHelper.delTagFromPoem(pid, info);
    }

    public static synchronized boolean renameTag(String o, String n) {
        if (o.equals(n)) {
            return false;
        }

        return MyDatabaseHelper.renameTag(o, n);
    }

    public static synchronized boolean delTag(String tag) {
        return MyDatabaseHelper.delTag(tag);
    }
}

package animalize.github.com.quantangshi.Data;


import java.util.ArrayList;

import animalize.github.com.quantangshi.T2sMap.T2s;

public class Poem {
    public int id;

    private String title;
    private String author;
    private String text;

    private String s_title;
    private String s_author;
    private String s_text;

    private String sp_title;
    private String sp_author;
    private String sp_text;

    private ArrayList<CodepointPosition> posi_text;

    /* 当前模式
     * 0: 繁体
     * 1: 简体
     * 2: 简体+
    */
    private int mode = 0;

    public Poem(int id, String title, String author, String text) {
        this.id = id;

        this.title = title;
        this.author = author;
        this.text = text;
    }

    private String getTextByMode(String s,
                                 int mode,
                                 ArrayList<CodepointPosition> lst) {
        if (mode == 1) {
            return T2s.t2s(s, null);
        } else if (mode == 2) {
            return T2s.t2s(s, lst);
        }
        return s;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;

        if (mode == 1 && s_title == null) {
            s_title = getTextByMode(title, mode, null);
            s_author = getTextByMode(author, mode, null);
            s_text = getTextByMode(text, mode, null);
        } else if (mode == 2 && sp_title == null) {
            sp_title = getTextByMode(title, mode, null);
            sp_author = getTextByMode(author, mode, null);

            posi_text = new ArrayList<>();
            sp_text = getTextByMode(text, mode, posi_text);
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        if (mode == 0)
            return title;
        else if (mode == 1)
            return s_title;
        else
            return sp_title;
    }

    public String getAuthor() {
        if (mode == 0)
            return author;
        else if (mode == 1)
            return s_author;
        else
            return sp_author;
    }

    public String getText() {
        if (mode == 0)
            return text;
        else if (mode == 1)
            return s_text;
        else
            return sp_text;
    }

    public ArrayList<CodepointPosition> getPosiText() {
        return posi_text;
    }

    public static class CodepointPosition {
        public int begin;
        public int end;
        public int s_codepoint;

        public CodepointPosition(int begin, int end, int s_codepoint) {
            this.begin = begin;
            this.end = end;
            this.s_codepoint = s_codepoint;
        }
    }
}

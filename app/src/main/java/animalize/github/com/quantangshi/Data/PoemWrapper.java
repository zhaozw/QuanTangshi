package animalize.github.com.quantangshi.Data;

import java.util.ArrayList;

import animalize.github.com.quantangshi.T2sMap.T2s;

/**
 * Created by anima on 17-3-10.
 */

public class PoemWrapper {
    private RawPoem poem;
    private String text;

    private boolean has_s;
    private String s_title;
    private String s_author;
    private String s_text;

    private boolean has_sp;
    private String sp_title;
    private String sp_author;
    private String sp_text;
    private ArrayList<CodepointPosition> posi_text;

    public PoemWrapper(RawPoem poem, int lineBreak) {
        this.poem = poem;
        setLineBreak(lineBreak);
    }

    public PoemWrapper(RawPoem poem) {
        this.poem = poem;
        text = poem.getText();
    }

    public void setLineBreak(int lineBreak) {
        has_s = false;
        has_sp = false;

        String pattern = "([^，。！？…\\n]{" +
                lineBreak +
                ",}?[，。！？…])(?!(?:\\n|$))";
        text = poem.getText().replaceAll(pattern, "$1\n");
    }

    public int getID() {
        return poem.getId();
    }

    /* 模式
     * 0: 繁体
     * 1: 简体
     * 2: 简体+
    */
    public String getTitle(int mode) {
        if (mode == 0) {
            return poem.getTitle();
        } else if (mode == 1) {
            if (!has_s) {
                doT2s(1);
            }
            return s_title;
        } else if (mode == 2) {
            if (!has_sp) {
                doT2s(2);
            }
            return sp_title;
        }
        return "";
    }

    public String getAuthor(int mode) {
        if (mode == 0) {
            return poem.getAuthor();
        } else if (mode == 1) {
            if (!has_s) {
                doT2s(1);
            }
            return s_author;
        } else if (mode == 2) {
            if (!has_sp) {
                doT2s(2);
            }
            return sp_author;
        }
        return "";
    }

    public String getText(int mode) {
        if (mode == 0) {
            return text;
        } else if (mode == 1) {
            if (!has_s) {
                doT2s(1);
            }
            return s_text;
        } else if (mode == 2) {
            if (!has_sp) {
                doT2s(2);
            }
            return sp_text;
        }
        return "";
    }

    public ArrayList<CodepointPosition> getCodeList() {
        if (!has_sp) {
            doT2s(2);
        }
        return posi_text;
    }

    private void doT2s(int mode) {
        if (mode == 1) {
            s_title = T2s.t2s(poem.getTitle(), null);
            s_author = T2s.t2s(poem.getAuthor(), null);
            s_text = T2s.t2s(text, null);

            has_s = true;
        } else if (mode == 2) {
            sp_title = T2s.t2s(poem.getTitle(), null);
            sp_author = T2s.t2s(poem.getAuthor(), null);
            posi_text = new ArrayList<>();
            sp_text = T2s.t2s(text, posi_text);

            has_sp = true;
        }
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

package animalize.github.com.quantangshi.Data;

import android.content.Context;
import android.content.SharedPreferences;

import animalize.github.com.quantangshi.MyApplication;

/**
 * Created by anima on 17-3-10.
 */

public class Typeset {
    private static Typeset singleTong;

    private int titleLines;
    private int titleSize;
    private int textSize;
    private int lineSpace;
    private int lineBreak;

    private Typeset() {
        loadConfig();
    }

    public static Typeset getInstance() {
        if (singleTong == null) {
            singleTong = new Typeset();
        }
        return singleTong;
    }

    public void loadConfig() {
        Context c = MyApplication.getContext();
        SharedPreferences sp = c.getSharedPreferences(
                "typeset",
                Context.MODE_PRIVATE);

        titleLines = sp.getInt("title_lines", 2);
        titleSize = sp.getInt("title_size", 26);
        textSize = sp.getInt("text_size", 26);
        lineSpace = sp.getInt("line_space", 8);
        lineBreak = sp.getInt("line_break", 5);
    }

    public void saveConfig() {
        Context c = MyApplication.getContext();
        SharedPreferences.Editor editor = c.getSharedPreferences(
                "typeset",
                Context.MODE_PRIVATE).edit();

        editor.putInt("title_lines", titleLines);
        editor.putInt("title_size", titleSize);
        editor.putInt("text_size", textSize);
        editor.putInt("line_space", lineSpace);
        editor.putInt("line_break", lineBreak);
        editor.apply();
    }

    public int getTitleLines() {
        return titleLines;
    }

    public void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    public int getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getLineBreak() {
        return lineBreak;
    }

    public void setLineBreak(int lineBreak) {
        this.lineBreak = lineBreak;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }
}

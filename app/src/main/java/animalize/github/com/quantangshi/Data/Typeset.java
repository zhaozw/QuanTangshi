package animalize.github.com.quantangshi.Data;

/**
 * Created by anima on 17-3-10.
 */

public class Typeset {
    private int titleLines;
    private int titleSize;
    private int textSize;
    private int lineBreak;

    public Typeset() {
        titleLines = 2;
        titleSize = 26;
        textSize = 26;
        lineBreak = 5;
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
}

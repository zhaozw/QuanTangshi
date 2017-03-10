package animalize.github.com.quantangshi.Data;

/**
 * Created by anima on 17-3-10.
 */

public class RawPoem {
    private int id;
    private String title;
    private String author;
    private String text;

    public RawPoem(int id, String title, String author, String text) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}

package animalize.github.com.quantangshi.Data;


public class RecentInfo {
    private int id;
    private String title;
    private String author;
    private int time;

    public RecentInfo(int id, String title, String author, int time) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.time = time;
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

    public int getTime() {
        return time;
    }
}

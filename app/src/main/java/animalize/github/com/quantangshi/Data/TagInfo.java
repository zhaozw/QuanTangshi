package animalize.github.com.quantangshi.Data;


public class TagInfo {
    private int id;
    private String name;
    private int count;

    public TagInfo(int id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}

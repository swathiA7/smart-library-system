import java.io.Serializable;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private boolean available;

    public Book(String id, String title, String author, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public void borrow() { this.available = false; }
    public void returnBook() { this.available = true; }

    @Override
    public String toString() {
        return id + " — " + title + " by " + author + (available ? " (Available)" : " (Borrowed)");
    }
}

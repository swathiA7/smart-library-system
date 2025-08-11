// BorrowRecord.java
import java.io.Serializable;

public class BorrowRecord implements Serializable {
    private String user;
    private String bookId;
    private String date;
    private String action; // "Borrowed" or "Returned"

    public BorrowRecord(String user, String bookId, String date, String action) {
        this.user = user;
        this.bookId = bookId;
        this.date = date;
        this.action = action;
    }

    public String getUser() { return user; }
    public String getBookId() { return bookId; }
    public String getDate() { return date; }
    public String getAction() { return action; }
}

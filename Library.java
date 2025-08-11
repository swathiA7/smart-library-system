import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.*;
import java.util.stream.Collectors; // Import for stream API

public class Library {
    private static Library instance;
    private final List<Book> books = new ArrayList<>();
    private final List<BorrowRecord> history = new ArrayList<>();
    private final Map<String, String> users = new LinkedHashMap<>(); // preserve order
    private final Map<String, String> roles = new HashMap<>();

    private static final String BOOKS_FILE = "books.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String HISTORY_FILE = "history.txt";

    private Library() {}

    public static synchronized Library getInstance() {
        if (instance == null) instance = new Library();
        return instance;
    }

    // Load everything
    public void loadAll() {
        loadBooks();
        loadUsers();
        loadHistory();
    }

    // Save everything
    public void saveAll() {
        saveBooks();
        saveUsers();
        saveHistory();
    }

    private void loadBooks() {
        books.clear();
        Path p = Paths.get(BOOKS_FILE);
        if (!Files.exists(p)) return;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                // id;title;author;available
                String[] parts = line.split(";", -1);
                if (parts.length >= 4) {
                    books.add(new Book(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3])));
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void saveBooks() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(BOOKS_FILE))) {
            for (Book b : books) {
                bw.write(String.join(";", b.getId(), b.getTitle(), b.getAuthor(), Boolean.toString(b.isAvailable())));
                bw.newLine();
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void loadUsers() {
        users.clear();
        roles.clear();
        Path p = Paths.get(USERS_FILE);
        if (!Files.exists(p)) {
            // default accounts
            users.put("admin", "admin");
            roles.put("admin", "admin");
            users.put("student", "student123");
            roles.put("student", "student");
            saveUsers();
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                // username;password;role
                String[] parts = line.split(";", -1);
                if (parts.length >= 3) {
                    users.put(parts[0], parts[1]);
                    roles.put(parts[0], parts[2]);
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void saveUsers() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(USERS_FILE))) {
            for (String u : users.keySet()) {
                bw.write(String.join(";", u, users.get(u), roles.getOrDefault(u, "student")));
                bw.newLine();
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void loadHistory() {
        history.clear();
        Path p = Paths.get(HISTORY_FILE);
        if (!Files.exists(p)) return;
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                // user;bookId;date;action  OR legacy user;bookId;date
                String[] parts = line.split(";", -1);
                if (parts.length >= 4) history.add(new BorrowRecord(parts[0], parts[1], parts[2], parts[3]));
                else if (parts.length >= 3) history.add(new BorrowRecord(parts[0], parts[1], parts[2], "Borrowed"));
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void saveHistory() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(HISTORY_FILE))) {
            for (BorrowRecord r : history) {
                bw.write(String.join(";", r.getUser(), r.getBookId(), r.getDate(), r.getAction()));
                bw.newLine();
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // --- Book operations ---
    public synchronized boolean addBook(Book book) {
        if (findBookById(book.getId()) != null) return false;
        books.add(book);
        return true;
    }

    public synchronized boolean removeBookById(String id) {
        return books.removeIf(b -> b.getId().equals(id));
    }

    public synchronized List<Book> getAllBooks() { return new ArrayList<>(books); }
    public synchronized List<Book> getAvailableBooks() {
        List<Book> r = new ArrayList<>();
        for (Book b : books) if (b.isAvailable()) r.add(b);
        return r;
    }

    public synchronized Book findBookById(String id) {
        for (Book b : books) if (b.getId().equals(id)) return b;
        return null;
    }

    public synchronized Book findBookByTitle(String title) {
        for (Book b : books) if (b.getTitle().equalsIgnoreCase(title)) return b;
        return null;
    }

    // New search methods
    public synchronized List<Book> searchBooks(String query, String filterType) {
        if (query == null || query.trim().isEmpty() || filterType == null || filterType.equalsIgnoreCase("All")) {
            return getAllBooks();
        }

        String lowerCaseQuery = query.toLowerCase();
        return books.stream()
                .filter(book -> {
                    switch (filterType.toLowerCase()) {
                        case "id":
                            return book.getId().toLowerCase().contains(lowerCaseQuery);
                        case "title":
                            return book.getTitle().toLowerCase().contains(lowerCaseQuery);
                        case "author":
                            return book.getAuthor().toLowerCase().contains(lowerCaseQuery);
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public synchronized List<Book> searchAvailableBooks(String query, String filterType) {
        if (query == null || query.trim().isEmpty() || filterType == null || filterType.equalsIgnoreCase("All")) {
            return getAvailableBooks();
        }

        String lowerCaseQuery = query.toLowerCase();
        return books.stream()
                .filter(Book::isAvailable)
                .filter(book -> {
                    switch (filterType.toLowerCase()) {
                        case "id":
                            return book.getId().toLowerCase().contains(lowerCaseQuery);
                        case "title":
                            return book.getTitle().toLowerCase().contains(lowerCaseQuery);
                        case "author":
                            return book.getAuthor().toLowerCase().contains(lowerCaseQuery);
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }


    // borrow / return
    public synchronized boolean borrowBook(String username, String bookId) {
        Book b = findBookById(bookId);
        if (b != null && b.isAvailable()) {
            b.setAvailable(false);
            history.add(new BorrowRecord(username, bookId, LocalDate.now().toString(), "Borrowed"));
            return true;
        }
        return false;
    }

    public synchronized boolean returnBook(String username, String bookId) {
        Book b = findBookById(bookId);
        if (b != null && !b.isAvailable()) {
            b.setAvailable(true);
            history.add(new BorrowRecord(username, bookId, LocalDate.now().toString(), "Returned"));
            return true;
        }
        return false;
    }

    public synchronized List<BorrowRecord> getHistoryForUser(String username) {
        List<BorrowRecord> r = new ArrayList<>();
        for (BorrowRecord rec : history) if (rec.getUser().equals(username)) r.add(rec);
        return r;
    }

    public synchronized List<BorrowRecord> getAllHistory() { return new ArrayList<>(history); }

    // users
    public synchronized boolean addUser(String username, String password, String role) {
        if (users.containsKey(username)) return false;
        users.put(username, password);
        roles.put(username, role);
        return true;
    }

    public synchronized boolean validateUser(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public synchronized String getUserRole(String username) { return roles.getOrDefault(username, "student"); }

    // stats
    public synchronized int totalBooks() { return books.size(); }
    public synchronized int totalUsers() { return users.size(); }
    public synchronized int totalBorrowed() {
        int c = 0;
        for (Book b : books) if (!b.isAvailable()) c++;
        return c;
    }
}

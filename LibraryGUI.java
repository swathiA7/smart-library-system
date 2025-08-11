import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibraryGUI extends JFrame {
    private final Library lib = Library.getInstance();
    private final String username;
    private final DefaultTableModel model;
    private final JTable table; // Make table a field
    private final JTextField searchField; // Search field
    private final JComboBox<String> filterComboBox; // Filter combo box

    public LibraryGUI(String username) {
        this.username = username;
        setTitle("Smart Library — Admin: " + username);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new BorderLayout(10,10));
        top.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Controls Panel (top-left)
        JPanel controls = new JPanel();
        JButton addBtn = styled("Add Book");
        JButton removeBtn = styled("Remove Book");
        JButton saveBtn = styled("Save Now");
        JButton dashboardBtn = styled("Dashboard");
        JButton logoutBtn = styled("Logout");
        controls.add(addBtn); controls.add(removeBtn); controls.add(saveBtn); controls.add(dashboardBtn); controls.add(logoutBtn);
        top.add(controls, BorderLayout.NORTH);

        // Search Panel (top-center)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchField = new JTextField(20);
        filterComboBox = new JComboBox<>(new String[]{"All", "ID", "Title", "Author"});
        JButton searchBtn = styled("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(searchBtn);
        top.add(searchPanel, BorderLayout.CENTER);


        String[] cols = {"ID","Title","Author","Available"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model); // Initialize table
        loadTable(lib.getAllBooks()); // Load all books initially

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton borrowBtn = styled("Borrow");
        JButton returnBtn = styled("Return");
        JButton historyBtn = styled("View History");
        bottom.add(borrowBtn); bottom.add(returnBtn); bottom.add(historyBtn);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Book ID:");
            if (id == null || id.trim().isEmpty()) return;
            String title = JOptionPane.showInputDialog(this, "Title:");
            if (title == null) return;
            String author = JOptionPane.showInputDialog(this, "Author:");
            if (author == null) return;
            Book b = new Book(id.trim(), title.trim(), author.trim(), true);
            if (lib.addBook(b)) { loadTable(lib.getAllBooks()); lib.saveAll(); JOptionPane.showMessageDialog(this, "Added."); }
            else JOptionPane.showMessageDialog(this, "ID exists.");
        });

        removeBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Book ID to remove:");
            if (id == null) return;
            if (lib.removeBookById(id.trim())) { loadTable(lib.getAllBooks()); lib.saveAll(); JOptionPane.showMessageDialog(this, "Removed."); }
            else JOptionPane.showMessageDialog(this, "Not found.");
        });

        saveBtn.addActionListener(e -> { lib.saveAll(); JOptionPane.showMessageDialog(this, "Saved."); });
        dashboardBtn.addActionListener(e -> new DashboardGUI());
        logoutBtn.addActionListener(e -> { dispose(); new LoginGUI(); });

        borrowBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Book ID to borrow:");
            if (id == null) return;
            if (lib.borrowBook(username, id.trim())) { loadTable(lib.getAllBooks()); lib.saveAll(); JOptionPane.showMessageDialog(this, "Borrow recorded."); }
            else JOptionPane.showMessageDialog(this, "Cannot borrow.");
        });

        returnBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Book ID to return:");
            if (id == null) return;
            if (lib.returnBook(username, id.trim())) { loadTable(lib.getAllBooks()); lib.saveAll(); JOptionPane.showMessageDialog(this, "Return recorded."); }
            else JOptionPane.showMessageDialog(this, "Cannot return.");
        });

        historyBtn.addActionListener(e -> new BorrowHistoryGUI_Admin(lib.getAllHistory()));

        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // Allow pressing Enter in search field

        setVisible(true);
    }

    private void loadTable(List<Book> booksToDisplay) {
        model.setRowCount(0);
        for (Book b : booksToDisplay) model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.isAvailable() ? "Yes" : "No"});
    }

    private void performSearch() {
        String query = searchField.getText();
        String filterType = (String) filterComboBox.getSelectedItem();
        List<Book> searchResults = lib.searchBooks(query, filterType);
        loadTable(searchResults);
    }

    private JButton styled(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(new Color(59, 89, 152));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}

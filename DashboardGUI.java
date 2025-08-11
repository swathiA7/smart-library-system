// DashboardGUI.java
import javax.swing.*;
import java.awt.*;

public class DashboardGUI extends JFrame {
    public DashboardGUI() {
        setTitle("Dashboard");
        setSize(520, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Library lib = Library.getInstance();

        int total = lib.totalBooks();
        int users = lib.totalUsers();
        int borrowed = lib.totalBorrowed();
        int available = total - borrowed;

        JPanel root = new JPanel(new GridLayout(2,2,12,12));
        root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        root.add(card("📚 Total Books", String.valueOf(total), new Color(66,133,244)));
        root.add(card("👥 Total Users", String.valueOf(users), new Color(52,168,83)));
        root.add(card("📖 Borrowed", String.valueOf(borrowed), new Color(244,180,0)));
        root.add(card("✅ Available", String.valueOf(available), new Color(124,77,255)));

        add(root);
        setVisible(true);
    }

    private JPanel card(String title, String value, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color,2), BorderFactory.createEmptyBorder(10,10,10,10)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 28));
        v.setForeground(color);
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}

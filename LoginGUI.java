// LoginGUI.java
import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {
    private final JTextField userField = new JTextField();
    private final JPasswordField passField = new JPasswordField();

    public LoginGUI() {
        setTitle("Smart Library — Login");
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        JLabel title = new JLabel("Welcome to Smart Library", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new GridLayout(2,2,10,10));
        center.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        center.add(new JLabel("Username:"));
        center.add(userField);
        center.add(new JLabel("Password:"));
        center.add(passField);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton loginBtn = styledButton("Login");
        JButton signupBtn = styledButton("Sign up");
        bottom.add(loginBtn);
        bottom.add(signupBtn);
        add(bottom, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> doLogin());
        signupBtn.addActionListener(e -> doSignup());

        setVisible(true);
    }

    private void doLogin() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        Library lib = Library.getInstance();
        if (lib.validateUser(u, p)) {
            String role = lib.getUserRole(u);
            dispose();
            if ("admin".equalsIgnoreCase(role)) new LibraryGUI(u);
            else new StudentLibraryGUI(u);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSignup() {
        String username = JOptionPane.showInputDialog(this, "Choose username:");
        if (username == null || username.trim().isEmpty()) return;
        String password = JOptionPane.showInputDialog(this, "Choose password:");
        if (password == null || password.trim().isEmpty()) return;

        String[] options = {"student", "admin"};
        String role = (String) JOptionPane.showInputDialog(this, "Select role:", "Role", JOptionPane.QUESTION_MESSAGE, null, options, "student");
        if (role == null) role = "student";

        Library lib = Library.getInstance();
        if (lib.addUser(username, password, role)) {
            lib.saveAll();
            JOptionPane.showMessageDialog(this, "Signup successful! Please login.");
        } else {
            JOptionPane.showMessageDialog(this, "Username exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(new Color(66,133,244));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}

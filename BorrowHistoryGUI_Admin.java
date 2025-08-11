// BorrowHistoryGUI_Admin.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BorrowHistoryGUI_Admin extends JFrame {
    public BorrowHistoryGUI_Admin(List<BorrowRecord> records) {
        setTitle("All Borrow History");
        setSize(760, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] cols = {"User","Book ID","Date","Action"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        for (BorrowRecord r : records) model.addRow(new Object[]{r.getUser(), r.getBookId(), r.getDate(), r.getAction()});

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setVisible(true);
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ContactManagingSystem extends JFrame {

    private JTextField nameField, phoneField, emailField;
    private JTable contactTable;
    private DefaultTableModel tableModel;

    private final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private final String USER = "root";
    private final String PASS = "ananya@sql";

    public ContactManagingSystem() {
        setTitle("Contact Management System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel for input
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        nameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);

        JButton addButton = new JButton("Add Contact");
        JButton refreshButton = new JButton("Refresh");
        inputPanel.add(addButton);
        inputPanel.add(refreshButton);

        add(inputPanel, BorderLayout.NORTH);

        // Center Panel for Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone", "Email"}, 0);
        contactTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(contactTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel for Delete
        JButton deleteButton = new JButton("Delete Selected Contact");
        add(deleteButton, BorderLayout.SOUTH);

        
        addButton.addActionListener(e -> addContact());
        refreshButton.addActionListener(e -> loadContacts());
        deleteButton.addActionListener(e -> deleteSelectedContact());

        
        loadContacts();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO contacts (Name, Phone_Number, Email_id) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Contact added successfully!");
            nameField.setText(""); phoneField.setText(""); emailField.setText("");
            loadContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM contacts";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("Name"),
                        rs.getString("Phone_Number"),
                        rs.getString("Email_id")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM contacts WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Contact deleted.");
            loadContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContactManagingSystem().setVisible(true));
    }
}

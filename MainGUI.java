// MainGUI.java
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainGUI {
    private JFrame frame;
    private MemberRepository repo;
    private JTable table;
    private DefaultTableModel tableModel;

    public MainGUI(MemberRepository repo) {
        this.repo = repo;
        initUI();
        loadTableData();
    }

    private void initUI() {
        frame = new JFrame("MMS - GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLayout(new BorderLayout());

        // Table columns
        String[] cols = {"ID","Name","Email","BaseFee","Perf","Type","CalcFee"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true); // allow column sorting
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons panel
        JPanel p = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updBtn = new JButton("Update");
        JButton delBtn = new JButton("Delete");
        JButton searchBtn = new JButton("Search");
        JButton saveBtn = new JButton("Save");
        p.add(addBtn); p.add(updBtn); p.add(delBtn); p.add(searchBtn); p.add(saveBtn);
        frame.add(p, BorderLayout.SOUTH);

        // Button handlers
        addBtn.addActionListener(e -> showAddDialog());
        updBtn.addActionListener(e -> showUpdateDialogForSelected());
        delBtn.addActionListener(e -> deleteSelected());
        searchBtn.addActionListener(e -> showSearchDialog());
        saveBtn.addActionListener(e -> { try { repo.saveMembersToFile("member_data.csv"); JOptionPane.showMessageDialog(frame,"Saved."); } catch (Exception ex) { JOptionPane.showMessageDialog(frame,"Save failed: "+ex.getMessage()); } });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Member> list = repo.getMembers();
        for (Member m : list) {
            String type = (m instanceof TrainerMember) ? "Trainer" : "Regular";
            tableModel.addRow(new Object[] { m.getId(), m.getFullName(), m.getEmail(), m.getBaseFee(), m.getPerformanceRating(), type, String.format("%.2f", m.calculateFee()) });
        }
    }

    private void showAddDialog() {
        // Simple form using JOptionPane - for production use custom JDialog
        JTextField idF = new JTextField(), fn = new JTextField(), ln = new JTextField(), email = new JTextField(), fee = new JTextField(), perf = new JTextField(), type = new JTextField();
        Object[] inputs = {"ID", idF, "First name", fn, "Last name", ln, "Email", email, "Base fee", fee, "Perf (0-100)", perf, "Type (Regular/Trainer)", type};
        int res = JOptionPane.showConfirmDialog(frame, inputs, "Add Member", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String id = idF.getText().trim();
                double base = Double.parseDouble(fee.getText().trim());
                int p = Integer.parseInt(perf.getText().trim());
                String t = type.getText().trim();
                Member m;
                if (t.equalsIgnoreCase("Trainer")) m = new TrainerMember(id, fn.getText().trim(), ln.getText().trim(), email.getText().trim(), base, p, "TrainerX");
                else m = new RegularMember(id, fn.getText().trim(), ln.getText().trim(), email.getText().trim(), base, p);
                boolean ok = repo.addMember(m);
                if (!ok) JOptionPane.showMessageDialog(frame, "ID exists");
                else { loadTableData(); JOptionPane.showMessageDialog(frame,"Added."); }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void showUpdateDialogForSelected() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(frame,"Select a row first."); return; }
        String id = (String) table.getValueAt(table.convertRowIndexToModel(r), 0);
        Member m = repo.findById(id);
        if (m == null) { JOptionPane.showMessageDialog(frame,"Member not found."); return; }
        // For brevity, reuse Add dialog pattern; in practice design dedicated update dialog
        JTextField fn = new JTextField(m.getFirstName()), ln = new JTextField(m.getLastName()), email = new JTextField(m.getEmail()), fee = new JTextField(String.valueOf(m.getBaseFee())), perf = new JTextField(String.valueOf(m.getPerformanceRating()));
        Object[] inputs = {"First name", fn, "Last name", ln, "Email", email, "Base fee", fee, "Perf (0-100)", perf};
        int res = JOptionPane.showConfirmDialog(frame, inputs, "Update Member "+id, JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                m.setFirstName(fn.getText().trim()); m.setLastName(ln.getText().trim()); m.setEmail(email.getText().trim());
                m.setBaseFee(Double.parseDouble(fee.getText().trim())); m.setPerformanceRating(Integer.parseInt(perf.getText().trim()));
                repo.updateMember(m); loadTableData(); JOptionPane.showMessageDialog(frame,"Updated.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame,"Invalid input."); }
        }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(frame,"Select a row"); return; }
        String id = (String) table.getValueAt(table.convertRowIndexToModel(r), 0);
        int conf = JOptionPane.showConfirmDialog(frame, "Delete " + id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) { repo.deleteMember(id); loadTableData(); JOptionPane.showMessageDialog(frame,"Deleted."); }
    }

    private void showSearchDialog() {
        String q = JOptionPane.showInputDialog(frame, "Enter name or ID to search (partial name allowed):");
        if (q == null || q.trim().isEmpty()) return;
        List<Member> byName = repo.findByName(q);
        Member byId = repo.findById(q);
        StringBuilder out = new StringBuilder();
        if (byId != null) out.append(byId).append("\n");
        for (Member m : byName) out.append(m).append("\n");
        if (out.length() == 0) out.append("No matches");
        JOptionPane.showMessageDialog(frame, out.toString());
    }

    public static void createAndShowGUI(MemberRepository repo) {
        new MainGUI(repo);
    }
}

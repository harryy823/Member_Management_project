// GUIManager.java
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUIManager {
    private MemberRepository repo;
    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable table;

    public GUIManager(MemberRepository repo) {
        this.repo = repo;
    }

    public void initGUI() {
        frame = new JFrame("Member Management System - GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,650);
        frame.setLayout(new BorderLayout());

        // Top panel - form
        JPanel form = new JPanel(new GridLayout(4,4,6,6));
        JTextField idField = new JTextField();
        JTextField fnField = new JTextField();
        JTextField lnField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField feeField = new JTextField();
        JTextField perfField = new JTextField();
        JTextField trainerField = new JTextField();
        JTextField monthField = new JTextField();
        JCheckBox achievedChk = new JCheckBox("Goal Achieved");
        JTextField notesField = new JTextField();

        form.add(new JLabel("ID:")); form.add(idField);
        form.add(new JLabel("First name:")); form.add(fnField);
        form.add(new JLabel("Last name:")); form.add(lnField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Base fee:")); form.add(feeField);
        form.add(new JLabel("Performance (0-100):")); form.add(perfField);
        form.add(new JLabel("Trainer (if any):")); form.add(trainerField);
        form.add(new JLabel("Perf Month (e.g., 2025-09):")); form.add(monthField);
        form.add(achievedChk); form.add(notesField);

        frame.add(form, BorderLayout.NORTH);

        // Center - table
        tableModel = new DefaultTableModel(new Object[]{"ID","First","Last","Email","BaseFee","Perf","Type","Trainer"},0);
        table = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(table);
        frame.add(sp, BorderLayout.CENTER);

        // Bottom - buttons
        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton loadBtn = new JButton("Load File");
        JButton saveBtn = new JButton("Save File");
        JButton searchBtn = new JButton("Search by ID");
        JButton sortNameBtn = new JButton("Sort by First Name");
        JButton addPerfBtn = new JButton("Add Performance");
        JButton genLetterBtn = new JButton("Generate Letter");
        buttons.add(addBtn); buttons.add(updateBtn); buttons.add(deleteBtn);
        buttons.add(searchBtn); buttons.add(sortNameBtn);
        buttons.add(addPerfBtn); buttons.add(genLetterBtn);
        buttons.add(loadBtn); buttons.add(saveBtn);
        frame.add(buttons, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String fn = fnField.getText().trim();
                String ln = lnField.getText().trim();
                String email = emailField.getText().trim();
                double fee = Double.parseDouble(feeField.getText().trim());
                int perf = Integer.parseInt(perfField.getText().trim());
                String trainer = trainerField.getText().trim();
                if (id.isEmpty()||fn.isEmpty()||ln.isEmpty()) { JOptionPane.showMessageDialog(frame, "ID/Name required"); return; }
                if (trainer.isEmpty()) {
                    repo.addMember(new RegularMember(id, fn, ln, email, fee, perf));
                } else {
                    repo.addMember(new TrainerMember(id, fn, ln, email, fee, perf, trainer));
                }
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Member added.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number format (fee or perf).");
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                Member m = repo.findById(id);
                if (m == null) { JOptionPane.showMessageDialog(frame, "Member not found."); return; }
                m.setFirstName(fnField.getText().trim());
                m.setLastName(lnField.getText().trim());
                m.setEmail(emailField.getText().trim());
                m.setBaseFee(Double.parseDouble(feeField.getText().trim()));
                m.setPerformanceRating(Integer.parseInt(perfField.getText().trim()));
                if (m instanceof TrainerMember) {
                    String t = trainerField.getText().trim();
                    ((TrainerMember)m).setTrainerName(t);
                }
                repo.updateMember(m);
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Member updated.");
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid number format."); }
        });

        deleteBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (repo.deleteMember(id)) {
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Member deleted.");
            } else JOptionPane.showMessageDialog(frame, "Member not found."); 
        });

        loadBtn.addActionListener(e -> {
            String fname = JOptionPane.showInputDialog(frame, "Enter filename to load (member_data.csv):");
            if (fname != null) {
                try { repo.loadFromFile(fname); refreshTable(); JOptionPane.showMessageDialog(frame, "Loaded."); }
                catch(Exception ex) { JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage()); }
            }
        });

        saveBtn.addActionListener(e -> {
            String fname = JOptionPane.showInputDialog(frame, "Enter filename to save (member_data_out.csv):");
            if (fname != null) {
                try { repo.saveToFile(fname); JOptionPane.showMessageDialog(frame, "Saved."); }
                catch(Exception ex) { JOptionPane.showMessageDialog(frame, "Save error: " + ex.getMessage()); }
            }
        });

        searchBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(frame, "Enter ID to search:");
            if (id != null) {
                Member m = repo.findById(id.trim());
                if (m == null) JOptionPane.showMessageDialog(frame, "Not found.");
                else JOptionPane.showMessageDialog(frame, m.toString());
            }
        });

        sortNameBtn.addActionListener(e -> {
            repo.sortByFirstName();
            refreshTable();
        });

        addPerfBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String month = monthField.getText().trim();
                boolean achieved = achievedChk.isSelected();
                String notes = notesField.getText().trim();
                if (id.isEmpty() || month.isEmpty()) { JOptionPane.showMessageDialog(frame, "ID and month required."); return; }
                Performance p = new Performance(id, month, achieved, notes);
                repo.addPerformanceRecord(id, p);
                JOptionPane.showMessageDialog(frame, "Performance recorded."); 
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error adding performance: " + ex.getMessage()); }
        });

        genLetterBtn.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String month = monthField.getText().trim();
                if (id.isEmpty() || month.isEmpty()) { JOptionPane.showMessageDialog(frame, "ID and month required for letter."); return; }
                int opt = JOptionPane.showConfirmDialog(frame, "Generate appreciation letter? (yes=no -> reminder)", "Letter type", JOptionPane.YES_NO_CANCEL_OPTION);
                if (opt == JOptionPane.CANCEL_OPTION) return;
                boolean appreciation = (opt == JOptionPane.YES_OPTION);
                String path = repo.generateLetter(id, month, appreciation);
                JOptionPane.showMessageDialog(frame, "Letter created: " + path);
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error generating letter: " + ex.getMessage()); }
        });

        // double click on row to populate form
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        idField.setText(tableModel.getValueAt(row,0).toString());
                        fnField.setText(tableModel.getValueAt(row,1).toString());
                        lnField.setText(tableModel.getValueAt(row,2).toString());
                        emailField.setText(tableModel.getValueAt(row,3).toString());
                        feeField.setText(tableModel.getValueAt(row,4).toString());
                        perfField.setText(tableModel.getValueAt(row,5).toString());
                        trainerField.setText(tableModel.getValueAt(row,7).toString());
                    }
                }
            }
        });

        // initial load into table if any
        refreshTable();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Member> list = repo.getMembers();
        for (Member m : list) {
            if (m instanceof TrainerMember) {
                TrainerMember t = (TrainerMember)m;
                tableModel.addRow(new Object[]{t.getId(), t.getFirstName(), t.getLastName(), t.getEmail(), t.getBaseFee(), t.getPerformanceRating(), "Trainer", t.getTrainerName()});
            } else {
                tableModel.addRow(new Object[]{m.getId(), m.getFirstName(), m.getLastName(), m.getEmail(), m.getBaseFee(), m.getPerformanceRating(), "Regular", ""});
            }
        }
    }
}

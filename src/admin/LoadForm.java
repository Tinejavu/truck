/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import config.Session;
import config.dbConnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author milan
 */
public class LoadForm extends javax.swing.JFrame {

    /**
     * Creates new form LoadForm
     */
    public LoadForm() {
        initComponents();
    }
   public void displayLoadData() {
    try {
        dbConnector dbc = new dbConnector();
        // Now include the status column in the SELECT query
        ResultSet rs = dbc.getData("SELECT load_id, cargo, destination, weight, status FROM tbl_loads");
        
        // Set the table model to display load data
        load_tbl.setModel(DbUtils.resultSetToTableModel(rs));  // Assuming load_tbl is your JTable
        
        rs.close();
    } catch (SQLException ex) {
        System.out.println("Errors: " + ex.getMessage());
    }
}

    
    DefaultTableModel model = new DefaultTableModel();

   public void tableChanged(TableModelEvent e) {
    if (e.getType() == TableModelEvent.UPDATE) {
        int row = e.getFirstRow();
        int column = e.getColumn();

        if (row == -1 || column == -1) {
            return; 
        }

        updateLoadDatabase(row, column); // You may need to define a similar update function for loads
    }

  String[] columnNames = {"load_id", "cargo", "destination", "weight", "status"};
model.setColumnIdentifiers(columnNames);
model.setRowCount(0);

String sql = "SELECT load_id, cargo, destination, weight, status FROM tbl_loads"; // include status here

try (Connection connect = new dbConnector().getConnection();
     PreparedStatement pst = connect.prepareStatement(sql);
     ResultSet rs = pst.executeQuery()) {

    while (rs.next()) {
        Object[] row = {
            rs.getInt("load_id"),
            rs.getString("cargo"),
            rs.getString("destination"),
            rs.getString("weight"),
            rs.getString("status") // add status here
        };
        model.addRow(row);
    }

} catch (SQLException ex) {
    JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}

}


private void updateLoadDatabase(int row, int column) {
    try (Connection connect = new dbConnector().getConnection()) {
        String columnName = load_tbl.getColumnName(column); // Get the column name
        String newValue = load_tbl.getValueAt(row, column).toString(); // Get the updated value
        int loadId = Integer.parseInt(load_tbl.getValueAt(row, 0).toString()); // Get the load ID from the first column

        String sql = "UPDATE tbl_loads SET " + columnName + " = ? WHERE load_id = ?";
        try (PreparedStatement pst = connect.prepareStatement(sql)) {
            pst.setString(1, newValue); // Set the updated value
            pst.setInt(2, loadId); // Set the load ID for the row being updated
            pst.executeUpdate(); // Execute the update
            JOptionPane.showMessageDialog(null, "Load Updated Successfully!");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


public void addLoad() {
    // Create and show the NewLoad form
    NewLoad addLoad = new NewLoad(); 
    addLoad.setVisible(true);
}

public void addLoad(String cargo, String destination, String weight) {
    Connection con = null;
    PreparedStatement pst = null;

    try {
        con = new dbConnector().getConnection();

        if (con == null) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed!");
            return;
        }

        String query = "INSERT INTO tbl_loads (cargo, destination, weight, status) VALUES (?, ?, ?, ?)";
        pst = con.prepareStatement(query);
        pst.setString(1, cargo);
        pst.setString(2, destination);
        pst.setString(3, weight);
        pst.setString(4, "Pending"); // Always set "Pending" when adding

        int rowsInserted = pst.executeUpdate();
        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(null, "Load Added Successfully (Status: Pending)!");

            loadLoadsData(); // Refresh table after adding load
        } else {
            JOptionPane.showMessageDialog(null, "Error adding load.");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}




private void loadLoadsData() {
    DefaultTableModel model = (DefaultTableModel) load_tbl.getModel();  // Assuming load_tbl is the JTable for loads
    model.setRowCount(0); // Clear the table before reloading

    // Include 'status' in the query
    String sql = "SELECT load_id, cargo, destination, weight, status FROM tbl_loads";

    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/truck", "root", "");
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        // Iterate through ResultSet and add rows to the table
        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getInt("load_id"),
                rs.getString("cargo"),
                rs.getString("destination"),
                rs.getString("weight"),
                rs.getString("status")  // <-- Add status here
            });
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading load data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void deleteLoad() {
    Session sess = Session.getInstance();  // Logged-in admin
    int selectedRow = load_tbl.getSelectedRow();  // Assuming load_tbl is the JTable for loads
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a load to delete.");
        return;
    }

    String loadId = load_tbl.getValueAt(selectedRow, 0).toString();  // Get the Load ID from the selected row
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this load?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        String sql = "DELETE FROM tbl_loads WHERE load_id = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/truck", "root", "");
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, loadId);  // Set the Load ID to delete
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Load Deleted Successfully!");

                // ✅ Logging the deletion action
                String logQuery = "INSERT INTO tbl_log (u_id, u_username, u_type, log_status, log_description) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement logPst = con.prepareStatement(logQuery)) {
                    logPst.setInt(1, sess.getUid()); // Admin ID from session
                    logPst.setString(2, sess.getUsername()); // Admin username
                    logPst.setString(3, sess.getType()); // Admin type, e.g., "Admin"
                    logPst.setString(4, "Active"); // Status of the log
                    logPst.setString(5, "Deleted load with ID: " + loadId); // Description
                    logPst.executeUpdate();
                } catch (SQLException logEx) {
                    JOptionPane.showMessageDialog(this, "Log Error: " + logEx.getMessage(), "Log Error", JOptionPane.ERROR_MESSAGE);
                }

                loadLoadsData();  // Ensure this method exists to reload the load data or table

            } else {
                JOptionPane.showMessageDialog(this, "No load found to delete.", "Deletion Failed", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        add = new javax.swing.JButton();
        update = new javax.swing.JButton();
        register1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        load_tbl = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        add.setBackground(new java.awt.Color(0, 0, 204));
        add.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        add.setText("ADD");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });
        jPanel1.add(add, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 160, 40));

        update.setBackground(new java.awt.Color(0, 0, 204));
        update.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        update.setText("UPDATE");
        update.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateMouseClicked(evt);
            }
        });
        update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });
        jPanel1.add(update, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 160, 40));

        register1.setBackground(new java.awt.Color(0, 0, 204));
        register1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        register1.setText("DELETE");
        register1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                register1ActionPerformed(evt);
            }
        });
        jPanel1.add(register1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, 160, 40));

        jButton1.setBackground(new java.awt.Color(0, 0, 204));
        jButton1.setText("BACK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 0, 90, -1));

        load_tbl.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        load_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Load ID", "Cargo", "Destination", "Weight", "Status"
            }
        ));
        jScrollPane2.setViewportView(load_tbl);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 760, 370));

        jButton2.setBackground(new java.awt.Color(0, 0, 204));
        jButton2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButton2.setText("REFRESH");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, 180, 40));

        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("LOAD FORM");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, -1, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 180, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 480));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed

        addLoad();
    }//GEN-LAST:event_addActionPerformed

    private void updateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateMouseClicked
        int selectedRow = load_tbl.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Load to edit.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = load_tbl.getValueAt(selectedRow, load_tbl.getColumn("Load_id").getModelIndex()).toString();

        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selected user has no Load.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;

        }
    }//GEN-LAST:event_updateMouseClicked

    private void updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateActionPerformed
     int rowIndex = load_tbl.getSelectedRow(); // Get the selected row index from load_tbl
    if (rowIndex < 0) {
        JOptionPane.showMessageDialog(null, "Please select a Load!");
        return;
    }

    try {
        dbConnector dbc = new dbConnector();
        TableModel tbl = load_tbl.getModel(); // Get the table model of load_tbl

        // Get the Load ID from the selected row (assuming it's in the first column, index 0)
        String loadId = tbl.getValueAt(rowIndex, 0).toString();
        
        if (loadId == null || loadId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selected load does not have a valid Load ID.");
            return;
        }

        // Query to fetch load details from the database
        String query = "SELECT * FROM tbl_loads WHERE load_id = ?";
        PreparedStatement pst = dbc.getConnection().prepareStatement(query);
        pst.setString(1, loadId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // Assuming NewLoad is the form to update the load data
            NewLoad nd = new NewLoad();

            // Set the load data to the form fields
            nd.loadID.setText(loadId); // Set Load ID in the form
            nd.cg.setText(rs.getString("cargo")); // Set Cargo in the form
            nd.dt.setText(rs.getString("destination")); // Set Destination in the form
            nd.wt.setText(rs.getString("weight")); // Set Weight in the form

            nd.setVisible(true);  // Show the form to update the load
            this.dispose(); // Close the current form (optional)
        } else {
            JOptionPane.showMessageDialog(null, "Load not found in the database.");
        }
    } catch (SQLException ex) {
        // Log error details and show a message to the user
        JOptionPane.showMessageDialog(null, "Error occurred while retrieving load data: " + ex.getMessage(), 
                                      "Database Error", JOptionPane.ERROR_MESSAGE);
    }  // TODO add your handling code here:
    }//GEN-LAST:event_updateActionPerformed

    private void register1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_register1ActionPerformed
        deleteLoad();
    }//GEN-LAST:event_register1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     adminDashboard adminDashboard = new adminDashboard();
    adminDashboard.setVisible(true); // Show the AdminDashboard
    this.dispose();  // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      loadLoadsData();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoadForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoadForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoadForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoadForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoadForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable load_tbl;
    private javax.swing.JButton register1;
    private javax.swing.JButton update;
    // End of variables declaration//GEN-END:variables
}

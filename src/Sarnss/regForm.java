
package Sarnss;

import config.dbConnector;
import config.passwordHasher;
import java.awt.Color;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;


public class regForm extends javax.swing.JFrame {

   
    public regForm() {
        initComponents(); 
    }
    Color redd = new Color(0,0,153);
    Color shok = new Color(255,255,255);
    
    public static String dbemail, dbusername;
    
    
    public boolean duplicateCheck(){
        
        dbConnector dbc = new dbConnector();
        
        try{
            String query = "SELECT * FROM tbl_users  WHERE u_username = '" + us.getText() + "' OR u_email = '" + mail.getText() + "'";
            ResultSet resultSet = dbc.getData(query);
            
            if(resultSet.next()){
                String email = resultSet.getString("u_email");
                if(email.equals(mail.getText())){
                      JOptionPane.showMessageDialog(null, "Email is Already Used!");
                      mail.setText("");
                }
                String username = resultSet.getString("u_username");
                if(username.equals(us.getText())){
                      JOptionPane.showMessageDialog(null, "Email is Already Used!");
                      us.setText("");
            }
            return true;
            }else{
                return false;
            }
          
        }catch (SQLException ex) {
            System.out.println(""+ex);
            return false;
        }
    }
    
     public void logEvent(int userId, String username, String description) {
    dbConnector dbc = new dbConnector();
    Connection con = dbc.getConnection();
    PreparedStatement pstmt = null;

    try {
        // Fixed: include `log_description` in your INSERT
        String sql = "INSERT INTO tbl_log (u_id, u_username, login_time, u_type, log_status, log_description) VALUES (?, ?, ?, ?, ?, ?)";
        pstmt = con.prepareStatement(sql);

        pstmt.setInt(1, userId);
        pstmt.setString(2, username);
        pstmt.setTimestamp(3, new Timestamp(new Date().getTime())); // login_time
        pstmt.setString(4, "Success - User Action"); // u_type (general category)
        pstmt.setString(5, "Active"); // log_status
        pstmt.setString(6, description); // log_description (e.g., "User Reset Their Password")

        pstmt.executeUpdate();
        System.out.println("Log event recorded successfully.");
    } catch (SQLException e) {
        System.out.println("Error recording log: " + e.getMessage());
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing resources: " + e.getMessage());
        }
    }
}

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton1 = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jRegister = new javax.swing.JButton();
        ut = new javax.swing.JComboBox<>();
        us = new javax.swing.JTextField();
        mail = new javax.swing.JTextField();
        ln = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        pw = new javax.swing.JPasswordField();
        fn = new javax.swing.JTextField();
        sq = new javax.swing.JComboBox<>();
        ans = new javax.swing.JTextField();
        check = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        jRadioButton1.setText("jRadioButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(148, 22, 22));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(51, 51, 255)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("First Name");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 90, 10));

        jRegister.setBackground(new java.awt.Color(0, 0, 204));
        jRegister.setFont(new java.awt.Font("Arial Black", 0, 16)); // NOI18N
        jRegister.setText("REGISTER");
        jRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRegisterMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jRegisterMouseExited(evt);
            }
        });
        jRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRegisterActionPerformed(evt);
            }
        });
        jPanel2.add(jRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 190, 30));

        ut.setBackground(new java.awt.Color(204, 204, 204));
        ut.setForeground(new java.awt.Color(255, 255, 255));
        ut.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "User", "Admin", "", "" }));
        ut.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        ut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                utActionPerformed(evt);
            }
        });
        jPanel2.add(ut, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 150, 140, 30));

        us.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 1, new java.awt.Color(0, 0, 153)));
        jPanel2.add(us, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 140, 30));

        mail.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 1, new java.awt.Color(0, 0, 153)));
        jPanel2.add(mail, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 140, 30));

        ln.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 1, new java.awt.Color(0, 0, 153)));
        jPanel2.add(ln, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 140, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("Email");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 30, 10));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("Username");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, 10));

        jLabel7.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("User Type");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, -1, 10));

        jLabel10.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText("Password");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, 60, 10));

        jButton1.setBackground(new java.awt.Color(0, 0, 204));
        jButton1.setFont(new java.awt.Font("Arial Black", 0, 16)); // NOI18N
        jButton1.setText("CANCEL");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton1MouseExited(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 300, 230, 30));

        jLabel15.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("Last Name");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, 110, 10));

        pw.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 1, new java.awt.Color(0, 0, 153)));
        pw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pwActionPerformed(evt);
            }
        });
        jPanel2.add(pw, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 120, 30));

        fn.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 2, 1, new java.awt.Color(0, 0, 153)));
        jPanel2.add(fn, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 140, 30));

        sq.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "What is the name of your first pet?", "What is the lastname of your Mother?", "What is your favorite food?", "What is your favorite Color?", " " }));
        jPanel2.add(sq, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, 270, -1));

        ans.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        ans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ansActionPerformed(evt);
            }
        });
        jPanel2.add(ans, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 220, 220, 30));

        check.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkActionPerformed(evt);
            }
        });
        jPanel2.add(check, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 360, 340));

        jLabel1.setFont(new java.awt.Font("Arial Black", 1, 17)); // NOI18N
        jLabel1.setText("REGISTRATION FORM");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 50));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/register.png"))); // NOI18N
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 90, 130, 150));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 610, 360));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void utActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_utActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_utActionPerformed

    private void jRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRegisterActionPerformed
         if(fn.getText().isEmpty() || ln.getText().isEmpty() || mail.getText().isEmpty() || us.getText().isEmpty() || 
       pw.getText().isEmpty() || ans.getText().isEmpty()) {  // Check if answer is empty
        JOptionPane.showMessageDialog(null, "All fields are required!");   
    } else if(pw.getText().length() < 8) {
        JOptionPane.showMessageDialog(null, "Password must be at least 8 characters.");
        pw.setText("");
    } else if(duplicateCheck()) {
        System.out.println("Duplicate Exists!");
    } else {
        dbConnector dbc = new dbConnector();
        try {
            String pass = passwordHasher.hashPassword(pw.getText()); // Hash password
            String secQuestion = sq.getSelectedItem().toString();
            String secAnswer = passwordHasher.hashPassword(ans.getText()); // 🔐 Hash security answer

            String query = "INSERT INTO tbl_users (u_fname, u_lname, u_email, u_username, u_password, security_question, security_answer, u_type, u_image, u_status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Null', 'Pending')";

            try (Connection con = dbc.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, fn.getText());
                stmt.setString(2, ln.getText());
                stmt.setString(3, mail.getText());
                stmt.setString(4, us.getText());
                stmt.setString(5, pass);           // Hashed password
                stmt.setString(6, secQuestion);
                stmt.setString(7, secAnswer);      // Hashed security answer
                stmt.setString(8, ut.getSelectedItem().toString());

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    // Get the generated user ID
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int userId = rs.getInt(1); // The auto-generated user ID
                            
                            // Log the event (registration)
                            logEvent(userId, us.getText(), "New user registered: " + us.getText());
                            
                            JOptionPane.showMessageDialog(null, "Registration Successful!");
                            loginForm lfr = new loginForm();
                            lfr.setVisible(true);
                            this.dispose();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Registration Failed. Try again!");      
                }
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    }//GEN-LAST:event_jRegisterActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        loginForm lgf = new loginForm();
        lgf.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        jButton1.setBackground(redd);
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseExited
        jButton1.setBackground(shok);
    }//GEN-LAST:event_jButton1MouseExited

    private void jRegisterMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRegisterMouseEntered
        jRegister.setBackground(redd);
    }//GEN-LAST:event_jRegisterMouseEntered

    private void jRegisterMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRegisterMouseExited
       jRegister.setBackground(shok);
    }//GEN-LAST:event_jRegisterMouseExited

    private void pwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pwActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pwActionPerformed

    private void checkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkActionPerformed

        boolean isSelected = check.isSelected();

        if (isSelected) {
            pw.setEchoChar((char)0);
        } else {
            pw.setEchoChar('*');
        }

    }//GEN-LAST:event_checkActionPerformed

    private void ansActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ansActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ansActionPerformed

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(regForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new regForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ans;
    private javax.swing.JCheckBox check;
    private javax.swing.JTextField fn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JButton jRegister;
    private javax.swing.JTextField ln;
    private javax.swing.JTextField mail;
    private javax.swing.JPasswordField pw;
    private javax.swing.JComboBox<String> sq;
    private javax.swing.JTextField us;
    private javax.swing.JComboBox<String> ut;
    // End of variables declaration//GEN-END:variables
}

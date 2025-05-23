
package admin;

import config.dbConnector;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import static Sarnss.regForm.dbemail;
import static Sarnss.regForm.dbusername;
import config.Session;
import config.passwordHasher;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author mypc
 */
public class CreateUserForm extends javax.swing.JFrame {

    /**
     * Creates new form AdminRegForm
     */
    public CreateUserForm() {
        initComponents();
    }
    
    Color shok = new Color(255,255,255);
    Color redd = new Color(0,0,153);
    
    public String destination = ""; 
    File selectedFile;
    public String oldpath;
    public String path;
    
    public boolean duplicateCheck(String username, String email) { 
    dbConnector dbc = new dbConnector();
    
    try {
        String query = "SELECT * FROM tbl_users WHERE u_username = '" + username + "' OR u_email = '" + email + "'";
        ResultSet resultSet = dbc.getData(query);
        
        if (resultSet.next()) {
            String existingEmail = resultSet.getString("u_email");
            if (existingEmail.equals(email)) {
                JOptionPane.showMessageDialog(null, "Email is already used!");
                return true;
            }

            String existingUsername = resultSet.getString("u_username");
            if (existingUsername.equals(username)) {
                JOptionPane.showMessageDialog(null, "Username is already used!");
                return true;
            }
        }
    } catch (SQLException ex) {
        System.out.println("" + ex);
    }
    
    return false; // No duplicate found
}

    public boolean UpdateCheck(){
        
    dbConnector dbc = new dbConnector();
        
     try{
            String query = "SELECT * FROM tbl_users  WHERE (u_username = '" +us.getText()+ "' OR u_email = '" +mail.getText()+ "') AND u_id != '"+uid.getText()+"'";
            ResultSet resultSet = dbc.getData(query);
           
            if(resultSet.next()){
                String email = resultSet.getString("u_email");
                if(mail.equals(mail.getText())){
                JOptionPane.showMessageDialog(null, "Email is already used!");
                mail.setText("");
                }
                String username = resultSet.getString("u_username");
                if(us.equals(us.getText())){
                JOptionPane.showMessageDialog(null, "Username is already used!");
                us.setText("");
                }
                return true;
        }else{
                
                return false;
     }
     }catch(SQLException ex){
         System.out.println(""+ex);
         return false;
     }
    }
     public static int getHeightFromWidth(String imagePath, int desiredWidth) {
        try {
            // Read the image file
            File imageFile = new File(imagePath);
            BufferedImage image = ImageIO.read(imageFile);
            
            // Get the original width and height of the image
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            
            // Calculate the new height based on the desired width and the aspect ratio
            int newHeight = (int) ((double) desiredWidth / originalWidth * originalHeight);
            
            return newHeight;
        } catch (IOException ex) {
            System.out.println("No image found!");
        }
        
        return -1;
    }    
    
       public  ImageIcon ResizeImage(String ImagePath, byte[] pic, JLabel label) {
        ImageIcon MyImage = null;
            if(ImagePath !=null){
                MyImage = new ImageIcon(ImagePath);
            }else{
                MyImage = new ImageIcon(pic);
            }

        int newHeight = getHeightFromWidth(ImagePath, label.getWidth());

        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(label.getWidth(), newHeight, Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
     public int FileExistenceChecker(String path){
        File file = new File(path);
        String fileName = file.getName();
        
        Path filePath = Paths.get("src/usersimages", fileName);
        boolean fileExists = Files.exists(filePath);
        
        if (fileExists) {
            return 1;
        } else {
            return 0;
        }
    
    }
    
        public void imageUpdater(String existingFilePath, String newFilePath){
        File existingFile = new File(existingFilePath);
        if (existingFile.exists()) {
            String parentDirectory = existingFile.getParent();
            File newFile = new File(newFilePath);
            String newFileName = newFile.getName();
            File updatedFile = new File(parentDirectory, newFileName);
            existingFile.delete();
            try {
                Files.copy(newFile.toPath(), updatedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image updated successfully.");
            } catch (IOException e) {
                System.out.println("Error occurred while updating the image: "+e);
            }
        } else {
            try{
                Files.copy(selectedFile.toPath(), new File(destination).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch(IOException e){
                System.out.println("Error on update!");
            }
        }
   }
    
     public void logEvent(int userId, String username, String action) 
    {
        dbConnector dbc = new dbConnector();
        Connection con = dbc.getConnection();
        PreparedStatement pstmt = null;
        Timestamp time = new Timestamp(new Date().getTime());

        try {
            String sql = "INSERT INTO tbl_log (u_id, u_username, login_time, log_status, log_description) "
                    + "VALUES ('" + userId + "', '" + username + "', '" + time + "', '" + action + "')";
            pstmt = con.prepareStatement(sql);

            /*            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
            pstmt.setString(4, userType);*/
            pstmt.executeUpdate();
            System.out.println("Login log recorded successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error recording log: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error closing resources: " + e.getMessage());
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

        jPanel10 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        rf = new javax.swing.JButton();
        pw = new javax.swing.JPasswordField();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cc = new javax.swing.JButton();
        us = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cl = new javax.swing.JButton();
        mail = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        ln = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        del = new javax.swing.JButton();
        fn = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        ut = new javax.swing.JComboBox<>();
        jPanel9 = new javax.swing.JPanel();
        stat = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        check = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        add = new javax.swing.JButton();
        uid = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        u_image = new javax.swing.JLabel();
        sq = new javax.swing.JComboBox<>();
        ans = new javax.swing.JTextField();
        select = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));
        jPanel7.setLayout(null);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Password");
        jPanel7.add(jLabel13);
        jLabel13.setBounds(140, 0, 70, 30);

        rf.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rf.setText("REFRESH");
        rf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rfActionPerformed(evt);
            }
        });
        jPanel7.add(rf);
        rf.setBounds(10, 0, 90, 30);

        pw.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        pw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pwActionPerformed(evt);
            }
        });
        jPanel7.add(pw);
        pw.setBounds(220, 0, 190, 30);

        jPanel10.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 340, 420, 30));

        jPanel6.setBackground(new java.awt.Color(204, 204, 204));
        jPanel6.setLayout(null);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Username");
        jPanel6.add(jLabel5);
        jLabel5.setBounds(140, 0, 59, 30);

        cc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cc.setText("CANCEL");
        cc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ccActionPerformed(evt);
            }
        });
        jPanel6.add(cc);
        cc.setBounds(10, 0, 90, 30);

        us.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        us.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        jPanel6.add(us);
        us.setBounds(220, 0, 190, 30);

        jPanel10.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 290, 420, 30));

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Email");
        jPanel5.add(jLabel4);
        jLabel4.setBounds(140, 0, 31, 30);

        cl.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cl.setText("CLEAR");
        cl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clActionPerformed(evt);
            }
        });
        jPanel5.add(cl);
        cl.setBounds(10, 0, 90, 30);

        mail.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mail.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        jPanel5.add(mail);
        mail.setBounds(220, 0, 190, 30);

        jPanel10.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, 420, 30));

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Last Name");
        jPanel4.add(jLabel3);
        jLabel3.setBounds(140, 0, 63, 30);

        ln.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ln.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        jPanel4.add(ln);
        ln.setBounds(220, 0, 190, 30);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton1.setText("UPDATE");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);
        jButton1.setBounds(10, 0, 90, 30);

        jPanel10.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 190, 420, 30));

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("First Name");
        jPanel3.add(jLabel10);
        jLabel10.setBounds(140, 0, 63, 30);

        del.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        del.setText("DELETE");
        del.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delActionPerformed(evt);
            }
        });
        jPanel3.add(del);
        del.setBounds(10, 0, 90, 30);

        fn.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fn.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        fn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fnActionPerformed(evt);
            }
        });
        jPanel3.add(fn);
        fn.setBounds(220, 0, 190, 30);

        jPanel10.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, 420, 30));

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));
        jPanel8.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("User Status");
        jPanel8.add(jLabel9);
        jLabel9.setBounds(0, 0, 90, 30);

        ut.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        ut.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "User", "Admin" }));
        jPanel8.add(ut);
        ut.setBounds(90, 0, 190, 30);

        jPanel10.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 480, 290, 30));

        jPanel9.setBackground(new java.awt.Color(204, 204, 204));
        jPanel9.setLayout(null);

        stat.setBackground(new java.awt.Color(204, 204, 204));
        stat.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        stat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Pending" }));
        jPanel9.add(stat);
        stat.setBounds(90, 0, 190, 30);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("User Type");
        jPanel9.add(jLabel8);
        jLabel8.setBounds(0, 0, 90, 30);

        jPanel10.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 520, 290, 30));

        check.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        check.setText("show");
        check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkActionPerformed(evt);
            }
        });
        jPanel10.add(check, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 340, -1, 30));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("User ID");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(140, 0, 44, 30);

        add.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        add.setText("ADD");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });
        jPanel2.add(add);
        add.setBounds(10, 0, 90, 30);

        uid.setBackground(new java.awt.Color(240, 240, 240));
        uid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        uid.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        uid.setEnabled(false);
        jPanel2.add(uid);
        uid.setBounds(220, 0, 190, 30);

        jPanel10.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, 420, 30));

        u_image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(u_image, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(u_image, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 60, 250, 210));

        sq.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "What is the name of your first pet?", "What is the lastname of your Mother?", "What is your favorite food?", "What is your favorite Color?", " " }));
        jPanel10.add(sq, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 390, 320, -1));

        ans.setEditable(false);
        ans.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 204)));
        jPanel10.add(ans, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 420, 320, 30));

        select.setBackground(new java.awt.Color(0, 51, 102));
        select.setText("SELECT");
        select.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectActionPerformed(evt);
            }
        });
        jPanel10.add(select, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 290, 80, 30));

        remove.setBackground(new java.awt.Color(0, 51, 102));
        remove.setText("REMOVE");
        remove.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        jPanel10.add(remove, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 290, 80, 30));

        getContentPane().add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 910, 590));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("USER");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, 110, 60));

        jLabel11.setFont(new java.awt.Font("Arial Black", 1, 24)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("CREAT USER");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 270, 34));
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 90));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 50));

        jPanel12.setBackground(new java.awt.Color(204, 204, 204));
        jPanel12.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 60, 600));

        jPanel13.setBackground(new java.awt.Color(204, 204, 204));
        jPanel13.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 0, 60, 640));

        jPanel14.setBackground(new java.awt.Color(204, 204, 204));
        jPanel14.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(0, 0, 153)));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("USER");
        jPanel14.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, 110, 60));
        jPanel14.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 90));

        getContentPane().add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 640, 1030, 50));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void rfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rfActionPerformed

    private void pwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pwActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pwActionPerformed

    private void ccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ccActionPerformed
        usersForm usf = new usersForm();
        usf.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_ccActionPerformed

    private void clActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clActionPerformed

    private void delActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_delActionPerformed

    private void fnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fnActionPerformed

    private void checkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkActionPerformed
        boolean isSelected = check.isSelected();

        if (isSelected) {
            pw.setEchoChar((char)0);
        } else {
            pw.setEchoChar('*');
        }
    }//GEN-LAST:event_checkActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        remove.setEnabled(false);
        select.setEnabled(true);
        u_image.setIcon(null);
        String destination = "";
        String path = "";
    }//GEN-LAST:event_removeActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String id = uid.getText().trim();
        String email = mail.getText().trim();
        String username = us.getText().trim();
        String password = pw.getText().trim();
        String firstName = fn.getText().trim();  // First name
        String lastName = ln.getText().trim();   // Last name
        String type = stat.getSelectedItem().toString();  // User type
        String statusValue = ut.getSelectedItem().toString(); // User status
        Session sess = Session.getInstance();

        // Validation
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: User ID is missing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: First Name and Last Name are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Invalid Email! Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Username validation
        if (!username.matches("[a-zA-Z0-9_]{5,}")) {
            JOptionPane.showMessageDialog(this, "Invalid Username! Must be at least 5 characters and contain only letters, numbers, and underscores.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            // Hash the password using SHA-256 before storing
            String hashedPassword = passwordHasher.hashPassword(password);

            dbConnector dbc = new dbConnector();
            String checkQuery = "SELECT COUNT(*) FROM tbl_users WHERE (u_username = ? OR u_email = ?) AND u_id != ?";

            try (Connection conn = dbc.getConnection();
                PreparedStatement pst = conn.prepareStatement(checkQuery)) {

                pst.setString(1, username);
                pst.setString(2, email);
                pst.setInt(3, Integer.parseInt(id));

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Username or Email already exists! Please use different credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // ✅ Corrected the column order in the UPDATE query
                String updateQuery = "UPDATE tbl_users SET u_fname = ?, u_lname = ?, u_username = ?, u_email = ?, u_password = ?, u_type = ?, u_status = ? WHERE u_id = ?";

                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/truck", "root", "");
                    PreparedStatement updatePst = con.prepareStatement(updateQuery)) {

                    String query = "SELECT * FROM tbl_users WHERE u_id = '"+sess.getUid()+"'";
                    ResultSet rs = dbc.getData(query);
                    if(rs.next())
                    {
                        String npass = rs.getString("u_password");

                        updatePst.setString(1, firstName);   // First name
                        updatePst.setString(2, lastName);    // Last name
                        updatePst.setString(3, username);    // Username
                        updatePst.setString(4, email);       // Email
                        updatePst.setString(5, npass);  // Store hashed password
                        updatePst.setString(6, statusValue); // Status (Corrected order)
                        updatePst.setString(7, type);        // Type (Corrected order)
                        updatePst.setInt(8, Integer.parseInt(id));  // User ID

                        int updated = updatePst.executeUpdate();
                        if (updated > 0) {
                            JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            new usersForm().setVisible(true);
                            this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (HeadlessException | NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CreateUserForm.class.getName()).log(Level.SEVERE, null, ex);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        dbConnector dbc = new dbConnector();
        Session sess = Session.getInstance();  // Logged-in admin

        String fname = fn.getText().trim();
        String lname = ln.getText().trim();
        String uname = us.getText().trim();
        String pass = pw.getText().trim();
        String email = mail.getText().trim();
        String status = ut.getSelectedItem().toString().trim();
        String type = stat.getSelectedItem().toString();
        String question = sq.getSelectedItem().toString();
        String answer = ans.getText().trim();

        // Use the class-level selectedFile
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(null, "Please select an image.");
            return;
        }

        String imageName = uname + "_" + selectedFile.getName();
        String destinationDir = "src/usersimages";
        new File(destinationDir).mkdirs();  // Ensure folder exists
        String destinationPath = destinationDir + "/" + imageName;

        // Validation
        if (fname.isEmpty() || lname.isEmpty() || uname.isEmpty() || pass.isEmpty() ||
            email.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all fields.");
            return;
        } else if (pass.length() < 8) {
            JOptionPane.showMessageDialog(null, "Password must be at least 8 characters.");
            return;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(null, "Enter a valid email address.");
            return;
        } else if (duplicateCheck(uname, email)) {
            return; // Already shown warning inside duplicateCheck
        }

        try {
            String hashedPassword = passwordHasher.hashPassword(pass);
            String hashedAnswer = passwordHasher.hashPassword(answer);

            // Save image to folder
            Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Insert user into DB
            String insertQuery = "INSERT INTO tbl_users (u_fname, u_lname, u_username, u_status, u_password, u_email, u_type, u_image, security_question, security_answer) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = dbc.getConnection();
                PreparedStatement pst = conn.prepareStatement(insertQuery)) {

                pst.setString(1, fname);
                pst.setString(2, lname);
                pst.setString(3, uname);
                pst.setString(4, status);
                pst.setString(5, hashedPassword);
                pst.setString(6, email);
                pst.setString(7, type);
                pst.setString(8, imageName);
                pst.setString(9, question);
                pst.setString(10, hashedAnswer);

                int rowsInserted = pst.executeUpdate();

                if (rowsInserted > 0) {
                    // Insert log entry
                    String logQuery = "INSERT INTO tbl_log (u_id, u_username, u_type, log_status, log_description) VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement logPst = conn.prepareStatement(logQuery)) {
                        logPst.setInt(1, sess.getUid());  // User ID of the admin performing the action
                        logPst.setString(2, sess.getUsername()); // Admin username
                        logPst.setString(3, sess.getType()); // Assuming you have this in session
                        logPst.setString(4, "Active"); // You can adjust this as needed
                        logPst.setString(5, "Admin Added a New Account: " + uname); // Description of the action
                        logPst.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(null, "Registered Successfully!");
                    new usersForm().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Registration Failed!");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addActionPerformed

    private void selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectActionPerformed
       JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Define destination path inside src/images/
            File destinationFolder = new File("src/images/");
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs(); // Create folder if it does not exist
            }

            File destinationFile = new File(destinationFolder, selectedFile.getName());

            try {
                // Copy file to the destination folder
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Store the image path
                String selectedImagePath = "src/images/" + selectedFile.getName();

                // Update the image label
                u_image.setIcon(new javax.swing.ImageIcon(destinationFile.getAbsolutePath()));

                JOptionPane.showMessageDialog(this, "Image uploaded successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error uploading image: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_selectActionPerformed

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
            java.util.logging.Logger.getLogger(CreateUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateUserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateUserForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton add;
    private javax.swing.JTextField ans;
    private javax.swing.JButton cc;
    private javax.swing.JCheckBox check;
    private javax.swing.JButton cl;
    private javax.swing.JButton del;
    public javax.swing.JTextField fn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    public javax.swing.JTextField ln;
    public javax.swing.JTextField mail;
    public javax.swing.JPasswordField pw;
    public javax.swing.JButton remove;
    private javax.swing.JButton rf;
    public javax.swing.JButton select;
    private javax.swing.JComboBox<String> sq;
    public javax.swing.JComboBox<String> stat;
    private javax.swing.JLabel u_image;
    public javax.swing.JTextField uid;
    public javax.swing.JTextField us;
    public javax.swing.JComboBox<String> ut;
    // End of variables declaration//GEN-END:variables

}

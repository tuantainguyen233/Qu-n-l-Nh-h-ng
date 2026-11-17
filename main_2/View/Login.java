
package View;
import Service.AuthService;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class Login extends javax.swing.JPanel {

    public Login() {
        initComponents();
        // Lưu ký tự che mật khẩu mặc định
    char kyTuChe = txtPassword.getEchoChar();

    // Nút hiện/ẩn mật khẩu
    jCheckBox1.addActionListener(e -> {
        if (jCheckBox1.isSelected()) {
            txtPassword.setEchoChar((char) 0);
        } else {
            txtPassword.setEchoChar(kyTuChe);
        }
    });

    // Nút đăng nhập
    btnLogin.addActionListener(e -> dangNhap());
    }
    private void dangNhap() {
    String tenDangNhap = txtName.getText().trim();
    String matKhau = new String(txtPassword.getPassword());

    // Kiểm tra dữ liệu trống
    if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!"
        );
        return;
    }

    // Gọi AuthService kiểm tra tài khoản
    AuthService.User user =
            AuthService.dangNhap(tenDangNhap, matKhau);

    // Nếu sai tài khoản hoặc mật khẩu
    if (user == null) {
        JOptionPane.showMessageDialog(
                this,
                "Tên đăng nhập hoặc mật khẩu không đúng!",
                "Đăng nhập thất bại",
                JOptionPane.ERROR_MESSAGE
        );

        txtPassword.setText("");
        txtPassword.requestFocus();
        return;
    }

    // Nếu đăng nhập thành công
    JOptionPane.showMessageDialog(
            this,
            "Xin chào " + user.hoTen + "!"
    );

    // Đóng cửa sổ đăng nhập
    java.awt.Window cuaSo =
            javax.swing.SwingUtilities.getWindowAncestor(this);

    if (cuaSo != null) {
        cuaSo.dispose();
    }

    // Mở giao diện chính
    MainFrame mainFrame = new MainFrame();
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        btnLogin = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();

        jTextField1.setText("jTextField1");

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 30)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QUẢN LÝ NHÀ HÀNG");

        jLabel2.setText("Tên đăng nhâp:");

        jLabel3.setText("Mật khẩu:");

        jCheckBox1.setText("Hiện mật khẩu");

        btnLogin.setBackground(new java.awt.Color(204, 255, 204));
        btnLogin.setText("Đăng nhập");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                        .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(55, 55, 55)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addGap(35, 35, 35)
                .addComponent(btnLogin)
                .addContainerGap(71, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Đăng Nhập Hệ Thống");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new Login());
            frame.pack();
            frame.setLocationRelativeTo(null); // Căn giữa màn hình
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
    
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}

package View;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainFrame extends javax.swing.JFrame {

    private CardLayout cardLayout;

    // 1. Khai báo 3 Panel của Thành viên 4 (Bạn)
    private BanHangPanel banHangPanel;           // Module 4.1
    private LichSuHoaDonPanel lichSuHoaDonPanel; // Module 4.2
    private ThongKePanel thongKePanel;           // Module 4.3

    // 2. Khai báo các Panel gốc của Thành viên 1, 2, 3
    private JPKV_BA banAnPanel;                  // Module 1.1 (TV1)
    // Module của TV2
    private MonAnPanel monAnPanel;
    private LoaiMonPanel loaiMonPanel;       // Thêm dòng này
    private TinhTrangPanel tinhTrangPanel;   // Thêm dòng này

    private NhanVienView nhanVienView;           // Module 3.1 (TV3)

    private KhuVucPanel khuVucPanel;
    private DatBanPanel datBanPanel;

    private CaLamView caLamView;

    public MainFrame() {
        initComponents();
        this.setTitle("Quản Lý Nhà Hàng - Hệ Thống 12 Modules");
        this.setSize(1320, 760);
        this.setMinimumSize(new java.awt.Dimension(1150, 680));
        this.setLocationRelativeTo(null); // Căn giữa màn hình

        caiDatHeThong();
    }

    private void caiDatHeThong() {
        // Lấy bộ layout CardLayout từ vùng chứa trung tâm pnlMainContent
        cardLayout = (CardLayout) pnlMainContent.getLayout();

        banHangPanel = new BanHangPanel();
        lichSuHoaDonPanel = new LichSuHoaDonPanel();
        thongKePanel = new ThongKePanel();
        banAnPanel = new JPKV_BA();
        nhanVienView = new NhanVienView();
        monAnPanel = new MonAnPanel();
        loaiMonPanel = new LoaiMonPanel();
        tinhTrangPanel = new TinhTrangPanel();
        khuVucPanel = new KhuVucPanel();
        datBanPanel = new DatBanPanel();
        caLamView = new CaLamView();

        banHangPanel.setPreferredSize(null);
        lichSuHoaDonPanel.setPreferredSize(null);
        thongKePanel.setPreferredSize(null);
        monAnPanel.setPreferredSize(null);
        loaiMonPanel.setPreferredSize(null);
        tinhTrangPanel.setPreferredSize(null);
        nhanVienView.setPreferredSize(null);
        khuVucPanel.setPreferredSize(null);
        caLamView.setPreferredSize(null);

        datBanPanel = new DatBanPanel();

        // Đăng ký 6 Panel đã có sẵn vào CardLayout
        pnlMainContent.add(banHangPanel, "CARD_1_BAN_HANG");
        pnlMainContent.add(lichSuHoaDonPanel, "CARD_2_LICH_SU");
        pnlMainContent.add(thongKePanel, "CARD_3_THONG_KE");
        pnlMainContent.add(banAnPanel, "CARD_4_BAN_AN");
        pnlMainContent.add(khuVucPanel, "CARD_5_KHU_VUC");
        pnlMainContent.add(datBanPanel, "CARD_6_DAT_BAN");
        pnlMainContent.add(monAnPanel, "CARD_7_MON_AN");
        pnlMainContent.add(loaiMonPanel, "CARD_8_LOAI_MON");
        pnlMainContent.add(tinhTrangPanel, "CARD_9_TINH_TRANG_MON");
        pnlMainContent.add(nhanVienView, "CARD_10_NHAN_VIEN");
        pnlMainContent.add(caLamView, "CARD_11_CHIA_CA");

        chuanHoaThanhMenu();
        hienThiTab("CARD_1_BAN_HANG", btnBanHang);

        // Hiển thị thông tin tài khoản đang đăng nhập (admin hoặc nhanvien)
        Service.AuthService.User user = Service.AuthService.getCurrentUser();

        if (user != null) {
            this.setTitle("Quản Lý Nhà Hàng - Xin chào: "
                    + user.hoTen + " [" + user.vaiTro + "]");

            jLabel1.setText(
                    "<html><center>NHÀ HÀNG POS<br>"
                    + "<font size='2' color='#D0E8D8'>Xin chào: "
                    + user.hoTen
                    + "</font></center></html>"
            );

            // Kiểm tra quyền đăng nhập
            String vaiTro = user.vaiTro == null
                    ? ""
                    : user.vaiTro.trim().toLowerCase();

            boolean laQuanLy = vaiTro.equals("admin")
                    || vaiTro.equals("quản lý")
                    || vaiTro.equals("quan ly")
                    || vaiTro.equals("manager");

            // Nhân viên không được hiển thị hai chức năng này
            if (!laQuanLy) {
                pnlSidebar.remove(btnNhanVien);
                pnlSidebar.remove(btnChiaCa);

                // Tự động sắp xếp lại menu, không để khoảng trống
                pnlSidebar.setLayout(new java.awt.GridLayout(0, 1, 0, 6));

                pnlSidebar.revalidate();
                pnlSidebar.repaint();
            }
        }
    }

    private JButton[] layDanhSach12NutMenu() {
        return new JButton[]{
            btnBanHang, btnLichSuHD, btnThongKe,
            btnBanAn, btnKhuVuc, btnDatBan,
            btnMonAn, btnLoaiMon, btnTinhTrangMon,
            btnNhanVien, btnChiaCa};
    }

    private void chuanHoaThanhMenu() {
        pnlSidebar.setBackground(new Color(36, 92, 59));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);

        for (JButton btn : layDanhSach12NutMenu()) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }

        // Nút Đăng xuất màu đỏ nổi bật ở dưới cùng
        btnDangXuat.setBackground(new Color(220, 53, 69));
        btnDangXuat.setForeground(Color.WHITE);
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDangXuat.setFocusPainted(false);
    }

    private void hienThiTab(String cardName, JButton nutHienTai) {
        cardLayout.show(pnlMainContent, cardName);

        for (JButton b : layDanhSach12NutMenu()) {
            b.setBackground(Color.WHITE);
            b.setForeground(new Color(40, 40, 40));
            b.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        }

        // Nút đang chọn có nền xanh nhạt và viền vàng giống hình kế hoạch
        nutHienTai.setBackground(new Color(220, 245, 230));
        nutHienTai.setForeground(new Color(36, 92, 59));
        nutHienTai.setBorder(BorderFactory.createLineBorder(new Color(240, 200, 0), 2));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSidebar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnBanHang = new javax.swing.JButton();
        btnLichSuHD = new javax.swing.JButton();
        btnThongKe = new javax.swing.JButton();
        btnBanAn = new javax.swing.JButton();
        btnKhuVuc = new javax.swing.JButton();
        btnDatBan = new javax.swing.JButton();
        btnMonAn = new javax.swing.JButton();
        btnLoaiMon = new javax.swing.JButton();
        btnTinhTrangMon = new javax.swing.JButton();
        btnNhanVien = new javax.swing.JButton();
        btnChiaCa = new javax.swing.JButton();
        btnDangXuat = new javax.swing.JButton();
        pnlMainContent = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlSidebar.setBackground(new java.awt.Color(36, 92, 59));
        pnlSidebar.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(220, 750));
        pnlSidebar.setLayout(new java.awt.GridLayout(13, 1, 0, 6));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NHÀ HÀNG POS");
        pnlSidebar.add(jLabel1);

        btnBanHang.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBanHang.setText("1. Bán Hàng (POS)");
        btnBanHang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBanHang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBanHang.addActionListener(this::btnBanHangActionPerformed);
        pnlSidebar.add(btnBanHang);

        btnLichSuHD.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLichSuHD.setText("2. Lịch Sử & In HĐ");
        btnLichSuHD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLichSuHD.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLichSuHD.addActionListener(this::btnLichSuHDActionPerformed);
        pnlSidebar.add(btnLichSuHD);

        btnThongKe.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnThongKe.setText("3. Thống Kê Doanh Thu");
        btnThongKe.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThongKe.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnThongKe.addActionListener(this::btnThongKeActionPerformed);
        pnlSidebar.add(btnThongKe);

        btnBanAn.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBanAn.setText("4. Sơ Đồ Bàn Ăn");
        btnBanAn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBanAn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBanAn.addActionListener(this::btnBanAnActionPerformed);
        pnlSidebar.add(btnBanAn);

        btnKhuVuc.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnKhuVuc.setText("5. Quản Lý Khu Vực");
        btnKhuVuc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnKhuVuc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnKhuVuc.addActionListener(this::btnKhuVucActionPerformed);
        pnlSidebar.add(btnKhuVuc);

        btnDatBan.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDatBan.setText("6. Đặt & Chuyển Bàn");
        btnDatBan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDatBan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDatBan.addActionListener(this::btnDatBanActionPerformed);
        pnlSidebar.add(btnDatBan);

        btnMonAn.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnMonAn.setText("7. Quản Lý Món Ăn");
        btnMonAn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMonAn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMonAn.addActionListener(this::btnMonAnActionPerformed);
        pnlSidebar.add(btnMonAn);

        btnLoaiMon.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLoaiMon.setText("8. Quản Lý Loại Món");
        btnLoaiMon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLoaiMon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLoaiMon.addActionListener(this::btnLoaiMonActionPerformed);
        pnlSidebar.add(btnLoaiMon);

        btnTinhTrangMon.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnTinhTrangMon.setText("9. Báo Hết Món Nhanh");
        btnTinhTrangMon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTinhTrangMon.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTinhTrangMon.addActionListener(this::btnTinhTrangMonActionPerformed);
        pnlSidebar.add(btnTinhTrangMon);

        btnNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNhanVien.setText("10. Hồ Sơ Nhân Viên");
        btnNhanVien.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNhanVien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNhanVien.addActionListener(this::btnNhanVienActionPerformed);
        pnlSidebar.add(btnNhanVien);

        btnChiaCa.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnChiaCa.setText("11. Chia Ca Làm Việc");
        btnChiaCa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChiaCa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnChiaCa.addActionListener(this::btnChiaCaActionPerformed);
        pnlSidebar.add(btnChiaCa);

        btnDangXuat.setBackground(new java.awt.Color(220, 53, 69));
        btnDangXuat.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDangXuat.setForeground(new java.awt.Color(255, 255, 255));
        btnDangXuat.setText("ĐĂNG XUẤT HỆ THỐNG");
        btnDangXuat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDangXuat.addActionListener(this::btnDangXuatActionPerformed);
        pnlSidebar.add(btnDangXuat);

        getContentPane().add(pnlSidebar, java.awt.BorderLayout.LINE_START);

        pnlMainContent.setLayout(new java.awt.CardLayout());
        getContentPane().add(pnlMainContent, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBanHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBanHangActionPerformed
        banHangPanel.reloadData();
        hienThiTab("CARD_1_BAN_HANG", btnBanHang);
    }//GEN-LAST:event_btnBanHangActionPerformed

    private void btnLichSuHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLichSuHDActionPerformed
        lichSuHoaDonPanel.reloadData();
        hienThiTab("CARD_2_LICH_SU", btnLichSuHD);
    }//GEN-LAST:event_btnLichSuHDActionPerformed

    private void btnThongKeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThongKeActionPerformed
        thongKePanel.reloadData();
        hienThiTab("CARD_3_THONG_KE", btnThongKe);
    }//GEN-LAST:event_btnThongKeActionPerformed

    private void btnBanAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBanAnActionPerformed
        hienThiTab("CARD_4_BAN_AN", btnBanAn);
    }//GEN-LAST:event_btnBanAnActionPerformed

    private void btnKhuVucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhuVucActionPerformed
        hienThiTab("CARD_5_KHU_VUC", btnKhuVuc);
    }//GEN-LAST:event_btnKhuVucActionPerformed

    private void btnDatBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatBanActionPerformed
        hienThiTab("CARD_6_DAT_BAN", btnDatBan);
    }//GEN-LAST:event_btnDatBanActionPerformed

    private void btnMonAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonAnActionPerformed
        hienThiTab("CARD_7_MON_AN", btnMonAn);
    }//GEN-LAST:event_btnMonAnActionPerformed

    private void btnLoaiMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoaiMonActionPerformed
        hienThiTab("CARD_8_LOAI_MON", btnLoaiMon);
    }//GEN-LAST:event_btnLoaiMonActionPerformed

    private void btnTinhTrangMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTinhTrangMonActionPerformed
        hienThiTab("CARD_9_TINH_TRANG_MON", btnTinhTrangMon);
    }//GEN-LAST:event_btnTinhTrangMonActionPerformed

    private void btnNhanVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhanVienActionPerformed
        hienThiTab("CARD_10_NHAN_VIEN", btnNhanVien);
    }//GEN-LAST:event_btnNhanVienActionPerformed

    private void btnChiaCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChiaCaActionPerformed
        hienThiTab("CARD_11_CHIA_CA", btnChiaCa);
    }//GEN-LAST:event_btnChiaCaActionPerformed

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        int chon = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác Nhận Đăng Xuất",
                JOptionPane.YES_NO_OPTION
        );
        if (chon == JOptionPane.YES_OPTION) {
            Service.AuthService.dangXuat();
            this.dispose();

            JFrame loginFrame = new JFrame("Đăng Nhập Hệ Thống");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setContentPane(new Login());
            loginFrame.pack();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setResizable(false);
            loginFrame.setVisible(true);
        }
    }//GEN-LAST:event_btnDangXuatActionPerformed

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBanAn;
    private javax.swing.JButton btnBanHang;
    private javax.swing.JButton btnChiaCa;
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnDatBan;
    private javax.swing.JButton btnKhuVuc;
    private javax.swing.JButton btnLichSuHD;
    private javax.swing.JButton btnLoaiMon;
    private javax.swing.JButton btnMonAn;
    private javax.swing.JButton btnNhanVien;
    private javax.swing.JButton btnThongKe;
    private javax.swing.JButton btnTinhTrangMon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel pnlMainContent;
    private javax.swing.JPanel pnlSidebar;
    // End of variables declaration//GEN-END:variables
}

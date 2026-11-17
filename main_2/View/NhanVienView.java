
package View;

import DAO.NhanVienDao;
import Entity.NhanVien;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;


public class NhanVienView extends javax.swing.JPanel {

        
private final NhanVienDao dao = new NhanVienDao();

public NhanVienView() {
    initComponents();

    // Đặt giá trị cho hai ComboBox
    jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"Nam", "Nữ", "Khác"}
    ));

    jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"Đang làm", "Nghỉ việc"}
    ));

    // Đặt tên các cột của bảng
    jTable1.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{
                "Mã NV", "Họ tên", "Ngày sinh", "Giới tính",
                "Điện thoại", "Email", "Trạng thái"
            }
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    });

    // Gắn sự kiện cho các nút
    btnAdd.addActionListener(e -> themNhanVien());
    btnUpdate.addActionListener(e -> suaNhanVien());
    btnDelete.addActionListener(e -> xoaNhanVien());
    btnReset.addActionListener(e -> lamMoi());
    btnSearch.addActionListener(e -> timNhanVien());
    btnShowall.addActionListener(e -> {
        txtSearch.setText("");
        loadTable();
    });
    //tạo dòng kẻ
    jTable1.setShowGrid(true);
    jTable1.setShowHorizontalLines(true);
    jTable1.setShowVerticalLines(true);
    jTable1.setGridColor(java.awt.Color.LIGHT_GRAY);
    jTable1.setIntercellSpacing(new java.awt.Dimension(1, 1));
    jTable1.setRowHeight(25);
    // Màu nền tiêu đề bảng: xanh lá cây
    jTable1.getTableHeader().setBackground(new java.awt.Color(0, 102, 51));

    // Màu chữ tiêu đề: trắng
    jTable1.getTableHeader().setForeground(java.awt.Color.WHITE);

    // Chữ tiêu đề in đậm
    jTable1.getTableHeader().setFont(
    new java.awt.Font("Arial", java.awt.Font.BOLD, 13)
    );

    // Hiển thị màu nền tiêu đề
    jTable1.getTableHeader().setOpaque(true);

    // Chọn dòng trên bảng để hiện thông tin
    jTable1.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            hienThiLenForm();
        }
    });

    loadTable();
}

    // Lấy dữ liệu từ form và kiểm tra
    private NhanVien layDuLieuForm() {
        String ma = txtManv.getText().trim();
        String ten = txtTen.getText().trim();
        String ngay = txtNgaysinh.getText().trim();
        String sdt = txtDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String chucVu = txtChucvu.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || ngay.isEmpty()
                || sdt.isEmpty() || chucVu.isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập đầy đủ thông tin bắt buộc!");
        }

        int maNV;
        try {
            maNV = Integer.parseInt(ma);
            if (maNV <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Mã nhân viên phải là số nguyên dương!");
        }

        if (!sdt.matches("0[0-9]{9}")) {
            throw new IllegalArgumentException(
                    "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!");
        }

        if (!email.isEmpty()
                && !email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Email không đúng định dạng!");
        }

        LocalDate ngaySinh;
        try {
            ngaySinh = LocalDate.parse(ngay);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Ngày sinh nhập theo dạng yyyy-MM-dd, ví dụ 2000-03-15!");
        }

        if (!ngaySinh.isBefore(LocalDate.now())
                || ngaySinh.plusYears(16).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Nhân viên phải đủ 16 tuổi!");
        }

        return new NhanVien(
                maNV,
                ten,
                Date.valueOf(ngaySinh),
                jComboBox1.getSelectedItem().toString(),
                sdt,
                email.isEmpty() ? null : email,
                chucVu,
                jComboBox2.getSelectedItem().toString()
        );
    }
    

    // Nút Thêm
    private void themNhanVien() {
        try {
            NhanVien nv = layDuLieuForm();

            if (dao.insert(nv)) {
                JOptionPane.showMessageDialog(this,
                        "Thêm nhân viên thành công!");
                loadTable();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm thất bại! Kiểm tra mã NV, SĐT hoặc email trùng.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Nút Sửa
    private void suaNhanVien() {
        if (jTable1.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Hãy chọn nhân viên cần sửa trong bảng!");
            return;
        }

        try {
            NhanVien nv = layDuLieuForm();

            if (dao.update(nv)) {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật nhân viên thành công!");
                loadTable();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thất bại!");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Nút Xóa
    private void xoaNhanVien() {
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Hãy chọn nhân viên cần xóa!");
            return;
        }

        String maNV = jTable1.getValueAt(row, 0).toString();

        int chon = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa nhân viên mã " + maNV + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (chon == JOptionPane.YES_OPTION) {
            if (dao.delete(maNV)) {
                JOptionPane.showMessageDialog(this,
                        "Xóa nhân viên thành công!");
                loadTable();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không xóa được nhân viên! Có thể nhân viên "
                        + "đã được dùng trong hóa đơn.");
            }
        }
    }

    // Nút Làm mới
    private void lamMoi() {
        txtManv.setText("");
        txtTen.setText("");
        txtNgaysinh.setText("");
        txtDienThoai.setText("");
        txtEmail.setText("");
        txtChucvu.setText("");
        txtSearch.setText("");

        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);

        jTable1.clearSelection();
        txtManv.requestFocus();
    }

    // Nút Tìm kiếm
    private void timNhanVien() {
        List<NhanVien> list = dao.search(txtSearch.getText().trim());
        hienThiDanhSach(list);

    }
    // Hiển thị tất cả nhân viên lên bảng
    private void loadTable() {
        hienThiDanhSach(dao.getAll());
    }

    private void hienThiDanhSach(List<NhanVien> list) {
        DefaultTableModel model =
                (DefaultTableModel) jTable1.getModel();

        model.setRowCount(0);

        for (NhanVien nv : list) {
            model.addRow(new Object[]{
                nv.getMaNV(),
                nv.getHoTen(),
                nv.getNgaySinh(),
                nv.getGioiTinh(),
                nv.getDienThoai(),
                nv.getEmail() == null ? "" : nv.getEmail(),
               
                nv.getTrangThai()
            });
        }
    }

    // Bấm vào một dòng thì đưa thông tin lên form
    private void hienThiLenForm() {
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            return;
        }

        txtManv.setText(jTable1.getValueAt(row, 0).toString());
        txtTen.setText(jTable1.getValueAt(row, 1).toString());
        txtNgaysinh.setText(jTable1.getValueAt(row, 2).toString());
        jComboBox1.setSelectedItem(
                jTable1.getValueAt(row, 3).toString());
        txtDienThoai.setText(
                jTable1.getValueAt(row, 4).toString());

        Object email = jTable1.getValueAt(row, 5);
        txtEmail.setText(email == null ? "" : email.toString());

        txtChucvu.setText(jTable1.getValueAt(row, 6).toString());
        jComboBox2.setSelectedItem(
                jTable1.getValueAt(row, 7).toString());
    }

    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtManv = new javax.swing.JTextField();
        txtNgaysinh = new javax.swing.JTextField();
        txtDienThoai = new javax.swing.JTextField();
        txtChucvu = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtTen = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        txtEmail = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox<>();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnShowall = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 25)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QUẢN LÝ NHÂN VIÊN");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin nhân viên\n"));

        jLabel2.setText("Mã nhân viên:");

        jLabel3.setText("Ngày sinh:");

        jLabel4.setText("Điện thoại:");

        jLabel5.setText("Chức vụ:");

        txtNgaysinh.addActionListener(this::txtNgaysinhActionPerformed);

        jLabel6.setText("Họ tên:");

        jLabel7.setText("Giới tính:");

        jLabel8.setText("Email:");

        jLabel9.setText("Trạng thái:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnAdd.setBackground(new java.awt.Color(0, 102, 51));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Thêm");

        btnUpdate.setBackground(new java.awt.Color(0, 102, 51));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Sửa");

        btnDelete.setBackground(new java.awt.Color(0, 102, 51));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Xoá");

        btnReset.setBackground(new java.awt.Color(0, 102, 51));
        btnReset.setForeground(new java.awt.Color(255, 255, 255));
        btnReset.setText("Làm mới");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtDienThoai, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(txtNgaysinh, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtManv, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtChucvu))
                .addGap(55, 55, 55)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jComboBox2, 0, 219, Short.MAX_VALUE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTen, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(btnAdd)
                .addGap(51, 51, 51)
                .addComponent(btnUpdate)
                .addGap(56, 56, 56)
                .addComponent(btnDelete)
                .addGap(54, 54, 54)
                .addComponent(btnReset)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtManv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel13)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtChucvu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel10.setText("Tìm mã,tên hoặc sđt:");

        btnSearch.setBackground(new java.awt.Color(0, 102, 51));
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Tìm kiếm");

        btnShowall.setBackground(new java.awt.Color(0, 102, 51));
        btnShowall.setForeground(new java.awt.Color(255, 255, 255));
        btnShowall.setText("Hiện tất cả");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(btnSearch)
                .addGap(40, 40, 40)
                .addComponent(btnShowall)
                .addGap(44, 44, 44))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(14, 14, 14))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnShowall))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtNgaysinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNgaysinhActionPerformed
       
    }//GEN-LAST:event_txtNgaysinhActionPerformed
public static void main(String[] args) {
    javax.swing.JFrame frame = new javax.swing.JFrame("Quản lý nhân viên");

    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(new NhanVienView());
    frame.setSize(1200, 850);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnShowall;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtChucvu;
    private javax.swing.JTextField txtDienThoai;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtManv;
    private javax.swing.JTextField txtNgaysinh;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTen;
    // End of variables declaration//GEN-END:variables
}

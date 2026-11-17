
package View;

import DAO.PhanCaDao;
import Entity.PhanCa;
import CONN.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
public class CaLamView extends javax.swing.JPanel {
    private final PhanCaDao phanCaDao = new PhanCaDao();

// Lưu mã phân ca của dòng đang chọn để sửa hoặc xóa
private int maPhanCaDangChon = -1;

// Lớp dùng để hiển thị tên nhưng vẫn giữ mã tương ứng
private static class ComboItem {
    private final int id;
    private final String ten;

    public ComboItem(int id, String ten) {
        this.id = id;
        this.ten = ten;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + " - " + ten;
    }
}
    public CaLamView() {
        initComponents();
        chinhBoCucKhiNhung();
        // Cấu hình các ô chọn và bảng
    khoiTaoGiaoDien();

    // Tải dữ liệu từ SQL Server
    taiDanhSachNhanVien();
    taiDanhSachKhuVuc();
    taiDanhSachPhanCa();

    // Chọn một dòng trong bảng để sửa hoặc xóa
    jTable1.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            hienThiDuLieuLenForm();
        }
    });
    }
    
    // Cấu hình giao diện
    private void khoiTaoGiaoDien() {
        cboCaLam.setModel(new DefaultComboBoxModel<>(
    new String[]{
        "Ca sáng (07:00 - 11:00)",
        "Ca chiều (11:00 - 16:00)",
        "Ca tối (16:00 - 22:00)"
    }
));

        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{
                "Mã NV",
                "Tên NV",
                "Ca làm",
                "Ngày làm",
                "Khu vực"
            }
        ) 
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(model);
        jTable1.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION
        );
        // Hiển thị đường kẻ ngang và dọc trong bảng
jTable1.setShowHorizontalLines(true);
jTable1.setShowVerticalLines(true);

// Màu đường kẻ
jTable1.setGridColor(new java.awt.Color(200, 200, 200));

// Khoảng cách giữa các ô
jTable1.setIntercellSpacing(new java.awt.Dimension(1, 1));

// Chiều cao mỗi hàng
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
        txtDate.setToolTipText("Nhập ngày theo định dạng yyyy-MM-dd");
    }

    // Lấy mã ở đầu chuỗi: "1 - Nguyễn Văn An" -> 1
    private int layMaTuCombo(JComboBox<String> combo) {
        Object selected = combo.getSelectedItem();

        if (selected == null) {
            throw new IllegalArgumentException("Bạn chưa chọn dữ liệu.");
        }

        String text = selected.toString();
        int viTri = text.indexOf(" - ");

        if (viTri < 0) {
            throw new IllegalArgumentException("Dữ liệu lựa chọn không hợp lệ.");
        }

        return Integer.parseInt(text.substring(0, viTri));

    }

    // Tải nhân viên đang làm
    private void taiDanhSachNhanVien() {
        DefaultComboBoxModel<String> model =
                new DefaultComboBoxModel<>();

        String sql = """
            SELECT MaNV, HoTen
            FROM NHANVIEN
            WHERE TrangThai = N'Đang làm'
            ORDER BY MaNV
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addElement(
                    rs.getInt("MaNV") + " - " + rs.getString("HoTen")
                );
            }

            cboNhanVien.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, "Không tải được danh sách nhân viên!"
            );
        }
    }

    // Tải khu vực: lưu cả mã và tên trong chuỗi lựa chọn
    private void taiDanhSachKhuVuc() {
        DefaultComboBoxModel<String> model =
                new DefaultComboBoxModel<>();

        String sql = """
            SELECT MaKhuVuc, TenKhuVuc
            FROM KHUVUC
            ORDER BY MaKhuVuc
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addElement(
                    rs.getInt("MaKhuVuc") + " - "
                    + rs.getString("TenKhuVuc")
                );
            }

            cboKhuVuc.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, "Không tải được danh sách khu vực!"
            );
        }
    }

    // Tải danh sách phân ca vào bảng
    private void taiDanhSachPhanCa() {
        hienThiDanhSach(phanCaDao.getAll());
    }

    private void hienThiDanhSach(List<PhanCa> danhSach) {
        DefaultTableModel model =
                (DefaultTableModel) jTable1.getModel();

        model.setRowCount(0);

        for (PhanCa pc : danhSach) {
            model.addRow(new Object[]{
                pc.getMaNV(),
                pc.getTenNV(),
                pc.getCaLam(),
                pc.getNgayLam(),
                pc.getTenKhuVuc()
            });
        }

        maPhanCaDangChon = -1;
    }

    // Kiểm tra và lấy ngày theo định dạng yyyy-MM-dd
    private Date layNgayLam() {
        try {
            LocalDate ngay = LocalDate.parse(txtDate.getText().trim());
            return Date.valueOf(ngay);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Ngày làm không hợp lệ. Hãy nhập dạng yyyy-MM-dd, ví dụ 2026-10-28."
            );
        }
    }

    // Tạo đối tượng phân ca từ dữ liệu trên form
    private PhanCa layDuLieuForm() {
        if (cboNhanVien.getSelectedItem() == null
                || cboCaLam.getSelectedItem() == null
                || cboKhuVuc.getSelectedItem() == null) {
            throw new IllegalArgumentException(
                "Vui lòng chọn nhân viên, ca làm và khu vực."
            );
        }

        int maNV = layMaTuCombo(cboNhanVien);
        int maKhuVuc = layMaTuCombo(cboKhuVuc);

        String caLam = cboCaLam.getSelectedItem().toString();
        Date ngayLam = layNgayLam();

        return new PhanCa(maNV, caLam, ngayLam, maKhuVuc);
    }

    // Thêm phân ca
    private void themPhanCa() {
        try {
            PhanCa pc = layDuLieuForm();

            if (phanCaDao.insert(pc)) {
                JOptionPane.showMessageDialog(this, "Thêm phân ca thành công!");
                taiDanhSachPhanCa();
                xoaTrangForm();
            } else {
                JOptionPane.showMessageDialog(
                    this, "Thêm phân ca thất bại! Hãy kiểm tra kết nối SQL."
                );
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Sửa phân ca đang chọn
    private void suaPhanCa() {
        if (maPhanCaDangChon == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn một dòng trong bảng để sửa!"
            );
            return;
        }

        try {
            PhanCa pc = layDuLieuForm();
            pc.setMaPhanCa(maPhanCaDangChon);

            if (phanCaDao.update(pc)) {
                JOptionPane.showMessageDialog(this, "Sửa phân ca thành công!");
                taiDanhSachPhanCa();
                xoaTrangForm();
            } else {
                JOptionPane.showMessageDialog(this, "Sửa phân ca thất bại!");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Xóa phân ca đang chọn
    private void xoaPhanCa() {
        if (maPhanCaDangChon == -1) {
            JOptionPane.showMessageDialog(
                this, "Vui lòng chọn một dòng trong bảng để xóa!"
            );
            return;
        }

        int chon = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn xóa phân ca này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );

        if (chon != JOptionPane.YES_OPTION) {
            return;
        }

        if (phanCaDao.delete(maPhanCaDangChon)) {
            JOptionPane.showMessageDialog(this, "Xóa phân ca thành công!");
            taiDanhSachPhanCa();
            xoaTrangForm();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa phân ca thất bại!");
        }
    }

    // Tìm kiếm theo mã nhân viên
    private void timKiemPhanCa() {
        String text = txtSeach.getText().trim();

        if (text.isEmpty()) {
            taiDanhSachPhanCa();
            return;
        }

        try {
            int maNV = Integer.parseInt(text);
            hienThiDanhSach(phanCaDao.findByMaNV(maNV));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, "Mã nhân viên phải là số nguyên!"
            );
        }
    }

    // Đưa dữ liệu của dòng được chọn lên form
    private void hienThiDuLieuLenForm() {
        int row = jTable1.getSelectedRow();

        if (row < 0) {
            return;
        }

        // Lấy lại mã phân ca từ danh sách đầy đủ
        List<PhanCa> danhSach = phanCaDao.getAll();

        if (row >= jTable1.getRowCount()) {
            return;
        }

        Object maNVCell = jTable1.getValueAt(row, 0);
        Object caCell = jTable1.getValueAt(row, 2);
        Object ngayCell = jTable1.getValueAt(row, 3);
        Object khuVucCell = jTable1.getValueAt(row, 4);

        if (maNVCell == null || ngayCell == null) {
            return;
        }

        int maNV = Integer.parseInt(maNVCell.toString());
        String caLam = caCell.toString();
        String ngayLam = ngayCell.toString();
        String tenKhuVuc = khuVucCell.toString();

        // Tìm mã phân ca tương ứng với dòng đang hiển thị.
        // Khi tìm kiếm, danh sách trong bảng có thể chỉ là một phần.
        maPhanCaDangChon = -1;

        for (PhanCa pc : danhSach) {
            if (pc.getMaNV() == maNV
                    && pc.getCaLam().equals(caLam)
                    && pc.getNgayLam().toString().equals(ngayLam)
                    && pc.getTenKhuVuc().equals(tenKhuVuc)) {
                maPhanCaDangChon = pc.getMaPhanCa();
                break;
            }
        }

        for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
            String item = cboNhanVien.getItemAt(i);
            if (item.startsWith(maNV + " - ")) {
                cboNhanVien.setSelectedIndex(i);
                break;
            }
        }

        cboCaLam.setSelectedItem(caLam);
        txtDate.setText(ngayLam);

        for (int i = 0; i < cboKhuVuc.getItemCount(); i++) {
            String item = cboKhuVuc.getItemAt(i);
            if (item.endsWith(" - " + tenKhuVuc)) {
                cboKhuVuc.setSelectedIndex(i);
                break;
            }
        }
    }

    // Xóa trắng form sau thao tác
    private void xoaTrangForm() {
        maPhanCaDangChon = -1;

        if (cboNhanVien.getItemCount() > 0) {
            cboNhanVien.setSelectedIndex(0);
        }

        if (cboCaLam.getItemCount() > 0) {
            cboCaLam.setSelectedIndex(0);
        }

        if (cboKhuVuc.getItemCount() > 0) {
            cboKhuVuc.setSelectedIndex(0);
        }

        txtDate.setText("");
        jTable1.clearSelection();
    }
    
private void chinhBoCucKhiNhung() {
    // Bố cục tổng: form bên trái, bảng bên phải
    this.removeAll();
    this.setLayout(new java.awt.BorderLayout(15, 0));

    this.setBorder(
        javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
    );

    // Khu vực nhập liệu bên trái
    jPanel1.setPreferredSize(new java.awt.Dimension(310, 500));

    // Khu vực tìm kiếm và bảng bên phải
    jPanel2.removeAll();
    jPanel2.setLayout(new java.awt.BorderLayout(0, 15));

    // Đưa tìm kiếm lên trên bảng
    javax.swing.JPanel pnlTimKiem = new javax.swing.JPanel(
        new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5)
    );

    pnlTimKiem.add(jLabel6);
    pnlTimKiem.add(txtSeach);
    pnlTimKiem.add(btnSearch);

    // Cho bảng giãn theo kích thước vùng nội dung
    jScrollPane1.setPreferredSize(new java.awt.Dimension(650, 400));

    jPanel2.add(pnlTimKiem, java.awt.BorderLayout.NORTH);
    jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    // Đặt hai panel vào giao diện chính
    this.add(jPanel1, java.awt.BorderLayout.WEST);
    this.add(jPanel2, java.awt.BorderLayout.CENTER);

    this.revalidate();
    this.repaint();
}



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboNhanVien = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboKhuVuc = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        cboCaLam = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        txtSeach = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("THÔNG TIN CHI TIẾT");

        jLabel2.setText("Chọn mã nhân viên:");

        cboNhanVien.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Ngày làm:");

        jLabel4.setText("Ca làm:");

        cboKhuVuc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Khu vực:");

        btnThem.setBackground(new java.awt.Color(0, 102, 51));
        btnThem.setForeground(new java.awt.Color(255, 255, 255));
        btnThem.setText("Thêm");
        btnThem.addActionListener(this::btnThemActionPerformed);

        btnSua.setBackground(new java.awt.Color(0, 102, 51));
        btnSua.setForeground(new java.awt.Color(255, 255, 255));
        btnSua.setText("Sửa");
        btnSua.addActionListener(this::btnSuaActionPerformed);

        btnXoa.setBackground(new java.awt.Color(0, 102, 51));
        btnXoa.setForeground(new java.awt.Color(255, 255, 255));
        btnXoa.setText("Xoá");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        cboCaLam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnThem)
                        .addGap(18, 18, 18)
                        .addComponent(btnSua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnXoa))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cboCaLam, javax.swing.GroupLayout.Alignment.LEADING, 0, 191, Short.MAX_VALUE)
                        .addComponent(cboKhuVuc, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDate, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cboNhanVien, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboCaLam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboKhuVuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(104, Short.MAX_VALUE))
        );

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

        jLabel6.setText("Nhập mã nhân viên:");

        txtSeach.setPreferredSize(new java.awt.Dimension(220, 23));

        btnSearch.setBackground(new java.awt.Color(0, 102, 51));
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Tìm kiếm");
        btnSearch.addActionListener(this::btnSearchActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSeach, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btnSearch))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSeach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        if (maPhanCaDangChon == -1) {
        JOptionPane.showMessageDialog(
            this, "Vui lòng chọn dòng cần sửa!"
        );
        return;
    }

    try {
        PhanCa pc = layDuLieuForm();
        pc.setMaPhanCa(maPhanCaDangChon);

        if (phanCaDao.update(pc)) {
            JOptionPane.showMessageDialog(this, "Sửa phân ca thành công!");
            taiDanhSachPhanCa();
            xoaTrangForm();
        } else {
            JOptionPane.showMessageDialog(this, "Sửa phân ca thất bại!");
        }
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        try {
        PhanCa pc = layDuLieuForm();

        if (phanCaDao.insert(pc)) {
            JOptionPane.showMessageDialog(this, "Thêm phân ca thành công!");
            taiDanhSachPhanCa();
            xoaTrangForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm phân ca thất bại!");
        }
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        if (maPhanCaDangChon == -1) {
        JOptionPane.showMessageDialog(
            this, "Vui lòng chọn dòng cần xóa!"
        );
        return;
    }

    int chon = JOptionPane.showConfirmDialog(
        this,
        "Bạn có chắc muốn xóa phân ca này không?",
        "Xác nhận xóa",
        JOptionPane.YES_NO_OPTION
    );

    if (chon != JOptionPane.YES_OPTION) {
        return;
    }

    if (phanCaDao.delete(maPhanCaDangChon)) {
        JOptionPane.showMessageDialog(this, "Xóa phân ca thành công!");
        taiDanhSachPhanCa();
        xoaTrangForm();
    } else {
        JOptionPane.showMessageDialog(this, "Xóa phân ca thất bại!");
    }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String text = txtSeach.getText().trim();

    if (text.isEmpty()) {
        taiDanhSachPhanCa();
        return;
    }

    try {
        int maNV = Integer.parseInt(text);
        hienThiDanhSach(phanCaDao.findByMaNV(maNV));
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this, "Mã nhân viên phải là số nguyên!"
        );
    }
    }//GEN-LAST:event_btnSearchActionPerformed
      public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(() -> {
        javax.swing.JFrame frame = new javax.swing.JFrame("Quản lý ca làm");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(new CaLamView());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboCaLam;
    private javax.swing.JComboBox<String> cboKhuVuc;
    private javax.swing.JComboBox<String> cboNhanVien;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtSeach;
    // End of variables declaration//GEN-END:variables
}

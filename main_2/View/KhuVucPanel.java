package View;

import DAO.KhuVucDAO;
import Entity.KhuVuc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class KhuVucPanel extends javax.swing.JPanel {

    private static final String CHON_TT = "-- Chọn trạng thái --";
    private final KhuVucDAO dao = new KhuVucDAO();
    private DefaultTableModel model;
    private List<KhuVuc> ds = new ArrayList<>();

    private static final java.awt.Color XANH = new java.awt.Color(0x21, 0x5C, 0x3C);
    private static final java.awt.Color XANH_NHAT = new java.awt.Color(0xD9, 0xEA, 0xD9);
    private static final java.awt.Color NEN_O = new java.awt.Color(0xF7, 0xF9, 0xF7);
    private static final java.awt.Color VIEN_O = new java.awt.Color(0xC8, 0xCF, 0xC8);
    private static final java.awt.Color CHU = new java.awt.Color(0x33, 0x33, 0x33);
    private static final java.awt.Font FONT = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
    private static final java.awt.Font FONT_DAM = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13);

    public KhuVucPanel() {
        initComponents();
        tinhChinhGiaoDien();
        initLogic();
    }

    private void tinhChinhGiaoDien() {
        setBackground(new java.awt.Color(0xEE, 0xF3, 0xEE));
        for (javax.swing.JPanel p : new javax.swing.JPanel[]{pnlForm, pnlBang}) {
            p.setBackground(java.awt.Color.WHITE);
            p.setBorder(vienBoGoc(20));   // 20 = độ bo, tăng lên nếu muốn tròn hơn
        }
        duyetGiaoDien(this);
        lblTieuDe.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblTieuDe.setForeground(XANH);
    }

    private javax.swing.border.Border vienBoGoc(int arc) {
        final java.awt.Color nenNgoai = new java.awt.Color(0xEE, 0xF3, 0xEE); // trùng màu nền ngoài
        return new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int w, int h) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                // tô phần góc thừa bằng màu nền ngoài để góc trắng của panel không lòi ra
                java.awt.geom.Area goc = new java.awt.geom.Area(new java.awt.geom.Rectangle2D.Float(x, y, w, h));
                goc.subtract(new java.awt.geom.Area(
                        new java.awt.geom.RoundRectangle2D.Float(x, y, w, h, arc, arc)));
                g2.setColor(nenNgoai);
                g2.fill(goc);

                // vẽ viền xanh bo góc
                g2.setColor(XANH);
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.draw(new java.awt.geom.RoundRectangle2D.Float(x + 1, y + 1, w - 2, h - 2, arc, arc));
                g2.dispose();
            }

            @Override
            public java.awt.Insets getBorderInsets(java.awt.Component c) {
                return new java.awt.Insets(4, 4, 4, 4);
            }
        };
    }

    private void duyetGiaoDien(java.awt.Component c) {
        if (c instanceof javax.swing.JButton) {
            javax.swing.JButton b = (javax.swing.JButton) c;
            b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            b.setBackground(XANH);
            b.setForeground(java.awt.Color.WHITE);
            b.setFont(FONT_DAM);
            b.setFocusPainted(false);
            b.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 18, 8, 18));
            b.setOpaque(true);
            b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        } else if (c instanceof javax.swing.JTextField) {
            javax.swing.JTextField t = (javax.swing.JTextField) c;
            t.setBackground(NEN_O);
            t.setForeground(CHU);
            t.setFont(FONT);
            t.setCaretColor(XANH);
            t.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(VIEN_O),
                    javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        } else if (c instanceof javax.swing.JComboBox) {
            javax.swing.JComboBox<?> cb = (javax.swing.JComboBox<?>) c;
            cb.setBackground(NEN_O);
            cb.setForeground(CHU);
            cb.setFont(FONT);
            cb.setBorder(javax.swing.BorderFactory.createLineBorder(VIEN_O));
        } else if (c instanceof javax.swing.JTable) {
            tinhChinhBang((javax.swing.JTable) c);
        } else if (c instanceof javax.swing.JScrollPane) {
            javax.swing.JScrollPane sp = (javax.swing.JScrollPane) c;
            sp.setBorder(javax.swing.BorderFactory.createLineBorder(VIEN_O));
            sp.getViewport().setBackground(java.awt.Color.WHITE);
        } else if (c instanceof javax.swing.JLabel) {
            c.setFont(FONT);
            c.setForeground(CHU);
        } else if (c instanceof javax.swing.JPanel && c != this) {
            c.setBackground(java.awt.Color.WHITE);
        }
        if (c instanceof java.awt.Container) {
            for (java.awt.Component con : ((java.awt.Container) c).getComponents()) {
                duyetGiaoDien(con);
            }
        }
    }

    private void tinhChinhBang(javax.swing.JTable t) {
        t.setRowHeight(34);
        t.setFont(FONT);
        t.setForeground(CHU);
        t.setBackground(java.awt.Color.WHITE);
        t.setShowGrid(false);
        t.setIntercellSpacing(new java.awt.Dimension(0, 0));
        t.setSelectionBackground(XANH_NHAT);
        t.setSelectionForeground(CHU);
        t.setFillsViewportHeight(true);

        javax.swing.table.DefaultTableCellRenderer giua = new javax.swing.table.DefaultTableCellRenderer();
        giua.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        t.setDefaultRenderer(Object.class, giua);

        javax.swing.table.JTableHeader h = t.getTableHeader();
        h.setPreferredSize(new java.awt.Dimension(0, 36));
        h.setReorderingAllowed(false);
        h.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable tb, Object v,
                    boolean sel, boolean foc, int r, int col) {
                javax.swing.JLabel l = (javax.swing.JLabel) super.getTableCellRendererComponent(tb, v, sel, foc, r, col);
                l.setBackground(XANH);
                l.setForeground(java.awt.Color.WHITE);
                l.setFont(FONT_DAM);
                l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                l.setBorder(null);
                l.setOpaque(true);
                return l;
            }
        });
    }

    private void initLogic() {

        model = (DefaultTableModel) tblKhuVuc.getModel();
        tblKhuVuc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cboTrangThai.setModel(new DefaultComboBoxModel<>(new String[]{CHON_TT, "Hoạt động", "Bảo trì"}));

        // Mã tự sinh nên không cho sửa
        txtMa.setEditable(false);
        txtMa.setText("");

        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnTimKiem.addActionListener(e -> timKiem());
        txtSearch.addActionListener(e -> timKiem());

        tblKhuVuc.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienLenForm();
            }
        });

        loadTable(dao.getAll());
    }

    private void loadTable(List<KhuVuc> list) {
        ds = list;
        Map<Integer, Integer> dem = dao.demBanTheoKhuVuc();
        model.setRowCount(0);
        for (KhuVuc kv : ds) {
            model.addRow(new Object[]{
                kv.getMaKhuVuc(),
                kv.getTenKhuVuc(),
                kv.getViTri(),
                dem.getOrDefault(kv.getMaKhuVuc(), 0),
                kv.getTrangThai()
            });
        }
    }

    private void hienLenForm() {
        int row = tblKhuVuc.getSelectedRow();
        if (row < 0 || row >= ds.size()) {
            return;
        }
        KhuVuc kv = ds.get(row);
        txtMa.setText(String.valueOf(kv.getMaKhuVuc()));
        txtTen.setText(kv.getTenKhuVuc());
        txtViTri.setText(kv.getViTri() == null ? "" : kv.getViTri());
        txtGhiChu.setText(kv.getGhiChu() == null ? "" : kv.getGhiChu());
        cboTrangThai.setSelectedItem(kv.getTrangThai() == null ? "Hoạt động" : kv.getTrangThai());
    }

    private void lamMoi() {
        tblKhuVuc.clearSelection();
        txtMa.setText("");
        txtTen.setText("");
        txtViTri.setText("");
        txtGhiChu.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtTen.requestFocus();
    }

    private void thongBao(String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", type);
    }

    // Đọc form -> KhuVuc, trả null nếu dữ liệu sai
    private KhuVuc docForm(int maKhuVuc) {
        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) {
            thongBao("Tên khu vực không được để trống!", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return null;
        }
        if (dao.isTrungTen(ten, maKhuVuc)) {
            thongBao("Tên khu vực \"" + ten + "\" đã tồn tại!", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return null;
        }

        if (cboTrangThai.getSelectedIndex() == 0) {
            thongBao("Hãy chọn trạng thái khu vực!", JOptionPane.WARNING_MESSAGE);
            cboTrangThai.requestFocus();
            return null;
        }

        return new KhuVuc(maKhuVuc, ten,
                txtViTri.getText().trim(),
                String.valueOf(cboTrangThai.getSelectedItem()),
                txtGhiChu.getText().trim());

    }

    private void them() {
        // đang chọn dòng cũ -> chỉ làm trống form để nhập mới
        if (tblKhuVuc.getSelectedRow() >= 0) {
            lamMoi();
            return;
        }

        KhuVuc kv = docForm(0);
        if (kv == null) {
            return;
        }
        if (dao.insert(kv)) {
            thongBao("Thêm khu vực thành công!", JOptionPane.INFORMATION_MESSAGE);
            txtSearch.setText("");
            loadTable(dao.getAll());
            lamMoi();
        } else {
            thongBao("Thêm thất bại! Xem lỗi trong console.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sua() {
        int row = tblKhuVuc.getSelectedRow();
        if (row < 0) {
            thongBao("Hãy chọn khu vực cần sửa trong bảng!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        KhuVuc kv = docForm(ds.get(row).getMaKhuVuc());
        if (kv == null) {
            return;
        }
        if (dao.update(kv)) {
            thongBao("Cập nhật thành công!", JOptionPane.INFORMATION_MESSAGE);
            loadTable(txtSearch.getText().trim().isEmpty() ? dao.getAll() : dao.search(txtSearch.getText()));
            lamMoi();
        } else {
            thongBao("Cập nhật thất bại!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        int row = tblKhuVuc.getSelectedRow();
        if (row < 0) {
            thongBao("Hãy chọn khu vực cần xóa trong bảng!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        KhuVuc kv = ds.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa khu vực \"" + kv.getTenKhuVuc() + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.delete(kv.getMaKhuVuc())) {
            thongBao("Đã xóa!", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dao.getAll());
            lamMoi();
        } else {
            thongBao("Không xóa được! Khu vực này đang có bàn ăn hoặc phân ca tham chiếu.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiem() {
        loadTable(dao.search(txtSearch.getText()));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlForm = new javax.swing.JPanel();
        lblTieuDe = new javax.swing.JLabel();
        lblMaKV = new javax.swing.JLabel();
        txtMa = new javax.swing.JTextField();
        lblTenKV = new javax.swing.JLabel();
        txtTen = new javax.swing.JTextField();
        lblViTri = new javax.swing.JLabel();
        txtViTri = new javax.swing.JTextField();
        lblTrangThai = new javax.swing.JLabel();
        cboTrangThai = new javax.swing.JComboBox<>();
        lblGhiChu = new javax.swing.JLabel();
        txtGhiChu = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        pnlBang = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKhuVuc = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(70, 25));

        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(30, 92, 58)));
        pnlForm.setPreferredSize(new java.awt.Dimension(266, 441));

        lblTieuDe.setText("THÔNG TIN CHI TIẾT");

        lblMaKV.setText("Mã Khu Vực:");

        txtMa.setPreferredSize(new java.awt.Dimension(200, 30));
        txtMa.addActionListener(this::txtMaActionPerformed);

        lblTenKV.setText("Tên Khu Vực:");

        txtTen.setPreferredSize(new java.awt.Dimension(200, 30));

        lblViTri.setText("Tầng / Vị trí:");

        txtViTri.setPreferredSize(new java.awt.Dimension(200, 30));

        lblTrangThai.setText("Trạng thái:");

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoạt động", "Bảo trì", " " }));
        cboTrangThai.setPreferredSize(new java.awt.Dimension(200, 33));
        cboTrangThai.addActionListener(this::cboTrangThaiActionPerformed);

        lblGhiChu.setText("Ghi chú mô tả:");

        txtGhiChu.setPreferredSize(new java.awt.Dimension(200, 30));

        btnThem.setText("Thêm");
        btnThem.addActionListener(this::btnThemActionPerformed);

        btnSua.setText("Sửa");
        btnSua.addActionListener(this::btnSuaActionPerformed);

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        javax.swing.GroupLayout pnlFormLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(pnlFormLayout);
        pnlFormLayout.setHorizontalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnThem)
                .addGap(18, 18, 18)
                .addComponent(btnSua)
                .addGap(18, 18, 18)
                .addComponent(btnXoa))
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTieuDe)
                    .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMaKV)
                    .addComponent(lblTenKV)
                    .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblViTri)
                    .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrangThai)
                    .addComponent(lblGhiChu)
                    .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pnlFormLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboTrangThai, txtGhiChu, txtMa, txtTen, txtViTri});

        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTieuDe)
                .addGap(29, 29, 29)
                .addComponent(lblMaKV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTenKV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblViTri)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtViTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(lblTrangThai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(lblGhiChu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa))
                .addGap(28, 28, 28))
        );

        pnlBang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(30, 92, 58)));

        jLabel1.setText("Tìm kiếm từ khóa: ");

        txtSearch.setPreferredSize(new java.awt.Dimension(250, 30));

        btnTimKiem.setText("Tìm kiếm");
        btnTimKiem.addActionListener(this::btnTimKiemActionPerformed);

        tblKhuVuc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã khu vực", "Tên khu vực", "Vị trí", "Số lượng bàn", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblKhuVuc);

        javax.swing.GroupLayout pnlBangLayout = new javax.swing.GroupLayout(pnlBang);
        pnlBang.setLayout(pnlBangLayout);
        pnlBangLayout.setHorizontalGroup(
            pnlBangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBangLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
            .addGroup(pnlBangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        pnlBangLayout.setVerticalGroup(
            pnlBangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBangLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(pnlBangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlForm, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlBang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlForm, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addComponent(pnlBang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaActionPerformed

    private void cboTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTrangThaiActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed

    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed

    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed

    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimKiemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGhiChu;
    private javax.swing.JLabel lblMaKV;
    private javax.swing.JLabel lblTenKV;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JLabel lblViTri;
    private javax.swing.JPanel pnlBang;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JTable tblKhuVuc;
    private javax.swing.JTextField txtGhiChu;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTen;
    private javax.swing.JTextField txtViTri;
    // End of variables declaration//GEN-END:variables
}

package View;

import DAO.BanAnDAO;
import DAO.DatBanDAO;
import Entity.BanAn;
import Entity.DatBan;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class DatBanPanel extends javax.swing.JPanel {

    private static final String CHON_BAN = "-- Chọn bàn --";
    private static final String KHONG_CHUYEN = "-- Không chuyển --";
    private static final String DA_DAT = "Đã đặt";
    private static final String DA_NHAN = "Đã nhận bàn";
    private static final String DA_HUY = "Đã hủy";

    private final DatBanDAO dao = new DatBanDAO();
    private final BanAnDAO banDao = new BanAnDAO();
    private DefaultTableModel model;
    private List<DatBan> ds = new ArrayList<>();

    private static final java.awt.Color XANH = new java.awt.Color(0x21, 0x5C, 0x3C);
    private static final java.awt.Color XANH_NHAT = new java.awt.Color(0xD9, 0xEA, 0xD9);
    private static final java.awt.Color NEN_O = new java.awt.Color(0xF7, 0xF9, 0xF7);
    private static final java.awt.Color VIEN_O = new java.awt.Color(0xC8, 0xCF, 0xC8);
    private static final java.awt.Color CHU = new java.awt.Color(0x33, 0x33, 0x33);
    private static final java.awt.Font FONT = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13);
    private static final java.awt.Font FONT_DAM = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13);

    public DatBanPanel() {
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
        model = (DefaultTableModel) tblKhuVuc.getModel();   // bảng danh sách phiếu đặt
        tblKhuVuc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        txtGio.setToolTipText("Nhập giờ dạng HH:mm, ví dụ 18:30");
        cboBanMoi.setToolTipText("Chọn bàn mới nếu muốn chuyển phiếu sang bàn khác (dùng với nút Sửa)");

        taiDanhSachBan();

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

        taoMenuChuotPhai();
        loadTable(dao.getAll());
    }

    private void taiDanhSachBan() {
        cboBan.removeAllItems();
        cboBanMoi.removeAllItems();
        cboBan.addItem(CHON_BAN);          // dòng mở đầu
        cboBanMoi.addItem(KHONG_CHUYEN);
        for (BanAn b : banDao.search(0, "")) {
            cboBan.addItem(b);
            cboBanMoi.addItem(b);
        }
        cboBan.setSelectedIndex(0);
    }

    private void taoMenuChuotPhai() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miNhan = new JMenuItem("Khách đã nhận bàn");
        JMenuItem miHuy = new JMenuItem("Hủy đặt bàn");
        miNhan.addActionListener(e -> doiTrangThai(DA_NHAN));
        miHuy.addActionListener(e -> doiTrangThai(DA_HUY));
        menu.add(miNhan);
        menu.add(miHuy);

        tblKhuVuc.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                hienMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                hienMenu(e);
            }

            private void hienMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tblKhuVuc.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tblKhuVuc.setRowSelectionInterval(row, row);
                        menu.show(tblKhuVuc, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void loadTable(List<DatBan> list) {
        ds = list;
        model.setRowCount(0);
        for (DatBan d : ds) {
            model.addRow(new Object[]{
                d.getMaDat(), d.getTenKhach(), d.getSdt(),
                d.getTenBan(), cat5(d.getGioHen()), d.getTrangThai()
            });
        }
    }

    // "18:30:00" -> "18:30"
    private String cat5(String gio) {
        return (gio != null && gio.length() > 5) ? gio.substring(0, 5) : gio;
    }

    private DatBan dongDangChon() {
        int row = tblKhuVuc.getSelectedRow();
        return (row >= 0 && row < ds.size()) ? ds.get(row) : null;
    }

    private void hienLenForm() {
        DatBan d = dongDangChon();
        if (d == null) {
            return;
        }
        txtTenKhach.setText(d.getTenKhach());
        txtSDT.setText(d.getSdt());
        txtGio.setText(cat5(d.getGioHen()));
        for (int i = 0; i < cboBan.getItemCount(); i++) {
            Object o = cboBan.getItemAt(i);
            if (o instanceof BanAn && ((BanAn) o).getMaBan() == d.getMaBan()) {
                cboBan.setSelectedIndex(i);
                break;
            }
        }
        cboBanMoi.setSelectedIndex(0);
    }

    private void lamMoi() {
        tblKhuVuc.clearSelection();
        txtTenKhach.setText("");
        txtSDT.setText("");
        txtGio.setText("");
        if (cboBan.getItemCount() > 0) {
            cboBan.setSelectedIndex(0);
        }
        cboBanMoi.setSelectedIndex(0);
        txtTenKhach.requestFocus();
    }

    private void thongBao(String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", type);
    }

    // Đọc + kiểm tra form. Trả null nếu sai (đã hiện thông báo).
    // maDat: "" khi thêm mới; choPhepChuyen: true khi sửa (dùng cboBanMoi)
    private DatBan docForm(String maDat, boolean choPhepChuyen) {
        String ten = txtTenKhach.getText().trim();
        if (ten.isEmpty()) {
            thongBao("Tên khách hàng không được để trống!", JOptionPane.WARNING_MESSAGE);
            txtTenKhach.requestFocus();
            return null;
        }
        String sdt = txtSDT.getText().trim();
        if (!sdt.matches("0\\d{9}")) {
            thongBao("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0!", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return null;
        }
        Object ban = cboBan.getSelectedItem();
        if (!(ban instanceof BanAn)) {
            thongBao("Hãy chọn bàn đặt!", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String gio;
        try {
            LocalTime t = LocalTime.parse(txtGio.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
            gio = t.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException ex) {
            thongBao("Giờ không hợp lệ! Nhập dạng HH:mm, ví dụ 18:30", JOptionPane.WARNING_MESSAGE);
            txtGio.requestFocus();
            return null;
        }
        BanAn banChon = (BanAn) ban;
        Object moi = cboBanMoi.getSelectedItem();
        if (choPhepChuyen && moi instanceof BanAn) {
            banChon = (BanAn) moi;
        }
        if (dao.isTrungGio(banChon.getMaBan(), gio, maDat)) {
            thongBao(banChon.getTenBan() + " đã có phiếu đặt trong khoảng "
                    + DatBanDAO.PHUT_GIAN_CACH + " phút quanh giờ " + gio + "!",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new DatBan(maDat, ten, sdt, banChon.getMaBan(), banChon.getTenBan(), gio, DA_DAT);
    }

    private void them() {
        // đang chọn dòng cũ -> chỉ làm trống form để nhập phiếu mới
        if (tblKhuVuc.getSelectedRow() >= 0) {
            lamMoi();
            return;
        }

        DatBan d = docForm("", false);
        if (d == null) {
            return;
        }

        // Tùy chọn: bỏ đoạn này nếu docForm đã kiểm tra trùng giờ
        if (dao.isTrungGio(d.getMaBan(), d.getGioHen(), "")) {
            thongBao("Bàn này đã có phiếu đặt cách giờ hẹn dưới "
                    + DatBanDAO.PHUT_GIAN_CACH + " phút!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dao.insert(d)) {
            thongBao("Đặt bàn thành công!", JOptionPane.INFORMATION_MESSAGE);
            txtSearch.setText("");
            loadTable(dao.getAll());
            lamMoi();
        } else {
            thongBao("Đặt bàn thất bại! Xem lỗi trong console.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sua() {
        DatBan cu = dongDangChon();
        if (cu == null) {
            thongBao("Hãy chọn phiếu đặt cần sửa trong bảng!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!DA_DAT.equals(cu.getTrangThai())) {
            thongBao("Chỉ sửa được phiếu ở trạng thái \"Đã đặt\"!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DatBan d = docForm(cu.getMaDat(), true);
        if (d == null) {
            return;
        }
        if (dao.update(d)) {
            thongBao("Cập nhật thành công!", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dao.getAll());
            lamMoi();
        } else {
            thongBao("Cập nhật thất bại!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        DatBan d = dongDangChon();
        if (d == null) {
            thongBao("Hãy chọn phiếu đặt cần xóa trong bảng!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Xóa phiếu " + d.getMaDat() + " của khách " + d.getTenKhach() + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.delete(d.getMaDat())) {
            thongBao("Đã xóa!", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dao.getAll());
            lamMoi();
        } else {
            thongBao("Xóa thất bại!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doiTrangThai(String trangThaiMoi) {
        DatBan d = dongDangChon();
        if (d == null) {
            return;
        }
        if (!DA_DAT.equals(d.getTrangThai())) {
            thongBao("Phiếu này đã \"" + d.getTrangThai() + "\", không đổi được nữa!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dao.doiTrangThai(d.getMaDat(), trangThaiMoi)) {
            loadTable(dao.getAll());
            lamMoi();
        } else {
            thongBao("Đổi trạng thái thất bại!", JOptionPane.ERROR_MESSAGE);
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
        txtTenKhach = new javax.swing.JTextField();
        lblTenKV = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        lblViTri = new javax.swing.JLabel();
        lblTrangThai = new javax.swing.JLabel();
        lblGhiChu = new javax.swing.JLabel();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        cboBan = new javax.swing.JComboBox<>();
        txtGio = new javax.swing.JTextField();
        cboBanMoi = new javax.swing.JComboBox<>();
        pnlBang = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKhuVuc = new javax.swing.JTable();

        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(30, 92, 58)));
        pnlForm.setPreferredSize(new java.awt.Dimension(266, 441));

        lblTieuDe.setText("THÔNG TIN CHI TIẾT");

        lblMaKV.setText("Tên Khách Hàng:");

        txtTenKhach.setPreferredSize(new java.awt.Dimension(200, 30));
        txtTenKhach.addActionListener(this::txtTenKhachActionPerformed);

        lblTenKV.setText("Số Điện Thoại:");

        txtSDT.setPreferredSize(new java.awt.Dimension(200, 30));

        lblViTri.setText("Chọn Bàn Đặt:");

        lblTrangThai.setText("Giờ Đến Nhận:");

        lblGhiChu.setText("Bàn Chuyển Sang:");

        btnThem.setText("Thêm");

        btnSua.setText("Sửa");

        btnXoa.setText("Xóa");

        cboBan.setPreferredSize(new java.awt.Dimension(200, 33));

        txtGio.setPreferredSize(new java.awt.Dimension(200, 30));

        cboBanMoi.setToolTipText("HH:mm\n");
        cboBanMoi.setPreferredSize(new java.awt.Dimension(200, 33));

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
                    .addComponent(cboBanMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTieuDe)
                    .addComponent(txtTenKhach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMaKV)
                    .addComponent(lblTenKV)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblViTri)
                    .addComponent(lblTrangThai)
                    .addComponent(lblGhiChu)))
        );
        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTieuDe)
                .addGap(29, 29, 29)
                .addComponent(lblMaKV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTenKhach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTenKV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblViTri)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboBan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(lblTrangThai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(lblGhiChu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboBanMoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem)
                    .addComponent(btnSua)
                    .addComponent(btnXoa))
                .addGap(28, 28, 28))
        );

        pnlBang.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(30, 92, 58)));

        jLabel1.setText("Tìm kiếm từ khóa: ");

        txtSearch.setPreferredSize(new java.awt.Dimension(250, 25));

        btnTimKiem.setText("Tìm kiếm");

        tblKhuVuc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã đặt", "Khách hàng", "Số điện thoại", "Bàn", "Giờ hẹn", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
                .addContainerGap(37, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlForm, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(pnlBang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 52, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTenKhachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenKhachActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenKhachActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<Object> cboBan;
    private javax.swing.JComboBox<Object> cboBanMoi;
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
    private javax.swing.JTextField txtGio;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTenKhach;
    // End of variables declaration//GEN-END:variables
}

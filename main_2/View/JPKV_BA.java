package View;

import DAO.BanAnDAO;
import Entity.BanAn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;

public class JPKV_BA extends javax.swing.JPanel {

    private BanAnDAO banAnDAO = new BanAnDAO();
    private int selectedMaKhuVuc = 0; // 0 = tất cả khu vực

    private JButton[] dsNut;

    // Bàn đang được chọn
    private BanAn banDangChon = null;
    private JPanel cardDangChon = null;

    private static final String[] TEN_KV = {"Tầng 1", "Tầng 2", "Ngoài trời", "Phòng VIP", "Sân vườn"};

    private static final Color XANH = new Color(46, 139, 87);
    private static final Color DO = new Color(205, 92, 92);
    private static final Color VANG = new Color(218, 165, 32);
    
    public JPKV_BA() {
        initComponents();
        if (java.beans.Beans.isDesignTime()) {
            return;
        }
        initEventAndLoadData();
    }

    public static class RoundedButton extends JButton {

        public RoundedButton() {
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean sel = getModel().isSelected();
            g2.setColor(sel ? new Color(232, 240, 254) : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(sel ? new Color(120, 160, 220) : new Color(190, 190, 190));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            Color fg = sel ? new Color(0, 90, 170) : new Color(50, 50, 50);
            if (!fg.equals(getForeground())) {
                setForeground(fg);
            }
            super.paintComponent(g);
        }
    }


    private void initEventAndLoadData() {
        dsNut = new JButton[]{btnTatCa, btnTang1, btnTang2, btnNgoaiTroi, btnPhongVIP, btnSanVuon};

        btnTatCa.addActionListener(e -> chonKhuVuc(0, btnTatCa));
        btnTang1.addActionListener(e -> chonKhuVuc(1, btnTang1));
        btnTang2.addActionListener(e -> chonKhuVuc(2, btnTang2));
        btnNgoaiTroi.addActionListener(e -> chonKhuVuc(3, btnNgoaiTroi));
        btnPhongVIP.addActionListener(e -> chonKhuVuc(4, btnPhongVIP));
        btnSanVuon.addActionListener(e -> chonKhuVuc(5, btnSanVuon));

        btnThem.addActionListener(e -> themBan());
        btnSua.addActionListener(e -> suaBan());
        btnXoa.addActionListener(e -> xoaBan());

        // Gõ tới đâu lọc tới đó
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                taiLai();
            }

            public void removeUpdate(DocumentEvent e) {
                taiLai();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

        chonKhuVuc(0, btnTatCa); // mặc định: tất cả
    }

    private void chonNut(JButton active) {
        for (JButton b : dsNut) {
            b.getModel().setSelected(b == active);
            b.repaint();
        }
    }

    private void chonKhuVuc(int maKhuVuc, JButton nut) {
        selectedMaKhuVuc = maKhuVuc;
        chonNut(nut);
        taiLai();
    }

    private void taiLai() {
        loadBanAnLenGiaoDien(selectedMaKhuVuc, txtSearch.getText().trim());
    }

    private void loadBanAnLenGiaoDien(int maKhuVuc, String keyword) {
        List<BanAn> listBan = banAnDAO.search(maKhuVuc, keyword);

        banDangChon = null;
        cardDangChon = null;

        pnlLuoi.removeAll();
        for (BanAn b : listBan) {
            pnlLuoi.add(taoOBan(b));
        }
        pnlLuoi.revalidate();
        pnlLuoi.repaint();
    }

        private Color layMauTheoTrangThai(String trangThai) {
        if (trangThai.equalsIgnoreCase("Trống")) {
            return XANH;
        } else if (trangThai.equalsIgnoreCase("Đang dùng")) {
            return DO;
        }
        return VANG; // Đã đặt
    }

    private Color sang(Color c, double t) {
        return new Color(
                (int) (c.getRed() + (255 - c.getRed()) * t),
                (int) (c.getGreen() + (255 - c.getGreen()) * t),
                (int) (c.getBlue() + (255 - c.getBlue()) * t));
    }

    private Border vienThuong() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 228, 228)),
                BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    private JPanel taoOBan(BanAn b) {
        String trangThai = b.getTrangThai() != null ? b.getTrangThai() : "Trống";
        Color mau = layMauTheoTrangThai(trangThai);

        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(150, 150));
        card.setBorder(vienThuong());

        JLabel lblSo = new JLabel(b.getTenBan(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, sang(mau, 0.30), 0, getHeight(), mau));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(mau.darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblSo.setOpaque(false);
        lblSo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSo.setForeground(Color.WHITE);

        JLabel lblTen = new JLabel(b.getTenBan() + " (" + b.getSoCho() + " chỗ)", SwingConstants.CENTER);
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblTrangThai = new JLabel(trangThai, SwingConstants.CENTER);
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTrangThai.setForeground(Color.GRAY);

        JPanel pnlChu = new JPanel(new GridLayout(2, 1));
        pnlChu.setOpaque(false);
        pnlChu.add(lblTen);
        pnlChu.add(lblTrangThai);

        card.add(lblSo, BorderLayout.CENTER);
        card.add(pnlChu, BorderLayout.SOUTH);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chonBan(b, card);
                if (e.getClickCount() == 2) {
                    suaBan();
                }
            }
        });
        return card;
    }

    private void chonBan(BanAn b, JPanel card) {
        if (cardDangChon != null) {
            cardDangChon.setBorder(vienThuong());
        }
        banDangChon = b;
        cardDangChon = card;
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }

    private boolean daChonBan() {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng click chọn một bàn trước!");
            return false;
        }
        return true;
    }

        private BanAn hienFormBan(BanAn b) {
        JTextField txtTen = new JTextField(b == null ? "" : b.getTenBan());
        JTextField txtSoCho = new JTextField(b == null ? "4" : String.valueOf(b.getSoCho()));
        JComboBox<String> cboKV = new JComboBox<>(TEN_KV);

        if (b != null) {
            cboKV.setSelectedIndex(b.getMaKhuVuc() - 1);
        } else if (selectedMaKhuVuc > 0) {
            cboKV.setSelectedIndex(selectedMaKhuVuc - 1);
        }

        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.add(new JLabel("Tên bàn:"));
        p.add(txtTen);
        p.add(new JLabel("Khu vực:"));
        p.add(cboKV);
        p.add(new JLabel("Sức chứa:"));
        p.add(txtSoCho);

        int kq = JOptionPane.showConfirmDialog(this, p,
                b == null ? "Thêm bàn" : "Sửa bàn",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (kq != JOptionPane.OK_OPTION) {
            return null;
        }

        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên bàn không được để trống!");
            return null;
        }
        int soCho;
        try {
            soCho = Integer.parseInt(txtSoCho.getText().trim());
            if (soCho <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sức chứa phải là số nguyên dương!");
            return null;
        }

        BanAn moi = new BanAn();
        moi.setMaBan(b == null ? 0 : b.getMaBan());
        moi.setTenBan(ten);
        moi.setMaKhuVuc(cboKV.getSelectedIndex() + 1);
        moi.setSoCho(soCho);
        moi.setTrangThai(b == null ? "Trống" : b.getTrangThai());
        return moi;
    }

    private void themBan() {
        BanAn b = hienFormBan(null);
        if (b == null) {
            return;
        }
        if (banAnDAO.insert(b)) {
            JOptionPane.showMessageDialog(this, "Thêm bàn thành công!");
            if (selectedMaKhuVuc != 0 && selectedMaKhuVuc != b.getMaKhuVuc()) {
                chonKhuVuc(b.getMaKhuVuc(), dsNut[b.getMaKhuVuc()]);
            } else {
                taiLai();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại (tên bàn có thể đã tồn tại)!");
        }
    }

    private void suaBan() {
        if (!daChonBan()) {
            return;
        }
        BanAn b = hienFormBan(banDangChon);
        if (b == null) {
            return;
        }
        if (banAnDAO.update(b)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            taiLai();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại (tên bàn có thể bị trùng)!");
        }
    }

    private void xoaBan() {
        if (!daChonBan()) {
            return;
        }
        if (banDangChon.getTrangThai().equalsIgnoreCase("Đang dùng")) {
            JOptionPane.showMessageDialog(this, "Bàn đang được sử dụng, không thể xóa!");
            return;
        }
        int kq = JOptionPane.showConfirmDialog(this,
                "Xóa " + banDangChon.getTenBan() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (kq != JOptionPane.YES_OPTION) {
            return;
        }
        if (banAnDAO.delete(banDangChon.getMaBan())) {
            JOptionPane.showMessageDialog(this, "Đã xóa bàn!");
            taiLai();
        } else {
            JOptionPane.showMessageDialog(this, "Không xóa được (bàn đã có hóa đơn)!");
        }
    }

    // Panel xếp thẻ bàn tự xuống dòng và cuộn được theo chiều dọc
    public static class PanelLuoi extends JPanel implements Scrollable {

        public PanelLuoi() {
            super(new FlowLayout(FlowLayout.LEFT, 18, 18));
            setBackground(Color.WHITE);
        }

        @Override
        public Dimension getPreferredSize() {
            int w = getParent() != null ? getParent().getWidth() : 0;
            if (w <= 0) {
                return super.getPreferredSize();
            }
            FlowLayout fl = (FlowLayout) getLayout();
            int hgap = fl.getHgap(), vgap = fl.getVgap();
            int x = hgap, y = vgap, rowH = 0;
            for (Component c : getComponents()) {
                if (!c.isVisible()) {
                    continue;
                }
                Dimension d = c.getPreferredSize();
                if (x + d.width + hgap > w && x > hgap) {
                    x = hgap;
                    y += rowH + vgap;
                    rowH = 0;
                }
                x += d.width + hgap;
                rowH = Math.max(rowH, d.height);
            }
            return new Dimension(w, y + rowH + vgap);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
            return 30;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
            return r.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        pnlMenu = new javax.swing.JPanel();
        btnTatCa = new RoundedButton();
        btnTang1 = new RoundedButton();
        btnTang2 = new RoundedButton();
        btnNgoaiTroi = new RoundedButton();
        btnPhongVIP = new RoundedButton();
        btnSanVuon = new RoundedButton();
        pnlLegend = new javax.swing.JPanel();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        lblColor1 = new javax.swing.JLabel();
        lblText1 = new javax.swing.JLabel();
        lblColor2 = new javax.swing.JLabel();
        lblText2 = new javax.swing.JLabel();
        lblColor3 = new javax.swing.JLabel();
        lblText3 = new javax.swing.JLabel();
        pnlTables = new javax.swing.JPanel();
        scrollBan = new javax.swing.JScrollPane();
        pnlLuoi =    new PanelLuoi();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1000, 600));
        setLayout(new java.awt.BorderLayout());

        pnlTop.setBackground(new java.awt.Color(250, 250, 250));
        pnlTop.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(225, 225, 225)), javax.swing.BorderFactory.createEmptyBorder(12, 22, 12, 22)));
        pnlTop.setLayout(new java.awt.BorderLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitle.setText("QUẢN LÝ BÀN ĂN");
        pnlTop.add(lblTitle, java.awt.BorderLayout.CENTER);

        pnlSearch.setOpaque(false);
        pnlSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));

        jLabel1.setText("🔍");
        pnlSearch.add(jLabel1);

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200))));
        txtSearch.setPreferredSize(new java.awt.Dimension(200, 25));
        pnlSearch.add(txtSearch);

        pnlTop.add(pnlSearch, java.awt.BorderLayout.LINE_END);

        add(pnlTop, java.awt.BorderLayout.PAGE_START);

        pnlMenu.setBackground(new java.awt.Color(250, 250, 250));
        pnlMenu.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, new java.awt.Color(225, 225, 225)), javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        pnlMenu.setPreferredSize(new java.awt.Dimension(200, 400));
        pnlMenu.setLayout(new java.awt.GridLayout(6, 1, 0, 8));

        btnTatCa.setText("Tất cả khu vực");
        pnlMenu.add(btnTatCa);

        btnTang1.setText("Tầng 1");
        pnlMenu.add(btnTang1);

        btnTang2.setText("Tầng 2");
        pnlMenu.add(btnTang2);

        btnNgoaiTroi.setText("\tNgoài trời");
        pnlMenu.add(btnNgoaiTroi);

        btnPhongVIP.setText("\tPhòng VIP");
        pnlMenu.add(btnPhongVIP);

        btnSanVuon.setText("\tSân vườn");
        pnlMenu.add(btnSanVuon);

        add(pnlMenu, java.awt.BorderLayout.LINE_START);

        pnlLegend.setBackground(new java.awt.Color(250, 250, 250));
        pnlLegend.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(225, 225, 225)));
        pnlLegend.setPreferredSize(new java.awt.Dimension(900, 55));
        pnlLegend.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 10));

        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnThem.setText("Thêm");
        btnThem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThem.setFocusPainted(false);
        pnlLegend.add(btnThem);

        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnSua.setText("Sửa");
        btnSua.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSua.setFocusPainted(false);
        btnSua.addActionListener(this::btnSuaActionPerformed);
        pnlLegend.add(btnSua);

        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXoa.setFocusPainted(false);
        btnXoa.addActionListener(this::btnXoaActionPerformed);
        pnlLegend.add(btnXoa);

        lblColor1.setBackground(new java.awt.Color(46, 139, 87));
        lblColor1.setOpaque(true);
        lblColor1.setPreferredSize(new java.awt.Dimension(22, 22));
        pnlLegend.add(lblColor1);

        lblText1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblText1.setText("Trống");
        lblText1.setPreferredSize(new java.awt.Dimension(60, 20));
        pnlLegend.add(lblText1);

        lblColor2.setBackground(new java.awt.Color(205, 92, 92));
        lblColor2.setOpaque(true);
        lblColor2.setPreferredSize(new java.awt.Dimension(22, 22));
        pnlLegend.add(lblColor2);

        lblText2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblText2.setText("Đang dùng");
        lblText2.setPreferredSize(new java.awt.Dimension(80, 20));
        pnlLegend.add(lblText2);

        lblColor3.setBackground(new java.awt.Color(218, 165, 32));
        lblColor3.setOpaque(true);
        lblColor3.setPreferredSize(new java.awt.Dimension(22, 22));
        pnlLegend.add(lblColor3);

        lblText3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblText3.setText("Đã đặt");
        lblText3.setPreferredSize(new java.awt.Dimension(60, 20));
        pnlLegend.add(lblText3);

        add(pnlLegend, java.awt.BorderLayout.PAGE_END);

        pnlTables.setBackground(new java.awt.Color(255, 255, 255));
        pnlTables.setLayout(new java.awt.BorderLayout());

        scrollBan.setBorder(null);
        scrollBan.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlLuoi.setBackground(new java.awt.Color(255, 255, 255));
        scrollBan.setViewportView(pnlLuoi);

        pnlTables.add(scrollBan, java.awt.BorderLayout.CENTER);

        add(pnlTables, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNgoaiTroi;
    private javax.swing.JButton btnPhongVIP;
    private javax.swing.JButton btnSanVuon;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnTang1;
    private javax.swing.JButton btnTang2;
    private javax.swing.JButton btnTatCa;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblColor1;
    private javax.swing.JLabel lblColor2;
    private javax.swing.JLabel lblColor3;
    private javax.swing.JLabel lblText1;
    private javax.swing.JLabel lblText2;
    private javax.swing.JLabel lblText3;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlLegend;
    private javax.swing.JPanel pnlLuoi;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlTables;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JScrollPane scrollBan;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

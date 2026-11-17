package View;

import DAO.ChiTietHoaDonDAO;
import DAO.HoaDonDAO;
import DAO.MonAnDao;
import Entity.ChiTietHoaDon;
import Entity.HoaDon;
import Entity.MonAn;
import java.awt.Color;
import java.awt.Font;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class LichSuHoaDonPanel extends javax.swing.JPanel {

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO();
    private MonAnDao monAnDAO = new MonAnDao();

    private DefaultTableModel modelLichSu;
    private List<HoaDon> dsHoaDon = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static final String NH_BIN = "970422";      // mã BIN ngân hàng
    private static final String NH_STK = "206442626";  // số tài khoản nhận tiền

    public LichSuHoaDonPanel() {
        initComponents();
        txtPhieuIn.setFont(fontPhieu());
        canGiuaPhieu();
        cauHinhBang();
        taiDanhSachLichSu("");
    }

    private Font fontPhieu() {
        java.util.Set<String> co = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String ten : new String[]{"Consolas", "Courier New"}) {
            if (co.contains(ten)) {
                return new Font(ten, Font.PLAIN, 12);
            }
        }
        return new Font("Monospaced", Font.PLAIN, 12);
    }

    private void canGiuaPhieu() {
        txtPhieuIn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JPanel nenPhieu = new JPanel(new GridBagLayout());
        nenPhieu.setBackground(txtPhieuIn.getBackground());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;
        gc.weightx = 1;
        gc.weighty = 1;
        nenPhieu.add(txtPhieuIn, gc);
        jScrollPane3.setViewportView(nenPhieu);
    }

    // Ghép 1 trường theo chuẩn: mã trường + độ dài (2 chữ số) + giá trị
    private static String tlv(String id, String giaTri) {
        return id + String.format("%02d", giaTri.length()) + giaTri;
    }

    // Mã kiểm tra CRC16 (CCITT) cho chuỗi VietQR
    private static String crc16(String s) {
        int crc = 0xFFFF;
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                crc = ((crc & 0x8000) != 0) ? (crc << 1) ^ 0x1021 : crc << 1;
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }

    // Chuỗi VietQR có số tiền + nội dung chuyển khoản (nội dung viết không dấu)
    private String taoChuoiVietQR(long soTien, String noiDung) {
        String taiKhoan = tlv("00", NH_BIN) + tlv("01", NH_STK);
        String merchant = tlv("00", "A000000727") + tlv("01", taiKhoan) + tlv("02", "QRIBFTTA");
        String s = tlv("00", "01") // phiên bản
                + tlv("01", "12") // 12 = QR động (có số tiền)
                + tlv("38", merchant) // thông tin tài khoản nhận
                + tlv("53", "704") // tiền tệ: VND
                + tlv("54", String.valueOf(soTien)) // SỐ TIỀN của hóa đơn
                + tlv("58", "VN")
                + tlv("62", tlv("08", noiDung)) // nội dung chuyển khoản
                + "6304";
        return s + crc16(s);
    }

    private ImageIcon taoQR(String noiDung, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix m = new QRCodeWriter().encode(noiDung, BarcodeFormat.QR_CODE, size, size, hints);
            return new ImageIcon(MatrixToImageWriter.toBufferedImage(m));
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Chèn QR thanh toán vào cuối phiếu, canh giữa
// Chèn QR vào cuối phiếu, nằm giữa theo bề rộng 42 ký tự (không phụ thuộc bề rộng trang in)
    private void chenQR(JTextPane pane, long soTien, String noiDung) {
        ImageIcon qr = taoQR(taoChuoiVietQR(soTien, noiDung), 160);
        if (qr == null) {
            return;
        }
        try {
            StyledDocument doc = pane.getStyledDocument();

            // số dấu cách cần đệm để QR nằm giữa 42 ký tự
            int rongKyTu = pane.getFontMetrics(pane.getFont()).charWidth(' ');
            int soDem = Math.max(0, (42 * rongKyTu - qr.getIconWidth()) / 2 / rongKyTu);
            String dem = new String(new char[soDem]).replace('\0', ' ');

            doc.insertString(doc.getLength(), "\n", null);      // dòng trống dưới lời cảm ơn
            doc.insertString(doc.getLength(), dem, null);       // đệm bên trái

            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setIcon(attr, qr);
            doc.insertString(doc.getLength(), " ", attr);       // chèn ảnh QR
            doc.insertString(doc.getLength(), "\n", null);

            pane.setCaretPosition(0);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void cauHinhBang() {
        modelLichSu = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Mã HĐ", "Bàn", "Giờ Vào Bàn", "Giờ Thanh Toán", "Giảm Giá", "Thực Thu"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        tblLichSuHoaDon.setModel(modelLichSu);
        tblLichSuHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLichSuHoaDon.setRowHeight(28);
        tblLichSuHoaDon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblLichSuHoaDon.getTableHeader().setBackground(new Color(36, 92, 59));
        tblLichSuHoaDon.getTableHeader().setForeground(Color.WHITE);

        // Sự kiện khi bấm chọn 1 dòng hóa đơn trên bảng -> Hiển thị phiếu in bên phải
        tblLichSuHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiPhieuIn();
            }
        });
    }

    // Hàm gọi để MainFrame cập nhật lại dữ liệu khi chuyển sang Tab này
    public void reloadData() {
        taiDanhSachLichSu(txtTimKiem.getText());
    }

    private void taiDanhSachLichSu(String tuKhoa) {
        modelLichSu.setRowCount(0);
        try {
            dsHoaDon = hoaDonDAO.getLichSuHoaDon(tuKhoa);
        } catch (Exception e) {
            dsHoaDon = new ArrayList<>();
        }

        for (HoaDon hd : dsHoaDon) {
            String gioVao = (hd.getGioVao() != null) ? sdf.format(hd.getGioVao())
                    : (hd.getNgayTao() != null ? sdf.format(hd.getNgayTao()) : "--:--");
            String gioRa = (hd.getGioThanhToan() != null) ? sdf.format(hd.getGioThanhToan())
                    : (hd.getNgayTao() != null ? sdf.format(hd.getNgayTao()) : "--:--");

            modelLichSu.addRow(new Object[]{
                "HD" + hd.getMaHD(),
                "Bàn 0" + hd.getMaBan(),
                gioVao,
                gioRa,
                df.format(hd.getGiamGia()),
                df.format(hd.getThanhTien())
            });
        }

        if (!dsHoaDon.isEmpty()) {
            tblLichSuHoaDon.setRowSelectionInterval(0, 0);
        } else {
            txtPhieuIn.setText("");
        }
    }

    private void hienThiPhieuIn() {
        int row = tblLichSuHoaDon.getSelectedRow();
        if (row < 0 || row >= dsHoaDon.size()) {
            return;
        }

        HoaDon hd = dsHoaDon.get(row);
        List<ChiTietHoaDon> dsMon = cthdDAO.getByMaHD(hd.getMaHD());
        List<MonAn> tatCaMon = monAnDAO.getAll();

        String gioVao = (hd.getGioVao() != null) ? sdf.format(hd.getGioVao())
                : (hd.getNgayTao() != null ? sdf.format(hd.getNgayTao()) : "--:--");
        String gioRa = (hd.getGioThanhToan() != null) ? sdf.format(hd.getGioThanhToan())
                : (hd.getNgayTao() != null ? sdf.format(hd.getNgayTao()) : "--:--");

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("          NHÀ HÀNG ẨM THỰC POS            \n");
        sb.append("   Đ/c: 54 Triều Khúc, Thanh Xuân, HN     \n");
        sb.append("==========================================\n");
        sb.append("Mã HĐ: HD").append(hd.getMaHD()).append("   |   Bàn: Bàn 0").append(hd.getMaBan()).append("\n");
        sb.append("Thu ngân: Quản trị viên (admin)\n");
        sb.append("Giờ khách vào : ").append(gioVao).append("\n");
        sb.append("Giờ thanh toán: ").append(gioRa).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-25s %2s %13s\n", "Món ăn", "SL", "Thành tiền"));
        sb.append("------------------------------------------\n");

        int stt = 1;
        for (ChiTietHoaDon ct : dsMon) {
            String tenMon = "Món #" + ct.getMaMon();
            for (MonAn m : tatCaMon) {
                if (m.getMaMon() == ct.getMaMon()) {
                    tenMon = m.getTenMon();
                    break;
                }
            }
            if (tenMon.length() > 22) {
                tenMon = tenMon.substring(0, 21) + ".";   // tên dài quá thì cắt bớt để khỏi lệch cột
            }
            sb.append(String.format("%-3s%-22s %2d %13s\n",
                    (stt++) + ".", tenMon, ct.getSoLuong(), df.format(ct.getThanhTien())));
        }

        sb.append("------------------------------------------\n");
        sb.append(String.format("%-21s %20s\n", "Tổng tiền món:", df.format(hd.getTongTien())));
        sb.append(String.format("%-21s %20s\n", "Chiết khấu/Giảm giá:", df.format(hd.getGiamGia())));
        sb.append(String.format("%-21s %20s\n", "TỔNG THANH TOÁN:", df.format(hd.getThanhTien())));
        sb.append("==========================================\n");
        sb.append("     Cảm ơn Quý khách & Hẹn gặp lại!      \n");

        txtPhieuIn.setText(sb.toString());

        // Đặt lại canh trái cho toàn phiếu (tránh dính canh giữa của lần chèn QR trước)
        StyledDocument doc = txtPhieuIn.getStyledDocument();
        SimpleAttributeSet trai = new SimpleAttributeSet();
        StyleConstants.setAlignment(trai, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), trai, false);

        // QR thanh toán: quét là hiện đúng số tiền của hóa đơn này
        long soTien = Math.round(((Number) hd.getThanhTien()).doubleValue());
        chenQR(txtPhieuIn, soTien, "Thanh toan HD" + hd.getMaHD());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLeft = new javax.swing.JPanel();
        lblTieuDeTrai = new javax.swing.JLabel();
        lblTim = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        btnHienTatCa = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLichSuHoaDon = new javax.swing.JTable();
        pnlRight = new javax.swing.JPanel();
        lblTieuDePhai = new javax.swing.JLabel();
        btnInHoaDon = new javax.swing.JButton();
        btnXuatTxt = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtPhieuIn = new javax.swing.JTextPane();

        setBackground(new java.awt.Color(255, 255, 255));

        pnlLeft.setBackground(new java.awt.Color(255, 255, 255));
        pnlLeft.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        lblTieuDeTrai.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTieuDeTrai.setForeground(new java.awt.Color(36, 92, 59));
        lblTieuDeTrai.setText("DANH SÁCH LỊCH SỬ HÓA ĐƠN");

        lblTim.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTim.setText("Tìm Mã HĐ / Bàn:");

        txtTimKiem.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        btnTimKiem.setBackground(new java.awt.Color(36, 92, 59));
        btnTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTimKiem.setForeground(new java.awt.Color(255, 255, 255));
        btnTimKiem.setText("Tìm kiếm");
        btnTimKiem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTimKiem.addActionListener(this::btnTimKiemActionPerformed);

        btnHienTatCa.setBackground(new java.awt.Color(108, 117, 125));
        btnHienTatCa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHienTatCa.setForeground(new java.awt.Color(255, 255, 255));
        btnHienTatCa.setText("Hiện tất cả");
        btnHienTatCa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHienTatCa.addActionListener(this::btnHienTatCaActionPerformed);

        tblLichSuHoaDon.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblLichSuHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã HĐ", "Bàn", "Giờ Vào Bàn", "Giờ Thanh Toán", "Giảm Giá", "Thực Thu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLichSuHoaDon.setRowHeight(28);
        tblLichSuHoaDon.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblLichSuHoaDon);

        javax.swing.GroupLayout pnlLeftLayout = new javax.swing.GroupLayout(pnlLeft);
        pnlLeft.setLayout(pnlLeftLayout);
        pnlLeftLayout.setHorizontalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLeftLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlLeftLayout.createSequentialGroup()
                        .addGroup(pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTieuDeTrai)
                            .addGroup(pnlLeftLayout.createSequentialGroup()
                                .addComponent(lblTim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnHienTatCa, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLeftLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTieuDeTrai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTim)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHienTatCa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        pnlRight.setBackground(new java.awt.Color(255, 255, 255));
        pnlRight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        lblTieuDePhai.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblTieuDePhai.setForeground(new java.awt.Color(36, 92, 59));
        lblTieuDePhai.setText("PHIẾU IN HÓA ĐƠN KHÁCH HÀNG");

        btnInHoaDon.setBackground(new java.awt.Color(36, 92, 59));
        btnInHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnInHoaDon.setForeground(new java.awt.Color(255, 255, 255));
        btnInHoaDon.setText("IN HÓA ĐƠN (Print)");
        btnInHoaDon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInHoaDon.addActionListener(this::btnInHoaDonActionPerformed);

        btnXuatTxt.setBackground(new java.awt.Color(0, 123, 255));
        btnXuatTxt.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnXuatTxt.setForeground(new java.awt.Color(255, 255, 255));
        btnXuatTxt.setText("XUẤT FILE .TXT");
        btnXuatTxt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXuatTxt.addActionListener(this::btnXuatTxtActionPerformed);

        txtPhieuIn.setEditable(false);
        txtPhieuIn.setBackground(new java.awt.Color(255, 253, 247));
        txtPhieuIn.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(txtPhieuIn);

        javax.swing.GroupLayout pnlRightLayout = new javax.swing.GroupLayout(pnlRight);
        pnlRight.setLayout(pnlRightLayout);
        pnlRightLayout.setHorizontalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlRightLayout.createSequentialGroup()
                        .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnXuatTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 25, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRightLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTieuDePhai)
                .addGap(58, 58, 58))
        );
        pnlRightLayout.setVerticalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(lblTieuDePhai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXuatTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pnlLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        taiDanhSachLichSu(txtTimKiem.getText());
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnHienTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHienTatCaActionPerformed
        txtTimKiem.setText("");
        taiDanhSachLichSu("");
    }//GEN-LAST:event_btnHienTatCaActionPerformed

    private void btnInHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInHoaDonActionPerformed
        if (txtPhieuIn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trong bảng để in!");
            return;
        }
        try {
            txtPhieuIn.print(); // Gọi hộp thoại máy in / Xuất PDF của Windows
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi in hóa đơn: " + e.getMessage());
        }
    }//GEN-LAST:event_btnInHoaDonActionPerformed

    private void btnXuatTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatTxtActionPerformed
        int row = tblLichSuHoaDon.getSelectedRow();
        if (row < 0 || txtPhieuIn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xuất file .txt!");
            return;
        }
        String maHD = tblLichSuHoaDon.getValueAt(row, 0).toString();
        String tenFile = "PhieuHoaDon_" + maHD + ".txt";
        try (FileWriter fw = new FileWriter(tenFile)) {
            fw.write(txtPhieuIn.getText());
            JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn thành công ra file: " + tenFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + e.getMessage());
        }
    }//GEN-LAST:event_btnXuatTxtActionPerformed

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Demo Module 4.2: Lịch Sử Hóa Đơn & In Hóa Đơn");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new LichSuHoaDonPanel());
            frame.setSize(1050, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHienTatCa;
    private javax.swing.JButton btnInHoaDon;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXuatTxt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTieuDePhai;
    private javax.swing.JLabel lblTieuDeTrai;
    private javax.swing.JLabel lblTim;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JTable tblLichSuHoaDon;
    private javax.swing.JTextPane txtPhieuIn;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}

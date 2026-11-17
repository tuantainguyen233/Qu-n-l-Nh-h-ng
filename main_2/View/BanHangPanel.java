package View;

import DAO.BanAnDAO;
import DAO.ChiTietHoaDonDAO;
import DAO.HoaDonDAO;
import DAO.MonAnDao;
import Entity.BanAn;
import Entity.ChiTietHoaDon;
import Entity.HoaDon;
import Entity.MonAn;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class BanHangPanel extends javax.swing.JPanel {

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO();
    private MonAnDao monAnDAO = new MonAnDao();
    private BanAnDAO banAnDAO = new BanAnDAO();

    private DefaultTableModel modelChiTiet;
    private int maBanHienTai = 1; // Mặc định mở Bàn 01 đầu tiên
    private String tenBanHienTai = "Bàn 01";
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private SimpleDateFormat sdfGio = new SimpleDateFormat("HH:mm (dd/MM/yyyy)");

    private static final String NH_BIN = "970422";
    private static final String NH_STK = "206442626";

    public BanHangPanel() {
        initComponents();

        // 1. Chuyển sang BorderLayout
        this.setLayout(new BorderLayout(15, 0));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 2. Cấu hình thanh cuộn cho jScrollPane3
        jScrollPane3.setPreferredSize(new Dimension(0, 60)); // Độ cao vừa vặn cho nút và scrollbar
        jScrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); // Tắt cuộn dọc
        jScrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Bật cuộn ngang
        jScrollPane3.getHorizontalScrollBar().setUnitIncrement(16); // Cuộn mượt với con lăn chuột

        // 3. Gom jScrollPane3 (thay vì pnlDanhSachBan) và lưới món ăn vào panel bên trái
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setOpaque(false);
        pnlLeft.add(jScrollPane3, BorderLayout.NORTH); // <--- ĐỔI pnlDanhSachBan THÀNH jScrollPane3 Ở ĐÂY
        pnlLeft.add(jScrollPane1, BorderLayout.CENTER);

        // 4. Đặt chiều rộng cố định cho hóa đơn bên phải
        pnlRight.setPreferredSize(new Dimension(320, 0));

        // 5. Gắn vào Panel chính
        this.removeAll();
        this.add(pnlLeft, BorderLayout.CENTER);
        this.add(pnlRight, BorderLayout.EAST);

        // Cấu hình bảng chi tiết hóa đơn
        tblChiTiet.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Món Ăn", "SL", "Đơn Giá", "Thành Tiền"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tblChiTiet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelChiTiet = (DefaultTableModel) tblChiTiet.getModel();

        // Chỉnh font, chiều cao và màu tiêu đề bảng giống hình minh họa
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblChiTiet.setRowHeight(28);
        tblChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblChiTiet.getTableHeader().setBackground(new Color(36, 92, 59));
        tblChiTiet.getTableHeader().setForeground(Color.WHITE);

        javax.swing.table.TableColumnModel colModel = tblChiTiet.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100);
        colModel.getColumn(1).setPreferredWidth(35);
        colModel.getColumn(2).setPreferredWidth(85);
        colModel.getColumn(3).setPreferredWidth(95);

        // Căn giữa cột SL
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        colModel.getColumn(1).setCellRenderer(centerRenderer);

        // Cấu hình layout dạng lưới 3 cột cho danh sách món ăn
        pnlGridMonAn.setLayout(new GridLayout(0, 3, 12, 12));

        // Tự động tính lại Tổng thanh toán khi người dùng thay đổi tiền Giảm giá
        txtGiamGia.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                tinhLaiThanhTien();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                tinhLaiThanhTien();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                tinhLaiThanhTien();
            }
        });

        loadDuLieu();
    }

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

    // Tạo ảnh QR từ một chuỗi nội dung
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

    // Chọn font có đủ dấu tiếng Việt và các chữ rộng bằng nhau (để phiếu không bị lệch cột)
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

    // Chèn QR thanh toán (có sẵn số tiền) vào cuối phiếu, canh giữa
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

    public void loadDuLieu() {
        renderDanhSachBan();
        renderLướiMonAn();
        loadChiTietHoaDon(maBanHienTai);
    }

    private void tinhLaiThanhTien() {
        try {
            HoaDon hd = hoaDonDAO.getHoaDonChuaThanhToan(maBanHienTai);
            if (hd == null) {
                return;
            }

            double tongTien = hd.getTongTien();
            double giamGia = 0;

            String strGiam = txtGiamGia.getText().trim().replace(",", "").replace(".", "");
            if (!strGiam.isEmpty()) {
                giamGia = Double.parseDouble(strGiam);
            }

            if (giamGia > tongTien) {
                giamGia = tongTien;
            }

            double thanhTien = tongTien - giamGia;
            txtThanhTien.setText(df.format(thanhTien));
        } catch (NumberFormatException ignored) {
        }
    }

    // 1. TẠO THANH BÀN ĂN
    public void renderDanhSachBan() {
        pnlDanhSachBan.removeAll();
        // Xếp theo chiều ngang (X_AXIS) để cuộn sang phải
        pnlDanhSachBan.setLayout(new BoxLayout(pnlDanhSachBan, BoxLayout.X_AXIS));
        pnlDanhSachBan.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        List<BanAn> listBan;
        try {
            listBan = banAnDAO.getAll();
        } catch (Exception e) {
            listBan = new ArrayList<>();
        }

        if (listBan == null || listBan.isEmpty()) {
            listBan = new ArrayList<>();
            listBan.add(new BanAn(1, "Bàn 01", 1, 4, "Trống"));
            listBan.add(new BanAn(2, "Bàn 02", 1, 4, "Đang dùng"));
            listBan.add(new BanAn(3, "Bàn 03", 2, 6, "Đã đặt"));
            listBan.add(new BanAn(4, "Bàn VIP 01", 4, 8, "Trống"));
        }

        for (BanAn ban : listBan) {
            JButton btnBan = new JButton(ban.getTenBan() + " (" + ban.getTrangThai() + ")");
            btnBan.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnBan.setOpaque(true);
            btnBan.setContentAreaFilled(true);
            btnBan.setFocusPainted(false);
            btnBan.setForeground(Color.BLACK);
            btnBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnBan.setMargin(new Insets(2, 6, 2, 6)); // Tạo lề thoáng cho chữ

            // Đặt kích thước chuẩn để các nút bằng nhau và không bị co méo trong BoxLayout
            Dimension btnSize = new Dimension(120, 30);
            btnBan.setPreferredSize(btnSize);
            btnBan.setMinimumSize(btnSize);
            btnBan.setMaximumSize(btnSize);

            // Đổi màu theo trạng thái bàn
            if ("Trống".equalsIgnoreCase(ban.getTrangThai())) {
                btnBan.setBackground(new Color(144, 238, 144));
            } else if ("Đang dùng".equalsIgnoreCase(ban.getTrangThai())) {
                btnBan.setBackground(new Color(255, 160, 122));
            } else {
                btnBan.setBackground(new Color(255, 236, 139));
            }

            // Bàn đang chọn có viền đen đậm
            if (ban.getMaBan() == maBanHienTai) {
                btnBan.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            } else {
                btnBan.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
            }

            btnBan.addActionListener(e -> {
                maBanHienTai = ban.getMaBan();
                tenBanHienTai = ban.getTenBan();
                renderDanhSachBan();
                loadChiTietHoaDon(maBanHienTai);
            });

            // 1. Thêm nút bàn vào panel
            pnlDanhSachBan.add(btnBan);

            // 2. Thêm khoảng trống 10px giữa các nút để không bị dính liền vào nhau
            pnlDanhSachBan.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        pnlDanhSachBan.revalidate();
        pnlDanhSachBan.repaint();
    }

    // Hàm gọi để MainFrame cập nhật lại khi chuyển Tab
    public void reloadData() {
        loadDuLieu();
    }

    // 2. VẼ LƯỚI THẺ MÓN ĂN
    public void renderLướiMonAn() {
        pnlGridMonAn.removeAll();
        List<MonAn> listMon;
        try {
            listMon = monAnDAO.getAll();
        } catch (Exception e) {
            listMon = new ArrayList<>();
        }

        if (listMon != null) {
            for (MonAn mon : listMon) {
                if (!"Còn món".equalsIgnoreCase(mon.getTrangThai())) {
                    continue;
                }

                JPanel card = new JPanel(new BorderLayout(5, 5));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(6, 6, 6, 6)
                ));

                JLabel lblImage = new JLabel();
                lblImage.setPreferredSize(new Dimension(140, 95));
                lblImage.setHorizontalAlignment(SwingConstants.CENTER);
                lblImage.setOpaque(true);
                lblImage.setBackground(new Color(245, 245, 245));

                URL imgUrl = getClass().getResource("/images/" + mon.getMaMon() + ".jpg");
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    Image img = icon.getImage().getScaledInstance(140, 95, Image.SCALE_SMOOTH);
                    lblImage.setIcon(new ImageIcon(img));
                } else {
                    lblImage.setText("<html><center><b>" + mon.getTenMon() + "</b></center></html>");
                }

                JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 2, 2));
                pnlInfo.setBackground(Color.WHITE);

                JLabel lblTen = new JLabel(mon.getTenMon());
                lblTen.setFont(new Font("Segoe UI", Font.BOLD, 13));

                double giaTien = 0;
                try {
                    giaTien = mon.getDonGia().doubleValue();
                } catch (Exception e) {
                }

                JLabel lblGia = new JLabel(df.format(giaTien));
                lblGia.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblGia.setForeground(new Color(40, 167, 69));

                pnlInfo.add(lblTen);
                pnlInfo.add(lblGia);

                card.add(lblImage, BorderLayout.CENTER);
                card.add(pnlInfo, BorderLayout.SOUTH);

                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        themMonVaoHoaDon(mon);
                    }
                });

                pnlGridMonAn.add(card);
            }
        }
        pnlGridMonAn.revalidate();
        pnlGridMonAn.repaint();
    }

    // 3. XỬ LÝ GỌI MÓN VÀ CẬP NHẬT DATABASE (Tự động mở bàn & lưu Giờ Vào)
    private void themMonVaoHoaDon(MonAn mon) {
        HoaDon hd = hoaDonDAO.getHoaDonChuaThanhToan(maBanHienTai);
        if (hd == null) {
            HoaDon newHD = new HoaDon(0, maBanHienTai, 1, null, 0, 0, "Chưa thanh toán");
            hoaDonDAO.insert(newHD);
            hd = hoaDonDAO.getHoaDonChuaThanhToan(maBanHienTai);
        }

        double giaTien = mon.getDonGia() != null ? mon.getDonGia().doubleValue() : 0;
        ChiTietHoaDon cthd = new ChiTietHoaDon(hd.getMaHD(), mon.getMaMon(), 1, giaTien);
        cthdDAO.insertOrUpdate(cthd);

        loadChiTietHoaDon(maBanHienTai);
        renderDanhSachBan();
    }

    // 4. HIỂN THỊ CHI TIẾT HÓA ĐƠN & GIỜ KHÁCH VÀO BÀN
    public void loadChiTietHoaDon(int maBan) {
        modelChiTiet.setRowCount(0);
        HoaDon hd = hoaDonDAO.getHoaDonChuaThanhToan(maBan);

        if (hd != null) {
            lblTieuDeHD.setText("HÓA ĐƠN - " + tenBanHienTai.toUpperCase() + " (Mã HĐ: HD" + hd.getMaHD() + ")");
            Date thoiGianVao = hd.getGioVao() != null ? hd.getGioVao() : hd.getNgayTao();
            String strGioVao = (thoiGianVao != null) ? sdfGio.format(thoiGianVao) : "Đang mở";
            lblGioVao.setText("Giờ vào: " + strGioVao + "  |  Giờ ra: Đang phục vụ");

            List<ChiTietHoaDon> list = cthdDAO.getByMaHD(hd.getMaHD());
            List<MonAn> listMon = monAnDAO.getAll();

            for (ChiTietHoaDon ct : list) {
                String tenMon = "Mã " + ct.getMaMon();
                for (MonAn m : listMon) {
                    if (m.getMaMon() == ct.getMaMon()) {
                        tenMon = m.getTenMon();
                        break;
                    }
                }
                modelChiTiet.addRow(new Object[]{
                    tenMon,
                    ct.getSoLuong(),
                    df.format(ct.getDonGia()),
                    df.format(ct.getThanhTien())
                });
            }

            // Đọc lại hóa đơn sau khi Trigger SQL tính tổng tiền
            hd = hoaDonDAO.getById(hd.getMaHD());
            txtTongTien.setText(df.format(hd.getTongTien()));
            tinhLaiThanhTien();
        } else {
            lblTieuDeHD.setText("HÓA ĐƠN - " + tenBanHienTai.toUpperCase());
            lblGioVao.setText("Giờ vào: Chưa mở bàn  |  Giờ ra: --:--");
            txtTongTien.setText("0 VNĐ");
            txtGiamGia.setText("0");
            txtThanhTien.setText("0 VNĐ");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlGridMonAn = new javax.swing.JPanel();
        pnlRight = new javax.swing.JPanel();
        lblTieuDeHD = new javax.swing.JLabel();
        lblGioVao = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChiTiet = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtTongTien = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtGiamGia = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtThanhTien = new javax.swing.JTextField();
        btnXoaMon = new javax.swing.JButton();
        btnInPhieu = new javax.swing.JButton();
        btnThanhToan = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlDanhSachBan = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        pnlGridMonAn.setBackground(new java.awt.Color(255, 255, 255));
        pnlGridMonAn.setLayout(new java.awt.GridLayout(1, 0));
        jScrollPane1.setViewportView(pnlGridMonAn);

        pnlRight.setBackground(new java.awt.Color(255, 255, 255));
        pnlRight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        lblTieuDeHD.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblTieuDeHD.setForeground(new java.awt.Color(36, 92, 59));
        lblTieuDeHD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTieuDeHD.setText("HÓA ĐƠN - BÀN 01");

        lblGioVao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblGioVao.setForeground(new java.awt.Color(210, 45, 45));
        lblGioVao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGioVao.setText("Giờ vào: Chưa mở bàn  |  Giờ ra: --:--");

        tblChiTiet.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblChiTiet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Món Ăn", "SL", "Đơn Giá", "Thành Tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblChiTiet.setRowHeight(28);
        tblChiTiet.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tblChiTiet);
        if (tblChiTiet.getColumnModel().getColumnCount() > 0) {
            tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(100);
            tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(35);
            tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(80);
            tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(90);
        }

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel1.setText("Tổng tiền:");

        txtTongTien.setEditable(false);
        txtTongTien.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txtTongTien.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel2.setText("Giảm giá (VNĐ):");

        txtGiamGia.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txtGiamGia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel3.setText("Khách cần trả:");

        txtThanhTien.setEditable(false);
        txtThanhTien.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        txtThanhTien.setForeground(new java.awt.Color(210, 45, 45));
        txtThanhTien.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        btnXoaMon.setBackground(new java.awt.Color(255, 102, 102));
        btnXoaMon.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoaMon.setForeground(new java.awt.Color(255, 255, 255));
        btnXoaMon.setText("Xóa món đã chọn");
        btnXoaMon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXoaMon.addActionListener(this::btnXoaMonActionPerformed);

        btnInPhieu.setBackground(new java.awt.Color(23, 162, 184));
        btnInPhieu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnInPhieu.setForeground(new java.awt.Color(255, 255, 255));
        btnInPhieu.setText("XEM & IN PHIẾU HÓA ĐƠN ");
        btnInPhieu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInPhieu.addActionListener(this::btnInPhieuActionPerformed);

        btnThanhToan.setBackground(new java.awt.Color(40, 167, 69));
        btnThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnThanhToan.setForeground(new java.awt.Color(255, 255, 255));
        btnThanhToan.setText("THANH TOÁN");
        btnThanhToan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThanhToan.addActionListener(this::btnThanhToanActionPerformed);

        javax.swing.GroupLayout pnlRightLayout = new javax.swing.GroupLayout(pnlRight);
        pnlRight.setLayout(pnlRightLayout);
        pnlRightLayout.setHorizontalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTieuDeHD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGioVao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnInPhieu, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addComponent(btnThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRightLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnXoaMon, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRightLayout.createSequentialGroup()
                        .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTongTien, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                            .addComponent(txtGiamGia)
                            .addComponent(txtThanhTien))))
                .addContainerGap())
        );
        pnlRightLayout.setVerticalGroup(
            pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblTieuDeHD)
                .addGap(4, 4, 4)
                .addComponent(lblGioVao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(btnXoaMon, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtThanhTien, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnInPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(0, 65));

        pnlDanhSachBan.setBackground(new java.awt.Color(255, 255, 255));
        pnlDanhSachBan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));
        pnlDanhSachBan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 8));
        jScrollPane3.setViewportView(pnlDanhSachBan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(pnlRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1)))
                .addGap(10, 10, 10))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnInPhieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInPhieuActionPerformed
        HoaDon hd = hoaDonDAO.getHoaDonChuaThanhToan(maBanHienTai);
        if (hd == null || modelChiTiet.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Bàn này hiện chưa có món ăn nào để in phiếu!");
            return;
        }

        Date thoiGianVao = hd.getGioVao() != null ? hd.getGioVao() : hd.getNgayTao();
        String strGioVao = (thoiGianVao != null) ? sdfGio.format(thoiGianVao) : sdfGio.format(new Date());

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("                NHÀ HÀNG            \n");
        sb.append("   Đ/c: 54 Triều Khúc, Thanh Xuân, HN     \n");
        sb.append("==========================================\n");
        sb.append("Mã HĐ: HD").append(hd.getMaHD()).append("   |   Bàn: ").append(tenBanHienTai).append("\n");
        sb.append("Giờ khách vào : ").append(strGioVao).append("\n");
        sb.append("Giờ thanh toán: Đang phục vụ (Tạm tính)\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-25s %2s %13s\n", "Món ăn", "SL", "Thành tiền"));
        sb.append("------------------------------------------\n");

        for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
            String tenMon = modelChiTiet.getValueAt(i, 0).toString();
            if (tenMon.length() > 22) {
                tenMon = tenMon.substring(0, 21) + ".";   // tên dài quá thì cắt bớt để khỏi lệch cột
            }
            String sl = modelChiTiet.getValueAt(i, 1).toString();
            String thanhTien = modelChiTiet.getValueAt(i, 3).toString();
            sb.append(String.format("%-3s%-22s %2s %13s\n", (i + 1) + ".", tenMon, sl, thanhTien));
        }

        sb.append("------------------------------------------\n");
        String giam = txtGiamGia.getText().trim();
        if (giam.isEmpty()) {
            giam = "0";                                    // ô giảm giá trống thì ghi 0 cho đẹp
        }
        sb.append(String.format("%-21s %20s\n", "Tổng tiền món:", txtTongTien.getText()));
        sb.append(String.format("%-21s %20s\n", "Giảm giá:", giam + " VNĐ"));
        sb.append(String.format("%-21s %20s\n", "KHÁCH CẦN TRẢ:", txtThanhTien.getText()));
        sb.append("==========================================\n");
        sb.append("     Cảm ơn Quý khách & Hẹn gặp lại!      \n");

        // Dùng JTextPane (thay JTextArea) để chèn được ảnh QR vào trong phiếu
        JTextPane txtBill = new JTextPane();
        txtBill.setText(sb.toString());
        txtBill.setFont(fontPhieu());
        txtBill.setBackground(new Color(255, 253, 247));
        txtBill.setEditable(false);

        // Số tiền trên QR = đúng số "Khách cần trả" đang hiện trên màn hình (đã trừ giảm giá)
        long soTien = 0;
        try {
            soTien = Long.parseLong(txtThanhTien.getText().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException ignored) {
        }
        if (soTien > 0) {
            chenQR(txtBill, soTien, "Thanh toan HD" + hd.getMaHD());
        }

        // Đặt phiếu vào panel GridBagLayout để cả khối phiếu nằm giữa chiều ngang
        txtBill.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JPanel nenPhieu = new JPanel(new GridBagLayout());
        nenPhieu.setBackground(new Color(255, 253, 247));
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;   // giữa theo chiều ngang, sát mép trên
        gc.weightx = 1;
        gc.weighty = 1;
        nenPhieu.add(txtBill, gc);

        JScrollPane spBill = new JScrollPane(nenPhieu);
        spBill.setPreferredSize(new Dimension(430, 560));
        spBill.getVerticalScrollBar().setUnitIncrement(16);

        Object[] options = {"In Phiếu (Print)", "Đóng"};
        int chon = JOptionPane.showOptionDialog(
                this,
                spBill,
                "Phiếu Hóa Đơn Khách Hàng - " + tenBanHienTai,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (chon == JOptionPane.YES_OPTION) {
            try {
                txtBill.print();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi in phiếu: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnInPhieuActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        HoaDon hd = hoaDonDAO.getHoaDonChuaThanhToan(maBanHienTai);
        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Bàn này hiện chưa có món ăn nào để thanh toán!");
            return;
        }

        int hoi = JOptionPane.showConfirmDialog(this,
                "Xác nhận thanh toán cho " + tenBanHienTai + " với tổng số tiền " + txtThanhTien.getText() + "?",
                "Xác Nhận Thanh Toán",
                JOptionPane.YES_NO_OPTION);

        if (hoi != JOptionPane.YES_OPTION) {
            return;
        }

        double giamGia = 0;
        try {
            giamGia = Double.parseDouble(txtGiamGia.getText().replace(",", "").replace(".", "").replace(" VNĐ", "").trim());
        } catch (Exception ignored) {
        }

        hd.setGiamGia(giamGia);
        hd.setTrangThai("Đã thanh toán");

        if (hoaDonDAO.update(hd)) {
            // Đưa trạng thái bàn về "Trống"
            BanAn ban = banAnDAO.getAll().stream()
                    .filter(b -> b.getMaBan() == maBanHienTai)
                    .findFirst().orElse(null);
            if (ban != null) {
                ban.setTrangThai("Trống");
                banAnDAO.update(ban);
            }

            JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nĐã chốt giờ thanh toán và chuyển bàn về trạng thái Trống.");
            renderDanhSachBan();
            loadChiTietHoaDon(maBanHienTai);
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại, vui lòng kiểm tra lại CSDL!");
        }
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void btnXoaMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaMonActionPerformed
        int selectedRow = tblChiTiet.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần xóa trong bảng hóa đơn!");
            return;
        }

        HoaDon hd = hoaDonDAO.getHoaDonChuaThanhToan(maBanHienTai);
        if (hd == null) {
            return;
        }

        String tenMon = tblChiTiet.getValueAt(selectedRow, 0).toString();

        List<MonAn> listMon = monAnDAO.getAll();
        int maMon = -1;
        for (MonAn m : listMon) {
            if (m.getTenMon().equalsIgnoreCase(tenMon)) {
                maMon = m.getMaMon();
                break;
            }
        }

        if (maMon == -1) {
            return;
        }

        int hoi = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa món [" + tenMon + "] khỏi hóa đơn bàn này?",
                "Xác Nhận Xóa Món",
                JOptionPane.YES_NO_OPTION);

        if (hoi != JOptionPane.YES_OPTION) {
            return;
        }

        if (cthdDAO.delete(hd.getMaHD(), maMon)) {
            JOptionPane.showMessageDialog(this, "Đã xóa món [" + tenMon + "] thành công!");

            List<ChiTietHoaDon> listConLai = cthdDAO.getByMaHD(hd.getMaHD());
            if (listConLai == null || listConLai.isEmpty()) {
                hoaDonDAO.delete(String.valueOf(hd.getMaHD()));
                BanAn ban = banAnDAO.getAll().stream()
                        .filter(b -> b.getMaBan() == maBanHienTai)
                        .findFirst().orElse(null);
                if (ban != null) {
                    ban.setTrangThai("Trống");
                    banAnDAO.update(ban);
                }
            }

            loadChiTietHoaDon(maBanHienTai);
            renderDanhSachBan();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa món thất bại!");
        }
    }//GEN-LAST:event_btnXoaMonActionPerformed

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Demo Module 4.1: Bán Hàng POS");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new BanHangPanel());
            frame.setSize(1080, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInPhieu;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnXoaMon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblGioVao;
    private javax.swing.JLabel lblTieuDeHD;
    private javax.swing.JPanel pnlDanhSachBan;
    private javax.swing.JPanel pnlGridMonAn;
    private javax.swing.JPanel pnlRight;
    private javax.swing.JTable tblChiTiet;
    private javax.swing.JTextField txtGiamGia;
    private javax.swing.JTextField txtThanhTien;
    private javax.swing.JTextField txtTongTien;
    // End of variables declaration//GEN-END:variables
}

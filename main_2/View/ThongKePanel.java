package View;

import CONN.MyConnection;
import DAO.BanAnDAO;
import DAO.HoaDonDAO;
import Entity.BanAn;
import Entity.HoaDon;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class ThongKePanel extends javax.swing.JPanel {

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private BanAnDAO banAnDAO = new BanAnDAO();

    private DefaultTableModel modelTopMon;
    private DefaultTableModel modelTongHopNgay;
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");
    private SimpleDateFormat sdfGio = new SimpleDateFormat("HH:mm");

    public ThongKePanel() {
        initComponents();
        cauHinhBang();
        reloadData();
    }

    private void cauHinhBang() {
        // 1. Cấu hình bảng Top món ăn bán chạy (Bên trái)
        modelTopMon = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Hạng", "Tên Món Ăn", "Số Lượng Bán", "Doanh Thu"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTopMonBanChay.setModel(modelTopMon);
        tblTopMonBanChay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTopMonBanChay.setRowHeight(28);
        tblTopMonBanChay.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTopMonBanChay.getTableHeader().setBackground(new Color(36, 92, 59));
        tblTopMonBanChay.getTableHeader().setForeground(Color.WHITE);

        // 2. Cấu hình bảng Tổng hợp hóa đơn cả ngày theo Giờ Vào - Giờ Ra (Bên phải)
        modelTongHopNgay = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Mã HĐ", "Bàn", "Giờ Vào", "Giờ Ra", "Giảm Giá", "Thực Thu"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTongHopNgay.setModel(modelTongHopNgay);
        tblTongHopNgay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTongHopNgay.setRowHeight(28);
        tblTongHopNgay.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTongHopNgay.getTableHeader().setBackground(new Color(36, 92, 59));
        tblTongHopNgay.getTableHeader().setForeground(Color.WHITE);
    }

    // Hàm gọi để MainFrame cập nhật lại dữ liệu khi chuyển sang Tab này
    public void reloadData() {
        taiTongHopHoaDonTrongNgay();
        taiTopMonBanChay();
        taiSoBanDangPhucVu();
        taiDoanhThuThang();
    }

    // 1. Đổ dữ liệu Bảng Tổng hợp hóa đơn cả ngày (Giờ vào - Giờ ra) & Doanh thu hôm nay
    private void taiTongHopHoaDonTrongNgay() {
        modelTongHopNgay.setRowCount(0);
        List<HoaDon> dsHomNay;
        try {
            dsHomNay = hoaDonDAO.getHoaDonTrongNgay();
        } catch (Exception e) {
            dsHomNay = new ArrayList<>();
        }

        double tongThucThu = 0;
        double tongGiamGia = 0;

        for (HoaDon hd : dsHomNay) {
            String gioVao = (hd.getGioVao() != null) ? sdfGio.format(hd.getGioVao())
                    : (hd.getNgayTao() != null ? sdfGio.format(hd.getNgayTao()) : "--:--");
            String gioRa = (hd.getGioThanhToan() != null) ? sdfGio.format(hd.getGioThanhToan())
                    : (hd.getNgayTao() != null ? sdfGio.format(hd.getNgayTao()) : "--:--");

            tongThucThu += hd.getThanhTien();
            tongGiamGia += hd.getGiamGia();

            modelTongHopNgay.addRow(new Object[]{
                "HD" + hd.getMaHD(),
                "Bàn 0" + hd.getMaBan(),
                gioVao,
                gioRa,
                df.format(hd.getGiamGia()),
                df.format(hd.getThanhTien())
            });
        }

        // Thêm dòng TỔNG CỘNG cuối ngày
        modelTongHopNgay.addRow(new Object[]{
            "TỔNG",
            dsHomNay.size() + " HĐ",
            "Cả ngày",
            "Hôm nay",
            df.format(tongGiamGia),
            df.format(tongThucThu)
        });

        lblDoanhThuHomNay.setText(df.format(tongThucThu));
        lblTongSoHoaDon.setText(dsHomNay.size() + " Hóa đơn");
    }

    // 2. Đổ dữ liệu Top món ăn bán chạy trong kỳ
    private void taiTopMonBanChay() {
        modelTopMon.setRowCount(0);
        String sql = "SELECT TOP 10 m.TenMon, SUM(ct.SoLuong) AS TongSL, SUM(ct.SoLuong * ct.DonGia) AS DoanhThu "
                   + "FROM CHITIETHOADON ct JOIN MONAN m ON ct.MaMon = m.MaMon "
                   + "GROUP BY m.TenMon ORDER BY TongSL DESC";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int hang = 1;
            while (rs.next()) {
                modelTopMon.addRow(new Object[]{
                    hang++,
                    rs.getString("TenMon"),
                    rs.getInt("TongSL") + " phần",
                    df.format(rs.getDouble("DoanhThu"))
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Đếm số bàn đang phục vụ / tổng số bàn
    private void taiSoBanDangPhucVu() {
        try {
            List<BanAn> listBan = banAnDAO.getAll();
            int dangDung = 0;
            for (BanAn b : listBan) {
                if ("Đang dùng".equalsIgnoreCase(b.getTrangThai())) {
                    dangDung++;
                }
            }
            lblBanDangPhucVu.setText(dangDung + " / " + listBan.size() + " Bàn");
        } catch (Exception e) {
            lblBanDangPhucVu.setText("0 / 0 Bàn");
        }
    }

    // 4. Tính tổng doanh thu trong tháng hiện tại
    private void taiDoanhThuThang() {
        String sql = "SELECT ISNULL(SUM(TongTien - GiamGia), 0) AS DoanhThuThang "
                   + "FROM HOADON WHERE TrangThai = N'Đã thanh toán' "
                   + "AND MONTH(NgayTao) = MONTH(GETDATE()) AND YEAR(NgayTao) = YEAR(GETDATE())";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                lblDoanhThuThang.setText(df.format(rs.getDouble("DoanhThuThang")));
            }
        } catch (Exception e) {
            lblDoanhThuThang.setText("0 VNĐ");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTopCards = new javax.swing.JPanel();
        pnlCard1 = new javax.swing.JPanel();
        lblTitleCard1 = new javax.swing.JLabel();
        lblDoanhThuHomNay = new javax.swing.JLabel();
        pnlCard2 = new javax.swing.JPanel();
        lblTitleCard2 = new javax.swing.JLabel();
        lblTongSoHoaDon = new javax.swing.JLabel();
        pnlCard3 = new javax.swing.JPanel();
        lblTitleCard3 = new javax.swing.JLabel();
        lblBanDangPhucVu = new javax.swing.JLabel();
        pnlCard4 = new javax.swing.JPanel();
        lblTitleCard4 = new javax.swing.JLabel();
        lblDoanhThuThang = new javax.swing.JLabel();
        pnlLeftTable = new javax.swing.JPanel();
        lblTopMon = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTopMonBanChay = new javax.swing.JTable();
        pnlRightTable = new javax.swing.JPanel();
        lblTongHopNgay = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTongHopNgay = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        pnlTopCards.setBackground(new java.awt.Color(255, 255, 255));
        pnlTopCards.setLayout(new java.awt.GridLayout(1, 4, 14, 0));

        pnlCard1.setBackground(new java.awt.Color(255, 255, 255));
        pnlCard1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(40, 167, 69), 2));
        pnlCard1.setLayout(new java.awt.BorderLayout());

        lblTitleCard1.setBackground(new java.awt.Color(40, 167, 69));
        lblTitleCard1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTitleCard1.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleCard1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitleCard1.setText("DOANH THU HÔM NAY");
        lblTitleCard1.setOpaque(true);
        lblTitleCard1.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlCard1.add(lblTitleCard1, java.awt.BorderLayout.PAGE_START);

        lblDoanhThuHomNay.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblDoanhThuHomNay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoanhThuHomNay.setText("0 VNĐ");
        pnlCard1.add(lblDoanhThuHomNay, java.awt.BorderLayout.CENTER);

        pnlTopCards.add(pnlCard1);

        pnlCard2.setBackground(new java.awt.Color(255, 255, 255));
        pnlCard2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 162, 184), 2));
        pnlCard2.setLayout(new java.awt.BorderLayout());

        lblTitleCard2.setBackground(new java.awt.Color(23, 162, 184));
        lblTitleCard2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTitleCard2.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleCard2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitleCard2.setText("TỔNG SỐ HÓA ĐƠN");
        lblTitleCard2.setOpaque(true);
        lblTitleCard2.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlCard2.add(lblTitleCard2, java.awt.BorderLayout.PAGE_START);

        lblTongSoHoaDon.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTongSoHoaDon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTongSoHoaDon.setText("0 Hóa đơn");
        pnlCard2.add(lblTongSoHoaDon, java.awt.BorderLayout.CENTER);

        pnlTopCards.add(pnlCard2);

        pnlCard3.setBackground(new java.awt.Color(255, 255, 255));
        pnlCard3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 193, 7), 2));
        pnlCard3.setLayout(new java.awt.BorderLayout());

        lblTitleCard3.setBackground(new java.awt.Color(255, 193, 7));
        lblTitleCard3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTitleCard3.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleCard3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitleCard3.setText("BÀN ĐANG PHỤC VỤ");
        lblTitleCard3.setOpaque(true);
        lblTitleCard3.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlCard3.add(lblTitleCard3, java.awt.BorderLayout.PAGE_START);

        lblBanDangPhucVu.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblBanDangPhucVu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBanDangPhucVu.setText("0 / 0 Bàn");
        pnlCard3.add(lblBanDangPhucVu, java.awt.BorderLayout.CENTER);

        pnlTopCards.add(pnlCard3);

        pnlCard4.setBackground(new java.awt.Color(255, 255, 255));
        pnlCard4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(36, 92, 59), 2));
        pnlCard4.setLayout(new java.awt.BorderLayout());

        lblTitleCard4.setBackground(new java.awt.Color(36, 92, 59));
        lblTitleCard4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTitleCard4.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleCard4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitleCard4.setText("DOANH THU THÁNG NÀY");
        lblTitleCard4.setOpaque(true);
        lblTitleCard4.setPreferredSize(new java.awt.Dimension(100, 30));
        pnlCard4.add(lblTitleCard4, java.awt.BorderLayout.PAGE_START);

        lblDoanhThuThang.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblDoanhThuThang.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoanhThuThang.setText("0 VNĐ");
        pnlCard4.add(lblDoanhThuThang, java.awt.BorderLayout.CENTER);

        pnlTopCards.add(pnlCard4);

        pnlLeftTable.setBackground(new java.awt.Color(255, 255, 255));
        pnlLeftTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        lblTopMon.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTopMon.setForeground(new java.awt.Color(36, 92, 59));
        lblTopMon.setText("TOP MÓN ĂN BÁN CHẠY TRONG KỲ");

        tblTopMonBanChay.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblTopMonBanChay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Hạng", "Tên Món Ăn", "Số Lượng Bán", "Doanh Thu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTopMonBanChay.setRowHeight(28);
        tblTopMonBanChay.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblTopMonBanChay);

        javax.swing.GroupLayout pnlLeftTableLayout = new javax.swing.GroupLayout(pnlLeftTable);
        pnlLeftTable.setLayout(pnlLeftTableLayout);
        pnlLeftTableLayout.setHorizontalGroup(
            pnlLeftTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLeftTableLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlLeftTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .addGroup(pnlLeftTableLayout.createSequentialGroup()
                        .addComponent(lblTopMon)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        pnlLeftTableLayout.setVerticalGroup(
            pnlLeftTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLeftTableLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblTopMon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        pnlRightTable.setBackground(new java.awt.Color(255, 255, 255));
        pnlRightTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));

        lblTongHopNgay.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTongHopNgay.setForeground(new java.awt.Color(36, 92, 59));
        lblTongHopNgay.setText("TỔNG HỢP HÓA ĐƠN CẢ NGÀY (GIỜ VÀO - GIỜ RA)");

        tblTongHopNgay.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblTongHopNgay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã HĐ", "Bàn", "Giờ Vào", "Giờ Ra", "Giảm Giá", "Thực Thu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTongHopNgay.setRowHeight(28);
        tblTongHopNgay.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tblTongHopNgay);

        javax.swing.GroupLayout pnlRightTableLayout = new javax.swing.GroupLayout(pnlRightTable);
        pnlRightTable.setLayout(pnlRightTableLayout);
        pnlRightTableLayout.setHorizontalGroup(
            pnlRightTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightTableLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlRightTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(pnlRightTableLayout.createSequentialGroup()
                        .addComponent(lblTongHopNgay)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        pnlRightTableLayout.setVerticalGroup(
            pnlRightTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRightTableLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblTongHopNgay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTopCards, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlLeftTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlRightTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pnlTopCards, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlLeftTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlRightTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Demo Module 4.3: Tổng Hợp Hóa Đơn & Thống Kê Doanh Thu");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ThongKePanel());
            frame.setSize(1050, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBanDangPhucVu;
    private javax.swing.JLabel lblDoanhThuHomNay;
    private javax.swing.JLabel lblDoanhThuThang;
    private javax.swing.JLabel lblTitleCard1;
    private javax.swing.JLabel lblTitleCard2;
    private javax.swing.JLabel lblTitleCard3;
    private javax.swing.JLabel lblTitleCard4;
    private javax.swing.JLabel lblTongHopNgay;
    private javax.swing.JLabel lblTongSoHoaDon;
    private javax.swing.JLabel lblTopMon;
    private javax.swing.JPanel pnlCard1;
    private javax.swing.JPanel pnlCard2;
    private javax.swing.JPanel pnlCard3;
    private javax.swing.JPanel pnlCard4;
    private javax.swing.JPanel pnlLeftTable;
    private javax.swing.JPanel pnlRightTable;
    private javax.swing.JPanel pnlTopCards;
    private javax.swing.JTable tblTongHopNgay;
    private javax.swing.JTable tblTopMonBanChay;
    // End of variables declaration//GEN-END:variables
}

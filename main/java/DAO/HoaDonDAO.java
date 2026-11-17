package DAO;

import Entity.HoaDon;
import CONN.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO implements Dao<HoaDon> {

    // Hàm dùng chung để đọc 1 dòng ResultSet ra đối tượng HoaDon (có cả GioVao và GioThanhToan)
    private HoaDon docHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon(
            rs.getInt("MaHD"),
            rs.getInt("MaBan"),
            rs.getInt("MaNV"),
            rs.getTimestamp("NgayTao"),
            rs.getDouble("TongTien"),
            rs.getDouble("GiamGia"),
            rs.getString("TrangThai")
        );
        try {
            hd.setGioVao(rs.getTimestamp("GioVao"));
            hd.setGioThanhToan(rs.getTimestamp("GioThanhToan"));
        } catch (SQLException ignored) {
            // Phòng trường hợp CSDL chưa kịp chạy lệnh ALTER TABLE thêm cột GioVao, GioThanhToan
        }
        return hd;
    }

    @Override
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON ORDER BY NgayTao DESC";
        try (Connection con = MyConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(docHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(HoaDon hd) {
        // Tự động lưu NgayTao = GETDATE() và GioVao = GETDATE() khi khách bắt đầu vào bàn gọi món
        String sql = "INSERT INTO HOADON (MaBan, MaNV, NgayTao, GioVao, TongTien, GiamGia, TrangThai) "
                   + "VALUES (?, ?, GETDATE(), GETDATE(), ?, ?, ?)";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hd.getMaBan());
            ps.setInt(2, hd.getMaNV());
            ps.setDouble(3, hd.getTongTien());
            ps.setDouble(4, hd.getGiamGia());
            ps.setString(5, hd.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(HoaDon hd) {
        // Tự động chốt GioThanhToan = GETDATE() khi khách thanh toán xong
        String sql = "UPDATE HOADON SET GiamGia = ?, GioThanhToan = GETDATE(), TrangThai = ? WHERE MaHD = ?";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, hd.getGiamGia());
            ps.setString(2, hd.getTrangThai());
            ps.setInt(3, hd.getMaHD());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM HOADON WHERE MaHD = ?";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<HoaDon> search(String keyword) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE TrangThai LIKE ? OR CAST(MaHD AS VARCHAR) LIKE ?";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(docHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Nghiệp vụ: Lấy hóa đơn "Chưa thanh toán" của bàn
    public HoaDon getHoaDonChuaThanhToan(int maBan) {
        String sql = "SELECT * FROM HOADON WHERE MaBan = ? AND TrangThai = N'Chưa thanh toán'";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maBan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return docHoaDon(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HoaDon getById(int maHD) {
        String sql = "SELECT * FROM HOADON WHERE MaHD = ?";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return docHoaDon(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // MỚI (Phục vụ Module 4.2 - LichSuHoaDonPanel): Lấy danh sách hóa đơn đã thanh toán
    public List<HoaDon> getLichSuHoaDon(String tuKhoa) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE TrangThai = N'Đã thanh toán' "
                   + "AND (CAST(MaHD AS VARCHAR(20)) LIKE ? OR CAST(MaBan AS VARCHAR(20)) LIKE ?) "
                   + "ORDER BY GioThanhToan DESC, MaHD DESC";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String key = "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(docHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // MỚI (Phục vụ Module 4.3 - ThongKePanel): Lấy danh sách hóa đơn đã thanh toán trong ngày hôm nay
    public List<HoaDon> getHoaDonTrongNgay() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE TrangThai = N'Đã thanh toán' "
                   + "AND CAST(ISNULL(GioThanhToan, NgayTao) AS DATE) = CAST(GETDATE() AS DATE) "
                   + "ORDER BY MaHD DESC";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(docHoaDon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

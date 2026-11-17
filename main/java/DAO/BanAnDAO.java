package DAO;

import CONN.MyConnection;
import Entity.BanAn;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BanAnDAO {

    // Tìm kiếm + lọc khu vực (maKhuVuc = 0 là lấy tất cả)
    public List<BanAn> search(int maKhuVuc, String keyword) {
        List<BanAn> list = new ArrayList<>();
        String sql = "SELECT MaBan, TenBan, MaKhuVuc, SoCho, TrangThai FROM BANAN "
                   + "WHERE (? = 0 OR MaKhuVuc = ?) AND TenBan LIKE ? ORDER BY MaBan";
        try (Connection CONN = MyConnection.getConnection();
             PreparedStatement ps = CONN.prepareStatement(sql)) {
            ps.setInt(1, maKhuVuc);
            ps.setInt(2, maKhuVuc);
            ps.setNString(3, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BanAn(rs.getInt(1), rs.getNString(2),
                            rs.getInt(3), rs.getInt(4), rs.getNString(5)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BanAn> getAll() {
    return search(0, ""); // Gọi search với maKhuVuc = 0 và keyword rỗng để lấy toàn bộ bàn
}
    
    // MaBan không IDENTITY nên tự lấy mã tiếp theo
    private int layMaMoi(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT ISNULL(MAX(MaBan), 0) + 1 FROM BANAN")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public boolean insert(BanAn b) {
        String sql = "INSERT INTO BANAN (MaBan, TenBan, MaKhuVuc, SoCho, TrangThai) VALUES (?,?,?,?,?)";
        try (Connection CONN = MyConnection.getConnection();
             PreparedStatement ps = CONN.prepareStatement(sql)) {
            ps.setInt(1, layMaMoi(CONN));
            ps.setNString(2, b.getTenBan());
            ps.setInt(3, b.getMaKhuVuc());
            ps.setInt(4, b.getSoCho());
            ps.setNString(5, b.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // thường do trùng TenBan
        }
    }

    public boolean update(BanAn b) {
        String sql = "UPDATE BANAN SET TenBan=?, MaKhuVuc=?, SoCho=?, TrangThai=? WHERE MaBan=?";
        try (Connection CONN = MyConnection.getConnection();
             PreparedStatement ps = CONN.prepareStatement(sql)) {
            ps.setNString(1, b.getTenBan());
            ps.setInt(2, b.getMaKhuVuc());
            ps.setInt(3, b.getSoCho());
            ps.setNString(4, b.getTrangThai());
            ps.setInt(5, b.getMaBan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int maBan) {
        String sql = "DELETE FROM BANAN WHERE MaBan=?";
        try (Connection CONN = MyConnection.getConnection();
             PreparedStatement ps = CONN.prepareStatement(sql)) {
            ps.setInt(1, maBan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // bàn đã có hóa đơn thì vướng khóa ngoại
        }
    }

}
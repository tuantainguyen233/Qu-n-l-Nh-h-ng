package DAO;


import CONN.MyConnection;
import Entity.ChiTietHoaDon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ChiTietHoaDonDAO {
    public boolean insertOrUpdate(ChiTietHoaDon cthd) {
        String checkSql = "SELECT SoLuong FROM CHITIETHOADON WHERE MaHD = ? AND MaMon = ?";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement psCheck = con.prepareStatement(checkSql)) {
            psCheck.setInt(1, cthd.getMaHD());
            psCheck.setInt(2, cthd.getMaMon());
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                // Đã có -> Cộng dồn số lượng
                int slMoi = rs.getInt("SoLuong") + cthd.getSoLuong();
                String updateSql = "UPDATE CHITIETHOADON SET SoLuong = ? WHERE MaHD = ? AND MaMon = ?";
                try (PreparedStatement psUp = con.prepareStatement(updateSql)) {
                    psUp.setInt(1, slMoi);
                    psUp.setInt(2, cthd.getMaHD());
                    psUp.setInt(3, cthd.getMaMon());
                    return psUp.executeUpdate() > 0;
                }
            } else {
                // Thêm dòng chi tiết mới
                String insertSql = "INSERT INTO CHITIETHOADON (MaHD, MaMon, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psIn = con.prepareStatement(insertSql)) {
                    psIn.setInt(1, cthd.getMaHD());
                    psIn.setInt(2, cthd.getMaMon());
                    psIn.setInt(3, cthd.getSoLuong());
                    psIn.setDouble(4, cthd.getDonGia());
                    return psIn.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ChiTietHoaDon> getByMaHD(int maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM CHITIETHOADON WHERE MaHD = ?";
        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ChiTietHoaDon(
                    rs.getInt("MaHD"), rs.getInt("MaMon"),
                    rs.getInt("SoLuong"), rs.getDouble("DonGia")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean delete(int maHD, int maMon) {
        String sql = "DELETE FROM CHITIETHOADON WHERE MaHD = ? AND MaMon = ?";
        try (Connection con = CONN.MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maHD);
            ps.setInt(2, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

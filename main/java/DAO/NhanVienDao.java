
package DAO;

import CONN.MyConnection;
import Entity.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDao implements Dao<NhanVien> {

    // Lấy toàn bộ nhân viên
    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();

        String sql = "SELECT * FROM NHANVIEN ORDER BY MaNV";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(docNhanVien(rs));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy nhân viên: " + e.getMessage());
        }

        return list;
    }

    // Đọc dữ liệu một nhân viên từ ResultSet
    private NhanVien docNhanVien(ResultSet rs) throws SQLException {
        return new NhanVien(
            rs.getInt("MaNV"),
            rs.getString("HoTen"),
            rs.getDate("NgaySinh"),
            rs.getString("GioiTinh"),
            rs.getString("DienThoai"),
            rs.getString("Email"),
            rs.getString("ChucVu"),
            rs.getString("TrangThai")
        );
    }

    // Thêm nhân viên
    @Override
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NHANVIEN "
                + "(MaNV, HoTen, NgaySinh, GioiTinh, "
                + "DienThoai, Email, ChucVu, TrangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setDate(3, nv.getNgaySinh());
            ps.setString(4, nv.getGioiTinh());
            ps.setString(5, nv.getDienThoai());

            if (nv.getEmail() == null || nv.getEmail().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, nv.getEmail());
            }

            ps.setString(7, nv.getChucVu());
            ps.setString(8, nv.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm nhân viên: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật nhân viên
    @Override
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NHANVIEN SET "
                + "HoTen = ?, NgaySinh = ?, GioiTinh = ?, "
                + "DienThoai = ?, Email = ?, ChucVu = ?, "
                + "TrangThai = ? WHERE MaNV = ?";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getHoTen());
            ps.setDate(2, nv.getNgaySinh());
            ps.setString(3, nv.getGioiTinh());
            ps.setString(4, nv.getDienThoai());

            if (nv.getEmail() == null || nv.getEmail().isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, nv.getEmail());
            }

            ps.setString(6, nv.getChucVu());
            ps.setString(7, nv.getTrangThai());
            ps.setInt(8, nv.getMaNV());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật nhân viên: " + e.getMessage());
            return false;
        }
    }

    // Xóa nhân viên theo mã
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM NHANVIEN WHERE MaNV = ?";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Lỗi xóa nhân viên: " + e.getMessage());
            return false;
        }
    }

    // Tìm theo mã, họ tên hoặc số điện thoại
    

@Override
public List<NhanVien> search(String keyword) {
    List<NhanVien> list = new ArrayList<>();

    if (keyword == null || keyword.trim().isEmpty()) {
        return list;
    }

    keyword = keyword.trim();

    String sql;

    boolean laMaNV = keyword.matches("\\d+");
    boolean laSoDienThoai = keyword.matches("0\\d+");

    if (laSoDienThoai) {
        sql = "SELECT * FROM NHANVIEN "
            + "WHERE DienThoai LIKE ? ORDER BY MaNV";
    } else if (laMaNV) {
        sql = "SELECT * FROM NHANVIEN "
            + "WHERE MaNV = ? ORDER BY MaNV";
    } else {
        sql = "SELECT * FROM NHANVIEN "
            + "WHERE HoTen LIKE ? ORDER BY MaNV";
    }

    try (Connection conn = MyConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        if (laSoDienThoai) {
            ps.setString(1, "%" + keyword + "%");
        } else if (laMaNV) {
            ps.setInt(1, Integer.parseInt(keyword));
        } else {
            ps.setString(1, "%" + keyword + "%");
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(docNhanVien(rs));
            }
        }

    } catch (SQLException | NumberFormatException e) {
        System.out.println("Lỗi tìm nhân viên: " + e.getMessage());
    }

    return list;
}

}

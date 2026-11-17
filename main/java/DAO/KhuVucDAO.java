package DAO;

import Entity.KhuVuc;
import CONN.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhuVucDAO implements Dao<KhuVuc> {

    private static final String SELECT =
        "SELECT MaKhuVuc, TenKhuVuc, ViTri, TrangThai, GhiChu FROM KHUVUC ";

    private KhuVuc map(ResultSet rs) {
        try {
            return new KhuVuc(
                rs.getInt("MaKhuVuc"),
                rs.getNString("TenKhuVuc"),
                rs.getNString("ViTri"),
                rs.getNString("TrangThai"),
                rs.getNString("GhiChu")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi ánh xạ dữ liệu KhuVuc", e);
        }
    }

    @Override
    public List<KhuVuc> getAll() {
        List<KhuVuc> list = new ArrayList<>();
        String sql = SELECT + "ORDER BY MaKhuVuc";

        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<KhuVuc> search(String keyword) {
        List<KhuVuc> list = new ArrayList<>();

        String sql = SELECT
            + "WHERE CAST(MaKhuVuc AS VARCHAR(20)) LIKE ? "
            + "OR TenKhuVuc LIKE ? "
            + "OR ViTri LIKE ? "
            + "ORDER BY MaKhuVuc";

        String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setNString(1, kw);
            ps.setNString(2, kw);
            ps.setNString(3, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean insert(KhuVuc kv) {
        String sql =
            "INSERT INTO KHUVUC (MaKhuVuc, TenKhuVuc, ViTri, TrangThai, GhiChu) "
          + "VALUES ((SELECT ISNULL(MAX(MaKhuVuc), 0) + 1 FROM KHUVUC), ?, ?, ?, ?)";

        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setNString(1, kv.getTenKhuVuc());
            ps.setNString(2, kv.getViTri());
            ps.setNString(3, kv.getTrangThai());
            ps.setNString(4, kv.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(KhuVuc kv) {
        String sql =
            "UPDATE KHUVUC SET TenKhuVuc = ?, ViTri = ?, TrangThai = ?, GhiChu = ? "
          + "WHERE MaKhuVuc = ?";

        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setNString(1, kv.getTenKhuVuc());
            ps.setNString(2, kv.getViTri());
            ps.setNString(3, kv.getTrangThai());
            ps.setNString(4, kv.getGhiChu());
            ps.setInt(5, kv.getMaKhuVuc());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đã sửa thành delete(String id) để khớp hoàn toàn với Dao<T>
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM KHUVUC WHERE MaKhuVuc = ?";

        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(id));

            return ps.executeUpdate() > 0;
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Giữ thêm hàm delete(int maKhuVuc) overload nếu tầng UI có gọi trực tiếp bằng số
    public boolean delete(int maKhuVuc) {
        return delete(String.valueOf(maKhuVuc));
    }

    public boolean isTrungTen(String tenKhuVuc, int excludeMaKhuVuc) {
        String sql =
            "SELECT COUNT(*) FROM KHUVUC "
          + "WHERE TenKhuVuc = ? AND MaKhuVuc <> ?";

        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setNString(1, tenKhuVuc);
            ps.setInt(2, excludeMaKhuVuc);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<Integer, Integer> demBanTheoKhuVuc() {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "SELECT MaKhuVuc, COUNT(*) FROM BANAN GROUP BY MaKhuVuc";

        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getInt(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
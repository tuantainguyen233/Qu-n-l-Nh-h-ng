package DAO;

import CONN.MyConnection;
import Entity.DatBan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatBanDAO implements Dao<DatBan> {

    public static final int PHUT_GIAN_CACH = 120;

    private static final String SELECT =
        "SELECT d.MaDat, d.TenKhach, d.SDT, d.MaBan, b.TenBan, d.GioHen, d.TrangThai "
      + "FROM DATBAN d JOIN BANAN b ON d.MaBan = b.MaBan ";

    private DatBan map(ResultSet rs) throws SQLException {
        return new DatBan(
            rs.getString("MaDat"),
            rs.getNString("TenKhach"),
            rs.getString("SDT"),
            rs.getInt("MaBan"),
            rs.getNString("TenBan"),
            rs.getString("GioHen"),
            rs.getNString("TrangThai")
        );
    }

    @Override
    public List<DatBan> getAll() {
        return truyVan(SELECT + "ORDER BY d.GioHen, d.MaDat", null);
    }

    @Override
    public List<DatBan> search(String keyword) {
        String sql = SELECT
            + "WHERE d.MaDat LIKE ? OR d.TenKhach LIKE ? OR d.SDT LIKE ? OR b.TenBan LIKE ? "
            + "ORDER BY d.GioHen, d.MaDat";
        String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        return truyVan(sql, kw);
    }

    private List<DatBan> truyVan(String sql, String kw) {
        List<DatBan> list = new ArrayList<>();
        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (kw != null) {
                for (int i = 1; i <= 4; i++) {
                    ps.setNString(i, kw);
                }
            }
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

    private String layMaMoi(Connection c) throws SQLException {
        String sql = "SELECT ISNULL(MAX(CAST(SUBSTRING(MaDat, 3, 8) AS INT)), 0) + 1 "
                   + "FROM DATBAN WHERE MaDat LIKE 'DB[0-9]%'";
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return String.format("DB%02d", rs.getInt(1));
        }
    }

    private int layMaBan(Connection c, String maDat) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT MaBan FROM DATBAN WHERE MaDat = ?")) {
            ps.setString(1, maDat);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    private void capNhatTrangThaiBan(Connection c, int maBan) throws SQLException {
        if (maBan <= 0) {
            return;
        }
        String sql = "UPDATE BANAN SET TrangThai = CASE WHEN EXISTS ("
                   + "SELECT 1 FROM DATBAN WHERE MaBan = BANAN.MaBan AND TrangThai = N'Đã đặt') "
                   + "THEN N'Đã đặt' ELSE N'Trống' END "
                   + "WHERE MaBan = ? AND TrangThai <> N'Đang dùng'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maBan);
            ps.executeUpdate();
        }
    }

    public boolean isTrungGio(int maBan, String gioHen, String boQuaMaDat) {
        String sql = "SELECT COUNT(*) FROM DATBAN "
                   + "WHERE MaBan = ? AND MaDat <> ? AND TrangThai <> N'Đã hủy' "
                   + "AND ABS(DATEDIFF(MINUTE, TRY_CAST(GioHen AS TIME), TRY_CAST(? AS TIME))) < ?";
        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maBan);
            ps.setString(2, boQuaMaDat == null ? "" : boQuaMaDat);
            ps.setString(3, gioHen);
            ps.setInt(4, PHUT_GIAN_CACH);
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

    @Override
    public boolean insert(DatBan d) {
        String sql = "INSERT INTO DATBAN (MaDat, TenKhach, SDT, MaBan, GioHen, TrangThai) "
                   + "VALUES (?,?,?,?,?,?)";
        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, layMaMoi(c));
            ps.setNString(2, d.getTenKhach());
            ps.setString(3, d.getSdt());
            ps.setInt(4, d.getMaBan());
            ps.setString(5, d.getGioHen());
            ps.setNString(6, d.getTrangThai());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                capNhatTrangThaiBan(c, d.getMaBan());
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(DatBan d) {
        String sql = "UPDATE DATBAN SET TenKhach=?, SDT=?, MaBan=?, GioHen=? WHERE MaDat=?";
        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int maBanCu = layMaBan(c, d.getMaDat());
            ps.setNString(1, d.getTenKhach());
            ps.setString(2, d.getSdt());
            ps.setInt(3, d.getMaBan());
            ps.setString(4, d.getGioHen());
            ps.setString(5, d.getMaDat());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                capNhatTrangThaiBan(c, maBanCu);
                capNhatTrangThaiBan(c, d.getMaBan());
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String maDat) {
        String sql = "DELETE FROM DATBAN WHERE MaDat = ?";
        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int maBan = layMaBan(c, maDat);
            ps.setString(1, maDat);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                capNhatTrangThaiBan(c, maBan);
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean chuyenBan(String maDat, int maBanMoi) {
        String sql = "UPDATE DATBAN SET MaBan = ? WHERE MaDat = ?";
        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int maBanCu = layMaBan(c, maDat);
            ps.setInt(1, maBanMoi);
            ps.setString(2, maDat);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                capNhatTrangThaiBan(c, maBanCu);
                capNhatTrangThaiBan(c, maBanMoi);
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doiTrangThai(String maDat, String trangThai) {
        String sql = "UPDATE DATBAN SET TrangThai = ? WHERE MaDat = ?";
        try (Connection c = MyConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int maBan = layMaBan(c, maDat);
            ps.setNString(1, trangThai);
            ps.setString(2, maDat);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                capNhatTrangThaiBan(c, maBan);
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package DAO;

import CONN.MyConnection;
import Entity.PhanCa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhanCaDao {

    // 1. Lấy toàn bộ danh sách phân ca
    // JOIN để lấy tên nhân viên và tên khu vực
    public List<PhanCa> getAll() {
        List<PhanCa> list = new ArrayList<>();

        String sql = """
            SELECT pc.MaPhanCa, pc.MaNV, nv.HoTen,
                   pc.CaLam, pc.NgayLam,
                   pc.MaKhuVuc, kv.TenKhuVuc
            FROM PHANCA pc
            JOIN NHANVIEN nv ON pc.MaNV = nv.MaNV
            JOIN KHUVUC kv ON pc.MaKhuVuc = kv.MaKhuVuc
            ORDER BY pc.NgayLam, pc.MaPhanCa
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(docPhanCa(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 2. Tìm phân ca theo mã nhân viên
    public List<PhanCa> findByMaNV(int maNV) {
        List<PhanCa> list = new ArrayList<>();

        String sql = """
            SELECT pc.MaPhanCa, pc.MaNV, nv.HoTen,
                   pc.CaLam, pc.NgayLam,
                   pc.MaKhuVuc, kv.TenKhuVuc
            FROM PHANCA pc
            JOIN NHANVIEN nv ON pc.MaNV = nv.MaNV
            JOIN KHUVUC kv ON pc.MaKhuVuc = kv.MaKhuVuc
            WHERE pc.MaNV = ?
            ORDER BY pc.NgayLam, pc.MaPhanCa
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maNV);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(docPhanCa(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Hàm dùng chung để đọc một dòng dữ liệu phân ca
    private PhanCa docPhanCa(ResultSet rs) throws SQLException {
        return new PhanCa(
            rs.getInt("MaPhanCa"),
            rs.getInt("MaNV"),
            rs.getString("HoTen"),
            rs.getString("CaLam"),
            rs.getDate("NgayLam"),
            rs.getInt("MaKhuVuc"),
            rs.getString("TenKhuVuc")
        );
    }

    // 3. Thêm phân ca
    public boolean insert(PhanCa pc) {
        String sql = """
            INSERT INTO PHANCA (MaNV, CaLam, NgayLam, MaKhuVuc)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pc.getMaNV());
            ps.setString(2, pc.getCaLam());
            ps.setDate(3, pc.getNgayLam());
            ps.setInt(4, pc.getMaKhuVuc());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Sửa phân ca theo mã phân ca
    public boolean update(PhanCa pc) {
        String sql = """
            UPDATE PHANCA
            SET MaNV = ?, CaLam = ?, NgayLam = ?, MaKhuVuc = ?
            WHERE MaPhanCa = ?
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pc.getMaNV());
            ps.setString(2, pc.getCaLam());
            ps.setDate(3, pc.getNgayLam());
            ps.setInt(4, pc.getMaKhuVuc());
            ps.setInt(5, pc.getMaPhanCa());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Xóa phân ca theo mã phân ca
    public boolean delete(int maPhanCa) {
        String sql = "DELETE FROM PHANCA WHERE MaPhanCa = ?";

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, maPhanCa);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Lấy danh sách nhân viên để đổ vào JComboBox
    // Trả về mỗi phần tử theo dạng: "1 - Nguyễn Văn An"
    public List<String> getDanhSachNhanVien() {
        List<String> list = new ArrayList<>();

        String sql = """
            SELECT MaNV, HoTen
            FROM NHANVIEN
            WHERE TrangThai = N'Đang làm'
            ORDER BY MaNV
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(
                    rs.getInt("MaNV") + " - " + rs.getString("HoTen")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 7. Lấy danh sách khu vực để đổ vào JComboBox
    // Trả về tên khu vực, ví dụ: "Tầng 1"
    public List<String> getDanhSachKhuVuc() {
        List<String> list = new ArrayList<>();

        String sql = """
            SELECT TenKhuVuc
            FROM KHUVUC
            ORDER BY MaKhuVuc
            """;

        try (Connection con = MyConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("TenKhuVuc"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}

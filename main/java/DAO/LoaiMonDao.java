/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.util.List;
import CONN.MyConnection;
import Entity.LoaiMon;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class LoaiMonDao implements Dao<LoaiMon>{

    @Override
    public List<LoaiMon> getAll() {
 List<LoaiMon> list = new ArrayList<>();
        String sql = "SELECT L.MaLoai, L.TenLoai, L.DonViTinh, L.TrangThai,\n" +
"       (SELECT COUNT(*) FROM MONAN M\n" +
"        WHERE M.MaLoai = L.MaLoai) AS SoMon\n" +
"FROM LOAIMON L";

        try (Connection conn = MyConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) { // cụm st. là chạy câu select, kq lưu vào rs
            // ResultSet chứa kết quả của câu truy vấn SQL. 
            // Chương trình dùng rs.next() để di chuyển qua từng dòng và lấy giá trị từng cột bằng những phương thức 
            // như rs.getInt() hoặc rs.getString().

            while (rs.next()) {
                LoaiMon lm = new LoaiMon( // đoạn này kết nối với sql và lấy dl sau đó    
                        rs.getInt("MaLoai"),
                        rs.getString("TenLoai"),
                        rs.getString("DonViTinh"),
                        rs.getString("TrangThai"),
                        rs.getInt("SoMon")
                );

                list.add(lm);  // đoạn này lay dl tư data thêm vào ds
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean insert(LoaiMon obj) {
String sql = "INSERT INTO LOAIMON (MaLoai, TenLoai, DonViTinh, TrangThai) VALUES (?, ?, ?, ?)";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, obj.getMaLoai());
            ps.setString(2, obj.getTenLoai()); // 2 cái obj. là lấy dl từ LoaiMon
            ps.setString(3, obj.getDonViTinh());
            ps.setString(4, obj.getTrangThai());
            
            
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(LoaiMon obj) {
        String sql = "UPDATE LOAIMON SET TenLoai = ?, DonViTinh = ?, TrangThai = ? WHERE MaLoai = ?";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, obj.getTenLoai());
            ps.setString(2, obj.getDonViTinh());
            ps.setString(3, obj.getTrangThai());
            ps.setInt(4, obj.getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
}

    @Override
    public boolean delete(String id) {
String sql = "DELETE FROM LOAIMON WHERE MaLoai = ?";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(id));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<LoaiMon> search(String keyword) {
 List<LoaiMon> list = new ArrayList<>();
       String sql = "SELECT L.MaLoai, L.TenLoai, L.DonViTinh, L.TrangThai, "
        + "(SELECT COUNT(*) FROM MONAN M WHERE M.MaLoai = L.MaLoai) AS SoMon "
        + "FROM LOAIMON L "
        + "WHERE L.TenLoai LIKE ? OR CAST(L.MaLoai AS NVARCHAR(20)) LIKE ?";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiMon lm = new LoaiMon(
                            rs.getInt("MaLoai"),
                            rs.getString("TenLoai"),
                            rs.getString("DonViTinh"),
                            rs.getString("TrangThai"),
                            rs.getInt("SoMon")
                    );

                    list.add(lm);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }  
}
    


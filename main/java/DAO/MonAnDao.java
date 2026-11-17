/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;


import CONN.MyConnection;
import Entity.MonAn;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class MonAnDao implements Dao<MonAn>{
    
   

    @Override
    public List<MonAn> getAll() {
List<MonAn> list = new ArrayList<>();
        String sql = "SELECT * FROM MONAN";

        try (Connection conn = MyConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                MonAn mon = new MonAn(
                    rs.getInt("MaMon"),
                    rs.getString("TenMon"),
                    rs.getInt("MaLoai"),
                    rs.getBigDecimal("DonGia"),
                    rs.getString("TrangThai")
                );
               mon.setLuongTonDuKien(rs.getInt("SoLuongTonDuKien"));
                list.add(mon);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean insert(MonAn obj) {
  String sql = "INSERT INTO MONAN "
                   + "(MaMon, TenMon, MaLoai, DonGia, TrangThai) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = MyConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, obj.getMaMon());
            ps.setString(2, obj.getTenMon());
            ps.setInt(3, obj.getMaLoai());
            ps.setBigDecimal(4, obj.getDonGia());
            ps.setString(5, obj.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

  @Override
public boolean update(MonAn obj) {
    String sql = "UPDATE MONAN SET TenMon = ?, MaLoai = ?, "
               + "DonGia = ?, TrangThai = ?, SoLuongTonDuKien = ? "
               + "WHERE MaMon = ?";

    try (Connection conn = MyConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, obj.getTenMon());
        ps.setInt(2, obj.getMaLoai());
        ps.setBigDecimal(3, obj.getDonGia());
        ps.setString(4, obj.getTrangThai());
        ps.setInt(5, obj.getLuongTonDuKien());
        ps.setInt(6, obj.getMaMon());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

    @Override
    public boolean delete(String id) {
    String sql = "DELETE FROM MONAN WHERE MaMon = ?";

    try (Connection conn = MyConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, Integer.parseInt(id));

        return ps.executeUpdate() > 0;

    } catch (java.sql.SQLException e) {
        if (e.getErrorCode() == 547) {
            System.out.println("Không thể xóa món ăn vì món đã được sử dụng trong hóa đơn.");
        } else {
            e.printStackTrace();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}

    @Override
    public List<MonAn> search(String keyword) {
  List<MonAn> list = new ArrayList<>();

    String sql = "SELECT * FROM MONAN "
               + "WHERE CAST(MaMon AS CHAR) LIKE ? "
               + "OR TenMon LIKE ?";
             // String sql = "SELECT * FROM MONAN WHERE DonGia = ?";
    try (Connection conn = MyConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        String key = "%" + keyword.trim() + "%";
        //  ps.setBigDecimal(1, new BigDecimal(keyword.trim()));, xóa bên dưới i 

        ps.setString(1, key);
        ps.setString(2, key);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MonAn mon = new MonAn(
                    rs.getInt("MaMon"),
                    rs.getString("TenMon"),
                    rs.getInt("MaLoai"),
                    rs.getBigDecimal("DonGia"),
                    rs.getString("TrangThai")
                );
                mon.setLuongTonDuKien(rs.getInt("SoLuongTonDuKien"));
                list.add(mon);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
    

}
    


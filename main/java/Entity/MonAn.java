package Entity;

import java.math.BigDecimal;

public class MonAn {
    private int MaMon;
    private String TenMon;
    private int MaLoai;
    private BigDecimal DonGia;
    private int LuongTonDuKien;
    private String TrangThai;

    public MonAn() {
    }

    public MonAn(int MaMon, String TenMon, int MaLoai, BigDecimal DonGia, String TrangThai) {
        this.MaMon = MaMon;
        this.TenMon = TenMon;
        this.MaLoai = MaLoai;
        this.DonGia = DonGia;
        this.TrangThai = TrangThai;
    }

    public int getMaMon() { return MaMon; }
    public void setMaMon(int MaMon) { this.MaMon = MaMon; }

    public String getTenMon() { return TenMon; }
    public void setTenMon(String TenMon) { this.TenMon = TenMon; }

    public int getMaLoai() { return MaLoai; }
    public void setMaLoai(int MaLoai) { this.MaLoai = MaLoai; }

    public BigDecimal getDonGia() { return DonGia; }
    public void setDonGia(BigDecimal DonGia) { this.DonGia = DonGia; }

    public String getTrangThai() { return TrangThai; }
    public void setTrangThai(String TrangThai) { this.TrangThai = TrangThai; }
    
    public int getLuongTonDuKien() { return LuongTonDuKien; }
    public void setLuongTonDuKien(int SoLuongTonDuKien) { this.LuongTonDuKien = SoLuongTonDuKien; }

    @Override
    public String toString() {
        return TenMon;
    }
}
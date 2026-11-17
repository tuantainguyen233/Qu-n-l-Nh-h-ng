package Entity;

import java.util.Date;

public class HoaDon {
    private int maHD;
    private int maBan;
    private int maNV;
    private Date ngayTao;
    private Date gioVao;   
    private Date gioThanhToan; 
    private double tongTien;
    private double giamGia;
    private String trangThai;

    public HoaDon() {
    }
    public HoaDon(int maHD, int maBan, int maNV, Date ngayTao, double tongTien, double giamGia, String trangThai) {
        this.maHD = maHD;
        this.maBan = maBan;
        this.maNV = maNV;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
        this.trangThai = trangThai;
    }

    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }

    public int getMaBan() { return maBan; }
    public void setMaBan(int maBan) { this.maBan = maBan; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getGioVao() { return gioVao; }
    public void setGioVao(Date gioVao) { this.gioVao = gioVao; }

    public Date getGioThanhToan() { return gioThanhToan; }
    public void setGioThanhToan(Date gioThanhToan) { this.gioThanhToan = gioThanhToan; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public double getGiamGia() { return giamGia; }
    public void setGiamGia(double giamGia) { this.giamGia = giamGia; }

    public double getThanhTien() {
        double tt = tongTien - giamGia;
        return tt < 0 ? 0 : tt;
    }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
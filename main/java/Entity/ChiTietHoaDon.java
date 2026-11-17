package Entity;

public class ChiTietHoaDon {
    private int maHD;
    private int maMon;
    private int soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(int maHD, int maMon, int soLuong, double donGia) {
        this.maHD = maHD;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
    }

    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }

    public int getMaMon() { return maMon; }
    public void setMaMon(int maMon) { this.maMon = maMon; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getThanhTien() { return soLuong * donGia; }
}

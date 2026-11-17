package Entity;

public class LoaiMon {
    private int MaLoai;
    private String TenLoai;
    private String DonViTinh;
    private String TrangThai;
    private int soMon;

    public LoaiMon() {
    }

    // Constructor cũ 2 tham số (Bắt buộc giữ để các module khác không bị lỗi đỏ)
    public LoaiMon(int MaLoai, String TenLoai) {
        this.MaLoai = MaLoai;
        this.TenLoai = TenLoai;
    }

    // Constructor mới 5 tham số cho LoaiMonPanel & TinhTrangPanel
    public LoaiMon(int MaLoai, String TenLoai, String DonViTinh, String TrangThai, int soMon) {
        this.MaLoai = MaLoai;
        this.TenLoai = TenLoai;
        this.DonViTinh = DonViTinh;
        this.TrangThai = TrangThai;
        this.soMon = soMon;
    }

    public int getMaLoai() { return MaLoai; }
    public void setMaLoai(int MaLoai) { this.MaLoai = MaLoai; }

    public String getTenLoai() { return TenLoai; }
    public void setTenLoai(String TenLoai) { this.TenLoai = TenLoai; }

    public String getDonViTinh() { return DonViTinh; }
    public void setDonViTinh(String DonViTinh) { this.DonViTinh = DonViTinh; }

    public String getTrangThai() { return TrangThai; }
    public void setTrangThai(String TrangThai) { this.TrangThai = TrangThai; }

    public int getSoMon() { return soMon; }
    public void setSoMon(int soMon) { this.soMon = soMon; }

    @Override
    public String toString() {
        return TenLoai;
    }
}
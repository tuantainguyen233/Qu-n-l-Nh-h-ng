package Entity;

public class DatBan {
    private String maDat;
    private String tenKhach;
    private String sdt;
    private int maBan;
    private String tenBan;      // chỉ để hiển thị (lấy từ JOIN với BANAN)
    private String gioHen;      // dạng HH:mm
    private String trangThai;   // Đã đặt / Đã nhận bàn / Đã hủy

    public DatBan() {}

    public DatBan(String maDat, String tenKhach, String sdt, int maBan,
                  String tenBan, String gioHen, String trangThai) {
        this.maDat = maDat;
        this.tenKhach = tenKhach;
        this.sdt = sdt;
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.gioHen = gioHen;
        this.trangThai = trangThai;
    }

    public String getMaDat() { return maDat; }
    public void setMaDat(String maDat) { this.maDat = maDat; }
    public String getTenKhach() { return tenKhach; }
    public void setTenKhach(String tenKhach) { this.tenKhach = tenKhach; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public int getMaBan() { return maBan; }
    public void setMaBan(int maBan) { this.maBan = maBan; }
    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }
    public String getGioHen() { return gioHen; }
    public void setGioHen(String gioHen) { this.gioHen = gioHen; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
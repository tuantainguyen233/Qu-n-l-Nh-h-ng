package Entity;

public class BanAn {
    private int maBan;
    private String tenBan;
    private int maKhuVuc;
    private int soCho;
    private String trangThai;

    public BanAn() {}

    public BanAn(int maBan, String tenBan, int maKhuVuc, int soCho, String trangThai) {
        this.maBan = maBan; this.tenBan = tenBan; this.maKhuVuc = maKhuVuc;
        this.soCho = soCho; this.trangThai = trangThai;
    }

    public int getMaBan() { return maBan; }
    public void setMaBan(int maBan) { this.maBan = maBan; }
    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }
    public int getMaKhuVuc() { return maKhuVuc; }
    public void setMaKhuVuc(int maKhuVuc) { this.maKhuVuc = maKhuVuc; }
    public int getSoCho() { return soCho; }
    public void setSoCho(int soCho) { this.soCho = soCho; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() { return tenBan; }   // để JComboBox hiển thị tên bàn
}
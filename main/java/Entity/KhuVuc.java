package Entity;

public class KhuVuc {
    private int maKhuVuc;
    private String tenKhuVuc;
    private String viTri;
    private String trangThai;
    private String ghiChu;

    public KhuVuc() {}

    public KhuVuc(int maKhuVuc, String tenKhuVuc) {
        this.maKhuVuc = maKhuVuc;
        this.tenKhuVuc = tenKhuVuc;
    }

    public KhuVuc(int maKhuVuc, String tenKhuVuc, String viTri, String trangThai, String ghiChu) {
        this.maKhuVuc = maKhuVuc;
        this.tenKhuVuc = tenKhuVuc;
        this.viTri = viTri;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public int getMaKhuVuc() { return maKhuVuc; }
    public void setMaKhuVuc(int maKhuVuc) { this.maKhuVuc = maKhuVuc; }
    public String getTenKhuVuc() { return tenKhuVuc; }
    public void setTenKhuVuc(String tenKhuVuc) { this.tenKhuVuc = tenKhuVuc; }
    public String getViTri() { return viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    @Override
    public String toString() { return tenKhuVuc; }   // để JComboBox hiển thị tên
}
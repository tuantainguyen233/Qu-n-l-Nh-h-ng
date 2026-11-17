
package Entity;

import java.sql.Date;

public class PhanCa {

    private int maPhanCa;
    private int maNV;
    private String tenNV;
    private String caLam;
    private Date ngayLam;
    private int maKhuVuc;
    private String tenKhuVuc;

    public PhanCa() {
    }

    // Constructor dùng khi thêm phân ca
    public PhanCa(int maNV, String caLam,
                  Date ngayLam, int maKhuVuc) {
        this.maNV = maNV;
        this.caLam = caLam;
        this.ngayLam = ngayLam;
        this.maKhuVuc = maKhuVuc;
    }

    // Constructor đầy đủ dữ liệu
    public PhanCa(int maPhanCa, int maNV, String tenNV,
                  String caLam, Date ngayLam,
                  int maKhuVuc, String tenKhuVuc) {
        this.maPhanCa = maPhanCa;
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.caLam = caLam;
        this.ngayLam = ngayLam;
        this.maKhuVuc = maKhuVuc;
        this.tenKhuVuc = tenKhuVuc;
    }

    public int getMaPhanCa() {
        return maPhanCa;
    }

    public void setMaPhanCa(int maPhanCa) {
        this.maPhanCa = maPhanCa;
    }

    public int getMaNV() {
        return maNV;
    }

    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getCaLam() {
        return caLam;
    }

    public void setCaLam(String caLam) {
        this.caLam = caLam;
    }

    public Date getNgayLam() {
        return ngayLam;
    }

    public void setNgayLam(Date ngayLam) {
        this.ngayLam = ngayLam;
    }

    public int getMaKhuVuc() {
        return maKhuVuc;
    }

    public void setMaKhuVuc(int maKhuVuc) {
        this.maKhuVuc = maKhuVuc;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc;
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        this.tenKhuVuc = tenKhuVuc;
    }
}

package Service;


public class AuthService {

    public static class User {
        public final String tenDangNhap;
        public final String hoTen;
        public final String vaiTro; // "ADMIN" hoặc "NHANVIEN"

        public User(String tenDangNhap, String hoTen, String vaiTro) {
            this.tenDangNhap = tenDangNhap;
            this.hoTen = hoTen;
            this.vaiTro = vaiTro;
        }

        public boolean laAdmin() {
            return "ADMIN".equals(vaiTro);
        }
    }

    private static User currentUser;

    /** Trả về User nếu đúng tài khoản, ngược lại trả về null. */
    public static User dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || matKhau == null) {
            return null;
        }
        // TODO: thay bằng truy vấn CSDL (bảng tài khoản/nhân viên), mật khẩu nên băm (BCrypt/SHA-256)
        User user = null;
        if (tenDangNhap.equals("admin") && matKhau.equals("admin123")) {
            user = new User("admin", "Quản trị viên", "ADMIN");
        } else if (tenDangNhap.equals("nhanvien") && matKhau.equals("123456")) {
            user = new User("nhanvien", "Nhân viên bán hàng", "NHANVIEN");
        }
        currentUser = user;
        return user;
    }

    public static void dangXuat() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
package DAO;



import java.sql.SQLException;
import java.util.List;
public interface Dao<T> {
    // Lấy toàn bộ danh sách
    List<T> getAll() ;

    // Thêm đối tượng
    boolean insert(T obj) ;

    // Cập nhật đối tượng
    boolean update(T obj) ;

    // Xóa theo mã
    boolean delete(String id) ;

    // Tìm kiếm theo từ khóa
    List<T> search(String keyword) ;
    
}

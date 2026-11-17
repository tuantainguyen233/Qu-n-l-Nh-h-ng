package CONN;
import java.sql.*;
public class MyConnection {
    private static final String SERVER="DESKTOP-BGLSIMT\\SQLEXPRESS";
    private static final String DATABASE ="QuanLyNhaHang";
    private static final String USER ="sa";
    private static final String PASSWORD = "123456";

    private static final String URL = "jdbc:sqlserver://" + SERVER + ":1433;"
            + "databaseName=" + DATABASE + ";"
            + "encrypt=true;trustServerCertificate=true;";

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(URL,USER,PASSWORD );

        } catch (SQLException e) {
            System.out.println("Không thể kết nối CSDL!");
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args){
        if(MyConnection.getConnection()!= null)
            System.out.println("Thanh cong");
        else
            System.out.println("That bai");
    }
}

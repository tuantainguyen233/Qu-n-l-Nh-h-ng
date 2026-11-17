# Quản Lý Nhà Hàng
I. Quản lý nhà hàng, quán ăn
-  Đây là Dự án xây dựng Hệ thống Quán lý nhà hàng, quán ăn
-  Dự án giúp giải quyết việc Sắp xếp ăn, quản lý từng khu vực ( nhà hàng lớn ), kiểm tra thu nhập theo ngày/tháng/năm, chỉnh sửa thực đơn khi hết món ăn hay thêm món

II. Công nghệ
1. Java, Java Swing
- Tạo khung hệ thống
- Tạo sự kiện cho các chức năng như ( thêm, sửa, xóa, cập nhật)
2. SQL Sever
- Lưu trữ toàn bộ dữ liệu gốc của ứng dụng
- Không mất dữ liệu khi tắt ứng dụng
- Tìm kiếm, lọc, thống kê nhanh chóng bằng SQL

III. Cách thức hoạt động của ứng dụng
1. Cài biến môi trường JDK 17, NetBeans và tải file gốc SQL Server
2. Mở SQL Server, chạy file để tạo CSDL
3. Mở file `DBConnection.java`, sửa user và password theo máy của mình
4. Mở project bằng NetBeans, bấm Run
5. Chờ đợi hiện kết quả

IV. Thành viên tham gia dự án 
  Dự án được thực hiện bởi 4 thành viên:
1.	Nguyễn Tuấn Tài (tôi):
-  Phân tích và xây dựng cụm module Quản lý bàn ăn, khu vực và đặt bàn
-  Hỗ trợ và góp ý các thành viên tạo ý tưởng về thiết kế các Module
-  Hỗ trợ xây dựng thanh toán QRcode
2.  Nguyễn Phương Thanh:
-  Phân tích và xây dựng cụm module Quản lý món ăn, loại món và kiểm kê tình trạng món
-  Hỗ trợ viết báo cáo khi hoàn thành dự án
3. Bùi Văn Thái
-  Phân tích và xây dựng cụm module Quản lý nhân viên, chia ca làm việc và đăng nhập
4. Nguyễn Văn Long Thái
-  Phân tích và xây dựng cụm module Quản lý bán hàng POS, lịch sử/in hóa đơn và thống kê doanh thu
-  Thiết kế khung tích hợp MainFrame 

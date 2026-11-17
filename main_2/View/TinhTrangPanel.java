/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package View;
import DAO.MonAnDao;
import Entity.MonAn;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import DAO.LoaiMonDao;
import Entity.LoaiMon;

/**
 *
 * @author admin
 */
public class TinhTrangPanel extends javax.swing.JPanel {
    
    LoaiMonDao loaiMonDao = new LoaiMonDao();
    MonAnDao monAnDao = new MonAnDao();
    DefaultTableModel tableModel;

    public TinhTrangPanel() {
        initComponents();
        tableModel = (DefaultTableModel) tblTinhTrang.getModel();
        loadDataToTable();
        
         cboTrangThai.removeAllItems();
        cboTrangThai.addItem("Còn món");
        cboTrangThai.addItem("Hết món");
        cboTrangThai.addItem("Ngừng bán");
        
        tblTinhTrang.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(new java.awt.Color(31, 92, 58));
        setForeground(java.awt.Color.WHITE);
        setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        return this;
    }
});
tblTinhTrang.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 40));
    }

    private String getTenLoai(int maLoai) {
    List<LoaiMon> list = loaiMonDao.getAll();

    for (LoaiMon loai : list) {
        if (loai.getMaLoai() == maLoai) {
            return loai.getTenLoai();
        }
    }

    return "";
}
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMonAn = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTrangThai = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboTrangThai = new javax.swing.JComboBox<>();
        txtLuongTon = new javax.swing.JTextField();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTinhTrang = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 51));
        jLabel1.setText("TÌNH TRẠNG MÓN");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 102, 51));
        jLabel2.setText("Chọn Món Ăn");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 102, 51));
        jLabel3.setText("Trạng Thái Hiện Tại");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 51));
        jLabel4.setText("Trạng Thái Mới");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 51));
        jLabel5.setText("Lượng Tồn Dự Kiến");

        btnSua.setBackground(new java.awt.Color(0, 102, 51));
        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSua.setForeground(new java.awt.Color(255, 255, 255));
        btnSua.setText("Sửa");
        btnSua.addActionListener(this::btnSuaActionPerformed);

        btnXoa.setBackground(new java.awt.Color(0, 102, 51));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 255, 255));
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(this::btnXoaActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(14, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel4)
                                        .addComponent(txtTrangThai)
                                        .addComponent(jLabel5)
                                        .addComponent(cboTrangThai, 0, 220, Short.MAX_VALUE))
                                    .addComponent(txtLuongTon, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(btnSua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnXoa)))
                .addGap(41, 41, 41))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMonAn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLuongTon, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 51)));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 51));
        jLabel6.setText("Tìm kiếm món");

        btnTimKiem.setBackground(new java.awt.Color(0, 102, 51));
        btnTimKiem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnTimKiem.setForeground(new java.awt.Color(255, 255, 255));
        btnTimKiem.setText("Tìm kiếm");
        btnTimKiem.addActionListener(this::btnTimKiemActionPerformed);

        tblTinhTrang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Món", "Tên Món", "Loại Món", "Đơn Giá", "Trạng Thái", "Lượng Tồn"
            }
        ));
        tblTinhTrang.setRowHeight(35);
        tblTinhTrang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTinhTrangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTinhTrang);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblTinhTrangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTinhTrangMouseClicked
    int row = tblTinhTrang.getSelectedRow();

if (row >= 0) {
    txtMonAn.setText(
        tblTinhTrang.getValueAt(row, 1).toString()
    );

    txtTrangThai.setText(
        tblTinhTrang.getValueAt(row, 4).toString()
    );

    txtLuongTon.setText(
        tblTinhTrang.getValueAt(row, 5).toString()
    );
}
    }//GEN-LAST:event_tblTinhTrangMouseClicked

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
     int row = tblTinhTrang.getSelectedRow();

if (row < 0) {
    JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần sửa!");
    return;
}

int maMon = Integer.parseInt(
    tblTinhTrang.getValueAt(row, 0).toString()
);

String trangThai = (String) cboTrangThai.getSelectedItem();

if (trangThai == null) {
    JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái mới!");
    return;
}

int soLuong;

try {
    soLuong = Integer.parseInt(txtLuongTon.getText().trim());

    if (soLuong < 0) {
        JOptionPane.showMessageDialog(this, "Số lượng tồn không được âm!");
        return;
    }
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng tồn là số nguyên!");
    return;
}

MonAn mon = null;

List<MonAn> list = monAnDao.getAll();

for (MonAn m : list) {
    if (m.getMaMon() == maMon) {
        mon = m;
        break;
    }
}

if (mon == null) {
    JOptionPane.showMessageDialog(this, "Không tìm thấy món ăn!");
    return;
}

mon.setTrangThai(trangThai);
mon.setLuongTonDuKien(soLuong);

if (monAnDao.update(mon)) {
    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
    loadDataToTable();
    txtTrangThai.setText(trangThai);
} else {
    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
}
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
       String keyword = txtTimKiem.getText().trim();

if (keyword.isEmpty()) {
    loadDataToTable();
    return;
}

tableModel.setRowCount(0);

List<MonAn> list = monAnDao.search(keyword);

for (MonAn mon : list) {
    tableModel.addRow(new Object[]{
        mon.getMaMon(),
        mon.getTenMon(),
        getTenLoai(mon.getMaLoai()),
        mon.getDonGia(),
        mon.getTrangThai()
    });
}
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        int row = tblTinhTrang.getSelectedRow();

if (row < 0) {
    JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần xóa!");
    return;
}

String maMon = tblTinhTrang.getValueAt(row, 0).toString();

int confirm = JOptionPane.showConfirmDialog(
        this,
        "Bạn có chắc muốn xóa món ăn này không?",
        "Xác nhận xóa",
        JOptionPane.YES_NO_OPTION
);

if (confirm == JOptionPane.YES_OPTION) {
    if (monAnDao.delete(maMon)) {
        JOptionPane.showMessageDialog(this, "Xóa món ăn thành công!");
        loadDataToTable();

        txtMonAn.setText("");
        txtTrangThai.setText("");
        txtLuongTon.setText("");
    } else {
        JOptionPane.showMessageDialog(
                this,
                "Xóa thất bại! Món ăn có thể đã được sử dụng trong hóa đơn."
        );
    }
}
    }//GEN-LAST:event_btnXoaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTinhTrang;
    private javax.swing.JTextField txtLuongTon;
    private javax.swing.JTextField txtMonAn;
    private javax.swing.JTextField txtTimKiem;
    private javax.swing.JTextField txtTrangThai;
    // End of variables declaration//GEN-END:variables

    private void loadDataToTable() {
          tableModel.setRowCount(0);

    List<MonAn> list = monAnDao.getAll();

    for (MonAn mon : list) {
        tableModel.addRow(new Object[]{
            mon.getMaMon(),
            mon.getTenMon(),
            getTenLoai(mon.getMaLoai()),
            mon.getDonGia(),
            mon.getTrangThai(),
            mon.getLuongTonDuKien()
        });
    }
    }
    
       public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(() -> {
        javax.swing.JFrame frame = new javax.swing.JFrame("Quản lý tình trạng");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new TinhTrangPanel());   // đổi thành tên class panel của bạn
        frame.pack();                           // khung tự vừa với panel
        frame.setMinimumSize(frame.getSize());  // không cho thu nhỏ hơn kích thước panel
        frame.setLocationRelativeTo(null);      // hiện giữa màn hình
        frame.setVisible(true);
    });
}
}

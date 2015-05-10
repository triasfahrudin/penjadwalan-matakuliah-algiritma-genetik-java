/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geneticAlgorithm.GUI;

import com.geneticAlgorithm.database.MysqlConnect;
import com.geneticAlgorithm.util.util;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author theultimate7
 */
public class FrmPengampu extends javax.swing.JInternalFrame {

    /**
     * Creates new form FrmPengampu
     */
    private final int[] kodeDosen;
    private int[] kodeMK;
    private int _selectedkode;

    public FrmPengampu() throws SQLException {
        //this.kodeMK = null;
        initComponents();

        setButtonStatus(true, false, false);

        cmbDosen.removeAllItems();
        int rowCount = util.getCount("SELECT * FROM dosen ORDER BY nama", "nama");
        kodeDosen = new int[rowCount];
        ResultSet dosen_dt = MysqlConnect.getDbCon().ExecuteQuery("SELECT * FROM dosen ORDER BY nama");
        int i = 0;
        while (dosen_dt.next()) {
            cmbDosen.addItem(dosen_dt.getString("nama"));
            kodeDosen[i] = Integer.parseInt(dosen_dt.getString("kode"));
            i++;
        }

        loadData(cmbSemester.getSelectedItem() == "GANJIL" ? 1 : 0);

        FrmPengampu.ListenerSemester actionListener = new FrmPengampu.ListenerSemester();
        cmbSemester.addItemListener(actionListener);
        cmbTahunAkademik.addItemListener(actionListener);

        //loadData(kodeDosen[0]);
        //FrmWaktuTidakBersedia.MyItemListener actionListener = new FrmWaktuTidakBersedia.MyItemListener();
        //cmbDosen.addItemListener(actionListener);
//        
    }

    private void loadData(int tipe) throws SQLException {
        //cmbMataKuliah.removeAll();
        //int rowCount = util.getCount("", title)
        String query = 
                "SELECT a.kode as Kode,\n"
                + "       b.kode as `Kode MK`,"
                + "       b.nama as `Nama MK`,"
                + "       c.kode as `Kode Dosen`,"
                + "       c.nama as  `Nama Dosen`,"
                + "       a.kelas as Kelas,"
                + "       a.tahun_akademik as `Tahun Akademik` "
                + "FROM pengampu a "
                + "LEFT JOIN mata_kuliah b "
                + "ON a.kode_mk = b.kode "
                + "LEFT JOIN dosen c "
                + "ON a.kode_dosen = c.kode "
                + "WHERE b.semester%2=" + Integer.toString(tipe) + " AND a.tahun_akademik = '" + cmbTahunAkademik.getSelectedItem().toString() + "' "
                + "ORDER BY b.nama,a.kelas";

        ResultSet rs = MysqlConnect.getDbCon().ExecuteQuery(query);

        //cmbMataKuliah.setModel((ComboBoxModel) util.buildTableModel(rs));
        jTable1.setModel(util.buildTableModel(rs));

        //ngasal lagi :(
        jTable1.removeColumn(jTable1.getColumnModel().getColumn(0));
        jTable1.removeColumn(jTable1.getColumnModel().getColumn(0));
        jTable1.removeColumn(jTable1.getColumnModel().getColumn(1));
        //jTable1.removeColumn(jTable1.getColumn(0));

        //--------------
        cmbMataKuliah.removeAllItems();

        int rowCount = util.getCount(
                "SELECT * FROM mata_kuliah WHERE semester%2 = "
                + Integer.toString(tipe)
                + " ORDER BY nama", "nama");
        kodeMK = new int[rowCount];
        ResultSet mk_dt = MysqlConnect.getDbCon().ExecuteQuery(
                "SELECT * FROM mata_kuliah WHERE semester%2 = "
                + Integer.toString(tipe)
                + " ORDER BY nama");
        int i = 0;
        while (mk_dt.next()) {
            cmbMataKuliah.addItem(mk_dt.getString("nama"));
            kodeMK[i] = Integer.parseInt(mk_dt.getString("kode"));
            i++;
        }

    }

    class ListenerSemester implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            Object item = e.getItem();

            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    // Item was just selected
                    loadData(cmbSemester.getSelectedItem() == "GANJIL" ? 1 : 0);
                    //JOptionPane.showMessageDialog(null, kodeDosen[cb.getSelectedIndex()]);
                } catch (SQLException ex) {
                    Logger.getLogger(FrmPengampu.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cmbSemester = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtKelas = new javax.swing.JTextField();
        cmbTahunAkademik = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cmbMataKuliah = new javax.swing.JComboBox();
        cmbDosen = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnBaru = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;   //Disallow the editing of any cell
            }
        };

        setClosable(true);
        setTitle("Form Pengampu");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbSemester.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GANJIL", "GENAP" }));

        jLabel2.setText("Semester");

        jLabel3.setText("Tahun Akademik");

        cmbTahunAkademik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2011/2012", "2012/2013", "2013/2014", "2014/2015", "2015/2016", "2016/2017", "2017/2018", "2018/2019", "2019/2020" }));

        jLabel4.setText("Mata Kuliah");
        jLabel4.setToolTipText("");

        jLabel5.setText("Dosen");
        jLabel5.setToolTipText("");

        jLabel6.setText("Kelas");
        jLabel6.setToolTipText("");

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        btnBaru.setText("Data Baru");
        btnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBaruActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbSemester, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbDosen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnBaru)
                                    .addComponent(txtKelas, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 164, Short.MAX_VALUE))
                            .addComponent(cmbMataKuliah, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cmbTahunAkademik, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(127, 127, 127))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSimpan)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cmbTahunAkademik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbMataKuliah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBaru)
                    .addComponent(btnSimpan)
                    .addComponent(btnBatal))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane1KeyPressed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBaruActionPerformed
        // TODO add your handling code here:
        txtKelas.setText("");
        setButtonStatus(false, true, true);
        _selectedkode = -1;
    }//GEN-LAST:event_btnBaruActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:        
        //
        if ("".equals(txtKelas.getText().trim())) {
            JOptionPane.showMessageDialog(null, "Data Belum Lengkap");
            return;
        }

        int kode_dosen = kodeDosen[cmbDosen.getSelectedIndex()];
        int kode_MK = kodeMK[cmbMataKuliah.getSelectedIndex()];

        if (_selectedkode != -1) {
            //update data
            String check = String.format(
                    "SELECT CAST(COUNT(*) AS CHAR(1)) "
                    + "FROM pengampu "
                    + "WHERE kode_mk= %d AND "
                    + "      kode_dosen= %d AND "
                    + "      kelas = '%s' AND "
                    + "      tahun_akademik = '%s' AND "
                    + "      kode <> %d", kode_MK, kode_dosen, txtKelas.getText(), cmbTahunAkademik.getSelectedItem().toString(), _selectedkode);
            try {
                int i = Integer.parseInt(MysqlConnect.getDbCon().ExecuteScalar(check));

                if (i != 0) {
                    JOptionPane.showMessageDialog(null, "Data ini sudah ada");
                    return;
                }

                String Qry_update = String.format(
                        "UPDATE pengampu "
                        + "SET kode_mk=%d, "
                        + "    kode_dosen=%d, "
                        + "    kelas='%s', "
                        + "tahun_akademik ='%s' "
                        + "WHERE kode=%d", kode_MK, kode_dosen, txtKelas.getText().trim(), cmbTahunAkademik.getSelectedItem().toString(), _selectedkode);
                MysqlConnect.getDbCon().insert(Qry_update);

            } catch (SQLException ex) {
                Logger.getLogger(FrmPengampu.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //new data
            String check = String.format(
                    "SELECT CAST(COUNT(*) AS CHAR(1)) "
                    + "FROM pengampu "
                    + "WHERE kode_mk= %d AND "
                    + "      kode_dosen= %d AND "
                    + "      kelas = '%s' AND "
                    + "      tahun_akademik = '%s'", kode_MK, kode_dosen, txtKelas.getText(), cmbTahunAkademik.getSelectedItem().toString());

            try {
                int i = Integer.parseInt(MysqlConnect.getDbCon().ExecuteScalar(check));

                if (i != 0) {
                    JOptionPane.showMessageDialog(null, "Data ini sudah ada");
                    return;
                }

                String Qry_insert = String.format(
                        "INSERT INTO pengampu(kode_mk,kode_dosen,kelas,tahun_akademik) VALUES(%d,%d,'%s','%s')", kode_MK, kode_dosen, txtKelas.getText().trim(), cmbTahunAkademik.getSelectedItem().toString());
                MysqlConnect.getDbCon().insert(Qry_insert);

            } catch (SQLException ex) {
                Logger.getLogger(FrmPengampu.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        _selectedkode = -1; //set to "-1" agar disign sebagai databaru
        txtKelas.setText("");

        setButtonStatus(true, false, false);

        try {
            loadData(cmbSemester.getSelectedItem() == "GANJIL" ? 1 : 0);
        } catch (SQLException ex) {
            Logger.getLogger(FrmPengampu.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_btnSimpanActionPerformed

    private void setButtonStatus(boolean bBaru, boolean bBatal, boolean bSimpan) {
        btnBaru.setEnabled(bBaru);
        btnBatal.setEnabled(bBatal);
        btnSimpan.setEnabled(bSimpan);
    }
    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        //
        setButtonStatus(true, false, false);
        txtKelas.setText("");
    }//GEN-LAST:event_btnBatalActionPerformed

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        // TODO add your handling code here:

        int row = jTable1.getSelectedRow();
        
        setButtonStatus(false, true, true);
        _selectedkode = Integer.parseInt(jTable1.getModel().getValueAt(row, 0).toString());
        cmbMataKuliah.setSelectedItem(jTable1.getModel().getValueAt(row, 2).toString());
        cmbDosen.setSelectedItem(jTable1.getModel().getValueAt(row, 4).toString());
        txtKelas.setText(jTable1.getModel().getValueAt(row, 5).toString());
        cmbTahunAkademik.setSelectedItem(jTable1.getModel().getValueAt(row, 6).toString());

    }//GEN-LAST:event_jTable1MousePressed

    private void jScrollPane1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane1KeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {

            if (JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus data ini?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }

            int row = jTable1.getSelectedRow();
            _selectedkode = Integer.parseInt(jTable1.getModel().getValueAt(row, 0).toString());
            //JOptionPane.showMessageDialog(null, _selectedkode);
            String Qry_Delete = "DELETE FROM pengampu where kode =  " + _selectedkode;
            try {
                MysqlConnect.getDbCon().ExecuteNonQuery(Qry_Delete);
            } catch (SQLException ex) {
                Logger.getLogger(FrmPengampu.class.getName()).log(Level.SEVERE, null, ex);
            }

            _selectedkode = -1;
            setButtonStatus(true, false, false);
            try {
                loadData(cmbSemester.getSelectedItem() == "GANJIL" ? 1 : 0);
            } catch (SQLException ex) {
                Logger.getLogger(FrmPengampu.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }//GEN-LAST:event_jTable1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBaru;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox cmbDosen;
    private javax.swing.JComboBox cmbMataKuliah;
    private javax.swing.JComboBox cmbSemester;
    private javax.swing.JComboBox cmbTahunAkademik;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtKelas;
    // End of variables declaration//GEN-END:variables

}

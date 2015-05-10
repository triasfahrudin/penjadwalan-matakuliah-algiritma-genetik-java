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
import static java.lang.Boolean.FALSE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author theultimate7
 */
public class FrmWaktuTidakBersedia extends javax.swing.JInternalFrame {

    /**
     * Creates new form FrmWaktuTidakBersedia
     *
     * @throws java.sql.SQLException
     */
    private final int[] kodeDosen;

    public FrmWaktuTidakBersedia() throws SQLException {
        initComponents();

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

        loadData(kodeDosen[0]);
        MyItemListener actionListener = new MyItemListener();
        cmbDosen.addItemListener(actionListener);
        

    }

    private void loadData(int kode_dosen) throws SQLException {
        int hari_row = util.getCount("SELECT * FROM hari", "kode");
        int jam_row = util.getCount("SELECT * FROM jam", "kode");
        int waktu_tidak_bersedia = util.getCount(
                "SELECT * FROM waktu_tidak_bersedia "
                + "WHERE kode_dosen=" + kode_dosen, "kode_hari");

        ResultSet dtHari = MysqlConnect.getDbCon().ExecuteQuery("SELECT nama,kode FROM hari");
        ResultSet dtJam = MysqlConnect.getDbCon().ExecuteQuery("SELECT range_jam,kode FROM jam");

        ResultSet dt = MysqlConnect.getDbCon().ExecuteQuery(
                "SELECT kode_hari,kode_jam "
                + "FROM waktu_tidak_bersedia "
                + "WHERE kode_dosen =" + kode_dosen);

        tabelTidakBersedia.removeAll();

        String column_names[] = {"Hari", "Jam", "Tidak Bersedia", "kodehari", "kodejam"};
        DefaultTableModel model = new DefaultTableModel(column_names, 0);

        //Object[][] rowData = {};
        while (dtHari.next()) {
            dtJam.beforeFirst();
            while (dtJam.next()) {

                String rowHari = dtHari.getString("nama");
                String rowKodeHari = dtHari.getString("kode");

                String rowJam = dtJam.getString("range_jam");
                String rowKodeJam = dtJam.getString("kode");

                dt.beforeFirst();
                boolean checked = false;
                while (dt.next()) {
                    String hariTidakBersedia = dt.getString("kode_hari");
                    String jamTidakBersedia = dt.getString("kode_jam");

                    if (rowKodeHari.equals(hariTidakBersedia) && rowKodeJam.equals(jamTidakBersedia)) {
                        checked = true;
                    }
                }

                model.addRow(new Object[]{rowHari, rowJam, checked, rowKodeHari, rowKodeJam});

            }
        }

        tabelTidakBersedia.setModel(model);

        //ngasal ye.....
        tabelTidakBersedia.removeColumn(tabelTidakBersedia.getColumnModel().getColumn(3));
        tabelTidakBersedia.removeColumn(tabelTidakBersedia.getColumnModel().getColumn(3));

        TableColumn tc = tabelTidakBersedia.getColumnModel().getColumn(2);
        tc.setCellEditor(tabelTidakBersedia.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(tabelTidakBersedia.getDefaultRenderer(Boolean.class));

        tabelTidakBersedia.getModel().addTableModelListener(new MyTableModelListener(tabelTidakBersedia));
    }

    class MyTableModelListener implements TableModelListener {

        JTable table;

        MyTableModelListener(JTable table) {
            this.table = table;
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();
            int index = e.getColumn();
            

            switch (e.getType()) {
                case TableModelEvent.INSERT:
                    for (int i = firstRow; i <= lastRow; i++) {
                        System.out.println(i);
                    }
                    break;
                case TableModelEvent.UPDATE:
                    if (firstRow == TableModelEvent.HEADER_ROW) {
                        if (index == TableModelEvent.ALL_COLUMNS) {
                            System.out.println("A column was added");
                        } else {
                            System.out.println(index + "in header changed");
                        }
                    } else {
                        for (int i = firstRow; i <= lastRow; i++) {
                            if (index == TableModelEvent.ALL_COLUMNS) {
                                System.out.println("All columns have changed");
                            } else {
                                //System.out.println(lastRow);
                                //tabelTidakBersedia.getModel().setValueAt(Boolean.TRUE, i, 2);
                                System.out.println(tabelTidakBersedia.getModel().getValueAt(lastRow, 2));
                                Object status = tabelTidakBersedia.getModel().getValueAt(lastRow, 2);
                                if(status.equals(Boolean.FALSE)){
                                    cbSelectAll.setSelected(FALSE);
                                }
                            }
                        }
                        
                    }
                    break;
                case TableModelEvent.DELETE:
                    for (int i = firstRow; i <= lastRow; i++) {
                        System.out.println(i);
                    }
                    break;                    
            }
        }

        //@Override
//        public void tableChanged(TableModelEvent e) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
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
        jLabel1 = new javax.swing.JLabel();
        cmbDosen = new javax.swing.JComboBox();
        btnSimpan = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelTidakBersedia = new javax.swing.JTable();
        cbSelectAll = new javax.swing.JCheckBox();

        setClosable(true);
        setTitle("Form Waktu Tidak Bersedia Dosen");

        jLabel1.setText("Dosen");

        cmbDosen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbDosen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbDosenItemStateChanged(evt);
            }
        });

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        tabelTidakBersedia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hari", "Jam", "Tidak Bersedia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelTidakBersedia.setColumnSelectionAllowed(true);
        tabelTidakBersedia.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tabelTidakBersediaPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tabelTidakBersedia);
        tabelTidakBersedia.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tabelTidakBersedia.getColumnModel().getColumnCount() > 0) {
            tabelTidakBersedia.getColumnModel().getColumn(0).setResizable(false);
            tabelTidakBersedia.getColumnModel().getColumn(1).setResizable(false);
            tabelTidakBersedia.getColumnModel().getColumn(2).setResizable(false);
        }

        cbSelectAll.setText("Pilih Semua");
        cbSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSelectAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbDosen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnSimpan))
                    .addComponent(cbSelectAll))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbDosen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSimpan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSelectAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    class MyItemListener implements ItemListener {

        // This method is called only if a new item has been selected.
        @Override
        public void itemStateChanged(ItemEvent evt) {
            JComboBox cb = (JComboBox) evt.getSource();

            Object item = evt.getItem();

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    // Item was just selected
                    loadData(kodeDosen[cb.getSelectedIndex()]);
                    //JOptionPane.showMessageDialog(null, kodeDosen[cb.getSelectedIndex()]);
                } catch (SQLException ex) {
                    Logger.getLogger(FrmWaktuTidakBersedia.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }


    private void cmbDosenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbDosenItemStateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_cmbDosenItemStateChanged

    private void deleteWaktuTidakBersedia(int kode_dosen) {
        try {
            MysqlConnect.getDbCon().ExecuteNonQuery(
                    "DELETE FROM waktu_tidak_bersedia "
                    + "WHERE kode_dosen =" + kode_dosen);
        } catch (SQLException ex) {
            Logger.getLogger(FrmWaktuTidakBersedia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
        int kode_dosen = kodeDosen[cmbDosen.getSelectedIndex()];
        deleteWaktuTidakBersedia(kode_dosen);
        for (int i = 0; i < tabelTidakBersedia.getRowCount(); i++) {

            if (tabelTidakBersedia.getModel().getValueAt(i, 2) == Boolean.TRUE) {
                int _kode_hari = Integer.parseInt(tabelTidakBersedia.getModel().getValueAt(i, 3).toString());
                int _kode_jam = Integer.parseInt(tabelTidakBersedia.getModel().getValueAt(i, 4).toString());

                try {
                    MysqlConnect.getDbCon().insert(
                            "INSERT INTO waktu_tidak_bersedia(kode_dosen,kode_hari, kode_jam) "
                            + "VALUE (" + kode_dosen + "," + _kode_hari + "," + _kode_jam + ")");
                } catch (SQLException ex) {
                    Logger.getLogger(FrmWaktuTidakBersedia.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Data telah tersimpan");

    }//GEN-LAST:event_btnSimpanActionPerformed

    private void tabelTidakBersediaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tabelTidakBersediaPropertyChange
        // TODO add your handling code here:
        //JOptionPane.showMessageDialog(null, "Populasi harus kelipatan 2");
    }//GEN-LAST:event_tabelTidakBersediaPropertyChange

    private void cbSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSelectAllActionPerformed
        // TODO add your handling code here:
        if (cbSelectAll.isSelected()) {
            //JOptionPane.showMessageDialog(null, "SELECTED");
            for (int i = 0; i < tabelTidakBersedia.getRowCount(); i++) {
                tabelTidakBersedia.getModel().setValueAt(Boolean.TRUE, i, 2);
            }
        } else {
            //   JOptionPane.showMessageDialog(null, "NOT SELECTED");
            for (int i = 0; i < tabelTidakBersedia.getRowCount(); i++) {
                tabelTidakBersedia.getModel().setValueAt(Boolean.FALSE, i, 2);
            }
        }


    }//GEN-LAST:event_cbSelectAllActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSimpan;
    private javax.swing.JCheckBox cbSelectAll;
    private javax.swing.JComboBox cmbDosen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelTidakBersedia;
    // End of variables declaration//GEN-END:variables
}

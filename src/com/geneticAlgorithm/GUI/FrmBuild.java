/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geneticAlgorithm.GUI;

import com.geneticAlgorithm.database.MysqlConnect;
import com.geneticAlgorithm.engine.ClassGenetik;
import com.geneticAlgorithm.util.util;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import static java.lang.Boolean.TRUE;
import static java.lang.Thread.sleep;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author theultimate7
 */
public class FrmBuild extends javax.swing.JInternalFrame {

    private int maxIterasi;
    private int populasi;
    private ClassGenetik genetik;
    private int kode_jumat;
    private int kode_dhuhur;
    private int[] range_jumat;
    private Task task;
    boolean found = false;

    //solution
    private int[][] jadwal_kuliah;

    class Task extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            setProgress(0);

            int loop = 0;
            while (loop < maxIterasi && !this.isCancelled()) {

                //if (this.isCancelled()) break;
                float[] fitness = genetik.HitungFitness();
                genetik.Seleksi(fitness);
                genetik.StartCrossOver();

                float[] fitnessAfterMutation = genetik.Mutasi();
                int i = 0;
                float TotalFitness = 0;
                sleep(1);
                while (i < fitnessAfterMutation.length && !this.isCancelled()) {

                    if (util.almostEquals(fitnessAfterMutation[i], 1.0, 0)) {
                        found = true;
                        task.cancel(true);
                        //JOptionPane.showMessageDialog(null, "KETEMU!");
                        jadwal_kuliah = genetik.GetIndividu(i);

                        /*
                         MysqlConnect.getDbCon().ExecuteNonQuery("TRUNCATE TABLE jadwal_kuliah");
                         for (int[] jadwal_kuliah1 : jadwal_kuliah) {
                         MysqlConnect.getDbCon().insert(String.format("INSERT INTO jadwal_kuliah(kode_pengampu,kode_jam,kode_hari,kode_ruang) VALUES(%d,%d,%d,%d)", jadwal_kuliah1[0], jadwal_kuliah1[1], jadwal_kuliah1[2], jadwal_kuliah1[3]));
                         }

                         String query = "SELECT e.nama as `Hari`,"
                         + "          Concat_WS('-',  concat('(', g.kode), concat( (SELECT kode"
                         + "                                  FROM jam "
                         + "                                  WHERE kode = (SELECT jm.kode "
                         + "                                                FROM jam jm  "
                         + "                                                WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1)),')')) as SESI, "
                         + " 		  Concat_WS('-', MID(g.range_jam,1,5),"
                         + "                (SELECT MID(range_jam,7,5) "
                         + "                 FROM jam "
                         + "                 WHERE kode = (SELECT jm.kode "
                         + "                               FROM jam jm "
                         + "                               WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1))) as Jam_Kuliah, "
                         + "        c.nama as `Nama MK`,"
                         + "        c.sks as SKS,"
                         + "        c.semester as Smstr,"
                         + "        b.kelas as Kelas,"
                         + "        d.nama as Dosen,"
                         + "        f.nama as Ruang "
                         + "FROM jadwal_kuliah a "
                         + "LEFT JOIN pengampu b "
                         + "ON a.kode_pengampu = b.kode "
                         + "LEFT JOIN mata_kuliah c "
                         + "ON b.kode_mk = c.kode "
                         + "LEFT JOIN dosen d "
                         + "ON b.kode_dosen = d.kode "
                         + "LEFT JOIN hari e "
                         + "ON a.kode_hari = e.kode "
                         + "LEFT JOIN ruang f "
                         + "ON a.kode_ruang = f.kode "
                         + "LEFT JOIN jam g "
                         + "ON a.kode_jam = g.kode "
                         + "order by e.nama desc,Jam_Kuliah asc;";

                        
                       
                         ResultSet rs = MysqlConnect.getDbCon().ExecuteQuery(query);                      
                        
                         jTable1.setModel(util.buildTableModel(rs));
                         //jTable1.setModel(DbUtils.resultSetToTableModel(rs));                        
                         jTable1.setAutoCreateRowSorter(true);
                         */
                    }

                    TotalFitness += fitnessAfterMutation[i];
                    i++;
                }

                Float Rata2Fitness = TotalFitness / populasi;
                lblRata2Fitness.setText(Float.toString(Rata2Fitness));
                jLabel7.setText(Integer.toString(loop));
                setProgress((loop + 1) * 100 / maxIterasi);
                loop++;

            }

            return null;
        }

    }

    /**
     * Creates new form FrmBuild
     */
    public FrmBuild() {
        initComponents();
        jTable1.setComponentPopupMenu(jPopupMenu2);
        /*
         <genetik>
         <populasi>10</populasi>
         <crossover>0.70</crossover>
         <kode_jumat>5</kode_jumat>
         <range_jumat>4-5-6</range_jumat>
         <kode_dhuhur>6</kode_dhuhur>
         <max_iterasi>10000</max_iterasi>
         </genetik>

         */
        try {
            File xmlFile = new File("config.xml");

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = documentBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("genetik");

            Node node = nodeList.item(0);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element _genetik = (Element) node;
                txtPopulasi.setText(_genetik.getElementsByTagName("populasi").item(0).getTextContent());
                txtCrossOver.setText(_genetik.getElementsByTagName("crossover").item(0).getTextContent());
                txtMutasi.setText(_genetik.getElementsByTagName("mutasi").item(0).getTextContent());
                txtIterasi.setText(_genetik.getElementsByTagName("max_iterasi").item(0).getTextContent());
                kode_jumat = Integer.parseInt(_genetik.getElementsByTagName("kode_jumat").item(0).getTextContent());
                kode_dhuhur = Integer.parseInt(_genetik.getElementsByTagName("kode_dhuhur").item(0).getTextContent());

                String[] strArr = _genetik.getElementsByTagName("range_jumat").item(0).getTextContent().split("-");
                range_jumat = new int[strArr.length];
                for (int i = 0; i < strArr.length; i++) {
                    range_jumat[i] = Integer.parseInt(strArr[i]);
                }

            }

        } catch (IOException | ParserConfigurationException | SAXException e) {
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

        jPopupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbTahunAkademik = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbSemester = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txtPopulasi = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCrossOver = new javax.swing.JTextField();
        txtMutasi = new javax.swing.JTextField();
        txtIterasi = new javax.swing.JTextField();
        btnProses = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        progressBar = new javax.swing.JProgressBar();
        jLabel8 = new javax.swing.JLabel();
        lblRata2Fitness = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnSaveToExcel = new javax.swing.JButton();

        jMenuItem1.setText("Reset Sort Order");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem1);

        setClosable(true);
        setTitle("Build Penjadwalan");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Probabilitas CrossOver");

        cmbTahunAkademik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2011/2012", "2012/2013", "2013/2014", "2014/2015", "2015/2016", "2016/2017", "2017/2018", "2018/2019", "2019/2020" }));

        jLabel2.setText("Semester");

        cmbSemester.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GANJIL", "GENAP" }));

        jLabel3.setText("Jumlah Populasi");

        jLabel4.setText("Tahun Akademik");

        jLabel5.setText("Probabilitas Mutasi");

        jLabel6.setText("Iterasi");

        btnProses.setText("PROSES");
        btnProses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProsesActionPerformed(evt);
            }
        });

        btnStop.setText("STOP");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(50, 50, 50))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(11, 11, 11)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cmbSemester, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbTahunAkademik, 0, 172, Short.MAX_VALUE))
                    .addComponent(txtPopulasi, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnProses))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtIterasi, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                            .addComponent(txtMutasi)
                            .addComponent(txtCrossOver))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbTahunAkademik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtCrossOver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtMutasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPopulasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtIterasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProses)
                    .addComponent(btnStop))
                .addContainerGap(17, Short.MAX_VALUE))
        );

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
        jScrollPane3.setViewportView(jTable1);

        jLabel8.setText("Rata-Rata Fitness : ");

        lblRata2Fitness.setText("#");
        lblRata2Fitness.setToolTipText("");

        jLabel7.setText("#");
        jLabel7.setToolTipText("");

        jLabel9.setText("Generasi Ke: ");

        btnSaveToExcel.setText("Save To Excel");
        btnSaveToExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveToExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(lblRata2Fitness))))
                        .addGap(0, 47, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSaveToExcel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(lblRata2Fitness))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSaveToExcel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("empty-statement")
    private void btnProsesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesActionPerformed
        // TODO add your handling code here:
        //jTable1.setModel(null);
        final int GANJIL = 1;
        final int GENAP = 0;
        found = false;
        int jenis_semester = cmbSemester.getSelectedItem().toString().equals("GANJIL") ? GANJIL : GENAP;
        String tahun_akademik = cmbTahunAkademik.getSelectedItem().toString();
        populasi = Integer.parseInt(txtPopulasi.getText());
        if (populasi % 2 != 0) {
            JOptionPane.showMessageDialog(null, "Populasi harus kelipatan 2");
            return;
        }
        float crossOver = Float.parseFloat(txtCrossOver.getText());
        float mutasi = Float.parseFloat(txtMutasi.getText());
        maxIterasi = Integer.parseInt(txtIterasi.getText());
        genetik = new ClassGenetik(jenis_semester, tahun_akademik, populasi,
                crossOver, mutasi, kode_jumat, range_jumat, kode_dhuhur);

        try {
            final long startTime = System.currentTimeMillis();
            if (!Objects.equals(genetik.AmbilData(), TRUE)) {
                JOptionPane.showConfirmDialog(null, "Kemungkinan data tidak ada untuk Tahun Akademik dan Semester yang terpilih!", "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                return;
            };
            genetik.Inisialisasi();

            progressBar.setStringPainted(true);
            task = new Task();
            task.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    if (null != e.getPropertyName()) {
                        switch (e.getPropertyName()) {
                            case "progress":
                                btnProses.setEnabled(false);
                                btnStop.setEnabled(true);
                                int progress = (Integer) e.getNewValue();
                                progressBar.setValue(progress);
                                break;
                            case "state":
                                switch ((StateValue) e.getNewValue()) {
                                    case DONE:
                                        btnProses.setEnabled(true);
                                        btnStop.setEnabled(false);
                                        task = null;
                                        if (found) {
                                            long estimatedTime = System.currentTimeMillis() - startTime;
                                            JOptionPane.showMessageDialog(null, "Solusi Ditemukan dalam " + estimatedTime + " milidetik");

                                            try {
                                                MysqlConnect.getDbCon().ExecuteNonQuery("TRUNCATE TABLE jadwal_kuliah");
                                            } catch (SQLException ex) {
                                                Logger.getLogger(FrmBuild.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            for (int[] jadwal_kuliah1 : jadwal_kuliah) {
                                                try {
                                                    MysqlConnect.getDbCon().insert(String.format("INSERT INTO jadwal_kuliah(kode_pengampu,kode_jam,kode_hari,kode_ruang) VALUES(%d,%d,%d,%d)", jadwal_kuliah1[0], jadwal_kuliah1[1], jadwal_kuliah1[2], jadwal_kuliah1[3]));
                                                } catch (SQLException ex) {
                                                    Logger.getLogger(FrmBuild.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            }

                                            String query = "SELECT e.nama as `Hari`,"
                                                    + "          Concat_WS('-',  concat('(', g.kode), concat( (SELECT kode"
                                                    + "                                  FROM jam "
                                                    + "                                  WHERE kode = (SELECT jm.kode "
                                                    + "                                                FROM jam jm  "
                                                    + "                                                WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1)),')')) as SESI, "
                                                    + " 		  Concat_WS('-', MID(g.range_jam,1,5),"
                                                    + "                (SELECT MID(range_jam,7,5) "
                                                    + "                 FROM jam "
                                                    + "                 WHERE kode = (SELECT jm.kode "
                                                    + "                               FROM jam jm "
                                                    + "                               WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1))) as Jam_Kuliah, "
                                                    + "        c.nama as `Nama MK`,"
                                                    + "        c.sks as SKS,"
                                                    + "        c.semester as Smstr,"
                                                    + "        b.kelas as Kelas,"
                                                    + "        d.nama as Dosen,"
                                                    + "        f.nama as Ruang "
                                                    + "FROM jadwal_kuliah a "
                                                    + "LEFT JOIN pengampu b "
                                                    + "ON a.kode_pengampu = b.kode "
                                                    + "LEFT JOIN mata_kuliah c "
                                                    + "ON b.kode_mk = c.kode "
                                                    + "LEFT JOIN dosen d "
                                                    + "ON b.kode_dosen = d.kode "
                                                    + "LEFT JOIN hari e "
                                                    + "ON a.kode_hari = e.kode "
                                                    + "LEFT JOIN ruang f "
                                                    + "ON a.kode_ruang = f.kode "
                                                    + "LEFT JOIN jam g "
                                                    + "ON a.kode_jam = g.kode "
                                                    + "order by e.nama desc,Jam_Kuliah asc;";

                                            ResultSet rs = null;
                                            try {
                                                rs = MysqlConnect.getDbCon().ExecuteQuery(query);
                                            } catch (SQLException ex) {
                                                Logger.getLogger(FrmBuild.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                            try {
                                                jTable1.setModel(util.buildTableModel(rs));
                                            } catch (SQLException ex) {
                                                Logger.getLogger(FrmBuild.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            //jTable1.setModel(DbUtils.resultSetToTableModel(rs));                        
                                            jTable1.setAutoCreateRowSorter(true);

                                        }
                                        break;
                                }
                                break;
                        }
                    }
                }
            });
            task.execute();

        } catch (SQLException e) {
        }


    }//GEN-LAST:event_btnProsesActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        // TODO add your handling code here:
        task.cancel(true);
    }//GEN-LAST:event_btnStopActionPerformed

    private void save2xl(String xlFilePath) throws SQLException, IOException, WriteException {
        String query = "SELECT  e.nama as Hari,"
                + "          Concat_WS('-',  concat('(', g.kode), concat( (SELECT kode"
                + "                                  FROM jam "
                + "                                  WHERE kode = (SELECT jm.kode "
                + "                                                FROM jam jm  "
                + "                                                WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1)),')')) as SESI, "
                + " 		  Concat_WS('-', MID(g.range_jam,1,5),"
                + "                (SELECT MID(range_jam,7,5) "
                + "                 FROM jam "
                + "                 WHERE kode = (SELECT jm.kode "
                + "                               FROM jam jm "
                + "                               WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1))) as Jam_Kuliah, "
                + "        c.nama as `Nama MK`,"
                + "        c.sks as SKS,"
                + "        c.semester as Smstr,"
                + "        b.kelas as Kelas,"
                + "        d.nama as Dosen,"
                + "        f.nama as Ruang "
                + "FROM jadwal_kuliah a "
                + "LEFT JOIN pengampu b "
                + "ON a.kode_pengampu = b.kode "
                + "LEFT JOIN mata_kuliah c "
                + "ON b.kode_mk = c.kode "
                + "LEFT JOIN dosen d "
                + "ON b.kode_dosen = d.kode "
                + "LEFT JOIN hari e "
                + "ON a.kode_hari = e.kode "
                + "LEFT JOIN ruang f "
                + "ON a.kode_ruang = f.kode "
                + "LEFT JOIN jam g "
                + "ON a.kode_jam = g.kode "
                + "order by e.nama desc,Jam_Kuliah asc;";

        ResultSet rs = MysqlConnect.getDbCon().ExecuteQuery(query);
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();
        List details = new ArrayList();
        // Read and store data in list variable.
        while (rs.next()) {
            for (int n = 1; n <= numberOfColumns; n++) {
                details.add(rs.getString(n));
            }
        }

        // Excel file properties
        File file = new File(xlFilePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook w = Workbook.createWorkbook(file, wbSettings);
        w.createSheet("Data", 0);
        WritableSheet s = w.getSheet(0);
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 10,
                WritableFont.NO_BOLD);
        WritableCellFormat cf = new WritableCellFormat(wf);
        //cf.setWrap(true);

        Iterator itr = details.iterator();
        Label lbl;

        // Write column header
        int col = 0;
        for (int j = 1; j <= numberOfColumns; j++) {
            lbl = new Label(col, 0, rsMetaData.getColumnLabel(j), cf);
            s.addCell(lbl);
            col++;
        }

        // Write content
        int row = 1;
        while (itr.hasNext()) {
            for (int column = 0; column < numberOfColumns; column++) {
                lbl = new Label(column, row, (String) itr.next(), cf);
                s.addCell(lbl);
            }
            row++;
        }
        w.write();
        w.close();
    }

    private void btnSaveToExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveToExcelActionPerformed
        // TODO add your handling code here:
        try {
            save2xl("JadwalKuliah.xls");
            JOptionPane.showMessageDialog(null, "Data telah tersimpan di JadwalKuliah.xls");
        } catch (SQLException | IOException | WriteException e) {

        }


    }//GEN-LAST:event_btnSaveToExcelActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            String query = "SELECT e.nama as `Hari`,"
                    + "          Concat_WS('-',  concat('(', g.kode), concat( (SELECT kode"
                    + "                                  FROM jam "
                    + "                                  WHERE kode = (SELECT jm.kode "
                    + "                                                FROM jam jm  "
                    + "                                                WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1)),')')) as SESI, "
                    + " 		  Concat_WS('-', MID(g.range_jam,1,5),"
                    + "                (SELECT MID(range_jam,7,5) "
                    + "                 FROM jam "
                    + "                 WHERE kode = (SELECT jm.kode "
                    + "                               FROM jam jm "
                    + "                               WHERE MID(jm.range_jam,1,5) = MID(g.range_jam,1,5)) + (c.sks - 1))) as Jam_Kuliah, "
                    + "        c.nama as `Nama MK`,"
                    + "        c.sks as SKS,"
                    + "        c.semester as Smstr,"
                    + "        b.kelas as Kelas,"
                    + "        d.nama as Dosen,"
                    + "        f.nama as Ruang "
                    + "FROM jadwal_kuliah a "
                    + "LEFT JOIN pengampu b "
                    + "ON a.kode_pengampu = b.kode "
                    + "LEFT JOIN mata_kuliah c "
                    + "ON b.kode_mk = c.kode "
                    + "LEFT JOIN dosen d "
                    + "ON b.kode_dosen = d.kode "
                    + "LEFT JOIN hari e "
                    + "ON a.kode_hari = e.kode "
                    + "LEFT JOIN ruang f "
                    + "ON a.kode_ruang = f.kode "
                    + "LEFT JOIN jam g "
                    + "ON a.kode_jam = g.kode "
                    + "order by e.nama desc,Jam_Kuliah asc;";

            /**
             * ****************************************
             */
            ResultSet rs = MysqlConnect.getDbCon().ExecuteQuery(query);
            jTable1.setModel(util.buildTableModel(rs));

            jTable1.setAutoCreateRowSorter(true);
        } catch (SQLException ex) {
            Logger.getLogger(FrmBuild.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProses;
    private javax.swing.JButton btnSaveToExcel;
    private javax.swing.JButton btnStop;
    private javax.swing.JComboBox cmbSemester;
    private javax.swing.JComboBox cmbTahunAkademik;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblRata2Fitness;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField txtCrossOver;
    private javax.swing.JTextField txtIterasi;
    private javax.swing.JTextField txtMutasi;
    private javax.swing.JTextField txtPopulasi;
    // End of variables declaration//GEN-END:variables
}

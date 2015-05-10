/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geneticAlgorithm.util;

import com.geneticAlgorithm.database.MysqlConnect;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author theultimate7
 */
public class util {

    public static boolean almostEquals(double[] d1, double[] d2, double eps) {
        for (int i = 0; i < d1.length; i++) {
            double v1 = d1[i];
            double v2 = d2[i];
            if (!almostEquals(v1, v2, eps)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Numbers that are closer than this are considered equal by almostEquals.
     */
    public static double EPSILON = 0.000001;

    public static boolean almostEquals(double d1, double d2) {
        return almostEquals(d1, d2, EPSILON);
    }

    public static boolean almostEquals(double d1, double d2, double epsilon) {
        return Math.abs(d1 - d2) <= epsilon;
    }

    public static int getCount(String sql, String nama_kolom) throws SQLException {
        ResultSet dt = MysqlConnect.getDbCon().ExecuteQuery(sql);
        while (dt.next()) {
            String kode = dt.getString(nama_kolom);
        }
        dt.last();

        return dt.getRow();
    }

    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnLabel(column));
        }

        // data of the table
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }   



}

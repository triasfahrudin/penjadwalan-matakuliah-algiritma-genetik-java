/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.geneticAlgorithm.database;

import com.mysql.jdbc.Connection;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.sql.DriverManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @desc A singleton database access class for MySQL
 * @author Ramindu
 */
public final class MysqlConnect {

    public Connection conn;
    private Statement statement;
    public static MysqlConnect db;

    private MysqlConnect() {

        try {
            File xmlFile = new File("config.xml");

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = documentBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("database");

            Node node = nodeList.item(0);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element database = (Element) node;
                String url = database.getElementsByTagName("url").item(0).getTextContent();
                String dbName = database.getElementsByTagName("database").item(0).getTextContent();
                String driver = database.getElementsByTagName("driver").item(0).getTextContent();
                String userName = database.getElementsByTagName("user").item(0).getTextContent();
                String password = database.getElementsByTagName("password").item(0).getTextContent();

                Class.forName(driver).newInstance();
                this.conn = (Connection) DriverManager.getConnection(url + dbName, userName, password);
            }

        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException | ParserConfigurationException | SAXException e) {
        }
    }

    /**
     *
     * @return MysqlConnect Database connection object
     */
    public static synchronized MysqlConnect getDbCon() {
        if (db == null) {
            db = new MysqlConnect();
        }
        return db;

    }

    public void ExecuteNonQuery(String query) throws SQLException {
        statement = db.conn.createStatement();
        statement.executeUpdate(query);
    }

    /**
     *
     * @param query String The ExecuteQuery to be executed
     * @return a ResultSet object containing the results or null if not
     * available
     * @throws SQLException
     */
    public ResultSet ExecuteQuery(String query) throws SQLException {
        statement = db.conn.createStatement();
        ResultSet res = statement.executeQuery(query);
        return res;
    }
    
    public String ExecuteScalar(String query) throws SQLException{
        statement = db.conn.createStatement();
        ResultSet res = statement.executeQuery(query);
        res.next();
        return res.getString(1);
    }

    /**
     * @desc Method to insert data to a table
     * @param insertQuery String The Insert ExecuteQuery
     * @return boolean
     * @throws SQLException
     */
    public int insert(String insertQuery) throws SQLException {
        statement = db.conn.createStatement();
        int result = statement.executeUpdate(insertQuery);
        return result;

    }

}

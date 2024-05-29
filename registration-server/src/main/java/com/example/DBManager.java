package com.example;

import java.sql.*;

public class DBManager {
    public String DB_URL;
    public String user;
    public String password;
    private PreparedStatement pstSelect;
    private PreparedStatement pstInsert;
    private PreparedStatement pstRegister;

    public DBManager(String URL, String user, String password) {
        this.DB_URL = URL;
        this.user = user;
        this.password = password;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, this.user, this.password);
            pstInsert = conn.prepareStatement(
                    "INSERT INTO measures (sensorName, sensorAddress, resourceName, resourceValue, timestamp ) " +
                            "VALUES (?, ?, ?, ?, ?);");
            pstRegister = conn.prepareStatement(
                    "INSERT INTO devices (name, address, type, sampling) VALUES (?, ?, ?, ?) ON DUPLICATE KEY address = ? , sampling = ?;");
            pstSelect = conn.prepareStatement(" SELECT address FROM devices WHERE name = ? ;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String select(String name){
        try {
            pstSelect.setString(1, name);
            ResultSet rs = pstSelect.executeQuery();
            if (rs.next()) {
                return rs.getString("address");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void insert(String sensorName, String sensorAddress, String resourceName, int resourceValue, int timestamp) {
        try {
            pstInsert.setString(1, sensorName);
            pstInsert.setString(2, sensorAddress);
            pstInsert.setString(3, resourceName);
            pstInsert.setInt(4, resourceValue);
            pstInsert.setInt(5, timestamp);
            pstInsert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void register(String name, String address, String type, int sampling) {
        try {
            pstRegister.setString(1, name);
            pstRegister.setString(2, address);
            pstRegister.setString(3, type);
            pstRegister.setInt(4, sampling);
            pstRegister.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
package com.example;

import java.sql.*;

public class DBManager {
    public String DB_URL;
    public String user;
    public String password;
    private PreparedStatement pstSelect;
    private PreparedStatement pstRegister;
    private PreparedStatement pstInsertIlluminationMeasures;
    private PreparedStatement pstInsertSprinklerMeasures;

    public DBManager(String URL, String user, String password) {
        this.DB_URL = URL;
        this.user = user;
        this.password = password;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, this.user, this.password);
            
            pstRegister = conn.prepareStatement(
                "INSERT INTO devices (name, address, type, sampling) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE address = VALUES(address), sampling = VALUES(sampling);");
            
            pstSelect = conn.prepareStatement(" SELECT address FROM devices WHERE name = ? ;");

            pstInsertIlluminationMeasures = conn.prepareStatement(
                "INSERT INTO illumination (co2, light, phase, timestamp) VALUES (?, ?, ?, ?)");

            pstInsertSprinklerMeasures = conn.prepareStatement(
                "INSERT INTO soil (moisture, temperature, timestamp) VALUES (?, ?, ?)");

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

    public void insertIlluminationMeasures(int co2, int light, int phase, int timestamp) {
        try {
            pstInsertIlluminationMeasures.setInt(1, co2);
            pstInsertIlluminationMeasures.setInt(2, light);
            pstInsertIlluminationMeasures.setInt(3, phase);
            pstInsertIlluminationMeasures.setInt(4, timestamp);
            
            pstInsertIlluminationMeasures.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSoilMeasures(int moisture, int temperature, int timestamp) {
        try {
            pstInsertSprinklerMeasures.setInt(1, moisture);
            pstInsertSprinklerMeasures.setInt(2, temperature);
            pstInsertSprinklerMeasures.setInt(3, timestamp);
            
            pstInsertSprinklerMeasures.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
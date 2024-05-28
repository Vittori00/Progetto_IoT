package com.example;

import java.sql.*;

public class DBManager {
    private String DB_URL;
    private String user;
    private String password;
    private PreparedStatement pstInsert;

    public DBManager(String URL, String user, String password){
        this.DB_URL = URL;
        this.user = user;
        this.password = password;

        try{
            Connection conn = DriverManager.getConnection(DB_URL, this.user, this.password);
            pstInsert = conn.prepareStatement(
                "INSERT INTO measures (sensorName, sensorAddress, resourceName, resourceValue, timestamp ) " +
                    "VALUES (?, ?, ?, ?, ?);"
            );

        }catch(SQLException e){
            e.printStackTrace();
        }
    }


    public void insert(String sensorName, String sensorAddress, String resourceName, int resourceValue, int timestamp){
        try{
            pstInsert.setString(2, sensorName);
            pstInsert.setString(3, sensorAddress);
            pstInsert.setString(4, resourceName);
            pstInsert.setInt(5, resourceValue);
            pstInsert.setInt(6, timestamp);
            pstInsert.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
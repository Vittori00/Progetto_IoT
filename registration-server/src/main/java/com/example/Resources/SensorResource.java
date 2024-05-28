package com.example.Resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import org.json.JSONObject;

import com.example.CoapObserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SensorResource extends CoapResource {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/CottonNet";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    public SensorResource(String name) {
        super(name);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        String payload = exchange.getRequestText();
        JSONObject json = new JSONObject(payload);
        String name = json.getString("name");
        String address = exchange.getSourceAddress().getHostAddress();
        String type = json.getString("type");
        int sampling = json.getInt("sampling");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO devices (name, address, type, sampling) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, type);
            stmt.setInt(4, sampling);
            stmt.executeUpdate();
            exchange.respond("Actuator resource created");
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.respond("Internal Server Error");
        }

    }

    @Override
    public void handleGET(CoapExchange exchange) {
        StringBuilder response = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM devices WHERE type = 'sensor'";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                response.append("{sensor: \"").append(rs.getString("name"))
                        .append("\", address: \"").append(rs.getString("address"))
                        .append("\", sampling: \"").append(rs.getString("sampling"))
                        .append("\"}\n");
            }
            exchange.respond(response.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.respond("Internal Server Error");
        }
    }
}
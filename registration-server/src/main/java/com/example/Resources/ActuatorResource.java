package com.example.Resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActuatorResource extends CoapResource {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/CottonNet";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    public ActuatorResource(String name) {
        super(name);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        String payload = exchange.getRequestText();
        JSONObject json = new JSONObject(payload);
        String actuator = json.getString("actuator");
        String state = json.getString("state");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO actuators (actuator, state) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, actuator);
            stmt.setString(2, state);
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
            String query = "SELECT * FROM actuators";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                response.append("{actuator: \"").append(rs.getString("actuator"))
                        .append("\", state: \"").append(rs.getString("state")).append("\"}\n");
            }
            exchange.respond(response.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.respond("Internal Server Error");
        }
    }
}

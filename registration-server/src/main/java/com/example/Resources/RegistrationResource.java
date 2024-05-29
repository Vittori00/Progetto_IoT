package com.example.Resources;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.server.resources.CoapExchange;

import org.json.JSONObject;

import com.example.CoapObserver;
import com.example.DBManager;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationResource extends CoapResource {

    private DBManager dbManager = new DBManager("jdbc:mysql://localhost:3306/CottonNet", "admin", "admin");

    public RegistrationResource(String name) {
        super(name);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();

        InetAddress sensorAddress = exchange.getSourceAddress();
        CoapClient client = new CoapClient("coap://[" + sensorAddress.getHostAddress() + "]:5683/registration");
        CoapResponse response = client.get();

        String responseCode = response.getCode().toString();
        if(!responseCode.startsWith("2")){
            System.out.println("Error: " + responseCode);
            return;
        }

        String payload = response.getResponseText();
        System.out.println(sensorAddress.getHostAddress() + ": " + payload);
        String name = payload.substring(payload.indexOf(",</") + 2, payload.indexOf(">"));
        System.out.println(payload.substring(payload.indexOf(",</") + 2, payload.indexOf(">")));

        IlluminationResource resource = new IlluminationResource(name, sensorAddress.getHostAddress());
        observe(resource);
        dbManager.regi

        /*
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO devices (name, address, type, sampling) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, type);
            if (type.equals("sensor")) {
                stmt.setInt(4, sampling);
            }
            stmt.executeUpdate();
            if (type.equals("sensor")) {
                exchange.respond("Sensor resource created");
            } else {
                exchange.respond("Actuator resource created");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.respond("Internal Server Error");
        }
        

        if (type.equals("actuator")) {
            observe(new IlluminationResource(name, address));
        }
        */
    }

    /*
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
    */

    private static void observe(IlluminationResource resource){
        CoapObserver obs = new CoapObserver(resource);
        obs.startObserving();
    }
}
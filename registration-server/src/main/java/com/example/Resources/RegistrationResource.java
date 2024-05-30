package com.example.Resources;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import com.example.CoapObserver;
import com.example.DBManager;
import com.google.protobuf.TextFormat.ParseException;

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
        Response response;
        String nodeAddress = exchange.getSourceAddress().getHostAddress();
        String payloadString = exchange.getRequestText();
        System.out.println("Payload received: " + payloadString + " \nlunghezza: " + payloadString.length());
        System.out.println("IP address: " + nodeAddress + "\n");

        JSONObject json = new JSONObject(payloadString);
        String sensorName = (String) json.get("s");
        String sensorType = (String) json.get("t");
        int samplingTime = (int) json.get("c");
        System.out.println("Parsed JSON: " + json);
        // dbManager.register(sensorName, nodeAddress, sensorType , samplingTime);
        // //samplingTime sarà 0 negli attuatori
        if (sensorType.equals("sensor")) {
            dbManager.register(sensorName, nodeAddress, sensorType, samplingTime);
            response = new Response(CoAP.ResponseCode.CREATED);
            exchange.respond(response); //qui non si passa nulla
        } else {
            // stiamo registrando un attuatore
            dbManager.register(sensorName, nodeAddress, sensorType, samplingTime);
            // dobbiamo inviare nella risposta l'ip del sensore al quale fanno riferimento
            if (sensorName.equals("illumination")) {
                // attuatore di illuminazione
                dbManager.register(sensorName, nodeAddress, sensorType, samplingTime);
                String sensorReference = dbManager.select("sensor0");
                System.out.println("Sending Ip Sensore di riferimento: " + sensorReference + "\n");
                response = new Response(CoAP.ResponseCode.CREATED);
                response.setPayload(sensorReference); //passiamo l'ip del sensore a cui fa riferimento
                exchange.respond(response);
            } else {
                // attuatore di irrigazione
                dbManager.register(sensorName, nodeAddress, sensorType, samplingTime);
                String sensorReference = dbManager.select("sensor1");
                System.out.println("Sending Ip Sensore di riferimento: " + sensorReference + "\n");
                response = new Response(CoAP.ResponseCode.CREATED);
                response.setPayload(sensorReference); 
                exchange.respond(response);
            }
            
        }
        System.out.println("node at ip: " + nodeAddress + " registered");
        /// observe(new IlluminationResource(sensorType, nodeAddress)); solo se sensore
    }

    private static void observe(IlluminationResource resource) {
        CoapObserver obs = new CoapObserver(resource);
        obs.startObserving();
    }
}
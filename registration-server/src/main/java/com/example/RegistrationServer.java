package com.example;

import org.eclipse.californium.core.CoapServer;

public class RegistrationServer extends CoapServer {
    public static void main(String[] args) {
        CoapServer server = new CoapServer();
        server.add(new ActuatorResource("actuators"));
        server.start();
        System.out.println("CoAP server is running...");
    }
}
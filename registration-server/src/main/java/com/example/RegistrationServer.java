package com.example;

import org.eclipse.californium.core.CoapServer;

import com.example.Resources.RegistrationResource;

public class RegistrationServer extends CoapServer {
    public static void main(String[] args) {
        RegistrationServer server = new RegistrationServer();
        server.add(new RegistrationResource("registration"));
        server.start();
        System.out.println("CoAP server is running...");
    }
}
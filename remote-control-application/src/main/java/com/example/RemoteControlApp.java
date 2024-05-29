package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class RemoteControlApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/CottonNet";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    private static final String COAP_ACTUATORS_URI = "coap://localhost/actuators";
    private static final String COAP_SENSORS_URI = "coap://localhost/sensors";

    private static final String SPRINKLER_RESOURCE_URI = "coap://[fd00::203:3:3:3]/sampling";
    private static final String ILLUMINATION_RESOURCE_URI = "coap://[fd00::202:2:2:2]/sampling";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        CoapClient actuators = new CoapClient(COAP_ACTUATORS_URI);
        CoapClient sensors = new CoapClient(COAP_SENSORS_URI);

        while (true) {
            System.out.println("Remote Control Application");
            System.out.println("1. Show active devices");
            System.out.println("2. Set new sample timing");
            System.out.print("\nScegli un'opzione: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Show active devices
                    getActiveDevices();
                    break;
                case 2:
                    // Choose resource
                    System.out.println("Choose a resource:");
                    System.out.println("1. Illumination");
                    System.out.println("2. Sprinkler");

                    int resource = scanner.nextInt();
                    scanner.nextLine();

                    switch (resource) {
                        case 1:
                            System.out.print("Insert Illumination sample timing: ");
                            int illuminationSampling = scanner.nextInt();
                            setIlluminationSampling(illuminationSampling);
                            break;
                        case 2:
                            System.out.print("Insert Sprinkler sample timing: ");
                            int sprinklerSampling = scanner.nextInt();
                            setSprinklerSampling(sprinklerSampling);
                            break;
                        default:
                            System.out.println("Invalid resource");
                    }
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void setIlluminationSampling(int illuminationSampling) {

        CoapClient client = new CoapClient(ILLUMINATION_RESOURCE_URI);
        JSONObject json = new JSONObject();
        json.put("sampling", illuminationSampling);

        CoapResponse response = client.post(json.toString(), 0);
        if (response != null) {
            System.out.println("Response: " + response.getResponseText());
        } else {
            System.out.println("No response from server.");
        }
    }

    private static void setSprinklerSampling(int sprinklerSampling) {
        CoapClient client = new CoapClient(SPRINKLER_RESOURCE_URI);
        JSONObject json = new JSONObject();
        json.put("sampling", sprinklerSampling);

        CoapResponse response = client.post(json.toString(), 0);
        if (response != null) {
            System.out.println("Response: " + response.getResponseText());
        } else {
            System.out.println("No response from server.");
        }
    }



    private static void getActiveDevices(){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM devices");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String type = resultSet.getString("type");
                int sampling = resultSet.getInt("sampling");

                System.out.println("name: " + name + ", address: " + address + ", type: " + type + ", sampling: " + sampling);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
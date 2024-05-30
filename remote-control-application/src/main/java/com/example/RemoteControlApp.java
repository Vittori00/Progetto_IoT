package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;


public class RemoteControlApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/CottonNet";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Remote Control Application");
            System.out.println("1. Show active devices");
            System.out.println("2. Set new sample timing");
            System.out.print("\nChoose an option: ");

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
                            setSampling(illuminationSampling, "sensor0");
                            break;
                        case 2:
                            System.out.print("Insert Sprinkler sample timing: ");
                            int sprinklerSampling = scanner.nextInt();
                            setSampling(sprinklerSampling, "sensor1");
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

    private static void setSampling(int newSampling, String sensorName) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = connection.createStatement();

            String query = "SELECT address FROM devices WHERE name = '" + sensorName + "'";
            ResultSet resultSet = statement.executeQuery(query);
            
            String address = resultSet.getString("address");
            CoapClient client = new CoapClient("coap://[" + address + "]/sampling");

            CoapResponse response = client.post(Integer.toString(newSampling), 0);
            if (response != null) {
                System.out.println("Response: " + response.getResponseText());
            } else {
                System.out.println("No response from server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
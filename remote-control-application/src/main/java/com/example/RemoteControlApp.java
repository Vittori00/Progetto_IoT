package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class RemoteControlApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/CottonNet";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";
    private static volatile boolean running = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Remote Control Application");
            System.out.println("1. Show registered devices");
            System.out.println("2. Set new sample timing for the illumination sensor");
            System.out.println("3. Set new sample timing for the soil sensor");
            System.out.println("4. Show real-time measures from the illumination sensor");
            System.out.println("5. Show real-time measures from the soil sensor");
            System.out.println("6. Exit");
            System.out.print("\nChoose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Show registered devices
                    getActiveDevices();
                    break;
                case 2:
                    System.out.print("Insert new illumination sample timing: ");
                    int illuminationSampling = scanner.nextInt();
                    setSampling(illuminationSampling, "sensor0");
                    break;
                case 3:
                    System.out.print("Insert new soil sample timing: ");
                    int sprinklerSampling = scanner.nextInt();
                    setSampling(sprinklerSampling, "sensor1");
                    break;
                case 4:
                    // Show new measures from the illumination sensor
                    startPolling("illumination");
                    break;
                case 5:
                    // Show new measures from the soil sensor
                    startPolling("soil");
                    break;
                case 6:
                    // Exit the application
                    running = false;
                    System.out.println("Exiting application...");
                    System.exit(0);
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

            if (resultSet.next()) {
                String address = resultSet.getString("address");
                CoapClient client = new CoapClient("coap://[" + address + "]/sampling");

                CoapResponse response = client.post(Integer.toString(newSampling), 0);
                if (response != null) {
                    System.out.println("Response: " + response.getResponseText());
                    resultSet = statement.executeQuery("UPDATE devices SET sampling = " + newSampling + " WHERE name = '" + sensorName + "'");

                } else {
                    System.out.println("No response from server.");
                }
            } else {
                System.out.println("No device found with name: " + sensorName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getActiveDevices() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM devices")) {

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

    private static void startPolling(final String tableName) {
        Thread pollingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getMeasures(tableName);
            }
        });
        pollingThread.start();

        // Attendi input dall'utente per fermare il polling
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter to stop polling...");
        scanner.nextLine();

        // Imposta il flag di controllo su false per fermare il polling
        running = false;
        try {
            pollingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Reset del flag di controllo per consentire nuovi cicli di polling
        running = true;
    }

    private static void getMeasures(String tableName) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            int lastReadId = 0;

            while (running) {
                try (Statement statement = connection.createStatement()) {

                    ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE id > " + lastReadId + " ORDER BY id ASC");

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        //int timestamp = resultSet.getInt("timestamp");
                        if (tableName.equals("illumination")) {
                            int co2 = resultSet.getInt("co2");
                            int light = resultSet.getInt("light");
                            int phase = resultSet.getInt("phase");
                            System.out.println("id: " + id + ", co2: " + co2 + ", light: " + light + ", phase: " + phase/* + ", timestamp: " + timestamp*/);
                        } else if (tableName.equals("soil")) {
                            int moisture = resultSet.getInt("moisture");
                            int temperature = resultSet.getInt("temperature");
                            System.out.println("id: " + id + ", moisture: " + moisture + ", temperature: " + temperature/* + ", timestamp: " + timestamp*/);
                        }
                        lastReadId = id;
                    }

                    Thread.sleep(2000);  // Polling interval

                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
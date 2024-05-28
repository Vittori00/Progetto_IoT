package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;

import java.util.Scanner;

public class RemoteControlApp {

    private static final String COAP_ACTUATORS_URI = "coap://localhost/actuators";
    private static final String COAP_SENSORS_URI = "coap://localhost/sensors";

    private static final String SPRINKLER_RESOURCE_URI = "coap://[fd00::203:3:3:3]/sampling";
    private static final String ILLUMINATION_RESOURCE_URI = "coap://[fd00::202:2:2:2]/sampling";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        CoapClient sensors = new CoapClient(COAP_SENSORS_URI);
        CoapClient actuators = new CoapClient(COAP_ACTUATORS_URI);

        while (true) {
            System.out.println("Remote Control Application");
            System.out.println("1. Turn on a device");
            System.out.println("2. Turn off a device");
            System.out.println("3. Show status of sensors");
            System.out.println("4. Show status of actuators");
            System.out.println("5. Set new sample timing");
            System.out.print("\nScegli un'opzione: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Turn on device
                    System.out.print("Insert type of sensor to turn on (sensor/actuator): ");
                    String type = scanner.nextLine();
                    System.out.print("Insert name of sensor to turn on: ");
                    String name = scanner.nextLine();
                    if (type.equals("sensor")) {
                        System.out.print("Insert sampling time: ");
                        int sampling = scanner.nextInt();
                        scanner.nextLine();
                        turnOnDevice(sensors, name, sampling);
                    } else if (type.equals("actuator")) {
                        turnOnDevice(actuators, name, 0);
                    }
                    break;
                case 2:
                    // Turn off a device
                    System.out.print("Insert sensor to turn off: ");
                    String sensorNameOff = scanner.nextLine();
                    //turnOffDevice(sensors, sensorNameOff);
                    break;
                case 3:
                    // Show status of sensors
                    getStatus(sensors);
                    break;
                case 4:
                    // Show status of actuators
                    getStatus(actuators);
                    break;
                case 5:
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

    private static void turnOnDevice(CoapClient client, String name, int sampling) {
        JSONObject json = new JSONObject();

        json.put("name", name);
        if (sampling == 0) {
            json.put("type", "actuator");
        } else {
            json.put("type", "sensor");
            json.put("sampling", sampling);
        }

        CoapResponse response = client.post(json.toString(), 0);
        if (response != null) {
            System.out.println("Response: " + response.getResponseText());
        } else {
            System.out.println("No response from server.");
        }
    }


    private static void getStatus(CoapClient client) {
        CoapResponse response = client.get();
        if (client.getURI().equals(COAP_SENSORS_URI)) {
            if (response != null) {
                System.out.println("Sensors overview" + response.getResponseText());
            } else {
                System.out.println("No response from server.");
            }            
        } else if (client.getURI().equals(COAP_ACTUATORS_URI)) {
            if (response != null) {
                System.out.println("Actuators overview: " + response.getResponseText());
            } else {
                System.out.println("No response from server.");
            }
        } else {
            System.out.println("No response from server.");
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

    }
}
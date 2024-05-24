package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;

import java.util.Scanner;

public class RemoteControlApp {

    private static final String COAP_ACTUATORS_URI = "coap://localhost/actuators";
    private static final String COAP_SENSORS_URI = "coap://localhost/sensors";

    private static final String SOIL_RESOURCE_URI = "coap://[SPRINKLER_SENSOR_IP]/soil";

    private static final String CO2_RESOURCE_URI = "coap://[ILLUMINATION_SENSOR_IP]/co2";
    private static final String LIGHT_RESOURCE_URI = "coap://[ILLUMINATION_SENSOR_IP]/light";
    private static final String PHASE_RESOURCE_URI = "coap://[ILLUMINATION_SENSOR_IP]/phase";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        CoapClient sensors = new CoapClient(COAP_SENSORS_URI);
        CoapClient actuators = new CoapClient(COAP_ACTUATORS_URI);

        while (true) {
            System.out.println("Remote Control Application\n");
            System.out.println("1. Turn on the system");
            System.out.println("2. Turn off the system");
            System.out.println("3. Turn on a sensor");
            System.out.println("4. Turn off a sensor");
            System.out.println("5. Turn on an actuator");
            System.out.println("6. Turn off an actuator");
            System.out.println("7. Show status of sensors");
            System.out.println("8. Show status of actuators");
            System.out.println("9. Set parameter");
            System.out.print("\n Scegli un'opzione: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Implementare l'accensione del sistema
                    
                case 2:
                    // Implementare lo spegnimento del sistema
                    
                case 3:
                    // Turn on sensor
                    System.out.print("Insert sensor to turn on: ");
                    String sensorOn = scanner.nextLine();
                    sendCommand(sensors, sensorOn, "on");
                    break;
                case 4:
                    // Turn off a sensor
                    System.out.print("Insert sensor to turn off: ");
                    String sensorOff = scanner.nextLine();
                    sendCommand(sensors, sensorOff, "off");
                    break;
                case 5:
                    // Turn on actuator
                    System.out.print("Insert sensor to turn on: ");
                    String actuatorOn = scanner.nextLine();
                    sendCommand(actuators, actuatorOn, "on");
                    break;
                case 6:
                    // Turn off a sensor
                    System.out.print("Insert sensor to turn off: ");
                    String actuatorOff = scanner.nextLine();
                    sendCommand(actuators, actuatorOff, "off");
                    break;
                case 7:
                    // Show status of sensors
                    getStatus(sensors);
                    break;
                case 8:
                    // Show status of actuators
                    getStatus(actuators);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void sendCommand(CoapClient client, String device, String state) {
        JSONObject json = new JSONObject();

        if (client.getURI().equals(COAP_SENSORS_URI)) {
            json.put("sensor", client);
        } else if (client.getURI().equals(COAP_ACTUATORS_URI)) {
            json.put("actuator", client);
        }
        json.put("state", state);

        CoapResponse response = client.post(json.toString(), 0);
        if (response != null) {
            System.out.println("Response: " + response.getResponseText());
        } else {
            System.out.println("No response from server.");
        }
    }


    private static void setCO2Parameters(int co2Level, int tooHigh, int tooLow) {
        CoapClient client = new CoapClient(CO2_RESOURCE_URI);
        JSONObject json = new JSONObject();
        json.put("co2_level", co2Level);
        json.put("too_high", tooHigh);
        json.put("too_low", tooLow);

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
}
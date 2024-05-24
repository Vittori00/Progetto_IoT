package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;

import java.util.Scanner;

public class RemoteControlApp {

    private static final String COAP_SERVER_URI = "coap://localhost/actuators";
    private static final String CO2_RESOURCE_URI = "coap://[INDIRIZZO_IP_SENSORE]/co2";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CoapClient client = new CoapClient(COAP_SERVER_URI);

        while (true) {
            System.out.println("Remote Control Application");
            System.out.println("1. Accendere il sistema");
            System.out.println("2. Spegnere il sistema");
            System.out.println("3. Accendere un attuatore");
            System.out.println("4. Spegnere un attuatore");
            System.out.println("5. Settare parametri (co2, umidità, ecc.)");
            System.out.println("6. Visualizzare lo stato degli attuatori");
            System.out.println("0. Esci");
            System.out.print("Scegli un'opzione: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    // Implementare l'accensione del sistema
                    sendCommand(client, "system", "on");
                    break;
                case 2:
                    // Implementare lo spegnimento del sistema
                    sendCommand(client, "system", "off");
                    break;
                case 3:
                    // Accendere un attuatore
                    System.out.print("Inserisci il nome dell'attuatore da accendere: ");
                    String actuatorOn = scanner.nextLine();
                    sendCommand(client, actuatorOn, "on");
                    break;
                case 4:
                    // Spegnere un attuatore
                    System.out.print("Inserisci il nome dell'attuatore da spegnere: ");
                    String actuatorOff = scanner.nextLine();
                    sendCommand(client, actuatorOff, "off");
                    break;
                case 5:
                    // Settare parametri
                    System.out.println("Settare parametri CO2");
                    System.out.print("Inserisci il livello di CO2: ");
                    int co2Level = scanner.nextInt();
                    System.out.print("Inserisci il livello massimo di CO2: ");
                    int tooHigh = scanner.nextInt();
                    System.out.print("Inserisci il livello minimo di CO2: ");
                    int tooLow = scanner.nextInt();
                    setCO2Parameters(co2Level, tooHigh, tooLow);
                    break;
                case 6:
                    // Visualizzare lo stato degli attuatori
                    getStatus(client);
                    break;
                case 0:
                    System.out.println("Uscita dall'applicazione...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private static void sendCommand(CoapClient client, String actuator, String state) {
        JSONObject json = new JSONObject();
        json.put("actuator", actuator);
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
        if (response != null) {
            System.out.println("Stato degli attuatori: " + response.getResponseText());
        } else {
            System.out.println("No response from server.");
        }
    }
}

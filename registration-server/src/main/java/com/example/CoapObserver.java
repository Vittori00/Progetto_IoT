package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;


public class CoapObserver implements Runnable {
	private CoapClient client;
	private DBManager dbManager = new DBManager("jdbc:mysql://localhost:3306/CottonNet", "admin", "admin");
	private String sensorName;
	// private String address;

	public CoapObserver(String sensorName, String address) {
		if(sensorName.equals("sensor0")){
			client = new CoapClient("coap://[" + address + "]:5683/observation");
		}else{
			client = new CoapClient("coap://[" + address + "]:5683/soil");
		}
		this.sensorName = sensorName;
		// this.address = address;
	}

	public void startObserving() {
		CoapObserveRelation relation = client.observe(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				String responseText = response.getResponseText();
				System.out.println("Payload received: " + responseText + " \nlunghezza: " + responseText.length());
				JSONObject json = null;

				try {
					json = new JSONObject(responseText);
					if (sensorName.equals("sensor0")) {
						int co2 = json.getInt("c");
						int light = json.getInt("l");
						int phase = json.getInt("p");
						System.out.println(co2 + " " + light + " " + phase);
						int timestamp = (int) System.currentTimeMillis();
						dbManager.insertIlluminationMeasures(co2, light, phase, timestamp);
						System.out.println("Dati illlumination Registrati correttamente");
					} else if (sensorName.equals("sensor1")) {
						json = new JSONObject(responseText);
						System.out.println("Dati soil Arrivati");
						int moisture = json.getInt("m");
						int temperature = json.getInt("t");
						int timestamp = (int) System.currentTimeMillis();
						System.out.println("Dati soil Registrati correttamente");
						System.out.println( temperature + " " +  moisture);
						dbManager.insertSoilMeasures(moisture, temperature, timestamp);
						System.out.println("Dati sensore Registrati correttamente");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError() {
				System.out.println("Nothing to observe");
			}
		});

	}

	@Override
	public void run() {
		startObserving();
	}
}
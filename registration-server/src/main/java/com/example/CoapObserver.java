package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class CoapObserver implements Runnable{
	private CoapClient client;
	private DBManager dbManager = new DBManager("jdbc:mysql://localhost:3306/CottonNet", "admin", "admin");
	private String sensorName;
	private String address;

	public CoapObserver(String sensorName, String address) {
		client = new CoapClient("coap://[" + address + "]:5683/observation");
		this.sensorName = sensorName;
		this.address = address;
	}

	public void startObserving(){
		CoapObserveRelation relation = client.observe(new CoapHandler() {
			
			@Override
			public void onLoad(CoapResponse response) {
				String responseText = response.getResponseText();
				JSONObject json = null;

				try {
					JSONParser parser = new JSONParser();
					if (sensorName.equals("sensor0")) {
						json = (JSONObject) parser.parse(responseText);
						
						int co2 = json.getInt("co2");
						int light = json.getInt("light");
						int phase = json.getInt("phase");
						int timestamp = json.getInt("timestamp");

						dbManager.insertIlluminationMeasures(co2, light, phase, timestamp);
					}
					else if (sensorName.equals("sensor1")) {
						json = (JSONObject) parser.parse(responseText);
						int temperature = json.getInt("temperature");
						int moisture = json.getInt("moisture");
						int timestamp = json.getInt("timestamp");

						dbManager.insertSprinklerMeasures(moisture, temperature, timestamp);
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
		relation.proactiveCancel();
	}

	@Override
	public void run() {
		startObserving();
	}
}
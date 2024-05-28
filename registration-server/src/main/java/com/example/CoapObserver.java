package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;

import com.example.Resources.IlluminationResource;
public class CoapObserver {
	private CoapClient client;
	private DBManager dbManager = new DBManager("jdbc:mysql://localhost:3306/measures", "admin", "admin");
	private IlluminationResource resource;

	public CoapObserver(IlluminationResource resource) {
		client = new CoapClient("coap://[" + resource.getSensorAddress() + "]/" + resource.getResourceName());
		this.resource = resource;
	}

	public void startObserving(){
		CoapObserveRelation relation = client.observe(new CoapHandler() {
			public void onLoad(CoapResponse response) {
				JSONObject json = new JSONObject(response.getResponseText());
				if(json.has("CO2")){
					String resouceName = "CO2";
					int CO2 = json.getInt("CO2");
					int timestamp = json.getInt("timestamp");
					dbManager.insert("Illumination Sensor", resource.getSensorAddress(), resouceName, CO2, timestamp);
					System.out.println("New CO2 of " + CO2 + "ppm registered at " + timestamp);
				}
				else if(json.has("Light")){
					String resouceName = "Light";
					int Light = json.getInt("Light");
					int timestamp = json.getInt("timestamp");
					dbManager.insert("Illumination Sensor", resource.getSensorAddress(), resouceName, Light, timestamp);
					if (Light == 1) {
						System.out.println("New Light phase registered at " + timestamp);
					} else {
						System.out.println("New Dark phase registered at " + timestamp);
					}
				}
				else if(json.has("Phase")){
					String resouceName = "Phase";
					int Phase = json.getInt("Phase");
					int timestamp = json.getInt("timestamp");
					dbManager.insert("Illumination Sensor", resource.getSensorAddress(), resouceName, Phase, timestamp);
					if (Phase == 0) {
						System.out.println("New Farm Phase 0 registered at " + timestamp);
					} else {
						System.out.println("New Farm Phase 1 registered at " + timestamp);
					}
				}
				else {
					System.out.println("Error: wrong message format from Illumination sensor ");
				}
			}

			public void onError() {
				System.out.println("Nothing to observe");
			}
		});
		relation.proactiveCancel();
	}
}
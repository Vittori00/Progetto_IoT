package com.example;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapHandler;

public class MyClient {

	public static void main(String[] args) {

		CoapClient client = new CoapClient("coap://127.0.0.1/hello");

		CoapResponse response = client.get();

		System.out.print(response.getResponseText());

		CoapObserveRelation relation = client.observe(
			new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					String content = response.getResponseText();
					System.out.println(content);
				}

				@Override
				public void onError() {
					System.err.println("-Failed--------");
				}
			});
		try {
			Thread.sleep(6 * 1000);
		} catch (InterruptedException e) {
		}
		relation.proactiveCancel(); // to cancel observing
	}
}
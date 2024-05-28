package com.example.Resources;

public class IlluminationResource {
    private String resourceName;
    private String sensorAddress;

    public IlluminationResource(String resourceName, String sensorAddress) {
        this.resourceName = resourceName;
        this.sensorAddress = sensorAddress;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getSensorAddress() {
        return sensorAddress;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setSensorAddress(String sensorAddress) {
        this.sensorAddress = sensorAddress;
    }
}
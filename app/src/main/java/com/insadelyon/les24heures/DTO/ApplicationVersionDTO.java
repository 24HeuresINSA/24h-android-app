package com.insadelyon.les24heures.DTO;

/**
 * Created by remi on 13/04/15.
 */
public class ApplicationVersionDTO {
    String android;

    public ApplicationVersionDTO(String appVersion) {
        this.android = appVersion;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }
}

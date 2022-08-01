package org.planetaccounting.saleAgent.model;

public class NotificationPost {

    String token;
    String user_id;
    String device_token;
    String device_id;
    String device_name;


    public NotificationPost(String token, String user_id, String device_token, String device_id, String device_name) {
        this.token = token;
        this.user_id = user_id;
        this.device_token = device_token;
        this.device_id = device_id;
        this.device_name = device_name;
    }
}

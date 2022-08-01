package org.planetaccounting.saleAgent.model.clients;

/**
 * Created by macb on 16/12/17.
 */

public class ClientCardPost {
    String token;
    String user_id;
    String partie_id;
    String date_from;
    String date_to;

    public ClientCardPost(String token, String user_id, String partie_id, String date_from, String date_to) {
        this.token = token;
        this.user_id = user_id;
        this.partie_id = partie_id;
        this.date_from = date_from;
        this.date_to = date_to;
    }
}

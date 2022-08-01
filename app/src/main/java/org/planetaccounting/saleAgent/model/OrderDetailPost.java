package org.planetaccounting.saleAgent.model;

/**
 * Created by macb on 06/02/18.
 */

public class OrderDetailPost {
    String token;
    String user_id;
    String order_id;
    String order_type;

    public OrderDetailPost(String token, String user_id, String order_id,String order_type) {
        this.token = token;
        this.user_id = user_id;
        this.order_id = order_id;
        this.order_type = order_type;
    }
}

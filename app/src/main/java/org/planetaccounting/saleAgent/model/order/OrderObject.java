package org.planetaccounting.saleAgent.model.order;

/**
 * Created by macb on 06/02/18.
 */

public class OrderObject {
    OrderPost order;
    String token;
    String user_id;

    public OrderObject(OrderPost order, String token, String user_id) {
        this.order = order;
        this.token = token;
        this.user_id = user_id;
    }
}

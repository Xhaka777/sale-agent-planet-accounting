package org.planetaccounting.saleAgent.events;

/**
 * Created by macb on 07/02/18.
 */

public class OpenOrderDetailEvent {
    String orderId;
    String orderType;

    public OpenOrderDetailEvent(String orderId,String type) {
        this.orderId = orderId;
        this.orderType =  type;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderType() {
        return orderType;
    }
}

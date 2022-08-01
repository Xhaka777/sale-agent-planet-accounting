package org.planetaccounting.saleAgent.model.order;

/**
 * Created by macb on 06/02/18.
 */

public class OrderItemPost {
    String no_order;
    String id_item;
    String item_group;
    String name;
    String quantity;
    String warehouse;

    public OrderItemPost(String no_order, String id_item, String item_group, String name, String quantity, String warehouse) {
        this.no_order = no_order;
        this.id_item = id_item;
        this.item_group = item_group;
        this.name = name;
        this.quantity = quantity;
        this.warehouse = warehouse;
    }
}

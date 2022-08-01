package org.planetaccounting.saleAgent.model.order;

import java.util.List;

/**
 * Created by macb on 06/02/18.
 */

public class OrderPost
{
    String partie_id;
    String partie_station_id;
    String sale_station_id;
    String data;
    String id_order_person;
    List<OrderItemPost> items;

    public OrderPost( String partie_id,String partie_station, String sale_station_id, String data, String id_order_person, List<OrderItemPost> items) {
        this.partie_id = partie_id;
        this.partie_station_id = partie_station;
        this.sale_station_id = sale_station_id;
        this.data = data;
        this.id_order_person = id_order_person;
        this.items = items;
    }
}

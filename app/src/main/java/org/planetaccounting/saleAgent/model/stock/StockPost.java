package org.planetaccounting.saleAgent.model.stock;

/**
 * Created by macb on 09/12/17.
 */

public class StockPost {
    String token;
    String user_id;
    String data;


    public StockPost(String token, String user_id) {
        this.token = token;
        this.user_id = user_id;
    }

    public void setData(String data) {
        this.data = data;
    }
}


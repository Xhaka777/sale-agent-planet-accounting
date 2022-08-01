package org.planetaccounting.saleAgent.model.order;

public class CheckQuantity {

    String user_id;
    String token;
    String quantity;
    String warehouse;
    String date;
    String item_id;

    public CheckQuantity(String user_id, String token, String quantity, String warehouse, String date, String item_id) {
        this.user_id = user_id;
        this.token = token;
        this.quantity = quantity;
        this.warehouse = warehouse;
        this.date = date;
        this.item_id = item_id;
    }


    @Override
    public String toString() {
        return "CheckQuantity{" +
                "user_id='" + user_id + '\'' +
                ", token='" + token + '\'' +
                ", quantity='" + quantity + '\'' +
                ", warehouse='" + warehouse + '\'' +
                ", date='" + date + '\'' +
                ", item_id='" + item_id + '\'' +
                '}';
    }
}

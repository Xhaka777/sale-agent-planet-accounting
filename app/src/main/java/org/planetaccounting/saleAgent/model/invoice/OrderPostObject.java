package org.planetaccounting.saleAgent.model.invoice;

import java.util.ArrayList;
import java.util.List;

public class OrderPostObject {

        String token;
        String user_id;
        List<InvoicePost> orders = new ArrayList<>();

        public void setToken(String token) {
            this.token = token;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

    public void setOrders(List<InvoicePost> orders) {
        this.orders = orders;
    }
}

package org.planetaccounting.saleAgent.model.invoice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macb on 25/01/18.
 */

public class InvoicePostObject {
    String token;
    String user_id;
    List<InvoicePost> invoices = new ArrayList<>();

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setInvoices(List<InvoicePost> invoices) {
        this.invoices = invoices;
    }
}

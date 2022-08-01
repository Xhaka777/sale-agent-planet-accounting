package org.planetaccounting.saleAgent.kthemallin;

import org.planetaccounting.saleAgent.model.invoice.InvoicePost;

import java.util.ArrayList;
import java.util.List;

public class ReturnPostObject {
    String token;
    String user_id;
    List<InvoicePost> return_invoices = new ArrayList<>();

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setRetrunPost(List<InvoicePost> invoices) {
        this.return_invoices = invoices;
    }
}

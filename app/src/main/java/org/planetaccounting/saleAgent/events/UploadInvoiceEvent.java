package org.planetaccounting.saleAgent.events;

/**
 * Created by macb on 05/02/18.
 */

public class UploadInvoiceEvent {
    public int invoiceId;

    public UploadInvoiceEvent(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }
}

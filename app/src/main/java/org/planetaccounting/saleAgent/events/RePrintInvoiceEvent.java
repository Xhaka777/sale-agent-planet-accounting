package org.planetaccounting.saleAgent.events;

/**
 * Created by macb on 05/02/18.
 */

public class RePrintInvoiceEvent {
    int position;
    boolean isFromServer;

    public RePrintInvoiceEvent(int position,boolean isFromServer) {
        this.position = position;
        this.isFromServer = isFromServer;
    }

    public int getPosition() {
        return position;
    }

    public boolean getIsFromServer() {
        return isFromServer;
    }
}

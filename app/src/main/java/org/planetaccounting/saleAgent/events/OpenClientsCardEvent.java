package org.planetaccounting.saleAgent.events;

import org.planetaccounting.saleAgent.model.clients.Client;

/**
 * Created by macb on 16/12/17.
 */

public class OpenClientsCardEvent {

    Client client;

    public OpenClientsCardEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}

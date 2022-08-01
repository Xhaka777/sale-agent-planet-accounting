package org.planetaccounting.saleAgent.inkasimi;

import java.util.List;

/**
 * Created by tahirietrit on 4/6/18.
 */

public class InkasimPost {
    String token;
    String user_id;
    List<InkasimiDetail> payments;

    public InkasimPost(String token, String user_id, List<InkasimiDetail> inkasimiDetails) {
        this.token = token;
        this.user_id = user_id;
        this.payments = inkasimiDetails;
    }
}

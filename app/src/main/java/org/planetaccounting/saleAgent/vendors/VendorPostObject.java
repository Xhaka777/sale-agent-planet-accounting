package org.planetaccounting.saleAgent.vendors;

import org.planetaccounting.saleAgent.inkasimi.InkasimiDetail;

import java.util.List;

/**
 * Created by tahirietrit on 4/6/18.
 */

public class VendorPostObject {
    String token;
    String user_id;
    List<VendorPost> purchases;

    public VendorPostObject(String token, String user_id, List<VendorPost> inkasimiDetails) {
        this.token = token;
        this.user_id = user_id;
        this.purchases = inkasimiDetails;
    }
}

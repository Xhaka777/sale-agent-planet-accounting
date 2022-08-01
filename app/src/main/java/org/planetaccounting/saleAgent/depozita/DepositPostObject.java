package org.planetaccounting.saleAgent.depozita;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tahirietrit on 4/6/18.
 */

public class DepositPostObject {
    String token;
    String user_id;
    List<DepositPost> deposits = new ArrayList<>();

    public DepositPostObject(String token, String user_id, List<DepositPost> depositPosts) {
        this.token = token;
        this.user_id = user_id;
        this.deposits = depositPosts;
    }
}

package org.planetaccounting.saleAgent.aksionet;

/**
 * Created by SHB on 2/16/2018.
 */

public class ActionPost {

    String token;
    String user_id;

    public ActionPost(String token, String user_id) {
        this.token = token;
        this.user_id = user_id;
    }
}

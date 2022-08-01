package org.planetaccounting.saleAgent.model.target;

/**
 * Created by tahirietrit on 3/20/18.
 */

public class BaseTargetPost {
    private String user_id;
    private String token;
    private String month;
    private String year;

    public BaseTargetPost(String user_id, String token, String month, String year) {
        this.user_id = user_id;
        this.token = token;
        this.month = month;
        this.year = year;
    }
}

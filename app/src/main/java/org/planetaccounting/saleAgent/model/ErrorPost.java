package org.planetaccounting.saleAgent.model;

import java.util.List;

/**
 * Created by tahirietrit on 6/20/18.
 */

public class ErrorPost {
    String token;
    String user_id;
    List<Error> errors;

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}

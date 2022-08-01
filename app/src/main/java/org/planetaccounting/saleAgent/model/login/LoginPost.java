package org.planetaccounting.saleAgent.model.login;

/**
 * Created by macb on 07/12/17.
 */

public class LoginPost {

    private String email;
    private String password;

    public LoginPost(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

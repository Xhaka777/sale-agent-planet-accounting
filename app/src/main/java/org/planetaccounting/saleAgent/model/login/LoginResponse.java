package org.planetaccounting.saleAgent.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.planetaccounting.saleAgent.model.Error;

/**
 * Created by macb on 07/12/17.
 */

public class LoginResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public LoginData data;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public LoginData getData() {
        return data;
    }
}
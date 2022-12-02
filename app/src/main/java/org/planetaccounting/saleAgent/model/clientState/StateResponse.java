package org.planetaccounting.saleAgent.model.clientState;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StateResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("states")
    @Expose
    public List<State> states = null;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public List<State> getStates() {
        return states;
    }
}

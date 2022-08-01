package org.planetaccounting.saleAgent.model.clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by macb on 16/12/17.
 */

public class ClientCardResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("error")
    @Expose
    public Error error;
    @SerializedName("data")
    @Expose
    public List<CardItem> cardItems = null;

    public Boolean getSuccess() {
        return success;
    }

    public Error getError() {
        return error;
    }

    public List<CardItem> getCardItems() {
        return cardItems;
    }
}
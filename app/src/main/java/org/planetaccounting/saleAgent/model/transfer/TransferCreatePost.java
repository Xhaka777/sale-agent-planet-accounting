package org.planetaccounting.saleAgent.model.transfer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferCreatePost {

    @SerializedName("user_id")
    @Expose
    public String userId;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("transfer")
    @Expose
    public TransferCreate transfer;

    public TransferCreatePost(String userId, String token, TransferCreate transfer) {
        this.userId = userId;
        this.token = token;
        this.transfer = transfer;
    }
}

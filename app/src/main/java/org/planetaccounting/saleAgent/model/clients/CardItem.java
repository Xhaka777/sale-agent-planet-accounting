package org.planetaccounting.saleAgent.model.clients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by macb on 16/12/17.
 */

public class CardItem {


    @SerializedName("document_number")
    @Expose
    public String documentNumber;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("amount")
    @Expose
    public String amount;
    @SerializedName("data")
    @Expose
    public String data;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("payment_date")
    @Expose
    public String paymentDate;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        if (amount!=null)
        return Double.parseDouble(amount);
        else return 0;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
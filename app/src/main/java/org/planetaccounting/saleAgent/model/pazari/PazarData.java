package org.planetaccounting.saleAgent.model.pazari;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tahirietrit on 4/10/18.
 */

public class PazarData {

    @SerializedName("data")
    @Expose
    public String data;
    @SerializedName("sale_with_invoice")
    @Expose
    public String saleWithInvoice;
    @SerializedName("sale_with_fiscal")
    @Expose
    public String saleWithFiscal;
    @SerializedName("cash")
    @Expose
    public String cash;
    @SerializedName("cash_collection")
    @Expose
    public String cashCollection;
    @SerializedName("deposite")
    @Expose
    public String deposite;
    @SerializedName("purchase")
    @Expose
    public String purchase;
    @SerializedName("debt")
    @Expose
    public String debt;
    @SerializedName("credit")
    @Expose
    public String credit;
    @SerializedName("total")
    @Expose
    public String total;

    public String getData() {
        return data;
    }

    public String getSaleWithInvoice() {
        return saleWithInvoice;
    }

    public String getSaleWithFiscal() {
        return saleWithFiscal;
    }

    public String getCash() {
        return cash;
    }

    public String getCashCollection() {
        return cashCollection;
    }

    public String getDeposite() {
        return deposite;
    }

    public String getPurchase() {
        return purchase;
    }

    public String getDebt() {
        return debt;
    }

    public String getCredit() {
        return credit;
    }

    public String getTotal() {
        return total;
    }
}
package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Role extends RealmObject {

    @SerializedName("main")
    @Expose
    public Main main;

    @SerializedName("daily_trade")
    @Expose
    public DailyTrade dailyTrade;

    @SerializedName("invoice")
    @Expose
    public InvoiceRole invoice;

    @SerializedName("stock")
    @Expose
    public Stock stock;

    @PrimaryKey
    public int userId = 0;


    public Main getMain() {
        return main;
    }

    public DailyTrade getDailyTrade() {
        return dailyTrade;
    }

    public InvoiceRole getInvoice() {
        return invoice;
    }

    public Stock getStock() {
        return stock;
    }
}

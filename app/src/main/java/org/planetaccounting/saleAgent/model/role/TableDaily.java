package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class TableDaily extends RealmObject {

    @SerializedName("date")
    @Expose
    public int date;

    @SerializedName("invoice_sale")
    @Expose
    public int invoice_sale;

    @SerializedName("bill_sale")
    @Expose
    public int bill_sale;

    @SerializedName("deposits")
    @Expose
    public int deposits;

    @SerializedName("cash")
    @Expose
    public int cash;

    @SerializedName("cash_collection")
    @Expose
    public int cashCollection;


    @SerializedName("expenses")
    @Expose
    public int expenses;

    @SerializedName("total")
    @Expose
    public int total;


    @SerializedName("dept")
    @Expose
    public int dept;


    public int getDate() {
        return date;
    }

    public int getInvoice_sale() {
        return invoice_sale;
    }

    public int getBill_sale() {
        return bill_sale;
    }

    public int getDeposits() {
        return deposits;
    }

    public int getCash() {
        return cash;
    }

    public int getCashCollection() {
        return cashCollection;
    }

    public int getExpenses() {
        return expenses;
    }

    public int getTotal() {
        return total;
    }

    public int getDept() {
        return dept;
    }
}

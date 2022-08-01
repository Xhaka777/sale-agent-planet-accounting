package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Main extends RealmObject {

    @PrimaryKey
    @SerializedName("update")
    @Expose
    public int update;

    @SerializedName("close_fiscal_arc")
    @Expose
    public int closeFiscalArc;

    @SerializedName("prepare_fiscal_arc")
    @Expose
    public int preparefiscalArc;

    @SerializedName("daily_trade")
    @Expose
    public int dailyTrade;

    @SerializedName("invoice")
    @Expose
    public int invoice;

    @SerializedName("stock")
    @Expose
    public int stock;

    @SerializedName("cash_collection")
    @Expose
    public int cashCollection;

    @SerializedName("deposits")
    @Expose
    public int deposits;

    @SerializedName("reports")
    @Expose
    public int reports;

    @SerializedName("stock_return")
    @Expose
    public int stockReturn;

    @SerializedName("transfers")
    @Expose
    public int transfers;

    @SerializedName("clients")
    @Expose
    public int clients;

    @SerializedName("targets")
    @Expose
    public int targets;

    @SerializedName("orders")
    @Expose
    public int orders;


    @SerializedName("uploads")
    @Expose
    public int uploads;

    @SerializedName("expenses")
    @Expose
    public int expenses;

    @SerializedName("actions")
    @Expose
    public int actions;


    public int getUpdate() {
        return update;
    }

    public int getCloseFiscalArc() {
        return closeFiscalArc;
    }

    public int getPreparefiscalArc() {
        return preparefiscalArc;
    }

    public int getDailyTrade() {
        return dailyTrade;
    }

    public int getInvoice() {
        return invoice;
    }

    public int getStock() {
        return stock;
    }

    public int getCashCollection() {
        return cashCollection;
    }

    public int getDeposits() {
        return deposits;
    }

    public int getReports() {
        return reports;
    }

    public int getStockReturn() {
        return stockReturn;
    }

    public int getTransfers() {
        return transfers;
    }

    public int getClients() {
        return clients;
    }

    public int getTargets() {
        return targets;
    }

    public int getOrders() {
        return orders;
    }

    public int getUploads() {
        return uploads;
    }

    public int getExpenses() {
        return expenses;
    }

    public int getActions() {
        return actions;
    }


    public ArrayList<Integer> isInRole() {
        ArrayList<Integer> list = new ArrayList<>();

      list.add(getInvoice());
        list.add(getStock());

        list.add(getCashCollection());
        list.add(getDeposits());


        list.add(getReports());

        list.add(getStockReturn());

        list.add(getTransfers());

        list.add(getClients());

        list.add(getTargets());

        list.add(getOrders());


        list.add(getUploads());

        list.add(getExpenses());

        list.add(getActions());

        return list;


    }
}

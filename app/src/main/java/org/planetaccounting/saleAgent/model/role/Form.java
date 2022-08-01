package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Form extends RealmObject {

    @SerializedName("date")
    @Expose
    public ShowEdit date;


    @SerializedName("invoice_number")
    @Expose
    public ShowEdit invoice_number;

    @SerializedName("client")
    @Expose
    public ShowEdit client;


    @SerializedName("client_station")
    @Expose
    public ShowEdit client_station;


    @SerializedName("client_discount")
    @Expose
    public ShowEdit client_discount;


    @SerializedName("article")
    @Expose
    public ShowEdit article;


    @SerializedName("article_number")
    @Expose
    public ShowEdit article_number;


    @SerializedName("article_unit")
    @Expose
    public ShowEdit article_unit;


    @SerializedName("article_discount")
    @Expose
    public ShowEdit article_discount;


    @SerializedName("price_no_vat")
    @Expose
    public ShowEdit price_no_vat;


    @SerializedName("price_of_vat")
    @Expose
    public ShowEdit price_of_vat;


    @SerializedName("amount_no_vat")
    @Expose
    public ShowEdit amount_no_vat;

    @SerializedName("amount_of_vat")
    @Expose
    public ShowEdit amount_of_vat;


    @SerializedName("amount_with_vat")
    @Expose
    public ShowEdit amount_with_vat;

    @SerializedName("sum_amount_no_vat")
    @Expose
    public ShowEdit sum_amount_no_vat;


    @SerializedName("sum_discount")
    @Expose
    public ShowEdit sum_discount;


    @SerializedName("sum_vat")
    @Expose
    public ShowEdit sum_vat;

    @SerializedName("sum_amount_with_vat")
    @Expose
    public ShowEdit sum_amount_with_vat;

    @SerializedName("create_invoice")
    @Expose
    public ShowEdit create_invoice;

    @SerializedName("create_bill")
    @Expose
    public ShowEdit create_bill;


    public ShowEdit getDate() {
        return date;
    }

    public ShowEdit getInvoice_number() {
        return invoice_number;
    }

    public ShowEdit getClient() {
        return client;
    }

    public ShowEdit getClient_station() {
        return client_station;
    }

    public ShowEdit getClient_discount() {
        return client_discount;
    }

    public ShowEdit getArticle() {
        return article;
    }

    public ShowEdit getArticle_number() {
        return article_number;
    }

    public ShowEdit getArticle_unit() {
        return article_unit;
    }

    public ShowEdit getArticle_discount() {
        return article_discount;
    }

    public ShowEdit getPrice_no_vat() {
        return price_no_vat;
    }

    public ShowEdit getPrice_of_vat() {
        return price_of_vat;
    }

    public ShowEdit getAmount_no_vat() {
        return amount_no_vat;
    }

    public ShowEdit getAmount_of_vat() {
        return amount_of_vat;
    }

    public ShowEdit getAmount_with_vat() {
        return amount_with_vat;
    }

    public ShowEdit getSum_amount_no_vat() {
        return sum_amount_no_vat;
    }

    public ShowEdit getSum_discount() {
        return sum_discount;
    }

    public ShowEdit getSum_vat() {
        return sum_vat;
    }

    public ShowEdit getSum_amount_with_vat() {
        return sum_amount_with_vat;
    }

    public ShowEdit getCreate_invoice() {
        return create_invoice;
    }

    public ShowEdit getCreate_bill() {
        return create_bill;
    }
}



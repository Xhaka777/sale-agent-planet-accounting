package org.planetaccounting.saleAgent.model.role;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class TableStock extends RealmObject {

    @SerializedName("image")
    @Expose
    public int image;

    @SerializedName("number")
    @Expose
    public int number;

    @SerializedName("article")
    @Expose
    public int article;


    @SerializedName("unit")
    @Expose
    public int unit;

    @SerializedName("quantity")
    @Expose
    public int quantity;


    @SerializedName("amount")
    @Expose
    public int amount;


    public int getImage() {
        return image;
    }

    public int getNumber() {
        return number;
    }

    public int getArticle() {
        return article;
    }

    public int getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAmount() {
        return amount;
    }
}



package org.planetaccounting.saleAgent.model.stock;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by macb on 09/12/17.
 */

public class StockData{

    @SerializedName("items")
    @Expose
    public List<Item> items = null;

    public List<Item> getItems() {
        return items;
    }
}
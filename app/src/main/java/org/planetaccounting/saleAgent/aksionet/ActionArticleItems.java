package org.planetaccounting.saleAgent.aksionet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * Created by SHB on 2/16/2018.
 */

public class ActionArticleItems extends RealmObject {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("from")
    @Expose
    public String from;
    @SerializedName("to")
    @Expose
    public String to;
    @SerializedName("item_id")
    @Expose
    public String itemId;
    @SerializedName("quantity")
    @Expose
    public String quantity;
    @SerializedName("discount")
    @Expose
    public String discount;
    @SerializedName("client_category")
    @Expose
    public String clientCategory;
    @SerializedName("client_category_name")
    @Expose
    public String clientCategoryName;
    @SerializedName("group_item")
    @Expose
    public String groupItem;
    @SerializedName("relacion")
    @Expose
    public String relacion;

    @SerializedName("client_id")
    @Expose
    public int clientId;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("steps")
    @Expose
    public RealmList<ActionSteps> steps;


    public String getItemId() {
        return itemId;
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public String getClientCategoryName() {
        return clientCategoryName;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getItem_id() {
        return itemId;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public String getGroupItem() {
        return groupItem;
    }

    public String getRelacion() {
        return relacion;
    }

    public int getClientId() {
        return clientId;
    }

    public String getType() {
        return type;
    }

    public RealmList<ActionSteps> getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "ActionArticleItems{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", item_id='" + itemId + '\'' +
                ", quantity='" + quantity + '\'' +
                ", discount='" + discount + '\'' +
                '}';
    }
}

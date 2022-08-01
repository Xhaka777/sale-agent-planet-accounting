package org.planetaccounting.saleAgent.model.stock;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macb on 09/12/17.
 */

public class Item extends RealmObject implements Parent<SubItem>  {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("amount")
    @Expose
    public String amount;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("number")
    @Expose
    public String number;
    @SerializedName("quantity")
    @Expose
    public String quantity;
    @SerializedName("manufacturer")
    @Expose
    public String manufacturer;
    @SerializedName("brand")
    @Expose
    public String brand;
    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("default_unit")
    @Expose
    public String defaultUnit;
    @SerializedName("relacion")
    @Expose
    public String relacion;
    @SerializedName("items")
    @Expose
    public RealmList<SubItem> items = null;

    int selectedPosition;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public List<SubItem> getItems() {
        return items;
    }

    public String getItemsRelation(int selectedPosition){
        String relacioni = items.get(selectedPosition).getRelacion()+"";
        return relacioni;
    }
    public String getRelacion() {
        return relacion;
    }

    @Override
    public List<SubItem> getChildList() {
        return getItems();
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(float quantity_minus) {
        float amount = Float.parseFloat( this.amount );
        float quantity = Float.parseFloat( this.quantity);
        float cost_price = amount / quantity;
        float cost_amount = cost_price * ( quantity_minus);
        this.amount = String.valueOf(amount - cost_amount);
    }

    public void setAmount_return(String amount) {
        float amount_stock = Float.parseFloat(this.amount);
        float amount_stock_add = Float.parseFloat(amount);
        this.amount = String.valueOf(amount_stock + amount_stock_add);
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", amount='" + amount + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", quantity='" + quantity + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", defaultUnit='" + defaultUnit + '\'' +
                ", relacion='" + relacion + '\'' +
                ", items=" + items +
                ", selectedPosition=" + selectedPosition +
                '}';
    }
}
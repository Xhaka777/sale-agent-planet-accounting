package org.planetaccounting.saleAgent.model.invoice;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by macb on 25/01/18.
 */

public class InvoiceItemPost extends RealmObject {
    @Index
    String id;
    String no_order;
    String no;
    String id_item_group;
    String id_item;
    String name;
    String quantity;
    String unit;
    String price;
    String discount;
    String barcode = "";
    //    String discount_extra;
    String vat_id;
    String vat_rate;
    String price_vat;
    String amount_of_discount;
    String price_base;
    String totalPrice;
    String relacioni;
    boolean isAction;
    boolean isCollection;
    String base_quantity;

    String amount_no_vat;

    public String getPrice_base() {
        return price_base;
    }

    public void setPrice_base(String price_base) {
        this.price_base = price_base;
    }

    public String getBase_quantity() {
        return base_quantity;
    }

    public void setRelacioni(String relacioni) {
        this.relacioni = relacioni;
    }

    public String getRelacioni() {
        return relacioni;
    }

    public void setNo_order(String no_order) {
        this.no_order = no_order;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setId_item_group(String id_item_group) {
        this.id_item_group = id_item_group;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    //    public void setDiscount_extra(String discount_extra) {
//        this.discount_extra = discount_extra;
//    }

    public void setVat_id(String vat_id) {
        this.vat_id = vat_id;
    }

    public void setVat_rate(String vat_rate) {
        this.vat_rate = vat_rate;
    }

    public void setPrice_vat(String price_vat) {
        this.price_vat = price_vat;
    }

    public void setAmount_of_discount(String amount_of_discount) {
        this.amount_of_discount = amount_of_discount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNo_order() {
        return no_order;
    }

    public String getNo() {
        return no;
    }

    public String getId_item_group() {
        return id_item_group;
    }

    public String getId_item() {
        return id_item;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscount() {
        return discount;
    }

    public String getVat_id() {
        return vat_id;
    }

    public String getVat_rate() {
        return vat_rate;
    }

    public String getPrice_vat() {
        return price_vat;
    }

    public String getAmount_of_discount() {
        return amount_of_discount;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setBase_quantity(String base_quantity) {
        this.base_quantity = base_quantity;
    }

    public boolean isAction() {
        return isAction;
    }

    public void setAction(boolean action) {
        isAction = action;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public String getAmount_no_vat() {
        return amount_no_vat;
    }

    public void setAmount_no_vat(String amount_no_vat) {
        this.amount_no_vat = amount_no_vat;
    }

    @Override
    public String toString() {
        return "InvoiceItemPost{" +
                ", no_order='" + no_order + '\'' +
                ", no='" + no + '\'' +
                ", id_item_group='" + id_item_group + '\'' +
                ", id_item='" + id_item + '\'' +
                ", name='" + name + '\'' +
                ", quantity='" + quantity + '\'' +
                ", unit='" + unit + '\'' +
                ", price='" + price + '\'' +
                ", discount='" + discount + '\'' +
//                ", discount_extra='" + discount_extra + '\'' +
                ", vat_id='" + vat_id + '\'' +
                ", vat_rate='" + vat_rate + '\'' +
                ", price_vat='" + price_vat + '\'' +
                ", price_vat='" + relacioni + '\'' +
                ", amount_of_discount='" + amount_of_discount + '\'' +
                '}';
    }
}

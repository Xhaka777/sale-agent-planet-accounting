package org.planetaccounting.saleAgent.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.model.stock.SubItem;

import java.util.List;

import io.realm.annotations.Ignore;

/**
 * Created by macb on 13/01/18.
 */

public class InvoiceItem implements Parcelable {
    public String discount= "0";
    public String baseDiscount ="0";
    @Ignore
    public String maxDiscound = "0";
    public String id;
    public String type;
    public String amount;
    public String name;
    public String number;
    public String quantity;
    public String manufacturer;
    public String brand;
    public String category;
    public String defaultUnit;
    public String relacion;
    public String barcode;
    public String sasia = "0";
    public List<SubItem> items = null;
    public double totalPrice;
    public double vleraPaTvsh;
    public double vleraEZbritur;
    public double vleraETvsh;
    public double vleraTotale;
    public double cmimiNeArk;
    public String selectedItemCode;
    public String selectedUnit;
    public String groupItem;
    public String availableQuantity;
    public double priceWithvat;
    public double basePrice;
    int selectedPosition;
    boolean isAction = false;
    boolean isCollection = false;
    String extraDiscount = "0";
    String minQuantityForDiscount = "0";



    public InvoiceItem(Item item) {
        this.id = item.getId();
        this.type = item.getType();
        this.name = item.getName();
        this.number = item.getNumber();
        this.quantity = item.getQuantity();
        this.manufacturer = item.getManufacturer();
        this.brand = item.getBrand();
        this.category = item.getCategory();
        this.defaultUnit = item.getDefaultUnit();
        this.relacion = item.getRelacion();
        this.items = item.getItems();
        this.amount = item.getAmount();
    }

    public InvoiceItem(InvoiceItem item){
        this.id = item.getId();
        this.type = item.getType();
        this.name = item.getName();
        this.number = item.getNumber();
        this.quantity = item.getQuantity();
        this.manufacturer = item.getManufacturer();
        this.brand = item.getBrand();
        this.category = item.getCategory();
        this.defaultUnit = item.getDefaultUnit();
        this.relacion = item.getRelacion();
        this.selectedUnit = item.getSelectedUnit();
        this.items = item.getItems();
        }
   public double getPriceWithvat() {
        return priceWithvat;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }


    public void setPriceWithvat(double priceWithvat) {
        this.priceWithvat = priceWithvat;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public String getRelacion() {
        return relacion;
    }

    public List<SubItem> getChildList() {
        return getItems();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setDiscount(String discount) {
        this.maxDiscound = discount;
        this.discount = discount;
    }

    public void setDiscount(String discount,boolean isFromEditText) {

        if (!isFromEditText){
            this.maxDiscound = discount;
        }
        this.discount = discount;
    }

    public String getDiscount() {
        return discount;
    }

    public String getMaxDiscound() {
        return maxDiscound;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getAmount() {
        return Double.parseDouble(amount);
    }

    public String getSasia() {
        return sasia;
    }

    public void setSasia(String sasia) {
        this.sasia = sasia;
    }

    public double getVleraPaTvsh() {
        return vleraPaTvsh;
    }

    public void setVleraPaTvsh(double vleraPaTvsh) {
        this.vleraPaTvsh = vleraPaTvsh;
    }

    public double getVleraEZbritur() {
        return vleraEZbritur;
    }

    public void setVleraEZbritur(double vleraEZbritur) {
        this.vleraEZbritur = vleraEZbritur;
    }

    public double getVleraETvsh() {
        return vleraETvsh;
    }

    public void setVleraETvsh(double vleraETvsh) {
        this.vleraETvsh = vleraETvsh;
    }

    public double getVleraTotale() {
        return vleraTotale;
    }

    public void setVleraTotale(double vleraTotale) {
        this.vleraTotale = vleraTotale;
    }

    public double getCmimiNeArk() {
        return cmimiNeArk;
    }

    public void setCmimiNeArk(double cmimiNeArk) {
        this.cmimiNeArk = cmimiNeArk;
    }

    public String getSelectedItemCode() {
        return selectedItemCode;
    }

    public void setSelectedItemCode(String selectedItemCode) {
        this.selectedItemCode = selectedItemCode;
    }

    public String getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(String selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public boolean isAction() {
        return isAction;
    }

    public void setAction(boolean action) {
        isAction = action;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtraDiscount() {
        return extraDiscount;
    }

    public void setExtraDiscount(String extraDiscount) {
        this.extraDiscount = extraDiscount;
    }

    public Double getMinQuantityForDiscount() {
        return Double.valueOf(minQuantityForDiscount);
    }

    public String getBaseDiscount() {
        return baseDiscount;
    }

    public void setBaseDiscount(String baseDiscount) {
        this.baseDiscount = baseDiscount;
    }

    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }

    public void setMinQuantityForDiscount(String minQuantityForDiscount) {
        this.minQuantityForDiscount = minQuantityForDiscount;
    }

    public String getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(String groupItem) {
        this.groupItem = groupItem;
    }



    public String getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public double getBasePrice(){

   return this.basePrice;

    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.discount);
        dest.writeString(this.baseDiscount);
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.amount);
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeString(this.quantity);
        dest.writeString(this.manufacturer);
        dest.writeString(this.brand);
        dest.writeString(this.category);
        dest.writeString(this.defaultUnit);
        dest.writeString(this.relacion);
        dest.writeString(this.barcode);
        dest.writeString(this.sasia);
        dest.writeTypedList(this.items);
        dest.writeDouble(this.totalPrice);
        dest.writeDouble(this.vleraPaTvsh);
        dest.writeDouble(this.vleraEZbritur);
        dest.writeDouble(this.vleraETvsh);
        dest.writeDouble(this.vleraTotale);
        dest.writeDouble(this.cmimiNeArk);
        dest.writeString(this.selectedItemCode);
        dest.writeString(this.selectedUnit);
        dest.writeString(this.groupItem);
        dest.writeString(this.availableQuantity);
        dest.writeDouble(this.priceWithvat);
        dest.writeInt(this.selectedPosition);
        dest.writeByte(this.isAction ? (byte) 1 : (byte) 0);
        dest.writeString(this.extraDiscount);
        dest.writeString(this.minQuantityForDiscount);
    }

    protected InvoiceItem(Parcel in) {
        this.discount = in.readString();
        this.baseDiscount = in.readString();
        this.id = in.readString();
        this.type = in.readString();
        this.amount = in.readString();
        this.name = in.readString();
        this.number = in.readString();
        this.quantity = in.readString();
        this.manufacturer = in.readString();
        this.brand = in.readString();
        this.category = in.readString();
        this.defaultUnit = in.readString();
        this.relacion = in.readString();
        this.barcode = in.readString();
        this.sasia = in.readString();
        this.items = in.createTypedArrayList(SubItem.CREATOR);
        this.totalPrice = in.readDouble();
        this.vleraPaTvsh = in.readDouble();
        this.vleraEZbritur = in.readDouble();
        this.vleraETvsh = in.readDouble();
        this.vleraTotale = in.readDouble();
        this.cmimiNeArk = in.readDouble();
        this.selectedItemCode = in.readString();
        this.selectedUnit = in.readString();
        this.groupItem = in.readString();
        this.availableQuantity = in.readString();
        this.priceWithvat = in.readDouble();
        this.selectedPosition = in.readInt();
        this.isAction = in.readByte() != 0;
        this.extraDiscount = in.readString();
        this.minQuantityForDiscount = in.readString();
    }

    public static final Creator<InvoiceItem> CREATOR = new Creator<InvoiceItem>() {
        @Override
        public InvoiceItem createFromParcel(Parcel source) {
            return new InvoiceItem(source);
        }

        @Override
        public InvoiceItem[] newArray(int size) {
            return new InvoiceItem[size];
        }
    };
}
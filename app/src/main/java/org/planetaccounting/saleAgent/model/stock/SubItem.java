package org.planetaccounting.saleAgent.model.stock;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macb on 09/12/17.
 */

public class SubItem extends RealmObject implements Parcelable {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("quantity")
    @Expose
    public String quantity;
    @SerializedName("group_item")
    @Expose
    public String groupItem;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("acronym")
    @Expose
    public String acronym;
    @SerializedName("number")
    @Expose
    public String number;
    @SerializedName("unit")
    @Expose
    public String unit;
    @SerializedName("relacion")
    @Expose
    public String relacion = "0.0";
    @SerializedName("price_sale")
    @Expose
    public String priceSale;

    @SerializedName("price_vat_sale")
    @Expose
    public String priceVatSale;

    @SerializedName("vat_code_sale")
    @Expose
    public String vatCodeSale;
    @SerializedName("cover_img")
    @Expose
    public String coverImg;
    @SerializedName("discount")
    @Expose
    public String discount;
    @SerializedName("vat_rate")
    @Expose
    public String vatRate;
    @SerializedName("vat_code_fisal_printer")
    @Expose
    public String vatCodeFisalPrinter;
    @SerializedName("barcode")
    @Expose
    public String barcode;

    public String getId() {
        return String.valueOf(id);
    }

    public String getQuantity() {
        return quantity;
    }

    public String getGroupItem() {
        return groupItem;
    }

    public String getName() {
        return name;
    }

    public String getAcronym() {
        return acronym;
    }

    public String getNumber() {
        return number;
    }

    public String getUnit() {
        return unit;
    }

    public double getRelacion() {
        return Double.parseDouble(relacion);
    }

    public String getBarcode() {
        return barcode;
    }

    public String getPriceSale() {
        return priceSale;
    }

    public String getPriceVatSale() {
        return priceVatSale;
    }

    public String getVatCodeSale() {
        return vatCodeSale;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public String getDiscount() {
        return discount;
    }


    public String getVatRate() {
        return vatRate;
    }

    public String getVatCodeFisalPrinter() {
        return vatCodeFisalPrinter;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }

    @Override
    public String toString() {
        return "SubItem{" +
                "id='" + id + '\'' +
                ", quantity='" + quantity + '\'' +
                ", groupItem='" + groupItem + '\'' +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", number='" + number + '\'' +
                ", unit='" + unit + '\'' +
                ", relacion='" + relacion + '\'' +
                ", priceSale='" + priceSale + '\'' +
                ", priceVatSale='" + priceVatSale + '\'' +
                ", vatCodeSale='" + vatCodeSale + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", discount='" + discount + '\'' +
                ", vatRate='" + vatRate + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.quantity);
        dest.writeString(this.groupItem);
        dest.writeString(this.name);
        dest.writeString(this.acronym);
        dest.writeString(this.number);
        dest.writeString(this.unit);
        dest.writeString(this.relacion);
        dest.writeString(this.priceSale);
        dest.writeString(this.priceVatSale);
        dest.writeString(this.vatCodeSale);
        dest.writeString(this.coverImg);
        dest.writeString(this.discount);
        dest.writeString(this.vatRate);
        dest.writeString(this.vatCodeFisalPrinter);
        dest.writeString(this.barcode);
    }

    public SubItem() {
    }

    protected SubItem(Parcel in) {
        this.id = in.readInt();
        this.quantity = in.readString();
        this.groupItem = in.readString();
        this.name = in.readString();
        this.acronym = in.readString();
        this.number = in.readString();
        this.unit = in.readString();
        this.relacion = in.readString();
        this.priceSale = in.readString();
        this.priceVatSale = in.readString();
        this.vatCodeSale = in.readString();
        this.coverImg = in.readString();
        this.discount = in.readString();
        this.vatRate = in.readString();
        this.vatCodeFisalPrinter = in.readString();
        this.barcode = in.readString();
    }

    public static final Parcelable.Creator<SubItem> CREATOR = new Parcelable.Creator<SubItem>() {
        @Override
        public SubItem createFromParcel(Parcel source) {
            return new SubItem(source);
        }

        @Override
        public SubItem[] newArray(int size) {
            return new SubItem[size];
        }
    };


}
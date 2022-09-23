package org.planetaccounting.saleAgent.model.clients;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.raportet.raportmodels.ReportsList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macb on 13/12/17.
 */

public class Client extends RealmObject implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("is_company")
    @Expose
    public String isCompany;
    @SerializedName("number")
    @Expose
    public String number;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("unique_number")
    @Expose
    public String unique_number;
    @SerializedName("balance")
    @Expose
    public String balance;
    @SerializedName("logo")
    @Expose
    public String logo;
    @SerializedName("number_fiscal")
    @Expose
    public String numberFiscal;
    @SerializedName("number_busniess")
    @Expose
    public String numberBusniess;
    @SerializedName("number_vat")
    @Expose
    public String numberVat;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("zip")
    @Expose
    public String zip;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("fax")
    @Expose
    public String fax;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("web")
    @Expose
    public String web;
    @SerializedName("discount")
    @Expose
    public String discount;
    @SerializedName("limit_balance")
    @Expose
    public String limitBalance;
    @SerializedName("payment_deadline")
    @Expose
    public String paymentDeadline;
    @SerializedName("client_category")
    @Expose
    public String clientCategory;
    @SerializedName("stations")
    @Expose
    public RealmList<Station> stations = null;

    public String getId() {
        return id;
    }

    public String getIsCompany() {
        return isCompany;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getUnique_number() { return unique_number; }

    public String getBalance() {
        return balance;
    }

    public String getLogo() {
        return logo;
    }

    public String getNumberFiscal() {
        return numberFiscal;
    }

    public String getNumberBusniess() {
        return numberBusniess;
    }

    public String getNumberVat() {
        return numberVat;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getState() {
        return state;
    }

    public String getPhone() {
        return phone;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getWeb() {
        return web;
    }

    public RealmList<Station> getStations() {
        return stations;
    }

    public String getDiscount() {
        return discount;
    }

    public String getLimitBalance() {
        return limitBalance;
    }

    public String getPaymentDeadline() {
        return paymentDeadline;
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = clientCategory;
    }

    public Client() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.isCompany);
        dest.writeString(this.number);
        dest.writeString(this.name);
        dest.writeString(this.unique_number);
        dest.writeString(this.balance);
        dest.writeString(this.logo);
        dest.writeString(this.numberFiscal);
        dest.writeString(this.numberBusniess);
        dest.writeString(this.numberVat);
        dest.writeString(this.address);
        dest.writeString(this.city);
        dest.writeString(this.zip);
        dest.writeString(this.state);
        dest.writeString(this.phone);
        dest.writeString(this.fax);
        dest.writeString(this.email);
        dest.writeString(this.web);
        dest.writeString(this.discount);
        dest.writeString(this.limitBalance);
        dest.writeString(this.paymentDeadline);
        dest.writeString(this.clientCategory);
        dest.writeTypedList(this.stations);

    }

    protected Client(Parcel in) {
        this.id = in.readString();
        this.isCompany = in.readString();
        this.number = in.readString();
        this.name = in.readString();
        this.unique_number = in.readString();
        this.balance = in.readString();
        this.logo = in.readString();
        this.numberFiscal = in.readString();
        this.numberBusniess = in.readString();
        this.numberVat = in.readString();
        this.address = in.readString();
        this.city = in.readString();
        this.zip = in.readString();
        this.state = in.readString();
        this.phone = in.readString();
        this.fax = in.readString();
        this.email = in.readString();
        this.web = in.readString();
        this.discount = in.readString();
        this.limitBalance = in.readString();
        this.paymentDeadline = in.readString();
        this.clientCategory = in.readString();
        this.stations = new RealmList<>();
        in.readList(this.stations, Station.class.getClassLoader());
    }

//    public void setClientFromReports(ReportsList reports){
//
//        this.logo = reports.logo;
//        this.number = reports.docNumber;
//        this.name = reports.partieName;
//        this.c
//        this.unique_number = unique_number;
//    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel source) {
            return new Client(source);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

}
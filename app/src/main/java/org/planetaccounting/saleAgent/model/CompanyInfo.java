package org.planetaccounting.saleAgent.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macb on 04/02/18.
 */

public class CompanyInfo extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("company_url")
    @Expose
    public String companyUrl;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("unique_number")
    @Expose
    public String unique_number;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("zip")
    @Expose
    public String zip;
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
    @SerializedName("fiscal_number")
    @Expose
    public String fiscalNumber;
    @SerializedName("busniess_number")
    @Expose
    public String busniessNumber;
    @SerializedName("vat_number")
    @Expose
    public String vatNumber;
    @SerializedName("logo")
    @Expose
    public String logo;
    @SerializedName("contact_person")
    @Expose
    public String contactPerson;
    @SerializedName("bank_accounts")
    @Expose
    public RealmList<BankAccount> bankAccounts = null;


    public String getId() {
        return id;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public String getName() {
        return name;
    }

    public String getUnique_number() { return unique_number; }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    public Object getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getWeb() {
        return web;
    }

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public String getBusniessNumber() {
        return busniessNumber;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public String getLogo() {
        return logo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public RealmList<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    @Override
    public String toString() {
        return "CompanyInfo{" +
                "id='" + id + '\'' +
                ", companyUrl='" + companyUrl + '\'' +
                ", name='" + name + '\'' +
                ", unique_number='" + unique_number + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", email='" + email + '\'' +
                ", web='" + web + '\'' +
                ", fiscalNumber='" + fiscalNumber + '\'' +
                ", busniessNumber='" + busniessNumber + '\'' +
                ", vatNumber='" + vatNumber + '\'' +
                ", logo='" + logo + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                '}';
    }
}

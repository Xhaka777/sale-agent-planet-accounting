package org.planetaccounting.saleAgent.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.planetaccounting.saleAgent.model.role.Role;

import java.util.List;

/**
 * Created by macb on 07/12/17.
 */

public class LoginData {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("username")
    @Expose
    public Object username;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("logged_company_id")
    @Expose
    public String loggedCompanyId;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("station_id")
    @Expose
    public String stationId;
    @SerializedName("station_name")
    @Expose
    public String stationName;
    @SerializedName("employee_number")
    @Expose
    public String employeeNumber;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("last_invoice_number")
    @Expose
    public String last_invoice_number;

    @SerializedName("last_return_invoice_number")
    @Expose
    public String lastReturnInvoiceNumber;

    @SerializedName("language")
    @Expose
    public String language;


    @SerializedName("default_warehouse")
    @Expose
    public String default_warehouse;

    @SerializedName("company_allowed")
    @Expose
    public List<CompanyAllowed> companyAllowed = null;


    @SerializedName("role")
    @Expose
    public Role role;


    public String getId() {
        return id;
    }

    public Object getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getLoggedCompanyId() {
        return loggedCompanyId;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<CompanyAllowed> getCompanyAllowed() {
        return companyAllowed;
    }

    public String getLast_invoice_number() {
        return last_invoice_number;
    }

    public void setLast_invoice_number(String last_invoice_number) {
        this.last_invoice_number = last_invoice_number;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public Role getRole() {
        return role;
    }

    public String getLastReturnInvoiceNumber() {
        return lastReturnInvoiceNumber;
    }

    public String getLanguage() {
        return language;
    }

    public String getDefault_warehouse() {
        return default_warehouse;
    }
}
package org.planetaccounting.saleAgent.model.invoice;

import org.planetaccounting.saleAgent.raportet.raportmodels.ReportsList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by macb on 25/01/18.
 */

public class InvoicePost extends RealmObject {
    @PrimaryKey
    int id;
    String type;
    String no_invoice;
    String fiscal_number;
    String device_serial_number;
    String partie_id;
    String partie_name;
    String partie_station_id;
    String partie_station_name;
    String partie_address;
    String partie_city;
    String partie_state_id;
    String partie_zip;
    String sale_station_id;
    String sale_station_name;
    String invoice_date;
    String data_ship;
    String discount;
    String is_payed;
    String amount_no_vat;
    String amount_of_vat;
    String amount_discount;
    String amount_payed;
    String amount_with_vat;
    String id_saler;
    String is_bill;
    String location;
    String total_without_discount;
    RealmList<InvoiceItemPost> items = new RealmList<>();
    boolean synced = false;
    String relacioni;

    @Ignore
    boolean isFromServer = false;

    public void setRelacioni(String relacioni) {
        this.relacioni = relacioni;
    }

    public String getRelacioni() {
        return relacioni;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(String loaction) {
        this.location = loaction;
    }

    public void setNo_invoice(String no_invoice) {
        this.no_invoice = no_invoice;
    }

    public void setPartie_id(String partie_id) {
        this.partie_id = partie_id;
    }

    public void setPartie_name(String partie_name) {
        this.partie_name = partie_name;
    }

    public void setPartie_station_id(String partie_station_id) {
        this.partie_station_id = partie_station_id;
    }

    public void setPartie_station_name(String partie_station_name) {
        this.partie_station_name = partie_station_name;
    }

    public void setPartie_address(String partie_address) {
        this.partie_address = partie_address;
    }

    public void setPartie_city(String partie_city) {
        this.partie_city = partie_city;
    }

    public void setPartie_state_id(String partie_state_id) {
        this.partie_state_id = partie_state_id;
    }

    public void setSale_station_id(String sale_station_id) {
        this.sale_station_id = sale_station_id;
    }

    public void setSale_station_name(String sale_station_name) {
        this.sale_station_name = sale_station_name;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setIs_payed(String is_payed) {
        this.is_payed = is_payed;
    }

    public void setAmount_no_vat(String amount_no_vat) {
        this.amount_no_vat = amount_no_vat;
    }

    public void setAmount_of_vat(String amount_of_vat) {
        this.amount_of_vat = amount_of_vat;
    }

    public void setAmount_discount(String amount_discount) {
        this.amount_discount = amount_discount;
    }

    public void setAmount_payed(String amount_payed) {
        this.amount_payed = amount_payed;
    }

    public void setAmount_with_vat(String amount_with_vat) {
        this.amount_with_vat = amount_with_vat;
    }

    public void setId_saler(String id_saler) {
        this.id_saler = id_saler;
    }

    public void setIs_bill(String is_bill) {
        this.is_bill = is_bill;
    }

    public void setItems(RealmList<InvoiceItemPost> items) {
        this.items = items;
    }

    public boolean getSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public void setPartie_zip(String partie_zip) {
        this.partie_zip = partie_zip;
    }

    public void setData_ship(String data_ship) {
        this.data_ship = data_ship;
    }

    public RealmList<InvoiceItemPost> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

    public String getNo_invoice() {
        return no_invoice;
    }

    public String getPartie_id() {
        return partie_id;
    }

    public String getPartie_name() {
        return partie_name;
    }

    public String getPartie_station_id() {
        return partie_station_id;
    }

    public String getPartie_station_name() {
        return partie_station_name;
    }

    public String getPartie_address() {
        return partie_address;
    }

    public String getPartie_city() {
        return partie_city;
    }

    public String getPartie_state_id() {
        return partie_state_id;
    }

    public String getSale_station_id() {
        return sale_station_id;
    }

    public String getSale_station_name() {
        return sale_station_name;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public String getDiscount() {
        return discount;
    }

    public String getIs_payed() {
        return is_payed;
    }

    public String getAmount_no_vat() {
        return amount_no_vat;
    }

    public String getAmount_of_vat() {
        return amount_of_vat;
    }

    public String getAmount_discount() {
        return amount_discount;
    }

    public String getAmount_payed() {
        return amount_payed;
    }

    public String getAmount_with_vat() {
        return amount_with_vat;
    }

    public String getId_saler() {
        return id_saler;
    }

    public String getIs_bill() {
        return is_bill;
    }

    public String getLocation() {
        return location;
    }

    public String getTotal_without_discount() {
        return total_without_discount;
    }

    public String getPartie_zip() {
        return partie_zip;
    }

    public String getData_ship() {
        return data_ship;
    }

    public boolean getIsFromServer() {
        return isFromServer;
    }

    public void setFromServer(boolean fromServer) {
        isFromServer = fromServer;
    }

    public void setTotal_without_discount(String total_without_discount) {
        this.total_without_discount = total_without_discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFiscal_number(String fiscal_number) {
        this.fiscal_number = fiscal_number;
    }

    public String getDevice_serial_number() {
        return device_serial_number;
    }

    public void setDevice_serial_number(String device_serial_number) {
        this.device_serial_number = device_serial_number;
    }

    public void setInvoiceFromReports(ReportsList reports){
        this.id = reports.id;
        this.no_invoice = reports.docNumber;
        this.amount_with_vat = reports.amount;
        this.partie_name = reports.partieName;
        this.invoice_date = reports.date;
        this.synced = true;
        this.isFromServer =  true;
    }

    @Override
    public String toString() {
        return "InvoicePost{" +
                "id='" + id + '\'' +
                ", no_invoice='" + no_invoice + '\'' +
                ", partie_id='" + partie_id + '\'' +
                ", partie_name='" + partie_name + '\'' +
                ", partie_station_id='" + partie_station_id + '\'' +
                ", partie_station_name='" + partie_station_name + '\'' +
                ", partie_address='" + partie_address + '\'' +
                ", partie_city='" + partie_city + '\'' +
                ", partie_state_id='" + partie_state_id + '\'' +
                ", sale_station_id='" + sale_station_id + '\'' +
                ", sale_station_name='" + sale_station_name + '\'' +
                ", invoice_date='" + invoice_date + '\'' +
                ", discount='" + discount + '\'' +
                ", is_payed='" + is_payed + '\'' +
                ", amount_no_vat='" + amount_no_vat + '\'' +
                ", amount_of_vat='" + amount_of_vat + '\'' +
                ", amount_discount='" + amount_discount + '\'' +
                ", amount_payed='" + amount_payed + '\'' +
                ", amount_with_vat='" + amount_with_vat + '\'' +
                ", id_saler='" + id_saler + '\'' +
                ", is_bill='" + is_bill + '\'' +
                ", items=" + items +
                '}';
    }
}

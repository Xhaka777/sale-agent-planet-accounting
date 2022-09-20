package org.planetaccounting.saleAgent.persistence;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.aksionet.ActionArticleItems;
import org.planetaccounting.saleAgent.aksionet.ActionCollectionItem;
import org.planetaccounting.saleAgent.aksionet.ActionData;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.depozita.DepositPost;
import org.planetaccounting.saleAgent.inkasimi.InkasimiDetail;
import org.planetaccounting.saleAgent.model.Categorie;
import org.planetaccounting.saleAgent.model.CompanyInfo;
import org.planetaccounting.saleAgent.model.DepositPostSerializer;
import org.planetaccounting.saleAgent.model.Error;
import org.planetaccounting.saleAgent.model.ErrorPost;
import org.planetaccounting.saleAgent.model.InkasimiDetailSerializer;
import org.planetaccounting.saleAgent.model.Token;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.invoice.InvoiceItemPostSerialiser;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePostSerialiser;
import org.planetaccounting.saleAgent.model.role.Role;
import org.planetaccounting.saleAgent.model.stock.Brand;
import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.vendors.VendorPost;
import org.planetaccounting.saleAgent.vendors.VendorPostSerializer;
import org.planetaccounting.saleAgent.vendors.VendorSaler;
import org.planetaccounting.saleAgent.vendors.VendorType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.DepositPostRealmProxy;
import io.realm.InkasimiDetailRealmProxy;
import io.realm.InvoiceItemPostRealmProxy;
import io.realm.InvoicePostRealmProxy;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.VendorPostRealmProxy;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by macb on 27/12/17.
 */

public class RealmHelper {

    Realm realm;
    Gson gson;
    @Inject
    Preferences preferences;
    @Inject
    ApiService apiService;

    public RealmHelper(Realm realm) {
        this.realm = realm;
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(InvoicePostRealmProxy.class, new InvoicePostSerialiser())
                .registerTypeAdapter(InvoiceItemPostRealmProxy.class, new InvoiceItemPostSerialiser())
                .registerTypeAdapter(VendorPostRealmProxy.class, new VendorPostSerializer())
                .registerTypeAdapter(DepositPostRealmProxy.class, new DepositPostSerializer())
                .registerTypeAdapter(InkasimiDetailRealmProxy.class, new InkasimiDetailSerializer())
                .create();
    }

    //Stock
    public RealmResults<Item> getStockItems() {
        RealmResults<Item> items = realm.where(Item.class).findAll();
        return items;
    }

    public RealmResults<Item> getStockItemsWithoutAction() {
        RealmResults<Item> items = realm.where(Item.class).notEqualTo("type", "action").findAll();
        return items;
    }

    public RealmResults<Brand> getBrands() {
        RealmResults<Brand> brands = realm.where(Brand.class).findAll();
        return brands;
    }

    public RealmResults<Categorie> getCategories() {
        RealmResults<Categorie> brands = realm.where(Categorie.class).findAll();
        return brands;
    }

    public String[] getStockItemsName() {
        RealmResults<Item> items = realm.where(Item.class).findAll();
        String[] itemNames = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i).getName();
        }
        return itemNames;
    }

    public String[] getStockItemsQuantity(){
        RealmResults<Item> items = realm.where(Item.class).findAll();
        String[] itemQuantities = new String[items.size()];
        for (int i = 0; i < items.size(); i++){
            itemQuantities[i] = items.get(i).getQuantity();
        }
        return itemQuantities;
    }

    public String[] getStockItemsName(boolean isInvoice) {
        RealmResults<Item> items = realm.where(Item.class).findAll();
        String[] itemNames = new String[items.size()];
        int count = 0;
        for (int i = 0; i < items.size(); i++) {
            if (Double.parseDouble(items.get(i).getQuantity()) > 0 || items.get(i).getType().equalsIgnoreCase("action")) {
                itemNames[count] = items.get(i).getName();
                count++;
            }
        }
        return itemNames;
    }

    public String [] getStockItemsQuantity(boolean isInvoice) {
        RealmResults<Item> items = realm.where(Item.class).findAll();
        String [] itemQuantity = new String[items.size()];
        int count = 0;
        for(int i  = 0; i < items.size(); i++){
            if(Double.parseDouble(items.get(i).getQuantity()) > 0){
                itemQuantity[count] = items.get(i).getQuantity();
                count++;
            }
        }
        return itemQuantity;
    }

    public String[] getStockItemsCodes() {
        RealmResults<Item> items = realm.where(Item.class).findAll();
        String[] itemNames = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i).getNumber();
        }
        return itemNames;
    }

    public void saveStockItems(List<Item> stockItems) {
        //while(!realm.isInTransaction())
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(stockItems);
        realm.commitTransaction();
    }

    public void saveRole(Role role) {
        //while(!realm.isInTransaction())
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(role);
        realm.commitTransaction();
    }

    public void saveBrands(List<Brand> brands) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(brands);
        realm.commitTransaction();
    }

    public void saveCategories(List<Categorie> categories) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(categories);
        realm.commitTransaction();
    }

    public CompanyInfo getCompanyInfo() {
        return realm.where(CompanyInfo.class).findFirst();
    }

    public void saveCompanyInfo(CompanyInfo companyInfo) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(companyInfo);
        realm.commitTransaction();
    }

    public void saveToken(Token token) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(token);
        realm.commitTransaction();
    }

    public Token getToken() {
        return realm.where(Token.class).findFirst();
    }

    public Role getRole() {
        return realm.where(Role.class).findFirst();
    }

    public void saveClients(List<Client> clients) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(clients);
        realm.commitTransaction();
    }

    public void saveInvoices(InvoicePost invoicePost) {
        if (realm.isInTransaction())
            realm.commitTransaction();
        realm.beginTransaction();
        for (int i = 0; i < invoicePost.getItems().size(); i++) {
            try {
                Item item = realm.where(Item.class).equalTo("id", invoicePost.getItems().get(i).getId()).findFirst();
                double relation = Double.parseDouble(invoicePost.getItems().get(i).getRelacioni());
                double sold = Double.parseDouble(invoicePost.getItems().get(i).getQuantity());
                double s = relation * sold;
                System.out.println("sasia1 " + Double.parseDouble(invoicePost.getItems().get(i).getQuantity()) + " " +
                        Double.parseDouble(invoicePost.getItems().get(i).getRelacioni()) + " " + s);
                item.setAmount((float) s);
                item.setQuantity(String.valueOf(Double.parseDouble(item.getQuantity()) - s));
            } catch (Exception e) {
                sendError(e.getLocalizedMessage());
            }

        }

        realm.copyToRealmOrUpdate(invoicePost);
        realm.commitTransaction();
//        updateStock(invoicePost);
    }

    public void returnInvoice(InvoicePost invoicePost) {
        if (realm.isInTransaction())
            realm.commitTransaction();
        realm.beginTransaction();
        for (int i = 0; i < invoicePost.getItems().size(); i++) {
            Item item = realm.where(Item.class).equalTo("id", invoicePost.getItems().get(i).getId()).findFirst();
            double relation = Double.parseDouble(invoicePost.getItems().get(i).getRelacioni());
            double sold = Double.parseDouble(invoicePost.getItems().get(i).getQuantity());
            double s = relation * sold;
            System.out.println("sasia1 " + Double.parseDouble(invoicePost.getItems().get(i).getQuantity()) + " " +
                    Double.parseDouble(invoicePost.getItems().get(i).getRelacioni()) + " " + s);
            item.setQuantity(String.valueOf(Double.parseDouble(item.getQuantity()) + s));
            item.setAmount_return(invoicePost.getItems().get(i).getAmount_no_vat());
        }

        realm.copyToRealmOrUpdate(invoicePost);
        realm.commitTransaction();
//        updateStock(invoicePost);
    }

    private void updateStock(InvoicePost invoicePost) {
        realm.beginTransaction();

        realm.commitTransaction();
    }

    public Item getItemsByName(String name) {
        return realm.where(Item.class).equalTo("name", name).findAll().get(0);
    }

    public Item getItemsByQuantity(String quantity) {
        return realm.where(Item.class).equalTo("quantity", quantity).findAll().get(0);
    }

    public RealmResults<Item> getItemsByType(String type) {
        return realm.where(Item.class).equalTo("type", type).findAll();
    }

    public Item getItemsByCode(String code) {
        return realm.where(Item.class).equalTo("number", code).findAll().get(0);
    }

    public Item getItemsByid(String code) {
        return realm.where(Item.class).equalTo("id", code).findAll().get(0);
    }

    public Item getItemsByGroupID(String ID) {
        return realm.where(Item.class).equalTo("id", ID).findAll().get(0);
    }

    public RealmResults<InvoicePost> getInvoices() {
        realm = Realm.getDefaultInstance();
        RealmResults<InvoicePost> items = realm.where(InvoicePost.class).sort("invoice_date", Sort.DESCENDING).findAll();
        String invoices = gson.toJson(realm.where(InvoicePost.class).sort("invoice_date", Sort.DESCENDING).findAll());
        return items;
    }

    public String getInvoicesString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(InvoicePost.class).sort("invoice_date", Sort.DESCENDING).findAll());
        return invoices;
    }

    public String getTInvoicesString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(InvoicePost.class).equalTo("type", "inv").equalTo("synced", false).sort("invoice_date", Sort.DESCENDING).findAll());
        return invoices;
    }

    public String getRInvoicesString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(InvoicePost.class).equalTo("type", "ret").equalTo("synced", false).sort("invoice_date", Sort.DESCENDING).findAll());
        return invoices;
    }

    public String getAllInvoicesString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(InvoicePost.class).equalTo("type", "inv").sort("invoice_date", Sort.DESCENDING).findAll());
        return invoices;
    }

    public String getAllReturnInvoicesString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(InvoicePost.class).equalTo("type", "ret").sort("invoice_date", Sort.DESCENDING).findAll());
        return invoices;
    }


    public String getVendorsString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(VendorPost.class).findAll());
        return invoices;
    }

    public String getDepositString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(DepositPost.class).findAll());
        return invoices;
    }

    public String getInkasimiString() {
        realm = Realm.getDefaultInstance();
        String invoices = gson.toJson(realm.where(InkasimiDetail.class).findAll());
        return invoices;
    }

    public String getInvoiceById(int id) {
        realm = Realm.getDefaultInstance();
        String invoicePost = gson.toJson(realm.where(InvoicePost.class).equalTo("id", id).findFirst());
        return invoicePost;
    }

    public String[] getItemUnits(String name) {
        Item item = realm.where(Item.class).equalTo("name", name).findAll().get(0);
        String[] units = new String[item.getItems().size()];
        for (int i = 0; i < item.getItems().size(); i++) {
            units[i] = item.getItems().get(i).getUnit();
        }
        return units;
    }

    public RealmResults<Client> getClients() {
        return realm.where(Client.class).findAll();
    }

    public String[] getClientsNames() {
        RealmResults<Client> clients = realm.where(Client.class).findAll();
        String[] clientNames = new String[clients.size()];
        for (int i = 0; i < clients.size(); i++) {
            clientNames[i] = clients.get(i).getName()+" nrf:"+ clients.get(i).getNumberFiscal();
        }
        return clientNames;
    }

    public String[] getClientsBalance() {
        RealmResults<Client> clients = realm.where(Client.class).findAll();
        String[] clientNames = new String[clients.size()];
        for (int i = 0; i < clients.size(); i++) {

            clientNames[i] = clients.get(i).getName() + " nrf:\t" + clients.get(i).getNumberFiscal() + "\t\t\t\t\t\t" + cutTo2(Double.parseDouble(clients.get(i).getBalance())) + "\t\t\t";
        }
        return clientNames;
    }



//    public void saveArticleActionItems(List<ActionArticleItems> articleItems) {
//        realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        realm.copyToRealmOrUpdate(articleItems);
//        realm.commitTransaction();
//        realm.close();
//    }

    public String getClientStationIdFromName(String name, String stationName) {
        Client client = realm.where(Client.class).equalTo("name", name).findAll().get(0);
        String stationIdClienti = "null";
        String[] clientStations = new String[client.getStations().size()];
        for (int i = 0; i < client.getStations().size(); i++) {
            clientStations[i] = client.getStations().get(i).getName();
            if (clientStations[i].equals(stationName)) {
                stationIdClienti = client.getStations().get(i).getId();
            }
        }
        return stationIdClienti;
    }

    public String[] getClientStations(String name) {
        Client client = realm.where(Client.class).equalTo("name", name).findAll().get(0);
        String[] clientStations = new String[client.getStations().size()];
        for (int i = 0; i < client.getStations().size(); i++) {
            clientStations[i] = client.getStations().get(i).getName();
        }
        return clientStations;
    }

    public Client getClientFromName(String name) {
        System.out.println("client name 11" + name);
        return realm.where(Client.class).equalTo("name", name).findAll().get(0);
    }


    public int getAutoIncrementIfForInvoice() {
        Number currentIdNum = realm.where(InvoicePost.class).equalTo("type", "inv").max("id");
        int nextId;
        if (currentIdNum == null) {
            nextId = preferences.getLastInvoiceNumber();
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }


    public int getAutoIncrementIfForReturn() {
        Number currentIdNum = realm.where(InvoicePost.class).equalTo("type", "ret").max("id");
        int nextId;
        if (currentIdNum == null) {
            nextId = preferences.getLastReturnInvoiceNumber();
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public int getAutoIncrementIfForVendor() {
        Number currentIdNum = realm.where(VendorPost.class).max("id");
        int nextId;
        if (currentIdNum == null) {
            nextId = 0;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public int getAutoIncrementIfForInkasim() {
        Number currentIdNum = realm.where(InkasimiDetail.class).max("id");
        int nextId;
        if (currentIdNum == null) {
            nextId = 0;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public int getAutoIncrementIfForDepozit() {
        Number currentIdNum = realm.where(DepositPost.class).max("id");
        int nextId;
        if (currentIdNum == null) {
            nextId = 0;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public static void removeAllData() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

// delete all realm objects
        realm.deleteAll();

//commit realm changes
        realm.commitTransaction();
    }

    //Vendors
    public void saveVendor(VendorPost vendorPost) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(vendorPost);
        realm.commitTransaction();

    }

    //Vendor
    public RealmResults<VendorPost> getVendors() {
        return realm.where(VendorPost.class).findAll();
    }

    public RealmResults<VendorType> getVendorTypes() {
        return realm.where(VendorType.class).findAll();
    }

    public RealmResults<VendorSaler> getVendorNames() {
        return realm.where(VendorSaler.class).findAll();
    }

    public void saveVendorTypes(List<VendorType> vendorTypes) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(vendorTypes);
        realm.commitTransaction();
    }

    public String[] getVendorTypeNames() {
        RealmResults<VendorType> items = realm.where(VendorType.class).findAll();
        String[] units = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            units[i] = items.get(i).getName();
        }
        return units;
    }

    public void saveVendorSalers(List<VendorSaler> vendorTypes) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(vendorTypes);
        realm.commitTransaction();
    }

    public String[] getVendorSalersName() {
        RealmResults<VendorSaler> items = realm.where(VendorSaler.class).findAll();
        String[] units = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            units[i] = items.get(i).getName();
        }
        return units;
    }


    //Inkasimi
    public void saveInkasimi(InkasimiDetail inkasimiDetail) {

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(inkasimiDetail);
        realm.commitTransaction();

    }

    public RealmResults<InkasimiDetail> getInkasimi() {
        return realm.where(InkasimiDetail.class).findAll();
    }

    //Depozita
    public void saveDepozita(DepositPost depositPost) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(depositPost);
        realm.commitTransaction();

    }

    public RealmResults<DepositPost> getDepozitat() {
        return realm.where(DepositPost.class).findAll();
    }

    //Aksionet
    public void saveAksionet(ActionData actionData) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ActionData.class);
        realm.copyToRealmOrUpdate(actionData);
        realm.commitTransaction();
    }

    public ActionData getAksionet() {
        return realm.where(ActionData.class).findFirst();
    }

    private void sendError(String merror) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String sStackTrace = sw.toString();
        ErrorPost errorPost = new ErrorPost();
        errorPost.setToken(preferences.getToken());
        errorPost.setUser_id(preferences.getUserId());
        errorPost.setUser_id(preferences.getUserId());
        ArrayList<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setMessage(merror);
        errors.add(error);
        errorPost.setErrors(errors);
        apiService.sendError(errorPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                }, throwable1 -> {

                });
    }

    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", value));
    }
}

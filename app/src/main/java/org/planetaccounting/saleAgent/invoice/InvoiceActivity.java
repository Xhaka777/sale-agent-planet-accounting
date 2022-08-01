package org.planetaccounting.saleAgent.invoice;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.aksionet.ArticleFinItems;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.InvoiceActivityBinding;
import org.planetaccounting.saleAgent.databinding.InvoiceItemBinding;
import org.planetaccounting.saleAgent.db.DatabaseOperations;
import org.planetaccounting.saleAgent.events.FinishInvoiceActivity;
import org.planetaccounting.saleAgent.model.InvoiceItem;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.invoice.InvoiceItemPost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePostObject;
import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.ActivityPrint;
import org.planetaccounting.saleAgent.utils.InvoicePrintUtil;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.lang.System.out;

/**
 * Created by tahirietrit on 27/12/17.
 */

public class InvoiceActivity extends AppCompatActivity {

    //Bashk me Strikt mode remove edhe kto
    public static final String ACTION_PRINT = "action_print";
    public static final String PRINT_Z_RAPORT = "print_z_raport";
    public static final String ACTION_ADD_ITEMS = "add_items";
    public static final String ACTION = "action";
    public static final String ITEMS = "items";
    public static boolean uploadThisMf = false;
    public static String isBill = "0";
    public String cash = "0";
    public String isPaid = "0";
//    InvoiceActivityBinding binding;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    ArrayList<InvoiceItem> stockItems = new ArrayList<>();
    Client client;
    int checked = 0;
    Date cDate;
    String fDate;
    String dDate;
    int pageWidth = 595;
    int pageHeight = 842;
    int stationPos;
    // Amount's
    Double amount_no_vat = 0.00;
    Double amount_of_discount = 0.00;
    Double amount_of_vat = 0.00;
    Double amount_vat = 0.00;
    private PrintManager printManager;
    @Inject
    RealmHelper realmHelper;
    DatabaseOperations mydb ;
    ArticleFinItems articleFin;
    //DB Loading Action Article
    String[] idAction;
    String[] fromDateAction;
    String[] tillDateAction;
    String[] itemId;
    String[] quantityAction;
    String[] discountAction;
    String discountiZbritur ="0";
    int pozitaeArtikullit = 0;
    String itemID = "";
    Item itemsi;
    Item itemsiAksion;
    int ActionPos = 1;
    boolean collectionAction;
    int item_in_action = 0;
    int pArtikulli =0;
    int pos = 0;
    InvoiceItem[] invoiceItemsss;
    //Collection
    String[] subColItem ;
    String[] subColQuantity;
    String[] fromdateCol;
    String[] tilldateCol;
    String[] subColDiscount;
    String discountiArtZbritur;
//
//
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        discountiZbritur = "0";
//        collectionAction = false;
//        mydb = new DatabaseOperations(getApplicationContext());
//        ArticleAction(); // Aksionet per Artikujt merren prej databazes me kete
//        binding = DataBindingUtil.setContentView(this, org.planetaccounting.saleAgent.R.layout.invoice_activity);
//        Kontabiliteti.getKontabilitetiComponent().inject(this);
//
//        cDate = new Date();
//        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
//        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
//
//        // Invoice Date
//        binding.dataEdittext.setText(fDate);
//
//        // Invoice Number
//        binding.numriFatures.setText(preferences.getUserId() + "-" + realmHelper.getAutoIncrementIfForInvoice());
//
//        // Itemi per kontroll
//        ColAction();
//
//        // Client Event
//        binding.emriKlientit.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, realmHelper.getClientsNames()));
//        binding.emriKlientit.setOnItemClickListener((adapterView, view, i, l) -> {
//            client = realmHelper.getClientFromName(binding.emriKlientit.getText().toString());
//            if (realmHelper.getClientStations(client.getName()).length > 0) {
//                binding.njersiaEdittext.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, realmHelper.getClientStations(client.getName())));
//                binding.njersiaEdittext.setEnabled(true);
//                binding.njersiaEdittext.requestFocus();
//                binding.njersiaEdittext.showDropDown();
//                binding.njersiaEdittext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        stationPos = i;
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
//            } else {
//                binding.njersiaEdittext.setText("--");
//                binding.njersiaEdittext.setEnabled(false);
//            }
//            binding.zbritjaKlientit.setText("Zbritja e klientit: " + client.getDiscount() + " %");
//        });
//
//        // BTN ADD ITEM ON INOICE (BTN SHTO)
//        binding.shtoTextview.setOnClickListener(view -> {
//            if (client != null) {
//                // Add new empty item on invoice
//                addInvoiceItem();
//            } else {
//                Toast.makeText(getApplicationContext(), "Ju lutem zgjedhni klientin para se ti shtoni artikujt", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // BTN Faturë
//        binding.fatureButton.setOnClickListener(view -> {
//            isBill = "0";
//            // Check Stock items
//            if (stockItems.size() > 0) {
//                if(checkSasia()) {
//                    showCashDialog();
//                }else{
//                    Toast.makeText(getApplicationContext(), "Një ose më shum artikuj kan sasine zero", Toast.LENGTH_SHORT).show();
//                }
//            }else{
//                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
//
//        // BTN Kupno Fiskal
//        binding.kuponButton.setOnClickListener(view -> {
//            isBill = "1";
//            if (stockItems.size() > 0) {
//                if(checkSasia()) {
//                    showCashDialog();
//                }else{
//                    Toast.makeText(getApplicationContext(), "Një ose më shum artikuj kan sasine zero", Toast.LENGTH_SHORT).show();
//                }
//            }else{
//                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                binding.emriKlientit.showDropDown();
//                binding.emriKlientit.requestFocus();
//            }
//        }, 500);
//    }
//
//
//    public void ColAction(){
//        Cursor res = mydb.getSubCollectionAction();
//
//        subColItem = new String[mydb.getSubCollectionAction().getColumnName(0).length()];
//        subColQuantity = new String[mydb.getSubCollectionAction().getColumnName(0).length()];
//        fromdateCol = new String[mydb.getCollectionAction().getColumnName(0).length()];
//        tilldateCol = new String[mydb.getCollectionAction().getColumnName(0).length()];
//        subColDiscount = new String[mydb.getSubCollectionAction().getColumnName(0).length()];
//
//        try{
//            if (res.moveToFirst()) {
//                for (int i = 0; i < res.getCount(); i++) {
//                    subColItem[i] = res.getString(0) ;
//                    subColQuantity[i] = res.getString(1);
//                    subColDiscount[i] = res.getString(2);
//                    res.moveToNext();
//                }
//                res.close();
//                Cursor res2 = mydb.getCollectionAction();
//                for(int i=0; i < res2.getCount(); i++){
//                    fromdateCol[i] = res2.getString(0);
//                    tilldateCol[i] = res2.getString(1);
//                    res2.moveToNext();
//                }
//                res2.close();
//            }
//        }catch (NullPointerException ex){}
//        catch (IndexOutOfBoundsException inx){}
//    }
//
//
//    public void ArticleAction(){
//        Cursor res = mydb.getArticleActions();
//        idAction = new String[mydb.getArticleActions().getColumnName(0).length()];
//        fromDateAction = new String[mydb.getArticleActions().getColumnName(0).length()];
//        tillDateAction = new String[mydb.getArticleActions().getColumnName(0).length()];
//        itemId = new String[mydb.getArticleActions().getColumnName(0).length()];
//        quantityAction = new String[mydb.getArticleActions().getColumnName(0).length()];
//        discountAction = new String[mydb.getArticleActions().getColumnName(0).length()];
//
//        try{
//            if (res.moveToFirst()) {
//                for (int i = 0; i < res.getCount(); i++) {
//                    idAction[i] = res.getString(0) ;
//                    fromDateAction[i] = res.getString(1);
//                    tillDateAction[i] = res.getString(2);
//                    itemId[i] = res.getString(3);
//                    quantityAction[i] = res.getString(4);
//                    discountAction[i] = res.getString(5);
//                    res.moveToNext();
//                }
//            }
//            res.close();
//        }catch(Exception ex){}
//    }
//
//    // On Print Invoice
//    private void openPrintingActivity() {
//        Intent i = new Intent(getApplicationContext(), ActivityPrint.class);
//        i.putExtra(ACTION, ACTION_PRINT);
//        i.putParcelableArrayListExtra(ITEMS, stockItems);
//        startActivity(i);
//
//    }
//
//    public boolean checkSasia(){
//        for (int i = 0; i < stockItems.size(); i++) {
//            double sasia;
//            try {
//                sasia = Double.parseDouble(stockItems.get(i).getSasia());
//            }catch (Exception e){
//                sasia = Double.parseDouble(stockItems.get(i).getSasia());
//            }
//            if(sasia == 0){
//                return false;
//            }
//        }
//        return true;
//    }
//    InvoiceItemBinding invoiceB;
//
//    private void addInvoiceItem() {
//        // Action Collection
//        if(collectionAction){
//            for(int i=0; i < item_in_action; i++){
//                addArticleInvoiceAction(i);
//            }
//            collectionAction = false;
//            calculation_total();
//            item_in_action = 0;
//        }else{
//            // Item single type
//            addInvoiceArticle();
//            calculation_total();
//        }
//    }
//
//    public void addArticleInvoiceAction(int index_item){
//        final InvoiceItem[] invoiceItem = new InvoiceItem[1];
//
//        InvoiceItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), org.planetaccounting.saleAgent.R.layout.invoice_item, binding.invoiceItemHolder, false);
//        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsName()));
//
//        invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByGroupID(itemsi.getItems().get(index_item).getGroupItem()));  // Get Group ID
//        invoiceItem[0].setDiscount(cutTo2(Double.parseDouble(subColDiscount[index_item]))+""); // Get Action Discount
//        invoiceItem[0].setId(itemsi.getItems().get(index_item).getId());
//        Double discount_for_item = Double.parseDouble(subColDiscount[index_item]);
//        invoiceItem[0].setSasia(cutTo2(Double.parseDouble(subColQuantity[index_item]))+""); // Get Action Quantity
//        invoiceItem[0].setAction(true);
//        invoiceItem[0].setActionId(itemsi.getId());
//        try {
//            stockItems.set(pos, invoiceItem[0]);
//        } catch (IndexOutOfBoundsException e) {
//            stockItems.add(pos, invoiceItem[0]);
//        }
//        pos++;
//        discountiZbritur = invoiceItem[0].getDiscount();
//        findCodeAndPosition(invoiceItem[0]);
//        fillInvoiceItemData(itemBinding, invoiceItem[0]);
//
//        // Item Numbers
//        itemBinding.shifraTextview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsCodes()));
//        if(discount_for_item != 100) {
//            itemBinding.shifraTextview.setOnItemClickListener((adapterView, view, i, l) -> {
//                invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByGroupID(itemsi.getItems().get(index_item).getGroupItem()));
//            });
//        }
//        new Handler().postDelayed(new Runnable() {
//            public void run(){
//                itemBinding.emertimiTextview.setText(realmHelper.getItemsByGroupID(itemsi.getItems().get(index_item).getGroupItem()).getName());
//                itemBinding.zbritjaTextview.setText(cutTo5(Double.parseDouble(subColDiscount[index_item]))+"");
//                itemBinding.sasiaTextview.setText(cutTo2(Double.parseDouble(subColQuantity[index_item]))+"");
//            }
//        },100);
//
//        // Item Quantity
//        if(discount_for_item != 100) {
//            itemBinding.sasiaTextview.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    try {
//                        if (itemBinding.sasiaTextview.getText().length() <= 0) {
//                            invoiceItem[0].setSasia("0");
//                            Toast.makeText(InvoiceActivity.this, "Shëno sasin ", Toast.LENGTH_LONG).show();
//                        } else {
//                            invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString() + "");
//                            // Item row index
//                            pozitaeArtikullit = (int) itemBinding.getRoot().getTag();
//                            // String Quantity
//                            String str_quantity = itemBinding.sasiaTextview.getText().toString();
//                            double sasia = 0;
//                            if (str_quantity.length() > 0) {
//                                sasia = Double.parseDouble(str_quantity);
//                            } else if (str_quantity.length() == 0) {
//                                invoiceItem[0].setSasia("0");
//                            }
//                            // Item Relacion
//                            double relacion = (int) invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getRelacion();
//                            // Quantit avaliable on warehouse
//                            double quantity_base_on_warehouse = Double.parseDouble(invoiceItem[0].getQuantity());
//                            // Quantity / Relacion
//                            double availableQuantity = quantity_base_on_warehouse / relacion;
//
//                            if (sasia <= availableQuantity && sasia > 0) {
//                                invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
////                                action_collection_quantity_regular(stockItems.get(pozitaeArtikullit).getAction_id());
//                                findCodeAndPosition(invoiceItem[0]);
//                                fillInvoiceItemData(itemBinding, invoiceItem[0]);
//                            } else {
//                                invoiceItem[0].setSasia("0");
//                                itemBinding.sasiaTextview.setText("0" + "");
//                                Toast.makeText(getApplicationContext(), "Nuk keni sasi të mjaftuesheme në depo", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }catch (Exception e){
//                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    calculation_total();
//                }
//            });
//        }else{
//            itemBinding.sasiaTextview.setKeyListener(null);
//        }
//
//        if(index_item == 0){
//            // Remove Button
//            itemBinding.removeButton.setOnClickListener(view -> {
//                try {
//                    int pos = (int) itemBinding.getRoot().getTag();
//                    String action_id = stockItems.get(pos).getActionId();
//
//                    for (int i = 0; i < stockItems.size(); i++) {
//                        String a = stockItems.get(i).getActionId();
//
//                        if(a.equals(action_id)){
////                        if (a.toString() == action_id.toString()) {
//                            stockItems.remove(i);
//                            binding.invoiceItemHolder.removeView(binding.invoiceItemHolder.getChildAt(i));
//                            i--;
//                        }
//                        System.out.println(a);
//                    }
////                    binding.invoiceItemHolder.removeView(itemBinding.getRoot());
//                    calculation_total();
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }else{
//            itemBinding.removeButton.setVisibility(View.INVISIBLE);
//        }
//
//        itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
//        binding.invoiceItemHolder.addView(itemBinding.getRoot());
//    }
//
//    //
//    public void addInvoiceArticle(){
//
////        final InvoiceItem[] invoiceItem = new InvoiceItem[1];
//////        final Item[] item = new Item[1];
////        InvoiceItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), org.planetaccounting.saleAgent.R.layout.invoice_item, binding.invoiceItemHolder, false);
////        itemBinding.sasiaTextview.setEnabled(false);
////        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsName()));
////        if(ActionPos<=2){
////            itemBinding.emertimiTextview.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    itemBinding.emertimiTextview.setText(realmHelper.getItemsByGroupID(itemsi.getItems().get(1).getGroupItem()).getName());
////
////                }
////            },100);
////        }
////        itemBinding.emertimiTextview.setOnItemClickListener((adapterView, view, i, l) -> {
////
////            itemsi = realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString());
////            if(itemsi.getType().equalsIgnoreCase("action")){
////
//
////                for(int h=0; h< itemsi.getItems().size();h++){
////                    invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByGroupID(itemsi.getItems().get(h).getGroupItem()));
////                    addActionInvoice(itemBinding,invoiceItem[0],h);}
////                itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
////                binding.invoiceItemHolder.addView(itemBinding.getRoot());
////            }
////              else{
////                itemBinding.emertimiTextview.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        itemBinding.emertimiTextview.setText(invoiceItem[0].getName());
////                    }
////                },100);
////
////                invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString()));
////                int pos = (int) itemBinding.getRoot().getTag();
////                try {
////                    //Stock item sasia dhe copa pako paketa ...
////                    stockItems.set(pos, invoiceItem[0]);
////                    itemBinding.sasiaTextview.setEnabled(true);
////                } catch (IndexOutOfBoundsException e) {
////                    stockItems.add(pos, invoiceItem[0]);
////                    itemBinding.sasiaTextview.setEnabled(true);
////                    pozitaeArtikullit = pos;
////                    stockArticleCheck(itemBinding.njesiaTextview.getText().toString(),itemBinding.sasiaTextview.getText().toString()+"");
////                    itemBinding.zbritjaTextview.setText(discountiZbritur + " %");
////                    //stockActionCheck(pos);
////
////                }
////                itemBinding.sasiaTextview.requestFocus();
////                findCodeAndPosition(invoiceItem[0]);
////                fillInvoiceItemData(itemBinding, invoiceItem[0]);
////            });
////        itemBinding.emertimiTextview.showDropDown();
////        itemBinding.emertimiTextview.requestFocus();
////        itemBinding.shifraTextview.setAdapter(new ArrayAdapter<String>(this,
////                android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsCodes()));
////        itemBinding.shifraTextview.setOnItemClickListener((adapterView, view, i, l) -> {
////            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByCode(itemBinding.shifraTextview.getText().toString()));
////
////            itemBinding.sasiaTextview.requestFocus();
////            findCodeAndPosition(invoiceItem[0]);
////            fillInvoiceItemData(itemBinding, invoiceItem[0]);
////        });
////        //Shifra kur ndrron . bej keto ndryshime
////        itemBinding.shifraTextview.addTextChangedListener(new TextWatcher() {
////            @Override
////            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////                Log.d("BeforeText",charSequence.toString());
////
////            }
////
////            @Override
////            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////                //stockActionCheck(pozitaeArtikullit); Kontrollo a vlen actioni per artikullin
////                stockArticleCheck(itemBinding.njesiaTextview.getText().toString(),itemBinding.sasiaTextview.getText().toString());
////                itemBinding.zbritjaTextview.setText(discountiZbritur + " %");
////            }
////
////            @Override
////            public void afterTextChanged(Editable editable) {
////
////            }
////        });
////        discountiZbritur = "0";
//        final InvoiceItem[] invoiceItem = new InvoiceItem[1];
//        InvoiceItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), org.planetaccounting.saleAgent.R.layout.invoice_item, binding.invoiceItemHolder, false);
//
//
//
//        // Item Name Event
//        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsName()));
//        itemBinding.emertimiTextview.setOnItemClickListener((adapterView, view, i, l) -> {
//        invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString()));
//        pos = (int) itemBinding.getRoot().getTag();
//        // If Action clicked
//        if(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString()).getType().equalsIgnoreCase("action")){
//            itemsi = realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString());
//            String action_id = itemsi.getId();
//            item_in_action = itemsi.getItems().size();
//            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByGroupID(itemsi.getItems().get(0).getGroupItem()));
//            invoiceItem[0].setActionId(action_id);
//            pos = (int)itemBinding.getRoot().getTag();
//            try {
//                itemBinding.zbritjaTextview.setText(discountiZbritur);
//                invoiceItem[0].setDiscount(itemBinding.zbritjaTextview.getText().toString());
//                stockItems.set(pos, invoiceItem[0]);
//            } catch (IndexOutOfBoundsException e) {
//                stockItems.add(pos, invoiceItem[0]);
//            }
//
//
//
//                findCodeAndPosition(invoiceItem[0]);
//                fillInvoiceItemData(itemBinding, invoiceItem[0]);
//                itemBinding.removeButton.performClick();
//                collectionAction = true;
//                addInvoiceItem();
//            }else{
//                // on Single Item click
//                try {
//                    String discount_init = "0";
//                    pos = (int) itemBinding.getRoot().getTag();
//                    itemBinding.zbritjaTextview.setText(discount_init);
//                    invoiceItem[0].setDiscount(discount_init);
//                    stockItems.set(pos, invoiceItem[0]);
//                } catch (IndexOutOfBoundsException e) {
//                    stockItems.add(pos, invoiceItem[0]);
//                }
//                itemBinding.sasiaTextview.requestFocus();
//                findCodeAndPosition(invoiceItem[0]);
//                fillInvoiceItemData(itemBinding, invoiceItem[0]);
//            }
//        });
//        itemBinding.emertimiTextview.showDropDown();
//        itemBinding.emertimiTextview.requestFocus();
//
//        // Item Number Event
//        itemBinding.shifraTextview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsCodes()));
//        itemBinding.shifraTextview.setOnItemClickListener((adapterView, view, i, l) -> {
//            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByCode(itemBinding.shifraTextview.getText().toString()));
//            itemBinding.sasiaTextview.requestFocus();
//            findCodeAndPosition(invoiceItem[0]);
//            fillInvoiceItemData(itemBinding, invoiceItem[0]);
//        });
//
//        // Item Unit
//        itemBinding.njesiaTextview.setOnClickListener(view -> dialog(invoiceItem[0], itemBinding));
//        itemBinding.njesiaTextview.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    pozitaeArtikullit = (int) itemBinding.getRoot().getTag();
//                    String discount_action = stock_article_action_check(itemBinding.njesiaTextview.getText().toString(), itemBinding.sasiaTextview.getText().toString());
//                    itemBinding.zbritjaTextview.setText(discount_action + "");
//
//                }catch (Exception e){
//                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                calculation_total();
//            }
//        });
//
//        // Item Quantity
//        itemBinding.sasiaTextview.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d("BeforeTextChanged",charSequence.toString());
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                try{
//                    // Item row index
//                    pozitaeArtikullit = (int) itemBinding.getRoot().getTag();
//                    // String Quantity
//                    String str_quantity =  itemBinding.sasiaTextview.getText().toString();
//                    double sasia = 0;
//                    if (str_quantity.length() > 0) {
//                        sasia = Double.parseDouble(str_quantity);
//                    }else if(str_quantity.length() == 0){
//                        invoiceItem[0].setSasia("0");
//                    }
//
//                    // Set Barcode
//                    invoiceItem[0].setBarcode(invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getBarcode());
//
//                    // Item Relacion
//                    double relacion = (int)invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getRelacion();
//                    // Quantit avaliable on warehouse
//                    double quantity_base_on_warehouse = Double.parseDouble(invoiceItem[0].getQuantity());
//                    // Quantity / Relacion
//                    double availableQuantity = quantity_base_on_warehouse / relacion;
//
//                    if (sasia <= availableQuantity && sasia > 0) {
//                        String dicount_action = stock_article_action_check(itemBinding.njesiaTextview.getText().toString(),itemBinding.sasiaTextview.getText().toString());
//                        itemBinding.zbritjaTextview.setText(dicount_action);
//                        invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
//                        invoiceItem[0].setDiscount(dicount_action);
//
//                        findCodeAndPosition(invoiceItem[0]);
//                        fillInvoiceItemData(itemBinding, invoiceItem[0]);
//                    } else {
//                        invoiceItem[0].setSasia("0");
//                        itemBinding.sasiaTextview.setText("0"+"");
//                        Toast.makeText(getApplicationContext(), "Nuk keni sasi të mjaftuesheme në depo", Toast.LENGTH_SHORT).show();
//                    }
//                    calculation_total();
//                }catch (Exception e){
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });
//
//        // Remove Button
//        itemBinding.removeButton.setOnClickListener(view -> {
//            try{
//                int pos = (int) itemBinding.getRoot().getTag();
//                stockItems.remove(pos);
//                binding.invoiceItemHolder.removeView(itemBinding.getRoot());
//                calculation_total();
//            }catch (Exception e){
//                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
//
//        // Add on Item Holder
//        binding.invoiceItemHolder.addView(itemBinding.getRoot());
//    }
//
//    public void action_collection_quantity_regular(String action_id){
//        try{
//            itemsiAksion = realmHelper.getItemsByGroupID(action_id);
//            Double gratis_item = 0.00;
//            Double minumium_gratis = 0.00;
//            Double divide_quantity = 0.00;
//            int index_action_item = 0;
//            for(int i = 0; i < stockItems.size(); i++){
//                String a_id = stockItems.get(i).getActionId();
//                Double discount_article = Double.parseDouble(stockItems.get(i).getDiscount());
//                if(a_id.equals(action_id)){
//                    Double table_quantity = Double.parseDouble(stockItems.get(i).getSasia());
//                    int item_id = Integer.parseInt(stockItems.get(i).getId());
//
//                    Double quantity_action = Double.parseDouble(subColQuantity[index_action_item]);
//                    int item_action_id = Integer.parseInt(itemsiAksion.getItems().get(index_action_item).getId());
//
//                    if(item_action_id == item_id){
//                        divide_quantity = table_quantity/quantity_action;
//                        if(minumium_gratis < divide_quantity){
//                            minumium_gratis = divide_quantity;
//                        }
//                    }
//                    index_action_item++;
//                }
//            }
//
//            for(int i = 0; i < stockItems.size(); i++){
//                String a_id = stockItems.get(i).getActionId();
//                Double discount_article = Double.parseDouble(stockItems.get(i).getDiscount());
//                if(a_id.equals(action_id)){
//                    if(discount_article == 100){
//                        stockItems.get(i).setSasia(minumium_gratis.toString());
//                    }
//                }
//            }
//            minumium_gratis = 0.00;
//        }catch (Exception e){
//            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void calculation_total(){
//        // Calculation Amount no VAT
//        this.amount_no_vat = 0.00;
//        this.amount_vat = 0.00;
//        this.amount_of_vat = 0.00;
//        this.amount_of_discount = 0.00;
//        for (int i = 0; i < stockItems.size(); i++) {
//            this.amount_no_vat += stockItems.get(i).getVleraPaTvsh();
//            this.amount_of_discount += stockItems.get(i).getVleraEZbritur();
//            this.amount_of_vat += stockItems.get(i).getVleraETvsh();
//            this.amount_vat += stockItems.get(i).getVleraTotale();
//        }
//
//        // Set Amount of VAT
//        this.amount_of_vat = cutTo2(amount_of_vat);
//        binding.vleraETvsh.setText("Vlera e TVSH-së: " + amount_of_vat.toString());
//
//        // Set Amount no VAT
//        this.amount_no_vat = cutTo2(amount_no_vat);
//        binding.vleraPaTvsh.setText("Vlera pa TVSH: " + amount_no_vat.toString());
//        // Set Amount of Discount
//        this.amount_of_discount = cutTo2(amount_of_discount);
//        binding.vleraEZbritur.setText("Vlera e zbritur: " + amount_of_discount.toString());
//        // Set Amount Total
//        this.amount_vat = cutTo2(amount_vat);
//        binding.vleraTotale.setText("Vlera Totale: " + amount_vat);
//    }
//
//    private String stock_article_action_check(String njesia, String sasia) {
//        try{
//            if(sasia.equals("")){
//                sasia = "0";
//            }
//            for(int i=0; i< stockItems.get(pozitaeArtikullit).getItems().size();i++){
//                if(stockItems.get(pozitaeArtikullit).getItems().get(i).getUnit().equals(njesia)){
//                    itemID = stockItems.get(pozitaeArtikullit).getItems().get(i).getId();
//                }
//            }
//            boolean validimi = false;
//            for(int i=0; i<itemId.length; i++){
//                if(itemID.equals(itemId[i]) && validimi == false){
//                    Date dataFillestare = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fromDateAction[i]);
//                    Date dataoerfindmtare = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(tillDateAction[i]);
//                    if(dataFillestare.before(cDate) && dataoerfindmtare.after(cDate) && validimi == false){
//                        if(Double.parseDouble(sasia)>= Double.parseDouble(quantityAction[i])&& validimi == false){
//                            validimi = true;
//                            return discountAction[i].toString();
//                        }else{
//                            return "0";
//                        }
//                    }
//                }
//            }
//        }catch (NullPointerException ex){
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return "0";
//    }
//
//    private void fillInvoiceItemData(InvoiceItemBinding itemBinding, InvoiceItem invoiceItem) {
//        Log.d("Fill Invoice ","");
//
//        itemBinding.emertimiTextview.setText(invoiceItem.getName());
//        itemBinding.shifraTextview.setText(invoiceItem.getSelectedItemCode());
//        itemBinding.njesiaTextview.setText(invoiceItem.getSelectedUnit());
//        itemBinding.vlera.setText("" + calculateValueOfItem(invoiceItem));
//        itemBinding.zbritjaTextview.setText(invoiceItem.getDiscount() + "");
//        for(int i=0; i< invoiceItem.getItems().size();i++){
//            if(invoiceItem.getItems().get(i).getUnit().equalsIgnoreCase(invoiceItem.getSelectedUnit())){
//                itemBinding.cmimiTvsh.setText(""+realmHelper.getItemsByName(invoiceItem.getName()).getItems().get(i).getPriceVatSale());
//            }
//        }
//
//    }
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//
//    private void createCollectionPostItem() {
//
//        InvoicePost invoicePost = new InvoicePost();
//        invoicePost.setId(realmHelper.getAutoIncrementIfForInvoice());
//        invoicePost.setNo_invoice(binding.numriFatures.getText().toString());
//        invoicePost.setPartie_id(client.getId());
//        invoicePost.setPartie_name(client.getName());
//        if (client.getStations().size() > 0) {
//            invoicePost.setPartie_station_id(client.getStations().get(stationPos).getId());
//        } else {
//            invoicePost.setPartie_station_id("0");
//        }
//        invoicePost.setPartie_station_name(binding.njersiaEdittext.getText().toString());
//        invoicePost.setPartie_address(client.getAddress());
//        invoicePost.setPartie_city(client.getCity());
//        invoicePost.setPartie_state_id(client.getState());
//        //add a few
//        invoicePost.setSale_station_id(preferences.getStationId());
//        invoicePost.setInvoice_date(dDate);
//        invoicePost.setDiscount(client.getDiscount());
//        invoicePost.setIs_payed(isPaid);
//        invoicePost.setAmount_no_vat(amount_no_vat.toString());
//        invoicePost.setAmount_of_vat(amount_of_vat.toString());
//        invoicePost.setAmount_discount(amount_of_discount.toString());
//        invoicePost.setAmount_payed(cash);//vlera qe e jep kesh
//        invoicePost.setLocation("42.3, 23.44");
//        invoicePost.setAmount_with_vat(amount_vat.toString());
//        invoicePost.setId_saler(preferences.getUserId());
//        //is bill 0 = fature, 1 = kupon
//        invoicePost.setIs_bill(isBill);
//        double totalNoDiscount = 0;
//        RealmList<InvoiceItemPost> invoiceItemPosts = new RealmList<>();
//        for (int i = 0; i < stockItems.size(); i++) {
//            totalNoDiscount += (Double.parseDouble(stockItems.get(i).getChildList().get(stockItems.get(i).getSelectedPosition()).getPriceVatSale()) * Double.parseDouble(stockItems.get(i).getSasia()));
//            InvoiceItemPost invoiceItemPost = new InvoiceItemPost();
//            invoiceItemPost.setId(stockItems.get(i).getId());
//            invoiceItemPost.setNo_order(String.valueOf(i));
//            invoiceItemPost.setNo(stockItems.get(i).selectedItemCode);
//            invoiceItemPost.setId_item_group(stockItems.get(i).getId());
//            invoiceItemPost.setId_item(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getId());
//            invoiceItemPost.setName(stockItems.get(i).getName());
//            //invoiceItemPost.setQuantity(stockItems.get(i).getSasia());
//            for(int j =0 ; j < stockItems.get(i).getItems().size(); j++){
//                if(invoiceItemPost.getId_item().equals(stockItems.get(i).getItems().get(j).getId())){
//                    // Quantity
//                    invoiceItemPost.setQuantity(stockItems.get(i).getSasia() + "");
////                    // Quantity Base
//                    invoiceItemPost.setBase_quantity(Double.parseDouble(stockItems.get(i).getSasia()) *
//                            stockItems.get(i).getItems().get(j).getRelacion()+"");
//                    // Relacion
//                    invoiceItemPost.setRelacioni(stockItems.get(i).getItems().get(j).getRelacion()+"");
////                    // Barcode
//                    invoiceItemPost.setBarcode(stockItems.get(i).getItems().get(j).getBarcode());
//                }
//
//            }
//
//
//            invoiceItemPost.setUnit(stockItems.get(i).getSelectedUnit());
//            double vleraPaTvsh = stockItems.get(i).getVleraPaTvsh() / Double.parseDouble(stockItems.get(i).getSasia());
//
//            invoiceItemPost.setPrice(String.valueOf(vleraPaTvsh));
////            invoiceItemPost.setDiscount(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getDiscount());
//            invoiceItemPost.setDiscount(stockItems.get(i).getDiscount()+"00"); //ShkelqimB
//            invoiceItemPost.setVat_id(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatCodeSale());
//            invoiceItemPost.setPrice_vat(String.valueOf(stockItems.get(i).getPriceWithvat()));
//            invoiceItemPost.setAmount_of_discount(String.valueOf(stockItems.get(i).getVleraEZbritur()));
//            invoiceItemPost.setVat_rate(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatRate());
//            invoiceItemPost.setTotalPrice(String.valueOf(stockItems.get(i).getVleraTotale()));
//
//
//            invoiceItemPosts.add(invoiceItemPost);
//
//            System.out.println(invoiceItemPost.getQuantity());
//            // qetu muj me ndreq stokin
//        }
//        invoicePost.setTotal_without_discount(String.valueOf(totalNoDiscount));
//        invoicePost.setItems(invoiceItemPosts);
//        RealmList<InvoicePost> invoicePosts = new RealmList<>();
//        invoicePosts.add(invoicePost);
//        InvoicePostObject invoicePostObject = new InvoicePostObject();
//        invoicePostObject.setToken(preferences.getToken());
//        invoicePostObject.setUser_id(preferences.getUserId());
//        invoicePostObject.setInvoices(invoicePosts);
//
//        InvoicePrintUtil util = new InvoicePrintUtil(invoicePost, binding.web, getApplicationContext(), client, printManager);
//
//        apiService.postFaturat(invoicePostObject)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(responseBody -> {
//                    if (responseBody.getSuccess()) {
//                        invoicePost.setSynced(true);
//                        realmHelper.saveInvoices(invoicePost);
//                        Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur dhe sinkronizuar!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        invoicePost.setSynced(false);
//                        realmHelper.saveInvoices(invoicePost);
//                        Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
//                    }
//                }, throwable -> {
//                    invoicePost.setSynced(false);
//                    realmHelper.saveInvoices(invoicePost);
//                    Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
//                });
//
//
//    }
//
//    private void findCodeAndPosition(InvoiceItem invoiceItem) {
//        for (int i = 0; i < invoiceItem.getItems().size(); i++) {
//            if (invoiceItem.getDefaultUnit().equalsIgnoreCase(invoiceItem.getItems().get(i).getUnit())) {
//                checked = i;
//                invoiceItem.setSelectedItemCode(invoiceItem.getItems().get(i).getNumber());
//                invoiceItem.setSelectedUnit(invoiceItem.getItems().get(i).getUnit());
//                invoiceItem.setSelectedPosition(checked);
//            }
//        }
//    }
//
//    void dialog(InvoiceItem invoiceItem, InvoiceItemBinding binding) {
//        try {
//            String[] units = realmHelper.getItemUnits(invoiceItem.getName());
//            AlertDialog.Builder alt_bld = new AlertDialog.Builder(InvoiceActivity.this);
//            alt_bld.setSingleChoiceItems(units, checked, (dialog, item) -> {
//                checked = item;
//                invoiceItem.setSelectedItemCode(invoiceItem.getItems().get(checked).getNumber());
//                invoiceItem.setSelectedUnit(invoiceItem.getItems().get(checked).getUnit());
//                invoiceItem.setSelectedPosition(checked);
//                dialog.dismiss();
//                fillInvoiceItemData(binding, invoiceItem);
//                calculation_total();
//            });
//            AlertDialog alert = alt_bld.create();
//            alert.show();
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Ju lutem zgjedhni produktin", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    ArrayList<String> splitLine(String text, float maxWidth, Paint paint) {
//
//        String[] splitText = text.split(" ");
//
//        String currentLine = "";
//        ArrayList<String> lineList = new ArrayList<>();
//
//        for (String token : splitText) {
//
//            if (paint.measureText(currentLine + token + " ") > maxWidth) {
//
//                lineList.add(currentLine);
//                currentLine = token + " ";
//            } else {
//
//                currentLine += token + " ";
//            }
//        }
//
//        lineList.add(currentLine);
//
//        return lineList;
//    }
//
//    private double calculateValueOfItem(InvoiceItem invoiceItem) {
//        double amount_with_vat = 0.00;
//        try {
//            // Quantity
//            BigDecimal quantity = new BigDecimal(invoiceItem.getSasia());
//
//            // Price Sale (with VAT) untouchable
//            BigDecimal priceSale = new BigDecimal(invoiceItem.getChildList().get(invoiceItem.getSelectedPosition()).getPriceVatSale());
//
//            // VAT Rate
//            double vat_rate = 0;
//            vat_rate = Double.parseDouble(invoiceItem.getChildList().get(invoiceItem.getSelectedPosition()).getVatRate()) * 0.01;
//            BigDecimal vatRate = new BigDecimal(vat_rate);
//            vatRate = vatRate.setScale(5, BigDecimal.ROUND_HALF_UP);
//
//            // Client Discount
//            double client_discount = Double.parseDouble(client.getDiscount()) * 0.01;
//            BigDecimal clientDiscount = new BigDecimal(Double.toString(client_discount));
//
//            // Article Discount
//            double article_discount = article_discount = Double.parseDouble(invoiceItem.getDiscount()) * 0.01;
//            BigDecimal articleDiscount = new BigDecimal(article_discount);
//
//
//            // Price no VAT with Discount, Price Base, Price no VAT, Price Real Sale
//            BigDecimal vat_rate_for_divide = vatRate.add(new BigDecimal(1));
//
//            BigDecimal price_base = priceSale.divide(vat_rate_for_divide, 5, BigDecimal.ROUND_HALF_UP);
//
//            // Price no VAT
//            invoiceItem.setVleraPaTvsh(price_base.doubleValue());
//
//            BigDecimal article_discount_for_divide = new BigDecimal(article_discount+1);
//            BigDecimal price_no_vat_no_discount_article = price_base.divide(article_discount_for_divide, 5, BigDecimal.ROUND_HALF_UP);
//
//            BigDecimal client_discount_for_divide = new BigDecimal(client_discount+1);
//            BigDecimal price_no_vat = price_no_vat_no_discount_article.divide(client_discount_for_divide, 5, BigDecimal.ROUND_HALF_UP);
//
//
//
//
//            if(article_discount == 1|| client_discount == 1 ){
//                price_no_vat = price_no_vat.subtract(price_no_vat);
//            }
//
//
//            BigDecimal price_vat_real_sale = price_no_vat.multiply(vat_rate_for_divide);
//            price_vat_real_sale = price_vat_real_sale.setScale(5,BigDecimal.ROUND_HALF_UP);
//
//            // Amount with VAT - Imagine
//            BigDecimal amount_vat_imagine = priceSale.multiply(quantity);
//            amount_vat_imagine = amount_vat_imagine.setScale(2,BigDecimal.ROUND_HALF_UP);
//
//            // Amount with VAT - Sale Real
//            BigDecimal amount_vat_total = price_vat_real_sale.multiply(quantity);
//            amount_vat_total = amount_vat_total.setScale(2,BigDecimal.ROUND_HALF_UP);
//            amount_with_vat = amount_vat_total.doubleValue();
//
//            // Amount no VAT
//            BigDecimal amount_no_vat = price_no_vat.multiply(quantity);
//            amount_no_vat = amount_no_vat.setScale(2,BigDecimal.ROUND_HALF_UP);
//
//            // Amount of VAT
//            BigDecimal amount_of_vat = amount_vat_total.subtract(amount_no_vat);
//            amount_of_vat = amount_of_vat.setScale(2,BigDecimal.ROUND_HALF_UP);
//
//            // Amount of Discount
//            BigDecimal amount_of_discount = amount_vat_imagine.subtract(amount_vat_total);
//            amount_of_discount = amount_of_discount.setScale(2,BigDecimal.ROUND_HALF_UP);
//
//
//            // Price Sale
//            invoiceItem.setPriceWithvat(price_vat_real_sale.doubleValue());
//            // Amount no VAT
//            invoiceItem.setVleraPaTvsh(amount_no_vat.doubleValue());
//            // Amount VAT - Real Sale
//            invoiceItem.setVleraTotale(amount_vat_total.doubleValue());
//            // Amount of Discount
//            invoiceItem.setVleraEZbritur(amount_of_discount.doubleValue());
//            // Amount of VAT
//            invoiceItem.setVleraETvsh(amount_of_vat.doubleValue());
//            // Price for fiscal Printer
//            invoiceItem.setCmimiNeArk(priceSale.doubleValue());
//
//
//
//        } catch (Exception e) {
//            System.out.println(" Error :" + e.getMessage());
//        }
//        return amount_with_vat;
//    }
//
//    public double cutTo5(double value) {
//        return Double.parseDouble(String.format("%.5f", value));
//    }
//
//    public double cutTo2(double value) {
//        return Double.parseDouble(String.format("%.2f", value));
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void createPostItem() {
//        InvoicePost invoicePost = new InvoicePost();
//        invoicePost.setId(realmHelper.getAutoIncrementIfForInvoice());
//        invoicePost.setNo_invoice(binding.numriFatures.getText().toString());
//        invoicePost.setPartie_id(client.getId());
//        invoicePost.setPartie_name(client.getName());
//        if (client.getStations().size() > 0) {
//            invoicePost.setPartie_station_id(client.getStations().get(stationPos).getId());
//        } else {
//            invoicePost.setPartie_station_id("0");
//        }
//        invoicePost.setPartie_station_name(binding.njersiaEdittext.getText().toString());
//        invoicePost.setPartie_address(client.getAddress());
//        invoicePost.setPartie_city(client.getCity());
//        invoicePost.setPartie_state_id(client.getState());
//        //add a few
//        invoicePost.setSale_station_id(preferences.getStationId());
//        invoicePost.setInvoice_date(dDate);
//        invoicePost.setDiscount(client.getDiscount());
//        invoicePost.setIs_payed(isPaid);
//        invoicePost.setAmount_no_vat(amount_no_vat.toString());
//        invoicePost.setAmount_of_vat(amount_of_vat.toString());
//        invoicePost.setAmount_discount(amount_of_discount.toString());
//        invoicePost.setAmount_payed(cash);//vlera qe e jep kesh
//        invoicePost.setLocation("42.3, 23.44");
//        invoicePost.setAmount_with_vat(amount_vat.toString());
//        invoicePost.setId_saler(preferences.getUserId());
//        //is bill 0 = fature, 1 = kupon fisakl
//        invoicePost.setIs_bill(isBill);
//        double totalNoDiscount = 0;
//        RealmList<InvoiceItemPost> invoiceItemPosts = new RealmList<>();
//        for (int i = 0; i < stockItems.size(); i++) {
//            totalNoDiscount += (Double.parseDouble(stockItems.get(i).getChildList().get(stockItems.get(i).getSelectedPosition()).getPriceVatSale()) * Double.parseDouble(stockItems.get(i).getSasia()));
//            InvoiceItemPost invoiceItemPost = new InvoiceItemPost();
//            invoiceItemPost.setId(stockItems.get(i).getId());
//            invoiceItemPost.setNo_order(String.valueOf(i));
//            invoiceItemPost.setNo(stockItems.get(i).selectedItemCode);
//            invoiceItemPost.setId_item_group(stockItems.get(i).getId());
//            invoiceItemPost.setId_item(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getId());
//            invoiceItemPost.setName(stockItems.get(i).getName());
//            //invoiceItemPost.setQuantity(stockItems.get(i).getSasia());
//            for(int j =0 ; j < stockItems.get(i).getItems().size(); j++){
//                if(invoiceItemPost.getId_item().equals(stockItems.get(i).getItems().get(j).getId())){
//                    invoiceItemPost.setQuantity((Double.parseDouble(stockItems.get(i).getSasia()) * stockItems.get(i).getItems().get(j).getRelacion())+""); //Kthen quantitetin ne baz te copes
//                    invoiceItemPost.setRelacioni(stockItems.get(i).getItems().get(j).getRelacion()+"");
//                }
//
//            }
//            invoiceItemPost.setUnit(stockItems.get(i).getSelectedUnit());
////            double vleraPaTvsh = stockItems.get(i).getVleraPaTvsh() / Double.parseDouble(stockItems.get(i).getSasia());
//
//            invoiceItemPost.setPrice(String.valueOf(stockItems.get(i).getVleraPaTvsh()));
//            //invoiceItemPost.setDiscount(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getDiscount());
//            invoiceItemPost.setDiscount(stockItems.get(i).getDiscount());
////            invoiceItemPost.setDiscount(discountiZbritur);
//            invoiceItemPost.setVat_id(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatCodeSale());
//            invoiceItemPost.setPrice_vat(String.valueOf(stockItems.get(i).getPriceWithvat()));
//            invoiceItemPost.setAmount_of_discount(String.valueOf(stockItems.get(i).getVleraEZbritur()));
//            invoiceItemPost.setVat_rate(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatRate());
//            invoiceItemPost.setTotalPrice(String.valueOf(stockItems.get(i).getVleraTotale()));
//
//
//            invoiceItemPosts.add(invoiceItemPost);
//
//            System.out.println(invoiceItemPost.getQuantity());
//            // qetu muj me ndreq stokin
//        }
//        invoicePost.setTotal_without_discount(String.valueOf(totalNoDiscount));
//        invoicePost.setItems(invoiceItemPosts);
//        RealmList<InvoicePost> invoicePosts = new RealmList<>();
//        invoicePosts.add(invoicePost);
//        InvoicePostObject invoicePostObject = new InvoicePostObject();
//        invoicePostObject.setToken(preferences.getToken());
//        invoicePostObject.setUser_id(preferences.getUserId());
//        // Send Invoice to API
//        invoicePostObject.setInvoices(invoicePosts);
//
//        InvoicePrintUtil util = new InvoicePrintUtil(invoicePost, binding.web, getApplicationContext(), client, printManager);
//
//        apiService.postFaturat(invoicePostObject)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(responseBody -> {
//                if (responseBody.getSuccess()) {
//                    invoicePost.setSynced(true);
//                    realmHelper.saveInvoices(invoicePost);
//                    Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur dhe sinkronizuar!", Toast.LENGTH_SHORT).show();
//                } else {
//                    invoicePost.setSynced(false);
//                    realmHelper.saveInvoices(invoicePost);
//                    Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
//                }
//            }, throwable -> {
//                invoicePost.setSynced(false);
//                realmHelper.saveInvoices(invoicePost);
//                Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
//            });
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void showCashDialog() {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(org.planetaccounting.saleAgent.R.layout.cash_dialog_layout, null);
//        dialogBuilder.setView(dialogView);
//        Button keshButton = (Button) dialogView.findViewById(org.planetaccounting.saleAgent.R.id.kesh_button);
//        Button pritjeButton = (Button) dialogView.findViewById(org.planetaccounting.saleAgent.R.id.later_button);
//        Button konfirmoButton = (Button) dialogView.findViewById(org.planetaccounting.saleAgent.R.id.ok_button);
//        EditText keshEditText = (EditText) dialogView.findViewById(org.planetaccounting.saleAgent.R.id.shuma_editText);
//        keshEditText.setText(amount_vat.toString());
//        LinearLayout buttonHolder = (LinearLayout) dialogView.findViewById(org.planetaccounting.saleAgent.R.id.button_holder);
//        LinearLayout keshHolder = (LinearLayout) dialogView.findViewById(org.planetaccounting.saleAgent.R.id.kesh_holder);
//        double totali = Double.parseDouble(amount_vat.toString());
//        if (totali > Double.parseDouble(client.getLimitBalance())) {
//            pritjeButton.setEnabled(false);
//            keshEditText.setEnabled(false);
//        } else {
//            pritjeButton.setEnabled(true);
//            keshEditText.setEnabled(true);
//        }
//        keshButton.setOnClickListener(view -> {
//            buttonHolder.setVisibility(View.GONE);
//            keshHolder.setVisibility(View.VISIBLE);
//        });
//        pritjeButton.setOnClickListener(view -> {
//            cash = "0";
//            if (Double.parseDouble(cash) > 0) {
//                isPaid = "1";
//            } else {
//                isPaid = "0";
//            }
//            if (isBill.equalsIgnoreCase("1")) {
//                openPrintingActivity();
//            } else {
//                createPostItem();
//            }
//
//        });
//        konfirmoButton.setOnClickListener(view -> {
//            if (keshEditText.getText().length() > 0) {
//                cash = keshEditText.getText().toString();
//                if (Double.parseDouble(cash) == Double.parseDouble(this.amount_vat.toString())) {
//                    isPaid = "1";
//                } else {
//                    isPaid = "0";
//                }
//                if (isBill.equalsIgnoreCase("1")) {
//                    openPrintingActivity();
//                } else {
//                    createPostItem();
//                }
//            } else {
//                Toast.makeText(getApplicationContext(), "Ju lutem jepni vleren e paguar", Toast.LENGTH_SHORT).show();
//            }
//        });
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (uploadThisMf) {
//            uploadThisMf = false;
//            createPostItem();
//        }
//    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(EventBus.getDefault().isRegistered(this)){
//        }else{
//            EventBus.getDefault().register(this);
//        }
//    }
//    @Subscribe
//    public void onEvent(FinishInvoiceActivity event){
//        out.println("finish ");
//        finish();
//    }
//
//
//
//    // BackUP Code
//    public void addActionInvoice(int numri){
//        discountiZbritur = "0";
//        final InvoiceItem[] invoiceItem = new InvoiceItem[1];
//        invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByGroupID(itemsi.getItems().get(numri).getGroupItem()));
//        InvoiceItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), org.planetaccounting.saleAgent.R.layout.invoice_item, binding.invoiceItemHolder, false);
//        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsName()));
//        itemBinding.emertimiTextview.setOnItemClickListener((adapterView, view, i, l) -> {
//        });
//        if(subColDiscount[numri].equalsIgnoreCase("null")){
//            discountiZbritur = "0";
//        }else{
//            discountiZbritur = subColDiscount[numri];
//        }
//        itemBinding.emertimiTextview.setText(invoiceItem[0].getName());
//        itemBinding.zbritjaTextview.setText(discountiZbritur+"");
//        itemBinding.sasiaTextview.setText(cutTo2(Double.parseDouble(subColQuantity[numri]))+"");
////        itemBinding.njesiaTextview.setText(itemsi.getItems().get(numri).getUnit());
//        pos = pos + numri;
//        try {
//            stockItems.set(pos, invoiceItem[0]);
//        } catch (IndexOutOfBoundsException e) {
//            stockItems.add(pos, invoiceItem[0]);
//        }
//
//        itemBinding.shifraTextview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsCodes()));
//        itemBinding.shifraTextview.setOnItemClickListener((adapterView, view, i, l) -> {
//        });
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(subColDiscount[numri].equalsIgnoreCase("null")){
//                    discountiZbritur = "0";
//                }else{
//                    discountiZbritur = subColDiscount[numri];
//                }
//                invoiceItem[0].setDiscount(discountiZbritur);
//            }
//        },100);
//        findCodeAndPosition(invoiceItem[0]);
//        fillInvoiceItemData(itemBinding, invoiceItem[0]);
//        calculation_total();
//
////        itemBinding.sasiaTextview.setText(itemBinding.sasiaTextview.getText().toString());
//        itemBinding.sasiaTextview.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                itemBinding.sasiaTextview.setText("");
//
//                if (itemBinding.sasiaTextview.getText().length() <= 0) {
//                    invoiceItem[0].setSasia("0");
//                }else{
//                    invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
//                }
//                fillInvoiceItemData(itemBinding, invoiceItem[0]);
//                calculation_total();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        itemBinding.removeButton.setVisibility(View.INVISIBLE);
//        calculation_total();
//        itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
//        binding.invoiceItemHolder.addView(itemBinding.getRoot());
//    }
}
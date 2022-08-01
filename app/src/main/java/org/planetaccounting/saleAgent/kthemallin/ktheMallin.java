package org.planetaccounting.saleAgent.kthemallin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Handler;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.aksionet.ActionData;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityKtheMallinBinding;
import org.planetaccounting.saleAgent.databinding.ReturnItemsBinding;
import org.planetaccounting.saleAgent.events.FinishInvoiceActivity;
import org.planetaccounting.saleAgent.invoice.InvoiceActivity;
import org.planetaccounting.saleAgent.invoice.InvoiceActivityOriginal;
import org.planetaccounting.saleAgent.model.InvoiceItem;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.invoice.InvoiceItemPost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.role.InvoiceRole;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.PlanetLocationManager;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.utils.ReturnPrintUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.planetaccounting.saleAgent.utils.ActivityPrint.DEVICE_SERIAL_NUMBER;
import static org.planetaccounting.saleAgent.utils.ActivityPrint.FISCAL_NUMBER;

public class ktheMallin extends AppCompatActivity {

    private ActivityKtheMallinBinding binding;

    public static final String ACTION_PRINT = "action_print";
    public static final String PRINT_Z_RAPORT = "print_z_raport";
    public static final String ACTION_ADD_ITEMS = "add_items";
    public static final String ACTION = "action";
    public static final String ITEMS = "items";
    public String cash = "0";
    public String isPaid = "0";
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    ArrayList<InvoiceItem> stockItems = new ArrayList<>();
    ArrayList<InvoiceItem> tempStockItems = new ArrayList<>();
    Client client;
    int checked = 0;
    String fDate;
    String dDate;
    String shDate;
    private DatePickerDialog.OnDateSetListener dateSh;
    private Calendar calendar;

    private java.util.Timer timer;
    int pageWidth = 595;
    int pageHeight = 842;
    int stationPos;
    String vleraPaTvsh = "0";
    String vleraZbritur = "0";
    String vleraETvsh = "0";
    String totaliFatures = "0";
    private PrintManager printManager;
    ActionData actionData;
    private InvoiceActivityOriginal.InvoiceState invoiceState = InvoiceActivityOriginal.InvoiceState.FATUR;
    String stationID = "2";

    PlanetLocationManager planetLocationManager;

    private InvoiceRole invoiceRole;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kthe_mallin);
        Kontabiliteti.getKontabilitetiComponent().inject(this);


        invoiceRole = realmHelper.getRole().getInvoice();

        Date cDate = new Date();
        calendar = Calendar.getInstance();

        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
        binding.dataEdittext.setText(fDate);
        binding.numriFatures.setText(preferences.getEmployNumber() + "-" + realmHelper.getAutoIncrementIfForReturn());
        binding.emriKlientit.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getClientsNames()));

        shopDropDownList();

        setRole();

        planetLocationManager = new PlanetLocationManager(this);

        binding.dataL.setOnClickListener(view -> getdata());

        binding.emriKlientit.setOnItemClickListener((adapterView, view, i, l) -> {
            System.out.println("client name " + binding.emriKlientit.getText().toString().substring(0, binding.emriKlientit.getText().toString().indexOf(" nrf:")));
            client = realmHelper.getClientFromName(binding.emriKlientit.getText().toString().substring(0, binding.emriKlientit.getText().toString().indexOf(" nrf:")));
            if (realmHelper.getClientStations(client.getName()).length > 0) {
                binding.njersiaEdittext.setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line, realmHelper.getClientStations(client.getName())));
                binding.njersiaEdittext.setEnabled(true);
                binding.njersiaEdittext.requestFocus();
                binding.njersiaEdittext.showDropDown();
                binding.njersiaEdittext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        stationPos = i;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } else {
                binding.njersiaEdittext.setText("--");
                binding.njersiaEdittext.setEnabled(false);
            }
            binding.zbritjaKlientit.setText("Zbritja e klientit: " + client.getDiscount() + " %");
        });



        binding.shtoTextview.setOnClickListener(view -> {
            if (client != null) {

                addInvoiceItem();

            } else {
                Toast.makeText(getApplicationContext(), "Ju lutem zgjedhni klientin para se ti shtoni artikujt", Toast.LENGTH_SHORT).show();
            }
        });

        binding.fatureButton.setOnClickListener(view -> {
            if (stockItems.size() > 0) {
                returnItems();

            } else {
                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
            }
        });
        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        new Handler().postDelayed(() -> {
            binding.emriKlientit.showDropDown();
            binding.emriKlientit.requestFocus();
        }, 500);
        actionData = realmHelper.getAksionet();

        dateSh = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String f = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                shDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
                binding.dataShip.setText(f);
            }
        };


    }

    //        this part is for to show DropDown when clicked editText for secound time and more ...
    private void shopDropDownList() {
        binding.emriKlientit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.emriKlientit.showDropDown();
                return false;
            }
        });

        binding.njersiaEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.njersiaEdittext.showDropDown();
                return false;
            }
        });

    }

    private void setRole() {


    }

    private void getdata() {

        new DatePickerDialog(this, dateSh, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addInvoiceItem() {
        final InvoiceItem[] invoiceItem = new InvoiceItem[1];

//        final Item[] item = new Item[1];

        ReturnItemsBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.return_items, binding.invoiceItemHolder, false);
        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsName()));
        itemBinding.emertimiTextview.setOnItemClickListener((adapterView, view, i, l) -> {
            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString()));

            itemBinding.sasiaTextview.setText("1");
            invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
            int pos = (int) itemBinding.getRoot().getTag();
            findCodeAndPosition(invoiceItem[0]);

            hideLoader();
            try {
                stockItems.set(pos, invoiceItem[0]);
            } catch (IndexOutOfBoundsException e) {
                stockItems.add(pos, invoiceItem[0]);
            }
            itemBinding.sasiaTextview.requestFocus();
            fillInvoiceItemData(itemBinding, invoiceItem[0]);

            calculateTotal();
            calculateVleraPaTvsh();
            calculateVleraETVSH();

        });
        itemBinding.emertimiTextview.showDropDown();
        itemBinding.emertimiTextview.requestFocus();
        itemBinding.emertimiTextview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                itemBinding.emertimiTextview.showDropDown();
                return false;
            }
        });
        itemBinding.shifraTextview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsCodes()));
        itemBinding.shifraTextview.setOnItemClickListener((adapterView, view, i, l) -> {
            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByCode(itemBinding.shifraTextview.getText().toString()));
            itemBinding.sasiaTextview.requestFocus();
            findCodeAndPosition(invoiceItem[0]);
            fillInvoiceItemData(itemBinding, invoiceItem[0]);
        });
        itemBinding.njesiaTextview.setOnClickListener(view -> dialog(invoiceItem[0], itemBinding));

        itemBinding.sasiaTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (itemBinding.sasiaTextview.getText().length() == 0) {
                    invoiceItem[0].setSasia("0");
                } else {
                    invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
                }

                fillInvoiceItemData(itemBinding, invoiceItem[0]);

                calculateTotal();
                calculateVleraPaTvsh();
                calculateVleraETVSH();

                    }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        itemBinding.removeButton.setOnClickListener(view ->
        {
            int pos = (int) itemBinding.getRoot().getTag();
            if (stockItems.size() > 0) {
                try {
                    stockItems.remove(pos);
                } catch (Exception e) {

                }
            }
            binding.invoiceItemHolder.removeView(itemBinding.getRoot());
            calculateTotal();
            calculateVleraPaTvsh();
            calculateVleraETVSH();
        });
        itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
        binding.invoiceItemHolder.addView(itemBinding.getRoot());
    }


    private void fillInvoiceItemData(ReturnItemsBinding itemBinding, InvoiceItem invoiceItem) {
        itemBinding.emertimiTextview.setText(invoiceItem.getName());
        itemBinding.shifraTextview.setText(invoiceItem.getSelectedItemCode());
        itemBinding.njesiaTextview.setText(invoiceItem.getSelectedUnit());

        List<Double> amontAndPrice = calculateValueOfItem(invoiceItem);
        itemBinding.vlera.setText("" + String.format(Locale.ENGLISH,"%.3f", new BigDecimal(amontAndPrice.get(0))));
        itemBinding.cmimiTvsh.setText("" + String.format(Locale.ENGLISH,"%.3f", new BigDecimal(amontAndPrice.get(1))));
        itemBinding.basePrice.setText(invoiceItem.getBasePrice() + "");
    }


    private void findCodeAndPosition(InvoiceItem invoiceItem) {
        for (int i = 0; i < invoiceItem.getItems().size(); i++) {
            if (invoiceItem.getDefaultUnit().equalsIgnoreCase(invoiceItem.getItems().get(i).getUnit())) {
                checked = i;
                invoiceItem.setId(invoiceItem.getId());
                invoiceItem.setDiscount(invoiceItem.getItems().get(i).getDiscount());
                invoiceItem.setSelectedItemCode(invoiceItem.getItems().get(i).getNumber());
                invoiceItem.setSelectedUnit(invoiceItem.getItems().get(i).getUnit());
                invoiceItem.setSelectedPosition(checked);
                System.out.println("sasia11 " + String.valueOf(invoiceItem.getItems().get(i).getRelacion()));
                invoiceItem.setRelacion(String.valueOf(invoiceItem.getItems().get(i).getRelacion()));
                invoiceItem.setBaseDiscount(invoiceItem.getItems().get(i).getDiscount());
                invoiceItem.setBarcode(invoiceItem.getItems().get(i).getBarcode());
                invoiceItem.setRelacion(String.valueOf(invoiceItem.getItems().get(i).getRelacion()));

            }
        }
    }

    void dialog(InvoiceItem invoiceItem, ReturnItemsBinding binding) {
        try {
            String[] units = realmHelper.getItemUnits(invoiceItem.getName());
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(ktheMallin.this);
            alt_bld.setSingleChoiceItems(units, checked, (dialog, item) -> {
                System.out.println();
                checked = item;
                invoiceItem.setSelectedItemCode(invoiceItem.getItems().get(checked).getNumber());
                invoiceItem.setSelectedUnit(invoiceItem.getItems().get(checked).getUnit());
                invoiceItem.setSelectedPosition(checked);
                invoiceItem.setRelacion(String.valueOf(invoiceItem.getItems().get(checked).getRelacion()));
                hideLoader();
                fillInvoiceItemData(binding, invoiceItem);
                calculateTotal();
                calculateVleraPaTvsh();
                calculateVleraETVSH();

                dialog.dismiss();
            });
            AlertDialog alert = alt_bld.create();
            alert.show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ju lutem zgjedhni produktin", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Double> calculateValueOfItem(InvoiceItem invoiceItem) {
        double amount_with_vat = 0.000;
        BigDecimal priceSale =BigDecimal.valueOf(amount_with_vat);
        try {
            // Quantity
            BigDecimal quantity = new BigDecimal(invoiceItem.getSasia());

            // Price Sale (with VAT) untouchable
             priceSale = new BigDecimal(invoiceItem.getChildList().get(invoiceItem.getSelectedPosition()).getPriceVatSale());


            // VAT Rate
            double vat_rate = 0;
            vat_rate = Double.parseDouble(invoiceItem.getChildList().get(invoiceItem.getSelectedPosition()).getVatRate()) * 0.01;
            BigDecimal vatRate = new BigDecimal(vat_rate);
            vatRate = vatRate.setScale(5, BigDecimal.ROUND_HALF_UP);

            // Client Discount
            double client_discount = Double.parseDouble(client.getDiscount()) * 0.01;
//            BigDecimal clientDiscount = new BigDecimal(Double.toString(client_discount));
            if (invoiceItem.getType().equalsIgnoreCase("action")) {
                client_discount = 0;
            }


            // Article Discount
            double article_discount = Double.parseDouble(invoiceItem.getDiscount()) * 0.01;
            BigDecimal articleDiscount = new BigDecimal(article_discount);


            // Price no VAT with Discount, Price Base, Price no VAT, Price Real Sale
            BigDecimal vat_rate_for_divide = vatRate.add(new BigDecimal(1));

            BigDecimal price_base = priceSale.divide(vat_rate_for_divide, 5, BigDecimal.ROUND_HALF_UP);

            // Price no VAT
            invoiceItem.setVleraPaTvsh(price_base.doubleValue());
            invoiceItem.setBasePrice(price_base.doubleValue());


            BigDecimal article_discount_for_divide = new BigDecimal(article_discount);
            BigDecimal price_no_vat_no_discount_article_discount = price_base.multiply(article_discount_for_divide);
            BigDecimal price_no_vat_no_discount_article = price_base.subtract(price_no_vat_no_discount_article_discount);

            BigDecimal client_discount_for_divide = new BigDecimal(client_discount);
            BigDecimal client_discount_for_divide_discount = price_no_vat_no_discount_article.multiply(client_discount_for_divide);
            BigDecimal price_no_vat = price_no_vat_no_discount_article.subtract(client_discount_for_divide_discount);


            if (article_discount == 1 || client_discount == 1) {
                price_no_vat = price_no_vat.subtract(price_no_vat);
            }


            BigDecimal price_vat_real_sale = price_no_vat.multiply(vat_rate_for_divide);
            price_vat_real_sale = price_vat_real_sale.setScale(5, BigDecimal.ROUND_HALF_UP);

            // Amount with VAT - Imagine
            BigDecimal amount_vat_imagine = priceSale.multiply(quantity);
            amount_vat_imagine = amount_vat_imagine.setScale(2, BigDecimal.ROUND_HALF_UP);

            // Amount with VAT - Sale Real
            BigDecimal amount_vat_total = price_vat_real_sale.multiply(quantity);
            amount_vat_total = amount_vat_total.setScale(2, BigDecimal.ROUND_HALF_UP);
            amount_with_vat = amount_vat_total.doubleValue();

            // Amount no VAT
            BigDecimal amount_no_vat = price_no_vat.multiply(quantity);
            amount_no_vat = amount_no_vat.setScale(2, BigDecimal.ROUND_HALF_UP);

            // Amount of VAT
            BigDecimal amount_of_vat = amount_vat_total.subtract(amount_no_vat);
            amount_of_vat = amount_of_vat.setScale(2, BigDecimal.ROUND_HALF_UP);

            // Amount of Discount
            BigDecimal amount_of_discount = amount_vat_imagine.subtract(amount_vat_total);
            amount_of_discount = amount_of_discount.setScale(2, BigDecimal.ROUND_HALF_UP);


            // Price Sale
            price_vat_real_sale = price_vat_real_sale.setScale(2, BigDecimal.ROUND_HALF_UP);
            invoiceItem.setPriceWithvat(price_vat_real_sale.doubleValue());
            // Amount no VAT
            invoiceItem.setVleraPaTvsh(amount_no_vat.doubleValue());
            // Amount VAT - Real Sale
            amount_vat_total = amount_vat_total.setScale(2, BigDecimal.ROUND_HALF_UP);
            invoiceItem.setVleraTotale(amount_vat_total.doubleValue());
            // Amount of Discount
//            invoiceItem.setVleraEZbritur(amount_of_discount.doubleValue());
            // Amount of VAT
            invoiceItem.setVleraETvsh(amount_of_vat.doubleValue());
            // Price for fiscal Printer
            invoiceItem.setCmimiNeArk(priceSale.doubleValue());





        } catch (Exception e) {
            System.out.println(" Error :" + e.getMessage());

        }
//        DecimalFormat dtime = new DecimalFormat("#.###");
//
//        return Double.valueOf(dtime.format(amount_with_vat)) ;

        List<Double> finalValues = new ArrayList<>();

        finalValues.add(amount_with_vat);
        finalValues.add(priceSale.doubleValue());
        return  finalValues;
    }

    public void calculateVleraPaTvsh() {
        double vleraPaTvsh = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            vleraPaTvsh += stockItems.get(i).getVleraPaTvsh();
        }
        this.vleraPaTvsh = String.valueOf(cutTo2(vleraPaTvsh));
        binding.vleraPaTvsh.setText("Vlera pa TVSH: " + cutTo2(vleraPaTvsh));
    }



    public void calculateVleraETVSH() {
        double vleraETvsh = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            vleraETvsh += stockItems.get(i).getVleraETvsh();
        }
        this.vleraETvsh = String.valueOf(cutTo2(vleraETvsh));
        binding.vleraETvsh.setText("Vlera e TVSH-së: " + cutTo2(vleraETvsh));
    }

    public void calculateTotal() {
        double total = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            total += stockItems.get(i).getVleraTotale();
        }
        this.totaliFatures = String.valueOf(cutTo2(total));
        binding.vleraTotale.setText("Vlera Totale: " + cutTo2(total));
    }

    public double cutTo5(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.5f", value));
    }

    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", value));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void returnItems() {
        InvoicePost invoicePost = new InvoicePost();
        invoicePost.setId(realmHelper.getAutoIncrementIfForReturn());
        invoicePost.setType("ret");
        invoicePost.setFiscal_number(FISCAL_NUMBER);
        invoicePost.setDevice_serial_number(DEVICE_SERIAL_NUMBER);
        invoicePost.setNo_invoice(binding.numriFatures.getText().toString());
        invoicePost.setPartie_id(client.getId());
        invoicePost.setPartie_name(client.getName());
        if (client.getStations().size() > 0) {
            invoicePost.setPartie_station_id(client.getStations().get(stationPos).getId());
        } else {
            invoicePost.setPartie_station_id("0");
        }
        invoicePost.setPartie_station_name(binding.njersiaEdittext.getText().toString());
        invoicePost.setPartie_address(client.getAddress());
        invoicePost.setPartie_city(client.getCity());
        invoicePost.setPartie_state_id(client.getState());
        //add a few
        invoicePost.setSale_station_id(preferences.getStationId());
        invoicePost.setInvoice_date(dDate);
        invoicePost.setDiscount(client.getDiscount());
        invoicePost.setIs_payed(isPaid);
        invoicePost.setAmount_no_vat(vleraPaTvsh);
        invoicePost.setAmount_of_vat(vleraETvsh);
        invoicePost.setAmount_discount(vleraZbritur);
        invoicePost.setAmount_payed(cash);//vlera qe e jep kesh
        invoicePost.setLocation(planetLocationManager.getLatitude() + "," + planetLocationManager.getLongitude());
        invoicePost.setAmount_with_vat(totaliFatures);
        invoicePost.setId_saler(preferences.getUserId());
        //is bill 0 = fature, 1 = kupon
        invoicePost.setIs_bill(InvoiceActivity.isBill);
        double totalNoDiscount = 0;
        RealmList<InvoiceItemPost> invoiceItemPosts = new RealmList<>();
        for (int i = 0; i < stockItems.size(); i++) {
            totalNoDiscount += (Double.parseDouble(stockItems.get(i).getChildList().get(stockItems.get(i).getSelectedPosition()).getPriceVatSale()) * Double.parseDouble(stockItems.get(i).getSasia()));
            InvoiceItemPost invoiceItemPost = new InvoiceItemPost();
            invoiceItemPost.setId(stockItems.get(i).getId());

            invoiceItemPost.setAmount_no_vat(String.valueOf( stockItems.get(i).getVleraPaTvsh()));
            invoiceItemPost.setNo_order(String.valueOf(i));
            invoiceItemPost.setNo(stockItems.get(i).selectedItemCode);
            invoiceItemPost.setId_item_group(stockItems.get(i).getId());
            invoiceItemPost.setId_item(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getId());
            invoiceItemPost.setName(stockItems.get(i).getName());
            invoiceItemPost.setQuantity(stockItems.get(i).getSasia());
            invoiceItemPost.setUnit(stockItems.get(i).getSelectedUnit());
            double vleraPaTvsh = stockItems.get(i).getVleraPaTvsh() / Double.parseDouble(stockItems.get(i).getSasia());
            BigDecimal vleraNoTvsh = new BigDecimal(vleraPaTvsh);
            vleraNoTvsh = vleraNoTvsh.setScale(3, BigDecimal.ROUND_UP);
            invoiceItemPost.setPrice(String.valueOf(vleraNoTvsh));
            System.out.println("zbritja " + stockItems.get(i).getDiscount());
            invoiceItemPost.setDiscount(stockItems.get(i).getDiscount());
            invoiceItemPost.setVat_id(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatCodeSale());
            invoiceItemPost.setPrice_base(stockItems.get(i).getBasePrice() + "");
            invoiceItemPost.setPrice_vat(String.valueOf(stockItems.get(i).getPriceWithvat()));
//            invoiceItemPost.setAmount_of_discount(String.valueOf(stockItems.get(i).getVleraEZbritur()));
            invoiceItemPost.setVat_rate(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatRate());
            invoiceItemPost.setTotalPrice(String.valueOf(stockItems.get(i).getVleraTotale()));
            invoiceItemPost.setRelacioni(stockItems.get(i).getRelacion());
            invoiceItemPost.setBarcode(stockItems.get(i).getBarcode());
            invoiceItemPost.setAction(stockItems.get(i).isAction());
            invoiceItemPost.setCollection(stockItems.get(i).isCollection());
            invoiceItemPosts.add(invoiceItemPost);
        }
        invoicePost.setTotal_without_discount(String.valueOf(totalNoDiscount));
        invoicePost.setItems(invoiceItemPosts);
        RealmList<InvoicePost> invoicePosts = new RealmList<>();
        invoicePosts.add(invoicePost);
        ReturnPostObject returnPostObject = new ReturnPostObject();
        returnPostObject.setToken(preferences.getToken());
        returnPostObject.setUser_id(preferences.getUserId());
        returnPostObject.setRetrunPost(invoicePosts);

        ReturnPrintUtil util = new ReturnPrintUtil(invoicePost, binding.web, this, client, printManager);

        apiService.postReturn(returnPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (responseBody.getSuccess()) {
                        invoicePost.setSynced(true);
                        Toast.makeText(getApplicationContext(), "Kthimi i mallit eshte ruajtur dhe sinkronizuar!", Toast.LENGTH_SHORT).show();
                    } else {
                        invoicePost.setSynced(false);
                        Toast.makeText(getApplicationContext(), "Kthimi i mallit eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
                    }
                    realmHelper.returnInvoice(invoicePost);
                }, throwable -> {
                    throwable.printStackTrace();
                    invoicePost.setSynced(false);
                    realmHelper.returnInvoice(invoicePost);
                    Toast.makeText(getApplicationContext(), "Kthimi i mallit eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
                });


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if (InvoiceActivity.uploadThisMf) {
            InvoiceActivity.uploadThisMf = false;
            returnItems();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {

        }
    }

    @Subscribe
    public void onEvent(FinishInvoiceActivity event) {
        finish();
    }

    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
        binding.loader.bringToFront();
        binding.loader.setOnClickListener(view -> {
        });
    }

    private void hideLoader() {
        binding.loader.setVisibility(View.GONE);
    }

}

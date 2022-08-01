package org.planetaccounting.saleAgent.invoice;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.aksionet.ActionArticleItems;
import org.planetaccounting.saleAgent.aksionet.ActionBrandItem;
import org.planetaccounting.saleAgent.aksionet.ActionCategoryItem;
import org.planetaccounting.saleAgent.aksionet.ActionData;
import org.planetaccounting.saleAgent.aksionet.ActionSteps;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActionInvItemBinding;
import org.planetaccounting.saleAgent.databinding.InvoiceActivityBinding;
import org.planetaccounting.saleAgent.databinding.InvoiceItemBinding;
import org.planetaccounting.saleAgent.escpostprint.EscPostPrintFragment;
import org.planetaccounting.saleAgent.events.FinishInvoiceActivity;
import org.planetaccounting.saleAgent.model.InvoiceItem;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.invoice.InvoiceItemPost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePostObject;
import org.planetaccounting.saleAgent.model.invoice.OrderPostObject;
import org.planetaccounting.saleAgent.model.order.CheckQuantity;
import org.planetaccounting.saleAgent.model.role.InvoiceRole;
import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.model.stock.SubItem;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.ActivityPrint;
import org.planetaccounting.saleAgent.utils.InvoicePrintUtil;
import org.planetaccounting.saleAgent.utils.PlanetLocationManager;
import org.planetaccounting.saleAgent.utils.Preferences;

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
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.planetaccounting.saleAgent.utils.ActivityPrint.DEVICE_SERIAL_NUMBER;
import static org.planetaccounting.saleAgent.utils.ActivityPrint.FISCAL_NUMBER;

/**
 * Created by tahirietrit on 27/12/17.
 */



public class InvoiceActivityOriginal extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    public enum InvoiceState{
        FATUR,POROSI
    }

    public static final String ACTION_PRINT = "action_print";
    public static final String PRINT_Z_RAPORT = "print_z_raport";
    public static final String ACTION_ADD_ITEMS = "add_items";
    public static final String ACTION = "action";
    public static final String ITEMS = "items";
    public String cash = "0";
    public String isPaid = "0";
    InvoiceActivityBinding binding;
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

    private java.util.Timer timer ;

    int pageWidth = 595;
    int pageHeight = 842;
    int stationPos;
    String vleraPaTvsh = "0";
    String vleraZbritur = "0";
    String vleraETvsh = "0";
    String totaliFatures = "0";
    private PrintManager printManager;
    ActionData actionData;
    private InvoiceState  invoiceState = InvoiceState.FATUR;
    String stationID = "2";

    PlanetLocationManager planetLocationManager;

    private InvoiceRole invoiceRole;

    String[] stocksName ;

    boolean isPrint = true;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        binding = DataBindingUtil.setContentView(this, R.layout.invoice_activity);

        Kontabiliteti.getKontabilitetiComponent().inject(this);
        checkIfHaveData();

        getStocksName();

        invoiceRole = realmHelper.getRole().getInvoice();

        Date cDate = new Date();
        calendar = Calendar.getInstance();

        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
        binding.dataEdittext.setText(fDate);
        binding.numriFatures.setText(preferences.getEmployNumber() + "-" + realmHelper.getAutoIncrementIfForInvoice());
        binding.emriKlientit.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getClientsNames()));

        shopDropDownList();

        setRole();

        planetLocationManager = new PlanetLocationManager(this);

        binding.segmentGroup1.setOnCheckedChangeListener(this);

        binding.dataL.setOnClickListener(view -> getdata());

        // Client Change
        binding.emriKlientit.setOnItemClickListener((adapterView, view, i, l) -> {
            System.out.println("client name "+ binding.emriKlientit.getText().toString().substring(0, binding.emriKlientit.getText().toString().indexOf(" nrf:")));
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
        // Add item on form
        binding.shtoTextview.setOnClickListener(view -> {
            if (client != null) {
                if (invoiceState == InvoiceState.POROSI){
                    checkedQuantity();
                } else {
                    addInvoiceItem();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Ju lutem zgjedhni klientin para se ti shtoni artikujt", Toast.LENGTH_SHORT).show();
            }
        });
        // Print Invoice
        binding.fatureButton.setOnClickListener(view -> {
            InvoiceActivity.isBill = "0";
            if (stockItems.size() > 0) {
                if (checkSasia()) {
                    showCashDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Një ose më shum artikuj kan sasine zero", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
            }
        });
        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        // Print Bill
        binding.kuponButton.setOnClickListener(view ->
        {
            InvoiceActivity.isBill = "1";
            if (stockItems.size() > 0) {
                if (checkSasia()) {
                    showCashDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Një ose më shum artikuj kan sasine zero", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
            }
        });
        // Send Order
        binding.porositButton.setOnClickListener(v ->
        {

            if (stockItems.size() > 0) {
                if (checkSasia()) {
                    showBillDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Një ose më shum artikuj kan sasine zero", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
            }
            }  );
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

                String  f = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                shDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
                binding.dataShip.setText(f);
            }
        };
    }

    private void setRole(){

//        if (invoiceRole.getForm().getDate().getShow()==0){
//            binding.dateContent.setVisibility(View.GONE);
//        }
//
//        if (invoiceRole.getForm().getInvoice_number().getShow()==0){
//            binding.invoiceNumberContent.setVisibility(View.GONE);
//        }
//
//        if (invoiceRole.getForm().getClient().getShow()==0){
//            binding.clientContent.setVisibility(View.GONE);
//        }
//
//        if (invoiceRole.getForm().getClient_station().getShow()==0){
//            binding.njersiaEdittext.setVisibility(View.GONE);
//            binding.njesiaTextview.setVisibility(View.GONE);
//
//        }
//
//        if (invoiceRole.getForm().getCreate_invoice().getShow()==0){
//            binding.fatureButton.setVisibility(View.GONE);
//            }
//
//        if (invoiceRole.getForm().getCreate_bill().getShow()==0){
//            binding.kuponButton.setVisibility(View.GONE);
//
//        }

    }

    private void setArticleRole(InvoiceItemBinding itemBinding){
//        if (invoiceRole.getForm().getArticle().getShow()==0){
//            itemBinding.emertimiTextview.setVisibility(View.GONE);
//        }
//
//        if (invoiceRole.getForm().getArticle_number().getShow()==0){
//            itemBinding.shifraTextview.setVisibility(View.GONE);
//        }
//
//        if (invoiceRole.getForm().getArticle_unit().getShow()==0){
//            itemBinding.njesiaTextview.setVisibility(View.GONE);
//        }
//
//        if (invoiceRole.getForm().getArticle_discount().getShow()==0){
//            itemBinding.zbritjaTextview.setVisibility(View.GONE);
//
//            if (invoiceRole.getForm().getArticle_discount().getEditable()==1){
//                itemBinding.zbritjaTextview.setEnabled(true);
//            }
//        }
    }

    private void getStocksName(){
        if (invoiceState == InvoiceState.FATUR) {
            stocksName = realmHelper.getStockItemsName(true);
            stocksName = removeNullElements(stocksName);
        } else  {
            stocksName = realmHelper.getStockItemsName();
        }
    }

    private void openPrintingActivity() {
        Intent i = new Intent(getApplicationContext(), ActivityPrint.class);
        i.putExtra(ACTION, ACTION_PRINT);
        i.putParcelableArrayListExtra(ITEMS, stockItems);
        startActivity(i);

    }

    // Form Mode | Invoce, Order
    private void checkIfHaveData(){

        switch (preferences.getLastCheck()){
            case 0:
                invoiceState = InvoiceState.FATUR;
                binding.faturRadio.setChecked(true);
                binding.porositButton.setVisibility(View.GONE);
                binding.dataL.setVisibility(View.GONE);
                binding.fatureButton.setVisibility(View.VISIBLE);
                binding.kuponButton.setVisibility(View.VISIBLE);
                preferences.saveLastCheck(-1);
                break;
            case 1:
                invoiceState = InvoiceState.POROSI;
                binding.porosiaRadio.setChecked(true);
                binding.porositButton.setVisibility(View.VISIBLE);
                binding.dataL.setVisibility(View.VISIBLE);

                binding.fatureButton.setVisibility(View.GONE);
                binding.kuponButton.setVisibility(View.GONE);
                preferences.saveLastCheck(-1);

                break;

                default:
                    preferences.saveLastCheck(-1);
                    break;
                    }
    }


    private void updateProductQuantity(){
        if (timer!=null){
            timer.cancel();
        }
    }

    // Show Date Picker
    private void getdata(){
        new DatePickerDialog(this, dateSh, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Check Quantity for all items
    public boolean checkSasia() {

        if (invoiceState == InvoiceState.FATUR) {

            ArrayList<InvoiceItem> sameItem = new ArrayList<>();

            for (int i = 0; i < stockItems.size(); i++) {

                if (sameItem.isEmpty()){

                    final InvoiceItem[] invoiceItemt = new InvoiceItem[1];
                    invoiceItemt[0]  = new InvoiceItem(stockItems.get(i));
                    invoiceItemt[0].setSasia(stockItems.get(i).getSasia());
                    sameItem.add(invoiceItemt[0]);

                } else  {

                    boolean isSame = false;
                    for (int j = 0; j < sameItem.size(); j++) {

                        String samename = sameItem.get(j).getName();
                        String stockname = stockItems.get(i).getName();

                        if (samename.equals(stockname)) {
                            final InvoiceItem[] invoiceItemt = new InvoiceItem[1];
                            invoiceItemt[0]  = new InvoiceItem(sameItem.get(j));
                            double sasia = Double.parseDouble(stockItems.get(i).getSasia()) + Double.parseDouble(sameItem.get(j).getSasia());
                            invoiceItemt[0].setSasia(String.valueOf(sasia));
                            sameItem.set(j, invoiceItemt[0]);
                            isSame = true;
                        }
                    }

                    if (!isSame){
                        final InvoiceItem[] invoiceItemt = new InvoiceItem[1];
                        invoiceItemt[0]  = new InvoiceItem(stockItems.get(i));
                        invoiceItemt[0].setSasia(stockItems.get(i).getSasia());
                        sameItem.add(invoiceItemt[0]);
                    }
                }

            }

            for (int i = 0; i < sameItem.size(); i++) {
                final InvoiceItem[] invoiceItemt = new InvoiceItem[1];
                invoiceItemt[0] =  new InvoiceItem(sameItem.get(i));
                double sasia = Double.parseDouble(sameItem.get(i).getSasia());
                double availableQuantity = 0;
                availableQuantity = Double.parseDouble(invoiceItemt[0].getQuantity()) / invoiceItemt[0].getItems().get(invoiceItemt[0].getSelectedPosition()).getRelacion();

                if (!(sasia <= availableQuantity && sasia > 0f)) {
                    Toast.makeText(getApplicationContext(), sameItem.get(i).getName() + " Nuk ka stok te mjaftueshem ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        }


        for (int i = 0; i < stockItems.size(); i++) {
            double sasia;
            try {
                sasia = Double.parseDouble(stockItems.get(i).getSasia());
            } catch (Exception e) {
                sasia = Double.parseDouble(stockItems.get(i).getSasia());
            }
            if (sasia == 0) {
                return false;
            }
        }
        return true;
    }

    private void addInvoiceItem() {
        final InvoiceItem[] invoiceItem = new InvoiceItem[1];

//        final Item[] item = new Item[1];
        InvoiceItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.invoice_item, binding.invoiceItemHolder, false);
        // Set Name


        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, stocksName));
        setArticleRole(itemBinding);
        // Change Item Name - Event
        itemBinding.emertimiTextview.setOnItemClickListener((adapterView, view, i, l) -> {
            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString()));
            itemBinding.sasiaTextview.setText("0");
            invoiceItem[0].setSasia("0");
            int pos = (int) itemBinding.getRoot().getTag();
            findCodeAndPosition(invoiceItem[0]);

            // Action with Collection
            if (invoiceItem[0].getType().equalsIgnoreCase("action")) {
                binding.invoiceItemHolder.removeView(itemBinding.getRoot());
                tempStockItems.clear();
                for (int i1 = 0; i1 < invoiceItem[0].getItems().size(); i1++) {
                    tempStockItems.add(new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString())));
                    tempStockItems.get(i1).setGroupItem(invoiceItem[0].getItems().get(i1).getGroupItem());

                }
                addActionViews(tempStockItems, 1);

            } else {
                stockItems.add(pos, invoiceItem[0]);
                checkIfArticleIsInAction(invoiceItem[0]);
                fillInvoiceItemData(itemBinding, invoiceItem[0]);
                itemBinding.sasiaTextview.requestFocus();
                itemBinding.sasiaTextview.setSelection(itemBinding.sasiaTextview.getText().length());
                calculcation_form();

            }
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
            itemBinding.sasiaTextview.setText("0");
            invoiceItem[0].setSasia("0");
            int pos = (int) itemBinding.getRoot().getTag();
            findCodeAndPosition(invoiceItem[0]);

            // Action with Collection
            if (invoiceItem[0].getType().equalsIgnoreCase("action")) {
                binding.invoiceItemHolder.removeView(itemBinding.getRoot());
                tempStockItems.clear();
                for (int i1 = 0; i1 < invoiceItem[0].getItems().size(); i1++) {
                    tempStockItems.add(new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString())));
                    tempStockItems.get(i1).setGroupItem(invoiceItem[0].getItems().get(i1).getGroupItem());

                }
                addActionViews(tempStockItems, 1);

            } else {
                stockItems.add(pos, invoiceItem[0]);
                checkIfArticleIsInAction(invoiceItem[0]);
                fillInvoiceItemData(itemBinding, invoiceItem[0]);
                itemBinding.sasiaTextview.requestFocus();
                itemBinding.sasiaTextview.setSelection(itemBinding.sasiaTextview.getText().length());
                calculcation_form();

            }
            });
        itemBinding.njesiaTextview.setOnClickListener(view -> dialog(invoiceItem[0], itemBinding));

        itemBinding.sasiaTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if ( invoiceState == InvoiceState.FATUR) {

                        double sasia = 0;
                        if (itemBinding.sasiaTextview.getText().length() > 0) {
                            sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
                        }
                        double availableQuantity = 0;
                         availableQuantity = Double.parseDouble(invoiceItem[0].getQuantity()) / invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getRelacion();

                        if (sasia <= availableQuantity && sasia > 0f) {
                            if (itemBinding.sasiaTextview.getText().length() == 0) {
                                invoiceItem[0].setSasia("0");
                            } else {
                                invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
                                if (invoiceItem[0].isAction() && sasia >= invoiceItem[0].getMinQuantityForDiscount()) {
                                    invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
                                    double totalDiscount = Double.parseDouble(invoiceItem[0].getDiscount())
                                            + Double.parseDouble(invoiceItem[0].getExtraDiscount());
                                    invoiceItem[0].setDiscount(String.valueOf(totalDiscount));
                                } else {
                                    invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
                                }
                            }
                            checkIfArticleIsInAction(invoiceItem[0]);
                            fillInvoiceItemData(itemBinding, invoiceItem[0]);
                            calculcation_form();

                            calculateTotal();
                            calculateVleraPaTvsh();
                            calculateVleraEZbritur();
                            calculateVleraETVSH();
                        }else {
                            invoiceItem[0].setSasia("0");
                            if (!itemBinding.sasiaTextview.getText().toString().equals("0") && !itemBinding.sasiaTextview.getText().toString().equals("") && !itemBinding.sasiaTextview.getText().toString().isEmpty()){
                                Toast.makeText(getApplicationContext(),"Nuk keni stok te mjaftueshem ", Toast.LENGTH_SHORT).show();
                                }
                        }

                    }

            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });

        itemBinding.sasiaTextview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus){
                    if (invoiceState == InvoiceState.POROSI) {

                        binding.loader.setVisibility(View.VISIBLE);

                        String  stockItemId = invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getId();
                        double  sasia1 = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());


                        CheckQuantity checkQuantity = new CheckQuantity(preferences.getUserId(),preferences.getToken(),sasia1 + "",preferences.getStationId(),dDate,stockItemId);

                        if (sasia1>0f){
                            apiService.checkQuantity(checkQuantity).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(invoiceUploadResponse -> {
                                        binding.loader.setVisibility(View.GONE);

                                        if (!invoiceUploadResponse.getSuccess()) {
                                            Toast.makeText(getApplicationContext(), invoiceUploadResponse.getError().getText(), Toast.LENGTH_SHORT).show();

                                        } else {

                                            double  sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());


                                            if (itemBinding.sasiaTextview.getText().length() > 0) {
                                                sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
                                            }
                                            double availableQuantity = 0;

                                            if (itemBinding.sasiaTextview.getText().length() == 0) {
                                                invoiceItem[0].setSasia("0");
                                            } else {
                                                invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
                                                if (invoiceItem[0].isAction() && sasia >= invoiceItem[0].getMinQuantityForDiscount()) {
                                                    invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
                                                    double totalDiscount = Double.parseDouble(invoiceItem[0].getDiscount())
                                                            + Double.parseDouble(invoiceItem[0].getExtraDiscount());
                                                    invoiceItem[0].setDiscount(String.valueOf(totalDiscount));
                                                } else {
                                                    invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
                                                }
                                            }
                                            checkIfArticleIsInAction(invoiceItem[0]);
                                            fillInvoiceItemData(itemBinding, invoiceItem[0]);

                                           calculcation_form();

                                        }
                                        binding.loader.setVisibility(View.GONE);
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            binding.loader.setVisibility(View.GONE);

                                            throwable.printStackTrace();
                                        }
                                    });

                        }else {
                            Toast.makeText(getApplicationContext(), "Jipeni sasin", Toast.LENGTH_SHORT).show();
                        }

                    }

                    if ( invoiceState == InvoiceState.FATUR) {

                        double sasia = 0;
                        if (itemBinding.sasiaTextview.getText().length() > 0) {
                            sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
                        }
                        double availableQuantity = 0;
                        availableQuantity = Double.parseDouble(invoiceItem[0].getQuantity()) / invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getRelacion();

                        if (sasia <= availableQuantity && sasia > 0f) {
                            if (itemBinding.sasiaTextview.getText().length() == 0) {
                                invoiceItem[0].setSasia("0");
                            } else {
                                invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
                                if (invoiceItem[0].isAction() && sasia >= invoiceItem[0].getMinQuantityForDiscount()) {
                                    invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
                                    double totalDiscount = Double.parseDouble(invoiceItem[0].getDiscount())
                                            + Double.parseDouble(invoiceItem[0].getExtraDiscount());
                                    invoiceItem[0].setDiscount(String.valueOf(totalDiscount));
                                } else {
                                    invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
                                }
                            }
                            checkIfArticleIsInAction(invoiceItem[0]);
                            fillInvoiceItemData(itemBinding, invoiceItem[0]);
                            calculcation_form();

                            calculateTotal();
                            calculateVleraPaTvsh();
                            calculateVleraEZbritur();
                            calculateVleraETVSH();
                        }else {
                            invoiceItem[0].setSasia("0");
                            itemBinding.sasiaTextview.setText("0");
                        }

                    }
                }
            }
        });


        itemBinding.zbritjaTextview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus){
                    if (itemBinding.zbritjaTextview.getText().toString().trim().isEmpty()){
                        itemBinding.zbritjaTextview.setText("0");
                        invoiceItem[0].setDiscount(itemBinding.zbritjaTextview.getText().toString(),true);

                        }
                }

            }
        });

        itemBinding.zbritjaTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if(!itemBinding.zbritjaTextview.getText().toString().replace("%","").isEmpty()) {
                    float zbritja = Float.parseFloat(itemBinding.zbritjaTextview.getText().toString().replace("%", ""));
                    float discount = Float.parseFloat(invoiceItem[0].getMaxDiscound());
                    if (zbritja > discount) {
                        fillInvoiceItemData(itemBinding,invoiceItem[0]);
                        Toast.makeText(getApplicationContext(),"Zbritja maksimale eshte: "+invoiceItem[0].getDiscount(),Toast.LENGTH_SHORT).show();
                    }
                    if ((zbritja <= discount) && !(zbritja < 0)) {
                        invoiceItem[0].setDiscount(itemBinding.zbritjaTextview.getText().toString(),true);
                    }


                }
            }
        });

        itemBinding.removeButton.setOnClickListener(view ->
        {
            doYouWantToDeleteThisArticleDialog(itemBinding.emertimiTextview.getText().toString(),() -> {
                int pos = (int) itemBinding.getRoot().getTag();
                if (stockItems.size() > 0) {
                    try {
                        stockItems.remove(pos);
                    } catch (Exception e) {

                    }
                }
                binding.invoiceItemHolder.removeView(itemBinding.getRoot());
                calculcation_form();
                calculateTotal();
                calculateVleraPaTvsh();
                calculateVleraEZbritur();
                calculateVleraETVSH();

            });
    });
        itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
        binding.invoiceItemHolder.addView(itemBinding.getRoot());
    }

    private void fillInvoiceItemData(InvoiceItemBinding itemBinding, InvoiceItem invoiceItem) {
        itemBinding.emertimiTextview.setText(invoiceItem.getName());
        itemBinding.shifraTextview.setText(invoiceItem.getSelectedItemCode());
        itemBinding.njesiaTextview.setText(invoiceItem.getSelectedUnit());
        itemBinding.zbritjaTextview.setText(invoiceItem.getDiscount());
        List<Double> amontAndPrice = calculateValueOfItem(invoiceItem);
        itemBinding.vlera.setText("" + String.format(Locale.ENGLISH,"%.3f", new BigDecimal(amontAndPrice.get(0))));
        itemBinding.cmimiTvsh.setText("" + String.format(Locale.ENGLISH,"%.3f", new BigDecimal(amontAndPrice.get(1))));
        itemBinding.basePrice.setText(invoiceItem.getBasePrice() +"");
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
                System.out.println("sasia11 "+ String.valueOf(invoiceItem.getItems().get(i).getRelacion()));
                invoiceItem.setRelacion(String.valueOf(invoiceItem.getItems().get(i).getRelacion()));
                invoiceItem.setBaseDiscount(invoiceItem.getItems().get(i).getDiscount());
                invoiceItem.setBarcode(invoiceItem.getItems().get(i).getBarcode());
                invoiceItem.setRelacion(String.valueOf(invoiceItem.getItems().get(i).getRelacion()));

            }
        }
    }

    //        this part is for to show DropDown when clicked editText for secound time and more ...
    private void shopDropDownList(){
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

    void dialog(InvoiceItem invoiceItem, InvoiceItemBinding binding) {
        try {
            String[] units = realmHelper.getItemUnits(invoiceItem.getName());
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(InvoiceActivityOriginal.this);
            alt_bld.setSingleChoiceItems(units, checked, (dialog, item) -> {
                System.out.println();
                checked = item;
                invoiceItem.setSelectedItemCode(invoiceItem.getItems().get(checked).getNumber());
                invoiceItem.setSelectedUnit(invoiceItem.getItems().get(checked).getUnit());
                invoiceItem.setSelectedPosition(checked);
                invoiceItem.setRelacion(String.valueOf(invoiceItem.getItems().get(checked).getRelacion()));
                checkIfArticleIsInAction(invoiceItem);
                hideLoader();
                fillInvoiceItemData(binding, invoiceItem);
                calculcation_form();

                cheakQuantityForInvoice(invoiceItem,binding);

                dialog.dismiss();
            });
            AlertDialog alert = alt_bld.create();
            alert.show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ju lutem zgjedhni produktin", Toast.LENGTH_SHORT).show();
        }
    }

    private void doYouWantToDeleteThisArticleDialog(String name,DoYouWantToDeleteThisArticleListener doYouWantToDeleteThisArticleListener) {
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(this);
        mBuilder.setTitle("");
        String message = getString(R.string.do_you_want_to_delete_this_article)  +" "+ name;
        mBuilder.setMessage(message);
        // Setting Negative "NO" Button
        mBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        mBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                doYouWantToDeleteThisArticleListener.Yes();
                dialog.cancel();
            }
        });
        // Showing Alert Message
        mBuilder.show();
    }

    private void cheakQuantityForInvoice(InvoiceItem invoiceItem, InvoiceItemBinding itemBinding){

        if ( invoiceState == InvoiceState.FATUR) {

            double sasia = 0;
            if (itemBinding.sasiaTextview.getText().length() > 0) {
                sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
            }
            double availableQuantity = 0;
            availableQuantity = Double.parseDouble(invoiceItem.getQuantity()) / invoiceItem.getItems().get(invoiceItem.getSelectedPosition()).getRelacion();

            if (sasia <= availableQuantity && sasia > 0f) {
                if (itemBinding.sasiaTextview.getText().length() == 0) {
                    invoiceItem.setSasia("0");
                } else {
                    invoiceItem.setSasia(itemBinding.sasiaTextview.getText().toString());
                    if (invoiceItem.isAction() && sasia >= invoiceItem.getMinQuantityForDiscount()) {
                        invoiceItem.setDiscount(invoiceItem.getBaseDiscount());
                        double totalDiscount = Double.parseDouble(invoiceItem.getDiscount())
                                + Double.parseDouble(invoiceItem.getExtraDiscount());
                        invoiceItem.setDiscount(String.valueOf(totalDiscount));
                    } else {
                        invoiceItem.setDiscount(invoiceItem.getBaseDiscount());
                    }
                }
                checkIfArticleIsInAction(invoiceItem);
                fillInvoiceItemData(itemBinding, invoiceItem);
                calculcation_form();

                calculateTotal();
                calculateVleraPaTvsh();
                calculateVleraEZbritur();
                calculateVleraETVSH();
            }else {
                invoiceItem.setSasia("0");
                if (!itemBinding.sasiaTextview.getText().toString().equals("0")){
                    Toast.makeText(getApplicationContext(),"Nuk keni stok te mjaftueshem ", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }


    ArrayList<String> splitLine(String text, float maxWidth, Paint paint) {

        String[] splitText = text.split(" ");

        String currentLine = "";
        ArrayList<String> lineList = new ArrayList<>();

        for (String token : splitText) {

            if (paint.measureText(currentLine + token + " ") > maxWidth) {

                lineList.add(currentLine);
                currentLine = token + " ";
            } else {

                currentLine += token + " ";
            }
        }

        lineList.add(currentLine);

        return lineList;
    }

    private List<Double> calculateValueOfItem(InvoiceItem invoiceItem) {
        double amount_with_vat = 0.000;
        BigDecimal priceSale =BigDecimal.valueOf(amount_with_vat);
        BigDecimal price_vat_real_sale =BigDecimal.valueOf(amount_with_vat);

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
            boolean role_price_base_vat_show = true;
            if(role_price_base_vat_show == true){
                invoiceItem.setBasePrice(priceSale.doubleValue());
            }else{
                invoiceItem.setBasePrice(price_base.doubleValue());
            }


            BigDecimal article_discount_for_divide = new BigDecimal(article_discount);
            BigDecimal price_no_vat_no_discount_article_discount = price_base.multiply(article_discount_for_divide);
            BigDecimal price_no_vat_no_discount_article = price_base.subtract(price_no_vat_no_discount_article_discount);

            BigDecimal client_discount_for_divide = new BigDecimal(client_discount);
            BigDecimal client_discount_for_divide_discount = price_no_vat_no_discount_article.multiply(client_discount_for_divide);
            BigDecimal price_no_vat = price_no_vat_no_discount_article.subtract(client_discount_for_divide_discount);


            if (article_discount == 1 || client_discount == 1) {
                price_no_vat = price_no_vat.subtract(price_no_vat);
            }


            price_vat_real_sale = price_no_vat.multiply(vat_rate_for_divide);
            price_vat_real_sale = price_vat_real_sale.setScale(5, BigDecimal.ROUND_HALF_UP);

            // Amount with VAT - Imagine
            BigDecimal amount_vat_imagine = priceSale.multiply(quantity);
            amount_vat_imagine = amount_vat_imagine.setScale(3, BigDecimal.ROUND_HALF_UP);

            // Amount with VAT - Sale Real
            BigDecimal amount_vat_total = price_vat_real_sale.multiply(quantity);
            amount_vat_total = amount_vat_total.setScale(3, BigDecimal.ROUND_HALF_UP);
            amount_with_vat = amount_vat_total.doubleValue();

            // Amount no VAT
            BigDecimal amount_no_vat = price_no_vat.multiply(quantity);
            amount_no_vat = amount_no_vat.setScale(3, BigDecimal.ROUND_HALF_UP);

            // Amount of VAT
            BigDecimal amount_of_vat = amount_vat_total.subtract(amount_no_vat);
            amount_of_vat = amount_of_vat.setScale(3, BigDecimal.ROUND_HALF_UP);

            // Amount of Discount
            BigDecimal amount_of_discount = amount_vat_imagine.subtract(amount_vat_total);
            amount_of_discount = amount_of_discount.setScale(3, BigDecimal.ROUND_HALF_UP);


            // Price Sale
            price_vat_real_sale = price_vat_real_sale.setScale(3, BigDecimal.ROUND_HALF_UP);
            invoiceItem.setPriceWithvat(price_vat_real_sale.doubleValue());

            // Amount no VAT
            invoiceItem.setVleraPaTvsh(amount_no_vat.doubleValue());
            // Amount VAT - Real Sale
            amount_vat_total = amount_vat_total.setScale(3, BigDecimal.ROUND_HALF_UP);
            invoiceItem.setVleraTotale(amount_vat_total.doubleValue());
            // Amount of Discount
            invoiceItem.setVleraEZbritur(amount_of_discount.doubleValue());
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
        finalValues.add(price_vat_real_sale.doubleValue());
        return  finalValues;
        }

    private double calculateTvsh(InvoiceItem invoiceItem) {
        double priceSale = Double.parseDouble(invoiceItem.getChildList().get(invoiceItem.getSelectedPosition()).getPriceSale());
        double clientDiscount = Double.parseDouble(client.getDiscount()) * 0.01;
        double articleDiscount = Double.parseDouble(invoiceItem.getChildList().get(invoiceItem.getSelectedPosition()).getDiscount()) * 0.01;
        double vatRate = Double.parseDouble(invoiceItem.getChildList().get(invoiceItem.getSelectedPosition()).getVatRate()) * 0.01;
        double tvsh = priceSale - (priceSale * clientDiscount);
        tvsh = tvsh - (tvsh * articleDiscount);
        tvsh = tvsh + (tvsh * vatRate);
        return tvsh;
    }


    public double cutTo5(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.5f", value));
    }

    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", value));
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPostItem(boolean isPrint) {
        this.isPrint = isPrint;
        InvoicePost invoicePost = new InvoicePost();
        invoicePost.setId(realmHelper.getAutoIncrementIfForInvoice());
        invoicePost.setType("inv");
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
        invoicePost.setLocation(planetLocationManager.getLatitude() +","+planetLocationManager.getLongitude());
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
            invoiceItemPost.setPrice_base(stockItems.get(i).getBasePrice()+"");
            invoiceItemPost.setPrice_vat(String.valueOf(stockItems.get(i).getPriceWithvat()));
            invoiceItemPost.setAmount_of_discount(String.valueOf(stockItems.get(i).getVleraEZbritur()));
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
        InvoicePostObject invoicePostObject = new InvoicePostObject();
        invoicePostObject.setToken(preferences.getToken());
        invoicePostObject.setUser_id(preferences.getUserId());
        invoicePostObject.setInvoices(invoicePosts);

        if (isPrint){
            InvoicePrintUtil util = new InvoicePrintUtil(invoicePost, binding.web, this, client, printManager);
        } else  {
            binding.fragment.setVisibility(View.VISIBLE);
            for (Fragment fragment:getSupportFragmentManager().getFragments()
                 ) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            addFragment(R.id.fragment, EscPostPrintFragment.Companion.newInstace(invoicePost,preferences,realmHelper,client,cash));
        }

        apiService.postFaturat(invoicePostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (responseBody.getSuccess()) {
                        invoicePost.setSynced(true);
                        Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur dhe sinkronizuar!", Toast.LENGTH_SHORT).show();
                    } else {
                        invoicePost.setSynced(false);
                        Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
                    }
                    realmHelper.saveInvoices(invoicePost);
                }, throwable -> {
                    throwable.printStackTrace();
                    invoicePost.setSynced(false);
                    realmHelper.saveInvoices(invoicePost);
                    Toast.makeText(getApplicationContext(), "Fatura eshte ruajtur por nuk eshte sinkronizuar!", Toast.LENGTH_SHORT).show();
                });


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createOrderPostItem() {
        InvoicePost invoicePost = new InvoicePost();
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
        invoicePost.setPartie_zip(client.getZip());
        //add a few
        invoicePost.setSale_station_id(preferences.getStationId());
        invoicePost.setInvoice_date(dDate);
        invoicePost.setData_ship(shDate);
        invoicePost.setDiscount(client.getDiscount());
        invoicePost.setAmount_no_vat(vleraPaTvsh);
        invoicePost.setAmount_of_vat(vleraETvsh);
        invoicePost.setAmount_discount(vleraZbritur);
        invoicePost.setAmount_payed(cash);//vlera qe e jep kesh
        invoicePost.setLocation(planetLocationManager.getLatitude() +","+planetLocationManager.getLongitude());
        invoicePost.setAmount_with_vat(totaliFatures);
        invoicePost.setId_saler(preferences.getUserId());
        //is bill 0 = fature, 1 = kupon
        invoicePost.setIs_bill(InvoiceActivity.isBill);
        double totalNoDiscount = 0;
        RealmList<InvoiceItemPost> invoiceItemPosts = new RealmList<>();
        for (int i = 0; i < stockItems.size(); i++) {
            totalNoDiscount += (Double.parseDouble(stockItems.get(i).getChildList().get(stockItems.get(i).getSelectedPosition()).getPriceVatSale()) * Double.parseDouble(stockItems.get(i).getSasia()));
            InvoiceItemPost invoiceItemPost = new InvoiceItemPost();
            invoiceItemPost.setNo_order(String.valueOf(i));
            invoiceItemPost.setNo(stockItems.get(i).selectedItemCode);
            invoiceItemPost.setId_item_group(stockItems.get(i).getId());
            invoiceItemPost.setId_item(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getId());
            invoiceItemPost.setName(stockItems.get(i).getName());
            invoiceItemPost.setQuantity(stockItems.get(i).getSasia());
            invoiceItemPost.setUnit(stockItems.get(i).getSelectedUnit());

            invoiceItemPost.setPrice_base(stockItems.get(i).getBasePrice()+"");
            System.out.println("zbritja " + stockItems.get(i).getDiscount());
            invoiceItemPost.setDiscount(stockItems.get(i).getDiscount());
            invoiceItemPost.setVat_id(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatCodeSale());
            invoiceItemPost.setPrice_vat(String.valueOf(stockItems.get(i).getPriceWithvat()));
            invoiceItemPost.setVat_rate(stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getVatRate());
            invoiceItemPosts.add(invoiceItemPost);
        }
        invoicePost.setItems(invoiceItemPosts);
        ArrayList<InvoicePost> invoicePosts = new ArrayList<>();
        invoicePosts.add(invoicePost);
        OrderPostObject orderPostObject = new OrderPostObject();
        orderPostObject.setToken(preferences.getToken());
        orderPostObject.setUser_id(preferences.getUserId());
        orderPostObject.setOrders(invoicePosts);


        apiService.postOrderFromInvoice(orderPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {



                    if(responseBody.getSuccess()) {
                        Toast.makeText(getApplicationContext(), "Porosia u krye me sukses!", Toast.LENGTH_SHORT).show();
                        finish();

                    }else {
                        Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();

                    }

                }, throwable -> {
                    throwable.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Diqka shkoj gabim", Toast.LENGTH_SHORT).show();
                });


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showCashDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cash_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        Button keshButton = (Button) dialogView.findViewById(R.id.kesh_button);
        Button pritjeButton = (Button) dialogView.findViewById(R.id.later_button);
        Button konfirmoButton = (Button) dialogView.findViewById(R.id.ok_button);
        EditText keshEditText = (EditText) dialogView.findViewById(R.id.shuma_editText);
        keshEditText.setText(totaliFatures);
        LinearLayout buttonHolder = (LinearLayout) dialogView.findViewById(R.id.button_holder);
        LinearLayout keshHolder = (LinearLayout) dialogView.findViewById(R.id.kesh_holder);
        double totali = Double.parseDouble(totaliFatures);
        if (Double.parseDouble(client.getPaymentDeadline()) > 0 && totali < Double.parseDouble(client.getLimitBalance())) {
            pritjeButton.setEnabled(true);
        } else {
            pritjeButton.setEnabled(false);
        }
        keshButton.setOnClickListener(view -> {
            buttonHolder.setVisibility(View.GONE);
            keshHolder.setVisibility(View.VISIBLE);
        });
        pritjeButton.setOnClickListener(view -> {
            cash = "0";
            if (Double.parseDouble(cash) > 0) {
                isPaid = "1";
            } else {
                isPaid = "0";
            }
            if (InvoiceActivity.isBill.equalsIgnoreCase("1")) {
                openPrintingActivity();
            } else {
               showPrintDialog();
//            binding.web.setVisibility(View.VISIBLE);
//            binding.web.bringToFront();
            }

        });
        AlertDialog alertDialog = dialogBuilder.create();

        konfirmoButton.setOnClickListener(view -> {
            cash = keshEditText.getText().toString();
            double csh = Double.parseDouble(cash);
            if ((totali - csh) > Double.parseDouble(client.getLimitBalance())) {
                Toast.makeText(getApplicationContext(), "Duhet te inkasohet se paku " + (totali - Double.parseDouble(client.getLimitBalance())), Toast.LENGTH_SHORT).show();
            } else if (keshEditText.getText().length() > 0) {
                cash = keshEditText.getText().toString();
                if (totali == csh) {
                    isPaid = "1";
                } else {
                    isPaid = "0";
                }
                if (InvoiceActivity.isBill.equalsIgnoreCase("1")) {
                    openPrintingActivity();
                } else {
                    alertDialog.dismiss();
                   showPrintDialog();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Ju lutem jepni vleren e paguar", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showBillDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cash_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        Button keshButton = (Button) dialogView.findViewById(R.id.kesh_button);
        Button pritjeButton = (Button) dialogView.findViewById(R.id.later_button);
        Button konfirmoButton = (Button) dialogView.findViewById(R.id.ok_button);
        LinearLayout buttonHolder = (LinearLayout) dialogView.findViewById(R.id.button_holder);
        LinearLayout keshHolder = (LinearLayout) dialogView.findViewById(R.id.kesh_holder);

        keshButton.setText("Fature");
        pritjeButton.setText("Kupon Fiskal");

    AlertDialog    alertDialog = dialogBuilder.create();

        keshButton.setOnClickListener(view -> {


            InvoiceActivity.isBill = "1";
            createOrderPostItem();

            alertDialog.dismiss();


        });
        pritjeButton.setOnClickListener(view -> {
            InvoiceActivity.isBill = "0";
            createOrderPostItem();
            alertDialog.dismiss();


        });

        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPrintDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.printing_mode_dialog, null);
        dialogBuilder.setView(dialogView);
        Button print = (Button) dialogView.findViewById(R.id.print_button);
        Button print80mm = (Button) dialogView.findViewById(R.id.print_80_button);
        LinearLayout buttonHolder = (LinearLayout) dialogView.findViewById(R.id.button_holder);


        AlertDialog    alertDialog = dialogBuilder.create();

        print.setOnClickListener(view -> {
            createPostItem(true);
            alertDialog.dismiss();
        });
        print80mm.setOnClickListener(view -> {
            createPostItem(false);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if (InvoiceActivity.uploadThisMf) {
            InvoiceActivity.uploadThisMf = false;
            createPostItem(isPrint);
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



    private void addActionViews(ArrayList<InvoiceItem> items, int count) {

        final InvoiceItem invoiceItem = items.get(0);
        final int[] actionCount = {count};
        int[] positions = new int[invoiceItem.getItems().size()];
        for (int h = 0; h < invoiceItem.getItems().size(); h++) {
            Item stock = realmHelper.getItemsByid(invoiceItem.getItems().get(h).getGroupItem().toString());
            final SubItem[] item = {invoiceItem.getItems().get(h)};
            double availableQuantity = Double.parseDouble(stock.getQuantity()) / item[0].getRelacion();
            double sasia = Double.parseDouble(item[0].getQuantity()) * actionCount[0];
            if (sasia <= availableQuantity) {
                sasia = Double.parseDouble(item[0].getQuantity()) * actionCount[0];
            } else {
                sasia = 0;
            }

            final InvoiceItem[] im = new InvoiceItem[1];

            im[0] =  items.get(h);
            im[0].setQuantity(stock.getQuantity());
            stockItems.add(items.get(h));
            ActionInvItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.action_inv_item, binding.invoiceItemHolder, false);

            itemBinding.shifraTextview.setEnabled(false);
            itemBinding.emertimiTextview.setEnabled(false);
            itemBinding.njesiaTextview.setEnabled(false);
            itemBinding.sasiaTextview.setEnabled(false);

            itemBinding.shifraTextview.setText(item[0].getNumber());
            itemBinding.emertimiTextview.setText(item[0].getName());
            itemBinding.njesiaTextview.setText(item[0].getUnit());
            itemBinding.sasiaTextview.setText("" + sasia);
            itemBinding.zbritjaTextview.setText(item[0].getDiscount());
            itemBinding.basePrice.setText(invoiceItem.getBasePrice() +"");


            if (h == invoiceItem.getItems().size() - 1) {
                itemBinding.buttonHolder.setVisibility(View.VISIBLE);
            } else {
                itemBinding.buttonHolder.setVisibility(View.GONE);
            }
            itemBinding.addButton.setOnClickListener(view -> {
                actionCount[0]++;
                for (int i = positions.length; i > 0; i--) {
                    binding.invoiceItemHolder.removeViewAt(positions[i - 1]);
                    stockItems.remove(positions[i - 1]);
                }
                addActionViews(items, actionCount[0]);

            });
            itemBinding.subButton.setOnClickListener(view -> {
                if (actionCount[0] > 1) {
                    actionCount[0]--;
                    for (int i = positions.length; i > 0; i--) {
                        binding.invoiceItemHolder.removeViewAt(positions[i - 1]);
                        stockItems.remove(positions[i - 1]);
                    }
                    addActionViews(items, actionCount[0]);
                }
            });
            itemBinding.removeButton.setOnClickListener(view -> {
                for (int i = positions.length; i > 0; i--) {

                    binding.invoiceItemHolder.removeViewAt(positions[i - 1]);
                    stockItems.remove(positions[i - 1]);
                }
                calculateTotal();
                calculateVleraPaTvsh();
                calculateVleraEZbritur();
                calculateVleraETVSH();
            });
            double price = Double.parseDouble(item[0].getPriceVatSale()) - (Double.parseDouble(item[0].getPriceVatSale()) * Double.parseDouble(item[0].getDiscount()) * 0.01);
            BigDecimal priceDec = new BigDecimal(price);
            priceDec = priceDec.setScale(2, BigDecimal.ROUND_HALF_UP);
            itemBinding.cmimiTvsh.setText("" + priceDec);
            double vlera = price * Double.parseDouble(item[0].getQuantity());
            BigDecimal vleraDec = new BigDecimal(vlera);
            vleraDec = vleraDec.setScale(2, BigDecimal.ROUND_HALF_UP);
            itemBinding.vlera.setText("" + vleraDec);
            positions[h] = binding.invoiceItemHolder.getChildCount();
            itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
            stockItems.get(positions[h]).setId(invoiceItem.getGroupItem());
            stockItems.get(positions[h]).setDiscount(invoiceItem.getItems().get(h).getDiscount());
            stockItems.get(positions[h]).setSelectedItemCode(invoiceItem.getItems().get(h).getNumber());
            stockItems.get(positions[h]).setSelectedUnit(invoiceItem.getItems().get(h).getUnit());
            stockItems.get(positions[h]).setSelectedPosition(h);
            stockItems.get(positions[h]).setBaseDiscount(invoiceItem.getDiscount());
            stockItems.get(positions[h]).setSasia(String.valueOf(sasia));
            stockItems.get(positions[h]).setName(invoiceItem.getItems().get(h).getName());
            stockItems.get(positions[h]).setBarcode(invoiceItem.getItems().get(h).getBarcode());
            stockItems.get(positions[h]).setAction(true);
            stockItems.get(positions[h]).setCollection(true);
            calculateValueOfItem(stockItems.get(positions[h]));
            itemBinding.basePrice.setText(String.valueOf(stockItems.get(positions[h]).getBasePrice()));

            binding.invoiceItemHolder.addView(itemBinding.getRoot());
            calculcation_form();
            calculateTotal();
            calculateVleraPaTvsh();
            calculateVleraEZbritur();
            calculateVleraETVSH();
        }

    }


    private void checkedQuantity(){
        binding.loader.setVisibility(View.VISIBLE);
        if (stockItems.size()>0){
            InvoiceItem stock = stockItems.get(stockItems.size()-1);
            String  stockItemId = stockItems.get(stockItems.size()-1).getItems().get(stockItems.get(stockItems.size()-1).getSelectedPosition()).getId();

            CheckQuantity checkQuantity = new CheckQuantity(preferences.getUserId(),preferences.getToken(),stock.getSasia(),preferences.getStationId(),dDate,stockItemId);
            Log.e("endritiQoke",checkQuantity.toString());

            apiService.checkQuantity(checkQuantity).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(invoiceUploadResponse -> {
                        if (!invoiceUploadResponse.getSuccess()) {
                            Toast.makeText(getApplicationContext(), invoiceUploadResponse.getError().getText(), Toast.LENGTH_SHORT).show();

                        } else {
                            addInvoiceItem();
                            Toast.makeText(getApplicationContext(), "Artikulli është në stok!", Toast.LENGTH_SHORT).show();
                        }
                        binding.loader.setVisibility(View.GONE);
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                            throwable.printStackTrace();
                        }
                    });
        }else {
            binding.loader.setVisibility(View.GONE);
            addInvoiceItem();

        }
    }

    // Loading Icon
        // Show Loading Icon
        private void showLoader() {
            binding.loader.setVisibility(View.VISIBLE);
            binding.loader.bringToFront();
            binding.loader.setOnClickListener(view -> {        });
        }
        // Hide Loading Icon
        private void hideLoader() {
            binding.loader.setVisibility(View.GONE);
        }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.fatur_radio:
                invoiceState = InvoiceState.FATUR;
                preferences.saveLastCheck(0);

                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);


                break;
            case R.id.porosia_radio:
                invoiceState = InvoiceState.POROSI;
                preferences.saveLastCheck(1);

                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);

                break;

            default:
                // Nothing to do
        }
    }


    private void calculcation_form(){
        showLoader();


        // Init

        for (InvoiceItem stock:stockItems) {
            if(!stock.getType().equals("action")){
                stock.setAction(false);
                }
        }
        // Action with Amount and Count - Step Action
        allActions();

        for (InvoiceItem stock:stockItems) {
            if(!stock.getType().equals("action")){

                final int childCount = binding.invoiceItemHolder.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ViewGroup v =  (ViewGroup) binding.invoiceItemHolder.getChildAt(i);

                    final int childCount2 = v.getChildCount();

                    for (int j = 0; j < childCount2; j++) {
                        View v2 = v.getChildAt(j);

                        if (v2.getId() == R.id.emertimi_textview){
                            AutoCompleteTextView emertimi = (AutoCompleteTextView) v2;
                            if (emertimi.getText().toString().equals(stock.getName())){
                                for (int k = 0; k < childCount2; k++) {
                                    View v3 = v.getChildAt(k);
                                    List<Double> amontAndPrice = calculateValueOfItem(stock);

                                    switch (v3.getId()){
                                        case R.id.zbritja_textview:
                                            AutoCompleteTextView zbirjta = (AutoCompleteTextView) v3;
                                            zbirjta.setText(stock.getDiscount());
                                            break;

                                        case R.id.base_price:
                                            AutoCompleteTextView base = (AutoCompleteTextView) v3;
                                            base.setText(stock.getBasePrice() +"");
                                            break;

                                        case R.id.cmimi_tvsh:
                                            AutoCompleteTextView cmimitvsh = (AutoCompleteTextView) v3;

                                            cmimitvsh.setText("" +  String.format(Locale.ENGLISH,"%.3f",new BigDecimal(amontAndPrice.get(1))));
                                            break;

                                        case R.id.vlera:
                                            AutoCompleteTextView vlera = (AutoCompleteTextView) v3;
                                            vlera.setText("" + String.format(Locale.ENGLISH,"%.3f",new BigDecimal(amontAndPrice.get(0))));
                                            break;

                                    }
                                    }

                           }
                        }
                    }


                    }

            }


        }


        // Form Totals
        calculateTotal();
        calculateVleraPaTvsh();
        calculateVleraEZbritur();
        calculateVleraETVSH();
        hideLoader();
    }

    private InvoiceItem checkIfArticleIsInAction(InvoiceItem invoiceItem) {
        //check product action
        for (int i = 0; i < actionData.getArticleItems().size(); i++) {
            if (actionData.getArticleItems().get(i).getType().equals("specific")) {
                boolean clientCat = actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                boolean itemId = invoiceItem.getItems().get(invoiceItem.getSelectedPosition()).getId().equalsIgnoreCase(actionData.getArticleItems().get(i).getItem_id());
                int clientId = Integer.parseInt(client.getId());
                int action_client_id = actionData.getArticleItems().get(i).getClientId();
                boolean isClientWithAction = clientId == action_client_id;
                boolean groupId = invoiceItem.getItems().get(invoiceItem.getSelectedPosition()).getGroupItem().equalsIgnoreCase(actionData.getArticleItems().get(i).getGroupItem());
                boolean type = invoiceItem.getType().equalsIgnoreCase("single");
                boolean relation = invoiceItem.getItems().get(invoiceItem.getSelectedPosition()).getRelacion() >= Double.parseDouble(actionData.getArticleItems().get(i).getRelacion());
                //guxh bab mos e prek se DIGJESH!!!!

                if (isClientWithAction && (itemId || (groupId && relation && type))) {
                    invoiceItem.setAction(true);
                    invoiceItem.setMinQuantityForDiscount(actionData.getArticleItems().get(i).getQuantity());
//                    invoiceItem.setExtraDiscount(actionData.getArticleItems().get(i).getDiscount());

                    double totalDiscount = Double.parseDouble(invoiceItem.getDiscount()) + Double.parseDouble(invoiceItem.getExtraDiscount());
                    invoiceItem.setDiscount(actionData.getArticleItems().get(i).getDiscount());
                    break;
                } else if ((clientCat) && (itemId || (groupId && relation && type)) && (action_client_id == 0)) {
                    invoiceItem.setAction(true);
                    invoiceItem.setMinQuantityForDiscount(actionData.getArticleItems().get(i).getQuantity());
//                    invoiceItem.setExtraDiscount(actionData.getArticleItems().get(i).getDiscount());
                    double totalDiscount = Double.parseDouble(invoiceItem.getDiscount())
                            + Double.parseDouble(invoiceItem.getExtraDiscount());
                    invoiceItem.setDiscount(actionData.getArticleItems().get(i).getDiscount());
                    break;
                } else {
                    invoiceItem.setAction(false);
                    invoiceItem.setMinQuantityForDiscount("0");
                    invoiceItem.setExtraDiscount("0");
                    invoiceItem.setDiscount("0");
                }

            }
        }
        //check brand action
        for (int i = 0; i < actionData.getArticleBrandItem().size(); i++) {
            if (actionData.getArticleBrandItem().get(i).getType().equals("specific")){
                boolean clientCat = actionData.getArticleBrandItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleBrandItem().get(i).getClientCategory().equalsIgnoreCase("0");
                boolean has_brand = invoiceItem.getBrand().equalsIgnoreCase(actionData.getArticleBrandItem().get(i).getBrandId());
                boolean checkUnit = checkUnitForAction(invoiceItem,actionData.getArticleBrandItem().get(i).getUnit());


                if (has_brand && clientCat && checkUnit){
                    invoiceItem.setAction(true);
                    invoiceItem.setMinQuantityForDiscount(actionData.getArticleBrandItem().get(i).getQuantity());
//                    invoiceItem.setExtraDiscount(actionData.getArticleBrandItem().get(i).getDiscount());
                    invoiceItem.setDiscount(actionData.getArticleBrandItem().get(i).getDiscount());
                    break;
                }

            }
        }
        //check category action
        for (int i = 0; i < actionData.getArticleCategoryItem().size(); i++) {
            if (actionData.getArticleCategoryItem().get(i).getType().equals("specific")) {
                boolean clientCat = actionData.getArticleCategoryItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                boolean has_item_category = invoiceItem.getCategory().equalsIgnoreCase(actionData.getArticleCategoryItem().get(i).getCategoryId());
                boolean checkUnit = checkUnitForAction(invoiceItem,actionData.getArticleCategoryItem().get(i).getUnit());

                if (has_item_category && clientCat && checkUnit) {
                    invoiceItem.setAction(true);
                    invoiceItem.setMinQuantityForDiscount(actionData.getArticleCategoryItem().get(i).getQuantity());
//                    invoiceItem.setExtraDiscount(actionData.getArticleCategoryItem().get(i).getDiscount());
                    invoiceItem.setDiscount(actionData.getArticleCategoryItem().get(i).getDiscount());

                    break;
                }

            }
        }
        return invoiceItem;
    }

    public Boolean checkUnitForAction(InvoiceItem invoiceItem, String unit) {

        if (unit.equals(invoiceItem.getSelectedUnit())) {
            return true;
        } else {

            SubItem actionItem = new SubItem();
            SubItem mainItem = new SubItem();

            mainItem.setUnit(invoiceItem.getSelectedUnit());
            actionItem.setUnit(unit);

            for (SubItem item : invoiceItem.getItems()) {

                if (item.getUnit().equals(actionItem.getUnit())) {
                    actionItem.setRelacion(String.valueOf(item.getRelacion()));
                }

                if (item.getUnit().equals(mainItem.getUnit())) {
                    mainItem.setRelacion(String.valueOf(item.getRelacion()));
                }
            }

            if (mainItem.getRelacion() >= actionItem.getRelacion()) {
                return true;
            } else {
                return false;
            }
        }

    }

    public String[] removeNullElements(String[] firstArray){
        ArrayList<String> list = new ArrayList<String>();
        for (String s : firstArray)
            if (s != null) {
                if (!s.equals("")){
                    list.add(s);
            }
        }

        return firstArray = list.toArray(new String[list.size()]);
    }
    public void allActions() {

        ArticleActionWithAmount();
        ArticleActionWithQuantity();

        brandActionWithAmount();
        brandActionWithQuantity();

        CategoryActionWithAmount();
        CategoryActionWithQuantity();
        }

    public void brandActionWithAmount(){

        //check brand with amount
        for (int i = 0; i < actionData.getArticleBrandItem().size(); i++) {
            if (actionData.getArticleBrandItem().get(i).getType().equals("amount")) {
                ActionBrandItem brend  = actionData.getArticleBrandItem().get(i);
                float amount = 0;
                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleBrandItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getBrand().equalsIgnoreCase(actionData.getArticleBrandItem().get(i).getBrandId())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        amount += stockItems.get(j).getVleraTotale();
                    }
                }

                // Steps
                float discount = 0;

                for (ActionSteps steps :brend.getSteps()) {
                    float from  = Float.parseFloat(steps.from);
                    float to  = Float.parseFloat(steps.to);
                    if (( from <= amount) && ( amount <= to ) ){

                        if ((discount<Float.parseFloat(steps.discount))){
                            discount = Float.parseFloat(steps.discount);
                        }
                    }
                }



                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleBrandItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getBrand().equalsIgnoreCase(actionData.getArticleBrandItem().get(i).getBrandId())
                            && clientCat && !stockItems.get(j).isAction() ) {

                        stockItems.get(j).setAction(true);
                        stockItems.get(j).setDiscount(String.valueOf(discount));
                    }
                }
            }
        }

    }

    public void brandActionWithQuantity(){

        //check brand with amount
        for (int i = 0; i < actionData.getArticleBrandItem().size(); i++) {
            if (actionData.getArticleBrandItem().get(i).getType().equals("quantity")) {
                ActionBrandItem brend  = actionData.getArticleBrandItem().get(i);
                float amount = 0;
                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleBrandItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getBrand().equalsIgnoreCase(actionData.getArticleBrandItem().get(i).getBrandId())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        amount ++;
                    }
                }


                // Steps
                float discount = 0;

                for (ActionSteps steps :brend.getSteps()) {
                    float from  = Float.parseFloat(steps.from);
                    float to  = Float.parseFloat(steps.to);
                    if (( from <= amount) && ( amount <= to ) ){

                        if ((discount<Float.parseFloat(steps.discount))){
                            discount = Float.parseFloat(steps.discount);
                        }
                    }
                }

                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleBrandItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getBrand().equalsIgnoreCase(actionData.getArticleBrandItem().get(i).getBrandId())
                            && clientCat && !stockItems.get(j).isAction() ) {

                        stockItems.get(j).setAction(true);
                        stockItems.get(j).setDiscount(String.valueOf(discount));
                    }
                }
            }
        }

    }

    public void ArticleActionWithAmount(){

        //check brand with amount
        for (int i = 0; i < actionData.getArticleItems().size(); i++) {
            if (actionData.getArticleItems().get(i).getType().equals("amount")) {
                ActionArticleItems article  = actionData.getArticleItems().get(i);
                float amount = 0;
                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getId().equalsIgnoreCase(actionData.getArticleItems().get(i).getItem_id())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        amount += stockItems.get(j).getVleraTotale();
                    }
                }

                // Steps
                float discount = 0;

                for (ActionSteps steps :article.getSteps()) {
                    float from  = Float.parseFloat(steps.from);
                    float to  = Float.parseFloat(steps.to);
                    if (( from <= amount) && ( amount <= to ) ){

                        if ((discount<Float.parseFloat(steps.discount))){
                            discount = Float.parseFloat(steps.discount);
                        }
                    }
                }



                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getId().equalsIgnoreCase(actionData.getArticleItems().get(i).getItem_id())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        stockItems.get(j).setAction(true);
                        stockItems.get(j).setDiscount(String.valueOf(discount));
                    }
                }
            }
        }

    }

    public void ArticleActionWithQuantity(){

        //check brand with amount
        for (int i = 0; i < actionData.getArticleItems().size(); i++) {
            if (actionData.getArticleItems().get(i).getType().equals("quantity")) {
                ActionArticleItems article  = actionData.getArticleItems().get(i);
                float amount = 0;
                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getId().equalsIgnoreCase(actionData.getArticleItems().get(i).getItem_id())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        amount ++;
                    }
                }

                // Steps
                float discount = 0;

                for (ActionSteps steps :article.getSteps()) {
                    float from  = Float.parseFloat(steps.from);
                    float to  = Float.parseFloat(steps.to);
                    if (( from <= amount) && ( amount <= to ) ){

                        if ((discount<Float.parseFloat(steps.discount))){
                            discount = Float.parseFloat(steps.discount);
                        }
                    }
                }

                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getId().equalsIgnoreCase(actionData.getArticleItems().get(i).getItem_id())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        stockItems.get(j).setAction(true);
                        stockItems.get(j).setDiscount(String.valueOf(discount));
                    }
                }
            }
        }

    }

    public void CategoryActionWithAmount(){

        //check category with amount
        for (int i = 0; i < actionData.getArticleCategoryItem().size(); i++) {
            if (actionData.getArticleCategoryItem().get(i).getType().equals("amount")) {
                ActionCategoryItem category  = actionData.getArticleCategoryItem().get(i);
                float amount = 0;
                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleCategoryItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getCategory().equalsIgnoreCase(actionData.getArticleCategoryItem().get(i).getCategoryId())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        amount += stockItems.get(j).getVleraTotale();
                    }
                }

                // Steps
                float discount = 0;

                for (ActionSteps steps :category.getSteps()) {
                    float from  = Float.parseFloat(steps.from);
                    float to  = Float.parseFloat(steps.to);
                    if (( from <= amount) && ( amount <= to ) ){

                        if ((discount<Float.parseFloat(steps.discount))){
                            discount = Float.parseFloat(steps.discount);
                        }
                    }
                }


                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleCategoryItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getCategory().equalsIgnoreCase(actionData.getArticleCategoryItem().get(i).getCategoryId())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        stockItems.get(j).setAction(true);
                        stockItems.get(j).setDiscount(String.valueOf(discount));
                    }
                }
            }
        }

    }

    public void CategoryActionWithQuantity(){

        //check category with amount
        for (int i = 0; i < actionData.getArticleCategoryItem().size(); i++) {
            if (actionData.getArticleCategoryItem().get(i).getType().equals("quantity")) {
                ActionCategoryItem category  = actionData.getArticleCategoryItem().get(i);
                float amount = 0;
                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleCategoryItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getCategory().equalsIgnoreCase(actionData.getArticleCategoryItem().get(i).getCategoryId())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        amount ++;
                    }
                }

                // Steps
                float discount = 0;

                for (ActionSteps steps :category.getSteps()) {
                    float from  = Float.parseFloat(steps.from);
                    float to  = Float.parseFloat(steps.to);
                    if (( from <= amount) && ( amount <= to ) ){

                        if ((discount<Float.parseFloat(steps.discount))){
                            discount = Float.parseFloat(steps.discount);
                        }
                    }
                }


                for (int j = 0; j < stockItems.size(); j++) {
                    boolean clientCat = actionData.getArticleCategoryItem().get(i).getClientCategory().equalsIgnoreCase(client.getClientCategory()) || actionData.getArticleItems().get(i).getClientCategory().equalsIgnoreCase("0");
                    if (stockItems.get(j).getCategory().equalsIgnoreCase(actionData.getArticleCategoryItem().get(i).getCategoryId())
                            && clientCat && !stockItems.get(j).isAction() ) {
                        stockItems.get(j).setAction(true);
                        stockItems.get(j).setDiscount(String.valueOf(discount));
                    }
                }
            }
        }

    }

    public void calculateTotal() {
        double total = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            total += stockItems.get(i).getVleraTotale();
        }
        this.totaliFatures = String.valueOf(cutTo2(total));
        binding.vleraTotale.setText("Vlera Totale: " + cutTo2(total));
    }
    public void calculateVleraPaTvsh() {
        double vleraPaTvsh = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            vleraPaTvsh += stockItems.get(i).getVleraPaTvsh();
        }
        this.vleraPaTvsh = String.valueOf(cutTo2(vleraPaTvsh));
        binding.vleraPaTvsh.setText("Vlera pa TVSH: " + cutTo2(vleraPaTvsh));
    }
    public void calculateVleraEZbritur() {
        double vleraEZbritur = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            vleraEZbritur += stockItems.get(i).getVleraEZbritur();
        }
        this.vleraZbritur = String.valueOf(cutTo2(vleraEZbritur));
        binding.vleraEZbritur.setText("Vlera e zbritur: " + cutTo2(vleraEZbritur));
    }
    public void calculateVleraETVSH() {
        double vleraETvsh = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            vleraETvsh += stockItems.get(i).getVleraETvsh();
        }
        this.vleraETvsh = String.valueOf(cutTo2(vleraETvsh));
        binding.vleraETvsh.setText("Vlera e TVSH-së: " + cutTo2(vleraETvsh));
    }


    public void addFragment(int view, Fragment fragment) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(view, fragment);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.getMessage();
        }

    }

    interface DoYouWantToDeleteThisArticleListener {
        void Yes();
    }

}
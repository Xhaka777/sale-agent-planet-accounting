package org.planetaccounting.saleAgent;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.OrderItemBinding;
import org.planetaccounting.saleAgent.databinding.OrderLayoutBinding;
import org.planetaccounting.saleAgent.model.InvoiceItem;
import org.planetaccounting.saleAgent.model.Varehouse;
import org.planetaccounting.saleAgent.model.VarehouseReponse;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.order.CheckQuantity;
import org.planetaccounting.saleAgent.model.order.OrderItemPost;
import org.planetaccounting.saleAgent.model.order.OrderObject;
import org.planetaccounting.saleAgent.model.order.OrderPost;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by macb on 08/01/18.
 */

public class OrdersActivity extends AppCompatActivity {
    OrderLayoutBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    ArrayList<InvoiceItem> stockItems = new ArrayList<>();
    Client client;
    String fDate;
    String dDate;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;

    Integer stationPos;
    int checked = 0;
    List<Varehouse> varehouses = new ArrayList<>();
    String stationID = "2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.order_layout);

        Kontabiliteti.getKontabilitetiComponent().inject(this);
        binding.shtoTextview.setOnClickListener(view -> checkedQuantity());
        Date cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
        binding.dataEdittext.setText(fDate);
        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fDate = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
                binding.dataEdittext.setText(fDate);

            }
        };

                binding.dataLinar.setOnClickListener(v -> getdata() );
        binding.emriKlientit.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getClientsNames()));

        binding.emriKlientit.setOnItemClickListener((adapterView, view, i, l) -> {
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
        });

        binding.porositButton.setOnClickListener(view -> {
            if (stockItems.size() > 0) {
                if (checkSasia()) {
                    createOrder();
                } else {
                    Toast.makeText(getApplicationContext(), "Një ose më shum artikuj kan sasine zero", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.depoEdittext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stationID = varehouses.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.clientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    binding.clientLinar.setVisibility(View.VISIBLE);
                    binding.stationLinar.setVisibility(View.VISIBLE);
                    }else {
                    binding.clientLinar.setVisibility(View.GONE);
                    binding.stationLinar.setVisibility(View.GONE);
                    binding.emriKlientit.getText().clear();
                    binding.njersiaEdittext.getText().clear();

                    client = null;
                    stationPos = null;
                }
            }
        });

        shopDropDownList();
        getVareHouses();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.depoEdittext.showDropDown();
                binding.depoEdittext.requestFocus();
            }
        }, 500);
    }

    //        this part is for to show DropDown when clicked editText for secound time and more ...
    private void shopDropDownList(){
        binding.depoEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.depoEdittext.showDropDown();
                return false;
            }
        });

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

    private void getdata(){

        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public boolean checkSasia() {
        for (int i = 0; i < stockItems.size(); i++) {
            double sasia;
            try {
                sasia = Double.parseDouble(stockItems.get(i).getSasia());
            } catch (Exception e) {
                sasia =  Double.parseDouble(stockItems.get(i).getSasia());
            }
            if (sasia == 0) {
                return false;
            }
        }
        return true;
    }

    private void addOrderItem() {

        final InvoiceItem[] invoiceItem = new InvoiceItem[1];
//        final Item[] item = new Item[1];
        OrderItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.order_item, binding.invoiceItemHolder, false);
        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsName()));
        itemBinding.emertimiTextview.setOnItemClickListener((adapterView, view, i, l) -> {
            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString()));
            int pos = (int) itemBinding.getRoot().getTag();
            try {
                stockItems.set(pos, invoiceItem[0]);
            } catch (IndexOutOfBoundsException e) {
                stockItems.add(pos, invoiceItem[0]);
            }
            itemBinding.sasiaTextview.requestFocus();
            findCodeAndPosition(invoiceItem[0]);
            fillInvoiceItemData(itemBinding, invoiceItem[0]);
        });

        itemBinding.emertimiTextview.showDropDown();
        itemBinding.emertimiTextview.requestFocus();
        itemBinding.shifraTextview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsCodes()));
        itemBinding.shifraTextview.setOnItemClickListener((adapterView, view, i, l) -> {
            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByCode(itemBinding.shifraTextview.getText().toString()));
            itemBinding.sasiaTextview.requestFocus();
            findCodeAndPosition(invoiceItem[0]);
            fillInvoiceItemData(itemBinding, invoiceItem[0]);
        });
        itemBinding.njesiaTextview.setOnClickListener(view -> dialog(invoiceItem[0], itemBinding));
        itemBinding.emertimiTextview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                itemBinding.emertimiTextview.showDropDown();
                return false;
            }
        });
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
        });
        itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
        binding.invoiceItemHolder.addView(itemBinding.getRoot());
    }

    private void checkedQuantity(){
        binding.loader.setVisibility(View.VISIBLE);
        if (stockItems.size()>0){
            InvoiceItem stock = stockItems.get(stockItems.size()-1);
            String  stockItemId = stockItems.get(stockItems.size()-1).getItems().get(stockItems.get(stockItems.size()-1).getSelectedPosition()).getId();
            CheckQuantity checkQuantity = new CheckQuantity(preferences.getUserId(),preferences.getToken(),stock.getSasia(),stationID,dDate,stockItemId);
            apiService.checkQuantity(checkQuantity).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(invoiceUploadResponse -> {

                        final int childCount = binding.invoiceItemHolder.getChildCount();
                            ViewGroup v =  (ViewGroup) binding.invoiceItemHolder.getChildAt(childCount - 1);

                        ViewGroup v2 = (ViewGroup) v.getChildAt(0);
                        ViewGroup v3  =(ViewGroup) v2.getChildAt(0);
                        ViewGroup v4  =(ViewGroup) v3.getChildAt(1);
                        ViewGroup v5  =(ViewGroup) v4.getChildAt(1);
                        View v6  = v5.getChildAt(1);


                        AutoCompleteTextView sasia_depo = (AutoCompleteTextView) v6;
                        sasia_depo.setText(String.valueOf(invoiceUploadResponse.getCurrentQuantity()));

                        if (!invoiceUploadResponse.getSuccess()) {
                            Toast.makeText(getApplicationContext(), invoiceUploadResponse.getError().getText(), Toast.LENGTH_SHORT).show();

                        } else {
                            addOrderItem();
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
            addOrderItem();

        }
    }

    private void findCodeAndPosition(InvoiceItem invoiceItem) {
        String itemCode;
        for (int i = 0; i < invoiceItem.getItems().size(); i++) {
            if (invoiceItem.getDefaultUnit().equalsIgnoreCase(invoiceItem.getItems().get(i).getUnit())) {
                checked = i;
                invoiceItem.setSelectedItemCode(invoiceItem.getItems().get(i).getNumber());
                invoiceItem.setSelectedUnit(invoiceItem.getItems().get(i).getUnit());
            }
        }
    }

    private void fillInvoiceItemData(OrderItemBinding itemBinding, InvoiceItem invoiceItem) {
        itemBinding.emertimiTextview.setText(invoiceItem.getName());
        itemBinding.shifraTextview.setText(invoiceItem.getSelectedItemCode());
        itemBinding.njesiaTextview.setText(invoiceItem.getSelectedUnit());
    }

    void dialog(InvoiceItem invoiceItem, OrderItemBinding binding) {
        try {
            String[] units = realmHelper.getItemUnits(invoiceItem.getName());
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(OrdersActivity.this);
            alt_bld.setSingleChoiceItems(units, checked, (dialog, item) -> {
                checked = item;
                invoiceItem.setSelectedItemCode(invoiceItem.getItems().get(checked).getNumber());
                invoiceItem.setSelectedUnit(invoiceItem.getItems().get(checked).getUnit());
                invoiceItem.setSelectedPosition(checked);
                dialog.dismiss();
                fillInvoiceItemData(binding, invoiceItem);

            });
            AlertDialog alert = alt_bld.create();
            alert.show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ju lutem zgjedhni produktin", Toast.LENGTH_SHORT).show();
        }
    }

    private void createOrder() {
        binding.loader.setVisibility(View.VISIBLE);
        List<OrderItemPost> orderItemPosts = new ArrayList<>();
        for (int i = 0; i < stockItems.size(); i++) {
            orderItemPosts.add(new OrderItemPost(String.valueOf(i),
                    stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getId(),
                    stockItems.get(i).getId(),
                    stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getName(),
                    stockItems.get(i).getSasia(),
                    stationID
            ));
        }
        String partie_id = "";
        String partie_station = "";
        if (client!=null){
            partie_id = client.getId();
            if (stationPos !=null){
                partie_station = client.getStations().get(stationPos).getId();

            }
        }
        OrderPost orderPost = new OrderPost(partie_id,
                partie_station,
                preferences.getStationId(),
                dDate,
                preferences.getUserId(),
                orderItemPosts);
        OrderObject orderObject = new OrderObject(orderPost, preferences.getToken(), preferences.getUserId());
        apiService.postOrder(orderObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(invoiceUploadResponse -> {
                    if (!invoiceUploadResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), invoiceUploadResponse.getError().getText(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Porosia u krye me sukses!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    binding.loader.setVisibility(View.GONE);
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getVareHouses() {
        apiService.getWareHouses(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VarehouseReponse>() {
                    @Override
                    public void call(VarehouseReponse varehouseReponse) {
                        varehouses = varehouseReponse.getStations();

                        if (preferences.getDefaultWarehouse().isEmpty() || preferences.getDefaultWarehouse().equals("")){
                            stationID = varehouses.get(0).getId();
                            } else  {
                            stationID = preferences.getDefaultWarehouse();
                            for (int i = 0; i < varehouses.size(); i++) {
                                if (varehouses.get(i).getId().equals(stationID)){
                                    binding.depoEdittext.setText(varehouses.get(i).getName());
                                }
                            }

                        }
                        String[] stations = new String[varehouses.size()];
                        for (int i = 0; i < varehouses.size(); i++) {
                            stations[i] = varehouses.get(i).getName();
                        }
                        binding.depoEdittext.setAdapter(new ArrayAdapter<String>(OrdersActivity.this,
                                android.R.layout.simple_dropdown_item_1line, stations));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                        throwable.printStackTrace();
                    }
                });
    }
}

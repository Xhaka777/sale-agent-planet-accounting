package org.planetaccounting.saleAgent;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import org.planetaccounting.saleAgent.helper.LocaleHelper;
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
import org.planetaccounting.saleAgent.transfere.CreateTransferActivity;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    int pozitaeArtikullit = 0;
    String nrArtikujtTotal = "0";
    String sasiaTotale = "0";

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.order_layout);

        Kontabiliteti.getKontabilitetiComponent().inject(this);
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

        binding.dataLinar.setOnClickListener(v -> getdata());
        binding.emriKlientit.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getClientsNames()));

        binding.emriKlientit.setOnItemClickListener((adapterView, view, i, l) -> {
            client = realmHelper.getClientFromName(binding.emriKlientit.getText().toString().substring(0, binding.emriKlientit.getText().toString().indexOf(" nrf:")));

            if (realmHelper.getClientStations(client.getName()).length > 0) {
                binding.njesiaEdittext.setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line, realmHelper.getClientStations(client.getName())));
                binding.njesiaEdittext.setEnabled(true);
                binding.njesiaEdittext.requestFocus();
                binding.njesiaEdittext.showDropDown();
                binding.njesiaEdittext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        stationPos = i;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } else {
                binding.njesiaEdittext.setText("");
                binding.njesiaEdittext.setHint("");
                binding.njesiaEdittext.setEnabled(false);
            }
        });

        binding.shtoTextview.setOnClickListener(view -> addOrderItem());

        binding.porositButton.setOnClickListener(view -> {
            if (stockItems.size() > 0) {
                if (checkSasia()) {
                    createOrder();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.nje_ose_me_shume_artikuj_kan_sasine_zero, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.shtoni_se_paku_nje_artikull, Toast.LENGTH_SHORT).show();
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
                if (isChecked) {
                    binding.clientLinar.setVisibility(View.VISIBLE);
                    binding.stationLinar.setVisibility(View.VISIBLE);
                } else {
                    binding.clientLinar.setVisibility(View.GONE);
                    binding.stationLinar.setVisibility(View.GONE);
                    binding.emriKlientit.getText().clear();
                    binding.njesiaEdittext.getText().clear();

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


        currentLanguage = getIntent().getStringExtra(currentLang);

    }


    //methods to change the languages

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLang)) {
            Context context = LocaleHelper.setLocale(this, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(OrdersActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    //        this part is for to show DropDown when clicked editText for secound time and more ...
    private void shopDropDownList() {
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

        binding.njesiaEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.njesiaEdittext.showDropDown();
                return false;
            }
        });


    }

    private void getdata() {

        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public boolean checkSasia() {
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

    private void addOrderItem() {

        final InvoiceItem[] invoiceItem = new InvoiceItem[1];
//        final Item[] item = new Item[1];
        OrderItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.order_item, binding.invoiceItemHolder, false);
        itemBinding.emertimiTextview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, realmHelper.getStockItemsName()));
        itemBinding.emertimiTextview.setOnItemClickListener((adapterView, view, i, l) -> {
            invoiceItem[0] = new InvoiceItem(realmHelper.getItemsByName(itemBinding.emertimiTextview.getText().toString()));
            itemBinding.sasiaTextview.setText("1");
            invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
            int pos = (int) itemBinding.getRoot().getTag();
            try {
                stockItems.set(pos, invoiceItem[0]);
            } catch (IndexOutOfBoundsException e) {
                stockItems.add(pos, invoiceItem[0]);
            }
            itemBinding.sasiaTextview.requestFocus();
            findCodeAndPosition(invoiceItem[0]);
            fillInvoiceItemData(itemBinding, invoiceItem[0]);
            checkedQuantity();
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
            checkedQuantity();
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
//                if (itemBinding.sasiaTextview.getText().length() == 0) {
//                    invoiceItem[0].setSasia("0");
//                } else {
//                    invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
////                    invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText(preferences.getSasia()));
//                }
//Pjesa e pare per error ne sasin...
                try {
                    if (itemBinding.sasiaTextview.getText().length() <= 0) {
                        invoiceItem[0].setSasia("0");
                        Toast.makeText(OrdersActivity.this, R.string.sheno_sasin, Toast.LENGTH_SHORT).show();
                    } else {
                        invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString() + "");
                        //Item row index
                        pozitaeArtikullit = (int) itemBinding.getRoot().getTag();
                        //String Quantity
                        String str_quantity = itemBinding.sasiaTextview.getText().toString();
                        double sasia = 0;
                        if (str_quantity.length() > 0) {
                            sasia = Double.parseDouble(str_quantity);
                        } else if (str_quantity.length() == 0) {
                            invoiceItem[0].setSasia("0");
                        }
                        //Item Relacion
                        double relacion = (int) invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getRelacion();
                        //Quantity available on warehouse
                        double quantity_base_on_warehouse = Double.parseDouble(invoiceItem[0].getQuantity());

//                        itemBinding.sasiaDepoTextview.setText(String.valueOf((quantity_base_on_warehouse) / relacion));

                    }
                    fillInvoiceItemData(itemBinding, invoiceItem[0]);
                    calculateTotalQuantity();
                    calculateTotalOfArticles();
                    checkedQuantity();

                } catch (Exception e) {
                    Toast.makeText(OrdersActivity.this, R.string.nuk_keni_sasi_te_mjaftueshme_ne_depo, Toast.LENGTH_SHORT).show();
                }
                //Pjesa e dyte ne errorin e sasis
//
//                double sasia = 0;
//                if (itemBinding.sasiaTextview.getText().length() > 0) {
//                    sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
//                }
//                double availableQuantity = 0;
//                availableQuantity = Double.parseDouble(invoiceItem[0].getQuantity()) / invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getRelacion();
//
//                if (sasia <= availableQuantity && sasia > 0f) {
//                    if (itemBinding.sasiaTextview.getText().length() == 0) {
//                        invoiceItem[0].setSasia("0");
//                    } else {
//                        invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
//                        if (invoiceItem[0].isAction() && sasia >= invoiceItem[0].getMinQuantityForDiscount()) {
//                            invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
//                            double totalDiscount = Double.parseDouble(invoiceItem[0].getDiscount()) + Double.parseDouble(invoiceItem[0].getExtraDiscount());
//                            invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
//                        } else {
//                            invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
//                        }
//                    }
//                    fillInvoiceItemData(itemBinding, invoiceItem[0]);
//                } else {
//                    invoiceItem[0].setSasia("0");
//                    if (!itemBinding.sasiaTextview.getText().toString().equals("0") && !itemBinding.sasiaTextview.getText().toString().equals("") && !itemBinding.sasiaTextview.getText().toString().isEmpty()) {
//                        Toast.makeText(getApplicationContext(), "Nuk keni stok te mjaftueshem", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        itemBinding.sasiaTextview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (!b) {
//                    binding.loader.setVisibility(View.VISIBLE);
//
//                    String stockItemId = invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getId();
//                    double sasia1 = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
//
//
//                    CheckQuantity checkQuantity = new CheckQuantity(preferences.getUserId(), preferences.getToken(), sasia1 + "", preferences.getStationId(), dDate, stockItemId);
//
//                    if (sasia1 > 0f) {
//                        apiService.checkQuantity(checkQuantity).subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(invoiceUploadResponse -> {
//                                    binding.loader.setVisibility(View.GONE);
//
//                                    if (!invoiceUploadResponse.getSuccess()) {
//                                        Toast.makeText(getApplicationContext(), invoiceUploadResponse.getError().getText(), Toast.LENGTH_SHORT).show();
//
//                                    } else {
//                                        double sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
//
//                                        if (itemBinding.sasiaTextview.getText().length() > 0) {
//                                            sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
//                                        }
//                                        double availableQuantity = 0;
//
//                                        if (itemBinding.sasiaTextview.getText().length() == 0) {
//                                            invoiceItem[0].setSasia("0");
//                                        } else {
//                                            invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
//                                            if (invoiceItem[0].isAction() && sasia >= invoiceItem[0].getMinQuantityForDiscount()) {
//                                                invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
//                                                double totalDiscount = Double.parseDouble(invoiceItem[0].getDiscount()) +
//                                                        Double.parseDouble(invoiceItem[0].getExtraDiscount());
//                                                invoiceItem[0].setDiscount(String.valueOf(totalDiscount));
//                                            } else {
//                                                invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
//                                            }
//                                        }
//                                        fillInvoiceItemData(itemBinding, invoiceItem[0]);
//                                    }
//                                    binding.loader.setVisibility(View.GONE);
//
//                                }, new Action1<Throwable>() {
//                                    @Override
//                                    public void call(Throwable throwable) {
//                                        binding.loader.setVisibility(View.GONE);
//
//                                        throwable.printStackTrace();
//                                    }
//                                });
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Jipeni sasin", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                double sasia = 0;
//                if (itemBinding.sasiaTextview.getText().length() > 0) {
//                    sasia = Double.parseDouble(itemBinding.sasiaTextview.getText().toString());
//                }
//                double availableQuantity = 0;
//                availableQuantity = Double.parseDouble(invoiceItem[0].getQuantity()) / invoiceItem[0].getItems().get(invoiceItem[0].getSelectedPosition()).getRelacion();
//
//                if (sasia <= availableQuantity && sasia > 0f) {
//                    if (itemBinding.sasiaTextview.getText().length() == 0) {
//                        invoiceItem[0].setSasia("0");
//                    } else {
//                        invoiceItem[0].setSasia(itemBinding.sasiaTextview.getText().toString());
//                        if (invoiceItem[0].isAction() && sasia >= invoiceItem[0].getMinQuantityForDiscount()) {
//                            invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
//                            double totalDiscount = Double.parseDouble(invoiceItem[0].getDiscount())
//                                    + Double.parseDouble(invoiceItem[0].getExtraDiscount());
//                        } else {
//                            invoiceItem[0].setDiscount(invoiceItem[0].getBaseDiscount());
//                        }
//                    }
//                    fillInvoiceItemData(itemBinding, invoiceItem[0]);
//                } else {
//                    invoiceItem[0].setSasia("0");
//                    itemBinding.sasiaTextview.setText("0");
//                }
//            }
//        });

        itemBinding.removeButton.setOnClickListener(view ->
        {
            doYouWantToDeleteThisArticleDialog(itemBinding.emertimiTextview.getText().toString(), itemBinding.sasiaTextview.getText().toString(), () -> {

                int pos = (int) itemBinding.getRoot().getTag();
                if (stockItems.size() > 0) {
                    try {
                        stockItems.remove(pos);
                    } catch (Exception e) {

                    }
                }
                binding.invoiceItemHolder.removeView(itemBinding.getRoot());
                calculateTotalQuantity();
               calculateTotalOfArticles();
            });
        });
        itemBinding.getRoot().setTag(binding.invoiceItemHolder.getChildCount());
        binding.invoiceItemHolder.addView(itemBinding.getRoot());
    }

    private void checkedQuantity() {
        binding.loader.setVisibility(View.VISIBLE);
        if (stockItems.size() > 0) {
            InvoiceItem stock = stockItems.get(stockItems.size() - 1);
            String stockItemId = stockItems.get(stockItems.size() - 1).getItems().get(stockItems.get(stockItems.size() - 1).getSelectedPosition()).getId();
            CheckQuantity checkQuantity = new CheckQuantity(preferences.getUserId(), preferences.getToken(), stock.getSasia(), stationID, dDate, stockItemId);
            apiService.checkQuantity(checkQuantity).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(invoiceUploadResponse -> {

                        final int childCount = binding.invoiceItemHolder.getChildCount();
                        ViewGroup v = (ViewGroup) binding.invoiceItemHolder.getChildAt(childCount - 1);

                        ViewGroup v2 = (ViewGroup) v.getChildAt(0);
                        ViewGroup v3 = (ViewGroup) v2.getChildAt(0);
                        ViewGroup v4 = (ViewGroup) v3.getChildAt(1);
                        ViewGroup v5 = (ViewGroup) v4.getChildAt(1);
                        View v6 = v5.getChildAt(1);

                        AutoCompleteTextView sasia_depo = (AutoCompleteTextView) v6;
                        sasia_depo.setText(String.valueOf(cutTo2(invoiceUploadResponse.getCurrentQuantity())));

                        if (!invoiceUploadResponse.getSuccess()) {
                            Toast.makeText(getApplicationContext(), invoiceUploadResponse.getError().getText(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.artikulli_eshte_ne_stok, Toast.LENGTH_SHORT).show();
                        }
                        binding.loader.setVisibility(View.GONE);
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        } else {
            binding.loader.setVisibility(View.GONE);
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
            Toast.makeText(getApplicationContext(), R.string.ju_lutem_zgjedhni_produktin, Toast.LENGTH_SHORT).show();
        }
    }

    private void doYouWantToDeleteThisArticleDialog(String name, String sasia, DoYouWantToDeleteThisArticleListener doYouWantToDeleteThisArticleListener){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("");
        String message = getString(R.string.do_you_want_to_delete_this_article) + " " + name + " me sasi " + sasia;
        builder.setMessage(message);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doYouWantToDeleteThisArticleListener.Yes();
                dialog.cancel();
            }
        });
        builder.show();
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
        if (client != null) {
            partie_id = client.getId();
            if (stationPos != null) {
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
                        Toast.makeText(getApplicationContext(), R.string.porosia_u_krye_me_sukses, Toast.LENGTH_SHORT).show();
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

                        if (preferences.getDefaultWarehouse().isEmpty() || preferences.getDefaultWarehouse().equals("")) {
                            stationID = varehouses.get(0).getId();
                        } else {
                            stationID = preferences.getDefaultWarehouse();
                            for (int i = 0; i < varehouses.size(); i++) {
                                if (varehouses.get(i).getId().equals(stationID)) {
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

    public void calculateTotalOfArticles() {
        int arcTotal = 0;
        for (int i = 0; i < stockItems.size(); i++) {

            String cap = stockItems.get(i).getName().trim();

            if (cap.length() > 0) {
                arcTotal++;
            }
        }
        this.nrArtikujtTotal = String.valueOf(cutTo2(arcTotal));
        binding.artikujTeZgjedhur.setText("Nr. i artikujve te zgjedhur : " + arcTotal);
    }

    public void calculateTotalQuantity() {
        double quaTotal = 0;
        for (int i = 0; i < stockItems.size(); i++) {
            quaTotal += Double.parseDouble(stockItems.get(i).getSasia());
        }
        this.sasiaTotale = String.valueOf(cutTo2(quaTotal));
        binding.artikujtSasiaTotale.setText("Sasia totale : " + cutTo2(quaTotal));
    }


    public double cutTo2(double value){
        return Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", value));
    }

    interface DoYouWantToDeleteThisArticleListener{
        void Yes();
    }
}

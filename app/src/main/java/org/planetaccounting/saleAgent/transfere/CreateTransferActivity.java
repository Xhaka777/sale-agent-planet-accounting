package org.planetaccounting.saleAgent.transfere;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityCreateTransferBinding;
import org.planetaccounting.saleAgent.databinding.OrderItemBinding;
import org.planetaccounting.saleAgent.model.InvoiceItem;
import org.planetaccounting.saleAgent.model.Varehouse;
import org.planetaccounting.saleAgent.model.VarehouseReponse;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.model.transfer.TransferCreate;
import org.planetaccounting.saleAgent.model.transfer.TransferCreateItem;
import org.planetaccounting.saleAgent.model.transfer.TransferCreatePost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CreateTransferActivity extends AppCompatActivity {
    private ActivityCreateTransferBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;

    @Inject
    RealmHelper realmHelper;
    ArrayList<InvoiceItem> stockItems = new ArrayList<>();
    String fDate;
    String dDate;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;

    Integer stationPos;
    int checked = 0;
    List<Varehouse> varehouses = new ArrayList<>();
    String stationID = "2";
    String description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_transfer);
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

        binding.shtoTextview.setOnClickListener(view -> addTransferItem());
        binding.createButton.setOnClickListener(view -> {

            if (stockItems.size() > 0) {
                descriptionDialog();
                    } else {
                Toast.makeText(getApplicationContext(), "Shtoni së paku një artikull!", Toast.LENGTH_SHORT).show();
            }
        } );
        binding.dataLinar.setOnClickListener(v -> getdata() );

        binding.depoEdittext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stationID = varehouses.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
    private void shopDropDownList() {
        binding.depoEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.depoEdittext.showDropDown();
                return false;
            }
        });


    }

    private void getdata() {

        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
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


    private void addTransferItem() {

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

    private void createTransfer() {
        binding.loader.setVisibility(View.VISIBLE);
        ArrayList<TransferCreateItem> transferCreateItems = new ArrayList<>();
        for (int i = 0; i < stockItems.size(); i++) {
            transferCreateItems.add(new TransferCreateItem( stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getId(),
                    stockItems.get(i).getId(),
                    stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getUnit(),
                    stockItems.get(i).getItems().get(stockItems.get(i).getSelectedPosition()).getQuantity()
            ));
        }

        TransferCreate transferCreate = new TransferCreate(stationID,
                description,
                transferCreateItems);
        TransferCreatePost transferCreatePost= new TransferCreatePost(preferences.getUserId(), preferences.getToken(),transferCreate);
        apiService.createTransfer(transferCreatePost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferCreateResposnse -> {
                    if (!transferCreateResposnse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), transferCreateResposnse.getError().getText(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Transferi u dergua per shqyrtim!", Toast.LENGTH_SHORT).show();
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

    void dialog(InvoiceItem invoiceItem, OrderItemBinding binding) {
        try {
            String[] units = realmHelper.getItemUnits(invoiceItem.getName());
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(CreateTransferActivity.this);
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

    public void descriptionDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Pershkrimi i Transferit");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
        input.setHint("shkruaj nje pershkrim(Opcionale)");
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Dergo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                description = input.getText().toString();
                createTransfer();
            }
        });
        builder.setNegativeButton("Anulo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }



    private void getVareHouses() {
        apiService.getWareHouses(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VarehouseReponse>() {
                    @Override
                    public void call(VarehouseReponse varehouseReponse) {
                        varehouses = varehouseReponse.getStations();
                        stationID = varehouses.get(0).getId();
                        String[] stations = new String[varehouses.size()];
                        for (int i = 0; i < varehouses.size(); i++) {
                            stations[i] = varehouses.get(i).getName();
                        }
                        binding.depoEdittext.setAdapter(new ArrayAdapter<String>(CreateTransferActivity.this,
                                android.R.layout.simple_dropdown_item_1line, stations));
                    }
                }, Throwable::printStackTrace);
    }
}

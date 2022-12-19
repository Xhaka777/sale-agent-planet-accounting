package org.planetaccounting.saleAgent.clients;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.planetaccounting.saleAgent.DepozitaActivity;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ClientCardLayoutBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.clients.CardItem;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.clients.ClientCardPost;
import org.planetaccounting.saleAgent.utils.ClientCardPrintUtil;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by planetaccounting on 16/12/17.
 */

public class ClientsDetailActivity extends Activity implements DatePickerDialog.OnDateSetListener {
    ClientCardLayoutBinding binding;
    Client client;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;

    ClientCardAdapter adapter;
    List<CardItem> cardItems = new ArrayList<>();
    private PrintManager printManager;
    DatePickerDialog datePickerDialog;
    boolean selectFrom = true;
    String nga = "";
    String deri = "";

    String fDate;
    String dDate;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, org.planetaccounting.saleAgent.R.layout.client_card_layout);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        client = getIntent().getParcelableExtra("client");
        setupData(client);
        getClientCard();
        adapter = new ClientCardAdapter();
        binding.articleRecyler.setAdapter(adapter);
        binding.articleRecyler.setLayoutManager(new LinearLayoutManager(this));
        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        binding.printButton.setOnClickListener(view -> {
            ClientCardPrintUtil print = new ClientCardPrintUtil(cardItems, binding.web,
                    getApplicationContext(), client, printManager);
        });

        Date cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
        binding.deri.setText(getString(R.string.to) + " " + fDate);
        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fDate = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
                binding.deri.setText(getString(R.string.to) + " " + fDate);
            }
        };

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(
                ClientsDetailActivity.this, this, year, month, day);
        binding.nga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFrom = true;
                datePickerDialog.show();
            }
        });
        binding.deri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFrom = false;
                datePickerDialog.show();
            }
        });

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
            Toast.makeText(ClientsDetailActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    @SuppressLint("SetTextI18n")
    private void setupData(Client client) {
        Glide.with(getApplicationContext()).load(client.getLogo()).into(binding.companyLogo);
        binding.emriKlientit.setText(client.getName().toUpperCase());
        binding.bilanciTextview.setText(getString(R.string.bilanci) + " :  " + cutTo2(Double.parseDouble(client.getBalance())));
        binding.idTextview.setText("ID: " + client.getId());
        binding.idTextview.setText("Nr. K: " + client.getNumber());
        binding.nrtvshTextview.setText(getString(R.string.nr_tvsh) + client.getNumberFiscal());
        binding.adresaTextview.setText(getString(R.string.adresa) + client.getAddress());
        binding.qytetiTextview.setText(getString(R.string.qyteti) + client.getCity());
        binding.shtetiTextview.setText(getString(R.string.shteti) + client.getState());
        binding.telefonTextview.setText(getString(R.string.tel) + client.getPhone());
        binding.faxTextview.setText(getString(R.string.fax) + client.getFax());
        binding.webTextview.setText(getString(R.string.web) + client.getWeb());
    }

    private void getClientCard() {
        apiService.getClientsCard(new ClientCardPost(preferences.getToken(), preferences.getUserId(), client.getId(), nga, deri))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(clientCardResponse -> {
                            this.cardItems = clientCardResponse.getCardItems();
                            adapter.setCardItems(clientCardResponse.getCardItems());
                        },
                        Throwable::printStackTrace);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (selectFrom) {
            nga = i2 + "-" + (i1 + 1) + "-" + i;
            binding.nga.setText(getString(R.string.from) + ":  " + nga);
        } else {
            deri = i2 + "-" + (i1 + 1) + "-" + i;
            binding.deri.setText(getString(R.string.to) + ":  " + deri);
        }
        getClientCard();
    }

    public double cutTo2(double value){
        return Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", value));
    }
}

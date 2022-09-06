package org.planetaccounting.saleAgent.clients;

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

import java.util.ArrayList;
import java.util.Calendar;
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

    Locale myLocale ;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

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
                    getApplicationContext(), client, printManager) ;
        });
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(
                ClientsDetailActivity.this, this, year , month, day);
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

    public void setLocale(String localeName){
        if(!localeName.equals(currentLang)){
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
        }else{
            Toast.makeText(ClientsDetailActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }

//    public void onBackPressed(){
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//        System.exit(0);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    private void setupData(Client client) {
        Glide.with(getApplicationContext()).load("http://" + client.getLogo()).into(binding.companyLogo);
        binding.emriKlientit.setText(client.getName().toUpperCase());
        binding.bilanciTextview.setText(R.string.bilanci+"\n" + client.getBalance());
        binding.idTextview.setText("ID: " + client.getId());
        binding.idTextview.setText("Nr. K: " + client.getNumber());
        binding.nrtvshTextview.setText(R.string.nr_tvsh + client.getNumberFiscal());
        binding.adresaTextview.setText(R.string.adresa + client.getAddress());
        binding.qytetiTextview.setText(R.string.qyteti + client.getCity());
        binding.shtetiTextview.setText(R.string.shteti + client.getState());
        binding.telefonTextview.setText(R.string.tel + client.getPhone());
        binding.faxTextview.setText(R.string.fax + client.getFax());
        binding.webTextview.setText(R.string.web + client.getWeb());
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


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if(selectFrom){
            nga = i+"-"+(i1+1)+"-"+i2;
            binding.nga.setText(R.string.from + nga);
        }else{
            deri = i+"-"+(i1+1)+"-"+i2;
            binding.deri.setText(R.string.to + deri);
        }
        getClientCard();
    }
}

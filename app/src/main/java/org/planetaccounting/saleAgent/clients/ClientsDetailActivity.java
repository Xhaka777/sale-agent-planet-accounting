package org.planetaccounting.saleAgent.clients;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;

import org.planetaccounting.saleAgent.DepozitaActivity;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ClientCardLayoutBinding;
import org.planetaccounting.saleAgent.model.clients.CardItem;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.clients.ClientCardPost;
import org.planetaccounting.saleAgent.utils.ClientCardPrintUtil;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    }


    private void setupData(Client client) {
        Glide.with(getApplicationContext()).load("http://" + client.getLogo()).into(binding.companyLogo);
        binding.emriKlientit.setText(client.getName().toUpperCase());
        binding.bilanciTextview.setText("Bilanci: \n" + client.getBalance());
        binding.idTextview.setText("ID: " + client.getId());
        binding.idTextview.setText("Nr. K: " + client.getNumber());
        binding.nrtvshTextview.setText("Nr. TVSH-së: " + client.getNumberFiscal());
        binding.adresaTextview.setText("Adresa: " + client.getAddress());
        binding.qytetiTextview.setText("Qyteti: " + client.getCity());
        binding.shtetiTextview.setText("Shteti: " + client.getState());
        binding.telefonTextview.setText("Tel: " + client.getPhone());
        binding.faxTextview.setText("Fax: " + client.getFax());
        binding.webTextview.setText("Web: " + client.getWeb());
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
            binding.nga.setText("Nga: "+ nga);
        }else{
            deri = i+"-"+(i1+1)+"-"+i2;
            binding.deri.setText("Deri: "+ deri);
        }
        getClientCard();
    }
}

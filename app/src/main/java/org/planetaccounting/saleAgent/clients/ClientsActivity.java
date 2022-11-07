package org.planetaccounting.saleAgent.clients;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.clients.infos.FragmentAdapter;
import org.planetaccounting.saleAgent.databinding.ClientLocationLayoutBinding;
import org.planetaccounting.saleAgent.databinding.ClientUnitItemBinding;
import org.planetaccounting.saleAgent.databinding.ClientsActivityLayoutBinding;
import org.planetaccounting.saleAgent.databinding.ClientsListItemBinding;
import org.planetaccounting.saleAgent.databinding.InvoiceItemBinding;
import org.planetaccounting.saleAgent.events.OpenClientsCardEvent;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.clients.ClientsResponse;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by planetaccounting on 13/12/17.
 */

public class ClientsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ClientsActivityLayoutBinding binding;
    ClientLocationLayoutBinding bindingLocation;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    RealmResults<Client> clients;
    ArrayList<Client> searchResults = new ArrayList<>();

    private Context ctx;
    String stationID = "2";

    private DatePickerDialog.OnDateSetListener dateSh;
    private Calendar calendar;
    private java.util.Timer timer;
    String fDate;
    String dDate;
    String shDate;


    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    TabLayout tabLayout ;
    ViewPager viewPager;
    FragmentAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.clients_activity_layout);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        clients = realmHelper.getClients();

        //pjesa per paraqitjen e dates aktuale kur krijojme kliente...
        Date cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
        binding.dataEdittext.setText(fDate);
        calendar = Calendar.getInstance();
        dateSh = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fDate = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
                binding.dataEdittext.setText(fDate);
            }
        };

        binding.dataEdittext.setOnClickListener(v -> getData());

        binding.numriKlientit.setText(preferences.getEmployNumber() + "-");


        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.sliding_tabs);

        viewPagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        //connect TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager);


    }

    //method for data (Calendar)

    private void getData(){
        new DatePickerDialog(getApplicationContext(), dateSh, calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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
            Toast.makeText(ClientsActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(OpenClientsCardEvent event) {
        Intent i = new Intent(getApplicationContext(), ClientsDetailActivity.class);
        i.putExtra("client", event.getClient());
        startActivity(i);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }




//    private void getClients() {
//        apiService.getClients(new StockPost(preferences.getToken(), preferences.getUserId()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(clientsResponse -> {
//                            adapter.setClients(clientsResponse.getClients());
//                            realmHelper.saveClients(clientsResponse.getClients());
//                        },
//                        Throwable::printStackTrace);
//    }

}

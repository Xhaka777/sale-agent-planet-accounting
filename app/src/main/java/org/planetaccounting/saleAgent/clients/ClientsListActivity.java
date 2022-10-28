package org.planetaccounting.saleAgent.clients;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityClientsListBinding;
import org.planetaccounting.saleAgent.events.OpenClientsCardEvent;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ClientsListActivity extends AppCompatActivity {

    ActivityClientsListBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    ClientsListAdapter adapter;
    RealmResults<Client> clients;
    ArrayList<Client> searchResults = new ArrayList<>();

    Locale locale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_clients_list);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        clients = realmHelper.getClients();

        //pjesa ku i qon data adapterit (nderlidhje adapter me recyclerview)
        adapter = new ClientsListAdapter(clients);
        binding.articleRecyler.setAdapter(adapter);
        binding.articleRecyler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setClients(clients);

        binding.searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchResults.clear();
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).getName().toLowerCase().startsWith(s.toString().toLowerCase())) {
                        searchResults.add(clients.get(i));
                    }
                }
                if (s.length() > 0) {
                    adapter.setClients(searchResults);
                    adapter = new ClientsListAdapter(ClientsListActivity.this, searchResults);
                    binding.articleRecyler.setAdapter(adapter);
                } else {
                    adapter.setClients(clients);
                    adapter = new ClientsListAdapter(ClientsListActivity.this, clients);
                    binding.articleRecyler.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getClients();

        currentLanguage = getIntent().getStringExtra(currentLang);

    }

    //setLocale is a methid that chages the lanf of the client list...

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLang)) {
            Context context = LocaleHelper.setLocale(this, localeName);
            //Resources res
            locale = new Locale(localeName);
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            resources.updateConfiguration(configuration, dm);
            configuration.locale = locale;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(currentLang, localeName);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

    //attach the LocaleHelper class here
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe
    public void onEvent(OpenClientsCardEvent event) {
        Intent i = new Intent(getApplicationContext(), ClientsDetailActivity.class);
        i.putExtra("client", event.getClient());
        startActivity(i);
    }

    private void getClients() {
        apiService.getClients(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(clientsResponse -> {
                            adapter.setClients(clientsResponse.getClients());
                            realmHelper.saveClients(clientsResponse.getClients());
                        }, Throwable::printStackTrace
                );

    }
}
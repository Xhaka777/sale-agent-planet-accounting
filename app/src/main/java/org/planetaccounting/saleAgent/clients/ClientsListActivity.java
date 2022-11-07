package org.planetaccounting.saleAgent.clients;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityClientsListBinding;
import org.planetaccounting.saleAgent.databinding.ClientsActivityLayoutBinding;
import org.planetaccounting.saleAgent.events.OpenClientsCardEvent;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;

import javax.inject.Inject;

import io.realm.RealmResults;

public class ClientsListActivity extends Activity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clients_list);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        clients = realmHelper.getClients();

        adapter = new ClientsListAdapter();
        binding.articleRecyler.setAdapter(adapter);
        binding.articleRecyler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setClients(clients);
        binding.searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchResults.clear();
                for (int j = 0; j < clients.size(); j++) {
                    if (clients.get(j).getName().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        searchResults.add(clients.get(j));
                    }
                }
                if (charSequence.length() > 0) {
                    adapter.setClients(searchResults);
                } else {
                    adapter.setClients(clients);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

//    private void getClients() {
//        apiService.getClients(new StockPost(preferences.getToken(), preferences.getUserId()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(clientsResponse -> {
//                            adapter.setClients(clientsResponse.getClients());
//                            realmHelper.saveClients(clientsResponse.getClients());
//                },
//                Throwable::printStackTrace);
//    }
}
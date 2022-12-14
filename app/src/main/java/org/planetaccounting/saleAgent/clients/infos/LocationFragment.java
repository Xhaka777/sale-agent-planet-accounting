package org.planetaccounting.saleAgent.clients.infos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ClientLocationLayoutBinding;
import org.planetaccounting.saleAgent.databinding.ClientUnitItemBinding;
import org.planetaccounting.saleAgent.databinding.InvoiceItemBinding;
import org.planetaccounting.saleAgent.model.VarehouseReponse;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.clients.ClientsResponse;
import org.planetaccounting.saleAgent.model.clients.Station;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LocationFragment extends Fragment {

    ClientLocationLayoutBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    Context context;
    List<Client> client = new ArrayList<>();
    Client clientS;


    public LocationFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//      View view = LayoutInflater.from(getContext()).inflate(R.layout.client_location_layout, null);
        binding = DataBindingUtil.inflate(inflater, R.layout.client_location_layout, container, false);
        View rootView = binding.getRoot();

        binding.shtoTextview.setOnClickListener(view -> addClientLocationItem());

        return rootView;
//        return inflater.inflate(R.layout.client_location_layout, container, false);
    }

    //Pjesa per me vendose per role..
    private void setClientUnitRole(ClientUnitItemBinding itemBinding) {
        //me bo kushte ne baze te nj??sis??...
    }

    private void findCodeAndPosition(Client client) {
        for (int i = 0; i < client.getStations().size(); i++) {
            if (!(client.getStations().get(i).getName().isEmpty())) {
                client.getStations().get(i).getName();
            }
        }
    }

    private void fillClientLocationData(ClientUnitItemBinding itemBinding, Client client) {
        for (int i = 0; i < client.getStations().size(); i++) {
            if (client.getStations().get(i).getName() != null) {
                itemBinding.locationUnitEdittext.setText(client.getStations().get(i).getName());
            }
            itemBinding.bilanceUnitEdittext.setText(client.getBalance());
        }
    }


    private void addClientLocationItem() {

        final Client[] clients = new Client[1];

        //itemBinding of addLocation layout...
        ClientUnitItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.client_unit_item, binding.clientUnitHolder, false);


        itemBinding.bilanceUnitEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (itemBinding.bilanceUnitEdittext.getText().length() == 0) {
                    clients[0].setBalance("0");
                } else {
                    clients[0].setBalance(itemBinding.bilanceUnitEdittext.getText().toString());
                }
                fillClientLocationData(itemBinding, clients[0]);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Pjesa per butonin remove ==>

        itemBinding.removeUnitButton.setOnClickListener(view -> {

            doYouWantToDeleteThisClientUnitDialog(() -> {

                int poss = (int) itemBinding.getRoot().getTag();
                //duhet me mbush array me clients....
                // po qet error pasi esht empty...

                if(client.size() > 0){
                    try {
                        client.remove(poss);
                    }catch (Exception e){

                    }
                }
                binding.clientUnitHolder.removeView(itemBinding.getRoot());

            });
        });
        itemBinding.getRoot().setTag(binding.clientUnitHolder.getChildCount());
        binding.clientUnitHolder.addView(itemBinding.getRoot());
    }

    private void createClientUnit() {
        binding.loader.setVisibility(View.VISIBLE);
        ArrayList<Station> createStations = new ArrayList<>();
        for (int i = 0; i < client.size(); i++) {
            createStations.add(new Station(
                    client.get(i).getStations().get(i).getId()
                    , client.get(i).getStations().get(i).getName()
                    , client.get(i).getStations().get(i).getVillage()
                    , client.get(i).getStations().get(i).getCity()));
        }
    }

    public void doYouWantToDeleteThisClientUnitDialog(DoYouWantToDeleteThisClientUnit doYouWantToDeleteThisClientUnit) {

        //take the layout of the client_unit_item....
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(getContext());
        mBuilder.setTitle("");
        String message = "A deshironi ta fshini ? ";
        mBuilder.setTitle(message);

        mBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Write your code here to invoke No event
                dialog.cancel();
            }
        });

        mBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //Write your code here to invoke the Yes event
                doYouWantToDeleteThisClientUnit.Yes();
                dialog.cancel();
            }
        });

        //The part that shows the Alert Message
        mBuilder.show();
    }



    interface DoYouWantToDeleteThisClientUnit {
        void Yes();
    }
}

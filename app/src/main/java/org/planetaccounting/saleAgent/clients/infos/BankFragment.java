package org.planetaccounting.saleAgent.clients.infos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ClientBankItemBinding;
import org.planetaccounting.saleAgent.databinding.ClientBankLayoutBinding;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;

import javax.inject.Inject;

public class BankFragment extends Fragment {

    ClientBankLayoutBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    Context context;
    ArrayList<Client> client = new ArrayList<>();
    int stationPos;

    public BankFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.client_bank_layout, container, false);
        View rootview = binding.getRoot();

        binding.shtoTextview.setOnClickListener(view -> addClientBankItem());
        return rootview;
    }

    private void fillClientBankData(ClientBankItemBinding itemBinding, Client client) {
//        itemBinding.clientNumAccBank.setText(client.getNumberBusniess());
          itemBinding.secondNrLlogariseEdittext.setText(client.getNumberBusniess());
    }

    private void addClientBankItem() {
        final Client[] clients = new Client[1];

        ClientBankItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.client_bank_item, binding.clientBankHolder, false);

        itemBinding.secondNrLlogariseEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (itemBinding.secondNrLlogariseEdittext.getText().length() == 0) {
                    clients[0].setNumberBusniess("0");
                } else {
                    clients[0].setNumberBusniess(itemBinding.secondNrLlogariseEdittext.getText().toString());
                }
                fillClientBankData(itemBinding, clients[0]);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Pjesa per butonin remove ==>

        itemBinding.removeBankAccButton.setOnClickListener(view -> {

            doYouWantToDeleteThisClientBankDialog(() -> {

                int pos = (int) itemBinding.getRoot().getTag();
                //duhet me mbush array me clients...
                //po qet error pasi esht empty...

                if(client.size() > 0){
                    try {
                        client.remove(pos);
                    }catch (Exception e){

                    }
                }
                binding.clientBankHolder.removeView(itemBinding.getRoot());
            });
        });

        itemBinding.getRoot().setTag(binding.clientBankHolder.getChildCount());
        binding.clientBankHolder.addView(itemBinding.getRoot());
    }

    public void doYouWantToDeleteThisClientBankDialog(DoYouWantToDeleteThisClientBank doYouWantToDeleteThisClientBank) {

        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(getContext());
        mBuilder.setTitle("");
        String message = "A deshironi te fshini ? ";
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
                doYouWantToDeleteThisClientBank.Yes();
                dialog.cancel();
            }
        });
        //The part that shows the Alert Message
        mBuilder.show();
    }

    interface DoYouWantToDeleteThisClientBank {
        void Yes();
    }
}

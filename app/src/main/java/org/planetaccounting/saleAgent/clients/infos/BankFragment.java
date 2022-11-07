package org.planetaccounting.saleAgent.clients.infos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.clients.banks.ClientCountries;
import org.planetaccounting.saleAgent.databinding.ClientBankItemBinding;
import org.planetaccounting.saleAgent.databinding.ClientBankLayoutBinding;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.util.ArrayList;
import java.util.Arrays;

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
    ArrayList<String> clientState = new ArrayList<>(Arrays.asList(ClientCountries.getCountry()));
    private static final String DEFAULT_LOCAL = "Kosovë";

    TextView textView;
    Dialog dialog;

    public BankFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.client_bank_layout, container, false);
        View rootview = binding.getRoot();
        showDropDownList();

//        binding.clientStateBank.setAdapter(new ArrayAdapter<>(
//                getContext(), android.R.layout.simple_dropdown_item_1line, clientState));
//
//        binding.clientStateBank.setOnClickListener(view -> binding.clientStateBank.showDropDown());

//        int statePosition = (new ArrayAdapter<>(
//                getContext(), android.R.layout.simple_dropdown_item_1line, clientState)).getPosition(DEFAULT_LOCAL);
//
//        binding.clientStateBank.setSelection(statePosition);

//        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_dropdown_item_1line, clientState);
//        binding.clientStateBank.setAdapter(adapter);
//        binding.clientStateBank.setSelection(adapter.getPosition(DEFAULT_LOCAL));

        textView = rootview.findViewById(R.id.clientState_bank);

        textView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {

                dialog = new Dialog(getContext());

                dialog.setContentView(R.layout.dialog_searchable_state);

                //adjust the width and height
                dialog.getWindow().setLayout(650, 800);

                WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
                wlp.gravity = Gravity.LEFT;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                dialog.getWindow().setAttributes(wlp);

//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();

                EditText editText = dialog.findViewById(R.id.stateEdit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, clientState);

                listView.setAdapter(adapter);
                listView.setSelection(adapter.getPosition(DEFAULT_LOCAL));

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        textView.setText(adapter.getItem(position));

                        dialog.dismiss();
                    }
                });
            }
        });


        binding.shtoTextview.setOnClickListener(view -> addClientBankItem());
        return rootview;
    }

    private void showDropDownList(){
        binding.clientStateBank.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clientStateBank.showDropDown();
                return false;
            }
        });
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

                if (client.size() > 0) {
                    try {
                        client.remove(pos);
                    } catch (Exception e) {

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
        String message = "A deshironi të fshini llogarinë ? ";
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

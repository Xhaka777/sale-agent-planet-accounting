package org.planetaccounting.saleAgent.clients.infos;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ClientOptionsLayoutBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.Varehouse;
import org.planetaccounting.saleAgent.model.VarehouseReponse;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.role.Main;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.vendors.VendorSaler;
import org.planetaccounting.saleAgent.vendors.VendorType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OptionsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    ClientOptionsLayoutBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    Context context;
    ArrayList<Client> client = new ArrayList<>();
    List<Varehouse> varehouses = new ArrayList<>();
    String stationID = "2";
    Date cDate;
    String fDate;

    List<VendorSaler> vendorSalers = new ArrayList<>();
    List<VendorType> vendorTypes = new ArrayList<>();

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "botom_sheet";
    TextView dateTextView;

    public OptionsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Kontabiliteti.getKontabilitetiComponent().inject(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.client_options_layout, container, false);
        View rootview = binding.getRoot();
        cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dateTextView = rootview.findViewById(R.id.clientData_options);
//        dateTextView.setText(fDate);
//        dateTextView = rootview.findViewById(R.id.clientData_options);
//        dateTextView.setText(fDate);
//        dateTextView.setOnClickListener(view -> datePickerDialog.show());
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        //nese nuk bone esht puna te context...
//        datePickerDialog = new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener)getParentFragment(), year, month, day);
//        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

        showDropDownList();

//        binding.clientDataOptions.setOnClickListener(view -> datePickerDialog.show());

        binding.clientLlogariaOptions.setAdapter(new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, realmHelper.getVendorTypeNames()
        ));

        binding.clientLlogariaOptions.setOnClickListener(view -> binding.clientLlogariaOptions.showDropDown());
        vendorTypes = realmHelper.getVendorTypes();
        vendorSalers = realmHelper.getVendorNames();

        new Handler().postDelayed(() -> binding.clientLocationEdittext.showDropDown(), 500);

        //pjesa per ndrrimin e gjuhes....
        //Ne fragment per me thirr getIntent duhet mas pari me thirr getActivity...
        currentLanguage = getActivity().getIntent().getStringExtra(currentLang);

        return rootview;
    }

    //methods to change tje languages
//    public void setLocale(String localeName){
//        if(!localeName.equals(currentLang)){
//            Context context = LocaleHelper.setLocale(getContext(), localeName);
//            //Resources recources = context.getResources();
//            myLocale = new Locale(localeName);
//            Resources res = context.getResources();
//            DisplayMetrics dm = res.getDisplayMetrics();
//            Configuration config = res.getConfiguration();
//            config.locale = myLocale;
//            res.updateConfiguration(config, dm);
//            Intent refresh = new Intent(getContext(), MainActivity.class);
//            refresh.putExtra(currentLang, localeName);
//            startActivity(refresh);
//        }else{
//            Toast.makeText(context, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
//        }
//    }

    private void getVareHouses() {
        apiService.getWareHouses(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VarehouseReponse>() {
                               @Override
                               public void call(VarehouseReponse varehouseReponse) {
                                   varehouses = varehouseReponse.getStations();

                                   if (preferences.getDefaultWarehouse().isEmpty() || preferences.getDefaultWarehouse().equals("")) {
                                       stationID = varehouses.get(0).getId();
                                   } else {
                                       stationID = preferences.getDefaultWarehouse();
                                       for (int i = 0; i < varehouses.size(); i++) {
                                           if (varehouses.get(i).getId().equals(stationID)) {
                                               binding.clientLocationEdittext.setText(varehouses.get(i).getName());
                                           }
                                       }
                                   }
                                   String[] stations = new String[varehouses.size()];
                                   for (int i = 0; i < varehouses.size(); i++) {
                                       stations[i] = varehouses.get(i).getName();
                                   }
                                   binding.clientLocationEdittext.setAdapter(new ArrayAdapter<String>(Objects.requireNonNull(getContext()),
                                           android.R.layout.simple_dropdown_item_1line, stations));
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   throwable.printStackTrace();
                               }
                           }
                );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showDropDownList() {
        binding.clientLocationEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clientLocationEdittext.showDropDown();
                return false;
            }
        });
    }

    DatePickerDialog datePickerDialog;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        System.out.println("date " + year + " " + month + " " + dayOfMonth);
//        fDate = dayOfMonth + "-" + (month + 1) + "-" + year;
//        binding.clientDataOptions.setText(fDate);
        StringBuilder sb = new StringBuilder().append(dayOfMonth).append("/").append(month + 1);
        String formattedDAte = sb.toString();
        dateTextView.setText(formattedDAte);
    }
}

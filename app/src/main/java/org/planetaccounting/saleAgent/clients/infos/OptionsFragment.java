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
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ClientOptionsLayoutBinding;
import org.planetaccounting.saleAgent.databinding.LoginActivityBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.Varehouse;
import org.planetaccounting.saleAgent.model.VarehouseReponse;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.deviceNumber.DeviceConfig;
import org.planetaccounting.saleAgent.model.empStock.EmpStockResponse;
import org.planetaccounting.saleAgent.model.empStock.Employee;
import org.planetaccounting.saleAgent.model.empStock.EmployeeStockPost;
import org.planetaccounting.saleAgent.model.login.LoginData;
import org.planetaccounting.saleAgent.model.login.LoginPost;
import org.planetaccounting.saleAgent.model.login.LoginResponse;
import org.planetaccounting.saleAgent.model.role.Main;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.shpenzimet.ShpenzimetActivity;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.vendors.VendorSaler;
import org.planetaccounting.saleAgent.vendors.VendorType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OptionsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    ClientOptionsLayoutBinding binding;
    LoginActivityBinding loginBinding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    Context context;
    ArrayList<Client> client = new ArrayList<>();
    List<Varehouse> varehouses = new ArrayList<>();
    List<Employee> employees = new ArrayList<>();
    String stationID = "2";
    String employeeID = "2";

    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
    String fDate;
    String dDate;

    List<VendorSaler> vendorSalers = new ArrayList<>();
    List<VendorType> vendorTypes = new ArrayList<>();

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "botom_sheet";

    AutoCompleteTextView agjenti;

    public OptionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Kontabiliteti.getKontabilitetiComponent().inject(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.client_options_layout, container, false);
        View rootview = binding.getRoot();

        Date cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        binding.clientDataOptions.setText(fDate);
        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fDate = new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
                binding.clientDataOptions.setText(fDate);
            }
        };

//        agjenti = (AutoCompleteTextView) Objects.requireNonNull(getView()).findViewById(R.id.clientAgjenti_edittext);
        agjenti = (AutoCompleteTextView) binding.clientAgjentiEdittext.findViewById(R.id.clientAgjenti_edittext);
        agjenti.setText(preferences.getFullName() + "");


        showDropDownList();
//        getVareHouses();
//        binding.clientDataOptions.setOnClickListener(view -> datePickerDialog.show());

        //pjesa per llojin e llogarise (dropdown tek klienti...)
        binding.clientLlogariaOptions.setAdapter(new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, realmHelper.getVendorTypeNames()
        ));
        binding.clientLlogariaOptions.setOnClickListener(view -> binding.clientLlogariaOptions.showDropDown());

        //pjesa per lokacion (dropdown tek klienti...)
        binding.clientLocationEdittext.setAdapter(new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, realmHelper.getVarehouseName()
        ));
        binding.clientLocationEdittext.setOnClickListener(view -> binding.clientLocationEdittext.showDropDown());


        //pjesa per paraqitjen e dates (calendarin)...
        binding.clientDataOptions.setOnClickListener(v -> getData());

        showDropDownList();
        getVareHouses();

        vendorTypes = realmHelper.getVendorTypes();
        vendorSalers = realmHelper.getVendorNames();
//        new Handler().postDelayed(() -> {
//            binding.clientLocationEdittext.requestFocus();
//            binding.clientLocationEdittext.showDropDown();
//        }, 500);

        //pjesa per ndrrimin e gjuhes....
        //Ne fragment per me thirr getIntent duhet mas pari me thirr getActivity...
        currentLanguage = getActivity().getIntent().getStringExtra(currentLang);

        return rootview;
    }

    //    methods to change tje languages
    public void setLocale(String localeName) {
        if (!localeName.equals(currentLang)) {
            Context context = LocaleHelper.setLocale(getContext(), localeName);
            //Resources recources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();
            config.locale = myLocale;
            res.updateConfiguration(config, dm);
            Intent refresh = new Intent(getContext(), MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(context, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {

        new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void getVareHouses() {

        apiService.getWareHouses(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VarehouseReponse>() {
                    @Override
                    public void call(VarehouseReponse varehouseReponse) {
                        varehouses = varehouseReponse.getStations();
                        stationID = varehouses.get(0).getId();
                        String[] stations = new String[varehouses.size()];

                        for (int i = 0; i < varehouses.size(); i++) {
                            stations[i] = varehouses.get(i).getName();
                        }
                        binding.clientLocationEdittext.setAdapter(new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, stations));
                    }
                }, Throwable::printStackTrace);
    }

    //this part is for showing DropDown when clicked editText for second time and more...
    private void showDropDownList() {
        binding.clientLocationEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clientLocationEdittext.showDropDown();
                return false;
            }
        });
        binding.clientLlogariaOptions.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.clientLlogariaOptions.showDropDown();
                return false;
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}

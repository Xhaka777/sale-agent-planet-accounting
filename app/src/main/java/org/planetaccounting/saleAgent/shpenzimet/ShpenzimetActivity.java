package org.planetaccounting.saleAgent.shpenzimet;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import org.planetaccounting.saleAgent.DepozitaActivity;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityShpenzimetLayoutBinding;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.vendors.VendorPost;
import org.planetaccounting.saleAgent.vendors.VendorPostObject;
import org.planetaccounting.saleAgent.vendors.VendorSaler;
import org.planetaccounting.saleAgent.vendors.VendorType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ShpenzimetActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ActivityShpenzimetLayoutBinding binding;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    int salerPos = 1000;
    int typePos;
    Date cDate;
    String fDate;
    List<VendorSaler> vendorSalers = new ArrayList<>();
    List<VendorType> vendorTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shpenzimet_layout);
        cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        binding.data.setText(fDate);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(
                ShpenzimetActivity.this, this, year , month, day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        binding.data.setOnClickListener(view -> datePickerDialog.show());

        shopDropDownList();
        binding.buttonInkaso.setOnClickListener(view -> {
            if (salerPos < 1000 && binding.shumaEditText.getText().length() > 0) {
                postVendor();
            } else {
                Toast.makeText(getApplicationContext(), "Ju lutem plotesoni te gjitha fushat", Toast.LENGTH_SHORT).show();
            }
        });
        binding.furnitoriEdittext.setOnItemClickListener((adapterView, view, i, l) -> salerPos = i);
        binding.furnitoriEdittext.setOnItemClickListener((adapterView, view, i, l) -> salerPos = i);
        binding.furnitoriEdittext.setAdapter(new ArrayAdapter<>(
                ShpenzimetActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                realmHelper.getVendorSalersName()));
        binding.tipiEdittext.setAdapter(new ArrayAdapter<>(
                ShpenzimetActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                realmHelper.getVendorTypeNames()));
        new Handler().postDelayed(() -> binding.furnitoriEdittext.showDropDown(), 500);
        binding.tipiEdittext.setOnClickListener(view -> binding.tipiEdittext.showDropDown());
        vendorTypes = realmHelper.getVendorTypes();
        vendorSalers = realmHelper.getVendorNames();
    }


    public void postVendor() {
        List<VendorPost> vendorPosts = new ArrayList<>();
        vendorPosts.add(getVendorPost());
        VendorPostObject vendorPostObject = new VendorPostObject(preferences.getToken(), preferences.getUserId(), vendorPosts);
        apiService.postVendor(vendorPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    System.out.println("post " + response.getSuccess());
                    if (response.getSuccess()) {
                        Toast.makeText(this, "Shpenzimi u ruajt me sukses ne server!", Toast.LENGTH_SHORT).show();
                        vendorPosts.get(0).setSynced(true);
                    } else {
                        Toast.makeText(this, "Shpenzimi nuk u rujat ne server!", Toast.LENGTH_SHORT).show();
                        vendorPosts.get(0).setSynced(false);
                    }
                    vendorPosts.get(0).setFurnitori(vendorSalers.get(salerPos).getName());
                    vendorPosts.get(0).setId(realmHelper.getAutoIncrementIfForVendor());
                    realmHelper.saveVendor(vendorPosts.get(0));
                    finish();
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(ShpenzimetActivity.this, "Shpenzimi nuk u rujat ne server!", Toast.LENGTH_SHORT).show();
                        vendorPosts.get(0).setSynced(false);
                        vendorPosts.get(0).setId(realmHelper.getAutoIncrementIfForVendor());
                        realmHelper.saveVendor(vendorPosts.get(0));
                        finish();

                    }
                });
    }

    public VendorPost getVendorPost() {

        return new VendorPost(vendorSalers.get(salerPos).getId(),
                vendorTypes.get(typePos).getAccount(),
                binding.shumaEditText.getText().toString(),
                fDate,
                binding.commentEdittext.getText().toString(),
                binding.nrFatures.getText().toString());
    }

    private void shopDropDownList(){
        binding.furnitoriEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.furnitoriEdittext.showDropDown();
                return false;
            }
        });

        binding.tipiEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.tipiEdittext.showDropDown();
                return false;
            }
        });

    }


    DatePickerDialog datePickerDialog;

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        System.out.println("date "+ i+ " "+ i1 +" "+ i2);
        fDate = i2+"-"+(i1+1)+"-"+i;
        binding.data.setText(fDate);
    }

}

package org.planetaccounting.saleAgent;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.depozita.DepositPost;
import org.planetaccounting.saleAgent.depozita.DepositPostObject;
import org.planetaccounting.saleAgent.model.BankAccount;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DepozitaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public Spinner banka;
    public String bankId;
    public EditText branch;
    public TextView data;
    public EditText shuma;
    public EditText referenca;
    public EditText komenti;
    public Button butoniProceso;
    public ArrayAdapter<String> bankAdapter;
    RealmList<BankAccount> listaBankav;

    @Inject
    RealmHelper realmHelper;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;


    public String[] bankat;
    public String[] bankidat;
    String fDate;
    Date cDate;
    RelativeLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depozita);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        try {
            listaBankav = realmHelper.getCompanyInfo().getBankAccounts();
            bankat = new String[listaBankav.size()];
            bankidat = new String[listaBankav.size()];
            for (int i = 0; i < bankat.length; i++) {
                bankat[i] = listaBankav.get(i).getName() + " - " + listaBankav.get(i).getBankAccountNumber();
                bankidat[i] = listaBankav.get(i).getId();
            }

        } catch (NullPointerException ex) {
        }
        //Mbush Te dhenat me Company bank accounts


        banka = (Spinner) findViewById(R.id.spinner2);
        bankAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, bankat);
        banka.setAdapter(bankAdapter);
        banka.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    bankId = bankidat[position];
                } catch (NullPointerException ex) {
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        branch = (EditText) findViewById(R.id.branch);
        shuma = (EditText) findViewById(R.id.shumaEdit);
        komenti = (EditText) findViewById(R.id.komentEdit);
        referenca = (EditText) findViewById(R.id.referenca);
        butoniProceso = (Button) findViewById(R.id.procesoButon);
        data = findViewById(R.id.data);
        data.setText(fDate);
        data.setOnClickListener(view -> datePickerDialog.show());
        loader = findViewById(R.id.loader);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(
                DepozitaActivity.this, this, year , month, day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        }

    public void depono(View v) {
        if (bankId == null || bankId.equals("")) {
            Toast.makeText(getApplicationContext(), "Ju lutemi zgjidhni një bankë", Toast.LENGTH_SHORT).show();
            return;
        }
        String Shuma = shuma.getText().toString();
        String Brenchi = branch.getText().toString();
        String ReferenceNr = referenca.getText().toString();
        String KomentiFin = komenti.getText().toString();

        List<DepositPost> depositPosts = new ArrayList<>();

        DepositPost depositDetailPost = new DepositPost(preferences.getToken(),
                preferences.getUserId(), bankId, fDate, Shuma, Brenchi, ReferenceNr, KomentiFin);
        depositDetailPost.setEmri_bankes(banka.getSelectedItem().toString());
        depositPosts.add(depositDetailPost);
        DepositPostObject depositPostObject = new DepositPostObject(preferences.getToken(), preferences.getUserId(), depositPosts);
        loader.setVisibility(View.VISIBLE);
        apiService.addDeposit(depositPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (responseBody.getSuccess()) {
                        Toast.makeText(getApplicationContext(), "Depozita u krye me sukses", Toast.LENGTH_SHORT).show();
                        depositDetailPost.setSynced(true);
                    } else {
                        depositDetailPost.setSynced(false);
                        Toast.makeText(getApplicationContext(), "Inkasimi u ruajt", Toast.LENGTH_SHORT).show();
                    }
                    depositDetailPost.setId(realmHelper.getAutoIncrementIfForDepozit());
                    realmHelper.saveDepozita(depositDetailPost);
                    finish();
                }, throwable -> {
                    depositDetailPost.setSynced(false);
                    Toast.makeText(getApplicationContext(), "Inkasimi u ruajt", Toast.LENGTH_SHORT).show();
                    depositDetailPost.setId(realmHelper.getAutoIncrementIfForDepozit());
                    realmHelper.saveDepozita(depositDetailPost);
                    finish();
                });

    }

    DatePickerDialog datePickerDialog;

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        System.out.println("date "+ i+ " "+ i1 +" "+ i2);
        fDate = i2+"-"+(i1+1)+"-"+i;
        data.setText(fDate);
    }
}

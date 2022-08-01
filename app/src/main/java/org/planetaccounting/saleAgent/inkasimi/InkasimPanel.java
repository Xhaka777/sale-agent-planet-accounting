package org.planetaccounting.saleAgent.inkasimi;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.clients.ClientsListAdapter;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InkasimPanel extends AppCompatActivity {

    private String Tokeni;
    private String user_id;
    private String klienti_Zgjedhur;
    private String station_id;
    private String shuma;
    private String komenti;
    String fDate;
    String dDate;
    private String[] listaemrav;
    private String[] listaStationev;
    public ArrayAdapter<String> adapter;
    public ArrayAdapter<String> adapterNjesia;
    public AutoCompleteTextView ClientList;
    public AutoCompleteTextView StationList;
    RealmResults<Client> Clientet;
    Client klienti;
    public EditText Amount;
    public EditText Comment;
    ProgressBar progressBar;
    Button inkaso;

    @Inject
    RealmHelper realmHelper;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inkasim_panel);
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        Bundle extras = getIntent().getExtras();
        Tokeni = extras.getString("token");
        user_id = extras.getString("user_id");
        Clientet = realmHelper.getClients();
        listaemrav = realmHelper.getClientsNames();

        ClientList = (AutoCompleteTextView) findViewById(R.id.spinner);
        StationList = (AutoCompleteTextView) findViewById(R.id.Spinner2);
        progressBar = findViewById(R.id.progress_bar);
        inkaso = findViewById(R.id.buttonInkaso);
        shopDropDownList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaemrav);
        ClientList.setAdapter(adapter);
        ClientList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    listaStationev = realmHelper.getClientStations(ClientList.getText().toString());
                } catch (NullPointerException ex) {
                    listaStationev = new String[0];
                }

                njesia(listaStationev);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Amount = (EditText) findViewById(R.id.amount);
        Comment = (EditText) findViewById(R.id.comment);

        Date cDate = new Date();
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);

        new Handler().postDelayed(() -> ClientList.showDropDown(), 500);
    }

    public void njesia(String[] njesiaS) {
        adapterNjesia = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, njesiaS);
        StationList.setAdapter(adapterNjesia);
    }

    public void Inkasimi(View v) {
        // nese Suksess
        klienti = realmHelper.getClientFromName(ClientList.getText().toString().split(" nrf:")[0]);//TODO: fix client name search in db with id
        String emriKlientit = klienti.getName();
        try {
            station_id = realmHelper.getClientStationIdFromName(emriKlientit, StationList.getText().toString());

        } catch (NullPointerException ex) {
            station_id = "0";
        }

        shuma = Amount.getText().toString();
        komenti = Comment.getText().toString();
        klienti_Zgjedhur = klienti.getId();
//        Toast.makeText(getApplicationContext(), "Pasi qe diti o tu e testu kisha pas qef me trus nja 20her", Toast.LENGTH_SHORT).show();
        List<InkasimiDetail> inkasimiDetails = new ArrayList<>();

        InkasimiDetail inkasimiDetail = new InkasimiDetail(klienti_Zgjedhur, station_id, shuma, fDate, komenti);
        inkasimiDetail.setKlienti(klienti.getName());
        try {
            inkasimiDetail.setNjesia(StationList.getText().toString());
        } catch (NullPointerException ex) {
            inkasimiDetail.setNjesia("");
        }
        inkasimiDetails.add(inkasimiDetail);
        progressBar.setVisibility(View.VISIBLE);
        inkaso.setVisibility(View.GONE);
        InkasimPost inkasimPost = new InkasimPost(preferences.getToken(), preferences.getUserId(), inkasimiDetails);
        apiService.postInkasimiDetail(inkasimPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (responseBody.getSuccess()) {
                        Toast.makeText(getApplicationContext(), "Inkasimi u krye me sukses", Toast.LENGTH_SHORT).show();
                        inkasimiDetail.setSynced(true);
                        getClients();
                    } else {
                        inkasimiDetail.setSynced(false);
                        Toast.makeText(this, "Inkasimi nuk u rujat ne server!", Toast.LENGTH_SHORT).show();
                    }
                    inkasimiDetail.setSynced(true);
                    inkasimiDetail.setId(realmHelper.getAutoIncrementIfForInkasim());
                    realmHelper.saveInkasimi(inkasimiDetail);
                    finish();
                }, throwable -> {
                    inkasimiDetail.setSynced(false);
                    Toast.makeText(this, "Inkasimi nuk u rujat ne server!", Toast.LENGTH_SHORT).show();
                    inkasimiDetail.setId(realmHelper.getAutoIncrementIfForInkasim());
                    realmHelper.saveInkasimi(inkasimiDetail);
                    finish();
                });
    }

    private void getClients() {
        apiService.getClients(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(clientsResponse -> {
                            realmHelper.saveClients(clientsResponse.getClients());
                            finish();
                        },
                        Throwable::printStackTrace);
    }

    private void shopDropDownList(){
        ClientList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClientList.showDropDown();
                return false;
            }
        });

        StationList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                StationList.showDropDown();
                return false;
            }
        });

    }

}

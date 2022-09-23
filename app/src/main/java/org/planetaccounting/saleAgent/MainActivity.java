package org.planetaccounting.saleAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.planetaccounting.saleAgent.aksionet.ActionActivity;
import org.planetaccounting.saleAgent.aksionet.ActionPost;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.clients.ClientsActivity;
import org.planetaccounting.saleAgent.companyimage.GetImages;
import org.planetaccounting.saleAgent.companyimage.ImageStorage;
import org.planetaccounting.saleAgent.databinding.ActivityMainBinding;
import org.planetaccounting.saleAgent.db.DatabaseOperations;
import org.planetaccounting.saleAgent.depozita.DepositPost;
import org.planetaccounting.saleAgent.depozita.DepositPostObject;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.inkasimi.InkasimPanel;
import org.planetaccounting.saleAgent.inkasimi.InkasimPost;
import org.planetaccounting.saleAgent.inkasimi.InkasimiDetail;
import org.planetaccounting.saleAgent.invoice.InvoiceActivityOriginal;
import org.planetaccounting.saleAgent.kthemallin.ReturnPostObject;
import org.planetaccounting.saleAgent.kthemallin.ktheMallin;
import org.planetaccounting.saleAgent.login.LoginActivity;
import org.planetaccounting.saleAgent.model.CompanyInfo;
import org.planetaccounting.saleAgent.model.Error;
import org.planetaccounting.saleAgent.model.ErrorPost;
import org.planetaccounting.saleAgent.model.NotificationPost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePostObject;
import org.planetaccounting.saleAgent.model.role.Main;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.ngarkime.ngarkimeActivity;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.raportet.RaportetActivity;
import org.planetaccounting.saleAgent.settings.SettingsActivity;
import org.planetaccounting.saleAgent.shpenzimet.ShpenzimetActivity;
import org.planetaccounting.saleAgent.stock.StockActivity;
import org.planetaccounting.saleAgent.target.TargetActivity;
import org.planetaccounting.saleAgent.transfere.transfereActivity;
import org.planetaccounting.saleAgent.utils.ActivityPrint;
import org.planetaccounting.saleAgent.utils.EqualSpacingItemDecoration;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.utils.Utils;
import org.planetaccounting.saleAgent.vendors.VendorPost;
import org.planetaccounting.saleAgent.vendors.VendorPostObject;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.planetaccounting.saleAgent.invoice.InvoiceActivity.ACTION;
import static org.planetaccounting.saleAgent.invoice.InvoiceActivity.ACTION_ADD_ITEMS;
import static org.planetaccounting.saleAgent.invoice.InvoiceActivity.PRINT_Z_RAPORT;

public class MainActivity extends AppCompatActivity implements MainActivityAdapter.Listener {

    ActivityMainBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    List<InvoicePost> unSyncedList = new ArrayList<>();

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";


    public static boolean isConnected = false;
    Boolean ServerConect = false;
    public String logoName = "";
    ImageView cLogo;
    Bitmap bitmap;
    TextView Njesia;
    TextView Ambulantori;
    public Boolean isServer;
    DatabaseOperations mydb;
    private Main mainRole;

    private int gmsize = 2;


    //Keshi i marr nga shitjet + Inkasimi - Shpenzimet - Depozitat = Bilanci Ditore.
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        AssetManager am = this.getApplicationContext().getAssets();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/fa-solid-900.ttf");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        mydb = new DatabaseOperations(getApplicationContext());
        mainRole = realmHelper.getRole().getMain();
        setRole();

//        if (preferences.isFromNotifications){
//            preferences.isFromNotifications = false;
//            openTransferOrder();
//        }
        checkDeviceDisplaySize();
        binding.mainRecyclerview.setHasFixedSize(true);
        binding.mainRecyclerview.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.GRID));
        binding.mainRecyclerview.setNestedScrollingEnabled(false);
        binding.mainRecyclerview.setLayoutManager(new GridLayoutManager(this, gmsize));
        binding.mainRecyclerview.setAdapter(new MainActivityAdapter(this, realmHelper.getRole()));

//        binding.faturaButton.setOnClickListener(view -> openInvoiceActivity());
//        binding.stokuButton.setOnClickListener(view -> openStockActivity());
//        binding.klientetButton.setOnClickListener(view -> openClientsActivity());
//        binding.porositeButton.setOnClickListener(view -> openOrderActivity());
//        binding.Inksimi.setOnClickListener(view -> InkasimiPanel());
        binding.mbyllDiten.setOnClickListener(view -> mbyllArken());
        binding.pregadit.setOnClickListener(view -> pregaditArken());
        binding.main.logoutButton.setOnClickListener(view -> dialog());
//        binding.kthimMalliButton.setOnClickListener(view -> openKtheMallinActivity());
//        binding.transfereButon.setOnClickListener(view -> openTransferOrder());
//        binding.ngarkimeButton.setOnClickListener(view -> openNgarkimeOrder());
//        binding.Depozit.setOnClickListener(view -> openDepozit());
//        binding.raportetButton.setOnClickListener(view -> openRaportActivity());
//        binding.targetiButton.setOnClickListener(view -> openTargetActivity());
//        binding.aksionetButton.setOnClickListener(view -> openAksionetActivity());
//        binding.shpenzimetButton.setOnClickListener(view -> openShpenzimetActivity());
        binding.pazariDitor.setOnClickListener(view -> openPazarActivity());
        binding.main.sync.setOnClickListener(view -> sync());
        binding.loader.setOnClickListener(view -> {
        });
        binding.main.settingsButton.setOnClickListener(view -> {   Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);});
        //Kontrollerat e internetit
        binding.connectivityTextivew.setTypeface(typeface);
        binding.networkIndicator.setTypeface(typeface);

        Utils.requestLocationPermission(this);

        getCategories();

        getcompanyInfo();

        cLogo = (ImageView) findViewById(R.id.cLogoImg);

        /* ---------------------------------------------------------------------------------------  */
//
//        String LogoLinku = realmHelper.getCompanyInfo().getLogo()+"";
//        if(LogoLinku.length()  < 59){
//
//        }else{
//            String PjesaF1 = LogoLinku.substring(0,48);
//            String PjesaF0 = "logos/";
//            String PjesaF2 = LogoLinku.substring(48,56);
//            String PjesaF3 = LogoLinku.substring(58,LogoLinku.length());
//            System.out.println(PjesaF1 + PjesaF0 + PjesaF2 +  PjesaF3+ "");
//            logoName =  preferences.getUserId()+"";
//            if(ImageStorage.checkifImageExists(logoName)){
//                // Debugging
//                File file = ImageStorage.getImage("/"+logoName+".jpg");
//                String path = file.getAbsolutePath();
//                if (path != null){
//                    bitmap = BitmapFactory.decodeFile(path);
//                    cLogo.setImageBitmap(bitmap);
//                }
//            } else {
//                new GetImages(PjesaF1 + PjesaF0 + PjesaF2 +PjesaF3 + "", cLogo, logoName).execute() ;
//            }
//        }
/* ---------------------------------------------------------------------------------------  */

        Njesia = (TextView) findViewById(R.id.textView3);
        Ambulantori = (TextView) findViewById(R.id.textView4);
        Njesia.setText(preferences.getStationNbame() + "");
        Ambulantori.setText(preferences.getFullName() + "");


        currentLanguage = getIntent().getStringExtra(currentLang);

        //bottom sheet design
//        ImageButton showBottomSheet = (ImageButton) findViewById(R.id.button);
//
//        showBottomSheet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BottomSheetFragment fragment = new BottomSheetFragment();
//                fragment.show(getSupportFragmentManager(), TAG);
//            }
//        });



        new AsyncTask<Boolean, Boolean, Boolean>() {

            @Override
            protected Boolean doInBackground(Boolean... voids) {
                return checkActiveInternetConnection();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                isConnected = aBoolean;
                if (aBoolean) {
                    // paisja ka internet thirr upload Faturat.
                    Log.d("Ka Internet - ", aBoolean.toString());

                    binding.networkIndicator.setTextColor(Color.parseColor("#27AE60"));
                    binding.networkIndicator.setText(getString(R.string.world_solid));


                    binding.connectivityTextivew.setTextColor(Color.parseColor("#27AE60"));
                    binding.connectivityTextivew.setText(getString(R.string.server_solid));

//                    uploadInvoices();
                } else {
                    Log.d("Ska Internet - ", aBoolean.toString());
                    binding.networkIndicator.setTextColor(Color.parseColor("#a2a2a2"));
                    binding.networkIndicator.setText(getString(R.string.world_solid));

                    binding.connectivityTextivew.setTextColor(Color.parseColor("#a2a2a2"));
                    binding.connectivityTextivew.setText(getString(R.string.server_solid));
                }
            }
        }.execute();
        Date cDate = new Date();
        dDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cDate);
        fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        try {
            CompanyInfo companyInfo = realmHelper.getCompanyInfo();
            Glide.with(getApplicationContext()).load(companyInfo.getLogo()).into(binding.cLogoImg);
            saveCompanyPic(companyInfo.getLogo());

        } catch (Exception e) {
            e.printStackTrace();
        }
        sync();
    }


    //methods to change the languages

    public void setLocale(String localeName){
        if(!localeName.equals(currentLang)){
            Context context = LocaleHelper.setLocale(this, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }else{
            Toast.makeText(MainActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

//    public void onBackPressed(){
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//        System.exit(0);
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }



    String fDate;

    public void getStockAction() {
        apiService.getStockAction(new ActionPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(actionResponse -> {
                    if (actionResponse.getSuccess()) {
                        realmHelper.saveAksionet(actionResponse.getData());
                        getVendorSalers();
                    } else {
                        hideLoader();
                        Toast.makeText(this, actionResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    sendError(throwable);
                    hideLoader();
                });


    }

    private void setRole() {
        if (mainRole.getUpdate() == 0) {
            binding.main.sync.setVisibility(View.GONE);
        } else {
            binding.main.sync.setVisibility(View.VISIBLE);
        }

        if (mainRole.getCloseFiscalArc() == 0) {
            binding.mbyllDiten.setVisibility(View.GONE);
        } else {
            binding.mbyllDiten.setVisibility(View.VISIBLE);
        }


        if (mainRole.getPreparefiscalArc() == 0) {
            binding.pregadit.setVisibility(View.GONE);
        } else {
            binding.pregadit.setVisibility(View.VISIBLE);
        }
    }

    private void openAksionetActivity() {
        Intent i = new Intent(getApplicationContext(), ActionActivity.class);
        startActivity(i);
//        Toast.makeText(getApplicationContext(), "Moduli nuk është aktiv.", Toast.LENGTH_SHORT).show();
    }

    private void openTargetActivity() {
        if (isConnected) {
            Intent i = new Intent(getApplicationContext(), TargetActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), R.string.please_connected_to_intertnet_for_make_order, Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(getApplicationContext(), "Moduli nuk është aktiv.", Toast.LENGTH_SHORT).show();
    }

    private void openRaportActivity() {
        Intent i = new Intent(getApplicationContext(), RaportetActivity.class);
        startActivity(i);
//        Toast.makeText(getApplicationContext(), "Moduli nuk është aktiv.", Toast.LENGTH_SHORT).show();
    }

    private void openDepozit() {
        Intent i = new Intent(getApplicationContext(), DepozitaActivity.class);
        i.putExtra("token", preferences.getToken());
        i.putExtra("user_id", preferences.getUserId());
        startActivity(i);
    }

    private void openShpenzimetActivity() {
        Intent i = new Intent(getApplicationContext(), ShpenzimetActivity.class);
        startActivity(i);
    }

    private void openNgarkimeOrder() {
//        Toast.makeText(getApplicationContext(), "Moduli nuk është aktiv.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), ngarkimeActivity.class);
        startActivity(i);
    }

    private void openTransferOrder() {
//        Toast.makeText(getApplicationContext(), "Moduli nuk është aktiv.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), transfereActivity.class);
        startActivity(i);
    }

    private void openKtheMallinActivity() {
//        Toast.makeText(getApplicationContext(), "Moduli nuk është aktiv.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), ktheMallin.class);
        startActivity(i);
    }

    Gson gson = new Gson();

    @Override
    protected void onResume() {
        super.onResume();
        String invoices = realmHelper.getInvoicesString();

        List<InvoicePost> savedInvoices = (ArrayList<InvoicePost>) gson.fromJson(invoices,
                new TypeToken<ArrayList<InvoicePost>>() {
                }.getType());
        unSyncedList = new ArrayList<>();


        for (int i = 0; i < savedInvoices.size(); i++) {
            if (!savedInvoices.get(i).getSynced()) {
                unSyncedList.add(savedInvoices.get(i));
            }
        }
        calculateDailyBalance();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openStockActivity() {
        Intent i = new Intent(getApplicationContext(), StockActivity.class);
        startActivity(i);

    }

    private void InkasimiPanel() {

        Intent i = new Intent(getApplicationContext(), InkasimPanel.class);
        i.putExtra("token", preferences.getToken());
        i.putExtra("user_id", preferences.getUserId());
//            i.putExtra("clientS", (RealmResults<Client>) realmHelper.getClients());

        startActivity(i);


    }


    private void openClientsActivity() {
        Log.d("Hap Klientin - ", " openClientsActivity");
        Intent i = new Intent(getApplicationContext(), ClientsActivity.class);
        startActivity(i);

    }

    private void openPazarActivity() {
        if (isConnected) {
            Intent i = new Intent(getApplicationContext(), PazariDitorActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.please_connected_to_intertnet_for_make_order), Toast.LENGTH_SHORT).show();
        }
        Log.d("Hap Klientin - ", " openClientsActivity");


    }

    private void openInvoiceActivity() {
        if (!preferences.isFaturaLocked()) {
            Log.d("Krijo faturen - ", " openInvoiceActivity");
            Intent i = new Intent(getApplicationContext(), InvoiceActivityOriginal.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), R.string.please_connected_to_intertnet_for_make_sell, Toast.LENGTH_SHORT).show();
        }

    }


    private void openOrderActivity() {
        if (isConnected) {
            Log.d("Hap Urdherat - ", " openOrderActivity");
            Intent i = new Intent(getApplicationContext(), OrdersActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.please_connected_to_intertnet_for_make_order), Toast.LENGTH_SHORT).show();
        }

    }


    public void popUpNetwork(View v) {
        if (isConnected) {
            // paisja ka internet thirr upload Faturat.
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            isConnected = false;
            if (networkInfo != null && networkInfo.isConnected()) {
                // Network is present and connected
                isConnected = true;
                binding.networkIndicator.setTextColor(Color.parseColor("#27AE60"));
                binding.networkIndicator.setText(getString(R.string.world_solid));
            }

            Toast.makeText(getApplicationContext(), R.string.device_hase_internet_connected, Toast.LENGTH_SHORT).show();
//                    uploadInvoices();
        } else {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            isConnected = false;
            binding.networkIndicator.setTextColor(Color.parseColor("#a2a2a2"));
            binding.networkIndicator.setText(getString(R.string.world_solid));
            binding.connectivityTextivew.setTextColor(Color.parseColor("#a2a2a2"));
            binding.connectivityTextivew.setText(getString(R.string.server_solid));

            if (networkInfo != null && networkInfo.isConnected()) {
                // Network is present and connected
                isConnected = true;
                binding.networkIndicator.setTextColor(Color.parseColor("#27AE60"));
                binding.networkIndicator.setText(getString(R.string.world_solid));

                binding.connectivityTextivew.setTextColor(Color.parseColor("#27AE60"));
                binding.connectivityTextivew.setText(getString(R.string.server_solid));

                //Metoda per me shiqu a ka qasje ne server
            }


            Toast.makeText(getApplicationContext(), R.string.device_hase_not_internet_connected, Toast.LENGTH_SHORT).show();
        }
    }

    public void popUpServer(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    isServer = false;
                    URL url = new URL("http://planetaccounting.org");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    if (urlConnection.getResponseCode() == 200) {
                        isServer = true;
                    } else {
                        isServer = false;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }).start();
        try {
            if (isServer) {
                binding.connectivityTextivew.setTextColor(Color.parseColor("#27AE60"));
                binding.connectivityTextivew.setText(getString(R.string.server_solid));
                //Internet
                binding.networkIndicator.setTextColor(Color.parseColor("#27AE60"));
                binding.networkIndicator.setText(getString(R.string.world_solid));
                Toast.makeText(getApplicationContext(), R.string.device_hase_internet_connected, Toast.LENGTH_SHORT).show();
            } else {

                binding.connectivityTextivew.setTextColor(Color.parseColor("#a2a2a2"));
                binding.connectivityTextivew.setText(getString(R.string.server_solid));
                //Internet
                binding.networkIndicator.setTextColor(Color.parseColor("#a2a2a2"));
                binding.networkIndicator.setText(getString(R.string.world_solid));
                Toast.makeText(getApplicationContext(), R.string.device_hase_not_internet_connected, Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException io) {

        }

    }

    private void mbyllArken() {
        Intent i = new Intent(getApplicationContext(), ActivityPrint.class);
        i.putExtra(ACTION, PRINT_Z_RAPORT);
        startActivity(i);
    }

    private void pregaditArken() {
        Intent i = new Intent(getApplicationContext(), ActivityPrint.class);
        i.putExtra(ACTION, ACTION_ADD_ITEMS);
        startActivity(i);
    }

    private void getStock() {
        apiService.getStock(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stockResponse -> {
                    if (stockResponse.getSuccess()) {
                        realmHelper.saveStockItems(stockResponse.getData().getItems());
                        getClients();
                    } else {
                        hideLoader();
                        Toast.makeText(this, stockResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    sendError(throwable);
                    hideLoader();
                });
    }


    private void getClients() {
        apiService.getClients(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(clientsResponse -> {
                    if (clientsResponse.getSuccess()) {
                        realmHelper.saveClients(clientsResponse.getClients());
                        getStockAction();
                    } else {
                        hideLoader();
                        Toast.makeText(this, clientsResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    sendError(throwable);
                    hideLoader();
                });
    }

    private void getBrands() {
        apiService.getBrands(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {

                            realmHelper.saveBrands(response.getData());
                        },
                        Throwable::printStackTrace);
    }

    private void getCategories() {
        apiService.getCategories(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            System.out.println("categories response " + response.getData().size());
                            realmHelper.saveCategories(response.getData());
                        },
                        Throwable::printStackTrace);
    }


    private void getcompanyInfo() {
        apiService.getCompanyInfo(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(clientsResponse -> {
                            Glide.with(getApplicationContext()).load(clientsResponse.getCompanyInfo().getLogo()).into(binding.cLogoImg);
                            realmHelper.saveCompanyInfo(clientsResponse.getCompanyInfo());
                            try {
                                saveCompanyPic(clientsResponse.getCompanyInfo().getLogo());
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Nuk po vjen fotoja per logo....");
                            }
                        },
                        throwable -> hideLoader());
    }

    private void logoutUser() {
        if (unSyncedList.size() > 0) {
            Toast.makeText(getApplicationContext(), R.string.please_sync_invoice, Toast.LENGTH_SHORT).show();
        } else {
            registerDevice("");
            preferences.deleteUserPrefs();
            realmHelper.removeAllData();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public boolean checkActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
//                Log.d("Krijo faturen - "," openInvoiceActivity");
                //Check Internet Connection
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();

                // kthe a ka internet
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }


    private void uploadRetruns() {


        String returns = realmHelper.getRInvoicesString();

        List<InvoicePost> unSyncedReturnList = (ArrayList<InvoicePost>) gson.fromJson(returns,
                new TypeToken<ArrayList<InvoicePost>>() {
                }.getType());

        if (unSyncedReturnList.size() > 0) {

        ReturnPostObject returnPostObject = new ReturnPostObject();
        returnPostObject.setToken(preferences.getToken());
        returnPostObject.setUser_id(preferences.getUserId());
        returnPostObject.setRetrunPost(unSyncedReturnList);
        apiService.postReturn(returnPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (responseBody.getSuccess()) {
                        for (int i = 0; i < unSyncedReturnList.size(); i++) {
                            unSyncedReturnList.get(i).setSynced(true);
                            realmHelper.saveInvoices(unSyncedReturnList.get(i));
                        }
                        uploadInvoices();
                    } else {
                        hideLoader();
                        Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    hideLoader();
                    sendError(throwable);
                });
    } else

    {
        uploadInvoices();
    }

}


    private void uploadInvoices() {

        String returns = realmHelper.getTInvoicesString();

        List<InvoicePost> unSyncedInvoicenList = (ArrayList<InvoicePost>) gson.fromJson(returns,
                new TypeToken<ArrayList<InvoicePost>>() {
                }.getType());

        if (unSyncedInvoicenList.size() > 0) {
            InvoicePostObject invoicePostObject = new InvoicePostObject();
            invoicePostObject.setToken(preferences.getToken());
            invoicePostObject.setUser_id(preferences.getUserId());
            invoicePostObject.setInvoices(unSyncedInvoicenList);
            apiService.postFaturat(invoicePostObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        if (responseBody.getSuccess()) {

                            for (int i = 0; i < unSyncedInvoicenList.size(); i++) {
                                unSyncedInvoicenList.get(i).setSynced(true);
                                realmHelper.saveInvoices(unSyncedInvoicenList.get(i));
                            }
                            syncInkasimi();
                        } else {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        hideLoader();
                        sendError(throwable);
                    });
        } else {
            syncInkasimi();
        }
    }

    //    Keshi i marr nga shitjet + Inkasimi - Shpenzimet - Depozitat = Bilanci Ditore.
    void calculateDailyBalance() {

        double dailyBalance = getCashFromSale() + getInkasimi() - getShpenzimet() - getDepozitat();
        binding.pazariDitor.setText("" + cutTo2(dailyBalance));

    }

    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH,"%.2f", value));
    }

    String dDate;

    private double getCashFromSale() {

        double amount = 0;
        RealmResults<InvoicePost> inv = realmHelper.getInvoices();
        for (int i = 0; i < inv.size(); i++) {
            String invoiceDate = inv.get(i).getInvoice_date().substring(0, 10);
            String date = dDate.substring(0, 10);
            if (invoiceDate.equalsIgnoreCase(date)) {
                amount += Double.parseDouble(inv.get(i).getAmount_payed());
            }
        }
        return amount;
    }

    private double getInkasimi() {
        double amount = 0;
        RealmResults<InkasimiDetail> inkasimiDetails = realmHelper.getInkasimi();
        for (int i = 0; i < inkasimiDetails.size(); i++) {
            String invoiceDate = inkasimiDetails.get(i).getDate().substring(0, 10);
            if (invoiceDate.equalsIgnoreCase(fDate)) {
                amount += inkasimiDetails.get(i).getAmount();
                System.out.println();
            }
        }
        return amount;
    }

    private double getShpenzimet() {
        double amount = 0;
        RealmResults<VendorPost> vendorPosts = realmHelper.getVendors();
        for (int i = 0; i < vendorPosts.size(); i++) {
            String invoiceDate = vendorPosts.get(i).getDate();
            if (invoiceDate.equalsIgnoreCase(fDate)) {
                amount += vendorPosts.get(i).getAmount();
            }
        }
        return amount;
    }

    private double getDepozitat() {
        double amount = 0;
        RealmResults<DepositPost> depozitat = realmHelper.getDepozitat();
        for (int i = 0; i < depozitat.size(); i++) {
            System.out.println("depozitat " + depozitat.get(i));
            String invoiceDate = depozitat.get(i).getDate();
            if (invoiceDate == null) {
                invoiceDate = "";
            }
            if (invoiceDate.equalsIgnoreCase(fDate)) {
                amount += depozitat.get(i).getAmount();
            }
        }
        return amount;
    }

    public void getVendorTypes() {
        apiService.getVendorTypes(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vendorTypeResponse -> {
                    if (vendorTypeResponse.getSuccess()) {
                        realmHelper.saveVendorTypes(vendorTypeResponse.getData());
                        hideLoader();
                    } else {
                        hideLoader();
                        Toast.makeText(getApplicationContext(), vendorTypeResponse.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    sendError(throwable);
                    hideLoader();
                });
    }

    public void getVendorSalers() {
        apiService.getVendorSalers(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.getSuccess()) {
                        realmHelper.saveVendorSalers(response.getData());
                        getVendorTypes();
                    } else {
                        {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), response.getError().getText(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, throwable -> {
                    sendError(throwable);
                    hideLoader();
                });
    }

    List<VendorPost> unsyncedVendor = new ArrayList<>();
    List<DepositPost> unsyncedDeposits = new ArrayList<>();
    List<InkasimiDetail> unsyncedInkasime = new ArrayList<>();

    private void sync() {
        showLoader();
        uploadRetruns();
    }

    private void syncShpenzimet() {
        String vendors = realmHelper.getVendorsString();
        List<VendorPost> savedVendors = (ArrayList<VendorPost>) gson.fromJson(vendors,
                new TypeToken<ArrayList<VendorPost>>() {
                }.getType());
        for (int i = 0; i < savedVendors.size(); i++) {
            if (!savedVendors.get(i).isSynced()) {
                unsyncedVendor.add(savedVendors.get(i));
            }
        }
        if (unsyncedVendor.size() > 0) {
            VendorPostObject vendorPostObject = new VendorPostObject(preferences.getToken(), preferences.getUserId(), unsyncedVendor);
            apiService.postVendor(vendorPostObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.getSuccess()) {
                            for (int i = 0; i < unsyncedVendor.size(); i++) {
                                unsyncedVendor.get(i).setSynced(true);
                                realmHelper.saveVendor(unsyncedVendor.get(i));
                            }
                            syncDepozitat();
                        } else {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), response.getError().getText(), Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        sendError(throwable);
                        hideLoader();
                    });
        } else {
            syncDepozitat();
        }
    }


    public void syncInkasimi() {
        String inkasimet = realmHelper.getInkasimiString();
        List<InkasimiDetail> inkasimetList = (ArrayList<InkasimiDetail>) gson.fromJson(inkasimet,
                new TypeToken<ArrayList<InkasimiDetail>>() {
                }.getType());
        for (int i = 0; i < inkasimetList.size(); i++) {
            if (!inkasimetList.get(i).isSynced()) {
                unsyncedInkasime.add(inkasimetList.get(i));
            }
        }
        if (unsyncedInkasime.size() > 0) {
            InkasimPost inkasimPost = new InkasimPost(preferences.getToken(), preferences.getUserId(), unsyncedInkasime);
            apiService.postInkasimiDetail(inkasimPost)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        if (responseBody.getSuccess()) {
                            for (int i = 0; i < unsyncedInkasime.size(); i++) {
                                unsyncedInkasime.get(i).setSynced(true);
                                realmHelper.saveInkasimi(unsyncedInkasime.get(i));
                            }
                            syncShpenzimet();
                        } else {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        sendError(throwable);
                        hideLoader();
                    });
        } else {
            syncShpenzimet();
        }
    }


    public void syncDepozitat() {
        String deposits = realmHelper.getDepositString();
        List<DepositPost> savetDeposits = (ArrayList<DepositPost>) gson.fromJson(deposits,
                new TypeToken<ArrayList<DepositPost>>() {
                }.getType());
        for (int i = 0; i < savetDeposits.size(); i++) {
            if (!savetDeposits.get(i).isSynced()) {
                unsyncedDeposits.add(savetDeposits.get(i));
            }
        }
        if (unsyncedDeposits.size() > 0) {
            DepositPostObject depositPostObject = new DepositPostObject(preferences.getToken(), preferences.getUserId(), unsyncedDeposits);
            apiService.addDeposit(depositPostObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        if (responseBody.getSuccess()) {
                            for (int i = 0; i < unsyncedDeposits.size(); i++) {
                                unsyncedDeposits.get(i).setSynced(true);
                                realmHelper.saveDepozita(unsyncedDeposits.get(i));
                            }
                            getStock();
                        } else {
                            hideLoader();
                            Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        sendError(throwable);
                        hideLoader();
                    });
        } else {
            getStock();
        }

    }

    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
        binding.loader.bringToFront();
    }

    private void hideLoader() {
        binding.loader.setVisibility(View.GONE);
    }

    private void saveCompanyPic(String src) {
        new  AsyncTask<Void, Void, Void>() {

            @Override
            protected Void  doInBackground(Void... voids) {
                System.out.println("download image ");
                InputStream in = null;
                OutputStream out = null;

                try {
                    File file = Glide.with(getApplicationContext())
                            .load(src)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    File dstFile = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/Planet Accounting Faturat/logo.png");


                    in = new BufferedInputStream(new FileInputStream(file));
                    out = new BufferedOutputStream(new FileOutputStream(dstFile));

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.flush();
                } catch (Exception e) {
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    private void sendError(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String sStackTrace = sw.toString();
        ErrorPost errorPost = new ErrorPost();
        errorPost.setToken(preferences.getToken());
        errorPost.setUser_id(preferences.getUserId());
        errorPost.setUser_id(preferences.getUserId());
        ArrayList<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setMessage(sStackTrace);
        error.setDate(fDate);
        errors.add(error);
        errorPost.setErrors(errors);
        apiService.sendError(errorPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                }, throwable1 -> {

                });
    }

    private void checkDeviceDisplaySize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int mWidthPixels = size.x;
        int mHeightPixels = size.y;

        DisplayMetrics dm = new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);

        if (x > 9) {
            gmsize = 3;
        } else if (x > 20) {
            gmsize = 4;
        }
    }

    void registerDevice(String newToken){
        apiService.deleteDevice(new NotificationPost(preferences.getToken(), preferences.getUserId(),preferences.getDeviceid(),"",""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferDetailRespose -> {
                    if (transferDetailRespose.getSuccess()) {

                    } else {
                    }
                });
    }

    public void dialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("");
        mBuilder.setMessage(R.string.do_you_want_to_logout);
        // Setting Negative "NO" Button
        mBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        mBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                logoutUser();
            }
        });
        // Showing Alert Message
        mBuilder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(int title, int positon) {
        switch (title){
            case R.string.title_fatura:openInvoiceActivity();
            break;

            case R.string.title_stoku:openStockActivity();
                break;

            case R.string.title_inkasimi:InkasimiPanel();
                break;

            case R.string.title_depozita:openDepozit();
                break;

            case R.string.title_raportet:openRaportActivity();
                break;

            case R.string.title_kthimMalli:openKtheMallinActivity();
                break;

            case R.string.title_transfere:openTransferOrder();
                break;

            case R.string.title_klientet:openClientsActivity();
                break;

            case R.string.title_target:openTargetActivity();
                break;

            case R.string.title_porositeInterne:openOrderActivity();
                break;

            case R.string.title_ngarkimet:openNgarkimeOrder();
                break;

            case R.string.title_shpenzimet:openShpenzimetActivity();
                break;

            case R.string.title_aksionet:openAksionetActivity();
                break;


        }
    }
}

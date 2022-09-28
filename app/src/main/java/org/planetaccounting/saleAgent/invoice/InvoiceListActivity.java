package org.planetaccounting.saleAgent.invoice;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.bottomsheetreport.BottomSheetReport;
import org.planetaccounting.saleAgent.databinding.ActivityBottomSheetReportBinding;
import org.planetaccounting.saleAgent.databinding.InvoiceListActivityBinding;
import org.planetaccounting.saleAgent.databinding.InvoiceListItemBinding;
import org.planetaccounting.saleAgent.escpostprint.EscPostPrintFragment;
import org.planetaccounting.saleAgent.events.RePrintInvoiceEvent;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.model.invoice.InvoicePostObject;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.raportet.raportmodels.InvoiceForReportObject;
import org.planetaccounting.saleAgent.raportet.raportmodels.RaportsPostObject;
import org.planetaccounting.saleAgent.raportet.raportmodels.ReportsList;
import org.planetaccounting.saleAgent.utils.InvoicePrintUtil;
import org.planetaccounting.saleAgent.utils.PaginationScrollListener;
import org.planetaccounting.saleAgent.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.planetaccounting.saleAgent.utils.ReturnPrintUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by macb on 31/01/18.
 */

public class InvoiceListActivity extends AppCompatActivity {

    private InvoiceListActivityBinding binding;
    private InvoiceListItemBinding bindingAdapter;
//    private InvoiceListAdapterBinding
//    private ActivityBottomSheetReportBinding bindingReport;

    @Inject
    RealmHelper realmHelper;
    @Inject
    Preferences preferences;
    @Inject
    ApiService apiService;
    RecyclerView recyclerView;
    InvoiceListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    double shuma;
    int numberFaildAttempt;
    String dDate;
    ArrayList<InvoicePost> inv;
    List<InvoicePost> invoicePosts = new ArrayList<>();
    List<InvoicePost> unSyncedList = new ArrayList<>();
    InvoicePost invoicePost;
    WebView webView;
    RelativeLayout loader;
    FrameLayout fragment;

    TextView nr_fatures;

    int totalPage = 0;
    int currentPage = 0;
    private boolean isLoading = false;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    String from; // 0->Invoice 1->Return

    Button myButton;
    View myView;
    boolean isUp;
    ArrayList<InvoicePost> invoicePostReports;
    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.invoice_list_activity);

        myView = findViewById(R.id.my_view);
        myButton = findViewById(R.id.congif_btn);

        //set the layout per config as invisible
        myView.setVisibility(View.INVISIBLE);
        myButton.setText("Konfiguro");
        isUp = false;

        from = getIntent().getStringExtra("from");
        if (from.equals("ret")) {
            TextView titleBar = (TextView) findViewById(R.id.title_bar);
            titleBar.setText(R.string.l_kthimMalli);
        }

        ((Kontabiliteti) getApplication()).getKontabilitetiComponent().inject(this);
        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        Date cDate = new Date();
        dDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);


//        ImageButton showbottomSheet = (ImageButton) findViewById(R.id.congif_btn);
//
//        showbottomSheet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                BottomSheetFReport fReport = new BottomSheetFReport();
//                BottomSheetReport report = new BottomSheetReport();
//                report.show(getSupportFragmentManager(), TAG);
//            }
//        });


        recyclerView = (RecyclerView) findViewById(R.id.invoice_list);
        webView = (WebView) findViewById(R.id.web);
        loader = findViewById(R.id.loader);
        fragment = findViewById(R.id.fragment);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        binding.invoiceNr.setVisibility(View.VISIBLE);

        if (from.equals("inv")) {

            String returns = realmHelper.getAllInvoicesString();

            inv = (ArrayList<InvoicePost>) new Gson().fromJson(returns,
                    new TypeToken<ArrayList<InvoicePost>>() {
                    }.getType());

            getInvoicesRepors();

        } else {
            String returns = realmHelper.getAllReturnInvoicesString();

            inv = (ArrayList<InvoicePost>) new Gson().fromJson(returns,
                    new TypeToken<ArrayList<InvoicePost>>() {
                    }.getType());

        }

        adapter = new InvoiceListAdapter(inv);
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < inv.size(); i++) {
            if (inv.get(i).getInvoice_date().equalsIgnoreCase(dDate)) {
                shuma += Double.parseDouble(inv.get(i).getAmount_with_vat());
            }
        }
//        TextView shuma = findViewById(R.id.totali);
//        shuma.setText("Numri i faqes " + " eshte: " + this.shuma);
//
        binding.totali.setText(preferences.getCurrentPage() + " : " + realmHelper.getAutoIncrementIfForReturn());

        String invoices = realmHelper.getInvoicesString();
        Gson gson = new Gson();
        savedInvoices = (ArrayList<InvoicePost>) gson.fromJson(invoices,
                new TypeToken<ArrayList<InvoicePost>>() {
                }.getType());
        unSyncedList = new ArrayList<>();
        for (int i = 0; i < savedInvoices.size(); i++) {
            if (!savedInvoices.get(i).getSynced()) {
                unSyncedList.add(savedInvoices.get(i));
            }
        }
        Button button = findViewById(R.id.sync);
        button.setOnClickListener(view -> uploadInvoices());

        recyclerView.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return totalPage;
            }

            @Override
            public boolean isLastPage() {
                return currentPage == totalPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        currentLanguage = getIntent().getStringExtra(currentLang);

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
            Toast.makeText(InvoiceListActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

    //slide the view from below itself to the current position
    public void slideUp(View view){

        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,0,view.getHeight(),0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    //slide th view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,0,0,view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void onSlideViewButtonclick(View view){

//        InvoiceListItemBinding bindingAdapterr = new InvoiceListItemBinding();


        if(isUp){
            slideDown(myView);
            myButton.setText("Konfig");
            binding.printSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        binding.invoiceNr.setVisibility(View.VISIBLE);
//                        bindingAdapterr.invoiceNr.setVisibility(View.VISIBLE);
                    }
                    else{
                        binding.invoiceNr.setVisibility(View.GONE);
//                        bindingAdapter.invoiceNr.setVisibility(View.GONE);
                    }
                }
            });

            binding.clientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        binding.client.setVisibility(View.VISIBLE);
//                        bindingAdapter.companyNameTextview.setVisibility(View.VISIBLE);
                    }
                    else{
                        binding.client.setVisibility(View.GONE);
//                        bindingAdapter.companyNameTextview.setVisibility(View.GONE);
                    }
                }
            });
        }else{
            slideUp(myView);
            myButton.setText("Config");
        }
        isUp = !isUp;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    List<InvoicePost> savedInvoices;

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

    private void uploadInvoices() {

        if (unSyncedList.size() > 0) {
            loader.setVisibility(View.VISIBLE);
            InvoicePostObject invoicePostObject = new InvoicePostObject();
            invoicePostObject.setToken(preferences.getToken());
            invoicePostObject.setUser_id(preferences.getUserId());
            invoicePostObject.setInvoices(savedInvoices);
            apiService.postFaturat(invoicePostObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        if (responseBody.getSuccess()) {
                            for (int i = 0; i < unSyncedList.size(); i++) {
                                unSyncedList.get(i).setSynced(true);
                                realmHelper.saveInvoices(unSyncedList.get(i));
                            }
//                            adapter.setCompanies(realmHelper.getInvoices());
                            loader.setVisibility(View.GONE);
                        } else {
                            loader.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), R.string.fatura_esht_ruajtur_por_nuk_esht_sinkronizuar, Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadNextPage() {
        isLoading = true;

        if (from.equals("inv")) {
            getInvoicesRepors();
        }
    }

    private void getInvoicesRepors() {

        RaportsPostObject raportsPostObject = new RaportsPostObject();
        raportsPostObject.setToken(preferences.getToken());
        raportsPostObject.setUser_id(preferences.getUserId());

        if (currentPage == 0) {
            if (!inv.isEmpty()) {
                raportsPostObject.setLast_document_number(inv.get(inv.size() - 1).getNo_invoice());
            }
            currentPage++;
            raportsPostObject.setPage(currentPage++);
        } else {
            raportsPostObject.setLast_document_number("");
            currentPage++;
            raportsPostObject.setPage(currentPage);
        }
        apiService.getRaportInvoiceList(raportsPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    isLoading = false;

                    if (responseBody.getSuccess()) {
                        currentPage = responseBody.getCurrentPage();
                        totalPage = responseBody.getTotalPage();

                        for (ReportsList report : responseBody.data) {
                            InvoicePost invoice = new InvoicePost();
                            invoice.setInvoiceFromReports(report);
                            inv.add(invoice);
                        }
                        adapter.notifyItemRangeInserted(0, inv.size());
                        adapter.notifyDataSetChanged();

                    } else {

                    }
                }, throwable -> {
                    isLoading = false;

                });

    }



    private void getOrderReports() {

        RaportsPostObject raportsPostObject = new RaportsPostObject();
        raportsPostObject.setToken(preferences.getToken());
        raportsPostObject.setUser_id(preferences.getUserId());

        if (currentPage == 0) {
            if (!inv.isEmpty()) {
                raportsPostObject.setLast_document_number(inv.get(inv.size() - 1).getNo_invoice());
            }
            currentPage++;
            raportsPostObject.setPage(currentPage++);
        } else {
            raportsPostObject.setLast_document_number("");
            currentPage++;
            raportsPostObject.setPage(currentPage);
        }
        apiService.getRaportOrderList(raportsPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    isLoading = false;

                    if (responseBody.getSuccess()) {
                        currentPage = responseBody.getCurrentPage();
                        totalPage = responseBody.getTotalPage();

                        for (ReportsList report : responseBody.data) {
                            InvoicePost invoice = new InvoicePost();
                            invoice.setInvoiceFromReports(report);
                            inv.add(invoice);
                        }
                        adapter.notifyItemRangeInserted(0, inv.size());
                        adapter.notifyDataSetChanged();

                    } else {

                    }
                }, throwable -> {
                    isLoading = false;

                });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printInvoices(int id, boolean isPrint) {

        InvoiceForReportObject invoiceForReportObject = new InvoiceForReportObject();
        invoiceForReportObject.setToken(preferences.getToken());
        invoiceForReportObject.setUser_id(preferences.getUserId());
        invoiceForReportObject.setId(String.valueOf(id));
        apiService.getRaportInvoiceDetail(invoiceForReportObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (responseBody.getSuccess()) {

                        invoicePost = responseBody.getData();

                        Client client = realmHelper.getClientFromName(invoicePost.getPartie_name());

                        if (from.equals("inv")) {

                            if (isPrint) {
                                InvoicePrintUtil util = new InvoicePrintUtil(invoicePost, webView, this, client, printManager);
                            } else {
                                fragment.setVisibility(View.VISIBLE);
                                for (Fragment fragment : getSupportFragmentManager().getFragments()
                                ) {
                                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                }
                                addFragment(R.id.fragment, EscPostPrintFragment.Companion.newInstace(invoicePost, preferences, realmHelper, client, invoicePost.getAmount_payed()));
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(getApplicationContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printOrder(int id) {

        InvoiceForReportObject invoiceForReportObject = new InvoiceForReportObject();
        invoiceForReportObject.setToken(preferences.getToken());
        invoiceForReportObject.setUser_id(preferences.getUserId());
        invoiceForReportObject.setId(String.valueOf(id));
        apiService.getRaportInvoiceDetail(invoiceForReportObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    if (responseBody.getSuccess()) {
                        invoicePost = responseBody.getData();
                        Client client = realmHelper.getClientFromName(invoicePost.getPartie_name());
                        ReturnPrintUtil util = new ReturnPrintUtil(invoicePost, webView, this, client, printManager);
                    } else {
                        Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(getApplicationContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                });

    }


    //    @Subscribe
//    public void onEvent(UploadInvoiceEvent event) {
//
////        InvoiceListAdapter.binding.syncedIndicator.setClickable(false);
//        if(numberFaildAttempt==0){
//            numberFaildAttempt = 1;
//            String invoice = realmHelper.getInvoiceById(event.getInvoiceId());
//            Gson gson = new Gson();
//
//            invoicePost = gson.fromJson(invoice, InvoicePost.class);
//            RealmList<InvoicePost> invoicePosts = new RealmList<>();
//            invoicePosts.add(invoicePost);
//            InvoicePostObject invoicePostObject = new InvoicePostObject();
//            invoicePostObject.setToken(preferences.getToken());
//            invoicePostObject.setUser_id(preferences.getUserId());
//            invoicePostObject.setInvoices(invoicePosts);
//
//            loader.setVisibility(View.VISIBLE);
//            apiService.postFaturat(invoicePostObject)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(responseBody -> {
//                        if(responseBody.getSuccess()) {
//                            invoicePost.setSynced(true);
//                            numberFaildAttempt=2;
//                            realmHelper.saveInvoices(invoicePost);
//                            adapter.setCompanies(realmHelper.getInvoices());
//                            loader.setVisibility(View.GONE);
//                        }
//                    else{
//                        Toast.makeText(getApplicationContext(), responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
//                            loader.setVisibility(View.GONE);
//                    }
//                    }, throwable -> {
//                        Log.d("problemi= ", throwable.getMessage());
//                        Toast.makeText(getApplicationContext(), "Nuk keni qasje ne internet", Toast.LENGTH_SHORT).show();
//                        loader.setVisibility(View.GONE);
//                    });
//        }else if(numberFaildAttempt >=1){
//            numberFaildAttempt = 0;
//            Log.d("deshtim klikimi ",numberFaildAttempt+"");
//        }
//
//    }
    private PrintManager printManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe
    public void onEvent(RePrintInvoiceEvent event) {

        if (event.getIsFromServer()) {
            if (from.equals("inv")) {
                showPrintDialog(event.getPosition());
            }
        } else {

            String invoice = realmHelper.getInvoiceById(event.getPosition());
            Gson gson = new Gson();
            invoicePost = gson.fromJson(invoice, InvoicePost.class);
            Client client = realmHelper.getClientFromName(invoicePost.getPartie_name());
//        for(int i=0; i < invoicePost.getItems().size(); i++){
//            invoicePost.getItems().get(i).setQuantity((Double.parseDouble(invoicePost.getItems().get(i).getQuantity()) / invoicePost.getItems().get(i).getRelacion())+"");
//            System.out.println(invoicePost.getItems().get(i).getQuantity().toString());
//        }

            if (from.equals("inv")) {
                showPrintDialog(new StatusClick() {
                    @Override
                    public void Printer() {
                        InvoicePrintUtil util = new InvoicePrintUtil(invoicePost, webView, InvoiceListActivity.this, client, printManager);
                    }

                    @Override
                    public void Printer80mm() {
                        fragment.setVisibility(View.VISIBLE);
                        for (Fragment fragment : getSupportFragmentManager().getFragments()
                        ) {
                            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                        addFragment(R.id.fragment, EscPostPrintFragment.Companion.newInstace(invoicePost, preferences, realmHelper, client, invoicePost.getAmount_payed()));
                    }
                });
            } else {
                ReturnPrintUtil util = new ReturnPrintUtil(invoicePost, webView, this, client, printManager);
            }

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPrintDialog(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.printing_mode_dialog, null);
        dialogBuilder.setView(dialogView);
        Button print = (Button) dialogView.findViewById(R.id.print_button);
        Button print80mm = (Button) dialogView.findViewById(R.id.print_80_button);
        LinearLayout buttonHolder = (LinearLayout) dialogView.findViewById(R.id.button_holder);


        AlertDialog alertDialog = dialogBuilder.create();

        print.setOnClickListener(view -> {
            printInvoices(id, true);
            alertDialog.dismiss();
        });
        print80mm.setOnClickListener(view -> {
            printInvoices(id, false);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPrintDialog(StatusClick listener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.printing_mode_dialog, null);
        dialogBuilder.setView(dialogView);
        Button print = (Button) dialogView.findViewById(R.id.print_button);
        Button print80mm = (Button) dialogView.findViewById(R.id.print_80_button);
        LinearLayout buttonHolder = (LinearLayout) dialogView.findViewById(R.id.button_holder);


        AlertDialog alertDialog = dialogBuilder.create();

        print.setOnClickListener(view -> {
            listener.Printer();
            alertDialog.dismiss();
        });
        print80mm.setOnClickListener(view -> {
            listener.Printer80mm();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    interface StatusClick {
        void Printer();

        void Printer80mm();
    }

    public void addFragment(int view, Fragment fragment) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(view, fragment);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.getMessage();
        }

    }



}

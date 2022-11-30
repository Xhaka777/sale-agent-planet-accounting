package org.planetaccounting.saleAgent.raportet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.adapters.AbsSpinnerBindingAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.RaportDetailActivityBinding;
import org.planetaccounting.saleAgent.depozita.DepositPost;
import org.planetaccounting.saleAgent.depozita.DepositPostObject;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.inkasimi.InkasimPost;
import org.planetaccounting.saleAgent.inkasimi.InkasimiDetail;
import org.planetaccounting.saleAgent.invoice.InvoiceListAdapter;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.raportet.raportmodels.RaportsPostObject;
import org.planetaccounting.saleAgent.raportet.raportmodels.ReportsList;
import org.planetaccounting.saleAgent.shpenzimet.ShpenzimetActivity;
import org.planetaccounting.saleAgent.utils.PaginationScrollListener;
import org.planetaccounting.saleAgent.utils.Preferences;
import org.planetaccounting.saleAgent.vendors.VendorPost;
import org.planetaccounting.saleAgent.vendors.VendorPostObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tahirietrit on 4/5/18.
 */

public class ReportDetailActivity extends Activity {
    RaportDetailActivityBinding binding;
    RaportetListAdapter adapter;

    List<VendorPost> vendorPosts = new ArrayList<>();
    List<InkasimiDetail> inkasimiDetails = new ArrayList<>();
    List<DepositPost> depositPosts = new ArrayList<>();

    List<VendorPost> unsyncedVendor = new ArrayList<>();
    List<DepositPost> unsyncedDeposits = new ArrayList<>();
    List<InkasimiDetail> unsyncedInkasime = new ArrayList<>();

    List<VendorPost> vendorSearchResults = new ArrayList<>();
    List<InkasimiDetail> inkasimiSearchResults = new ArrayList<>();
    List<DepositPost> depositSearchResults = new ArrayList<>();

    RealmResults<InkasimiDetail> inkasimiResults;


    @Inject
    RealmHelper realmHelper;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    int type;

    int totalPage = 0;
    int currentPage = 0;
    private boolean isLoading = false;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", 0);
        binding = DataBindingUtil.setContentView(this, R.layout.raport_detail_activity);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        inkasimiResults = realmHelper.getInkasimi();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(mLayoutManager);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new RaportetListAdapter(type);

        if (type == 0) {
            vendorPosts.addAll(realmHelper.getVendors());
            adapter.setVendorPosts(vendorPosts);
            getVendorRepors();
            binding.recycler.setAdapter(adapter);
            binding.title.setText(R.string.shpenzimet);
            binding.col1.setText(R.string.data);
            binding.col2.setText(R.string.furnitori);
            binding.col3.setText(R.string.lloji);
            binding.col4.setText(R.string.nr_fatures);
            binding.col5.setText(R.string.shuma);
            binding.col6.setText(R.string.komenti);
            binding.totali.setText(preferences.getCurrentPage() + " : " + realmHelper.getAutoIncrementIfForReturn());

        } else if (type == 1) {
            inkasimiDetails.addAll(realmHelper.getInkasimi());
            adapter.setInkasimiDetails(inkasimiDetails);
            getPaymentRepors();
            binding.recycler.setAdapter(adapter);

            binding.title.setText(R.string.inkasimet);
            binding.col1.setText(R.string.data);
            binding.col2.setText(R.string.klienti);
            binding.col3.setText(R.string.njesia);
            binding.col4.setVisibility(View.GONE);
            binding.col5.setText(R.string.shuma);
            binding.col6.setText(R.string.komenti);
            binding.totali.setText(preferences.getCurrentPage() + " : " + realmHelper.getAutoIncrementIfForReturn());


        } else if (type == 2) {
            depositPosts.addAll(realmHelper.getDepozitat());
            adapter.setDepositPosts(depositPosts);
            getDepositRepors();
            binding.recycler.setAdapter(adapter);

            binding.title.setText(R.string.depozitet);

            binding.col1.setText(R.string.data);
            binding.col2.setText(R.string.bank);
            binding.col3.setText(R.string.depo);
            binding.col4.setVisibility(View.GONE);
            binding.col5.setText(R.string.shuma);
            binding.col6.setText(R.string.komenti);
            binding.totali.setText(preferences.getCurrentPage() + " : " + realmHelper.getAutoIncrementIfForReturn());

        }

        binding.sync.setOnClickListener(view -> {
            System.out.println("click " + type);
            if (type == 0) {
                syncShpenzimet();
            } else if (type == 1) {
                syncInkasimi();
            } else if (type == 2) {
                syncDepozitat();
            }
        });

        if (type == 0) {

            getVendorRepors();
            binding.searchEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    vendorSearchResults.clear();

                    for (int i = 0; i < vendorPosts.size(); i++) {
                        if (vendorPosts.get(i).getFurnitori().toLowerCase().startsWith(s.toString().toLowerCase())) {
                            vendorSearchResults.add(vendorPosts.get(i));
                        }
                    }
                    if (s.length() > 0) {
                        adapter = new RaportetListAdapter(0);
                        adapter.setVendorPosts(vendorSearchResults);
                        binding.pageLayout.setVisibility(View.GONE);
                        binding.recycler.setAdapter(adapter);
                    } else if (s.length() <= 0) {
                        adapter.setVendorPosts(vendorPosts);
                        getVendorRepors();
                        binding.recycler.setAdapter(adapter);
                    } else {
                        adapter.setVendorPosts(vendorPosts);
                        adapter = new RaportetListAdapter(0);
                        binding.pageLayout.setVisibility(View.VISIBLE);
                        binding.recycler.setAdapter(adapter);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (type == 1) {

            binding.searchEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inkasimiSearchResults.clear();

                    for (int i = 0; i < inkasimiDetails.size(); i++) {
                        if (inkasimiDetails.get(i).getKlienti().toLowerCase().startsWith(s.toString().toLowerCase())) {
                            inkasimiSearchResults.add(inkasimiDetails.get(i));
                        }
                    }
                    if (s.length() > 0) {
                        adapter = new RaportetListAdapter(1);
                        adapter.setInkasimiDetails(inkasimiSearchResults);
                        binding.pageLayout.setVisibility(View.GONE);
                        binding.recycler.setAdapter(adapter);
                    } else if (s.length() <= 0) {
                        adapter.setInkasimiDetails(inkasimiDetails);
                        binding.recycler.setAdapter(adapter);
                        getPaymentRepors();
                    } else {
                        adapter.setInkasimiDetails(inkasimiDetails);
                        adapter = new RaportetListAdapter(1);
                        binding.pageLayout.setVisibility(View.VISIBLE);
                        binding.recycler.setAdapter(adapter);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        if (type == 2) {

//            getDepositRepors();
            binding.searchEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    depositSearchResults.clear();

                    for (int k = 0; k < depositPosts.size(); k++) {
                        if (depositPosts.get(k).getEmri_bankes().toLowerCase().startsWith(s.toString().toLowerCase())) {
                            depositSearchResults.add(depositPosts.get(k));
                        }
                    }

                    if (s.length() > 0) {
                        adapter = new RaportetListAdapter(2);
                        adapter.setDepositPosts(depositSearchResults);
                        binding.pageLayout.setVisibility(View.GONE);
                        binding.recycler.setAdapter(adapter);
                    } else if (s.length() <= 0) {
                        adapter.setDepositPosts(depositPosts);
                        binding.recycler.setAdapter(adapter);
                        getDepositRepors();
                    } else {
                        adapter.setDepositPosts(depositPosts);
                        adapter = new RaportetListAdapter(2);
                        binding.pageLayout.setVisibility(View.VISIBLE);
                        binding.recycler.setAdapter(adapter);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        Gson gson = new Gson();
        String vendors = realmHelper.getVendorsString();
        List<VendorPost> savedVendors = (ArrayList<VendorPost>) gson.fromJson(vendors,
                new TypeToken<ArrayList<VendorPost>>() {
                }.getType());
        unsyncedVendor = new ArrayList<>();
        for (int i = 0; i < savedVendors.size(); i++) {
            if (!savedVendors.get(i).isSynced()) {
                unsyncedVendor.add(savedVendors.get(i));
            }
        }

        String deposits = realmHelper.getDepositString();
        List<DepositPost> savedDeposits = (ArrayList<DepositPost>) gson.fromJson(deposits,
                new TypeToken<ArrayList<DepositPost>>() {
                }.getType());
        for (int i = 0; i < savedDeposits.size(); i++) {
            if (!savedDeposits.get(i).isSynced()) {
                unsyncedDeposits.add(savedDeposits.get(i));
            }
        }

        String inkasimet = realmHelper.getInkasimiString();
        List<InkasimiDetail> inkasimetList = (ArrayList<InkasimiDetail>) gson.fromJson(inkasimet,
                new TypeToken<ArrayList<InkasimiDetail>>() {
                }.getType());


        for (int i = 0; i < inkasimetList.size(); i++) {
            if (!inkasimetList.get(i).isSynced()) {
                unsyncedInkasime.add(inkasimetList.get(i));
            }
        }

        binding.recycler.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) mLayoutManager) {
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

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLang)) {
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
        } else {
            Toast.makeText(ReportDetailActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
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


    private void syncShpenzimet() {
        if (unsyncedVendor.size() > 0) {
            showLoader();
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
                            adapter.setVendorPosts(realmHelper.getVendors());
                            hideLoader();
                        } else {
                            Toast.makeText(this, response.getError().getText(), Toast.LENGTH_SHORT).show();
                        }
                        showLoader();
                    }, throwable -> {
                        Toast.makeText(ReportDetailActivity.this, R.string.shpenzimi_nuk_u_ruajt_ne_server, Toast.LENGTH_SHORT).show();
                        hideLoader();
                    });
        }
    }


    public void syncInkasimi() {
        showLoader();
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
                        adapter.setInkasimiDetails(realmHelper.getInkasimi());
                        hideLoader();
                    } else {
                        Toast.makeText(this, responseBody.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                    hideLoader();
                }, throwable -> {
                    hideLoader();
                    Toast.makeText(this, R.string.inkasimi_nuk_u_ruajt_me_sukses, Toast.LENGTH_SHORT).show();
                });
    }


    public void syncDepozitat() {
        showLoader();
        DepositPostObject depositPostObject = new DepositPostObject(preferences.getToken(), preferences.getUserId(), unsyncedDeposits);
        apiService.addDeposit(depositPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    for (int i = 0; i < unsyncedDeposits.size(); i++) {
                        unsyncedDeposits.get(i).setSynced(true);
                        realmHelper.saveDepozita(unsyncedDeposits.get(i));
                    }
                    adapter.setDepositPosts(realmHelper.getDepozitat());
                    hideLoader();
                }, throwable -> {
                    hideLoader();
                });

    }


    private void loadNextPage() {
        isLoading = true;

        if (type == 0) {
            getVendorRepors();
        } else if (type == 1) {
            getPaymentRepors();
        } else if (type == 2) {
            getDepositRepors();
        }
    }


    private void getVendorRepors() {

        RaportsPostObject raportsPostObject = new RaportsPostObject();
        raportsPostObject.setToken(preferences.getToken());
        raportsPostObject.setUser_id(preferences.getUserId());

        if (currentPage == 0) {
            if (!vendorPosts.isEmpty()) {
                raportsPostObject.setLast_document_number(String.valueOf(vendorPosts.get(vendorPosts.size() - 1).getNo_invoice()));
            }
            currentPage++;
            raportsPostObject.setPage(currentPage++);
        } else {
            raportsPostObject.setLast_document_number("");
            currentPage++;
            raportsPostObject.setPage(currentPage);
        }
        apiService.getRaportExpenseList(raportsPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    isLoading = false;

                    if (responseBody.getSuccess()) {
                        currentPage = responseBody.getCurrentPage();
                        totalPage = responseBody.getTotalPage();

                        for (ReportsList report : responseBody.data) {
//                            VendorPost vendorPost = new VendorPost(report);
                            VendorPost vendorPost = new VendorPost();
                            vendorPost.setVendorFromReport(report);
                            vendorPosts.add(vendorPost);
                        }
                        adapter.notifyItemRangeInserted(0, vendorPosts.size());
                        adapter.notifyDataSetChanged();

                    } else {

                    }
                }, throwable -> {
                    isLoading = false;

                });

    }


    private void getPaymentRepors() {

        RaportsPostObject raportsPostObject = new RaportsPostObject();
        raportsPostObject.setToken(preferences.getToken());
        raportsPostObject.setUser_id(preferences.getUserId());

        if (currentPage == 0) {
            if (!inkasimiDetails.isEmpty()) {
                raportsPostObject.setLast_document_number(String.valueOf(inkasimiDetails.get(inkasimiDetails.size() - 1).getId()));
            }
            currentPage++;
            raportsPostObject.setPage(currentPage++);
        } else {
            raportsPostObject.setLast_document_number("");
            currentPage++;
            raportsPostObject.setPage(currentPage);
        }
        apiService.getRaportPaymentList(raportsPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    isLoading = false;

                    if (responseBody.getSuccess()) {
                        currentPage = responseBody.getCurrentPage();
                        totalPage = responseBody.getTotalPage();

                        for (ReportsList report : responseBody.data) {
                            InkasimiDetail inkasimiDetail = new InkasimiDetail(report);
                            inkasimiDetails.add(inkasimiDetail);
                        }
                        adapter.notifyItemRangeInserted(0, inkasimiDetails.size());
                        adapter.notifyDataSetChanged();

                    } else {

                    }
                }, throwable -> {
                    isLoading = false;

                });

    }


    private void getDepositRepors() {

        RaportsPostObject raportsPostObject = new RaportsPostObject();
        raportsPostObject.setToken(preferences.getToken());
        raportsPostObject.setUser_id(preferences.getUserId());

        if (currentPage == 0) {
            if (!depositPosts.isEmpty()) {
                raportsPostObject.setLast_document_number(String.valueOf(depositPosts.get(depositPosts.size() - 1).getId()));
            }
            currentPage++;
            raportsPostObject.setPage(currentPage++);
        } else {
            raportsPostObject.setLast_document_number("");
            currentPage++;
            raportsPostObject.setPage(currentPage);
        }
        apiService.getRaportDepositeList(raportsPostObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    isLoading = false;

                    if (responseBody.getSuccess()) {
                        currentPage = responseBody.getCurrentPage();
                        totalPage = responseBody.getTotalPage();

                        for (ReportsList report : responseBody.data) {
                            DepositPost depositPost = new DepositPost(report);
                            depositPosts.add(depositPost);
                        }
                        adapter.notifyItemRangeInserted(0, depositPosts.size());
                        adapter.notifyDataSetChanged();

                    } else {

                    }
                }, throwable -> {
                    isLoading = false;

                });

    }


    private void showLoader() {
        binding.loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        binding.loader.setVisibility(View.GONE);
    }

    public double cutTo2(double value) {
        return Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", value));

    }
}

package org.planetaccounting.saleAgent.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.planetaccounting.saleAgent.BottomSheetFragment;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.events.CompanySelectedEvent;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.LoginActivityBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.NotificationPost;
import org.planetaccounting.saleAgent.model.Token;
import org.planetaccounting.saleAgent.model.login.LoginData;
import org.planetaccounting.saleAgent.model.login.LoginPost;
import org.planetaccounting.saleAgent.model.role.TableStock;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.BrowserSupportMethod;
import org.planetaccounting.saleAgent.utils.LocaleManager;
import org.planetaccounting.saleAgent.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by planetaccounting on 05/12/17.
 */

public class LoginActivity extends AppCompatActivity {

    LoginActivityBinding binding;
    CompanyListAdapter adapter;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    @Inject
    LocaleManager localeManager;

    private  final String termsAndPolicyUrl = "http://planetaccounting.org/www/privacy_policy ";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, org.planetaccounting.saleAgent.R.layout.login_activity);
        ((Kontabiliteti) getApplication()).getKontabilitetiComponent().inject(this);


        currentLanguage = getIntent().getStringExtra(currentLang);

        //bootom sheet design
        ImageButton showBottomSheet = (ImageButton) findViewById(R.id.button);

        showBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment fragment = new BottomSheetFragment();
                fragment.show(getSupportFragmentManager(), TAG);
            }
        });


        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().get("data")!=null && !getIntent().getExtras().get("data").equals("")){
                preferences.isFromNotifications = true;
            }
        }

        if(preferences.getToken()!=null&& ( preferences.getRole()== 2)){
            localeManager.updateResources();
            startMainActivity();
        } else {
            realmHelper.removeAllData();
            preferences.saveRoleState(2);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.companyRecycler.setLayoutManager(mLayoutManager);
        binding.companyRecycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new CompanyListAdapter();
        binding.companyRecycler.setAdapter(adapter);
        binding.loginButton.setOnClickListener(view -> {
            if(binding.emailEdittext.getText().length()>0&& binding.passwordEdittext.getText().length()>0) {
                loginUser();
                binding.loginButton.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(getApplicationContext(), R.string.please_write_email_and_password, Toast.LENGTH_SHORT).show();
            }
        });

        binding.termsPolicyButton.setOnClickListener(view -> startActivity(new Intent(BrowserSupportMethod.getBrowserIntent(termsAndPolicyUrl))));
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
            Toast.makeText(LoginActivity.this, R.string.language_already_selected, Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

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

    void loginUser() {
        apiService.loginUser(new LoginPost(getUserName(), getPassword()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    if(loginResponse.getSuccess()) {
                        binding.loginHolder.setVisibility(View.GONE);

                        LoginData daa=    loginResponse.data;

                        binding.companyRecycler.setVisibility(View.VISIBLE);
                        preferences.saveToken(loginResponse.getData().getToken());
                        preferences.saveUserId(loginResponse.getData().getId());
                        preferences.setStationId(loginResponse.getData().getStationId());
                        preferences.saveFullName(loginResponse.getData().getFirstName(), loginResponse.getData().getLastName());
                        preferences.saveStationName(loginResponse.getData().getStationName());
                        preferences.saveEmpNumb(loginResponse.getData().getEmployeeNumber());
                        registerDevice(preferences.getNotification());
                        // re-check
                        if (TextUtils.isDigitsOnly(loginResponse.getData().getLast_invoice_number()) ){
                            preferences.saveLastInvoiceNumber(Integer.parseInt(loginResponse.getData().getLast_invoice_number()));
                        }

                        preferences.saveLastReturnInvoiceNumber(Integer.parseInt(loginResponse.getData().getLastReturnInvoiceNumber()));
                        preferences.saveDefaultWarehouse(loginResponse.getData().getDefault_warehouse());

                        if (loginResponse.getData().getLanguage()!= null) {
                            localeManager.setNewLanguage(loginResponse.getData().getLanguage());
                        } else {
                            localeManager.setNewLanguage("en");

                        }

                        realmHelper.saveRole(loginResponse.getData().getRole());
                        if(loginResponse.getData().getCompanyAllowed().size()>1) {
                            adapter.setCompanies(loginResponse.getData().getCompanyAllowed());
                        }else{
                            preferences.saveCompany(loginResponse.getData().getCompanyAllowed().get(0).getCompanyID());
                            startMainActivity();
                        }
                        Token token = new Token();
                        token.setToken(loginResponse.getData().getToken());
                        realmHelper.saveToken(token);
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.password_or_email_is_wrong, Toast.LENGTH_SHORT).show();
                        binding.loginButton.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                }, throwable -> {
                    throwable.printStackTrace();

                    Log.e("loginfid",throwable.getLocalizedMessage());

                    Toast.makeText(getApplicationContext(), R.string.please_connected_to_internet, Toast.LENGTH_SHORT).show();
                    binding.loginButton.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                });
    }
    @Subscribe
    public void onEvent(CompanySelectedEvent event){
        //Kjo pjes duhet me u studju
        preferences.saveCompany(event.getCompanyId());
        startMainActivity();
    }

    private String getUserName() {
        return binding.emailEdittext.getText().toString();
    }

    private String getPassword() {
        return binding.passwordEdittext.getText().toString();
    }

    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    void registerDevice(String newToken){
        apiService.setNewDevice(new NotificationPost(preferences.getToken(), preferences.getUserId(),newToken,preferences.getDeviceid(),preferences.getDeviceName()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferDetailRespose -> {
                    if (transferDetailRespose.getSuccess()) {

                    } else {
                    }
                });
    }
}

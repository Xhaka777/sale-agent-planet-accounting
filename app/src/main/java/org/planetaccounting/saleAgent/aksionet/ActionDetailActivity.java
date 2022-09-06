package org.planetaccounting.saleAgent.aksionet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.OrdersActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.ActivityActionDetailBinding;
import org.planetaccounting.saleAgent.databinding.AllActionDetailHolderBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class ActionDetailActivity extends AppCompatActivity {

   private ActivityActionDetailBinding binding;

    @Inject
    RealmHelper realmHelper;

    ActionData actionData;


    private String barTitle = "";
    private String title = "";
    private String titleContent = "";
    private String status = "";
    private String shifra = "";
    private String client = "";
    private String discount = "";
    private String amount = "";
    private String nga = "";
    private String deri = "";
    private String from = "";
    private String id = "";
    private String type = "";
    private String unit = "";


    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_action_detail);

        from =  getIntent().getStringExtra("from");
        id =  getIntent().getStringExtra("id");

        barTitle = getIntent().getStringExtra("bar_title");
        title = getIntent().getStringExtra("title");
        titleContent = getIntent().getStringExtra("title_count");
        status = getIntent().getStringExtra("status");
        shifra = getIntent().getStringExtra("shifra");
        client = getIntent().getStringExtra("client");
        discount = getIntent().getStringExtra("discount");
        amount = getIntent().getStringExtra("amount");
        type = getIntent().getStringExtra("type");
        unit = getIntent().getStringExtra("unit");


        nga = getIntent().getStringExtra("nga");
        deri = getIntent().getStringExtra("deri");

        Kontabiliteti.getKontabilitetiComponent().inject(this);

        binding.barTitle.setText(barTitle);
        binding.title.setText(title);
        binding.titleText.setText(titleContent);
        binding.staus.setText(status);
        binding.shifra.setText(shifra);
        binding.client.setText(client);
        binding.discount.setText(discount);
        binding.amount.setText(amount);
        binding.type.setText(type);
        binding.unit.setText(unit);
        binding.nga.setText(nga);
        binding.deri.setText(deri);
        actionData = realmHelper.getAksionet();
        switch (from){
            case "article" :
                for (ActionArticleItems action: actionData.getArticleItems()) {
                    if (action.getId().equals(id)){
                        fillSteps(action.getSteps(),action.getType());
                    }
                }
                break;

            case "brand" :
                for (ActionBrandItem action: actionData.getArticleBrandItem()) {
                    if (action.getId().equals(id)){
                        fillSteps(action.getSteps(),action.getType());
                    }
                }
                break;

            case "category" :
                for (ActionBrandItem action: actionData.getArticleBrandItem()) {
                    if (action.getId().equals(id)){
                        fillSteps(action.getSteps(),action.getType());
                    }
                }
                break;
        }

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
            Toast.makeText(ActionDetailActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
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


    private void fillSteps(List<ActionSteps> steps,String type){

            for (ActionSteps step:steps) {


                AllActionDetailHolderBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                        R.layout.all_action_detail_holder, binding.allActionHolder, false);

                itemBinding.from.setText(step.getFrom());
                itemBinding.to.setText(step.getTo());
                itemBinding.discount.setText(step.getDiscount());

                binding.allActionHolder.addView(itemBinding.getRoot());
                }

        }
}

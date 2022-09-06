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
import org.planetaccounting.saleAgent.databinding.ActivityActionCollectionDetailBinding;
import org.planetaccounting.saleAgent.databinding.CollectionDetailHolderBinding;
import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.model.stock.SubItem;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import java.util.Locale;

import javax.inject.Inject;

import io.realm.RealmResults;

public class ActionCollectionDetailActivity extends AppCompatActivity {
    private ActivityActionCollectionDetailBinding binding;

    @Inject
    RealmHelper realmHelper;

    private int idCount;

    private ActionCollectionItem actionCollectionItem;
    RealmResults<Item> stockItems;

    Locale myLocale;
    String currentLanguage = "sq", currentLang;
    public static final String TAG = "bottom_sheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_action_collection_detail);
        idCount = getIntent().getIntExtra("id", -1);
        if (idCount == -1) finish();
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        actionCollectionItem = realmHelper.getAksionet().getActionCollectionItem().get(idCount);
        stockItems = realmHelper.getStockItems();


        binding.barTitle.setText(R.string.aksionet_me_kombinim);
        binding.title.setText(R.string.kombinim);
        binding.titleText.setText(actionCollectionItem.getId());
        binding.staus.setText(R.string.aktiv);

        binding.nga.setText(actionCollectionItem.getFrom());
        binding.deri.setText(actionCollectionItem.getTo());

        for (int i1 = 0; i1 < actionCollectionItem.getItems().size(); i1++) {

            CollectionDetailHolderBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                    R.layout.collection_detail_holder, binding.collectionActionHolder, false);
            SubItem subItem =  getArtcleById(actionCollectionItem.getItems().get(i1).getItem_id());

            if (subItem != null) {
                ActionCollectionSubItem actionCollectionSubItem = actionCollectionItem.getItems().get(i1);

                itemBinding.action.setText(subItem.getName());
                itemBinding.quantity.setText(actionCollectionSubItem.getQuantity());
                itemBinding.discount.setText(actionCollectionSubItem.getDiscount());
                }

            binding.collectionActionHolder.addView(itemBinding.getRoot());

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
            Toast.makeText(ActionCollectionDetailActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }
//
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


    private SubItem getArtcleById(String article_id) {
        for (int i = 0; i < stockItems.size(); i++) {
            for (int j = 0; j < stockItems.get(i).getItems().size(); j++) {
                if (stockItems.get(i).getItems().get(j).getId().equalsIgnoreCase(article_id)) {
                    return stockItems.get(i).getItems().get(j);
                }
            }
        }
        return null;
    }
}

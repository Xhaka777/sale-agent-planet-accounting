package org.planetaccounting.saleAgent.aksionet;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.ActivityActionCollectionDetailBinding;
import org.planetaccounting.saleAgent.databinding.CollectionDetailHolderBinding;
import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.model.stock.SubItem;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import javax.inject.Inject;

import io.realm.RealmResults;

public class ActionCollectionDetailActivity extends AppCompatActivity {
    private ActivityActionCollectionDetailBinding binding;

    @Inject
    RealmHelper realmHelper;

    private int idCount;

    private ActionCollectionItem actionCollectionItem;
    RealmResults<Item> stockItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_action_collection_detail);
        idCount = getIntent().getIntExtra("id", -1);
        if (idCount == -1) finish();
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        actionCollectionItem = realmHelper.getAksionet().getActionCollectionItem().get(idCount);
        stockItems = realmHelper.getStockItems();


        binding.barTitle.setText("Aksionet me Kombinim");
        binding.title.setText("Kombinim");
        binding.titleText.setText(actionCollectionItem.getId());
        binding.staus.setText("Aktiv");

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

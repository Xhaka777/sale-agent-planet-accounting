package org.planetaccounting.saleAgent.aksionet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.databinding.ActionItemBinding;
import org.planetaccounting.saleAgent.databinding.ActivityActionBinding;
import org.planetaccounting.saleAgent.databinding.KombinimHeaderBinding;
import org.planetaccounting.saleAgent.model.Categorie;
import org.planetaccounting.saleAgent.model.stock.Brand;
import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.model.stock.SubItem;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import java.util.List;

import javax.inject.Inject;

import io.realm.RealmList;
import io.realm.RealmResults;

public class ActionActivity extends AppCompatActivity {
    @Inject
    RealmHelper realmHelper;

    ActivityActionBinding binding;
    ActionData actionData;
    RealmResults<Item> stockItems;
    RealmResults<Brand> brands;
    RealmResults<Categorie> categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_action);
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        actionData = realmHelper.getAksionet();
        stockItems = realmHelper.getStockItems();
        brands = realmHelper.getBrands();
        categories = realmHelper.getCategories();
        try {
            setupArticleAction(actionData.getArticleItems());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Nje ose me shume artikuj ne aksion nuk jane ne stok!", Toast.LENGTH_SHORT).show();
        }
        try {
            setupBrandAction(actionData.getArticleBrandItem());
        } catch (Exception e){
        }
        try {
            setupCategoryAcion(actionData.getArticleCategoryItem());
        } catch (Exception e){
        }

            setupCombinationAction(actionData.getActionCollectionItem());

    }

    private void setupArticleAction(List<ActionArticleItems> articleItems) {
        for (int i = 0; i < articleItems.size(); i++) {
            SubItem subItem = getArtcleById(articleItems.get(i).getItemId());

            if (subItem != null) {
                ActionArticleItems articleItems1 = articleItems.get(i);
                ActionItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                        R.layout.action_item, binding.articleActionHolder, false);
                itemBinding.numri.setText("" + (i + 1));
                itemBinding.artikulli.setText(subItem.getName());
                itemBinding.nga.setText(articleItems.get(i).getFrom());
                itemBinding.deri.setText(articleItems.get(i).getTo());


                itemBinding.action.setOnClickListener(view -> {
                            Intent articleIntent = new Intent(this, ActionDetailActivity.class);

                    articleIntent.putExtra("id", articleItems1.getId());
                    articleIntent.putExtra("from", "article");
                    articleIntent.putExtra("bar_title", "Aksionet me produkte");
                            articleIntent.putExtra("title", "Artikulli");
                            articleIntent.putExtra("title_count", subItem.getName());
                            articleIntent.putExtra("status", "Activ");
                            articleIntent.putExtra("shifra", subItem.getNumber());
                            articleIntent.putExtra("type", articleItems1.getType());
                            articleIntent.putExtra("unit", "-/-");
                            articleIntent.putExtra("client", articleItems1.getClientCategoryName());
                            articleIntent.putExtra("discount", articleItems1.getDiscount());
                            articleIntent.putExtra("amount", articleItems1.getQuantity());
                            articleIntent.putExtra("nga", articleItems1.getFrom());
                            articleIntent.putExtra("deri", articleItems1.getTo());
                            startActivity(articleIntent);

                        }
                );

                binding.articleActionHolder.addView(itemBinding.getRoot());

            }
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

    private void setupBrandAction(List<ActionBrandItem> actionBrandItems) {
        for (int i = 0; i < actionBrandItems.size(); i++) {
            ActionItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                    R.layout.action_item, binding.brandActionHolder, false);
            ActionBrandItem actionBrandItem = actionBrandItems.get(i);
            itemBinding.numri.setText("" + (i + 1));
            itemBinding.artikulli.setText(actionBrandItems.get(i).getBrandName());
            itemBinding.nga.setText(actionBrandItems.get(i).getFrom());
            itemBinding.deri.setText(actionBrandItems.get(i).getTo());

            itemBinding.action.setOnClickListener(view -> {
                        Intent articleIntent = new Intent(this,ActionDetailActivity.class);

                articleIntent.putExtra("id", actionBrandItem.getId());
                articleIntent.putExtra("from", "brand");
                        articleIntent.putExtra("bar_title","Aksionet me Brend");
                        articleIntent.putExtra("title","Brendi");
                        articleIntent.putExtra("title_count",actionBrandItem.getBrandName());
                        articleIntent.putExtra("status","Activ");
                        articleIntent.putExtra("shifra","-/-");
                        articleIntent.putExtra("type", actionBrandItem.getType());
                        articleIntent.putExtra("unit", actionBrandItem.getUnit());
                        articleIntent.putExtra("client",actionBrandItem.getClientCategoryName());
                        articleIntent.putExtra("discount",actionBrandItem.getDiscount());
                        articleIntent.putExtra("amount",actionBrandItem.getQuantity());
                        articleIntent.putExtra("nga",actionBrandItem.getFrom());
                        articleIntent.putExtra("deri",actionBrandItem.getTo());
                        startActivity(articleIntent);

                    }
            );

            binding.brandActionHolder.addView(itemBinding.getRoot());
        }
    }

    private void setupCategoryAcion(List<ActionCategoryItem> actionCategoryItems) {
        for (int i = 0; i < actionCategoryItems.size(); i++) {
            ActionItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                    R.layout.action_item, binding.categoryActionHolder, false);
            ActionCategoryItem  actionCategoryItem = actionCategoryItems.get(i);
            itemBinding.numri.setText("" + (i + 1));
            itemBinding.artikulli.setText(actionCategoryItems.get(i).getCategoryName());
            itemBinding.nga.setText(actionCategoryItems.get(i).getFrom());
            itemBinding.deri.setText(actionCategoryItems.get(i).getTo());

            itemBinding.action.setOnClickListener(view -> {
                        Intent articleIntent = new Intent(this,ActionDetailActivity.class);

                articleIntent.putExtra("id", actionCategoryItem.getId());
                articleIntent.putExtra("from", "category");

                articleIntent.putExtra("bar_title","Aksionet me Kategori");
                        articleIntent.putExtra("title","Kategoria");
                        articleIntent.putExtra("title_count",actionCategoryItem.getCategoryName());
                        articleIntent.putExtra("status","Activ");
                        articleIntent.putExtra("shifra","-/-");
                        articleIntent.putExtra("type", actionCategoryItem.getType());
                        articleIntent.putExtra("unit", actionCategoryItem.getUnit());
                        articleIntent.putExtra("client",actionCategoryItem.getClientCategoryName());
                        articleIntent.putExtra("discount",actionCategoryItem.getDiscount());
                        articleIntent.putExtra("amount",actionCategoryItem.getQuantity());
                        articleIntent.putExtra("nga",actionCategoryItem.getFrom());
                        articleIntent.putExtra("deri",actionCategoryItem.getTo());
                        startActivity(articleIntent);

                    }
            );

            binding.categoryActionHolder.addView(itemBinding.getRoot());
        }
    }


    private void setupCombinationAction(List<ActionCollectionItem> actions) {

        for (int i = 0; i < actions.size(); i++) {
            ActionItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                    R.layout.action_item, binding.collectionActionHolder, false);

            itemBinding.nga.setText(actions.get(i).getFrom());
            itemBinding.deri.setText(actions.get(i).getTo());
            itemBinding.numri.setText("" + (i + 1));
            itemBinding.artikulli.setText(actions.get(i).getId());
            int finalI = i;
            itemBinding.action.setOnClickListener(view -> {
                Intent collectionIntent = new Intent(this,ActionCollectionDetailActivity.class);
                collectionIntent.putExtra("id", finalI);
                startActivity(collectionIntent);
            });
            binding.collectionActionHolder.addView(itemBinding.getRoot());

        }
    }
}

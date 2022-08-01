package org.planetaccounting.saleAgent.aksionet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SHB on 2/16/2018.
 */

public class ActionData extends RealmObject {
    @PrimaryKey
    private int id = 0;
    @SerializedName("action_article")
    @Expose
    public RealmList<ActionArticleItems> ArticleItems = null;

    @SerializedName("action_brand")
    @Expose
    public RealmList<ActionBrandItem> ArticleBrandItem = null;

    @SerializedName("action_category")
    @Expose
    public RealmList<ActionCategoryItem> ArticleCategoryItem = null;

    @SerializedName("action_collection")
    @Expose
    public RealmList<ActionCollectionItem> ActionCollectionItem = null;

    public List<ActionCollectionItem> getActionCollectionItem() {
        return ActionCollectionItem;
    }

    public List<ActionArticleItems> getArticleItems() {
        return ArticleItems;
    }

    public List<ActionBrandItem> getArticleBrandItem() {
        return ArticleBrandItem;
    }

    public List<ActionCategoryItem> getArticleCategoryItem() {
        return ArticleCategoryItem;
    }

    @Override
    public String toString() {
        return "ActionData{" +
                "ArticleItems=" + ArticleItems +
                ", ArticleBrandItem=" + ArticleBrandItem +
                ", ArticleCategoryItem=" + ArticleCategoryItem +
                ", ActionCollectionItem=" + ActionCollectionItem +
                '}';
    }
}

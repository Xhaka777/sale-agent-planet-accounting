package org.planetaccounting.saleAgent.db;

import android.provider.BaseColumns;

/**
 * Created by SHB on 2/16/2018.
 */

public class TableData {

    public static final String DATABASE_NAME = "planetAccountin" ;
    public TableData(){

    }

    public static abstract class TableArticleAction implements BaseColumns {

        public static final String ID = "id" ;
        public static final String data_from = "data_from" ;
        public static final String data_to = "data_to" ;
        public static final String item_id = "item_id" ;
        public static final String quantity = "quantity" ;
        public static final String discount = "discount" ;
        public static final String TABLE_NAME = "action_article" ;

    }

    public static abstract class TableBrandAction implements BaseColumns {

        public static final String ID = "id" ;
        public static final String data_from = "data_from" ;
        public static final String data_to = "data_to" ;
        public static final String brand_id = "brand_id" ;
        public static final String quantity = "quantity" ;
        public static final String discount = "discount" ;
        public static final String unit = "unit" ;
        public static final String TABLE_NAME = "action_brand" ;

    }

    public static abstract class TableActionCategory implements BaseColumns {

        public static final String ID = "`id`" ;
        public static final String data_from = "`data_from`" ;
        public static final String data_to = "`data_to`" ;
        public static final String category_id = "`category_id`" ;
        public static final String quantity = "`quantity`" ;
        public static final String discount = "`discount`" ;
        public static final String unit = "`unit`" ;
        public static final String TABLE_NAME = "`action_category`" ;

    }
    public static abstract class TableCollectionItem implements BaseColumns {

        public static final String ID = "id" ;
        public static final String data_from = "data_from" ;
        public static final String data_to = "data_to" ;
        public static final String catclient = "clientcat";
        public static final String TABLE_NAME = "table_collection" ;

    }

    public static abstract class TableSubCollectionItem implements BaseColumns {

        public static final String item_id = "item_id" ;
        public static final String quantity = "quantity" ;
        public static final String discount = "discount" ;
        public static final String TABLE_NAME = "table_subCollection" ;

    }



}

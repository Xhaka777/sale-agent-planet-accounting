package org.planetaccounting.saleAgent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SHB on 2/16/2018.
 */

public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int database_version = 1;
    public String CREATE_TABLE_ARTICLE_ = "CREATE TABLE  `action_article` (`id` text,`data_from` text,`data_to` text,`item_id` text,`quantity` text,`discount` text);";
    public String CREATE_TABLE_BRAND_ACTION = "CREATE TABLE IF NOT EXISTS `action_brand` ( `id` text ,`data_from` text ,`data_to` text , `brand_id` text ,`quantity` text ,`discount` text , `unit` text );";
    public String CREATE_TABLE_CATEGORY_ACTION = "CREATE TABLE IF NOT EXISTS `action_category` ( `id` text ,`data_from` text ,`data_to` text , `category_id` text ,`quantity` text , `discount` text ,`unit` text);";

    public String CREATE_TABLE_COLLECTION_ACTION ="CREATE TABLE IF NOT EXISTS `table_collection` ( `id` text, `data_from` text,`data_to` text, `clientcat` text);";

    public String CREATE_TABLE_SUBCOLLECTION_ACTION = "CREATE TABLE IF NOT EXISTS `table_subcollection` ( `item_id` text ,`quantity` text ,`discount` text );";

    public DatabaseOperations(Context context) {
        super(context, TableData.DATABASE_NAME, null, database_version);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
         Log.d("Databaza ","Tabelen e krijoi");
        db.execSQL(CREATE_TABLE_ARTICLE_);
//        db.execSQL(CREATE_TABLE_BRAND_ACTION);
//        db.execSQL(CREATE_TABLE_SUBCOLLECTION_ACTION);
//        db.execSQL(CREATE_TABLE_COLLECTION_ACTION);
//        db.execSQL(CREATE_TABLE_CATEGORY_ACTION);
    }

    public void createArticleDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_ARTICLE_);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Databaza ","Tabelen e krijuar edito");
    }

    public boolean insertDatatoActionArticle(String ID,String data_from, String data_to, String item_id,String quantity,String discount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenti = new ContentValues();

        contenti.put(TableData.TableArticleAction.ID,ID);
        contenti.put(TableData.TableArticleAction.data_from,data_from);
        contenti.put(TableData.TableArticleAction.data_to,data_to);
        contenti.put(TableData.TableArticleAction.item_id,item_id);
        contenti.put(TableData.TableArticleAction.quantity,quantity);
        contenti.put(TableData.TableArticleAction.discount,discount);

        long rezultati = db.insert(TableData.TableArticleAction.TABLE_NAME,null,contenti);

        if(rezultati == -1) {
             System.out.println("-1");
            return false;
        }else{
            System.out.println("00");
            return true;
        }

    }

    public void dropArticleDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + TableData.TableArticleAction.TABLE_NAME + "'");

    }

    public boolean insertDatatoBrand(String ID,String data_from, String data_to, String brand_id,String quantity,String discount,String unit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenti = new ContentValues();

        contenti.put(TableData.TableBrandAction.ID,ID);
        contenti.put(TableData.TableBrandAction.data_from,data_from);
        contenti.put(TableData.TableBrandAction.data_to,data_to);
        contenti.put(TableData.TableBrandAction.brand_id,brand_id);
        contenti.put(TableData.TableBrandAction.quantity,quantity);
        contenti.put(TableData.TableBrandAction.discount,discount);
        contenti.put(TableData.TableBrandAction.unit,unit);

        long rezultati = db.insert(TableData.TableBrandAction.TABLE_NAME,null,contenti);

        if(rezultati == -1) {
            // System.out.println("-1");
            return false;
        }else{
            // System.out.println("00");
            return true;
        }

    }
    public void dropBrandDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + TableData.TableBrandAction.TABLE_NAME + "'");

    }
    public void createBrandDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_BRAND_ACTION);
    }
    public void dropCategoryDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + TableData.TableActionCategory.TABLE_NAME + "'");

    }
    public void createCategoryDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_CATEGORY_ACTION);
    }
    public void dropCollectionDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + TableData.TableCollectionItem.TABLE_NAME + "'");

    }
    public void createCollectionDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_COLLECTION_ACTION);
    }
    public void dropSubCollectionDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + TableData.TableSubCollectionItem.TABLE_NAME + "'");

    }
    public void createSubCollectionDataTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_SUBCOLLECTION_ACTION);
    }

    public boolean inertDatatoCategory(String ID,String data_from, String data_to, String category_id,String quantity,String discount,String unit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenti = new ContentValues();

        contenti.put(TableData.TableActionCategory.quantity,quantity);
        contenti.put(TableData.TableActionCategory.unit,unit);
        contenti.put(TableData.TableActionCategory.discount,discount);
        contenti.put(TableData.TableActionCategory.category_id,category_id);
        contenti.put(TableData.TableActionCategory.data_from,data_from);
        contenti.put(TableData.TableActionCategory.data_to,data_to);
        contenti.put(TableData.TableActionCategory.ID,ID);
        long rezultati = db.insert(TableData.TableActionCategory.TABLE_NAME,null,contenti);

        if(rezultati == -1) {
            // System.out.println("-1");
            return false;
        }else{
            // System.out.println("00");
            return true;
        }

    }

    public boolean insertDatatoCollection(String ID,String data_from, String data_to,String catclient){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenti = new ContentValues();

        contenti.put(TableData.TableCollectionItem.ID,ID);
        contenti.put(TableData.TableCollectionItem.data_from,data_from);
        contenti.put(TableData.TableCollectionItem.data_to,data_to);
        contenti.put(TableData.TableCollectionItem.catclient,catclient);


        long rezultati = db.insert(TableData.TableCollectionItem.TABLE_NAME,null,contenti);

        if(rezultati == -1) {
            // System.out.println("-1");
            return false;
        }else{
            // System.out.println("00");
            return true;
        }

    }

    public boolean insertDatatoSubCollection(String item_id,String quantity, String discount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenti = new ContentValues();

        contenti.put(TableData.TableSubCollectionItem.item_id,item_id);
        contenti.put(TableData.TableSubCollectionItem.quantity,quantity);
        contenti.put(TableData.TableSubCollectionItem.discount,discount);


        long rezultati = db.insert(TableData.TableSubCollectionItem.TABLE_NAME,null,contenti);

        if(rezultati == -1) {
            // System.out.println("-1");
            return false;
        }else{
            // System.out.println("00");
            return true;
        }

    }



    public Cursor getArticleActions(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TableData.TableArticleAction.TABLE_NAME, null);

        return result;
    }

    public Cursor getBrandAction(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TableData.TableBrandAction.TABLE_NAME, null);

        return result;
    }
    public Cursor getCategoryAction(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TableData.TableActionCategory.TABLE_NAME, null);

        return result;
    }

    public Cursor getCollectionAction(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TableData.TableCollectionItem.TABLE_NAME, null);

        return result;
    }

    public Cursor getSubCollectionAction(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+ TableData.TableSubCollectionItem.TABLE_NAME, null);

        return result;
    }

}

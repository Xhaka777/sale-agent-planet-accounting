package org.planetaccounting.saleAgent.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.function.BinaryOperator;

/**
 * Created by macb on 09/12/17.
 */

public class Preferences {
    public static String PREFS = "kontabiliteti_prefs";
    public static String LOGIN_PREFS = "login_prefs";
    public static String TOKEN = "access_token";
    public static String SELECTED_COMPANY = "selected_company";
    public static String STATION_NBAME = "station_name";
    public static String FULL_NAME = "full_name";
    public static String USER_ID = "user_id";
    public static String ROLE = "ROLE";
    public static String LANGUAGE = "LANGUAGE";
    public static String NotificationToken = "NotificationToken";


    public static String FISCAL_NUMBER = "FISCAL_NUMBER";
    public static String Default_Warehouse = "Default_Warehouse";

    public static String DEVICE_SERIAL_NUMBER = "DEVICE_SERIAL_NUMBER";

    public static String LAST_INVOICE_NUMBER = "invoice_number";
    public static String LAST_INVOICE_RETURN_NUMBER = "invoice_return_number";

    public static String STATION_INVOICE_ID = "station_id";
    public static String LOCK_FATURA = "lock_fatura";
    public static String EMPLOY_NUMBER = "emp_num";
    public static String LAST_CHECK= "last_check";
    public static String CURRENT_PAGE = "current_page";
    public static String SASIA = "sasia";

    public static String KODE_SETTINGS = "KODE_SETTINGS";
    public static String BARCODE_SETTINGS = "BARCODE_SETTINGS";
    public static String QUANTITY_SETTINGS = "QUANTITY_SETTINGS";
    public static String DISCOUNT_SETTINGS= "DISCOUNT_SETTINGS";
    public static String DISCOUNT_EXTRA_SETTINGS= "DISCOUNT_EXTRA_SETTINGS";


    public SharedPreferences preferences;
    public SharedPreferences userPreferences;

    public Boolean isFromNotifications = false;
    public Context context;

    public Preferences(Context ctx) {
        preferences = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        userPreferences = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        context = ctx;
    }

    public void saveToken(String token) {
        userPreferences.edit().putString(TOKEN, token).commit();
    }

    public void saveStationName(String stationName) {
        userPreferences.edit().putString(STATION_NBAME, stationName).commit();
    }

    public void saveDefaultWarehouse(String defaultWarehouse) {
        userPreferences.edit().putString(Default_Warehouse, defaultWarehouse).commit();
    }



    public void saveFullName(String name, String surname) {
        String fullName = name + " " + surname;
        userPreferences.edit().putString(FULL_NAME, fullName).commit();
    }

    public void saveLastCheck(int lastCheck){
        preferences.edit().putInt(LAST_CHECK, lastCheck).apply();
        }


    public void saveLanguage(String language) {
        userPreferences.edit().putString(LANGUAGE, language).commit();
    }

    public String getLanguage() {
        return userPreferences.getString(LANGUAGE, "sq");
    }


    public void saveNotification(String notificationToken) {
        userPreferences.edit().putString(NotificationToken, notificationToken).commit();
    }


    public String getNotification() {
        return userPreferences.getString(NotificationToken, "");
    }

    public void saveRoleState(int last){
        userPreferences.edit().putInt(ROLE,last).apply();
        }

    public  int getRole() {
        return userPreferences.getInt(ROLE, 0);
    }

    public String getStationNbame() {
        return userPreferences.getString(STATION_NBAME, "");
    }

    public String getDefaultWarehouse() {
        return userPreferences.getString(Default_Warehouse, "");
    }


    public String getFullName() {
        return userPreferences.getString(FULL_NAME, "");
    }

    public String getToken() {
        return userPreferences.getString(TOKEN, null);
    }

    public int getLastCheck(){
        return preferences.getInt(LAST_CHECK,-1);
    }

    public void saveCompany(String companyId) {
        userPreferences.edit().putString(SELECTED_COMPANY, companyId).commit();
    }

    public String getSelectedCompany() {
        return userPreferences.getString(SELECTED_COMPANY, "");
    }

    public void saveUserId(String userId) {
        userPreferences.edit().putString(USER_ID, userId).commit();
    }

    public String getUserId() {
        return preferences.getString(USER_ID, "");
    }
    public void saveEmpNumb(String userId) {
        userPreferences.edit().putString(EMPLOY_NUMBER, userId).commit();
    }

    public String getEmployNumber() {
        return preferences.getString(EMPLOY_NUMBER, "");
    }

    public void saveLastInvoiceNumber(int invoiceNumber) {
        userPreferences.edit().putInt(LAST_INVOICE_NUMBER, invoiceNumber).commit();
    }

    public int getLastInvoiceNumber() {
        int last = preferences.getInt(LAST_INVOICE_NUMBER, 0);
        return last + 1;
    }

    public String getCurrentPage(){
        return preferences.getString(CURRENT_PAGE, "");
    }

    public String getSasia(){
        return preferences.getString(SASIA, "");
    }

    public void saveFisclNumber(String fiscalNumber) {
        userPreferences.edit().putString(FISCAL_NUMBER, fiscalNumber).commit();
    }

    public String getFisclNumber() {
        return preferences.getString(FISCAL_NUMBER,"");
    }

    public void saveDeviceSerialNumber(String serialNumber) {
        userPreferences.edit().putString(DEVICE_SERIAL_NUMBER, serialNumber).commit();
    }

    public String getDeviceSerialNumber() {
        return preferences.getString(DEVICE_SERIAL_NUMBER,"");
    }

    public void saveLastReturnInvoiceNumber(int invoiceNumber) {
        userPreferences.edit().putInt(LAST_INVOICE_RETURN_NUMBER, invoiceNumber).commit();
    }

    public int getLastReturnInvoiceNumber() {
        int last = preferences.getInt(LAST_INVOICE_RETURN_NUMBER, 0);
        return last + 1;
    }


    public String getStationId() {
        return userPreferences.getString(STATION_INVOICE_ID, "");
    }

    public void setStationId(String stationId) {
        preferences.edit().putString(STATION_INVOICE_ID, stationId).commit();
    }

    public void deleteUserPrefs() {
        userPreferences.edit().clear().commit();
    }

    public void lockFatura(boolean lockIt) {
        preferences.edit().putBoolean(LOCK_FATURA, lockIt).commit();
    }
    public String getDeviceid() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getDeviceName(){
        return  android.os.Build.MODEL;
    }

    public boolean isFaturaLocked() {
        return preferences.getBoolean(LOCK_FATURA, false);
    }



    // For settings


    public  boolean getKodeSettings() {
        return userPreferences.getBoolean(KODE_SETTINGS, true);
    }

    public void setKodeSettings(Boolean kodeSettings) {
         userPreferences.edit().putBoolean(KODE_SETTINGS,kodeSettings).commit();
    }

    public  Boolean getBarcodeSettings() {
        return userPreferences.getBoolean(BARCODE_SETTINGS, true);
    }

    public  void setBarcodeSettings(Boolean barcodeSettings) {
        userPreferences.edit().putBoolean(BARCODE_SETTINGS,barcodeSettings).commit();
    }

    public  boolean getQuantitySettings() {
        return userPreferences.getBoolean(QUANTITY_SETTINGS, true);
    }

    public  void setQuantitySettings(boolean quantitySettings) {
        userPreferences.edit().putBoolean(QUANTITY_SETTINGS,quantitySettings).commit();
    }

    public boolean getDiscountSettings() {
        return userPreferences.getBoolean(DISCOUNT_SETTINGS, true);
    }

    public  void setDiscountSettings(boolean discountSettings) {
        userPreferences.edit().putBoolean(DISCOUNT_SETTINGS,discountSettings).commit();
    }

    public  boolean getDiscountExtraSettings() {
        return userPreferences.getBoolean(DISCOUNT_EXTRA_SETTINGS, true);
    }

    public  void setDiscountExtraSettings(boolean discountExtraSettings) {
        userPreferences.edit().putBoolean(DISCOUNT_EXTRA_SETTINGS,discountExtraSettings).commit();
    }
}

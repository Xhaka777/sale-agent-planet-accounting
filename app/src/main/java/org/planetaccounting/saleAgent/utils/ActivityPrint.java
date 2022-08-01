package org.planetaccounting.saleAgent.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.datecs.fiscalprinter.FiscalPrinterException;
import com.datecs.fiscalprinter.FiscalResponse;
import com.datecs.fiscalprinter.kos.FMP10KOS;
import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.invoice.InvoiceActivity;
import org.planetaccounting.saleAgent.model.InvoiceItem;
import org.planetaccounting.saleAgent.model.stock.Item;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import io.realm.Realm;


interface MethodInvoker {
    public void invoke() throws IOException;
}

public class ActivityPrint extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DEVICE = 2;
    public static String FISCAL_NUMBER = "";
    public static String DEVICE_SERIAL_NUMBER = "";


    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String action;
    ArrayList<InvoiceItem> invoiceItems;
    @Inject
    RealmHelper realmHelper;
    @Inject
    Preferences preferences;
    List<Item> stockItems;
    int g = 0;
    private BluetoothAdapter mBtAdapter;
    private BluetoothSocket mBtSocket;
    private FMP10KOS mFMP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.planetaccounting.saleAgent.R.layout.activity_print);

        Kontabiliteti.getKontabilitetiComponent().inject(this);
        action = getIntent().getStringExtra(InvoiceActivity.ACTION);
        if (action.length() > 0) {
            invoiceItems = getIntent().getParcelableArrayListExtra(InvoiceActivity.ITEMS);

        }
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null) {
            if (mBtAdapter.isEnabled()) {
                selectDevice();
            } else {
                enableBluetooth();
            }
        } else {
            Toast.makeText(this, "Paisja nuk ka bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(org.planetaccounting.saleAgent.R.id.btn_panic_operation).setOnClickListener(v -> performPanicOperation());

        findViewById(org.planetaccounting.saleAgent.R.id.btn_defining_items).setOnClickListener(v -> definingItems());

        findViewById(org.planetaccounting.saleAgent.R.id.btn_fiscal_receipt).setOnClickListener(v -> printFiscalReceipt());

        findViewById(org.planetaccounting.saleAgent.R.id.btn_z_report).setOnClickListener(v -> performZReport());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT: {
                if (resultCode == RESULT_OK) {
                    selectDevice();
                } else {
                    finish();
                }
                break;
            }
            case REQUEST_DEVICE: {
                if (resultCode == RESULT_OK) {
                    String address = data.getStringExtra(DeviceActivity.EXTRA_ADDRESS);
                    connect(address);
                } else {
                    finish();
                }
                break;
            }
        }
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void selectDevice() {
        Intent selectDevice = new Intent(this, DeviceActivity.class);
        startActivityForResult(selectDevice, REQUEST_DEVICE);
    }

    private void postToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void connect(final String address) {
        invokeHelper(new MethodInvoker() {
            @Override
            public void invoke() throws IOException {
                final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
                final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();

                mBtSocket = socket;
                final InputStream in = socket.getInputStream();
                final OutputStream out = socket.getOutputStream();
                mFMP = new FMP10KOS(in, out);
                postToast("Connected");
                if (action.equalsIgnoreCase(InvoiceActivity.ACTION_PRINT)) {
                    printFiscalReceipt();
                } else if (action.equalsIgnoreCase(InvoiceActivity.PRINT_Z_RAPORT)) {
                    performZReport();
                } else if (action.equalsIgnoreCase(InvoiceActivity.ACTION_ADD_ITEMS)) {
                    definingItems();
                }

            }
        });
    }

    public synchronized void disconnect() {
        if (mFMP != null) {
            mFMP.close();
        }

        if (mBtSocket != null) {
            try {
                mBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void invokeHelper(final MethodInvoker invoker) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Ju lutem pritni");
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        dialog.show();

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    invoker.invoke();
                } catch (FiscalPrinterException e) { // Fiscal printer error
                    e.printStackTrace();
                    postToast("FiscalPrinterException: " + e.getMessage());
                } catch (IOException e) { //Communication error
                    e.printStackTrace();
                    postToast("IOException: " + e.getMessage());
                    disconnect();
                    selectDevice();
                } catch (Exception e) { // Critical exception
                    e.printStackTrace();
                    postToast("Exception: " + e.getMessage());
                    disconnect();
                    selectDevice();
                } finally {
                    dialog.dismiss();
                }
            }
        });
        t.start();
    }

    private void performPanicOperation() {
        invokeHelper(new MethodInvoker() {
            @Override
            public void invoke() throws IOException {
                Realm realm = Realm.getDefaultInstance();
                stockItems = realm.where(Item.class).findAll();
                mFMP.command107Variant4Version2(stockItems.get(0).getItems().get(0).getId(),
                stockItems.get(stockItems.size() - 1).getItems().get(stockItems.get(stockItems.size() - 1).getItems().size() - 1).getId());
                preferences.lockFatura(true);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    private void definingItems() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {


                    try{
                        FiscalResponse fiscalResponse = mFMP.command113Variant0Version0();
                        FISCAL_NUMBER = fiscalResponse.get("lastDocumentNumber");
                        preferences.saveFisclNumber(FISCAL_NUMBER);
                        System.out.println("numri fiskal "+ FISCAL_NUMBER);
                    }catch (Exception e) {
                        System.out.println("numri fiskal catch ");
                    }

                    try{
                        FiscalResponse fiscalResponse = mFMP.command90Variant0Version0();
                        DEVICE_SERIAL_NUMBER = fiscalResponse.get("serialNumber");
                        preferences.saveDeviceSerialNumber(DEVICE_SERIAL_NUMBER);
                        System.out.println("DEVICE_SERIAL_NUMBER "+ DEVICE_SERIAL_NUMBER);
                    }catch (Exception e) {
                        System.out.println("DEVICE_SERIAL_NUMBER catch ");
                    }

                    mFMP.command107Variant4Version0();

//                        mFMP.command107Variant1Version0("D", "104", "1", "1.04", "", "104", "ITEM PLU #104");
                    Realm realm = Realm.getDefaultInstance();
                    stockItems = realm.where(Item.class).findAll();
                    for (int i = 0; i < stockItems.size(); i++) {
                        for (int j = 0; j < stockItems.get(i).getItems().size(); j++) {
                            String name;
                            if (stockItems.get(i).getItems().get(j).getName().length() > 32) {
                                name = stockItems.get(i).getItems().get(j).getName().substring(0, 32);
                            } else {
                                name = stockItems.get(i).getItems().get(j).getName();
                            }
                            System.out.println(stockItems.get(i).getItems().get(j).getId());
                            String cmimi = String.format(Locale.ENGLISH,"%.2f",
                                    Double.parseDouble(stockItems.get(i).getItems().get(j).getPriceSale()));
                            System.out.println(cmimi);
                            System.out.println(name);
                            mFMP.command107Variant1Version0(
                                    stockItems.get(i).getItems().get(j).getVatCodeFisalPrinter(),
                                    stockItems.get(i).getItems().get(j).getId(), "1",
                                    cmimi, "", "", name);
                            g++;
                        }
                    }
                    preferences.lockFatura(false);
                    realm.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }.execute();

    }

    private void printFiscalReceipt() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mFMP.command48Variant0Version0("1", "0000", "1");
                    for (int i = 0; i < invoiceItems.size(); i++) {
                        String cmimi = String.format(Locale.ENGLISH,"%.2f", invoiceItems.get(i).getCmimiNeArk());
                        String vlera = String.format(Locale.ENGLISH,"%.2f", invoiceItems.get(i).getVleraEZbritur()); //Vlera e zbritur

                        if (Double.parseDouble(vlera) > 0) {
                            mFMP.command58Variant2Version1("", invoiceItems.get(i).getItems().get(invoiceItems.get(i).getSelectedPosition()).getId(),
                                    invoiceItems.get(i).getSasia(),
                                    "-" + vlera, cmimi);
                        } else {
                            mFMP.command58Variant0Version1("", invoiceItems.get(i).getItems().get(invoiceItems.get(i).getSelectedPosition()).getId(),
                                    invoiceItems.get(i).getSasia(), cmimi);
                        }
                    }
                    mFMP.totalInCash();
                    mFMP.closeFiscalCheck();

                    if (!preferences.getFisclNumber().equals("")) {
                        FISCAL_NUMBER = preferences.getFisclNumber();
                    } else {

                        try {
                            FiscalResponse fiscalResponse = mFMP.command113Variant0Version0();
                            FISCAL_NUMBER = fiscalResponse.get("lastDocumentNumber");
                            preferences.saveFisclNumber(FISCAL_NUMBER);
                            System.out.println("numri fiskal " + FISCAL_NUMBER);
                        } catch (Exception e) {
                            System.out.println("numri fiskal catch ");
                        }

                    }

                    if (!preferences.getDeviceSerialNumber().equals("")) {
                        DEVICE_SERIAL_NUMBER = preferences.getDeviceSerialNumber();

                    } else {

                        try {
                            FiscalResponse fiscalResponse = mFMP.command90Variant0Version0();
                            DEVICE_SERIAL_NUMBER = fiscalResponse.get("serialNumber");
                            System.out.println("DEVICE_SERIAL_NUMBER " + DEVICE_SERIAL_NUMBER);
                        } catch (Exception e) {
                            System.out.println("DEVICE_SERIAL_NUMBER catch ");
                        }

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
                InvoiceActivity.uploadThisMf = true;
                finish();
            }
        }.execute();

    }


    private void performZReport() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                System.out.println("perform z raport");
                try {
                    mFMP.command120Variant5Version0();
                    mFMP.command120Variant3Version0();
                    mFMP.command69Variant0Version0("0", "", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                performPanicOperation();

            }
        }.execute();
    }

    @Override
    public void onBackPressed() {

    }
}

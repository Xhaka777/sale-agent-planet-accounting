package org.planetaccounting.saleAgent;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.dx.stock.ProxyBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by macb on 04/02/18.
 */

public class PdfPrint {

    public static final String TAG = PdfPrint.class.getSimpleName();
    public static PrintAttributes printAttributes;
    File cacheFolder;


    public PdfPrint(PrintAttributes printAttributes, Context ctx) {
        this.printAttributes = printAttributes;
        cacheFolder = new File(ctx.getFilesDir() + "/etemp/");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static PrintDocumentAdapter.WriteResultCallback getWriteResultCallback(InvocationHandler invocationHandler,
                                                                                  File dexCacheDir) throws IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.WriteResultCallback.class)
                .dexCache(dexCacheDir)
                .handler(invocationHandler)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static PrintDocumentAdapter.LayoutResultCallback getLayoutResultCallback(InvocationHandler invocationHandler,
                                                                                    File dexCacheDir) throws IOException {
        return ProxyBuilder.forClass(PrintDocumentAdapter.LayoutResultCallback.class)
                .dexCache(dexCacheDir)
                .handler(invocationHandler)
                .build();
    }

  /*  public void print(final PrintDocumentAdapter printAdapter, final File path, final String fileName) {
        printAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {


                try {
                    PrintDocumentAdapter.WriteResultCallback callback = getWriteResultCallback(new InvocationHandler() {
                        @Override
                        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                            if (method.getName().equals("onWriteFinished")) {
                                //                            pdfCallback.onPdfCreated();
                            } else {
                                Log.e(TAG, "Layout failed");
                                //                            pdfCallback.onPdfFailed();
                            }
                            return null;
                        }
                    }, null);

                    printAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, getOutputFile(path, fileName), new CancellationSignal(), callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }, null);
    }*/

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void printNew(final PrintDocumentAdapter printAdapter, final File path, final String fileName, final String fileNamenew) {

        try {

            printAdapter.onStart();
            printAdapter.onLayout(null, printAttributes, new CancellationSignal(), getLayoutResultCallback(new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

                    if (method.getName().equals("onLayoutFinished")) {

                        onLayoutSuccess(printAdapter, path, fileName, fileNamenew);
                    } else {
                        Log.e(TAG, "Layout failed");

                    }
                    return null;
                }
            }, new File(fileNamenew)), new Bundle());


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onLayoutSuccess(PrintDocumentAdapter printAdapter, File path, String fileName, String filenamenew) throws IOException {
        PrintDocumentAdapter.WriteResultCallback callback = getWriteResultCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if (method.getName().equals("onWriteFinished")) {

                    System.out.print("hello");

                } else {
                    Log.e(TAG, "Layout failed");

                }
                return null;
            }
        }, new File(filenamenew));

        printAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, getOutputFile(path, fileName), new CancellationSignal(), callback);
    }

    private ParcelFileDescriptor getOutputFile(File path, String fileName) {
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, fileName);
        try {
            file.createNewFile();
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
        }
        return null;
    }
}
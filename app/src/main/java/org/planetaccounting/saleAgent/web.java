package org.planetaccounting.saleAgent;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by macb on 04/02/18.
 */

public class web extends Activity {
    String line;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        line = ReadFromfile("test.html", getApplicationContext());
        line = line.replace("nrFatures", "108-12");
        line = line.replace("nrFatures", "108-12");

        WebView webView = (WebView) findViewById(R.id.web);
        String base_url = "file:///android_asset/";
        webView.loadDataWithBaseURL(base_url, line, "text/html", "UTF-8", null);

//        webView.loadData(line, "text/html", null);


//                load("file:///android_asset/test.html");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                createWebPrintJob(webView);
            }
        });


    }
    public String ReadFromfile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

        try {
            PrintDocumentAdapter printAdapter;
            String jobName = getString(R.string.app_name) + " Document";
            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
            File path = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/AamirPDF/");


            path.mkdirs();
            PdfPrint pdfPrint = new PdfPrint(attributes, getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                printAdapter = webView.createPrintDocumentAdapter(jobName);
            } else {
                printAdapter = webView.createPrintDocumentAdapter();
            }


            pdfPrint.printNew(printAdapter, path, "output_" + System.currentTimeMillis() + ".pdf", getCacheDir().getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

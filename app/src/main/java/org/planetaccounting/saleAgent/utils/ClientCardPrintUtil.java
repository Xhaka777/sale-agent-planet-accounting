package org.planetaccounting.saleAgent.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.PDFPrintDocumentAdapter;
import org.planetaccounting.saleAgent.PdfPrint;
import org.planetaccounting.saleAgent.model.CompanyInfo;
import org.planetaccounting.saleAgent.model.clients.CardItem;
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by macb on 05/02/18.
 */

public class ClientCardPrintUtil {
    WebView swebView;
    Context ctx;
    String line;
    Client client;
    CompanyInfo companyInfo;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    String file = "";
    PrintManager printManager;
    List<CardItem> cardItemst;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ClientCardPrintUtil(List<CardItem> mcardItemst, WebView mwebView, Context ctx, Client client, PrintManager printManager) {
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        this.ctx = ctx;
        this.client = client;
        this.printManager = printManager;
        this.companyInfo = realmHelper.getCompanyInfo();
        this.cardItemst = mcardItemst;
        System.out.println(companyInfo.toString());
        line = ReadFromfile("list.html", ctx);
        line = line.replace("sellerName", companyInfo.getName());
        line = line.replace("clientId", client.getId());
        line = line.replace("clientName", client.getName());
        line = line.replace("clientFiscalNumber", client.getNumberFiscal());
        line = line.replace("clientBussinessNumber", client.getNumberBusniess());
        line = line.replace("clientAdress", client.getAddress()+" "+
                client.getCity()+" "+client.getZip());
        line = line.replace("cardItems", createArticleHtml(cardItemst));
        line = line.replace("balance", String.format(Locale.ENGLISH,"%.2f", balance));

        WebView baseWebView = new WebView(ctx);
        swebView = baseWebView;        String base_url = "file:///android_asset/";
        baseWebView.loadDataWithBaseURL(base_url, line, "text/html", "UTF-8", null);
        baseWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onPageFinished(WebView view, String url) {

                createWebPrintJob(view);
                swebView = null;
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
            String jobName = ctx.getString(org.planetaccounting.saleAgent.R.string.app_name) + " Document";
            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
            File path = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Planet Accounting Faturat/");


            path.mkdirs();
            PdfPrint pdfPrint = new PdfPrint(attributes, ctx);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                printAdapter = webView.createPrintDocumentAdapter(jobName);
            } else {
                printAdapter = webView.createPrintDocumentAdapter();
            }
            String fileName = "Kartela.pdf";
            this.file = path+"/" + fileName;
            pdfPrint.printNew(printAdapter, path, fileName, ctx.getCacheDir().getPath());
            System.out.println("pathiiii "+ getFile());
            new Handler().postDelayed(() -> {
                PrintJob printJob = printManager.print("Planet Accounting", new PDFPrintDocumentAdapter(ctx, "Kartela.pdf", getFile()), null);
                }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFile() {
        return file;
    }
    double balance = 0;
    String createArticleHtml(List<CardItem> cardItems) {
        String finalCode = "";
        double balance = 0.0;
        for (int i = 0; i < cardItems.size(); i++) {
            try{
                balance += cardItems.get(i).getAmount();
            }catch (Exception e){

            }
            finalCode = finalCode + "<tr >" +
                    "<td class=\"text-center\">"+ cardItems.get(i).getData() +"</td >"+
                    "<td class=\"text-center\">"+ cardItems.get(i).getDocumentNumber() +"</td >"+
                    "<td >"+ cardItems.get(i).getDescription() +"</td >"+
                    "<td class=\"t-c\">"+ preferences.getFullName() +"</td >"+
                    "<td class=\"text-center danger\">"+ cardItems.get(i).getPaymentDate() +"</td >"+
                    "<td class=\"text-right number_fs\">"+ String.format(Locale.ENGLISH,"%.2f", cardItems.get(i).getAmount()) +"</td >"+
                    "<td class=\"text-right number_fs\">"+ String.format(Locale.ENGLISH,"%.2f", balance) +"</td >"+
                    "</tr >";
        }
        this.balance = balance;
        return finalCode;
    }

    String createCashHoles(CompanyInfo companyInfo) {
        String finalCode = "";
        for (int i = 0; i < companyInfo.getBankAccounts().size(); i++) {
            finalCode = finalCode + "<div class=\"col-xs-12\" style=\"font-size:10px;\"><b>"
                    + companyInfo.getBankAccounts().get(i).getName()
                    + " : " + companyInfo.getBankAccounts().get(i).getBankAccountNumber() + "</b></div>";
        }
        return finalCode;
    }
}


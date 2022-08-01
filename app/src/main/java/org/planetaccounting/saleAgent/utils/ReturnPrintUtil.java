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
import org.planetaccounting.saleAgent.model.clients.Client;
import org.planetaccounting.saleAgent.model.invoice.InvoicePost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

public class ReturnPrintUtil {
    WebView swebView;
    Context ctx;
    String line;
    Client client;
    CompanyInfo companyInfo;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    PrintManager printManager;
    InvoicePost invoicePost;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ReturnPrintUtil(InvoicePost minvoicePost, WebView mwebView, Context ctx, Client client, PrintManager printManager) {
        Kontabiliteti.getKontabilitetiComponent().inject(this);
        this.ctx = ctx;
        this.client = client;
        this.companyInfo = realmHelper.getCompanyInfo();
        this.printManager = printManager;
        this.invoicePost = minvoicePost;
        System.out.println(companyInfo.toString());
//        Environment.getExternalStorageDirectory()
//                .getAbsolutePath() + "/Planet Accounting Faturat/logo.png"
        line = ReadFromfile("test.html", ctx);

//        <img src="adriatik.png" class="logo_head no_invoice_hide"/>
        String img = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Planet Accounting Faturat/logo.png";
        line = line.replace("companyLogo", " <img src= \"" + img + "\" class=\"logo_head no_invoice_hide\"/>" + "");
        line = line.replace("nrFatures", invoicePost.getNo_invoice() + "");
        line = line.replace("document_type", "Kthim Malli");
        line = line.replace("data", invoicePost.getInvoice_date() + "");
        line = line.replace("njesiaShitjes", preferences.getStationNbame() + "");
        line = line.replace("fullName", preferences.getFullName() + "");
        line = line.replace("clientName", invoicePost.getPartie_name() + "");
        line = line.replace("stationName", invoicePost.getPartie_station_name() + "");
        line = line.replace("clientAdress", invoicePost.getPartie_address() + "");
        line = line.replace("valueWithoutDiscount", invoicePost.getTotal_without_discount() + "");
        line = line.replace("discountValue", invoicePost.getAmount_discount() + "");
        line = line.replace("clientContact", client.getPhone() + "");
        line = line.replace("clientFiscalNumber", client.getNumberFiscal() + "");
        line = line.replace("clientBussinessNumber", client.getNumberBusniess() + "");
        line = line.replace("clientVatNumber", client.getNumberVat() + "");
        line = line.replace("invoiceItems", createArticleHtml(invoicePost, client) + "");
        line = line.replace("discountAmount", invoicePost.getAmount_discount() + "");
        line = line.replace("amountNoVat", invoicePost.getAmount_no_vat() + "");
        line = line.replace("amountOfVat", invoicePost.getAmount_of_vat() + "");
        line = line.replace("totali", invoicePost.getAmount_with_vat() + "");
        line = line.replace("sellerName", companyInfo.getName() + "");
        line = line.replace("sellerFiscal", companyInfo.getFiscalNumber() + "");
        line = line.replace("sellerTel", companyInfo.getPhone() + "");
        line = line.replace("sellerEmail", companyInfo.getEmail() + "");
        line = line.replace("sellerBussines", companyInfo.getBusniessNumber() + "");
        line = line.replace("sellerTvshNumber", companyInfo.getVatNumber() + "");
        line = line.replace("sellerContactPerson", companyInfo.getContactPerson() + "");
        line = line.replace("sellerAdress", companyInfo.getAddress() + "");
        line = line.replace("sellerCity", companyInfo.getCity() + "");
        line = line.replace("sellerState", companyInfo.getState() + "");
        line = line.replace("cashMoney", createCashHoles(companyInfo) + "");
        if (invoicePost.getIs_bill().equalsIgnoreCase("0")) {
            line = line.replace(".no_invoice_hide { display: none;}", "");
        }

        WebView baseWebView = new WebView(ctx);
        swebView = baseWebView;
        String base_url = "file:///android_asset/";
        baseWebView.getSettings().setJavaScriptEnabled(true);
        baseWebView.loadDataWithBaseURL(base_url, line, "text/html", "UTF-8", null);
        baseWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onPageFinished(WebView view, String url) {
//                webView.bringToFront();
                createWebPrintJob(view,invoicePost);
                swebView = null;
            }
        });
        System.out.println("pathiii " + getFile());
//        PrintJob printJob = printManager.print("Planet Accounting", new PDFPrintDocumentAdapter(ctx, "Fatura", getFile()), null);
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
    private void createWebPrintJob(WebView webView,InvoicePost invoicePost) {

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

            String nm = invoicePost.getNo_invoice();
            String fileName = "KthimiMalli"+nm+".pdf";
            this.file = path + "/" + fileName;
            pdfPrint.printNew(printAdapter, path, fileName, ctx.getCacheDir().getPath());
            System.out.println("pathiiii " + getFile());


            new Handler().postDelayed(() -> {
                PrintJob printJob = printManager.print("Planet Accounting", new PDFPrintDocumentAdapter(ctx, "KthimiMalli.pdf", getFile()), null);
                System.out.println("print job " + printJob.isCompleted());
            }, 1000);

        } catch (Exception e) {
            System.out.println("pdf babo ");
            e.printStackTrace();
        }
    }

    String file = "";

    public String getFile() {
        return file;
    }

    String createArticleHtml(InvoicePost invoicePost, Client client) {
        String finalCode = "";

        for (int i = 0; i < invoicePost.getItems().size(); i++) {
            if (invoicePost.getItems().get(i).getRelacioni() == null) {
                invoicePost.getItems().get(i).setRelacioni("1");

            }
            String discount = client.getDiscount();
            if (invoicePost.getItems().get(i).isCollection()) {
                discount = "0";
            } else {
                discount = client.getDiscount();
            }
            String sasia = "";
            if (Double.parseDouble(invoicePost.getItems().get(i).getRelacioni()) > 1) {
                sasia = invoicePost.getItems().get(i).getQuantity();
            }
            int pos = i + 1;
            finalCode = finalCode + "<tr >" +
                    "<td class=\"text-left\" style=\"width:30px\">" + pos + "</td>" +
                    "<td>" + invoicePost.getItems().get(i).getNo() + "</td>" +
                    "<td>" + invoicePost.getItems().get(i).getBarcode() + "</td>" +
                    "<td style=\"min-width:250px;\">" + invoicePost.getItems().get(i).getName() + "</td>" +
                    "<td class=\"text-right\" >" + invoicePost.getItems().get(i).getUnit() + "</td>" +
                    "<td class=\"text-right \">" + sasia + "</td>" +
                    "<td class=\"text-right \">" + Double.parseDouble(invoicePost.getItems().get(i).getQuantity()) * Double.parseDouble(invoicePost.getItems().get(i).getRelacioni()) + "" + "</td>" +
                    "<td class=\"text-right\">" + invoicePost.getItems().get(i).getPrice() + "</td>" +
                    "<td class=\"text-center\" >" + discount + "</td>" +
                    "<td class=\"text-center\" >" + invoicePost.getItems().get(i).getDiscount() + "</td>" +
                    "<td class=\"text-center\">" + invoicePost.getItems().get(i).getVat_rate() + "</td>" +
                    "<td class=\"text-right\">" + invoicePost.getItems().get(i).getPrice_vat() + "</td>" +
                    "<td class=\"text-right\">" + invoicePost.getItems().get(i).getTotalPrice() + "</td>" +
                    "</tr >";
        }
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

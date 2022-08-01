package org.planetaccounting.saleAgent.transfere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityDetailTransferBinding;
import org.planetaccounting.saleAgent.databinding.DetailItemTransferBinding;
import org.planetaccounting.saleAgent.model.Error;
import org.planetaccounting.saleAgent.model.ErrorPost;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.model.transfer.TransferDetail;
import org.planetaccounting.saleAgent.model.transfer.TransferItem;
import org.planetaccounting.saleAgent.model.transfer.TransferPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DetailTransferActivity extends AppCompatActivity {
    private ActivityDetailTransferBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;

    private int transferId;
    private int from;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transferId = getIntent().getIntExtra("transferId",0);
        from = getIntent().getIntExtra("from",0);
        type = getIntent().getStringExtra("type");
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_transfer);
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        binding.barTitle.setText("Transfer Detail");

        if (from == 1){
            binding.acceptOtherTransfer.setVisibility(View.INVISIBLE);
            }

        if (type.equals("tp")){
            binding.acceptOtherTransfer.setVisibility(View.INVISIBLE);
            binding.cancelOtherTransfer.setVisibility(View.INVISIBLE);
        }


        binding.cancelOtherTransfer.setOnClickListener(view ->
                dialog("anuloni transferin ",true)
        );

        binding.acceptOtherTransfer.setOnClickListener(view ->
                dialog("pranoni transferin",false)
        );

        getTransferDetail();

        }

        private void getTransferDetail(){

        TransferPost transferPost = new TransferPost(preferences.getToken(),preferences.getUserId(),transferId + "");

        apiService.getDetailTransfer(transferPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferDetailRespose -> {
                    if (transferDetailRespose.getSuccess()) {
                        TransferDetail detail =  transferDetailRespose.getData();

                        if ( detail.getFromStationId() != null){

                            binding.titleText.setText(detail.getFromStationId());
                            }

                            binding.number.setText(String.valueOf(detail.getNumber()));
                        binding.date.setText(detail.getDate());
                        binding.description.setText(detail.getDescription());

                        setItemDetail(detail.getItems());

                        } else {
                        Toast.makeText(this, transferDetailRespose.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {

                    Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                });
        }

        private void setItemDetail(ArrayList<TransferItem>items){

        for (int i = 0; i < items.size(); i++) {

                TransferItem item = items.get(i);

                DetailItemTransferBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                        R.layout.detail_item_transfer, binding.itemsHolder, false);
                itemBinding.number.setText(item.getNumber());
                itemBinding.name.setText(item.getName());
                itemBinding.unit.setText(item.getUnit());
                itemBinding.quantity.setText(item.getQuantity());
                binding.itemsHolder.addView(itemBinding.getRoot());

            }

        }


    public void dialog(String njesia, boolean isCancel) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("");
        mBuilder.setMessage("A dëshironi të "+njesia);
        // Setting Negative "NO" Button
        mBuilder.setNegativeButton("JO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        mBuilder.setPositiveButton("PO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                if (isCancel){
                    cancelTransfer();
                } else {
                    aceptTransfer();
                }


            }
        });
        // Showing Alert Message
        mBuilder.show();
    }

    private void aceptTransfer(){

        apiService.acceptlTransfer(new TransferPost(preferences.getToken(),preferences.getUserId(),transferId + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferDetailRespose -> {
                    if (transferDetailRespose.getSuccess()) {
                        getStock();
                        } else {
                        Toast.makeText(this, transferDetailRespose.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                }, this::sendError);
    }

    private void cancelTransfer(){

        apiService.cancelTransfer(new TransferPost(preferences.getToken(),preferences.getUserId(),transferId + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferDetailRespose -> {
                    if (transferDetailRespose.getSuccess()) {

                        finish();


                    } else {
                        Toast.makeText(this, transferDetailRespose.getError().getText(), Toast.LENGTH_SHORT).show();
                    }
                }, this::sendError);
    }

    private void getStock() {
        apiService.getStock(new StockPost(preferences.getToken(), preferences.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stockResponse -> {
                    if (stockResponse.getSuccess()) {
                        realmHelper.saveStockItems(stockResponse.getData().getItems());
                        finish();
                    } else {
                        Toast.makeText(this, stockResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, this::sendError);
    }

    private void sendError(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String sStackTrace = sw.toString();
        ErrorPost errorPost = new ErrorPost();
        errorPost.setToken(preferences.getToken());
        errorPost.setUser_id(preferences.getUserId());
        errorPost.setUser_id(preferences.getUserId());
        ArrayList<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setMessage(sStackTrace);
        error.setDate("");
        errors.add(error);
        errorPost.setErrors(errors);
        apiService.sendError(errorPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                }, throwable1 -> {

                });
    }
}

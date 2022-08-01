package org.planetaccounting.saleAgent.transfere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivityTransfereBinding;
import org.planetaccounting.saleAgent.databinding.MyTransferItemsBinding;
import org.planetaccounting.saleAgent.databinding.OthersTransfereItemBinding;
import org.planetaccounting.saleAgent.model.Error;
import org.planetaccounting.saleAgent.model.ErrorPost;
import org.planetaccounting.saleAgent.model.UserToken;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.model.transfer.GetTransfere;
import org.planetaccounting.saleAgent.model.transfer.TransferPost;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.utils.Preferences;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class transfereActivity extends AppCompatActivity {
    private ActivityTransfereBinding binding;

    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_transfere);
        Kontabiliteti.getKontabilitetiComponent().inject(this);

        binding.addNewTransfer.setOnClickListener(view -> startActivity(new Intent(this,CreateTransferActivity.class)));

        allTransfer();
        }

        private void allTransfer(){

        apiService.getOtherTransfere(new UserToken(preferences.getUserId(),preferences.getToken())).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getTranseteResponse -> {

                    if (getTranseteResponse.getSuccess()){

                       ArrayList<GetTransfere> transfere  = getTranseteResponse.data;

                        otherTransfer(transfere);
                        myTransfer(transfere);


                        }


                    },
                        throwable -> {});
        }


        private void otherTransfer(ArrayList<GetTransfere> transfere){


            for(int i = 0; i<transfere.size();i++ ){
                GetTransfere getTransfere = transfere.get(i);

                if (!getTransfere.getFromStationId().equals(preferences.getStationId())){
                    return;
                }

                OthersTransfereItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                        R.layout.others_transfere_item, binding.otherTransfersHolder, false);

                itemBinding.numri.setText(getTransfere.getNumber() + "");

                String[] date = getTransfere.getDate().split(" ");
                itemBinding.data.setText(date[0] +"\n"+ date[1]);

                if (getTransfere.getFromStationId() != null){
                    itemBinding.njesia.setText(getTransfere.getFromStationName());
                }

                if (getTransfere.getType().equals("tp")){
                    itemBinding.acceptOtherTransfer.setVisibility(View.INVISIBLE);
                    itemBinding.cancelOtherTransfer.setVisibility(View.INVISIBLE);
                }

                itemBinding.cancelOtherTransfer.setOnClickListener(view ->
                        dialog("anuloni transferin ",true,itemBinding.getRoot(),getTransfere.getId())
                );

                itemBinding.acceptOtherTransfer.setOnClickListener(view ->
                        dialog("pranoni transferin ",false,itemBinding.getRoot(),getTransfere.getId())
                );

                itemBinding.seenOtherTransfer.setOnClickListener(view ->
                {
                    startActivity(new Intent(this,DetailTransferActivity.class)
                            .putExtra("transferId",getTransfere.getId())
                            .putExtra("from",0)
                            .putExtra("type",getTransfere.getType()));

                });

                binding.otherTransfersHolder.addView(itemBinding.getRoot());
            }

        }

    private void myTransfer(ArrayList<GetTransfere> transfere){


        for(int i = 0; i<transfere.size();i++ ){
            GetTransfere getTransfere = transfere.get(i);

            if (!getTransfere.getToStationId().equals(preferences.getStationId())){
                return;
            }

            MyTransferItemsBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(),
                    R.layout.my_transfer_items, binding.myTransfersHolder, false);

            itemBinding.numri.setText(getTransfere.getNumber() + "");

            String[] date = getTransfere.getDate().split(" ");
            itemBinding.data.setText(date[0] +"\n"+ date[1]);

            if (getTransfere.getFromStationId() != null){
                itemBinding.njesia.setText(getTransfere.getFromStationName());
            }

            if (getTransfere.getType().equals("tp")){
                itemBinding.cancelOtherTransfer.setVisibility(View.INVISIBLE);
            }

            itemBinding.cancelOtherTransfer.setOnClickListener(view ->
                    dialog("anuloni transferin ",true,itemBinding.getRoot(),getTransfere.getId())
            );


            itemBinding.seenOtherTransfer.setOnClickListener(view ->
            {
                startActivity(new Intent(this,DetailTransferActivity.class)
                        .putExtra("transferId",getTransfere.getId())
                        .putExtra("from",1)
                        .putExtra("type",getTransfere.getType()));

            });

            binding.myTransfersHolder.addView(itemBinding.getRoot());
        }

    }

    public void dialog(String njesia, boolean isCancel, View view,int transferId) {
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
                    cancelTransfer(transferId);

                } else {
                    aceptTransfer(transferId);
                }

            }
        });
        // Showing Alert Message
        mBuilder.show();
    }


    private void aceptTransfer(int transferId){

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

    private void cancelTransfer(int transferId){

        apiService.cancelTransfer(new TransferPost(preferences.getToken(),preferences.getUserId(),transferId + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transferDetailRespose -> {
                    if (transferDetailRespose.getSuccess()) {

                        binding.otherTransfersHolder.removeAllViews();
                        binding.myTransfersHolder.removeAllViews();
                        allTransfer();

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
                        binding.otherTransfersHolder.removeAllViews();
                        binding.myTransfersHolder.removeAllViews();
                        allTransfer();
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


    @Override
    protected void onResume() {
        super.onResume();

        binding.otherTransfersHolder.removeAllViews();
        binding.myTransfersHolder.removeAllViews();
        allTransfer();
    }
}

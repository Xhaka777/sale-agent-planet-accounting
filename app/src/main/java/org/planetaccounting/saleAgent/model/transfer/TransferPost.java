package org.planetaccounting.saleAgent.model.transfer;

public class TransferPost {
    String token;
    String user_id;
    // because backend have for one request transfer_id and for another transfere_Id :P
    String transfer_id;


    public TransferPost(String token, String user_id, String transfer_id) {
        this.token = token;
        this.user_id = user_id;
        this.transfer_id = transfer_id;
    }
}

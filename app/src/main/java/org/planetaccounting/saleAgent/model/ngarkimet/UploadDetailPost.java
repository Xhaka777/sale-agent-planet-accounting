package org.planetaccounting.saleAgent.model.ngarkimet;

public class UploadDetailPost {
    String token;
    String user_id;
    String transfer_id;

    public UploadDetailPost(String token, String user_id, String transfer_id) {
        this.token = token;
        this.user_id = user_id;
        this.transfer_id = transfer_id;
    }
}

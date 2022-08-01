package org.planetaccounting.saleAgent.raportet.raportmodels;
public class RaportsPostObject {
    String token;
    String user_id;
    int page;
    String Last_document_number;
    public void setToken(String token) {
        this.token = token;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLast_document_number(String last_document_number) {
        Last_document_number = last_document_number;
    }
}

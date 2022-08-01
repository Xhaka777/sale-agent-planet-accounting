package org.planetaccounting.saleAgent.events;

/**
 * Created by macb on 09/12/17.
 */

public class CompanySelectedEvent {
    private String companyId;

    public CompanySelectedEvent(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }
}

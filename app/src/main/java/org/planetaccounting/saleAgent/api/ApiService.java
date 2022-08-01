package org.planetaccounting.saleAgent.api;


import org.planetaccounting.saleAgent.aksionet.ActionPost;
import org.planetaccounting.saleAgent.aksionet.ActionResponse;
import org.planetaccounting.saleAgent.depozita.DepositPost;
import org.planetaccounting.saleAgent.depozita.DepositPostObject;
import org.planetaccounting.saleAgent.depozita.depositDetailResponse;
import org.planetaccounting.saleAgent.inkasimi.InkasimPost;
import org.planetaccounting.saleAgent.inkasimi.InkasimiDetailResponse;
import org.planetaccounting.saleAgent.kthemallin.ReturnPostObject;
import org.planetaccounting.saleAgent.model.BrandReponse;
import org.planetaccounting.saleAgent.model.CategoriesResponse;
import org.planetaccounting.saleAgent.model.CompanyInfoResponse;
import org.planetaccounting.saleAgent.model.ErrorPost;
import org.planetaccounting.saleAgent.model.InvoiceUploadResponse;
import org.planetaccounting.saleAgent.model.NotificationPost;
import org.planetaccounting.saleAgent.model.OrderDetailPost;
import org.planetaccounting.saleAgent.model.OrderDetailResponse;
import org.planetaccounting.saleAgent.model.UserToken;
import org.planetaccounting.saleAgent.model.VarehouseReponse;
import org.planetaccounting.saleAgent.model.clients.ClientCardPost;
import org.planetaccounting.saleAgent.model.clients.ClientCardResponse;
import org.planetaccounting.saleAgent.model.clients.ClientsResponse;
import org.planetaccounting.saleAgent.model.invoice.InvoicePostObject;
import org.planetaccounting.saleAgent.model.invoice.OrderPostObject;
import org.planetaccounting.saleAgent.model.login.LoginPost;
import org.planetaccounting.saleAgent.model.login.LoginResponse;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadDetailPost;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadDetailResponse;
import org.planetaccounting.saleAgent.model.ngarkimet.UploadsResponse;
import org.planetaccounting.saleAgent.model.order.CheckQuantity;
import org.planetaccounting.saleAgent.model.order.OrderCheckQuantityResponse;
import org.planetaccounting.saleAgent.model.order.OrderObject;
import org.planetaccounting.saleAgent.model.order.OrdersResponse;
import org.planetaccounting.saleAgent.model.pazari.PazarResponse;
import org.planetaccounting.saleAgent.model.stock.StockPost;
import org.planetaccounting.saleAgent.model.stock.StockResponse;
import org.planetaccounting.saleAgent.model.target.BaseTargetPost;
import org.planetaccounting.saleAgent.model.target.BaseTargetResponse;
import org.planetaccounting.saleAgent.model.target.BrandTargetResponse;
import org.planetaccounting.saleAgent.model.target.SkuTargetResponse;
import org.planetaccounting.saleAgent.model.target.TotalSaleTargetResponse;
import org.planetaccounting.saleAgent.model.transfer.BaseTranserResponse;
import org.planetaccounting.saleAgent.model.transfer.GetTranseteResponse;
import org.planetaccounting.saleAgent.model.transfer.GetTransferCreateResposnse;
import org.planetaccounting.saleAgent.model.transfer.GetTransferDetailResponse;
import org.planetaccounting.saleAgent.model.transfer.TransferCreatePost;
import org.planetaccounting.saleAgent.model.transfer.TransferPost;
import org.planetaccounting.saleAgent.raportet.raportmodels.GetInvoiceForReportResponse;
import org.planetaccounting.saleAgent.raportet.raportmodels.GetReportsListResponse;
import org.planetaccounting.saleAgent.raportet.raportmodels.InvoiceForReportObject;
import org.planetaccounting.saleAgent.raportet.raportmodels.RaportsPostObject;
import org.planetaccounting.saleAgent.vendors.VendorPostObject;
import org.planetaccounting.saleAgent.vendors.VendorSalerResponse;
import org.planetaccounting.saleAgent.vendors.VendorTypeResponse;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface ApiService {
    @POST("api/login")
    Observable<LoginResponse> loginUser(@Body LoginPost loginPost);

    @POST("api/get_stock_agent_saler_list")
    Observable<StockResponse> getStock(@Body StockPost stockPost);

    @POST("api/company_info")
    Observable<CompanyInfoResponse> getCompanyInfo(@Body StockPost stockPost);

    @POST("api/get_client_agent_saler_list")
    Observable<ClientsResponse> getClients(@Body StockPost stockPost);

    @POST("api/company_warehouse")
    Observable<VarehouseReponse> getWareHouses(@Body StockPost stockPost);

    @POST("api/set_device_token")
    Observable<BaseTranserResponse> setNewDevice(@Body NotificationPost notificationPost);

    @POST("api/delete_device_token")
    Observable<BaseTranserResponse> deleteDevice(@Body NotificationPost notificationPost);

    @POST("api/get_order_agent_saler_list")
    Observable<OrdersResponse> getOrders(@Body StockPost stockPost);

    @POST("api/get_transfer_list_agent_saler")
    Observable<UploadsResponse> getUploads(@Body StockPost stockPost);

    @POST("api/get_transfer_agent_saler")
    Observable<UploadDetailResponse> getUploadDetail(@Body UploadDetailPost uploadDetailPost);

    @POST("api/get_daily_sales__sale_agent")
    Observable<PazarResponse> getPazariDitor(@Body StockPost stockPost);

    @POST("api/get_purchase_type_list")
    Observable<VendorTypeResponse> getVendorTypes(@Body StockPost stockPost);

    @POST("api/get_vendor_agent_saler_list")
    Observable<VendorSalerResponse> getVendorSalers(@Body StockPost stockPost);

    @POST("api/set_purchase_invoice_sale_agent")
    Observable<InvoiceUploadResponse> postVendor(@Body VendorPostObject vendorPost);

    @POST("api/get_employee_benefits")
    Observable<BaseTargetResponse> getBaseTarget(@Body BaseTargetPost baseTargetPost);

    @POST("api/get_total_sale_benefit")
    Observable<TotalSaleTargetResponse> getTotalTarget(@Body BaseTargetPost baseTargetPost);

    @POST("api/get_cash_collection_benefit")
    Observable<TotalSaleTargetResponse> getCashTarget(@Body BaseTargetPost baseTargetPost);

    @POST("api/get_brand_benefit")
    Observable<BrandTargetResponse> getBrandTarget(@Body BaseTargetPost baseTargetPost);

    @POST("api/get_article_benefit")
    Observable<BrandTargetResponse> getArticleTarget(@Body BaseTargetPost baseTargetPost);

    @POST("api/get_power_sku_benefit")
    Observable<SkuTargetResponse> getSkuTarget(@Body BaseTargetPost baseTargetPost);

    @POST("api/get_stock_brands")
    Observable<BrandReponse> getBrands(@Body StockPost stockPost);

    @POST("api/get_stock_categories")
    Observable<CategoriesResponse> getCategories(@Body StockPost stockPost);

    @POST("api/get_client_card_agent_saler")
    Observable<ClientCardResponse> getClientsCard(@Body ClientCardPost clientCardPost);

    @POST("api/send_invoice_agent_saler")
    Observable<InvoiceUploadResponse> postFaturat(@Body InvoicePostObject invoicePostObject);

    @POST("api/set_return_invoice_agent_saler")
    Observable<InvoiceUploadResponse> postReturn(@Body ReturnPostObject returnPostObject);

    @POST("api/set_order_agent_saler_v2")
    Observable<InvoiceUploadResponse> postOrderFromInvoice(@Body OrderPostObject orderPostObject);

    @POST("api/send_order_agent_saler")
    Observable<InvoiceUploadResponse> postOrder(@Body OrderObject orderObject);

    @POST("api/check_item_quantity_agent_saler")
    Observable<OrderCheckQuantityResponse> checkQuantity(@Body CheckQuantity checkQuantity);

    @POST("api/get_order_agent_saler")
    Observable<OrderDetailResponse> getOrderDetail(@Body OrderDetailPost orderDetailPost);

    @POST("api/cancel_order_sale_agent")
    Observable<OrderDetailResponse> cancelOrder(@Body OrderDetailPost orderDetailPost);

    @POST("api/send_money_collection")
    Observable<InkasimiDetailResponse> postInkasimiDetail(@Body InkasimPost InkasimiDetail);

    @POST("api/company_bank_account")
    Observable<depositDetailResponse> getBankCompany(@Body DepositPost depositDetailPost);

    @POST("api/add_bank_deposite")
    Observable<depositDetailResponse> addDeposit(@Body DepositPostObject depositPostObject);

    @POST("api/get_sale_action_sale_agent")
    Observable<ActionResponse> getStockAction(@Body ActionPost Actionpost);

    @POST("api/set_error")
    Observable<ResponseBody> sendError(@Body ErrorPost errorPost);

    @POST("api/transferes_agent_saler")
    Observable<GetTranseteResponse> getOtherTransfere(@Body UserToken userToken);

    @POST("api/get_transferes_data_agent_saler")
    Observable<GetTransferDetailResponse> getDetailTransfer(@Body TransferPost transferPost);

    @POST("api/new_transfer_agent_saler")
    Observable<GetTransferCreateResposnse> createTransfer(@Body TransferCreatePost transferCreatePost);


    @POST("api/cancel_transfer_agent_saler")
    Observable<BaseTranserResponse> cancelTransfer(@Body TransferPost transferPost);

    @POST("api/accept_transfer_agent_saler")
    Observable<BaseTranserResponse> acceptlTransfer(@Body TransferPost transferPost);

    @POST("api/get_invoice_list")
    Observable<GetReportsListResponse> getRaportInvoiceList(@Body RaportsPostObject raportsPostObject);

    @POST("api/get_invoice_data")
    Observable<GetInvoiceForReportResponse> getRaportInvoiceDetail(@Body InvoiceForReportObject invoiceForReportObject);

    @POST("api/get_order_list")
    Observable<GetReportsListResponse> getRaportOrderList(@Body RaportsPostObject raportsPostObject);

    @POST("api/get_order_data")
    Observable<GetInvoiceForReportResponse> getRaportOrderDetail(@Body InvoiceForReportObject invoiceForReportObject);


    @POST("api/get_expense_list")
    Observable<GetReportsListResponse> getRaportExpenseList(@Body RaportsPostObject raportsPostObject);

    @POST("api/get_deposite_list")
    Observable<GetReportsListResponse> getRaportDepositeList(@Body RaportsPostObject raportsPostObject);

    @POST("api/get_payment_list")
    Observable<GetReportsListResponse> getRaportPaymentList(@Body RaportsPostObject raportsPostObject);

}

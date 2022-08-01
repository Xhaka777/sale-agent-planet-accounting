package org.planetaccounting.saleAgent.persistence;

import org.planetaccounting.saleAgent.model.role.DailyTrade;
import org.planetaccounting.saleAgent.model.role.FilterDailyTrade;
import org.planetaccounting.saleAgent.model.role.FilterStock;
import org.planetaccounting.saleAgent.model.role.Form;
import org.planetaccounting.saleAgent.model.role.InvoiceRole;
import org.planetaccounting.saleAgent.model.role.Main;
import org.planetaccounting.saleAgent.model.role.Role;
import org.planetaccounting.saleAgent.model.role.ShowEdit;
import org.planetaccounting.saleAgent.model.role.Stock;
import org.planetaccounting.saleAgent.model.role.TableDaily;
import org.planetaccounting.saleAgent.model.role.TableStock;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by tahirietrit on 5/21/18.
 */

public class RealmMigrationConfig implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();

        if (oldVersion == 1) {
            final RealmObjectSchema userSchema = schema.get("InvoicePost");
            userSchema.addField("fiscal_number", String.class);
            final RealmObjectSchema itemSchema = schema.get("InvoiceItemPost");
            userSchema.addField("isCollection", boolean.class);
            oldVersion++;

        }
        if (oldVersion == 2) {
            final RealmObjectSchema itemSchema = schema.get("InvoiceItemPost");
            itemSchema.addField("isCollection", boolean.class);
            oldVersion++;

        }
        if (oldVersion == 3) {
            final RealmObjectSchema itemSchema = schema.get("ActionArticleItems");
            itemSchema.addField("clientId", int.class);
            oldVersion++;

        }
        if (oldVersion == 4) {
            final RealmObjectSchema itemSchema = schema.get("InvoiceItemPost");
            itemSchema.addField("price_base", String.class);

            final RealmObjectSchema itemSchemaW = schema.get("InvoicePost");
            itemSchemaW.addField("partie_zip", String.class);
            itemSchemaW.addField("data_ship", String.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            RealmObjectSchema filterDaily = schema.create("FilterDailyTrade").addField("date", int.class);
            RealmObjectSchema filterStock = schema.create("FilterStock").addField("search", int.class);
            RealmObjectSchema showEdit = schema.create("ShowEdit")
                    .addField("show", int.class)
                    .addField("editable", int.class);

            RealmObjectSchema main = schema.create("Main")
                    .addField("update", int.class)
                    .addField("closeFiscalArc", int.class)
                    .addField("preparefiscalArc", int.class)
                    .addField("dailyTrade", int.class)
                    .addField("invoice", int.class)
                    .addField("stock", int.class)
                    .addField("cashCollection", int.class)
                    .addField("deposits", int.class)
                    .addField("reports", int.class)
                    .addField("stockReturn", int.class)
                    .addField("transfers", int.class)
                    .addField("clients", int.class)
                    .addField("targets", int.class)
                    .addField("orders", int.class)
                    .addField("uploads", int.class)
                    .addField("expenses", int.class)
                    .addField("actions", int.class);


            RealmObjectSchema tableDaily = schema.create("TableDaily")
                    .addField("date", int.class)
                    .addField("invoice_sale", int.class)
                    .addField("bill_sale", int.class)
                    .addField("deposits", int.class)
                    .addField("cash", int.class)
                    .addField("cashCollection", int.class)
                    .addField("expenses", int.class)
                    .addField("total", int.class)
                    .addField("dept", int.class);


            RealmObjectSchema dailyTrade = schema.create("DailyTrade")
                    .addField("xs", org.planetaccounting.saleAgent.model.role.TableDaily.class)
                    .addField("filter", FilterDailyTrade.class);


            RealmObjectSchema form = schema.create("Form")
                    .addField("date", ShowEdit.class)
                    .addField("invoice_number", ShowEdit.class)
                    .addField("client", ShowEdit.class)
                    .addField("client_station", ShowEdit.class)
                    .addField("client_discount", ShowEdit.class)
                    .addField("article", ShowEdit.class)
                    .addField("article_number", ShowEdit.class)
                    .addField("article_unit", ShowEdit.class)
                    .addField("article_discount", ShowEdit.class)
                    .addField("price_no_vat", ShowEdit.class)
                    .addField("price_of_vat", ShowEdit.class)
                    .addField("amount_no_vat", ShowEdit.class)
                    .addField("amount_of_vat", ShowEdit.class)
                    .addField("amount_with_vat", ShowEdit.class)
                    .addField("sum_amount_no_vat", ShowEdit.class)
                    .addField("sum_discount", ShowEdit.class)
                    .addField("sum_vat", ShowEdit.class)
                    .addField("sum_amount_with_vat", ShowEdit.class)
                    .addField("create_invoice", ShowEdit.class)
                    .addField("create_bill", ShowEdit.class);

            RealmObjectSchema invoiceRole = schema.create("InvoiceRole")
                    .addField("form", Form.class);


            RealmObjectSchema tableStock = schema.create("TableStock")
                    .addField("image", int.class)
                    .addField("number", int.class)
                    .addField("article", int.class)
                    .addField("unit", int.class)
                    .addField("quantity", int.class)
                    .addField("amount", int.class);


            RealmObjectSchema stock = schema.create("Stock")
                    .addField("tableS", TableStock.class)
                    .addField("filter", FilterStock.class);


            RealmObjectSchema role = schema.create("Role")
                    .addField("main", Main.class)
                    .addField("dailyTrade", DailyTrade.class)
                    .addField("invoice", InvoiceRole.class)
                    .addField("stock", Stock.class);

            oldVersion++;

        }

        if (oldVersion < 8 ) {
            final RealmObjectSchema itemSchema = schema.get("InvoiceItemPost");
            itemSchema.addField("amount_no_vat", String.class);

            final RealmObjectSchema itemSchema2 = schema.get("InvoicePost");
            itemSchema.addField("type", String.class);

            oldVersion++;



        }



    }




}
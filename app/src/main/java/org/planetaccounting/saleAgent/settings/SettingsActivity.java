package org.planetaccounting.saleAgent.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.planetaccounting.saleAgent.Kontabiliteti;
import org.planetaccounting.saleAgent.MainActivity;
import org.planetaccounting.saleAgent.R;
import org.planetaccounting.saleAgent.api.ApiService;
import org.planetaccounting.saleAgent.databinding.ActivitySettingsBinding;
import org.planetaccounting.saleAgent.persistence.RealmHelper;
import org.planetaccounting.saleAgent.settings.escpostsettngs.ESCSettingsActivity;
import org.planetaccounting.saleAgent.utils.BrowserSupportMethod;
import org.planetaccounting.saleAgent.utils.LocaleManager;
import org.planetaccounting.saleAgent.utils.Preferences;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    @Inject
    ApiService apiService;
    @Inject
    Preferences preferences;
    @Inject
    RealmHelper realmHelper;
    @Inject
    LocaleManager localeManager;

    private  final String termsAndPolicyUrl = "http://planetaccounting.org/www/privacy_policy ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        ((Kontabiliteti) getApplication()).getKontabilitetiComponent().inject(this);
        binding.languageTxt.setText(preferences.getLanguage());
        binding.changeLanguageButton.setOnClickListener(view -> showTimeAlert());
        binding.termsPolicyButton.setOnClickListener(view -> startActivity(new Intent(BrowserSupportMethod.getBrowserIntent(termsAndPolicyUrl))));
        binding.printerButton.setOnClickListener(view -> startActivity(new Intent(this, ESCSettingsActivity.class)));
        binding.aboutButton.setOnClickListener(view -> startActivity(new Intent(this, AboutPage.class)));
    }

    private void showTimeAlert() {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

//        // Set a title for alert dialog
//        builder.setTitle("Choose a color...");

        // Initializing an array of colors
        final String[] languages = new String[]{
                "EN",
                "SHQ",
                "GR",
                "TR"
                ,"SR"
        };

        // Set the list of items for alert dialog
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:
                    changeLanguage("en");
                    break;
                case 1:
                    changeLanguage("shq");
                    break;
                case 2:
                    changeLanguage("gr");
                    break;
                case 3:
                    changeLanguage("tr");
                    break;
                case 4:
                    changeLanguage("sr");
                    break;
            }
            }
        });

        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    void changeLanguage(String language){
        localeManager.setNewLanguage(language);
        Intent newLanguage = new Intent(SettingsActivity.this, MainActivity.class);
        newLanguage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newLanguage);
        finish();
    }
}

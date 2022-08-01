package org.planetaccounting.saleAgent.settings.escpostsettngs

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.planetaccounting.saleAgent.Kontabiliteti
import org.planetaccounting.saleAgent.R
import org.planetaccounting.saleAgent.api.ApiService
import org.planetaccounting.saleAgent.databinding.ActivityEscsettingsBinding
import org.planetaccounting.saleAgent.persistence.RealmHelper
import org.planetaccounting.saleAgent.utils.LocaleManager
import org.planetaccounting.saleAgent.utils.Preferences
import javax.inject.Inject

class ESCSettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivityEscsettingsBinding
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var preferences: Preferences
    @Inject
    lateinit var realmHelper: RealmHelper
    @Inject
    lateinit var localeManager: LocaleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_escsettings)
        Kontabiliteti.getKontabilitetiComponent().inject(this)
        initView()
        action()

    }

    fun initView(){
        binding.kodiSwitch.isChecked = preferences.kodeSettings
        binding.barcodeSwitch.isChecked = preferences.barcodeSettings
        binding.quantitySwitch.isChecked = preferences.quantitySettings
        binding.discountSwitch.isChecked = preferences.discountSettings
        binding.discountExtraSwitch.isChecked = preferences.discountExtraSettings
    }

    fun action(){
        binding.kodiSwitch.setOnCheckedChangeListener { compoundButton, check ->

            if (binding.kodiSwitch.isChecked == check){
                preferences.kodeSettings = check
            }

        }

        binding.barcodeSwitch.setOnCheckedChangeListener { compoundButton, check ->

            if (binding.barcodeSwitch.isChecked == check){
                preferences.barcodeSettings = check
            }

        }

        binding.quantitySwitch.setOnCheckedChangeListener { compoundButton, check ->

            if (binding.quantitySwitch.isChecked == check){
                preferences.quantitySettings = check
            }

        }

        binding.discountSwitch.setOnCheckedChangeListener { compoundButton, check ->

            if (binding.discountSwitch.isChecked == check){
                preferences.discountSettings = check
            }

        }

        binding.discountExtraSwitch.setOnCheckedChangeListener { compoundButton, check ->

            if (binding.discountExtraSwitch.isChecked == check){
                preferences.discountExtraSettings = check
            }

        }
    }

}

package org.planetaccounting.saleAgent.clients.infos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {

        // brenda vendosim fragmentat per Adresen, Opsionet, Lokacionet , Bank dhe Shenimet (sipas deshires)...
         Fragment fragment = null;
         if(position == 0){
             fragment = new AdresaFragment();
         }else if(position == 1){
             fragment = new OptionsFragment();
         }else if(position == 2){
             fragment = new LocationFragment();
         }else if(position == 3){
             fragment = new BankFragment();
         }else if(position == 4){
             fragment = new ShenimeFragment();
         }
         return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = null;
        if(position == 0){
            title = "Adresa";
        }else if(position == 1){
            title = "Opsionet";
        }else if (position == 2){
            title = "Lokacioni";
        }else if (position == 3){
            title = "Bank";
        }else if (position == 4){
            title = "Shenime";
        }

        return title;
    }
}

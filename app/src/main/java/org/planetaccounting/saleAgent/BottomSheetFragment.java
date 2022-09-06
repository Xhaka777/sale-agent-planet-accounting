package org.planetaccounting.saleAgent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import org.planetaccounting.saleAgent.helper.LocaleHelper;
import org.planetaccounting.saleAgent.login.LoginActivity;

import java.util.Locale;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    ImageView tickEnglish;
    ImageView tickAlbania;
    ImageView tickSerbia;
    ImageView tickTurkey;
    ImageView tickGreek;
    MainActivity main;
    Locale myLocale;
    String currentLang;
    String currentLanguage = "sq";


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Set the custom view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(view);
        main = new MainActivity();

        //removing all tick mark from the layout
        tickEnglish = (ImageView) view.findViewById(R.id.tick_english);
        tickEnglish.setVisibility(View.GONE);
        tickAlbania = (ImageView) view.findViewById(R.id.tick_albania);
        tickAlbania.setVisibility(View.GONE);
        tickSerbia = (ImageView) view.findViewById(R.id.tick_serbia);
        tickSerbia.setVisibility(View.GONE);
        tickTurkey = (ImageView) view.findViewById(R.id.tick_turk);
        tickTurkey.setVisibility(view.GONE);
        tickGreek = (ImageView) view.findViewById(R.id.tick_greek);
        tickGreek.setVisibility(view.GONE);
        currentLanguage = getActivity().getIntent().getStringExtra(currentLang);

        //case for making the tick mark alive
        switch (LocaleHelper.getLanguage(getContext())) {
            case "sq":
                tickAlbania.setVisibility(View.VISIBLE);
                break;
            case "en":
                tickEnglish.setVisibility(View.VISIBLE);
                break;
            case "sr":
                tickSerbia.setVisibility(View.VISIBLE);
                break;
            case "tr":
                tickTurkey.setVisibility(View.VISIBLE);
                break;
            case "gk":
                tickGreek.setVisibility(View.VISIBLE);
                break;
            default:
                System.out.println("no match");
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    String state = "";

                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING: {
                            state = "DRAGGING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_SETTLING: {
                            state = "SETTLING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_EXPANDED: {
                            state = "EXPANDED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            state = "COLLAPSED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_HIDDEN: {
                            dismiss();
                            state = "HIDDEN";
                            break;
                        }
                    }
//                    Toast.makeText(getContext(), "Bottom Sheet State Changed to: " + state, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }

        //close icon of bottom sheet
        ImageView imageViewClose = (ImageView) view.findViewById(R.id.imageView);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        //onclick for albanian flag
        ImageView flagAlbania = (ImageView) view.findViewById(R.id.flagView_albania);
        flagAlbania.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("sq");
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        //onclick on english language
        ImageView flagEnglish = (ImageView) view.findViewById(R.id.flagView_english);
        flagEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        //onclick for serbian flag
        ImageView flagSerbian = (ImageView) view.findViewById(R.id.flagView_serbia);
        flagSerbian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("sr");
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        //onclick for turkey flag
        ImageView flagTurkey = (ImageView) view.findViewById(R.id.flagView_turkey);
        flagTurkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("tr");
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        //onclick for greek flag
        ImageView flagGreek = (ImageView) view.findViewById(R.id.tick_greek);
        flagGreek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("gr");
                //to close the vottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    //to change the language and refresh the screen
    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            Context context = LocaleHelper.setLocale(getContext(), localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(getContext(), LoginActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            //Toast.makeText(getActivity(), "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }
}
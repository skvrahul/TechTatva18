package in.mittt.tt18.utilities;

import android.content.Context;
import android.util.Log;

import in.mittt.tt18.R;

/**
 * Created by Saptarshi on 2/13/2018.
 */

public class IconCollection {

    public IconCollection() {
    }

    public int getIconResource(Context context, String catName) {
        if (catName == null) return R.mipmap.ic_launcher;

        switch (catName.toLowerCase().trim()) {
            case "acumen":
                return R.drawable.acumen;
            case "airborne":
                return R.drawable.airborne;
            case "alacrity":
                return R.drawable.alacrity;
            case "bizzmaestro":
                return R.drawable.bizzmaestro;
            case "bizzventure":
                return R.drawable.bizzventure;
            case "cheminova":
                return R.drawable.cheminova;
            case "chrysalis":
                return R.drawable.chrysalis;
            case "constructure":
                return R.drawable.constructure;
            case "cosmic con":
                return R.drawable.cosmic_con;
            case "cryptoss":
                return R.drawable.cryptoss;
            case "drone racing league":
                return R.drawable.idc_logo_black_final;
            case "electrific":
                return R.drawable.electrific;
            case "energia":
                return R.drawable.energia;
            case "epsilon":
                return R.drawable.epsilon;
            case "fuel rc":
                return  R.drawable.fuelrclogo;
            case "kraftwagen":
                return R.drawable.kraftwagen;
            case "mechanize":
                return R.drawable.mechanize;
            case "mechatron":
                return R.drawable.mechatron;
            case "overclocked by techquila":
                return  R.drawable.techquila;
            case "paper presentation":
                return R.drawable.paper_presentation_logo;
            case "questionable intelligence":
                return R.drawable.qi;
            case "robotrek":
                return R.drawable.robotrek;
            case "robowars":
                return R.drawable.robowars;
            case "techquila":
                return  R.drawable.techquila;
            case "turing":
                return R.drawable.turing;
            case "vedanth":
                return R.drawable.vedanth;

            default: {
                String TAG = "IconCollection";
                Log.i(TAG, catName);
                return R.mipmap.ic_launcher;
            }
        }

    }


}
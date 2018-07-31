package in.mittt.tt18.utilities;

import android.content.Context;
import android.util.Log;

import in.mittt.tt18.R;

/**
 * Created by Saptarshi on 2/13/2018.
 */

public class IconCollection {

    //
//    private final int animania = R.drawable.animania;
//    private final int anubhuti = R.drawable.anubhuti;
//    private final int crescendo = R.drawable.crescendo;
//    private final int dramebaaz = R.drawable.dramebaaz;
//    private final int eq_iq = R.drawable.eq_iq;
//
//    private final int ergo = R.drawable.ergo;
//    private final int footloose = R.drawable.footloose;
//    private final int haute_couture = R.drawable.haute_couture;
//    private final int iridescent = R.drawable.iridescent;
//    private final int kalakriti = R.drawable.kalakriti;
//    private final int lensation = R.drawable.lensation;
//    private final int paradigm_shift = R.drawable.paradigm_shift;
//    private final int psychus = R.drawable.psychus;
//    private final int gaming = R.drawable.gaming;
//    private final int xventure = R.drawable.xventure;
    private final int dummy = R.mipmap.ic_launcher;

    String TAG = "IconCollection";

    public IconCollection() {
    }

    public int getIconResource(Context context, String catName) {
        if (catName == null) return R.mipmap.ic_launcher;

        switch (catName.toLowerCase()) {
            case "crescendo":
                return dummy;
            case "eq iq":
                return dummy;
            case "lensation":
                return dummy;
            case "dramebaaz":
                return dummy;
            case "footloose":
                return dummy;
            case "iridescent":
                return dummy;
            case "animania":
                return dummy;
            case "anubhuti":
                return dummy;
            case "psychus":
                return dummy;
            case "haute couture":
                return dummy;
            case "xventure":
                return dummy;
            case "kalakriti":
                return dummy;
            case "paradigm shift":
                return dummy;
            case "ergo":
                return dummy;
            case "gaming":
                return dummy;

            default: {
                Log.i(TAG, catName);
                return R.mipmap.ic_launcher;
            }
        }

    }


}
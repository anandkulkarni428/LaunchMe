package com.anand.launchme.Utills;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceManager {
    private static final String PREFERENCE_NAME = "test";
    private static final String KEY_SELECTION = "key_selection";

    Context mContext;

    public PreferenceManager(Context context) {
        mContext = context;
    }

    public void setSelection(int pos) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        sharedPref.edit().putInt(KEY_SELECTION, pos).commit();
    }

    public int getSelection() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPref.getInt(KEY_SELECTION, 0);
    }
}

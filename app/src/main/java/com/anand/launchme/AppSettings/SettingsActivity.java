package com.anand.launchme.AppSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anand.launchme.Apps.GetApps;
import com.anand.launchme.R;
import com.anand.launchme.Utills.AppPreferences;
import com.anand.launchme.Utills.PreferenceManager;

import java.util.ArrayList;

public class SettingsActivity extends Activity {

    private String tutorialsName;
    private int prefPosition;

    private Spinner gridNospinner;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        gridNospinner = findViewById(R.id.grid_no_spinner);

        preferenceManager = new PreferenceManager(SettingsActivity.this);


        ArrayList<Integer> gridNoArrayList = new ArrayList<>();
        gridNoArrayList.add(5);
        gridNoArrayList.add(4);
        gridNoArrayList.add(3);
        gridNoArrayList.add(2);
        gridNoArrayList.add(6);
        gridNoArrayList.add(7);
        gridNoArrayList.add(8);
        gridNoArrayList.add(9);


        ArrayAdapter<Integer> gridNoArrayAdapter = new ArrayAdapter<Integer>(SettingsActivity.this,
                R.layout.spinner_item, gridNoArrayList);


        gridNospinner.setAdapter(gridNoArrayAdapter);
        gridNospinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferenceManager.setSelection(position);
                tutorialsName = parent.getItemAtPosition(position).toString();
                AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.GRID_NO,tutorialsName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gridNospinner.setSelection(preferenceManager.getSelection());
                prefPosition = preferenceManager.getSelection();
                AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.GRID_NO,String.valueOf(prefPosition));
            }
        });


        tutorialsName = String.valueOf(prefPosition);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, GetApps.class);
        intent.putExtra("GRID_NO", tutorialsName);
        Log.d("TAG_NO", tutorialsName + "");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gridNospinner.setSelection(preferenceManager.getSelection());
    }
}
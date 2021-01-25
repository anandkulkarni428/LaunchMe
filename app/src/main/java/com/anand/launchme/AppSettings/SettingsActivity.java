package com.anand.launchme.AppSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.anand.launchme.Apps.GetApps;
import com.anand.launchme.R;
import com.anand.launchme.Utills.AppPreferences;
import com.anand.launchme.Utills.PreferenceManager;

import java.util.ArrayList;

public class SettingsActivity extends Activity {

    private String gridCurrentNo;
    private int prefPosition;
    private boolean appName = true;

    private Spinner gridNospinner;
    private Switch applicationNameswitch;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        gridNospinner = findViewById(R.id.grid_no_spinner);
        applicationNameswitch = findViewById(R.id.an_switch);

        preferenceManager = new PreferenceManager(SettingsActivity.this);


        ArrayList<Integer> gridNoArrayList = new ArrayList<>();
        gridNoArrayList.add(4);
        gridNoArrayList.add(5);
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
                gridCurrentNo = parent.getItemAtPosition(position).toString();
                AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.GRID_NO, gridCurrentNo);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gridNospinner.setSelection(preferenceManager.getSelection());
                prefPosition = preferenceManager.getSelection();
                AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.GRID_NO, String.valueOf(prefPosition));
            }
        });


        gridCurrentNo = String.valueOf(prefPosition);

        if (appName){
            applicationNameswitch.setChecked(true);
        } else {
            applicationNameswitch.setChecked(false);
        }

        applicationNameswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                appName = b;
                Toast.makeText(SettingsActivity.this, "" + b, Toast.LENGTH_SHORT).show();
                AppPreferences.getInstance(getApplicationContext()).put(AppPreferences.Key.GRID_NO, appName);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, GetApps.class);
        intent.putExtra("GRID_NO", gridCurrentNo);
        Log.d("TAG_NO", gridCurrentNo + "");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gridNospinner.setSelection(preferenceManager.getSelection());
    }
}
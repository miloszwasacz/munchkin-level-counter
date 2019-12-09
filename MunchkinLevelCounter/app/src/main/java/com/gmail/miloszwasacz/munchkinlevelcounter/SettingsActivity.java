package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity
{
    String playerList;
    int maxPlayerLevelValue;
    String SharedPrefs;
    Boolean exit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Ustawienia");
        Intent intent = getIntent();
        playerList = intent.getStringExtra("EXTRA_LIST");

        //Sprawdź zapisany zakres poziomów
        SharedPreferences prefs = getSharedPreferences(SharedPrefs, MODE_PRIVATE);
        int maksymalnyPoziom = prefs.getInt("MaksymalnyPoziomPrefs", 10);
        int minimalnyPoziom = prefs.getInt("MinimalnyPoziomPrefs", 1);
        ((Variables)getApplication()).setMaxPlayerLevel(maksymalnyPoziom);
        ((Variables)getApplication()).setMinLevel(minimalnyPoziom);

        final Switch switchGamemode = findViewById(R.id.switchGamemode);
        final Switch switchDungeon = findViewById(R.id.switchDungeon);

        //Zaktualizuj pozycje switch'y
        switch (((Variables)getApplication()).getMaxPlayerLevel())
        {
            case 10:
                switchGamemode.setChecked(false);
                switchDungeon.setChecked(false);
                break;
            case 11:
                switchGamemode.setChecked(false);
                switchDungeon.setChecked(true);
                break;
            case 20:
                switchGamemode.setChecked(true);
                switchDungeon.setChecked(false);
                break;
            case 22:
                switchGamemode.setChecked(true);
                switchDungeon.setChecked(true);
                break;
        }

        //Ustaw maksymalny poziom gracza
        switchGamemode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    if(switchDungeon.isChecked())
                        maxPlayerLevelValue = 22;
                    else
                        maxPlayerLevelValue = 20;
                }
                else
                {
                    if(switchDungeon.isChecked())
                        maxPlayerLevelValue = 11;
                    else
                        maxPlayerLevelValue = 10;
                }
            }
        });
        //Ustaw maksymalny poziom gracza
        switchDungeon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    if(switchGamemode.isChecked())
                        maxPlayerLevelValue = 22;
                    else
                        maxPlayerLevelValue = 11;
                }
                else
                {
                    if(switchGamemode.isChecked())
                        maxPlayerLevelValue = 20;
                    else
                        maxPlayerLevelValue = 10;
                }
            }
        });
    }

    //Obsługa strzałeczki w tył
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    //Wyjście z Activity
    @Override
    public void onBackPressed()
    {
        //Okienko z pytaniem o zapisanie ustawień
        new AlertDialog.Builder(this)
                .setTitle("Zapisać zmiany?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ((Variables)getApplication()).setMaxPlayerLevel(maxPlayerLevelValue);

                        SharedPreferences.Editor editor = getSharedPreferences(SharedPrefs, MODE_PRIVATE).edit();
                        editor.putInt("MaksymalnyPoziomPrefs", ((Variables)getApplication()).getMaxPlayerLevel());
                        editor.putInt("MinimalnyPoziomPrefs", ((Variables)getApplication()).getMinLevel());
                        editor.commit();

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("resultList", playerList);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                })
                .setNegativeButton("Anuluj", null)
                .setNeutralButton("Nie", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("resultList", playerList);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                })
                .create()
                .show();
        //finish();
    }
}

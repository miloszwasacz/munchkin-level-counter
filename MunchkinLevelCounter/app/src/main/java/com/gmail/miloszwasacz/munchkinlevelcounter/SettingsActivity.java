package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity
{
    String playerList;
    int maxPlayerLevel;
    int minLevel;
    boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Ustawienia");
        Intent intent = getIntent();
        playerList = intent.getStringExtra("EXTRA_LIST");
        maxPlayerLevel = intent.getIntExtra("EXTRA_MAX_LEVEL", getResources().getInteger(R.integer.deafult_rules));
        minLevel = intent.getIntExtra("EXTRA_MIN_LEVEL", getResources().getInteger(R.integer.deafult_min_level));
        editMode = intent.getBooleanExtra("EXTRA_EDIT_MODE", true);

        final Switch switchGamemode = findViewById(R.id.switchGamemode);
        final Switch switchDungeon = findViewById(R.id.switchDungeon);

        //Zaktualizuj pozycje switch'y
        if(maxPlayerLevel == getResources().getInteger(R.integer.deafult_rules))
        {
            switchGamemode.setChecked(false);
            switchDungeon.setChecked(false);
        }
        else if(maxPlayerLevel == getResources().getInteger(R.integer.dungeon_rules))
        {
            switchGamemode.setChecked(false);
            switchDungeon.setChecked(true);
        }
        else if(maxPlayerLevel == getResources().getInteger(R.integer.epic_rules))
        {
            switchGamemode.setChecked(true);
            switchDungeon.setChecked(false);
        }
        else if(maxPlayerLevel == getResources().getInteger(R.integer.epic_dungeon_rules))
        {
            switchGamemode.setChecked(true);
            switchDungeon.setChecked(true);
        }

        //Ustaw maksymalny poziom gracza
        switchGamemode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    if(switchDungeon.isChecked())
                        maxPlayerLevel = getResources().getInteger(R.integer.epic_dungeon_rules);
                    else
                        maxPlayerLevel = getResources().getInteger(R.integer.epic_rules);
                }
                else
                {
                    if(switchDungeon.isChecked())
                        maxPlayerLevel = getResources().getInteger(R.integer.dungeon_rules);
                    else
                        maxPlayerLevel = getResources().getInteger(R.integer.deafult_rules);
                }
            }
        });
        //Ustaw maksymalny poziom gracza
        switchDungeon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    if(switchGamemode.isChecked())
                        maxPlayerLevel = getResources().getInteger(R.integer.epic_dungeon_rules);
                    else
                        maxPlayerLevel = getResources().getInteger(R.integer.dungeon_rules);
                }
                else
                {
                    if(switchGamemode.isChecked())
                        maxPlayerLevel = getResources().getInteger(R.integer.epic_rules);
                    else
                        maxPlayerLevel = getResources().getInteger(R.integer.deafult_rules);
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
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultList", playerList);
        returnIntent.putExtra("resultMaxLevel", maxPlayerLevel);
        returnIntent.putExtra("resultMinLevel", minLevel);
        returnIntent.putExtra("resultEditMode", editMode);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}

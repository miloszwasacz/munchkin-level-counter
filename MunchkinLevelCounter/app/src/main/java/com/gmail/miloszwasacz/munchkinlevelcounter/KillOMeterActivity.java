package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class KillOMeterActivity extends AppCompatActivity
{
    int playerLevel;
    int playerPosition;
    EditText editTextPlayerLevel;
    EditText editTextPlayerItems;
    EditText editTextPlayerBonus;
    EditText editTextPlayerSummary;
    EditText editTextMonsterLevel;
    EditText editTextMonsterEnhancer;
    EditText editTextMonsterBonus;
    EditText editTextMonsterSummary;
    String playerList;
    int maxPlayerLevel;
    int noMaxValue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_o_meter);

        getSupportActionBar().setTitle("Kill-O-Meter");
        maxPlayerLevel = 22;

        //Odbieranie danych o poziomie i pozycji
        Intent intent = getIntent();
        String playerName = intent.getStringExtra("EXTRA_NAME");
        playerLevel = intent.getIntExtra("EXTRA_LEVEL", 1);
        playerPosition = intent.getIntExtra("EXTRA_POSITION", 0);
        playerList = intent.getStringExtra("EXTRA_LIST");

        //Ustawianie poziomu i nazwy gracza
        TextView textViewPlayerName = findViewById(R.id.textViewPlayerName);
        textViewPlayerName.setText(playerName);
        editTextPlayerLevel = findViewById(R.id.editTextPlayerLevel);
        editTextPlayerLevel.setText(String.valueOf(playerLevel));

        //Kontrolki gracza
        ImageView imageViewPlayerLevelRemove = findViewById(R.id.imageViewPlayerLevelRemove);
        ImageView imageViewPlayerLevelAdd = findViewById(R.id.imageViewPlayerLevelAdd);

        ImageView imageViewPlayerItemsRemove = findViewById(R.id.imageViewPlayerItemsRemove);
        ImageView imageViewPlayerItemsAdd = findViewById(R.id.imageViewPlayerItemsAdd);
        editTextPlayerItems = findViewById(R.id.editTextPlayerItems);

        ImageView imageViewPlayerBonusRemove = findViewById(R.id.imageViewPlayerBonusRemove);
        ImageView imageViewPlayerBonusAdd = findViewById(R.id.imageViewPlayerBonusAdd);
        editTextPlayerBonus = findViewById(R.id.editTextPlayerBonus);

        editTextPlayerSummary = findViewById(R.id.editTextPlayerSummary);

        //Kontrolki potwora
        ImageView imageViewMonsterLevelRemove = findViewById(R.id.imageViewMonsterLevelRemove);
        ImageView imageViewMonsterLevelAdd = findViewById(R.id.imageViewMonsterLevelAdd);
        editTextMonsterLevel = findViewById(R.id.editTextMonsterLevel);

        ImageView imageViewMonsterEnhancerRemove = findViewById(R.id.imageViewMonsterEnhancerRemove);
        ImageView imageViewMonsterEnhancerAdd = findViewById(R.id.imageViewMonsterEnhancerAdd);
        editTextMonsterEnhancer = findViewById(R.id.editTextMonsterEnhancer);

        ImageView imageViewMonsterBonusRemove = findViewById(R.id.imageViewMonsterBonusRemove);
        ImageView imageViewMonsterBonusAdd = findViewById(R.id.imageViewMonsterBonusAdd);
        editTextMonsterBonus = findViewById(R.id.editTextMonsterBonus);

        editTextMonsterSummary = findViewById(R.id.editTextMonsterSummary);

        //Zsumuj moc gracza i potwora
        updateSummary();

        //Odejmij poziom graczowi
        imageViewPlayerLevelRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(1, maxPlayerLevel, editTextPlayerLevel, "Remove", 1);
            }
        });

        //Dodaj poziom graczowi
        imageViewPlayerLevelAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(1, maxPlayerLevel, editTextPlayerLevel, "Add", 1);
            }
        });

        //Sprawdź czy poziom gracza jest w zakrasie poziomów
        editTextPlayerLevel.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                editValueInBracket(1, maxPlayerLevel, editTextPlayerLevel, "Check", 1);
            }
        });

        //Odejmij bonus z przedmiotów gracza
        imageViewPlayerItemsRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextPlayerItems, "Remove", 1);
            }
        });

        //Dodaj bonus z przedmiotów gracza
        imageViewPlayerItemsAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextPlayerItems, "Add", 1);
            }
        });

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerItems.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                editValueInBracket(0, noMaxValue, editTextPlayerItems, "Check", 1);
            }
        });

        //Odejmij bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextPlayerBonus, "Remove", 1);
            }
        });

        //Dodaj bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextPlayerBonus, "Add", 1);
            }
        });

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerBonus.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                editValueInBracket(0, noMaxValue, editTextPlayerBonus, "Check", 1);
            }
        });



        //Odejmij poziom potworowi
        imageViewMonsterLevelRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(1, noMaxValue, editTextMonsterLevel, "Remove", 1);
            }
        });

        //Dodaj poziom potworowi
        imageViewMonsterLevelAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(1, noMaxValue, editTextMonsterLevel, "Add", 1);
            }
        });

        //Sprawdź czy poziom potwora jest większy od 0 i nie jest pusty
        editTextMonsterLevel.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                editValueInBracket(1, noMaxValue, editTextMonsterLevel, "Check", 1);
            }
        });

        //Odejmij wzmacniacz potwora
        imageViewMonsterEnhancerRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextMonsterEnhancer, "Remove", 5);
            }
        });

        //Dodaj wzmacniacz potwora
        imageViewMonsterEnhancerAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextMonsterEnhancer, "Add", 5);
            }
        });

        //Sprawdź czy wzmacniacz potwora nie jest pusty
        editTextMonsterEnhancer.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                editValueInBracket(0, noMaxValue, editTextMonsterEnhancer, "Check", 5);
            }
        });

        //Odejmij bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextMonsterBonus, "Remove", 1);
            }
        });

        //Dodaj bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(0, noMaxValue, editTextMonsterBonus, "Add", 1);
            }
        });

        //Sprawdź czy bonus z jednorazowego użytku potwora nie jest pusty
        editTextMonsterBonus.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                editValueInBracket(0, noMaxValue, editTextMonsterBonus, "Check", 1);
            }
        });
    }

    //Metoda "Zsumuj moc gracza i potwora"
    public void updateSummary ()
    {
        editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
        editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
    }


    public void editValueInBracket(int minValue, int maxValue, EditText bracket, String operation, int changingValue)
    {
        if(operation == "Add")
        {
            if(bracket.getText().toString().equals("") || Integer.parseInt(bracket.getText().toString()) < minValue)
                bracket.setText(String.valueOf(minValue));
            else
            {
                if(maxValue == noMaxValue)
                {
                    if(changingValue == 1)
                        bracket.setText(String.valueOf(Integer.parseInt(bracket.getText().toString()) + changingValue));
                    if(changingValue == 5)
                        bracket.setText(String.valueOf(((Integer.parseInt(bracket.getText().toString()) / 5) * 5) + changingValue));
                }
                else
                {
                    if(Integer.parseInt(bracket.getText().toString()) < maxValue)
                        bracket.setText(String.valueOf(Integer.parseInt(bracket.getText().toString()) + changingValue));
                }
            }

        }
        if(operation == "Remove")
        {
            if(bracket.getText().toString().equals("") || Integer.parseInt(bracket.getText().toString()) < minValue)
                bracket.setText(String.valueOf(minValue));
            else
            {
                if(changingValue == 1)
                {
                    if(Integer.parseInt(bracket.getText().toString()) > minValue)
                        bracket.setText(String.valueOf(Integer.parseInt(bracket.getText().toString()) - 1));
                }
                if(changingValue == 5)
                {
                    if(Integer.parseInt(bracket.getText().toString()) < 5)
                        bracket.setText(String.valueOf(minValue));
                    else
                        bracket.setText(String.valueOf((Integer.parseInt(bracket.getText().toString())/5)*5));
                }
            }
        }
        if(operation == "Check");
        {
            if(bracket.getText().toString().equals("") || Integer.parseInt(bracket.getText().toString()) < minValue)
                bracket.setText(String.valueOf(minValue));
            if(maxValue != noMaxValue && Integer.parseInt(bracket.getText().toString()) > maxValue)
                bracket.setText(String.valueOf(maxValue));
            updateSummary();
        }
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
        playerLevel = Integer.parseInt(editTextPlayerLevel.getText().toString());
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultLevel", playerLevel);
        returnIntent.putExtra("resultPosition", playerPosition);
        returnIntent.putExtra("resultList", playerList);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}

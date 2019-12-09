package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class KillOMeterActivity extends AppCompatActivity
{
    int playerLevel;
    int playerPosition;
    String playerList;

    EditText editTextPlayerLevel;
    EditText editTextPlayerItems;
    EditText editTextPlayerBonus;
    EditText editTextPlayerSummary;
    EditText editTextMonsterLevel;
    EditText editTextMonsterEnhancer;
    EditText editTextMonsterBonus;
    EditText editTextMonsterSummary;
    ArrayList<EditText> editTextList;
    int maxPlayerLevel;
    int maxViewValue;
    int minLevel;
    int minBonus;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_o_meter);

        getSupportActionBar().setTitle("Kill-O-Meter");
        maxPlayerLevel = ((Variables)getApplication()).getMaxPlayerLevel();
        maxViewValue = 999;
        minLevel = ((Variables)getApplication()).getMinLevel();
        minBonus = 0;

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

        //Stwórz listę pól
        editTextList = new ArrayList<EditText>();
        editTextList.add(editTextPlayerLevel);
        editTextList.add(editTextPlayerItems);
        editTextList.add(editTextPlayerBonus);
        editTextList.add(editTextMonsterLevel);
        editTextList.add(editTextMonsterEnhancer);
        editTextList.add(editTextMonsterBonus);

        updateSummary();

        //Odejmij poziom graczowi
        imageViewPlayerLevelRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minLevel, maxPlayerLevel, editTextPlayerLevel, "Remove", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Dodaj poziom graczowi
        imageViewPlayerLevelAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minLevel, maxPlayerLevel, editTextPlayerLevel, "Add", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Sprawdź czy poziom gracza jest w zakrasie poziomów
        editTextPlayerLevel.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Odejmij bonus z przedmiotów gracza
        imageViewPlayerItemsRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextPlayerItems, "Remove", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Dodaj bonus z przedmiotów gracza
        imageViewPlayerItemsAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextPlayerItems, "Add", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerItems.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Odejmij bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextPlayerBonus, "Remove", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Dodaj bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextPlayerBonus, "Add", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerBonus.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });



        //Odejmij poziom potworowi
        imageViewMonsterLevelRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minLevel, maxViewValue, editTextMonsterLevel, "Remove", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Dodaj poziom potworowi
        imageViewMonsterLevelAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minLevel, maxViewValue, editTextMonsterLevel, "Add", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Sprawdź czy poziom potwora jest większy od 0 i nie jest pusty
        editTextMonsterLevel.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Odejmij wzmacniacz potwora
        imageViewMonsterEnhancerRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextMonsterEnhancer, "Remove", 5);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Dodaj wzmacniacz potwora
        imageViewMonsterEnhancerAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextMonsterEnhancer, "Add", 5);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Sprawdź czy wzmacniacz potwora nie jest pusty
        editTextMonsterEnhancer.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Odejmij bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextMonsterBonus, "Remove", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Dodaj bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editValueInBracket(minBonus, maxViewValue, editTextMonsterBonus, "Add", 1);
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });

        //Sprawdź czy bonus z jednorazowego użytku potwora nie jest pusty
        editTextMonsterBonus.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                checkValuesInBrackets(minLevel, minBonus, maxViewValue, editTextList);
            }
        });
    }

    //Metoda "Zsumuj moc gracza i potwora"
    public void updateSummary ()
    {
        editTextPlayerSummary.setText(String.valueOf(tryParse(editTextPlayerLevel.getText().toString(), maxPlayerLevel) + tryParse(editTextPlayerItems.getText().toString(), maxViewValue) + tryParse(editTextPlayerBonus.getText().toString(), maxViewValue)));
        editTextMonsterSummary.setText(String.valueOf(tryParse(editTextMonsterLevel.getText().toString(), maxViewValue) + tryParse(editTextMonsterEnhancer.getText().toString(), maxViewValue) + tryParse(editTextMonsterBonus.getText().toString(), maxViewValue)));
    }

    //Metoda "Zastosuj podaną operację na liczbach z pola"
    public void editValueInBracket(int minValue, int maxValue, EditText bracket, String operation, int incrementationValue)
    {
        if(operation == "Add")
        {
            if(bracket.getText().toString().equals("") || tryParse(bracket.getText().toString(), maxValue) < minValue)
                bracket.setText(String.valueOf(minValue));
            else
            {
                if(tryParse(bracket.getText().toString(), maxValue) < maxValue)
                    bracket.setText(String.valueOf(((tryParse(bracket.getText().toString(), maxValue) / incrementationValue) * incrementationValue) + incrementationValue));
                else
                    bracket.setText(String.valueOf(maxValue));
            }

        }
        else if(operation == "Remove")
        {
            if(bracket.getText().toString().equals("") || tryParse(bracket.getText().toString(), maxValue) < minValue)
                bracket.setText(String.valueOf(minValue));
            else
            {
                if(tryParse(bracket.getText().toString(), maxValue) < incrementationValue)
                    bracket.setText(String.valueOf(minValue));
                else
                {
                    if((tryParse(bracket.getText().toString(), maxValue)%incrementationValue) != 0)
                        bracket.setText(String.valueOf((tryParse(bracket.getText().toString(), maxValue)/incrementationValue)*incrementationValue));
                    else
                        bracket.setText(String.valueOf(tryParse(bracket.getText().toString(), maxValue) - incrementationValue));
                }
            }
        }
    }

    //Metoda "Sprawdź czy żadne pole nie jest puste"
    public void checkValuesInBrackets(int minLevelValue, int minBonusValue, int maxValue, ArrayList<EditText> bracketList)
    {
        for (EditText element:bracketList)
        {
            if(element == editTextPlayerLevel || element == editTextMonsterLevel)
            {
                if (element.getText().toString().equals("") || tryParse(element.getText().toString(), maxValue) < minLevelValue)
                    element.setText(String.valueOf(minLevelValue));
                else if (tryParse(element.getText().toString(), maxValue) > maxValue)
                    element.setText(String.valueOf(maxValue));
            }
            else
            {
                if (element.getText().toString().equals("") || tryParse(element.getText().toString(), maxValue) < minBonusValue)
                    element.setText(String.valueOf(minBonusValue));
                else if (tryParse(element.getText().toString(), maxValue) > maxValue)
                    element.setText(String.valueOf(maxValue));
            }
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
        playerLevel = tryParse(editTextPlayerLevel.getText().toString(), maxPlayerLevel);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultLevel", playerLevel);
        returnIntent.putExtra("resultPosition", playerPosition);
        returnIntent.putExtra("resultList", playerList);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    //tryParse int
    public int tryParse(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}

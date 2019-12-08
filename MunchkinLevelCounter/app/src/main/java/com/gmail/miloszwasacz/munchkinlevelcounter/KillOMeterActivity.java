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
    String playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_o_meter);

        getSupportActionBar().setTitle("Kill-O-Meter");

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
        final EditText editTextPlayerItems = findViewById(R.id.editTextPlayerItems);

        ImageView imageViewPlayerBonusRemove = findViewById(R.id.imageViewPlayerBonusRemove);
        ImageView imageViewPlayerBonusAdd = findViewById(R.id.imageViewPlayerBonusAdd);
        final EditText editTextPlayerBonus = findViewById(R.id.editTextPlayerBonus);

        final EditText editTextPlayerSummary = findViewById(R.id.editTextPlayerSummary);

        //Kontrolki potwora
        ImageView imageViewMonsterLevelRemove = findViewById(R.id.imageViewMonsterLevelRemove);
        ImageView imageViewMonsterLevelAdd = findViewById(R.id.imageViewMonsterLevelAdd);
        final EditText editTextMonsterLevel = findViewById(R.id.editTextMonsterLevel);

        ImageView imageViewMonsterEnhancerRemove = findViewById(R.id.imageViewMonsterEnhancerRemove);
        ImageView imageViewMonsterEnhancerAdd = findViewById(R.id.imageViewMonsterEnhancerAdd);
        final EditText editTextMonsterEnhancer = findViewById(R.id.editTextMonsterEnhancer);

        ImageView imageViewMonsterBonusRemove = findViewById(R.id.imageViewMonsterBonusRemove);
        ImageView imageViewMonsterBonusAdd = findViewById(R.id.imageViewMonsterBonusAdd);
        final EditText editTextMonsterBonus = findViewById(R.id.editTextMonsterBonus);

        final EditText editTextMonsterSummary = findViewById(R.id.editTextMonsterSummary);

        //Zsumuj moc gracza i potwora
        editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
        editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));

        //Odejmij poziom graczowi
        imageViewPlayerLevelRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextPlayerLevel.getText().toString().equals("") || Integer.parseInt(editTextPlayerLevel.getText().toString()) < 1)
                    editTextPlayerLevel.setText("1");
                if(Integer.parseInt(editTextPlayerLevel.getText().toString()) > 1)
                    editTextPlayerLevel.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) - 1));
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Dodaj poziom graczowi
        imageViewPlayerLevelAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextPlayerLevel.getText().toString().equals(""))
                    editTextPlayerLevel.setText("1");
                else
                {
                    if (Integer.parseInt(editTextPlayerLevel.getText().toString()) < 22)
                        editTextPlayerLevel.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + 1));
                }
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Sprawdź czy poziom gracza jest w zakrasie poziomów
        editTextPlayerLevel.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(editTextPlayerLevel.getText().toString().equals("") || Integer.parseInt(editTextPlayerLevel.getText().toString()) < 1)
                    editTextPlayerLevel.setText("1");
                if(Integer.parseInt(editTextPlayerLevel.getText().toString()) > 22)
                    editTextPlayerLevel.setText("22");
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Odejmij bonus z przedmiotów gracza
        imageViewPlayerItemsRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextPlayerItems.getText().toString().equals(""))
                    editTextPlayerItems.setText("0");
                if(Integer.parseInt(editTextPlayerItems.getText().toString()) > 0)
                    editTextPlayerItems.setText(String.valueOf(Integer.parseInt(editTextPlayerItems.getText().toString()) - 1));
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Dodaj bonus z przedmiotów gracza
        imageViewPlayerItemsAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextPlayerItems.getText().toString().equals(""))
                    editTextPlayerItems.setText("0");
                else
                    editTextPlayerItems.setText(String.valueOf(Integer.parseInt(editTextPlayerItems.getText().toString()) + 1));
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerItems.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(editTextPlayerItems.getText().toString().equals(""))
                    editTextPlayerItems.setText("0");
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Odejmij bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextPlayerBonus.getText().toString().equals(""))
                    editTextPlayerBonus.setText("0");
                if(Integer.parseInt(editTextPlayerBonus.getText().toString()) > 0)
                    editTextPlayerBonus.setText(String.valueOf(Integer.parseInt(editTextPlayerBonus.getText().toString()) - 1));
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Dodaj bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextPlayerBonus.getText().toString().equals(""))
                    editTextPlayerBonus.setText("0");
                else
                    editTextPlayerBonus.setText(String.valueOf(Integer.parseInt(editTextPlayerBonus.getText().toString()) + 1));
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerBonus.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(editTextPlayerBonus.getText().toString().equals(""))
                    editTextPlayerBonus.setText("0");
                editTextPlayerSummary.setText(String.valueOf(Integer.parseInt(editTextPlayerLevel.getText().toString()) + Integer.parseInt(editTextPlayerItems.getText().toString()) + Integer.parseInt(editTextPlayerBonus.getText().toString())));
            }
        });



        //Odejmij poziom potworowi
        imageViewMonsterLevelRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextMonsterLevel.getText().toString().equals("") || Integer.parseInt(editTextMonsterLevel.getText().toString()) < 1)
                    editTextMonsterLevel.setText("1");
                if(Integer.parseInt(editTextMonsterLevel.getText().toString()) > 1)
                    editTextMonsterLevel.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) - 1));
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Dodaj poziom potworowi
        imageViewMonsterLevelAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextMonsterLevel.getText().toString().equals(""))
                    editTextMonsterLevel.setText("1");
                else
                    editTextMonsterLevel.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + 1));
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Sprawdź czy poziom potwora jest większy od 0 i nie jest pusty
        editTextMonsterLevel.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(editTextMonsterLevel.getText().toString().equals("") || Integer.parseInt(editTextMonsterLevel.getText().toString()) < 1)
                    editTextMonsterLevel.setText("1");
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Odejmij wzmacniacz potwora
        imageViewMonsterEnhancerRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextMonsterEnhancer.getText().toString().equals(""))
                    editTextMonsterEnhancer.setText("0");
                if(Integer.parseInt(editTextMonsterEnhancer.getText().toString()) >= 5)
                    editTextMonsterEnhancer.setText(String.valueOf(((Integer.parseInt(editTextMonsterEnhancer.getText().toString())/5)*5) - 5));
                if(Integer.parseInt(editTextMonsterEnhancer.getText().toString()) < 5)
                    editTextMonsterEnhancer.setText("0");
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Dodaj wzmacniacz potwora
        imageViewMonsterEnhancerAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextMonsterEnhancer.getText().toString().equals(""))
                    editTextMonsterEnhancer.setText("0");
                else
                    editTextMonsterEnhancer.setText(String.valueOf(((Integer.parseInt(editTextMonsterEnhancer.getText().toString())/5)*5) + 5));
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Sprawdź czy wzmacniacz potwora nie jest pusty
        editTextMonsterEnhancer.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(editTextMonsterEnhancer.getText().toString().equals(""))
                    editTextMonsterEnhancer.setText("0");
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Odejmij bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextMonsterBonus.getText().toString().equals(""))
                    editTextMonsterBonus.setText("0");
                if(Integer.parseInt(editTextMonsterBonus.getText().toString()) > 0)
                    editTextMonsterBonus.setText(String.valueOf(Integer.parseInt(editTextMonsterBonus.getText().toString()) - 1));
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Dodaj bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(editTextMonsterBonus.getText().toString().equals(""))
                    editTextMonsterBonus.setText("0");
                else
                    editTextMonsterBonus.setText(String.valueOf(Integer.parseInt(editTextMonsterBonus.getText().toString()) + 1));
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
            }
        });

        //Sprawdź czy bonus z jednorazowego użytku potwora nie jest pusty
        editTextMonsterBonus.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(editTextMonsterBonus.getText().toString().equals(""))
                    editTextMonsterBonus.setText("0");
                editTextMonsterSummary.setText(String.valueOf(Integer.parseInt(editTextMonsterLevel.getText().toString()) + Integer.parseInt(editTextMonsterEnhancer.getText().toString()) + Integer.parseInt(editTextMonsterBonus.getText().toString())));
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
        playerLevel = Integer.parseInt(editTextPlayerLevel.getText().toString());
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultLevel", playerLevel);
        returnIntent.putExtra("resultPosition", playerPosition);
        returnIntent.putExtra("resultList", playerList);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}

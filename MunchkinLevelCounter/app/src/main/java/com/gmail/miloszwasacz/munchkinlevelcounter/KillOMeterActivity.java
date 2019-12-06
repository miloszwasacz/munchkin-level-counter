package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class KillOMeterActivity extends AppCompatActivity
{
    int playerLevel;
    int playerPosition;

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

        //Ustawianie poziomu i nazwy gracza
        TextView textViewPlayerName = findViewById(R.id.textViewName);
        textViewPlayerName.setText(playerName);
        final TextView editTextPlayerLevel = findViewById(R.id.editTextPlayerLevel);
        editTextPlayerLevel.setText(playerLevel);
            //Kontrolki gracza
        ImageView imageViewPlayerLevelRemove = findViewById(R.id.imageViewPlayerLevelRemove);
        ImageView imageViewPlayerLevelAdd = findViewById(R.id.imageViewPlayerLevelAdd);

        ImageView imageViewPlayerItemsRemove = findViewById(R.id.imageViewPlayerItemsRemove);
        ImageView imageViewPlayerItemsAdd = findViewById(R.id.imageViewPlayerItemsAdd);
        EditText editTextPlayerItems = findViewById(R.id.editTextPlayerItems);

        ImageView imageViewPlayerBonusRemove = findViewById(R.id.imageViewPlayerBonusRemove);
        ImageView imageViewPlayerBonusAdd = findViewById(R.id.imageViewPlayerBonusAdd);
        EditText editTextPlayerBonus = findViewById(R.id.editTextPlayerBonus);

            //Kontrolki potwora
        ImageView imageViewMonsterLevelRemove = findViewById(R.id.imageViewMonsterLevelRemove);
        ImageView imageViewMonsterLevelAdd = findViewById(R.id.imageViewMonsterLevelAdd);
        EditText editTextMonsterLevel = findViewById(R.id.editTextMonsterLevel);

        ImageView imageViewMonsterEnhancerRemove = findViewById(R.id.imageViewMonsterEnhancerRemove);
        ImageView imageViewMonsterEnhancerAdd = findViewById(R.id.imageViewMonsterEnhancerAdd);
        EditText editTextMonsterEnhancer = findViewById(R.id.editTextMonsterEnhancer);

        ImageView imageViewMonsterBonusRemove = findViewById(R.id.imageViewMonsterBonusRemove);
        ImageView imageViewMonsterBonusAdd = findViewById(R.id.imageViewMonsterBonusAdd);
        EditText editTextMonsterBonus = findViewById(R.id.editTextMonsterBonus);

        imageViewPlayerLevelRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Integer.parseInt(editTextPlayerLevel.getText().toString()) > 1)
                    editTextPlayerLevel.setText(Integer.parseInt(editTextPlayerLevel.getText().toString()) - 1);
            }
        });

        imageViewPlayerLevelAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Integer.parseInt(editTextPlayerLevel.getText().toString()) < 22)
                    editTextPlayerLevel.setText(Integer.parseInt(editTextPlayerLevel.getText().toString()) + 1);
            }
        });

        editTextPlayerLevel.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(Integer.parseInt(editTextPlayerLevel.getText().toString()) < 1)
                    editTextPlayerLevel.setText("1");
                if(Integer.parseInt(editTextPlayerLevel.getText().toString()) > 22)
                    editTextPlayerLevel.setText("22");
            }
        });
    }

    //Wyjście z Activity
    @Override
    public void onBackPressed()
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultLevel", playerLevel);
        returnIntent.putExtra("resultPosition", playerPosition);
        setResult(KillOMeterActivity.RESULT_OK, returnIntent);
        finish();
    }
}

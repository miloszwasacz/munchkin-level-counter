package com.gmail.miloszwasacz.munchkinlevelcounter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    FloatingActionButton floatingActionButton;
    PlayerAdapter adapter;
    List<Player> list;
    String SharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Licznik");

        list = new ArrayList<Player>();
        SharedPreferences prefs = getSharedPreferences(SharedPrefs, MODE_PRIVATE);
        int maksymalnyPoziom = prefs.getInt("MaksymalnyPoziomPrefs", 10);
        int minimalnyPoziom = prefs.getInt("MinimalnyPoziomPrefs", 1);
        ((Variables)getApplication()).setMaxPlayerLevel(maksymalnyPoziom);
        ((Variables)getApplication()).setMinLevel(minimalnyPoziom);

        //Tworzenie domyślnej listy graczy
        if(savedInstanceState == null)
        {
            //Tworzenie domyślnej listy
            list.add(new Player("Gracz 1", 1));
            list.add(new Player("Gracz 2", 1));
            list.add(new Player("Gracz 3", 1));
        }
        //Przywracanie stanu poprzedniego listy graczy
        else
        {
            Gson gson = new Gson();
            String json = savedInstanceState.getString("ListaGraczy");
            Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
            list = new Gson().fromJson(json, listType);
        }

        setPlayerAdapter();

        //Zmiana trybu guzika: edycja/dodawanie graczy
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (adapter.editMode)
                {
                    list.add(new Player("Nowy gracz"));
                    adapter.notifyItemInserted(list.size() - 1);
                }
                else
                    changeEditMode(R.drawable.ic_baseline_add_white_24dp, true, "Edytuj graczy");
            }
        });
    }

    //RecyclerView i PlayerAdapter
    public void setPlayerAdapter()
    {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayerAdapter(list);

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (Player element:list)
        {
            if(element.level > ((Variables)getApplication()).getMaxPlayerLevel())
                element.level = ((Variables)getApplication()).getMaxPlayerLevel();
            else if(element.level < ((Variables)getApplication()).getMinLevel())
                element.level = ((Variables)getApplication()).getMinLevel();
        }

        adapter.setOnItemClickListener(new PlayerAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(final int position)
            {
                final FrameLayout frameLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.player_dialog, null, false);
                final EditText editText = (EditText) frameLayout.findViewById(R.id.editText);
                editText.setText(list.get(position).name);

                //Okienko do edycji poszczególnego gracza
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Edytuj gracza")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                list.get(position).name = editText.getText().toString();
                                adapter.notifyItemChanged(position);
                            }
                        })
                        .setNegativeButton("Anuluj", null)
                        .setNeutralButton("Usuń", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                list.remove(position);
                                adapter.notifyItemRemoved(position);
                            }
                        })
                        .setView(frameLayout)
                        .create()
                        .show();
            }

            //Zwiększenie poziomu gracza
            @Override
            public void onAddClick(int position)
            {
                if (list.get(position).level < ((Variables)getApplication()).getMaxPlayerLevel())
                {
                    list.get(position).level++;
                    adapter.notifyItemChanged(position);
                }
            }

            //Zmniejszenie poziomu gracza
            @Override
            public void onRemoveClick(int position)
            {
                if (list.get(position).level > ((Variables)getApplication()).getMinLevel())
                {
                    list.get(position).level--;
                    adapter.notifyItemChanged(position);
                }
            }

            //Wejście w tryb Kill-O-Meter
            @Override
            public void onFightClick(int position)
            {
                Intent intent = new Intent(MainActivity.this, KillOMeterActivity.class);
                int playerLevel = list.get(position).level;
                String playerName = list.get(position).name;
                int playerPosition = position;
                Gson gson = new Gson();
                String json = gson.toJson(list);
                intent.putExtra("EXTRA_LEVEL", playerLevel);
                intent.putExtra("EXTRA_NAME", playerName);
                intent.putExtra("EXTRA_POSITION", playerPosition);
                intent.putExtra("EXTRA_LIST", json);
                startActivityForResult(intent, 1);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    //Włączanie/wyłączanie trybu edycji
    public void changeEditMode(int icon, Boolean bool, String title)
    {
        getSupportActionBar().setTitle(title);
        floatingActionButton.setImageResource(icon);
        adapter.editMode = bool;
        invalidateOptionsMenu();
        adapter.notifyDataSetChanged();
    }

    //Strzałeczka w tył (tryb edycji)
    @Override
    public void onBackPressed()
    {
        if (adapter.editMode)
            changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");
        else
            super.onBackPressed();
    }

    //Tworzenie guzików na app barze
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.clear();
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem mSaveButton = menu.findItem(R.id.action_save);
        MenuItem mLoadButton = menu.findItem(R.id.action_folder);
        MenuItem mClearButton = menu.findItem(R.id.action_clear);
        MenuItem mSettingsButton = menu.findItem(R.id.navigation_settings);
        if(adapter.editMode)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mSaveButton.setVisible(true);
            mLoadButton.setVisible(true);
            mClearButton.setVisible(true);
            mSettingsButton.setVisible(false);
        }
        else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mSaveButton.setVisible(false);
            mLoadButton.setVisible(false);
            mClearButton.setVisible(false);
            mSettingsButton.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    //Obsługa guzików na app barze
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //Zapisanie rozgrywki
            case R.id.action_save:
                Gson gsonSave = new Gson();
                String jsonSave = gsonSave.toJson(list);

                SharedPreferences.Editor editor = getSharedPreferences(SharedPrefs, MODE_PRIVATE).edit();
                editor.putString("ListaGraczyPrefs", jsonSave);
                editor.commit();
                changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");

                Toast.makeText(this, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show();
                return true;

            //Wczytanie ostatniej rozgrywki
            case R.id.action_folder:
                SharedPreferences prefs = getSharedPreferences(SharedPrefs, MODE_PRIVATE);
                String listaGraczy = prefs.getString("ListaGraczyPrefs", null);
                Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
                list = new Gson().fromJson(listaGraczy, listType);
                setPlayerAdapter();
                changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");

                Toast.makeText(this, "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show();
                return true;

            //Przywrócenie stanu domyślnego
            case R.id.action_clear:
                list = new ArrayList<Player>();
                list.add(new Player("Gracz 1", 1));
                list.add(new Player("Gracz 2", 1));
                list.add(new Player("Gracz 3", 1));
                setPlayerAdapter();
                changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");

                Toast.makeText(this, "Przywrócono domyśnych graczy", Toast.LENGTH_SHORT).show();
                return true;

            //Otwarcie ustawień
            case R.id.navigation_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                Gson gsonSettings = new Gson();
                String jsonSettings = gsonSettings.toJson(list);
                intent.putExtra("EXTRA_LIST", jsonSettings);
                startActivityForResult(intent, 2);
                return true;

            //Strzałeczka "back"
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Zapisywanie stanu listy graczy
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        Gson gson = new Gson();
        String json = gson.toJson(list);

        savedInstanceState.putString("ListaGraczy", json);
    }

    //Odebranie danych z innych Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Aktualizacja poziomu z Kill-O-Meter'a
        if(requestCode == 1)
        {
            int resultPosition = data.getIntExtra("resultPosition", 0);
            int resultLevel = data.getIntExtra("resultLevel", 1);
            String json = data.getStringExtra("resultList");
            Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
            list = new Gson().fromJson(json, listType);
            if(resultCode == RESULT_OK)
            {
                list.get(resultPosition).level = resultLevel;
                setPlayerAdapter();
            }
        }
        //Sprawdzenie poziomów po aktualizacji maksymalnego poziomu
        else if(requestCode == 2)
        {
            String json = data.getStringExtra("resultList");
            Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
            list = new Gson().fromJson(json, listType);
            if(resultCode == RESULT_OK)
                setPlayerAdapter();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

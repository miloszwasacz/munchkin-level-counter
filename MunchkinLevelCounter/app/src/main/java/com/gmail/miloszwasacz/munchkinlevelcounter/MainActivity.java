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
    int MaxPlayerLevel;
    int MinLevel;
    List<Game> gameList;
    String gameIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Licznik");

        list = new ArrayList<Player>();

        //Tworzenie domyślnej listy graczy
        if(savedInstanceState == null)
        {
            gameIndex = null;
            MaxPlayerLevel = getResources().getInteger(R.integer.deafult_rules);
            MinLevel = getResources().getInteger(R.integer.deafult_min_level);

            //Tworzenie domyślnej listy
            list.add(new Player("Gracz 1", MinLevel));
            list.add(new Player("Gracz 2", MinLevel));
            list.add(new Player("Gracz 3", MinLevel));
        }
        //Przywracanie stanu poprzedniego listy graczy
        else
        {
            MaxPlayerLevel = savedInstanceState.getInt("MaksymalnyPoziom", getResources().getInteger(R.integer.deafult_rules));
            MinLevel = savedInstanceState.getInt("MinimalnyPoziom", getResources().getInteger(R.integer.deafult_min_level));
            gameIndex = savedInstanceState.getString("IndexGry", null);
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
                    list.add(new Player("Nowy gracz", MinLevel));
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
            if(element.level > MaxPlayerLevel)
                element.level = MaxPlayerLevel;
            else if(element.level < MinLevel)
                element.level = MinLevel;
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
                if (list.get(position).level < MaxPlayerLevel)
                {
                    list.get(position).level++;
                    adapter.notifyItemChanged(position);
                }
            }

            //Zmniejszenie poziomu gracza
            @Override
            public void onRemoveClick(int position)
            {
                if (list.get(position).level > MinLevel)
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
                intent.putExtra("EXTRA_MAX_LEVEL", MaxPlayerLevel);
                intent.putExtra("EXTRA_MIN_LEVEL", MinLevel);
                startActivityForResult(intent, 1);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    //Włączanie/wyłączanie trybu edycji
    public void changeEditMode(int icon, Boolean enabled, String title)
    {
        getSupportActionBar().setTitle(title);
        floatingActionButton.setImageResource(icon);
        adapter.editMode = enabled;
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
            mSaveButton.setVisible(false);
            mLoadButton.setVisible(false);
            mClearButton.setVisible(false);
            mSettingsButton.setVisible(true);
        }
        else
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mSaveButton.setVisible(true);
            mLoadButton.setVisible(true);
            mClearButton.setVisible(true);
            mSettingsButton.setVisible(false);
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
                final FrameLayout frameLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.player_dialog, null, false);
                final EditText editText = (EditText) frameLayout.findViewById(R.id.editText);

                SharedPreferences prefsSave = getSharedPreferences(SharedPrefs, MODE_PRIVATE);
                String listaGier = prefsSave.getString("ListaGierPrefs", null);
                if(listaGier == null)
                    gameList = new ArrayList<Game>();
                else
                {
                    Type listType = new TypeToken<ArrayList<Game>>(){}.getType();
                    gameList = new Gson().fromJson(listaGier, listType);
                }

                if(gameIndex == null)
                {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Zapisz rozgrywkę")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if(editText.getText().toString().equals(""))
                                        editText.setText("Gra " + (gameList.size() + 1));

                                    Gson gsonListSave = new Gson();
                                    String jsonListSave = gsonListSave.toJson(list);

                                    gameList.add(new Game(editText.getText().toString(), jsonListSave, MaxPlayerLevel, MinLevel));
                                    gameIndex = String.valueOf(gameList.size() - 1);

                                    /*
                                    Gson gsonGameSave = new Gson();
                                    String jsonGameSave = gsonGameSave.toJson(gameList);
                                    SharedPreferences.Editor editor = getSharedPreferences(SharedPrefs, MODE_PRIVATE).edit();
                                    editor.putString("ListaGierPrefs", jsonGameSave);
                                    editor.commit();*/
                                    saveGameListInSharedPreferences(gameList);

                                    changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");
                                    Toast.makeText(MainActivity.this, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Anuluj", null)
                            .setView(frameLayout)
                            .create()
                            .show();
                }
                else
                {
                    Gson gsonListSave = new Gson();
                    String jsonListSave = gsonListSave.toJson(list);
                    String nazwaGry = gameList.get(Integer.parseInt(gameIndex)).name;
                    gameList.set(Integer.parseInt(gameIndex), new Game(nazwaGry, jsonListSave, MaxPlayerLevel, MinLevel));

                    /*
                    Gson gsonGameSave = new Gson();
                    String jsonGameSave = gsonGameSave.toJson(gameList);
                    SharedPreferences.Editor editor = getSharedPreferences(SharedPrefs, MODE_PRIVATE).edit();
                    editor.putString("ListaGierPrefs", jsonGameSave);
                    editor.commit();*/
                    saveGameListInSharedPreferences(gameList);

                    changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");
                    Toast.makeText(MainActivity.this, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show();
                }

                return true;

            //Wczytanie ostatniej rozgrywki
            case R.id.action_folder:
                SharedPreferences prefsLoad = getSharedPreferences(SharedPrefs, MODE_PRIVATE);
                String listaGierWczytaj = prefsLoad.getString("ListaGierPrefs", null);
                if(listaGierWczytaj == null)
                    Toast.makeText(MainActivity.this, "Brak zapisanych rozgrywek", Toast.LENGTH_SHORT).show();
                else
                {
                    Type listType = new TypeToken<ArrayList<Game>>(){}.getType();
                    gameList = new Gson().fromJson(listaGierWczytaj, listType);

                    String[] nameArray = new String[gameList.size()];

                    for (int i = 0; i < gameList.size(); i++)
                    {
                        nameArray[i] = gameList.get(i).name;
                    }


                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Wczytaj rozgrywkę")
                            .setItems(nameArray, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    gameIndex = String.valueOf(which);
                                    MaxPlayerLevel = gameList.get(which).maxLevel;
                                    MinLevel = gameList.get(which).minLevel;
                                    String jsonGame = gameList.get(which).content;
                                    Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
                                    list = new Gson().fromJson(jsonGame, listType);
                                    setPlayerAdapter();

                                    changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");
                                    Toast.makeText(MainActivity.this, "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                }
                return true;

            //Przywrócenie stanu domyślnego
            case R.id.action_clear:
                SharedPreferences prefsDelete = getSharedPreferences(SharedPrefs, MODE_PRIVATE);
                String listaGierUsun = prefsDelete.getString("ListaGierPrefs", null);
                if(listaGierUsun == null)
                    Toast.makeText(MainActivity.this, "Brak zapisanych rozgrywek", Toast.LENGTH_SHORT).show();
                else
                {
                    Type listType = new TypeToken<ArrayList<Game>>(){}.getType();
                    gameList = new Gson().fromJson(listaGierUsun, listType);

                    String[] nameArray = new String[gameList.size()];

                    for (int i = 0; i < gameList.size(); i++)
                    {
                        nameArray[i] = gameList.get(i).name;
                    }


                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Usuń rozgrywkę")
                            .setItems(nameArray, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    gameList.remove(which);

                                    /*
                                    Gson gsonGameRemove = new Gson();
                                    String jsonGameRemove = gsonGameRemove.toJson(gameList);
                                    SharedPreferences.Editor editor = getSharedPreferences(SharedPrefs, MODE_PRIVATE).edit();
                                    editor.putString("ListaGierPrefs", jsonGameRemove);
                                    editor.commit();*/
                                    saveGameListInSharedPreferences(gameList);

                                    if(Integer.parseInt(gameIndex) == which)
                                    {
                                        gameIndex = null;
                                        MaxPlayerLevel = getResources().getInteger(R.integer.deafult_rules);
                                        MinLevel = getResources().getInteger(R.integer.deafult_min_level);

                                        list = new ArrayList<Player>();
                                        list.add(new Player("Gracz 1", MinLevel));
                                        list.add(new Player("Gracz 2", MinLevel));
                                        list.add(new Player("Gracz 3", MinLevel));
                                        setPlayerAdapter();
                                    }

                                    changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik");
                                    Toast.makeText(MainActivity.this, "Usunięto rozgrywkę", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                }
                return true;

            //Otwarcie ustawień
            case R.id.navigation_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                Gson gsonSettings = new Gson();
                String jsonSettings = gsonSettings.toJson(list);
                intent.putExtra("EXTRA_LIST", jsonSettings);
                intent.putExtra("EXTRA_MAX_LEVEL", MaxPlayerLevel);
                intent.putExtra("EXTRA_MIN_LEVEL", MinLevel);
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
        savedInstanceState.putInt("MaksymalnyPoziom", MaxPlayerLevel);
        savedInstanceState.putInt("MinimalnyPoziom", MinLevel);
        savedInstanceState.putString("IndexGry", gameIndex);
    }

    //Odebranie danych z innych Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Aktualizacja poziomu z Kill-O-Meter'a
        if(requestCode == 1)
        {
            MaxPlayerLevel = data.getIntExtra("resultMaxLevel", getResources().getInteger(R.integer.deafult_rules));
            MinLevel = data.getIntExtra("resultMinLevel", getResources().getInteger(R.integer.deafult_min_level));
            int resultPosition = data.getIntExtra("resultPosition", 0);
            int resultLevel = data.getIntExtra("resultLevel", MinLevel);
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
            MaxPlayerLevel = data.getIntExtra("resultMaxLevel", getResources().getInteger(R.integer.deafult_rules));
            MinLevel = data.getIntExtra("resultMinLevel", getResources().getInteger(R.integer.deafult_min_level));
            String json = data.getStringExtra("resultList");
            Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
            list = new Gson().fromJson(json, listType);
            if(resultCode == RESULT_OK)
                setPlayerAdapter();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Zapisywanie listy gier do SharedPreferences
    public void saveGameListInSharedPreferences(List<Game> list)
    {
        Gson gsonGameRemove = new Gson();
        String jsonGameRemove = gsonGameRemove.toJson(list);
        SharedPreferences.Editor editor = getSharedPreferences(SharedPrefs, MODE_PRIVATE).edit();
        editor.putString("ListaGierPrefs", jsonGameRemove);
        editor.commit();
    }
}

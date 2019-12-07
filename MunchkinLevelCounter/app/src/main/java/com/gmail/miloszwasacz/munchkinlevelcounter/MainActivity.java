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

        //Zmiana guzika: tryb edycji/dodawanie gracza
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
                {
                    floatingActionButton.setImageResource(R.drawable.ic_baseline_add_white_24dp);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    adapter.editMode = true;
                    adapter.notifyDataSetChanged();
                }
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
                if (list.get(position).level < 22)
                {
                    list.get(position).level++;
                    adapter.notifyItemChanged(position);
                }
            }

            //Zmniejszenie poziomu gracza
            @Override
            public void onRemoveClick(int position)
            {
                if (list.get(position).level > 1)
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

    //Strzałeczka w tył (tryb edycji)
    @Override
    public void onBackPressed()
    {
        if (adapter.editMode)
        {
            floatingActionButton.setImageResource(R.drawable.ic_baseline_edit_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            adapter.editMode = false;
            adapter.notifyDataSetChanged();
        } else
        {
            super.onBackPressed();
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

    //Guziki na app barze
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Guziki na app barze
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //Zapisanie rozgrywki
            case R.id.action_save:
                Gson gson = new Gson();
                String json = gson.toJson(list);

                SharedPreferences.Editor editor = getSharedPreferences(SharedPrefs, MODE_PRIVATE).edit();
                editor.putString("ListaGraczyPrefs", json);
                editor.commit();
                Toast.makeText(this, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show();
                return true;

            //Wczytanie ostatniej rozgrywki
            case R.id.action_folder:
                SharedPreferences prefs = getSharedPreferences(SharedPrefs, MODE_PRIVATE);
                String listaGraczy = prefs.getString("ListaGraczyPrefs", null);
                Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
                list = new Gson().fromJson(listaGraczy, listType);
                setPlayerAdapter();

                Toast.makeText(this, "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show();
                return true;

            //Przywrócenie stanu domyślnego
            case R.id.action_clear:
                list = new ArrayList<Player>();
                list.add(new Player("Gracz 1", 1));
                list.add(new Player("Gracz 2", 1));
                list.add(new Player("Gracz 3", 1));
                setPlayerAdapter();

                Toast.makeText(this, "Przywrócono domyśnych graczy", Toast.LENGTH_SHORT).show();
                return true;


            //Strzałeczka "back"
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Aktualizacja poziomu z Kill-O-Meter'a
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            int resultPosition = data.getIntExtra("resultPosition", 0);
            int resultLevel = data.getIntExtra("resultLevel", 1);
            String json = data.getStringExtra("resultList");
            Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
            list = new Gson().fromJson(json, listType);
            if (resultCode == RESULT_OK)
            {
                list.get(resultPosition).level = resultLevel;
                setPlayerAdapter();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

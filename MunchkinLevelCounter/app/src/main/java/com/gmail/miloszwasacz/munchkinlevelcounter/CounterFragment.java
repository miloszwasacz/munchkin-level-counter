package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.content.SharedPreferences;

public class CounterFragment extends Fragment
{
    FloatingActionButton floatingActionButton;
    PlayerAdapter adapter;
    List<Player> list;
    String SharedPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_counter, null);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<Player>();

        if(savedInstanceState == null)
        {
            //Tworzenie domyślnej listy
            list.add(new Player("Gracz 1", 1));
            list.add(new Player("Gracz 2", 1));
            list.add(new Player("Gracz 3", 1));
        }
        else
        {
            //Przywracanie stanu poprzedniego listy graczy
            Gson gson = new Gson();
            String json = savedInstanceState.getString("ListaGraczy");
            Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
            list = new Gson().fromJson(json, listType);
        }

        setPlayerAdapter();

        //Zmiana guzika: tryb edycji/dodawanie gracza
        floatingActionButton = (FloatingActionButton) getView().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (adapter.editMode)
                {
                    list.add(new Player("Nowy gracz"));
                    adapter.notifyItemInserted(list.size() - 1);
                } else
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
        final RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
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
                new AlertDialog.Builder(getActivity())
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
        });
        recyclerView.setAdapter(adapter);
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu, inflater);
    }*/

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

                SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(SharedPrefs, Context.MODE_PRIVATE).edit();
                editor.putString("ListaGraczyPrefs", json);
                editor.commit();
                Toast.makeText(this.getActivity(), "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show();
                return true;

            //Wczytanie ostatniej rozgrywki
            case R.id.action_folder:
                SharedPreferences prefs = this.getActivity().getSharedPreferences(SharedPrefs, Context.MODE_PRIVATE);
                String listaGraczy = prefs.getString("ListaGraczyPrefs", null);
                Type listType = new TypeToken<ArrayList<Player>>(){}.getType();
                list = new Gson().fromJson(listaGraczy, listType);
                setPlayerAdapter();

                Toast.makeText(this.getActivity(), "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show();
                return true;

            //Przywrócenie stanu domyślnego
            case R.id.action_clear:
                list = new ArrayList<Player>();
                list.add(new Player("Gracz 1", 1));
                list.add(new Player("Gracz 2", 1));
                list.add(new Player("Gracz 3", 1));
                setPlayerAdapter();

                Toast.makeText(this.getActivity(), "Przywrócono domyśnych graczy", Toast.LENGTH_SHORT).show();
                return true;


            //Strzałeczka "back"
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

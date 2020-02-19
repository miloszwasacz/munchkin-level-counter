package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var gameList: ArrayList<Game>
    internal lateinit var adapter: GameAdapter
    private var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.title_game_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        gameList = getGameListFromSharedPreferences() ?: ArrayList()
        setGameAdapter(gameList)

        //Dodanie nowej gry
        floatingActionButton.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateGameActivity::class.java)
            intent.putExtra("EXTRA_SIZE", gameList.size)
            startActivityForResult(intent, 3)
        }


    }

    //RecyclerView i GameAdapter
    private fun setGameAdapter(gameList: ArrayList<Game>) {

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = GameAdapter(gameList)

        //Obsługa kontrolek
        adapter.setOnItemClickListener(object : GameAdapter.OnItemClickListener {
            //Uruchomienie poszczególnej gry
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, GameActivity::class.java)
                intent.putExtra("EXTRA_GAME", Gson().toJson(gameList[position]))
                intent.putExtra("EXTRA_POSITION", position)
                startActivityForResult(intent, 1)
            }

            //Wejście w ustawienia gry
            override fun onSettingsClick(position: Int) {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra("EXTRA_GAME_NAME", gameList[position].name)
                intent.putExtra("EXTRA_MAX_LEVEL", gameList[position].maxLevel)
                intent.putExtra("EXTRA_MIN_LEVEL", gameList[position].minLevel)
                intent.putExtra("EXTRA_POSITION", position)
                startActivityForResult(intent, 2)
            }

            //Usunięcie gry
            override fun onDeleteClick(position: Int) {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Usunąć grę?")
                        .setPositiveButton("Tak") { dialog, which ->
                            gameList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                        .setNegativeButton("Nie", null)
                        .create()
                        .show()
            }
        })

        recycler_view.adapter = adapter
    }

    //Odebranie danych z innych Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Powrót z rozgrywki
        if (requestCode == 1) {
            val position = data!!.getIntExtra("resultPosition", 0)
            val json = data.getStringExtra("resultGame")
            val listType = object : TypeToken<Game>() {}.type
            if (resultCode == Activity.RESULT_OK) {
                gameList[position] = Gson().fromJson<Game>(json, listType)
                saveGameListInSharedPreferences(gameList)
                setGameAdapter(gameList)
            }
        }
        //Powrót z ustawień
        else if (requestCode == 2) {
            val position = data!!.getIntExtra("resultPosition", 0)
            if (resultCode == Activity.RESULT_OK) {
                gameList[position].maxLevel = data.getIntExtra("resultMaxLevel", resources.getInteger(R.integer.default_rules))
                gameList[position].minLevel = data.getIntExtra("resultMinLevel", resources.getInteger(R.integer.min_level))
                gameList[position].name = data.getStringExtra("resultName")
                setGameAdapter(gameList)
            }
        }
        //Powrót z kreatora gier
        else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val json = data!!.getStringExtra("resultGame")
                val listType = object : TypeToken<Game>() {}.type
                gameList.add(Gson().fromJson<Game>(json, listType))
                setGameAdapter(gameList)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Zapisywanie listy gier do SharedPreferences
    private fun saveGameListInSharedPreferences(gameList: ArrayList<Game>) {
        val jsonGame = Gson().toJson(gameList)
        val editor = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE).edit()
        editor.putString("ListaGierPrefs", jsonGame)
        editor.apply()
    }

    //Wczytanie listy gier z SharedPreferences
    private fun getGameListFromSharedPreferences(): ArrayList<Game>? {
        val prefs = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val listaGier: String? = prefs.getString("ListaGierPrefs", null)
        val listType = object : TypeToken<ArrayList<Game>>() {}.type
        return when(listaGier) {
            null -> null
            else -> Gson().fromJson<ArrayList<Game>>(listaGier, listType)
        }
    }

    //Zapisywanie listy gier podczas wyjścia z aplikacji
    override fun onPause() {
        saveGameListInSharedPreferences(gameList)
        super.onPause()
    }
}

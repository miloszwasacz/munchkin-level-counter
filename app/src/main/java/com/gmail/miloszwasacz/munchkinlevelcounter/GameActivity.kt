package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*

class GameActivity : AppCompatActivity() {

    private lateinit var game: Game
    private var gameIndex: Int = 0
    internal lateinit var adapter: PlayerAdapter
    private var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Ustawianie wartości z poprzedniego activity
        gameIndex = intent.getIntExtra("EXTRA_POSITION", 0)
        val json  = intent.getStringExtra("EXTRA_GAME")
        val gameType = object : TypeToken<Game>() {}.type
        game = Gson().fromJson<Game>(json, gameType)

        //Przywracanie stanu poprzedniego listy graczy
        if(savedInstanceState != null){
            val jsonGame = savedInstanceState.getString("Gra")
            game = Gson().fromJson<Game>(jsonGame, gameType)

            gameIndex = savedInstanceState.getInt("Index")
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = game.name

        setPlayerAdapter(game)

        //Dodawanie graczy
        floatingActionButton.setOnClickListener {
            val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
            val editTextName = frameLayout.findViewById<EditText>(R.id.editText)
            editTextName.hint = "Nazwa gracza"

            AlertDialog.Builder(this@GameActivity)
                    .setTitle("Dodaj nowego gracza")
                    .setPositiveButton("Dodaj") { dialog, which ->
                        val list = extractPlayerListFromGame(game)

                        editTextName.setText(editTextName.text.toString().trim())
                        if (editTextName.text.toString() == "")
                            editTextName.setText("Gracz " + (list.size + 1))

                        list.add(Player(editTextName.text.toString(), game.minLevel))
                        insertPlayerListIntoGame(list, game)
                        setPlayerAdapter(game)
                    }
                    .setNegativeButton("Anuluj", null)
                    .setView(frameLayout)
                    .create()
                    .show()
        }
    }

    //RecyclerView i PlayerAdapter
    fun setPlayerAdapter(game: Game) {
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val list: ArrayList<Player> = extractPlayerListFromGame(game)
        adapter = PlayerAdapter(game)

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (element in list) {
            if (element.value > game.maxLevel)
                element.value = game.maxLevel
            else if (element.value < game.minLevel)
                element.value = game.minLevel
        }
        insertPlayerListIntoGame(list, game)

        //Obsługa kontrolek
        adapter.setOnItemClickListener(object : PlayerAdapter.OnItemClickListener {
            //Edycja poszczególnego gracza
            override fun onEditClick(position: Int) {
                try {
                    val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
                    val editTextName = frameLayout.findViewById<EditText>(R.id.editText)
                    editTextName.setText(list[position].name)

                    AlertDialog.Builder(this@GameActivity).setTitle("Edytuj gracza").setPositiveButton("Ok") { dialog, which ->
                                if(editTextName.text.toString() != "") list[position].name = editTextName.text.toString()
                                insertPlayerListIntoGame(list, game)
                                adapter.notifyItemChanged(position)
                            }.setNegativeButton("Anuluj", null).setNeutralButton("Usuń") { dialog, which ->
                                list.removeAt(position)
                                insertPlayerListIntoGame(list, game)
                                setPlayerAdapter(game)
                            }.setView(frameLayout).create().show()
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }

            //Zwiększenie poziomu gracza
            override fun onAddClick(position: Int) {
                try {
                    if(list[position].value < game.maxLevel) {
                        list[position].value++
                        insertPlayerListIntoGame(list, game)
                        adapter.notifyItemChanged(position)
                    }
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }

            //Zmniejszenie poziomu gracza
            override fun onRemoveClick(position: Int) {
                try {
                    if(list[position].value > game.minLevel) {
                        list[position].value--
                        insertPlayerListIntoGame(list, game)
                        adapter.notifyItemChanged(position)
                    }
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }

            //Wejście w tryb Kill-O-Meter
            override fun onFightClick(position: Int) {
                try {
                    insertPlayerListIntoGame(list, game)
                    val intent = Intent(this@GameActivity, KillOMeterActivity::class.java)
                    intent.putExtra("EXTRA_GAME_INDEX", gameIndex)
                    intent.putExtra("EXTRA_PLAYER_POSITION", position)
                    intent.putExtra("EXTRA_GAME", Gson().toJson(game))
                    startActivityForResult(intent, 4)
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }
        })

        recycler_view.adapter = adapter
    }

    //Odebranie danych z innych Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Aktualizacja poziomu z Kill-O-Meter'a
        if (requestCode == 4) {
            val json = data!!.getStringExtra("resultGame")
            val listType = object : TypeToken<Game>() {}.type
            game = Gson().fromJson<Game>(json, listType)
            if (resultCode == Activity.RESULT_OK)
                setPlayerAdapter(game)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Zapisywanie listy graczy w grze
    fun insertPlayerListIntoGame(list: ArrayList<Player>, game: Game) {
        val json = Gson().toJson(list)
        game.content = json
    }

    //Wczytanie listy graczy z gry
    fun extractPlayerListFromGame(game: Game): ArrayList<Player> {
        val json = game.content
        val listType = object : TypeToken<ArrayList<Player>>() {}.type
        return Gson().fromJson<ArrayList<Player>>(json, listType)
    }

    //Zapisywanie listy gier do SharedPreferences
    fun saveGameListInSharedPreferences(gameList: ArrayList<Game>) {
        val jsonGame = Gson().toJson(gameList)
        val editor = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE).edit()
        editor.putString("ListaGierPrefs", jsonGame)
        editor.apply()
    }

    //Wczytanie listy gier z SharedPreferences
    fun getGameListFromSharedPreferences(): ArrayList<Game>? {
        val prefs = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val listaGier: String? = prefs.getString("ListaGierPrefs", null)
        val listType = object : TypeToken<ArrayList<Game>>() {}.type
        return when(listaGier) {
            null -> null
            else -> Gson().fromJson<ArrayList<Game>>(listaGier, listType)
        }
    }

    //Zapisywanie stanu gry
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("Gra", Gson().toJson(game))
        savedInstanceState.putInt("Index", gameIndex)
    }

    //Strzałeczka "back"
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //Wyjście z Activity
    override fun onBackPressed() {
        val json = Gson().toJson(game)
        val returnIntent = Intent()
        returnIntent.putExtra("resultGame", json)
        returnIntent.putExtra("resultPosition", gameIndex)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //Zapisywanie gry podczas wyjścia z aplikacji
    override fun onPause() {
        val gameList = getGameListFromSharedPreferences()
        gameList!![gameIndex] = game
        saveGameListInSharedPreferences(gameList)
        super.onPause()
    }
}

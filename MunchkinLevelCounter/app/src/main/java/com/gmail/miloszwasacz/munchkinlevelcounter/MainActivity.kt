package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    internal lateinit var adapter: PlayerAdapter
    internal lateinit var game: Game
    internal var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"
    internal lateinit var gameList: ArrayList<Game>
    internal var editMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.title = "Licznik"

        gameList = getGameListFromSharedPreferences() ?: ArrayList<Game>()


        //Przywracanie stanu poprzedniego listy graczy
        if(savedInstanceState != null){
            val json = savedInstanceState.getString("Gra")
            val gameType = object : TypeToken<Game>() {}.type
            game = Gson().fromJson<Game>(json, gameType)
        }
        else {
            if(gameList.isEmpty()) {
                createNewGame(gameList)
            }
            else {
                loadGame(gameList)
            }
        }

        setPlayerAdapter(game)

        //Zmiana trybu guzika: edycja/dodawanie graczy
        floatingActionButton.setOnClickListener {
            if (adapter.editMode) {
                val list = extractPlayerListFromGame(game)
                list.add(Player("Nowy gracz", game.minLevel))
                insertPlayerListIntoGame(list, game)
                setPlayerAdapter(game)
            } else
                changeEditMode(R.drawable.ic_baseline_add_white_24dp, true, "Edytuj graczy")
        }
    }

    //RecyclerView i PlayerAdapter
    fun setPlayerAdapter(game: Game) {
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val list: ArrayList<Player> = extractPlayerListFromGame(game)
        adapter = PlayerAdapter(list)
        adapter.editMode = editMode

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (element in list) {
            if (element.level > game.maxLevel)
                element.level = game.maxLevel
            else if (element.level < game.minLevel)
                element.level = game.minLevel
        }

        adapter.setOnItemClickListener(object : PlayerAdapter.OnItemClickListener {
            //Edycja poszczególnego gracza
            override fun onItemClick(position: Int) {
                val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
                val editText = frameLayout.findViewById<EditText>(R.id.editText)
                editText.setText(list[position].name)

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Edytuj gracza")
                        .setPositiveButton("Ok") { dialog, which ->
                            list[position].name = editText.text.toString()
                            adapter.notifyItemChanged(position)
                        }
                        .setNegativeButton("Anuluj", null)
                        .setNeutralButton("Usuń") { dialog, which ->
                            list.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                        .setView(frameLayout)
                        .create()
                        .show()

                insertPlayerListIntoGame(list, game)
            }

            //Zwiększenie poziomu gracza
            override fun onAddClick(position: Int) {
                if (list[position].level < game.maxLevel) {
                    list[position].level++
                    adapter.notifyItemChanged(position)
                    insertPlayerListIntoGame(list, game)
                }
            }

            //Zmniejszenie poziomu gracza
            override fun onRemoveClick(position: Int) {
                if (list[position].level > game.minLevel) {
                    list[position].level--
                    adapter.notifyItemChanged(position)
                    insertPlayerListIntoGame(list, game)
                }
            }

            //Wejście w tryb Kill-O-Meter
            override fun onFightClick(position: Int) {
                insertPlayerListIntoGame(list, game)
                val intent = Intent(this@MainActivity, KillOMeterActivity::class.java)
                intent.putExtra("EXTRA_POSITION", position)
                intent.putExtra("EXTRA_GAME", Gson().toJson(game))
                startActivityForResult(intent, 1)
            }
        })
        recycler_view.adapter = adapter
    }

    //Włączanie/wyłączanie trybu edycji
    fun changeEditMode(icon: Int, enabled: Boolean?, title: String) {
        supportActionBar!!.title = title
        floatingActionButton.setImageResource(icon)
        editMode = enabled!!
        adapter.editMode = enabled
        invalidateOptionsMenu()
        adapter.notifyDataSetChanged()
    }

    //Strzałeczka w tył (tryb edycji)
    override fun onBackPressed() {
        if (adapter.editMode)
            changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
        else
            super.onBackPressed()
    }

    //Tworzenie guzików na app barze
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val mSaveButton = menu.findItem(R.id.action_save)
        val mLoadButton = menu.findItem(R.id.action_folder)
        val mClearButton = menu.findItem(R.id.action_clear)
        val mSettingsButton = menu.findItem(R.id.navigation_settings)
        if (adapter.editMode) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            mSaveButton.isVisible = false
            mLoadButton.isVisible = false
            mClearButton.isVisible = false
            mSettingsButton.isVisible = true
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            mSaveButton.isVisible = true
            mLoadButton.isVisible = true
            mClearButton.isVisible = true
            mSettingsButton.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    //Obsługa guzików na app barze
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        gameList = getGameListFromSharedPreferences() ?: ArrayList<Game>()

        when (item.itemId) {
            //Tworzenie nowej rozgrywki
            R.id.action_save -> {


                /*
                val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
                val editText = frameLayout.findViewById<View>(R.id.editText) as EditText

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Dodaj grę")
                        .setPositiveButton("Ok") { dialog, which ->
                            if (gameList == null)
                                gameList = ArrayList()

                            if (editText.text.toString() == "")
                                editText.setText("Gra " + (gameList!!.size + 1))

                            val playerList = ArrayList<Player>()
                            playerList.add(Player("Gracz 1", resources.getInteger(R.integer.default_min_level)))
                            playerList.add(Player("Gracz 2", resources.getInteger(R.integer.default_min_level)))
                            playerList.add(Player("Gracz 3", resources.getInteger(R.integer.default_min_level)))
                            game = Game(editText.text.toString(), serializePlayerList(playerList), resources.getInteger(R.integer.default_rules), resources.getInteger(R.integer.default_min_level))
                            gameList!!.add(game)
                            saveGameListInSharedPreferences(gameList)

                            changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
                            Toast.makeText(this@MainActivity, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Anuluj", null)
                        .setView(frameLayout)
                        .create()
                        .show()*/

                return true
            }

            //Wczytanie ostatniej rozgrywki
            R.id.action_folder -> {
                /*if (gameList == null)
                    gameList = ArrayList()
                val lista = ArrayList<Player>()
                //createDefaultGame(lista)
                gameList!!.add(0, Game("default", serializePlayerList(lista), maxPlayerLevel, minLevel))

                val nameArray = arrayOfNulls<String>(gameList!!.size)
                for (i in gameList!!.indices) {
                    nameArray[i] = gameList!![i].name
                }

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Wczytaj rozgrywkę")
                        .setItems(nameArray) { dialog, which ->
                            maxPlayerLevel = gameList!![which].maxLevel
                            minLevel = gameList!![which].minLevel
                            val jsonGame = gameList!![which].content
                            val listType = object : TypeToken<ArrayList<Player>>() {}.type
                            list = Gson().fromJson<ArrayList<Player>>(jsonGame, listType)
                            setPlayerAdapter()
                            changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")

                            Toast.makeText(this@MainActivity, "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show()
                        }
                        .create()
                        .show()*/

                return true
            }

            //Usunięcie gry z listy
            R.id.action_clear -> {
                if (gameList == null)
                    Toast.makeText(this@MainActivity, "Brak zapisanych rozgrywek", Toast.LENGTH_SHORT).show()
                else {
                    val nameArrayDelete = arrayOfNulls<String>(gameList!!.size)

                    for (i in gameList!!.indices) {
                        nameArrayDelete[i] = gameList!![i].name
                    }

                    AlertDialog.Builder(this@MainActivity)
                            .setTitle("Usuń rozgrywkę")
                            .setItems(nameArrayDelete) { dialog, which ->
                                gameList!!.removeAt(which)
                                saveGameListInSharedPreferences(gameList)

                                changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
                                Toast.makeText(this@MainActivity, "Usunięto rozgrywkę", Toast.LENGTH_SHORT).show()
                            }
                            .create()
                            .show()
                }
                return true
            }

            //Otwarcie ustawień
            R.id.navigation_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra("EXTRA_MAX_LEVEL", game.maxLevel)
                intent.putExtra("EXTRA_MIN_LEVEL", game.minLevel)
                intent.putExtra("EXTRA_EDIT_MODE", adapter.editMode)
                startActivityForResult(intent, 2)
                return true
            }

            //Strzałeczka "back"
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    //Zapisywanie stanu listy graczy
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        val json = Gson().toJson(game)
        savedInstanceState.putString("Gra", json)
    }

    //Odebranie danych z innych Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Aktualizacja poziomu z Kill-O-Meter'a
        if (requestCode == 1) {
            val json = data!!.getStringExtra("resultGame")
            val listType = object : TypeToken<Game>() {}.type
            game = Gson().fromJson<Game>(json, listType)
            if (resultCode == Activity.RESULT_OK) {
                setPlayerAdapter(game)
            }
        }
        //Sprawdzenie poziomów po aktualizacji maksymalnego poziomu
        else if (requestCode == 2) {
            editMode = data!!.getBooleanExtra("resultEditMode", true)
            game.maxLevel= data.getIntExtra("resultMaxLevel", resources.getInteger(R.integer.default_rules))
            game.minLevel = data.getIntExtra("resultMinLevel", resources.getInteger(R.integer.default_min_level))
            if (resultCode == Activity.RESULT_OK)
                setPlayerAdapter(game)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Zapisywanie listy gier do SharedPreferences
    fun saveGameListInSharedPreferences(list: ArrayList<Game>) {
        val jsonGame = Gson().toJson(list)
        val editor = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE).edit()
        editor.putString("ListaGierPrefs", jsonGame)
        editor.commit()
    }

    fun getGameListFromSharedPreferences(): ArrayList<Game>? {
        val prefs = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val listaGier: String? = prefs.getString("ListaGierPrefs", null)
        val listType = object : TypeToken<ArrayList<Game>>() {}.type
        return when(listaGier) {
            null -> null
            else -> Gson().fromJson<ArrayList<Game>>(listaGier, listType)
        }
    }

    //Zserializuj listę graczy do jsona
    fun serializePlayerList(list: List<Player>): String = Gson().toJson(list)

    //Stwórz domyślną grę
    fun createDefaultGame(name: String): Game {
        val playerList = ArrayList<Player>()
        playerList.add(Player("Gracz 1", resources.getInteger(R.integer.default_min_level)))
        playerList.add(Player("Gracz 2", resources.getInteger(R.integer.default_min_level)))
        playerList.add(Player("Gracz 3", resources.getInteger(R.integer.default_min_level)))
        return Game(name, serializePlayerList(playerList))
    }

    fun extractPlayerListFromGame(game: Game): ArrayList<Player> {
        val json = game.content
        val listType = object : TypeToken<ArrayList<Player>>() {}.type
        return Gson().fromJson<ArrayList<Player>>(json, listType)
    }

    fun insertPlayerListIntoGame(list: ArrayList<Player>, game: Game) {
        val json = serializePlayerList(list)
        game.content = json
    }

    fun createNewGame(gameList: ArrayList<Game>) {
        /*
        var gameList = when(inputGameList) {
            null -> ArrayList<Game>()
            else -> inputGameList
        }*/
        val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
        val editText = frameLayout.findViewById<View>(R.id.editText) as EditText
        AlertDialog.Builder(this@MainActivity)
                .setTitle("Stwórz grę")
                .setPositiveButton("Ok") { dialog, which ->
                    if (editText.text.toString() == "")
                        editText.setText("Gra " + (gameList.size + 1))

                    game = createDefaultGame(editText.text.toString())
                    gameList.add(game)

                    changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
                    //Toast.makeText(this@MainActivity, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Anuluj", null)
                .setView(frameLayout)
                .create()
                .show()
        saveGameListInSharedPreferences(gameList)
    }

    fun loadGame(inputGameList: ArrayList<Game>) {
        var gameList: ArrayList<Game> = inputGameList

        val nameArray = arrayOfNulls<String>(gameList.size)
        for (i in gameList.indices) {
            nameArray[i] = gameList[i].name
        }
        game = gameList[0]

        AlertDialog.Builder(this@MainActivity)
                .setTitle("Wczytaj rozgrywkę")
                .setItems(nameArray) { dialog, which ->
                    game = gameList[which]
                    changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
                    Toast.makeText(this@MainActivity, "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show()
                }
                .create()
                .show()
        setPlayerAdapter(game)
    }

    fun deleteGame(gameList: ArrayList<Game>?) {

    }
}

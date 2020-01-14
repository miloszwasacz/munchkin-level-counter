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

    internal lateinit var adapter: PlayerAdapter
    internal lateinit var game: Game
    internal var gameIndex: Int = 0
    //internal var gameIndex: Int? = null
    internal var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"
    //internal lateinit var gameList: ArrayList<Game>
    //internal var editMode: Boolean = false
    //internal var visibleEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameIndex = intent.getIntExtra("EXTRA_POSITION", 0)
        val json  = intent.getStringExtra("EXTRA_GAME")
        val gameType = object : TypeToken<Game>() {}.type
        game = Gson().fromJson<Game>(json, gameType)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = game.name

        //gameList = getGameListFromSharedPreferences() ?: ArrayList<Game>()
        //game = Game("", serializePlayerList(ArrayList<Player>()))

        //Przywracanie stanu poprzedniego listy graczy
        if(savedInstanceState != null){
            val jsonGame = savedInstanceState.getString("Gra")
            val gameType = object : TypeToken<Game>() {}.type
            game = Gson().fromJson<Game>(jsonGame, gameType)

            /*val jsonGameList = savedInstanceState.getString("ListaGier")
            val gameListType = object : TypeToken<ArrayList<Game>>() {}.type
            gameList = Gson().fromJson<ArrayList<Game>>(jsonGameList, gameListType)*/

            /*val jsonIndex = savedInstanceState.getString("Index")
            val indexType = object : TypeToken<Int>() {}.type
            gameIndex = Gson().fromJson<Int>(jsonIndex, indexType)*/
            gameIndex = savedInstanceState.getInt("Index")

            //editMode = savedInstanceState.getBoolean("EditMode")
            //visibleEditMode = savedInstanceState.getBoolean("VisibleEditMode")
        }
        /*else {
            if(gameList.isEmpty()) {
                createNewGame(gameList)
            }
            else {
                loadGame(gameList)
            }
        }*/
        setPlayerAdapter(game)

        //Dodawanie graczy
        floatingActionButton.setOnClickListener {
            val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
            val editTextName = frameLayout.findViewById<EditText>(R.id.editText)
            editTextName.hint = "Nazwa gracza"

            AlertDialog.Builder(this@GameActivity)
                    .setTitle("Nowy gracz")
                    .setPositiveButton("Ok") { dialog, which ->
                        val list = extractPlayerListFromGame(game)

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
        adapter = PlayerAdapter(list)

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (element in list) {
            if (element.level > game.maxLevel)
                element.level = game.maxLevel
            else if (element.level < game.minLevel)
                element.level = game.minLevel
        }

        //Obsługa kontrolek
        adapter.setOnItemClickListener(object : PlayerAdapter.OnItemClickListener {
            //Edycja poszczególnego gracza
            override fun onItemClick(position: Int) {
                val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
                val editTextName = frameLayout.findViewById<EditText>(R.id.editText)
                editTextName.setText(list[position].name)

                AlertDialog.Builder(this@GameActivity)
                        .setTitle("Edytuj gracza")
                        .setPositiveButton("Ok") { dialog, which ->
                            if (editTextName.text.toString() != "")
                                list[position].name = editTextName.text.toString()
                            insertPlayerListIntoGame(list, game)
                            adapter.notifyItemChanged(position)
                        }
                        .setNegativeButton("Anuluj", null)
                        .setNeutralButton("Usuń") { dialog, which ->
                            list.removeAt(position)
                            insertPlayerListIntoGame(list, game)
                            adapter.notifyItemRemoved(position)
                        }
                        .setView(frameLayout)
                        .create()
                        .show()
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
                val intent = Intent(this@GameActivity, KillOMeterActivity::class.java)
                intent.putExtra("EXTRA_POSITION", position)
                intent.putExtra("EXTRA_GAME", Gson().toJson(game))
                startActivityForResult(intent, 4)
            }
        })

        recycler_view.adapter = adapter
    }

    //Obsługa guzików na app barze
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
        if(gameIndex != null)
            saveGame(game, gameList)
        gameList = getGameListFromSharedPreferences() ?: ArrayList<Game>()*/

        when (item.itemId) {
            /*
            //Tworzenie nowej rozgrywki
            R.id.action_create_new -> {
                createNewGame(gameList)
                return true
            }

            //Wczytanie ostatniej rozgrywki
            R.id.action_folder -> {
                loadGame(gameList)
                return true
            }

            //Usunięcie gry z listy
            R.id.action_clear -> {
                deleteGame(gameList)
                return true
            }

            //Otwarcie ustawień
            R.id.navigation_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra("EXTRA_MAX_LEVEL", game.maxLevel)
                intent.putExtra("EXTRA_MIN_LEVEL", game.minLevel)
                intent.putExtra("EXTRA_GAME_NAME", game.name)
                startActivityForResult(intent, 2)
                return true
            }*/

            //Strzałeczka "back"
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
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
        editor.commit()
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
        /*
    //Sprawdzenie poziomów po aktualizacji maksymalnego poziomu
    else if (requestCode == 2) {
        game.maxLevel= data!!.getIntExtra("resultMaxLevel", resources.getInteger(R.integer.default_rules))
        game.minLevel = data.getIntExtra("resultMinLevel", resources.getInteger(R.integer.default_min_level))
        game.name = data.getStringExtra("resultName")
        if (resultCode == Activity.RESULT_OK)
            setPlayerAdapter(game, visibleEditMode)
    }*/
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Zapisywanie stanu gry
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //val game = Gson().toJson(game)
        //val gameList = Gson().toJson(gameList)
        //val index = Gson().toJson(gameIndex)
        savedInstanceState.putString("Gra", Gson().toJson(game))
        //savedInstanceState.putString("ListaGier", gameList)
        //savedInstanceState.putString("Index", index)
        savedInstanceState.putInt("Index", gameIndex)
        //savedInstanceState.putBoolean("EditMode", editMode)
        //savedInstanceState.putBoolean("VisibleEditMode", visibleEditMode)
    }

    //Strzałeczka w tył
    override fun onBackPressed() {
        /*
        if (adapter.editMode)
            changeEditMode(false, true)
        else
            super.onBackPressed()*/
        val json = Gson().toJson(game)
        val returnIntent = Intent()
        returnIntent.putExtra("resultGame", json)
        returnIntent.putExtra("resultPosition", gameIndex)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //Zapisywanie gry podczas wyjścia z aplikacji
    override fun onPause() {
        /*
        if(gameIndex != null) {
            saveGame(game, gameList)
            Toast.makeText(this@MainActivity, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show()
        }*/
        val gameList = getGameListFromSharedPreferences()
        gameList!![gameIndex] = game
        saveGameListInSharedPreferences(gameList)
        //Toast.makeText(this@GameActivity, "Saved", Toast.LENGTH_SHORT).show()
        super.onPause()
    }

    //Włączanie/wyłączanie trybu edycji
    /*fun changeEditMode(enabled: Boolean, visible: Boolean) {
        editMode = enabled
        adapter.editMode = enabled

        if(!visible) {
            supportActionBar!!.title = "Licznik"
            floatingActionButton.hide()
        }
        else {
            floatingActionButton.show()
            if (adapter.editMode) {
                supportActionBar!!.title = "Edytuj gracza"
                floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp)
            }
            else {
                supportActionBar!!.title = game.name
                floatingActionButton.setImageResource(R.drawable.ic_edit_white_24dp)
            }
        }

        invalidateOptionsMenu()
        adapter.notifyDataSetChanged()
    }*/

    //Tworzenie guzików na app barze
    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val mCreateNewButton = menu.findItem(R.id.action_create_new)
        val mLoadButton = menu.findItem(R.id.action_folder)
        val mClearButton = menu.findItem(R.id.action_clear)
        val mSettingsButton = menu.findItem(R.id.navigation_settings)
        if (adapter.editMode) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            mCreateNewButton.isVisible = false
            mLoadButton.isVisible = false
            mClearButton.isVisible = false
            mSettingsButton.isVisible = true
        }
        else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            mCreateNewButton.isVisible = true
            mLoadButton.isVisible = true
            mClearButton.isVisible = true
            mSettingsButton.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }*/

    //Zapisywanie gry do SharedPreferences
    /*fun saveGameInSharedPreferences(game: Game, position: Int) {
        val jsonGame = Gson().toJson(game)
        val editor = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE).edit()
        editor.putString("GraPrefs", jsonGame)
        editor.putInt("IndexPrefs", position)
        editor.commit()
    }*/

    //Wczytanie gry z SharedPreferences
    /*fun getGameFromSharedPreferences(): Game? {
        val prefs = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val gra: String? = prefs.getString("GraPrefs", null)
        val gameType = object : TypeToken<Game>() {}.type
        return when(gra) {
            null -> null
            else -> Gson().fromJson<Game>(gra, gameType)
        }
    }*/

    //Wczytanie indexu gry z SharedPreferences
    /*fun getGameIndexFromSharedPreferences(): Int {
        val prefs = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        return prefs.getInt("IndexPrefs", 0)
    }*/

    //Serializowanie listy graczy do jsona
    /*fun serializePlayerList(list: ArrayList<Player>): String = Gson().toJson(list)*/

    //Tworzenie domyślnej gry
    /*fun createDefaultGame(name: String): Game {
        val playerList = ArrayList<Player>()
        playerList.add(Player("Gracz 1", resources.getInteger(R.integer.default_min_level)))
        playerList.add(Player("Gracz 2", resources.getInteger(R.integer.default_min_level)))
        playerList.add(Player("Gracz 3", resources.getInteger(R.integer.default_min_level)))
        return Game(name, serializePlayerList(playerList))
    }*/

    //Tworzenie nowej gry
    /*fun createNewGame(inputList: ArrayList<Game>) {
        val list = ArrayList<Game>()
        list.addAll(inputList)
        val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
        val editText = frameLayout.findViewById<View>(R.id.editText) as EditText
        AlertDialog.Builder(this@MainActivity)
                .setTitle("Stwórz grę")
                .setPositiveButton("Ok") { dialog, which ->
                    if (editText.text.toString() == "")
                        editText.setText("Gra " + (list.size + 1))
                    game = createDefaultGame(editText.text.toString())
                    list.add(game)
                    gameIndex = (list.size - 1)
                    visibleEditMode = true
                    setPlayerAdapter(game, visibleEditMode)
                    gameList = list
                    saveGame(game, gameList)
                }
                .setNegativeButton("Anuluj", null)
                .setView(frameLayout)
                .create()
                .show()
    }*/

    //Wczytanie gry z listy
    /*fun loadGame(list: ArrayList<Game>) {
        if(list.isNotEmpty()) {
            val nameArray = arrayOfNulls<String>(list.size)
            for (i in list.indices)
                nameArray[i] = list[i].name

            AlertDialog.Builder(this@MainActivity)
                    .setTitle("Wczytaj rozgrywkę")
                    .setItems(nameArray) { dialog, which ->
                        game = list[which]
                        gameIndex = which
                        visibleEditMode = true
                        setPlayerAdapter(game, visibleEditMode)
                        Toast.makeText(this@MainActivity, "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show()
                        gameList = list
                        saveGame(game, gameList)
                    }
                    .create()
                    .show()
        }
        else
            Toast.makeText(this@MainActivity, "Brak zapisanych rozgrywek", Toast.LENGTH_SHORT).show()
    }*/

    //Usuwanie aktywnej gry
    /*fun deleteGame(gameList: ArrayList<Game>) {
        if(gameIndex != null)
        {
            AlertDialog.Builder(this@MainActivity)
                    .setTitle("Usuń grę")
                    .setPositiveButton("Tak") { dialog, which ->
                        gameList.removeAt(gameIndex!!)
                        saveGameListInSharedPreferences(gameList)
                        game = Game("", serializePlayerList(ArrayList<Player>()))
                        visibleEditMode = false
                        setPlayerAdapter(game, visibleEditMode)
                        gameIndex = null
                        if (gameList.isEmpty())
                            createNewGame(gameList)
                        else
                            loadGame(gameList)
                    }
                    .setNeutralButton("Nie", null)
                    .create()
                    .show()
        }
        else
            Toast.makeText(this@MainActivity, "Brak aktywnej rozgrywki", Toast.LENGTH_SHORT).show()
    }*/

    //Zapisywanie aktywnej gry i listy gier
    /*fun saveGame(game: Game, gameList: ArrayList<Game>) {
        gameList[gameIndex!!] = game
        saveGameListInSharedPreferences(gameList)
    }*/
}

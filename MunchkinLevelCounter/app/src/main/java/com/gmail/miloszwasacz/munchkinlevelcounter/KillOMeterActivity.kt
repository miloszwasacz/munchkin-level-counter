package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_kill_o_meter.*

class KillOMeterActivity : AppCompatActivity() {
    private lateinit var game: Game
    private var gameIndex = 0
    private var playerPosition = 0
    private var minBonus = 0
    private var maxViewValue = 999
    private var playerCategoriesList = ArrayList<Category>()
    private var monsterCategoriesList = ArrayList<Category>()
    internal lateinit var playerAdapter: KillOMeterAdapter
    internal lateinit var monsterAdapter: KillOMeterAdapter
    internal var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill_o_meter)

        supportActionBar!!.title = "Kill-O-Meter"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Ustawianie wartości z poprzedniego activity
        minBonus = resources.getInteger(R.integer.default_min_bonus)
        gameIndex = intent.getIntExtra("EXTRA_GAME_INDEX", 0)
        playerPosition = intent.getIntExtra("EXTRA_PLAYER_POSITION", 0)
        val json = intent.getStringExtra("EXTRA_GAME")
        val gameType = object: TypeToken<Game>() {}.type
        game = Gson().fromJson<Game>(json, gameType)

        //Przywracanie stanu poprzedniego listy graczy
        if(savedInstanceState != null){
            val jsonGame = savedInstanceState.getString("Gra")
            val gameType = object : TypeToken<Game>() {}.type
            game = Gson().fromJson<Game>(jsonGame, gameType)
            playerPosition = savedInstanceState.getInt("Pozycja")

            val listType = object : TypeToken<ArrayList<Category>>() {}.type

            val jsonCategoryPlayer = savedInstanceState.getString("ListaKategoriiGracza")
            playerCategoriesList = Gson().fromJson<ArrayList<Category>>(jsonCategoryPlayer, listType)

            val jsonCategoryMonster = savedInstanceState.getString("ListaKategoriiPotwora")
            monsterCategoriesList = Gson().fromJson<ArrayList<Category>>(jsonCategoryMonster, listType)
        }
        else {
            val playerList: ArrayList<Player> = extractPlayerListFromGame(game)
            playerCategoriesList.add(Category("Poziom:", playerList[playerPosition].level, resources.getInteger(R.integer.level_incremetation), game.maxLevel, game.minLevel, true))
            playerCategoriesList.add(Category("Przedmioty:", minBonus, resources.getInteger(R.integer.items_incrementation), maxViewValue, minBonus, true))
            playerCategoriesList.add(Category("Jednorazowego użytku:", minBonus, resources.getInteger(R.integer.bonus_incrementation), maxViewValue, minBonus, true))
            playerCategoriesList.add(Category("Suma:", 0, 1, Int.MAX_VALUE, minBonus, false))

            monsterCategoriesList.add(Category("Poziom:", game.minLevel, resources.getInteger(R.integer.level_incremetation), game.maxLevel, game.minLevel, true))
            monsterCategoriesList.add(Category("Wzmacniacze:", minBonus, resources.getInteger(R.integer.enhancer_incrementation), maxViewValue, minBonus, true))
            monsterCategoriesList.add(Category("Jednorazowego użytku:", minBonus, resources.getInteger(R.integer.bonus_incrementation), maxViewValue, minBonus, true))
            monsterCategoriesList.add(Category("Suma:", 0, 1, Int.MAX_VALUE, minBonus, false))
        }

        setAdapters(game)
    }

    //Ustawianie adapterów
    fun setAdapters(game: Game) {
        recyclerViewPlayer.setHasFixedSize(true)
        recyclerViewPlayer.layoutManager = LinearLayoutManager(this)
        val playerList: ArrayList<Player> = extractPlayerListFromGame(game)
        playerAdapter = KillOMeterAdapter(playerCategoriesList)

        recyclerViewMonster.setHasFixedSize(true)
        recyclerViewMonster.layoutManager = LinearLayoutManager(this)
        monsterAdapter = KillOMeterAdapter(monsterCategoriesList)

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (element in playerList) {
            if (element.level > game.maxLevel)
                element.level = game.maxLevel
            else if (element.level < game.minLevel)
                element.level = game.minLevel
        }

        //Obsługa kontrolek gracza
        playerAdapter.setOnItemClickListener(object : KillOMeterAdapter.OnItemClickListener {
            //Zwiększenie wartości
            override fun onAddClick(position: Int) {
                if (playerCategoriesList[position].value < playerCategoriesList[position].maxValue) {
                    playerCategoriesList[position].value += playerCategoriesList[position].incrementation
                    playerAdapter.notifyItemChanged(position)
                    updateSummary(playerCategoriesList, playerAdapter)
                    checkWinner(playerCategoriesList[playerCategoriesList.size - 1].value, monsterCategoriesList[monsterCategoriesList.size - 1].value)
                }
            }

            //Zmniejszenie wartości
            override fun onRemoveClick(position: Int) {
                if (playerCategoriesList[position].value > playerCategoriesList[position].minValue) {
                    playerCategoriesList[position].value -= playerCategoriesList[position].incrementation
                    playerAdapter.notifyItemChanged(position)
                    updateSummary(playerCategoriesList, playerAdapter)
                    checkWinner(playerCategoriesList[playerCategoriesList.size - 1].value, monsterCategoriesList[monsterCategoriesList.size - 1].value)
                }
            }
        })

        //Obsługa kontrolek potwora
        monsterAdapter.setOnItemClickListener(object : KillOMeterAdapter.OnItemClickListener {
            //Zwiększenie wartości
            override fun onAddClick(position: Int) {
                if (monsterCategoriesList[position].value < monsterCategoriesList[position].maxValue) {
                    monsterCategoriesList[position].value += monsterCategoriesList[position].incrementation
                    monsterAdapter.notifyItemChanged(position)
                    updateSummary(monsterCategoriesList, monsterAdapter)
                    checkWinner(playerCategoriesList[playerCategoriesList.size - 1].value, monsterCategoriesList[monsterCategoriesList.size - 1].value)
                }
            }

            //Zmniejszenie wartości
            override fun onRemoveClick(position: Int) {
                if (monsterCategoriesList[position].value < monsterCategoriesList[position].maxValue) {
                    monsterCategoriesList[position].value -= monsterCategoriesList[position].incrementation
                    monsterAdapter.notifyItemChanged(position)
                    updateSummary(monsterCategoriesList, monsterAdapter)
                    checkWinner(playerCategoriesList[playerCategoriesList.size - 1].value, monsterCategoriesList[monsterCategoriesList.size - 1].value)
                }
            }
        })

        updateSummary(playerCategoriesList, playerAdapter)
        updateSummary(monsterCategoriesList, monsterAdapter)
        checkWinner(playerCategoriesList[playerCategoriesList.size - 1].value, monsterCategoriesList[monsterCategoriesList.size - 1].value)

        recyclerViewPlayer.adapter = playerAdapter
        recyclerViewMonster.adapter = monsterAdapter
    }

    //Zapisywanie stanu gry
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("Gra", Gson().toJson(game))
        savedInstanceState.putInt("Pozycja", playerPosition)
        savedInstanceState.putString("ListaKategoriiGracza", Gson().toJson(playerCategoriesList))
        savedInstanceState.putString("ListaKategoriiPotwora", Gson().toJson(monsterCategoriesList))
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

    //Aktualizacja podsumowań
    fun updateSummary(categoryList: ArrayList<Category>, adapter: KillOMeterAdapter) {
        categoryList[categoryList.size - 1].value = 0
        for(element in categoryList.dropLast(1)) {
            categoryList[categoryList.size - 1].value += element.value
        }
        adapter.notifyItemChanged(categoryList.size - 1)
    }

    //Sprawdzanie wygrywającego
    fun checkWinner(playerSummary: Int, monsterSummary: Int) {
        val winnerDrawable = resources.getDrawable(R.drawable.ic_munchkin_winner_24dp)
        val loserDrawable = resources.getDrawable(R.drawable.ic_munchkin_loser_24dp)
        val tieDrawable = resources.getDrawable(R.drawable.ic_munchkin_tie_24dp)

        winnerDrawable.setTint(resources.getColor(R.color.text_color))
        loserDrawable.setTint(resources.getColor(R.color.text_color))
        tieDrawable.setTint(resources.getColor(R.color.text_color))

        if (playerSummary > monsterSummary) {
            imageViewWinnerPlayer.setImageDrawable(winnerDrawable)
            imageViewWinnerMonster.setImageDrawable(loserDrawable)
        } else if (playerSummary < monsterSummary) {
            imageViewWinnerPlayer.setImageDrawable(loserDrawable)
            imageViewWinnerMonster.setImageDrawable(winnerDrawable)
        } else {
            imageViewWinnerPlayer.setImageDrawable(tieDrawable)
            imageViewWinnerMonster.setImageDrawable(tieDrawable)
        }
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

    //Strzałeczka "back"
    override fun onOptionsItemSelected(item: MenuItem) = if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else super.onOptionsItemSelected(item)

    //Wyjście z Activity
    override fun onBackPressed() {
        val playerList = extractPlayerListFromGame(game)
        playerList[playerPosition].level= playerCategoriesList[0].value
        insertPlayerListIntoGame(playerList, game)
        val json = Gson().toJson(game)
        val returnIntent = Intent()
        returnIntent.putExtra("resultGame", json)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //Zapisywanie gry podczas wyjścia z aplikacji
    override fun onPause() {
        val playerList = extractPlayerListFromGame(game)
        playerList[playerPosition].level= playerCategoriesList[0].value
        insertPlayerListIntoGame(playerList, game)
        val gameList = getGameListFromSharedPreferences()
        gameList!![gameIndex] = game
        saveGameListInSharedPreferences(gameList)
        super.onPause()
    }
}
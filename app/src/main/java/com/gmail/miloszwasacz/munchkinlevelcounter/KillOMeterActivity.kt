package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_kill_o_meter.*

class KillOMeterActivity : AppCompatActivity() {
    private lateinit var game: Game
    private var gameIndex = 0
    private lateinit var playerList: ArrayList<Player>
    private var playerPosition = 0
    private var playerFieldList = ArrayList<BaseItem>()
    private var monsterFieldList = ArrayList<BaseItem>()
    private lateinit var playerAdapter: KillOMeterAdapter
    private lateinit var monsterAdapter: KillOMeterAdapter
    private lateinit var pagerAdapter: KillOMeterPagerAdapter
    private val titleList = ArrayList<String>()
    //private lateinit var operationAdd: String
    //private lateinit var operationRemove: String
    private var maxViewValue = 999
    //private var minBonus = 0
    //private var levelIncrementation = 1
    //private var itemIncrementation = 1
    //private var bonusIncrementation = 1
    //private var enhancerIncrementation = 5
    private var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill_o_meter)

        //supportActionBar!!.title = "Kill-O-Meter"
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val gameType = object: TypeToken<Game>() {}.type
        val json = intent.getStringExtra("EXTRA_GAME")
        game = Gson().fromJson<Game>(json, gameType)
        playerPosition = intent.getIntExtra("EXTRA_PLAYER_POSITION", 0)
        gameIndex = intent.getIntExtra("EXTRA_GAME_INDEX", 0)

        //Przywracanie stanu poprzedniego
        if(savedInstanceState != null) {
            val jsonGame = savedInstanceState.getString("Gra")
            game = Gson().fromJson<Game>(jsonGame, gameType)
            playerPosition = savedInstanceState.getInt("Pozycja")
            gameIndex = savedInstanceState.getInt("IndexGry")

            val jsonPlayerFieldList = savedInstanceState.getString("ListaPolGraczy")
            val playerItemListType = object: TypeToken<ArrayList<PlayerItem>>() {}.type
            val itemList = Gson().fromJson<ArrayList<PlayerItem>>(jsonPlayerFieldList, playerItemListType)
            playerList = GameActivity().extractPlayerListFromGame(game)
            for(item in itemList) {
                for(player in playerList) {
                    if(item.playerIndex == playerList.indexOf(player)) {
                        playerFieldList.add(player)
                        for(bonus in item.bonusList)
                            playerFieldList.add(bonus)
                    }
                }
            }

            val jsonMonsterFieldList = savedInstanceState.getString("ListaPolPotworow")
            val monsterItemListType = object: TypeToken<ArrayList<MonsterItem>>() {}.type
            val monsterItemList = Gson().fromJson<ArrayList<MonsterItem>>(jsonMonsterFieldList, monsterItemListType)
            for(item in monsterItemList) {
                monsterFieldList.add(item.monster)
                for(bonus in item.bonusList)
                    monsterFieldList.add(bonus)
            }
        }
        else {
            playerList = GameActivity().extractPlayerListFromGame(game)
            playerFieldList.add(playerList[playerPosition])
        }

        //Atualizacja podsumowania
        var playerSummary = 0
        for(item in playerFieldList) {
            playerSummary += item.value
        }
        var monsterSummary = 0
        for(item in monsterFieldList) {
            monsterSummary += item.value
        }

        //Ustawianie adapterów dla pagera
        val playerRecyclerView = layoutInflater.inflate(R.layout.player_kill_o_meter, pager, false) as RecyclerView
        val monsterRecyclerView = layoutInflater.inflate(R.layout.monster_kill_o_meter, pager, false) as RecyclerView

        val viewList = ArrayList<View>()
        viewList.add(playerRecyclerView)
        viewList.add(monsterRecyclerView)

        titleList.add("Gracze: $playerSummary")
        titleList.add("Potwory: $monsterSummary")

        pagerAdapter = KillOMeterPagerAdapter(viewList, titleList)
        pager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(pager)

        //Ustawianie adapterów dla recyclerView
        setPlayerAdapter(playerFieldList, playerRecyclerView, game)
        setMonsterAdapter(monsterFieldList, monsterRecyclerView, game)

        //Dodawanie nowych graczy/potworów
        floatingActionButton.setOnClickListener {
            //Dodawanie nowych graczy
            if(pager.focusedChild == viewList[0]) {
                //Tworzenie listy graczy jeszcze nie dodanych
                val tempPlayers = ArrayList<Player>()
                for(player in playerList) {
                    tempPlayers.add(player)
                }
                for(element in playerFieldList) {
                    if(element is Player) {
                        //tempPlayers.add(element)
                        tempPlayers.remove(element)
                    }
                }
                val tempNameList = arrayOfNulls<String>(tempPlayers.size)
                for(i in tempPlayers.indices) {
                    tempNameList[i] = tempPlayers[i].name
                }

                //Dodawanie graczy
                if(tempNameList.isNotEmpty()) {
                    AlertDialog.Builder(this@KillOMeterActivity).setTitle("Wybierz gracza").setItems(tempNameList) { dialog, which ->
                        for(player in playerList) {
                            if(tempPlayers[which] == player) {
                                playerFieldList.add(player)
                                playerAdapter.notifyItemInserted(playerFieldList.size - 1)
                            }
                        }
                        //Sprawdzanie czy można dodać jeszcze jakiś graczy
                        tempPlayers.removeAt(which)
                        if(tempPlayers.isEmpty())
                            floatingActionButton.hide()

                        //Atualizacja podsumowania
                        var playerSummary = 0
                        for(item in playerFieldList) {
                            playerSummary += item.value
                        }
                        titleList[0] = "Gracze: $playerSummary"
                        pagerAdapter.notifyDataSetChanged()
                    }
                    .create()
                    .show()
                }
            }
            //Dodawanie nowych potworów
            else {
                val frameLayout = layoutInflater.inflate(R.layout.monster_dialog, null, false) as FrameLayout
                val editTextName = frameLayout.findViewById<EditText>(R.id.editTextName)
                val editTextLevel = frameLayout.findViewById<EditText>(R.id.editTextLevel)

                AlertDialog.Builder(this@KillOMeterActivity)
                        .setTitle("Dodaj potwora")
                        .setPositiveButton("Ok") { dialog, which ->
                            editTextName.text.trim()
                            removeLeadingZeros(editTextLevel)
                            if(editTextName.text.toString() == "")
                                editTextName.setText("Potwór")
                            if(editTextLevel.text.toString() == "")
                                editTextLevel.setText("1")
                            val level = tryParse(editTextLevel.text.toString(), maxViewValue)
                            monsterFieldList.add(Monster(editTextName.text.toString(), level))
                            monsterAdapter.notifyItemInserted(monsterFieldList.size - 1)

                            //Atualizacja podsumowania
                            var monsterSummary = 0
                            for(item in monsterFieldList) {
                                monsterSummary += item.value
                            }
                            titleList[1] = "Potwory: $monsterSummary"
                            pagerAdapter.notifyDataSetChanged()
                        }
                        .setNegativeButton("Anuluj", null)
                        .setView(frameLayout)
                        .create()
                        .show()
            }
        }
    }

    //PlayerAdapter
    private fun setPlayerAdapter(fieldList: ArrayList<BaseItem>, playerRecyclerView: RecyclerView, game: Game) {
        playerRecyclerView.setHasFixedSize(true)
        playerRecyclerView.layoutManager = LinearLayoutManager(this)
        val list: ArrayList<Player> = GameActivity().extractPlayerListFromGame(game)
        playerAdapter = KillOMeterAdapter(fieldList, game)

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (element in list) {
            if (element.value > game.maxLevel)
                element.value = game.maxLevel
            else if (element.value < game.minLevel)
                element.value = game.minLevel
        }
        GameActivity().insertPlayerListIntoGame(list, game)

        //Obsługa kontrolek
        playerAdapter.setOnItemClickListener(object : KillOMeterAdapter.OnItemClickListener {
            //Zwiększenie poziomu gracza
            override fun onAddClick(position: Int) {
                if (fieldList[position].value < game.maxLevel) {
                    fieldList[position].value++
                    playerAdapter.notifyItemChanged(position)

                    //Atualizacja podsumowania
                    var playerSummary = 0
                    for(item in playerFieldList) {
                        playerSummary += item.value
                    }
                    titleList[0] = "Gracze: $playerSummary"
                    pagerAdapter.notifyDataSetChanged()
                }
            }

            //Zmniejszenie poziomu gracza
            override fun onRemoveClick(position: Int) {
                if (fieldList[position].value > game.minLevel) {
                    fieldList[position].value--
                    playerAdapter.notifyItemChanged(position)

                    //Atualizacja podsumowania
                    var playerSummary = 0
                    for(item in playerFieldList) {
                        playerSummary += item.value
                    }
                    titleList[0] = "Gracze: $playerSummary"
                    pagerAdapter.notifyDataSetChanged()
                }
            }

            //Dodanie nowego bonusu
            override fun onAddBonusClick(position: Int) {
                val values = arrayOf("1", "2", "3", "4", "5", "6")

                AlertDialog.Builder(this@KillOMeterActivity)
                        .setTitle("Dodaj bonus")
                        .setItems(values) { dialog, which ->
                            fieldList.add(position + 1, BaseItem(values[which].toInt()))
                            playerAdapter.notifyItemInserted(position + 1)

                            //Atualizacja podsumowania
                            var playerSummary = 0
                            for(item in playerFieldList) {
                                playerSummary += item.value
                            }
                            titleList[0] = "Gracze: $playerSummary"
                            pagerAdapter.notifyDataSetChanged()
                            }
                        .create()
                        .show()
            }

            //Usunięcie gracza/bonusu
            override fun onDeleteClick(position: Int) {
                //Usuwanie gracza
                if(fieldList[position] is Player) {
                    val playerFieldsIndexes = ArrayList<Int>()

                    for(element in fieldList) {
                        if(element is Player && fieldList.indexOf(element) > position)
                            playerFieldsIndexes.add(fieldList.indexOf(element))
                    }
                    val minPlayerIndex = playerFieldsIndexes.min()
                    if(minPlayerIndex != null){
                        for(i in (minPlayerIndex - 1)  downTo position){
                            fieldList.removeAt(i)
                            playerAdapter.notifyItemRemoved(i)
                        }
                    }
                    else {
                        for(i in (fieldList.size - 1) downTo position) {
                            fieldList.removeAt(i)
                            playerAdapter.notifyItemRemoved(i)
                        }
                    }

                    //Sprawdzanie czy można dodać jeszcze jakiś graczy
                    val tempPlayers = ArrayList<Player>()
                    for(player in playerList) {
                        tempPlayers.add(player)
                    }
                    for(element in playerFieldList) {
                        if(element is Player) {
                            //tempPlayers.add(element)
                            tempPlayers.remove(element)
                        }
                    }
                    if(tempPlayers.isNotEmpty())
                        floatingActionButton.show()
                }
                //Usuwanie bonusu
                else {
                    fieldList.removeAt(position)
                    playerAdapter.notifyItemRemoved(position)
                }

                //Atualizacja podsumowania
                var playerSummary = 0
                for(item in playerFieldList) {
                    playerSummary += item.value
                }
                titleList[0] = "Gracze: $playerSummary"
                pagerAdapter.notifyDataSetChanged()
            }
        })

        playerRecyclerView.adapter = playerAdapter
    }

    //MonsterAdapter
    private fun setMonsterAdapter(fieldList: ArrayList<BaseItem>, monsterRecyclerView: RecyclerView, game: Game) {
        monsterRecyclerView.setHasFixedSize(true)
        monsterRecyclerView.layoutManager = LinearLayoutManager(this)
        monsterAdapter = KillOMeterAdapter(fieldList, game)

        //Obsługa kontrolek
        monsterAdapter.setOnItemClickListener(object : KillOMeterAdapter.OnItemClickListener {
            override fun onAddClick(position: Int) {}
            override fun onRemoveClick(position: Int) {}

            //Dodanie nowego bonusu
            override fun onAddBonusClick(position: Int) {
                val values = arrayOf("1", "2", "3", "4", "5", "6")

                AlertDialog.Builder(this@KillOMeterActivity)
                        .setTitle("Dodaj bonus")
                        .setItems(values) { dialog, which ->
                            fieldList.add(position + 1, BaseItem(values[which].toInt()))
                            monsterAdapter.notifyItemInserted(position + 1)

                            //Atualizacja podsumowania
                            var monsterSummary = 0
                            for(item in monsterFieldList) {
                                monsterSummary += item.value
                            }
                            titleList[1] = "Potwory: $monsterSummary"
                            pagerAdapter.notifyDataSetChanged()
                        }
                        .create()
                        .show()
            }

            //Usunięcie potwora/bonusu
            override fun onDeleteClick(position: Int) {
                //Usuwanie potwora
                if(fieldList[position] is Monster) {
                    val monsterFieldsIndexes = ArrayList<Int>()

                    for(element in fieldList) {
                        if(element is Monster && fieldList.indexOf(element) > position)
                            monsterFieldsIndexes.add(fieldList.indexOf(element))
                    }
                    val minMonsterIndex = monsterFieldsIndexes.min()
                    if(minMonsterIndex != null){
                        for(i in (minMonsterIndex - 1)  downTo position){
                            fieldList.removeAt(i)
                            monsterAdapter.notifyItemRemoved(i)
                        }
                    }
                    else {
                        for(i in (fieldList.size - 1) downTo position) {
                            fieldList.removeAt(i)
                            monsterAdapter.notifyItemRemoved(i)
                        }
                    }
                }
                //Usuwanie bonusu
                else {
                    fieldList.removeAt(position)
                    monsterAdapter.notifyItemRemoved(position)
                }

                //Atualizacja podsumowania
                var monsterSummary = 0
                for(item in monsterFieldList) {
                    monsterSummary += item.value
                }
                titleList[1] = "Potwory: $monsterSummary"
                pagerAdapter.notifyDataSetChanged()
            }
        })

        monsterRecyclerView.adapter = monsterAdapter
    }

    //tryParse int
    fun tryParse(value: String, defaultVal: Int) = try {
        Integer.parseInt(value)
    } catch (e: NumberFormatException) {
        defaultVal
    }

    //Usuń zera z przodu
    fun removeLeadingZeros(field: EditText) {
        var value = field.text.toString()
        while (value.indexOf("0") == 0 && value.length > 1)
            value = value.substring(1)

        field.setText(value)
    }

    //Obsługa strzałeczki w tył
    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else super.onOptionsItemSelected(item)

    //Wyjście z Activity
    override fun onBackPressed() {
        GameActivity().insertPlayerListIntoGame(playerList, game)
        val json = Gson().toJson(game)
        val returnIntent = Intent()
        returnIntent.putExtra("resultGame", json)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
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

    //Zapisywanie stanu Kill-O-Metera
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //Gra, indeks gry, pozycja gracza
        GameActivity().insertPlayerListIntoGame(playerList, game)
        savedInstanceState.putString("Gra", Gson().toJson(game))
        savedInstanceState.putInt("IndexGry", gameIndex)
        savedInstanceState.putInt("Pozycja", playerPosition)

        /*
        val itemList = ArrayList<PlayerItem>()
        for(field in playerFieldList) {
            if(field is Player) {
                val fieldIndex = playerFieldList.indexOf(field)
                val indexes = ArrayList<Int>()
                for(element in playerFieldList) {
                    if(element is Player && playerFieldList.indexOf(element) > fieldIndex)
                        indexes.add(playerFieldList.indexOf(element))
                }
                val minIndex = indexes.min()
                if(minIndex != null) {
                    val bonusList = ArrayList<BaseItem>()
                    for(i in (minIndex - 1) downTo (fieldIndex + 1))
                        bonusList.add(playerFieldList[i])
                    itemList.add(PlayerItem(field, bonusList))
                }
                else {
                    val bonusList = ArrayList<BaseItem>()
                    for(i in (playerFieldList.size - 1) downTo (fieldIndex + 1))
                        bonusList.add(playerFieldList[i])
                    itemList.add(PlayerItem(field, bonusList))
                }
            }
        }*/
        //Lista graczy z bonusami
        val playerItemList = ArrayList<PlayerItem>()
        for(field in playerFieldList){
            if(field is Player) {
                val fieldIndex = playerFieldList.indexOf(field)
                var playerIndex = 0
                for(player in playerList) {
                    if(player == field)
                        playerIndex = playerList.indexOf(player)
                }
                val indexes = ArrayList<Int>()
                for(element in playerFieldList) {
                    if(element is Player && playerFieldList.indexOf(element) > fieldIndex)
                        indexes.add(playerFieldList.indexOf(element))
                }
                val minIndex = indexes.min()
                if(minIndex != null) {
                    val bonusList = ArrayList<BaseItem>()
                    for(i in (minIndex - 1) downTo (fieldIndex + 1))
                        bonusList.add(playerFieldList[i])
                    bonusList.reverse()
                    playerItemList.add(PlayerItem(playerIndex, bonusList))
                }
                else {
                    val bonusList = ArrayList<BaseItem>()
                    for(i in (playerFieldList.size - 1) downTo (fieldIndex + 1))
                        bonusList.add(playerFieldList[i])
                    bonusList.reverse()
                    playerItemList.add(PlayerItem(playerIndex, bonusList))
                }
            }
        }
        savedInstanceState.putString("ListaPolGraczy", Gson().toJson(playerItemList))

        //Lista potworów z bonusami
        val monsterItemList = ArrayList<MonsterItem>()
        for(field in monsterFieldList) {
            if(field is Monster) {
                val fieldIndex = monsterFieldList.indexOf(field)
                val indexes = ArrayList<Int>()
                for(element in monsterFieldList) {
                    if(element is Monster && monsterFieldList.indexOf(element) > fieldIndex)
                        indexes.add(monsterFieldList.indexOf(element))
                }
                val minIndex = indexes.min()
                if(minIndex != null) {
                    val bonusList = ArrayList<BaseItem>()
                    for(i in (minIndex - 1) downTo (fieldIndex + 1))
                        bonusList.add(monsterFieldList[i])
                    bonusList.reverse()
                    monsterItemList.add(MonsterItem(field, bonusList))
                }
                else {
                    val bonusList = ArrayList<BaseItem>()
                    for(i in (monsterFieldList.size - 1) downTo (fieldIndex + 1))
                        bonusList.add(monsterFieldList[i])
                    bonusList.reverse()
                    monsterItemList.add(MonsterItem(field, bonusList))
                }
            }
        }
        savedInstanceState.putString("ListaPolPotworow", Gson().toJson(monsterItemList))
    }

    //Zapisywanie gry podczas wyjścia z aplikacji
    override fun onPause() {
        GameActivity().insertPlayerListIntoGame(playerList, game)
        val gameList = getGameListFromSharedPreferences()
        gameList!![gameIndex] = game
        saveGameListInSharedPreferences(gameList)
        super.onPause()
    }

    /*
    //Ustawianie domyślnych wartości
    minBonus = resources.getInteger(R.integer.default_min_bonus)
    operationAdd = resources.getString(R.string.operation_add)
    operationRemove = resources.getString(R.string.operation_remove)
    levelIncrementation = resources.getInteger(R.integer.level_incremetation)
    itemIncrementation = resources.getInteger(R.integer.items_incrementation)
    bonusIncrementation = resources.getInteger(R.integer.bonus_incrementation)
    enhancerIncrementation = resources.getInteger(R.integer.enhancer_incrementation)
    gameIndex = intent.getIntExtra("EXTRA_GAME_INDEX", 0)
    playerPosition = intent.getIntExtra("EXTRA_PLAYER_POSITION", 0)
    val json = intent.getStringExtra("EXTRA_GAME")
    val gameType = object: TypeToken<Game>() {}.type
    game = Gson().fromJson<Game>(json, gameType)

    //Stworzenie listy pól
    bracketList.add(Bracket(editTextPlayerLevel, imageViewPlayerLevelAdd, imageViewPlayerLevelRemove))
    bracketList.add(Bracket(editTextPlayerItems, imageViewPlayerItemsAdd, imageViewPlayerItemsRemove))
    bracketList.add(Bracket(editTextPlayerBonus, imageViewPlayerBonusAdd, imageViewPlayerBonusRemove))
    bracketList.add(Bracket(editTextMonsterLevel, imageViewMonsterLevelAdd, imageViewMonsterLevelRemove))
    bracketList.add(Bracket(editTextMonsterEnhancer, imageViewMonsterEnhancerAdd, imageViewMonsterEnhancerRemove))
    bracketList.add(Bracket(editTextMonsterBonus, imageViewMonsterBonusAdd, imageViewMonsterBonusRemove))

    //Przywracanie stanu poprzedniego wartości
    if(savedInstanceState != null) {
        val jsonGame = savedInstanceState.getString("Gra")
        val gameType = object: TypeToken<Game>() {}.type
        game = Gson().fromJson<Game>(jsonGame, gameType)
        playerPosition = savedInstanceState.getInt("Pozycja")
        gameIndex = savedInstanceState.getInt("IndexGry")

        val values: ArrayList<String>
        val jsonValues = savedInstanceState.getString("ListaWartosci")
        val valuesListType = object: TypeToken<ArrayList<String>>() {}.type
        values = Gson().fromJson<ArrayList<String>>(jsonValues, valuesListType)
        for(i in bracketList.indices)
            bracketList[i].editText.setText(values[i])
    }
    playerList = GameActivity().extractPlayerListFromGame(game)

    //Ustawianie poziomu i nazwy gracza
    textViewPlayerName.text = playerList[playerPosition].name
    editTextPlayerLevel.setText(playerList[playerPosition].level.toString())

    checkValuesInBrackets(bracketList)
    updateSummary()
    */
    /*
        //Odejmij poziom graczowi
        imageViewPlayerLevelRemove.setOnClickListener {
            editValueInBracket(game.minLevel, game.maxLevel, editTextPlayerLevel, operationRemove, levelIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Dodaj poziom graczowi
        imageViewPlayerLevelAdd.setOnClickListener {
            editValueInBracket(game.minLevel, game.maxLevel, editTextPlayerLevel, operationAdd, levelIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Sprawdź czy poziom gracza jest w zakrasie poziomów
        editTextPlayerLevel.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(bracketList) }

        //Odejmij bonus z przedmiotów gracza
        imageViewPlayerItemsRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerItems, operationRemove, itemIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Dodaj bonus z przedmiotów gracza
        imageViewPlayerItemsAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerItems, operationAdd, itemIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerItems.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(bracketList) }

        //Odejmij bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerBonus, operationRemove, bonusIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Dodaj bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerBonus, operationAdd, bonusIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerBonus.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(bracketList) }


        //Odejmij poziom potworowi
        imageViewMonsterLevelRemove.setOnClickListener {
            editValueInBracket(game.minLevel, maxViewValue, editTextMonsterLevel, operationRemove, levelIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Dodaj poziom potworowi
        imageViewMonsterLevelAdd.setOnClickListener {
            editValueInBracket(game.minLevel, maxViewValue, editTextMonsterLevel, operationAdd, levelIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Sprawdź czy poziom potwora jest większy od 0 i nie jest pusty
        editTextMonsterLevel.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(bracketList) }

        //Odejmij wzmacniacz potwora
        imageViewMonsterEnhancerRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterEnhancer, operationRemove, enhancerIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Dodaj wzmacniacz potwora
        imageViewMonsterEnhancerAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterEnhancer, operationAdd, enhancerIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Sprawdź czy wzmacniacz potwora nie jest pusty
        editTextMonsterEnhancer.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(bracketList) }

        //Odejmij bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterBonus, operationRemove, bonusIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Dodaj bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterBonus, operationAdd, bonusIncrementation)
            checkValuesInBrackets(bracketList)
        }

        //Sprawdź czy bonus z jednorazowego użytku potwora nie jest pusty
        editTextMonsterBonus.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(bracketList) }
    }*/
    /*
    //Metoda "Zsumuj moc gracza i potwora"
    fun updateSummary() {
        editTextPlayerSummary.setText((tryParse(editTextPlayerLevel.text.toString(), game.maxLevel) + tryParse(editTextPlayerItems.text.toString(), maxViewValue) + tryParse(editTextPlayerBonus.text.toString(), maxViewValue)).toString())
        editTextMonsterSummary.setText((tryParse(editTextMonsterLevel.text.toString(), maxViewValue) + tryParse(editTextMonsterEnhancer.text.toString(), maxViewValue) + tryParse(editTextMonsterBonus.text.toString(), maxViewValue)).toString())
        checkWinner(editTextPlayerSummary, editTextMonsterSummary)
    }

    //Metoda "Zastosuj podaną operację na liczbach z pola"
    fun editValueInBracket(minValue: Int, maxValue: Int, bracket: EditText, operation: String, incrementationValue: Int) {
        //Dodawanie
        if (operation === resources.getString(R.string.operation_add)) {
            if (bracket.text.toString() == "" || tryParse(bracket.text.toString(), maxValue) < minValue)
                bracket.setText(minValue.toString())
            else {
                if (tryParse(bracket.text.toString(), maxValue) < maxValue)
                    bracket.setText((tryParse(bracket.text.toString(), maxValue) / incrementationValue * incrementationValue + incrementationValue).toString())
                else
                    bracket.setText(maxValue.toString())
            }

        }
        //Odejmowanie
        else if (operation === resources.getString(R.string.operation_remove)) {
            if (bracket.text.toString() == "" || tryParse(bracket.text.toString(), maxValue) < minValue)
                bracket.setText(minValue.toString())
            else {
                if (tryParse(bracket.text.toString(), maxValue) < incrementationValue)
                    bracket.setText(minValue.toString())
                else {
                    if (tryParse(bracket.text.toString(), maxValue) % incrementationValue != 0)
                        bracket.setText((tryParse(bracket.text.toString(), maxValue) / incrementationValue * incrementationValue).toString())
                    else
                        bracket.setText((tryParse(bracket.text.toString(), maxValue) - incrementationValue).toString())
                }
            }
        }
    }

    //Metoda "Sprawdź czy żadne pole nie jest puste"
    fun checkValuesInBrackets(bracketList: ArrayList<Bracket>) {
        for (element in bracketList) {
            removeLeadingZeros(element.editText)

            var maxValue: Int
            var minValue: Int
            when {
                element.editText === editTextPlayerLevel -> {
                    maxValue = game.maxLevel
                    minValue = game.minLevel
                }
                element.editText === editTextMonsterLevel -> {
                    maxValue = maxViewValue
                    minValue = game.minLevel
                }
                else -> {
                    maxValue = maxViewValue
                    minValue = minBonus
                }
            }

            if (element.editText.text.toString() == "" || tryParse(element.editText.text.toString(), maxValue) < minValue)
                element.editText.setText(minValue.toString())
            else if (tryParse(element.editText.text.toString(), maxValue) >= maxValue)
                element.editText.setText(maxValue.toString())

            if(element.editText.text.toString().toInt() == maxValue)
                element.buttonAdd.visibility = View.INVISIBLE
            else if(element.editText.text.toString().toInt() < maxValue)
                element.buttonAdd.visibility = View.VISIBLE
            if(element.editText.text.toString().toInt() == minValue)
                element.buttonRemove.visibility = View.INVISIBLE
            else if(element.editText.text.toString().toInt() > minValue)
                element.buttonRemove.visibility = View.VISIBLE
        }
        updateSummary()
    }

    //Metoda "Sprawdź kto wygrywa"
    fun checkWinner(PlayerSummary: EditText, MonsterSummary: EditText) {

        val winnerDrawable = resources.getDrawable(R.drawable.ic_munchkin_winner_24dp)
        val loserDrawable = resources.getDrawable(R.drawable.ic_munchkin_loser_24dp)
        val tieDrawable = resources.getDrawable(R.drawable.ic_munchkin_tie_24dp)

        //winnerDrawable.setTint(resources.getColor(R.color.text_color))
        //loserDrawable.setTint(resources.getColor(R.color.text_color))
        //tieDrawable.setTint(resources.getColor(R.color.text_color))

        when {
            tryParse(PlayerSummary.text.toString(), game.minLevel) > tryParse(MonsterSummary.text.toString(), game.minLevel) -> {
                imageViewWinnerPlayer.setImageDrawable(winnerDrawable)
                imageViewWinnerMonster.setImageDrawable(loserDrawable)
            }
            tryParse(PlayerSummary.text.toString(), game.minLevel) < tryParse(MonsterSummary.text.toString(), game.minLevel) -> {
                imageViewWinnerPlayer.setImageDrawable(loserDrawable)
                imageViewWinnerMonster.setImageDrawable(winnerDrawable)
            }
            else -> {
                imageViewWinnerPlayer.setImageDrawable(tieDrawable)
                imageViewWinnerMonster.setImageDrawable(tieDrawable)
            }
        }
    }

    //Strać focus podczas przewijania
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        for(bracket in bracketList) {
            if(event.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if(bracket.editText.isFocused) {
                    val outRect = Rect()
                    bracket.editText.getGlobalVisibleRect(outRect)
                    if(!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        bracket.editText.clearFocus()
                        //
                        // Hide keyboard
                        //
                        val imm: InputMethodManager = v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
*/
}

package com.gmail.miloszwasacz.munchkinlevelcounter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
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
    private var minViewValue = -99
    private var maxViewValue = 99
    private var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill_o_meter)

        //Ustawianie toolbara
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.activity_title_kill_o_meter)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        minViewValue = resources.getInteger(R.integer.min_view_value)
        maxViewValue = resources.getInteger(R.integer.max_view_value)
        val gameType = object: TypeToken<Game>() {}.type
        val json = intent.getStringExtra("EXTRA_GAME")
        game = Gson().fromJson(json, gameType)
        playerPosition = intent.getIntExtra("EXTRA_PLAYER_POSITION", 0)
        gameIndex = intent.getIntExtra("EXTRA_GAME_INDEX", 0)

        //Przywracanie stanu poprzedniego
        if(savedInstanceState != null) {
            val jsonGame = savedInstanceState.getString("Gra")
            game = Gson().fromJson(jsonGame, gameType)
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

        //Ustawianie adapterów dla pagera
        val playerRecyclerView = layoutInflater.inflate(R.layout.player_kill_o_meter, pager, false) as RecyclerView
        val monsterRecyclerView = layoutInflater.inflate(R.layout.monster_kill_o_meter, pager, false) as RecyclerView
        val viewList = ArrayList<View>()
        viewList.add(playerRecyclerView)
        viewList.add(monsterRecyclerView)

        titleList.add("${resources.getString(R.string.title_players)}: ${updateSumarry(playerFieldList)}")
        titleList.add("${resources.getString(R.string.title_monsters)}: ${updateSumarry(monsterFieldList)}")

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
                    if(element is Player)
                        tempPlayers.remove(element)
                }
                val tempNameList = arrayOfNulls<String>(tempPlayers.size)
                for(i in tempPlayers.indices) {
                    tempNameList[i] = tempPlayers[i].name
                }

                //Dodawanie graczy
                if(tempNameList.isNotEmpty()) {
                    AlertDialog.Builder(this@KillOMeterActivity)
                            .setTitle(resources.getString(R.string.dialog_choose_player))
                            .setItems(tempNameList) { dialog, which ->
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
                                titleList[0] = "${resources.getString(R.string.title_players)}: ${updateSumarry(playerFieldList)}"
                                pagerAdapter.notifyDataSetChanged()
                            }
                    .create()
                    .show()
                }
            }
            //Dodawanie nowych potworów
            else {
                val linearLayout = layoutInflater.inflate(R.layout.monster_dialog, null, false) as LinearLayout
                val editTextName = linearLayout.findViewById<EditText>(R.id.editTextName)
                val editTextLevel = linearLayout.findViewById<EditText>(R.id.editTextLevel)
                editTextName.requestFocus()

                val dialog: AlertDialog = AlertDialog.Builder(this@KillOMeterActivity)
                        .setTitle(resources.getString(R.string.dialog_add_new_monster))
                        .setPositiveButton(resources.getString(R.string.button_add)) { dialog, which ->
                            editTextName.text.trim()
                            removeLeadingZeros(editTextLevel)
                            if(editTextName.text.toString() == "")
                                editTextName.setText(resources.getString(R.string.monster))
                            if(editTextLevel.text.toString() == "")
                                editTextLevel.setText(resources.getInteger(R.integer.min_level).toString())
                            var level = tryParse(editTextLevel.text.toString(), maxViewValue)
                            if(level > maxViewValue)
                                level = maxViewValue
                            monsterFieldList.add(Monster(editTextName.text.toString(), level))
                            monsterAdapter.notifyItemInserted(monsterFieldList.size - 1)

                            //Atualizacja podsumowania
                            titleList[1] = "${resources.getString(R.string.title_monsters)}: ${updateSumarry(monsterFieldList)}"
                            pagerAdapter.notifyDataSetChanged()
                        }
                        .setNegativeButton(resources.getString(R.string.button_cancel), null)
                        .setView(linearLayout)
                        .create()
                dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                dialog.show()
            }
        }

        //Ukrywanie FABa
        pager.addOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if(position == 0) {
                    val tempPlayers = ArrayList<Player>()
                    for(player in playerList) {
                        tempPlayers.add(player)
                    }
                    for(element in playerFieldList) {
                        if(element is Player)
                            tempPlayers.remove(element)
                    }
                    if(tempPlayers.isEmpty())
                        floatingActionButton.hide()
                    else
                        floatingActionButton.show()
                }
                else {
                    floatingActionButton.show()
                }
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
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
                try {
                    if(fieldList[position].value < game.maxLevel) {
                        fieldList[position].value++
                        playerAdapter.notifyItemChanged(position)

                        //Atualizacja podsumowania
                        titleList[0] = "${resources.getString(R.string.title_players)}: ${updateSumarry(playerFieldList)}"
                        pagerAdapter.notifyDataSetChanged()
                    }
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }

            //Zmniejszenie poziomu gracza
            override fun onRemoveClick(position: Int) {
                try {
                    if(fieldList[position].value > game.minLevel) {
                        fieldList[position].value--
                        playerAdapter.notifyItemChanged(position)

                        //Atualizacja podsumowania
                        titleList[0] = "${resources.getString(R.string.title_players)}: ${updateSumarry(playerFieldList)}"
                        pagerAdapter.notifyDataSetChanged()
                    }
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }

            //Dodanie nowego bonusu
            @SuppressLint("InflateParams")
            override fun onAddBonusClick(position: Int) {
                try {
                    val frameLayout = layoutInflater.inflate(R.layout.bonus_dialog, null, false) as FrameLayout
                    val editTextValue = frameLayout.findViewById<EditText>(R.id.editText)

                    val dialog: AlertDialog = AlertDialog.Builder(this@KillOMeterActivity)
                            .setTitle(resources.getString(R.string.dialog_add_new_bonus))
                            .setPositiveButton(resources.getString(R.string.button_add)) { dialog, which ->
                                removeLeadingZeros(editTextValue)
                                if(editTextValue.text.toString() != "") {
                                    if(tryParse(editTextValue.text.toString(), maxViewValue) >= maxViewValue) editTextValue.setText(maxViewValue.toString())
                                    if(tryParse(editTextValue.text.toString(), minViewValue) <= minViewValue) editTextValue.setText(minViewValue.toString())
                                    fieldList.add(position + 1, BaseItem(editTextValue.text.toString().toInt()))
                                    playerAdapter.notifyItemInserted(position + 1)

                                    //Atualizacja podsumowania
                                    titleList[0] = "${resources.getString(R.string.title_players)}: ${updateSumarry(playerFieldList)}"
                                    pagerAdapter.notifyDataSetChanged()
                                }
                            }
                            .setNegativeButton(resources.getString(R.string.button_cancel), null)
                            .setView(frameLayout)
                            .create()
                    dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    dialog.show()
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }

            //Usunięcie gracza/bonusu
            override fun onDeleteClick(position: Int) {
                try {
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
                    titleList[0] = "${resources.getString(R.string.title_players)}: ${updateSumarry(playerFieldList)}"
                    pagerAdapter.notifyDataSetChanged()
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
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
            @SuppressLint("InflateParams")
            override fun onAddBonusClick(position: Int) {
                try {
                    val frameLayout = layoutInflater.inflate(R.layout.bonus_dialog, null, false) as FrameLayout
                    val editTextValue = frameLayout.findViewById<EditText>(R.id.editText)

                    val dialog: AlertDialog = AlertDialog.Builder(this@KillOMeterActivity)
                            .setTitle(resources.getString(R.string.dialog_add_new_bonus))
                            .setPositiveButton(resources.getString(R.string.button_add)) { dialog, which ->
                                removeLeadingZeros(editTextValue)
                                if(editTextValue.text.toString() != "") {
                                    if(tryParse(editTextValue.text.toString(), maxViewValue) >= maxViewValue) editTextValue.setText(maxViewValue.toString())
                                    if(tryParse(editTextValue.text.toString(), minViewValue) <= minViewValue) editTextValue.setText(minViewValue.toString())
                                    fieldList.add(position + 1, BaseItem(editTextValue.text.toString().toInt()))
                                    monsterAdapter.notifyItemInserted(position + 1)

                                    //Atualizacja podsumowania
                                    titleList[1] = "${resources.getString(R.string.title_monsters)}: ${updateSumarry(monsterFieldList)}"
                                    pagerAdapter.notifyDataSetChanged()
                                }
                            }
                            .setNegativeButton(resources.getString(R.string.button_cancel), null)
                            .setView(frameLayout)
                            .create()
                    dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    dialog.show()
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
            }

            //Usunięcie potwora/bonusu
            override fun onDeleteClick(position: Int) {
                try {
                    //Usuwanie potwora
                    if(fieldList[position] is Monster) {
                        val monsterFieldsIndexes = ArrayList<Int>()

                        for(element in fieldList) {
                            if(element is Monster && fieldList.indexOf(element) > position) monsterFieldsIndexes.add(fieldList.indexOf(element))
                        }
                        val minMonsterIndex = monsterFieldsIndexes.min()
                        if(minMonsterIndex != null) {
                            for(i in (minMonsterIndex - 1) downTo position) {
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
                    titleList[1] = "${resources.getString(R.string.title_monsters)}: ${updateSumarry(monsterFieldList)}"
                    pagerAdapter.notifyDataSetChanged()
                }
                catch(e: ArrayIndexOutOfBoundsException) {}
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

    //Usuwanie zer z przodu
    fun removeLeadingZeros(field: EditText) {
        var value = field.text.toString()
        var isNegative = false
        if(value.indexOf("-") == 0) {
            value = value.substring(1)
            isNegative = true
        }
        while (value.indexOf("0") == 0 && value.isNotEmpty())
            value = value.substring(1)
        if(isNegative && value.isNotEmpty()) {
            val int = tryParse(value, Int.MAX_VALUE)
            value = (-int).toString()
        }

        field.setText(value)
    }

    //Aktualizacja podsumowania
    private fun updateSumarry(fieldList: ArrayList<BaseItem>): Int {
        var summary = 0
        for(item in fieldList)
            summary += item.value
        return summary
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
}

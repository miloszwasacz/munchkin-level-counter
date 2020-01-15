package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_kill_o_meter.*

class KillOMeterActivity : AppCompatActivity() {
    internal var playerPosition = 0
    //internal lateinit var playerList: ArrayList<Player>
    var playerCategoriesList = ArrayList<Category>()
    var monsterCategoriesList = ArrayList<Category>()
    internal var maxViewValue = 999
    internal var minBonus = 0
    //internal lateinit var operationAdd: String
    //internal lateinit var operationRemove: String
    //internal var levelIncrementation = 1
    //internal var itemIncrementation = 1
    //internal var bonusIncrementation = 1
    //internal var enhancerIncrementation = 5
    internal lateinit var game: Game
    internal lateinit var playerAdapter: KillOMeterAdapter
    internal lateinit var monsterAdapter: KillOMeterAdapter
    internal var playerLevel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill_o_meter)

        supportActionBar!!.title = "Kill-O-Meter"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Ustawianie domyślnych wartości
        //minBonus = resources.getInteger(R.integer.default_min_bonus)
        //operationAdd = resources.getString(R.string.operation_add)
        //operationRemove = resources.getString(R.string.operation_remove)
        //levelIncrementation = resources.getInteger(R.integer.level_incremetation)
        //itemIncrementation = resources.getInteger(R.integer.items_incrementation)
        //bonusIncrementation = resources.getInteger(R.integer.bonus_incrementation)
        //enhancerIncrementation = resources.getInteger(R.integer.enhancer_incrementation)
        playerPosition = intent.getIntExtra("EXTRA_POSITION", 0)
        val json = intent.getStringExtra("EXTRA_GAME")
        val gameType = object: TypeToken<Game>() {}.type
        game = Gson().fromJson<Game>(json, gameType)
        //playerList = GameActivity().extractPlayerListFromGame(game)

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
    /*
        //Ustawianie poziomu i nazwy gracza
        textViewPlayerName.text = playerList[playerPosition].name
        editTextPlayerLevel.setText(playerList[playerPosition].level.toString())

        //Stworzenie listy pól
        val editTextList = ArrayList<EditText>()
        editTextList.add(editTextPlayerLevel)
        editTextList.add(editTextPlayerItems)
        editTextList.add(editTextPlayerBonus)
        editTextList.add(editTextMonsterLevel)
        editTextList.add(editTextMonsterEnhancer)
        editTextList.add(editTextMonsterBonus)

        updateSummary()

        //Odejmij poziom graczowi
        imageViewPlayerLevelRemove.setOnClickListener {
            editValueInBracket(game.minLevel, game.maxLevel, editTextPlayerLevel, operationRemove, levelIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj poziom graczowi
        imageViewPlayerLevelAdd.setOnClickListener {
            editValueInBracket(game.minLevel, game.maxLevel, editTextPlayerLevel, operationAdd, levelIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Sprawdź czy poziom gracza jest w zakrasie poziomów
        editTextPlayerLevel.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(editTextList) }

        //Odejmij bonus z przedmiotów gracza
        imageViewPlayerItemsRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerItems, operationRemove, itemIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj bonus z przedmiotów gracza
        imageViewPlayerItemsAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerItems, operationAdd, itemIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerItems.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(editTextList) }

        //Odejmij bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerBonus, operationRemove, bonusIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj bonus z jednorazowego użytku graczowi
        imageViewPlayerBonusAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextPlayerBonus, operationAdd, bonusIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Sprawdź czy bonus z przedmiotów gracza nie jest pusty
        editTextPlayerBonus.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(editTextList) }


        //Odejmij poziom potworowi
        imageViewMonsterLevelRemove.setOnClickListener {
            editValueInBracket(game.minLevel, maxViewValue, editTextMonsterLevel, operationRemove, levelIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj poziom potworowi
        imageViewMonsterLevelAdd.setOnClickListener {
            editValueInBracket(game.minLevel, maxViewValue, editTextMonsterLevel, operationAdd, levelIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Sprawdź czy poziom potwora jest większy od 0 i nie jest pusty
        editTextMonsterLevel.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(editTextList) }

        //Odejmij wzmacniacz potwora
        imageViewMonsterEnhancerRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterEnhancer, operationRemove, enhancerIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj wzmacniacz potwora
        imageViewMonsterEnhancerAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterEnhancer, operationAdd, enhancerIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Sprawdź czy wzmacniacz potwora nie jest pusty
        editTextMonsterEnhancer.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(editTextList) }

        //Odejmij bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusRemove.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterBonus, operationRemove, bonusIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj bonus z jednorazowego użytku potworowi
        imageViewMonsterBonusAdd.setOnClickListener {
            editValueInBracket(minBonus, maxViewValue, editTextMonsterBonus, operationAdd, bonusIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Sprawdź czy bonus z jednorazowego użytku potwora nie jest pusty
        editTextMonsterBonus.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> checkValuesInBrackets(editTextList) }
    }

    //Metoda "Zsumuj moc gracza i potwora"
    fun updateSummary() {
        editTextPlayerSummary.setText((tryParse(editTextPlayerLevel.text.toString(), game.maxLevel) + tryParse(editTextPlayerItems.text.toString(), maxViewValue) + tryParse(editTextPlayerBonus.text.toString(), maxViewValue)).toString())
        editTextMonsterSummary.setText((tryParse(editTextMonsterLevel.text.toString(), maxViewValue) + tryParse(editTextMonsterEnhancer.text.toString(), maxViewValue) + tryParse(editTextMonsterBonus.text.toString(), maxViewValue)).toString())
        checkWinner(editTextPlayerSummary, editTextMonsterSummary)
    }

    //Metoda "Zastosuj podaną operację na liczbach z pola"
    fun editValueInBracket(minValue: Int, maxValue: Int, bracket: EditText, operation: String, incrementationValue: Int) {
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
    fun checkValuesInBrackets(bracketList: ArrayList<EditText>) {
        for (element in bracketList) {
            removeLeadingZeros(element)

            if (element === editTextPlayerLevel) {
                if (element.text.toString() == "" || tryParse(element.text.toString(), game.maxLevel) < game.minLevel)
                    element.setText(game.minLevel.toString())
                else if (tryParse(element.text.toString(), game.maxLevel) >= game.maxLevel)
                    element.setText(game.maxLevel.toString())
            } else if (element === editTextMonsterLevel) {
                if (element.text.toString() == "" || tryParse(element.text.toString(), maxViewValue) < game.minLevel)
                    element.setText(game.minLevel.toString())
                else if (tryParse(element.text.toString(), maxViewValue) >= maxViewValue)
                    element.setText(maxViewValue.toString())
            } else {
                if (element.text.toString() == "" || tryParse(element.text.toString(), maxViewValue) < minBonus)
                    element.setText(minBonus.toString())
                else if (tryParse(element.text.toString(), maxViewValue) >= maxViewValue)
                    element.setText(maxViewValue.toString())
            }
            updateSummary()
        }
    }

    //Metoda "Sprawdź kto wygrywa"
    fun checkWinner(PlayerSummary: EditText, MonsterSummary: EditText) {

        val winnerDrawable = resources.getDrawable(R.drawable.ic_munchkin_winner_24dp)
        val loserDrawable = resources.getDrawable(R.drawable.ic_munchkin_loser_24dp)
        val tieDrawable = resources.getDrawable(R.drawable.ic_munchkin_sword_24dp)

        winnerDrawable.setTint(resources.getColor(R.color.text_color))
        loserDrawable.setTint(resources.getColor(R.color.text_color))
        tieDrawable.setTint(resources.getColor(R.color.text_color))

        if (tryParse(PlayerSummary.text.toString(), game.minLevel) > tryParse(MonsterSummary.text.toString(), game.minLevel)) {
            imageViewWinnerPlayer.setImageDrawable(winnerDrawable)
            imageViewWinnerMonster.setImageDrawable(loserDrawable)
        } else if (tryParse(PlayerSummary.text.toString(), game.minLevel) < tryParse(MonsterSummary.text.toString(), game.minLevel)) {
            imageViewWinnerPlayer.setImageDrawable(loserDrawable)
            imageViewWinnerMonster.setImageDrawable(winnerDrawable)
        } else {
            imageViewWinnerPlayer.setImageDrawable(tieDrawable)
            imageViewWinnerMonster.setImageDrawable(tieDrawable)
        }
    }

    //Obsługa strzałeczki w tył
    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else
                super.onOptionsItemSelected(item)

    //Wyjście z Activity
    override fun onBackPressed() {
        playerList[playerPosition].level= tryParse(editTextPlayerLevel.text.toString(), game.maxLevel)
        GameActivity().insertPlayerListIntoGame(playerList, game)
        val json = Gson().toJson(game)
        val returnIntent = Intent()
        returnIntent.putExtra("resultGame", json)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //tryParse int
    fun tryParse(value: String, defaultVal: Int) = try {
        Integer.parseInt(value)
    } catch (e: NumberFormatException) {
        defaultVal
    }

    //Usuń zera z przodu
    fun removeLeadingZeros(bracket: EditText) {
        var value = bracket.text.toString()
        while (value.indexOf("0") == 0 && value.length > 1)
            value = value.substring(1)

        bracket.setText(value)
    }*/

    //tryParse int
    fun tryParse(value: String, defaultVal: Int) = try {
        Integer.parseInt(value)
    } catch (e: NumberFormatException) {
        defaultVal
    }

    //Usuń zera z przodu
    fun removeLeadingZeros(bracket: EditText) {
        var value = bracket.text.toString()
        while (value.indexOf("0") == 0 && value.length > 1)
            value = value.substring(1)

        bracket.setText(value)
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
                    playerLevel = playerCategoriesList[0].value
                    checkWinner(playerCategoriesList[playerCategoriesList.size - 1].value, monsterCategoriesList[monsterCategoriesList.size - 1].value)
                }
            }

            //Zmniejszenie wartości
            override fun onRemoveClick(position: Int) {
                if (playerCategoriesList[position].value > playerCategoriesList[position].minValue) {
                    playerCategoriesList[position].value -= playerCategoriesList[position].incrementation
                    playerAdapter.notifyItemChanged(position)
                    updateSummary(playerCategoriesList, playerAdapter)
                    playerLevel = playerCategoriesList[0].value
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
        playerLevel = playerCategoriesList[0].value
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

    //Obsługa strzałeczki w tył
    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else
                super.onOptionsItemSelected(item)

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

}
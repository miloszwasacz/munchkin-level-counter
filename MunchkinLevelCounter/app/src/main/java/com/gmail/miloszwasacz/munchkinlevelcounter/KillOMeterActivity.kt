package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_kill_o_meter.*

class KillOMeterActivity : AppCompatActivity() {
    private lateinit var game: Game
    private var gameIndex = 0
    private lateinit var playerList: ArrayList<Player>
    private var playerPosition = 0
    private var bracketList = ArrayList<Bracket>()
    private lateinit var operationAdd: String
    private lateinit var operationRemove: String
    private var maxViewValue = 999
    private var minBonus = 0
    private var levelIncrementation = 1
    private var itemIncrementation = 1
    private var bonusIncrementation = 1
    private var enhancerIncrementation = 5
    private var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill_o_meter)

        supportActionBar!!.title = "Kill-O-Meter"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Ustawianie domyślnych wartości
        minBonus = resources.getInteger(R.integer.default_min_bonus)
        operationAdd = resources.getString(R.string.operation_add)
        operationRemove = resources.getString(R.string.operation_remove)
        levelIncrementation = resources.getInteger(R.integer.level_incremetation)
        itemIncrementation = resources.getInteger(R.integer.items_incrementation)
        bonusIncrementation = resources.getInteger(R.integer.bonus_incrementation)
        enhancerIncrementation = resources.getInteger(R.integer.enhancer_incrementation)
        gameIndex = intent.getIntExtra("EXTRA_GAME_INDEX", 0)
        playerPosition = intent.getIntExtra("EXTRA_POSITION", 0)
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
    }

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
            /*
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
            }*/
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

        winnerDrawable.setTint(resources.getColor(R.color.text_color))
        loserDrawable.setTint(resources.getColor(R.color.text_color))
        tieDrawable.setTint(resources.getColor(R.color.text_color))

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

    //Obsługa strzałeczki w tył
    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else super.onOptionsItemSelected(item)

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

    //Zapisywanie stanu Kill-O-Metera
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("Gra", Gson().toJson(game))
        savedInstanceState.putInt("Pozycja", playerPosition)
        savedInstanceState.putInt("IndexGry", gameIndex)
        checkValuesInBrackets(bracketList)
        val values = ArrayList<String>()
        for(bracket in bracketList)
            values.add(bracket.editText.text.toString())
        savedInstanceState.putString("ListaWartosci", Gson().toJson(values))
    }

    //Zapisywanie gry podczas wyjścia z aplikacji
    override fun onPause() {
        playerList[playerPosition].level= tryParse(editTextPlayerLevel.text.toString(), game.maxLevel)
        GameActivity().insertPlayerListIntoGame(playerList, game)
        val gameList = getGameListFromSharedPreferences()
        gameList!![gameIndex] = game
        saveGameListInSharedPreferences(gameList)
        super.onPause()
    }
}

package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_kill_o_meter.*
import java.util.*

class KillOMeterActivity : AppCompatActivity() {
    internal var playerLevel = 1
    internal var playerPosition = 0
    internal lateinit var playerList: String
    internal var maxPlayerLevel = 10
    internal var maxViewValue = 999
    internal var minLevel = 1
    internal var minBonus = 0
    internal lateinit var operationAdd: String
    internal lateinit var operationRemove: String
    internal var levelIncrementation = 1
    internal var itemIncrementation = 1
    internal var bonusIncrementation = 1
    internal var enhancerIncrementation = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill_o_meter)

        supportActionBar!!.title = "Kill-O-Meter"

        //Ustawianie domyślnych wartości
        playerLevel = resources.getInteger(R.integer.deafult_min_level)
        maxPlayerLevel = resources.getInteger(R.integer.deafult_rules)
        minLevel = resources.getInteger(R.integer.deafult_min_level)
        minBonus = resources.getInteger(R.integer.deafult_min_bonus)
        operationAdd = resources.getString(R.string.operation_add)
        operationRemove = resources.getString(R.string.operation_remove)
        levelIncrementation = resources.getInteger(R.integer.level_incremetation)
        itemIncrementation = resources.getInteger(R.integer.items_incrementation)
        bonusIncrementation = resources.getInteger(R.integer.bonus_incrementation)
        enhancerIncrementation = resources.getInteger(R.integer.enhancer_incrementation)

        val playerName = intent.getStringExtra("EXTRA_NAME")
        playerLevel = intent.getIntExtra("EXTRA_LEVEL", resources.getInteger(R.integer.deafult_min_level))
        playerPosition = intent.getIntExtra("EXTRA_POSITION", 0)
        playerList = intent.getStringExtra("EXTRA_LIST")
        maxPlayerLevel = intent.getIntExtra("EXTRA_MAX_LEVEL", resources.getInteger(R.integer.deafult_rules))
        minLevel = intent.getIntExtra("EXTRA_MIN_LEVEL", resources.getInteger(R.integer.deafult_min_level))



        //Ustawianie poziomu i nazwy gracza
        textViewPlayerName.text = playerName
        editTextPlayerLevel.setText(playerLevel.toString())

        //Stwórz listę pól
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
            editValueInBracket(minLevel, maxPlayerLevel, editTextPlayerLevel, operationRemove, levelIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj poziom graczowi
        imageViewPlayerLevelAdd.setOnClickListener {
            editValueInBracket(minLevel, maxPlayerLevel, editTextPlayerLevel, operationAdd, levelIncrementation)
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
            editValueInBracket(minLevel, maxViewValue, editTextMonsterLevel, operationRemove, levelIncrementation)
            checkValuesInBrackets(editTextList)
        }

        //Dodaj poziom potworowi
        imageViewMonsterLevelAdd.setOnClickListener {
            editValueInBracket(minLevel, maxViewValue, editTextMonsterLevel, operationAdd, levelIncrementation)
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
        editTextPlayerSummary.setText((tryParse(editTextPlayerLevel.text.toString(), maxPlayerLevel) + tryParse(editTextPlayerItems.text.toString(), maxViewValue) + tryParse(editTextPlayerBonus.text.toString(), maxViewValue)).toString())
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
                if (element.text.toString() == "" || tryParse(element.text.toString(), maxPlayerLevel) < minLevel)
                    element.setText(minLevel.toString())
                else if (tryParse(element.text.toString(), maxPlayerLevel) >= maxPlayerLevel)
                    element.setText(maxPlayerLevel.toString())
            } else if (element === editTextMonsterLevel) {
                if (element.text.toString() == "" || tryParse(element.text.toString(), maxViewValue) < minLevel)
                    element.setText(minLevel.toString())
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

        if (tryParse(PlayerSummary.text.toString(), minLevel) > tryParse(MonsterSummary.text.toString(), minLevel)) {
            imageViewWinnerPlayer.setImageDrawable(winnerDrawable)
            imageViewWinnerMonster.setImageDrawable(loserDrawable)
        } else if (tryParse(PlayerSummary.text.toString(), minLevel) < tryParse(MonsterSummary.text.toString(), minLevel)) {
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
        val tieDrawable = resources.getDrawable(R.drawable.ic_munchkin_sword_24dp)
        tieDrawable.setTint(Color.BLACK)

        playerLevel = tryParse(editTextPlayerLevel.text.toString(), maxPlayerLevel)
        val returnIntent = Intent()
        returnIntent.putExtra("resultLevel", playerLevel)
        returnIntent.putExtra("resultPosition", playerPosition)
        returnIntent.putExtra("resultList", playerList)
        returnIntent.putExtra("resultMaxLevel", maxPlayerLevel)
        returnIntent.putExtra("resultMinLevel", minLevel)
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
}
